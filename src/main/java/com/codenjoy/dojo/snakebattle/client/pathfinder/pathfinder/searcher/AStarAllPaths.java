package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_UP;

@Component
public class AStarAllPaths extends Searcher {

    @Override
    public Optional<PathFinderResult> findSinglePath(PathPoint target) {
        Map<PathPoint, PathPoint> openList = new HashMap<>();
        Map<PathPoint, PathPoint> closedList = new HashMap<>();

        Point me = world.getBoard().getMe();

        // add head coordinates as starting point
        PathPoint startingPoint = buildPathPoint(me.getX(), me.getY(), HEAD_UP);
        openList.put(startingPoint, startingPoint);

        recursive(startingPoint, target, openList, closedList);

        return Optional.empty();
    }

    private void recursive(PathPoint current, PathPoint target, Map<PathPoint, PathPoint> openList, Map<PathPoint, PathPoint> closedList) {

        // removing current point from open list since it's already visited
        openList.remove(current);

        // adding current point to closed list
        closedList.put(current, current);

        // generate left, right, up and down path points with all parameters
        List<PathPoint> children = generateChildren(current, target);

        // add all reachable children to open list
        for (PathPoint childPoint : children) {

            if (closedList.containsKey(childPoint)) {
                continue;
            }

            if (openList.containsKey(childPoint)) {
                PathPoint existingPoint = openList.get(childPoint);
                existingPoint.getParent().add(current);

                if (existingPoint.getG() < childPoint.getG()) {
                    continue;
                }

            }

            openList.put(childPoint, childPoint);

            recursive(childPoint, target, openList, closedList);
        }
    }

}
