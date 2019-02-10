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

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.Searcher;
import com.codenjoy.dojo.snakebattle.client.pathfinder.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.checkPriorityHigher;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public abstract class PathFinder {

    public static World world = new World();

    protected Searcher searcher;

    public abstract Optional<PathFinderResult> findNextResult();

    // calculate all possible paths and return direction

    public List<PathFinderResult> getResults(List<PathPoint> pathPoints) {
        return pathPoints.stream()
                .map(p -> searcher.findSinglePath(p))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public PathFinderResult getNextResult(List<PathFinderResult> results) {
        List<PathFinderResult> withNextPoint = results.stream()
                .filter(r -> r.getNextPoint() != null).collect(toList());

        int min = withNextPoint.stream()
                .mapToInt(PathFinderResult::getDistance)
                .min().orElse(Integer.MAX_VALUE);

        PathFinderResult result = null;

        results = results.stream()
                .filter(p -> p.getDistance() >= min && p.getDistance() < min + 2)
                .collect(toList());

        for (PathFinderResult currentResult : results) {
            if (checkPriorityHigher(currentResult, result)) {
                result = currentResult;
            }
        }

        // TODO weight
        return result;
    }

}
