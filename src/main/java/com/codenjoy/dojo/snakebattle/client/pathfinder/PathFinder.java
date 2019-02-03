package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.World;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.generateChildren;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getCloseDirection;

public class PathFinder {

    public static Snake snake = new Snake();
    public static World world = new World();

    // calculate all possible paths and return direction
    public String findPath() {

        // update World data
        setup();

        //// group valuable path points by estimated distance to it from starting point
        //TreeMap<Integer, List<PathPoint>> pathPointGroups = mapHelper.groupValuablePointsByDistance(board);
        ////TreeMap<Integer, List<PathPoint>> enemies = mapHelper.findAllEnemies(board);

        PathFinderResult result = findNextDirection().orElse(null);

        return getFinalDirection(world.getBoard(), result);
    }

    private void setup() {
        world.updateWorldState(world.getBoard());
    }

    private String getFinalDirection(Board board, PathFinderResult result) {
        if (result != null) {
            if (result.getDirection().equals(ACT)) {
                return ACT(0);
            }
            if (board.isStoneAt(result.getNextPoint().getX(), result.getNextPoint().getY())) {
                snake.setState(SnakeState.HAS_STONE);
            }
            if (board.getAt(result.getNextPoint().getX(), result.getNextPoint().getY()).equals(Elements.FURY_PILL)) {
                snake.setState(SnakeState.FURY);
            }
            else if (shouldDropStone(result.getNextPoint().getX(), result.getNextPoint().getY())) {
                return ACT.toString() + ", " + result.getDirection().toString();
            }
            return result.getDirection().toString();
        } else {
            return anyDirection(board).toString();
        }
    }

    private boolean shouldDropStone(int nextX, int nextY) {
        return world.getBoard().getAt(nextX, nextY).equals(Elements.FURY_PILL)
                || calculateSnakeLengthStupid(world.getBoard()) > 4
                || snake.getState().equals(SnakeState.FURY);
    }

    public Optional<PathFinderResult> findNextDirection() {
        for (Map.Entry<Integer, List<PathPoint>> group : world.getPathPointGroups().entrySet()) {
            if (group.getValue() != null) {
                return Optional.of(getNextDirectionResult(world.getBoard(), group.getValue()));
            }
        }

        return Optional.empty();
    }

    private PathFinderResult getNextDirectionResult(Board board, List<PathPoint> pathPoints) {
        int minDistance = Integer.MAX_VALUE;
        PathFinderResult result = null;

        for (PathPoint p : pathPoints) {
            PathFinderResult currentResult = findSinglePath(board, p);
            if (currentResult.getDistance() < minDistance) {
                minDistance = currentResult.getDistance();
                if (currentResult.getNextPoint() == null) {
                    currentResult.setDirection(ACT);
                    return currentResult;
                }
                Direction direction = getCloseDirection(board.getMe().getX(), board.getMe().getY(),
                        currentResult.getNextPoint().getX(), currentResult.getNextPoint().getY());
                currentResult.setDirection(direction);
                result = currentResult;
            }
        }

        return result;
    }

    // get any possible direction
    private Direction anyDirection(Board board) {
        Point me = board.getMe();

        for (int[] direction : childrenDirections) {
            if (canPassThrough(board, me.getX() + direction[0], me.getY() + direction[1])) {
                return getCloseDirection(me.getX(), me.getY(), me.getX() + direction[0], me.getY() + direction[1]);
            }
        }

        return Direction.STOP;
    }

    // return best path to get to specific Point
    private PathFinderResult findSinglePath(Board board, PathPoint target) {
        PathFinderResult result = new PathFinderResult();

        Map<PathPoint, PathPoint> openList = new HashMap<>();
        Map<PathPoint, PathPoint> closedList = new HashMap<>();

        // add head coordinates as starting point
        PathPoint startingPoint = buildPathPoint(board.getMe(), Elements.HEAD_UP);
        openList.put(startingPoint, startingPoint);

        // loop through open list
        while (!openList.isEmpty()) {
            // get current path point according to min F value
            PathPoint current = openList.keySet().stream().min(Comparator.comparing(PathPoint::getF)).orElse(null);

            // removing current point from open list since it's already visited
            openList.remove(current);

            // adding current point to closed list
            closedList.put(current, current);

            // check if open list is empty
            if (current == null) {
                break;
            }

            // check if current path point is a target point and return successful result if it's true
            if (current.equals(target)) {
                return calculatePath(current, startingPoint);
            }

            // generate left, right, up and down path points with all parameters
            List<PathPoint> children = generateChildren(board, current, target);

            // add all reachable children to open list
            for (PathPoint childPoint : children) {

                if (closedList.containsKey(childPoint)) {
                    continue;
                }

                if (openList.containsKey(childPoint)) {
                    PathPoint existingPoint = openList.get(childPoint);
                    if (existingPoint.getG() < childPoint.getG()) {
                        continue;
                    }

                }

                openList.put(childPoint, childPoint);
            }
        }

        return result;
    }

    private PathFinderResult calculatePath(PathPoint target, PathPoint startingPoint) {
        PathPoint current = target;

        while (current.getParent() != null) {
            if (current.getParent().equals(startingPoint)) {
                return PathFinderResult.builder()
                        .reachable(true)
                        .distance(target.getG())
                        .nextPoint(current)
                        .build();
            }

            current = current.getParent();
        }

        return null;
    }

}
