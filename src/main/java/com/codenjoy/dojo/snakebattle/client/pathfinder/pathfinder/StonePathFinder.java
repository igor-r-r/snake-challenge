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

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StonePathFinder extends PathFinder {

    public StonePathFinder(Searcher searcher, DirectionProvider directionProvider) {
        super(searcher, directionProvider);
    }

    public Optional<PathFinderResult> findNextResult() {
        /*

        Actions to be performed:
        1. Update world state:
          1.1. My snake (done)
          1.2. Enemy snakes (done)
          1.3. All valuable resources (done)
          1.4. Group resources for Stone finder (done)
          1.5. Group resources by area
            1.5.1. Calculate weight for each group  (TODO - real weights)
        2. Choose current PathFinder
          2.1. If there are enemies nearby and can attack - EnemyPathFinder (done)
          2.2. Else StonePathFinder (done)
        3. EnemyPathFinder:
          3.1. Get all enemies PathPoints and Enemy objects (done)
          3.2. Convert target path points to projected path points (done)
          3.3. Get all possible results for nearby enemies: (done)
            3.3.1. Use AStar algorithm to find path (done)
          3.4. Prioritize results - assign some weight according to amount of points that can be gathered on the way (TODO)
          3.5. Filter prioritized results to get most effective path (TODO)
        4. StonePathFinder
          4.1. Get groups by area (TODO) 30x30 -> 5x5
          4.2. Get all possible results for most valuable group nearby. If group weight is equal - choose closer to center (TODO)
          4.3. Choose best target and path: (TODO)
            4.3.1. Priority from lowest to highest: Flight -> Apple -> Gold -> Stone -> Fury (TODO)
            4.3.2. Calculate weight for each target path (TODO)
            4.3.3. Area should not contain enemy snake longer than my snake (TODO)
            4.3.4. Choose target with highest priority and then most valuable path to it (TODO)
          4.4. If no result for current group go to next. (TODO)
          4.5. If ate Fury: (TODO)
            4.5.1. Turn 180%, drop stone (TODO)
            4.5.2. Repeat until isFury (TODO)

          ? Should switch to enemy if under fury?
          ? maybe try to eat any part of enemy snake under fury?
          ? TODO refactor codebase:
                - make all operations simple and short
                - no code repeat
                - easy behavior switch
                - check all operations for null and stuff

          TODO choose most valuable path
         */

        System.out.println("STONE PATH FINDER");

        for (Map.Entry<Integer, List<PathPoint>> group : world.getRegularPathPointGroups().entrySet()) {

            if (group.getValue() != null) {
                PathFinderResult result = getNextDirectionResult(getGroupResults(group.getValue()));
                if (result != null && result.getNextPoint() != null) {
                    return Optional.of(result);

                }
            }
        }

        return Optional.empty();
    }

    public List<PathFinderResult> getGroupResults(List<PathPoint> pathPoints) {
        Stream<PathFinderResult> stream = pathPoints.stream()
                .map(p -> searcher.findSinglePath(p));

        System.out.println(pathPoints);

        return stream.collect(Collectors.toList());
    }

    private PathFinderResult getNextDirectionResult(List<PathFinderResult> results) {
        int minDistance = Integer.MAX_VALUE;
        PathFinderResult result = null;

        for (PathFinderResult currentResult : results) {
            if (currentResult.getDistance() < minDistance) {

                minDistance = currentResult.getDistance();

                if (currentResult.getNextPoint() == null) {
                    continue;
                }

                result = currentResult;
            }
        }

        return result;
    }


}
