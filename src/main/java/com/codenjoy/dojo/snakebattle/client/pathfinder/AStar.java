package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.generateChildren;

public class AStar implements Finder {

    @Override
    public PathFinderResult findSinglePath(PathPoint target) {
        PathFinderResult result = new PathFinderResult();

        Map<PathPoint, PathPoint> openList = new HashMap<>();
        Map<PathPoint, PathPoint> closedList = new HashMap<>();

        Point me = PathFinder.world.getBoard().getMe();

        // add head coordinates as starting point
        PathPoint startingPoint = buildPathPoint(me.getX(), me.getY(), Elements.HEAD_UP);
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
                current.setElementType(target.getElementType());
                return calculatePathResult(current, startingPoint);
            }

            // generate left, right, up and down path points with all parameters
            List<PathPoint> children = generateChildren(PathFinder.world.getBoard(), current, target);

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

    // get PathFinderResult with direction for the next move
    public PathFinderResult calculatePathResult(PathPoint target, PathPoint startingPoint) {
        PathPoint current = target;

        while (current.getParent() != null) {
            if (current.getParent().equals(startingPoint)) {
                return PathFinderResult.builder()
                        .reachable(true)
                        .distance(target.getG())
                        .nextPoint(current)
                        .targetElementType(target.getElementType())
                        .build();
            }

            current = current.getParent();
        }

        return null;
    }
}
