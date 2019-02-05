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

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getCloseDirection;

public class StonePathFinder extends PathFinder {

    public StonePathFinder(Searcher searcher, DirectionProvider directionProvider) {
        super(searcher, directionProvider);
    }

    public Optional<PathFinderResult> findNextDirection() {
        for (Map.Entry<Integer, List<PathPoint>> group : world.getRegularPathPointGroups().entrySet()) {

            if (group.getValue() != null) {
                return Optional.of(getNextDirectionResult(getGroupResults(group.getValue())));
            }
        }

        return Optional.empty();
    }

    public List<PathFinderResult> getGroupResults(List<PathPoint> pathPoints) {
        Stream<PathFinderResult> stream = pathPoints.stream()
                .map(p -> searcher.findSinglePath(p));

        System.out.println(stream);

        return stream.collect(Collectors.toList());
    }

    private PathFinderResult getNextDirectionResult(List<PathFinderResult> results) {
        int minDistance = Integer.MAX_VALUE;
        PathFinderResult result = null;

        for (PathFinderResult currentResult : results) {

            if (currentResult.getDistance() < minDistance) {
                minDistance = currentResult.getDistance();
                if (currentResult.getNextPoint() == null) {
                    currentResult.setDirection(directionProvider.anyDirection());
                    return currentResult;
                }
                Point me = world.getBoard().getMe();

                Direction direction = getCloseDirection(me.getX(), me.getY(),
                        currentResult.getNextPoint().getX(), currentResult.getNextPoint().getY());
                currentResult.setDirection(direction);
                result = currentResult;
            }
        }

        return result;
    }

}
