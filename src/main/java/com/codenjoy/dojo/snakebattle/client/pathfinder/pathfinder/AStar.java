package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.generateChildren;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;

public class AStar implements Searcher {

    @Override
    public PathFinderResult findSinglePath(PathPoint target) {
        PathFinderResult result = new PathFinderResult();

        Map<PathPoint, PathPoint> openList = new HashMap<>();
        Map<PathPoint, PathPoint> closedList = new HashMap<>();

        Point me = world.getBoard().getMe();

        // add head coordinates as starting point
        PathPoint startingPoint = buildPathPoint(me.getX(), me.getY(), Elements.HEAD_UP);
        openList.put(startingPoint, startingPoint);

        //System.out.println("Open list: " + openList);
        // loop through open list
        System.out.print("Priorities: ");
        while (!openList.isEmpty()) {
            // get current path point according to min F value
            PathPoint current = openList.keySet().stream().min(Comparator.comparing(PathPoint::getF)).orElse(null);

            // removing current point from open list since it's already visited
            openList.remove(current);

            // adding current point to closed list
            closedList.put(current, current);
            System.out.print(PathPointPriority.getPriority(current.getElementType()) + ", ");

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
            List<PathPoint> children = generateChildren(current, target);

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

    //public List<PathPoint> sortAccordingToEnemyDirection() {
    //
    //}
    //
    //public static final int[][] childrenDirections = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    //
    //
    //public int[] getDirection(PathPoint me, PathPoint enemy) {
    //    if (Math.abs(enemy.getX() - me.getX()) > Math.abs(enemy.getY() - me.getY())) {
    //
    //    }
    //
    //}

    // get PathFinderResult with direction for the next move
    public PathFinderResult calculatePathResult(PathPoint target, PathPoint startingPoint) {
        PathPoint current = target;

        int weight = 0;
        List<PathPoint> allValuables = new ArrayList<>();

        while (current.getParent() != null) {
            if (current.getParent().equals(startingPoint)) {
                Snake me = world.getMySnake();

                return PathFinderResult.builder()
                        .distance(target.getG())
                        .nextPoint(current)
                        .realTarget(target)
                        .target(target)
                        .weight(weight)
                        .allValuables(allValuables)
                        .direction(getCloseDirection(me.getHead().getX(), me.getHead().getY(),
                                current.getX(), current.getY()))
                        .build();
            }

            weight += PathPointPriority.getPriority(current.getElementType());
            if (PathPointPriority.getPriority(current.getElementType()) != 0) {
                allValuables.add(current);
            }

            current = current.getParent();
        }

        return null;
    }


}
