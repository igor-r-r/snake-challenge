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

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Component
public class EnemyPathFinder extends PathFinder {

    public EnemyPathFinder(Searcher searcher) {
        super(searcher);
    }

    public Optional<PathFinderResult> findNextResult() {

        System.out.println("ENEMY PATH FINDER");

        List<PathFinderResult> enemyResults = world.getAllEnemyResults();

        //System.out.println("Enemy results: " + enemyResults.stream().map(PathFinderResult::getRealTarget).collect(Collectors.toList()));
        PathFinderResult result = getNextResult(enemyResults);

        if (result == null || result.getNextPoint() == null) {
            List<PathPoint> targets = Stream.of(world.getStones(), world.getApples(), world.getGold(), world.getFury())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<PathFinderResult> results = world.getAllValuableResults().stream()
                    .filter(r -> targets.contains(r.getTarget()))
                    .collect(Collectors.toList());

            result = getNextResult(results);
        }

        return ofNullable(result);
    }
}
