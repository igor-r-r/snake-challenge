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
import com.codenjoy.dojo.snakebattle.client.pathfinder.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public abstract class PathFinder {

    public static World world = new World();

    protected Searcher searcher;
    protected DirectionProvider directionProvider;

    public abstract Optional<PathFinderResult> findNextResult();

    // calculate all possible paths and return direction
    public String findPath() {
        if (world.getMySnake().isFury() && world.getMySnake().getStoneCount() > 0) {
            System.out.println("FURY MODE! State: " + world.getMySnake().getState() );
            System.out.println("FURY MODE! Stone count: " + world.getMySnake().getStoneCount() );
            return directionProvider.furyDirection();
        }

        System.out.println("State: " + world.getMySnake().getState() );
        System.out.println("Stone count: " + world.getMySnake().getStoneCount() );
        PathFinderResult result = findNextResult().orElse(null);
        return directionProvider.getFinalDirectionString(result);
    }

    public List<PathFinderResult> getGroupResults(List<PathPoint> pathPoints) {
        return pathPoints.stream()
                .map(this::search)
                .collect(Collectors.toList());
    }

    protected PathFinderResult search(PathPoint target) {
        return searcher.findSinglePath(target);
    }

}
