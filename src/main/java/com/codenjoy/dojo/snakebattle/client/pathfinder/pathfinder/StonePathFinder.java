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
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.Searcher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class StonePathFinder extends PathFinder {

    public StonePathFinder(@Qualifier("AStar") Searcher searcher) {
        super(searcher);
    }

    public Optional<PathFinderResult> findNextResult() {
        System.out.println("STONE PATH FINDER");

        for (Map.Entry<Integer, List<PathPoint>> group : world.getGroups().entrySet()) {
            if (group.getValue() != null) {
                PathFinderResult result = getNextResult(getResults(group.getValue()));
                if (result != null && result.getNextPoint() != null) {
                    return Optional.of(result);

                }
            }
        }

        return Optional.empty();
    }

}
