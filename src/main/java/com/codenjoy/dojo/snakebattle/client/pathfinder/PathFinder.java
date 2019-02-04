package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.buildAct;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getCloseDirection;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public abstract class PathFinder {

    public static World world = new World();

    protected Finder finder;

    // calculate all possible paths and return direction
    public String findPath() {
        // update World data
        world.updateWorldState();

        PathFinderResult result = findNextDirection().orElse(null);

        return getFinalDirection(result);
    }

    public abstract Optional<PathFinderResult> findNextDirection();

    public List<PathFinderResult> getGroupResults(List<PathPoint> pathPoints) {
        return pathPoints.stream()
                .map(p -> finder.findSinglePath(p))
                .collect(Collectors.toList());
    }

    // get any possible direction
    protected Direction anyDirection() {
        Point me = world.getBoard().getMe();

        for (int[] direction : childrenDirections) {
            if (canPassThrough(world.getBoard(), me.getX() + direction[0], me.getY() + direction[1])) {
                return getCloseDirection(me.getX(), me.getY(), me.getX() + direction[0], me.getY() + direction[1]);
            }
        }

        return Direction.RIGHT;
    }

    protected String getFinalDirection(PathFinderResult result) {
        if (result != null) {
            if (result.getDirection().equals(ACT) || result.getNextPoint() == null) {
                return anyDirection().toString();
            }

            if (world.getBoard().getAt(result.getNextPoint().getX(), result.getNextPoint().getY()).equals(Elements.STONE)) {
                world.getMySnake().changeStoneCount(1);
            }

            if (world.getBoard().getAt(result.getNextPoint().getX(), result.getNextPoint().getY()).equals(Elements.FURY_PILL)) {
                world.getMySnake().setState(SnakeState.FURY);
            } else if (shouldDropStone(result.getNextPoint().getX(), result.getNextPoint().getY())) {
                world.getMySnake().changeStoneCount(-1);
                return buildAct(result.getDirection(), false);
            }
            return result.getDirection().toString();
        } else {
            return anyDirection().toString();
        }
    }

    protected boolean shouldDropStone(int nextX, int nextY) {
        return (world.getBoard().getAt(nextX, nextY).equals(Elements.FURY_PILL)
                || world.getMySnake().getLength() > 4
                || world.getMySnake().getState().equals(SnakeState.FURY))
                && world.getMySnake().getStoneCount() > 0;
    }

}
