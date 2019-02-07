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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinderPredicate.canAttackEnemy;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getEnemyDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getPathPointByDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static java.util.Optional.ofNullable;

public class EnemyPathFinder extends PathFinder {

    public EnemyPathFinder(Searcher searcher, DirectionProvider directionProvider) {
        super(searcher, directionProvider);
    }

    public Optional<PathFinderResult> findNextResult() {

        System.out.println("ENEMY PATH FINDER");

        List<Enemy> enemies = world.getEnemies();

        List<PathFinderResult> enemyResults = getAllEnemyResults(enemies.stream()
                        .map(e -> buildPathPoint(
                                e.getHead().getX(),
                                e.getHead().getY(),
                                e.getHead().getElementType()))
                        .collect(Collectors.toList()),
                canAttackEnemy());

        System.out.println("Enemy results: " + enemyResults.stream().map(PathFinderResult::getRealTarget).collect(Collectors.toList()));
        PathFinderResult result = directionProvider.getNextResult(enemyResults);

        if (result == null || result.getNextPoint() == null) {
            List<PathPoint> targets = Stream.of(world.getApples(), world.getGold(), world.getFury())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<PathFinderResult> appleAndGoldResults = getAllPointsResults(targets);

            result = directionProvider.getNextResult(appleAndGoldResults);
        }

        return ofNullable(result);
    }

    private PathPoint convertToProjectedHeadPathPoint(PathPoint currentPathPoint) {
        Direction currentEnemyDirection = getEnemyDirection(currentPathPoint.getElementType());

        if (currentEnemyDirection == null) {
            return currentPathPoint;
        }

        PathPoint projectedPathPoint = getPathPointByDirection(currentPathPoint, currentEnemyDirection);

        if (!canPassThrough(projectedPathPoint)) {
            return currentPathPoint;
        }

        return projectedPathPoint;

    }

    public List<PathFinderResult> getAllEnemyResults(List<PathPoint> pathPoints, Predicate<PathPoint> canAttackEnemy) {
        List<PathFinderResult> list = new ArrayList<>();
        for (PathPoint pathPoint : pathPoints) {
            if (canAttackEnemy.test(pathPoint)) {
                PathPoint p = convertToProjectedHeadPathPoint(pathPoint);
                PathFinderResult singlePathResult = searcher.findSinglePath(p);
                singlePathResult.setRealTarget(pathPoint);
                list.add(singlePathResult);
            }
        }
        return list;
    }

    public List<PathFinderResult> getAllPointsResults(List<PathPoint> pathPoints) {
        return pathPoints.stream()
                .map(p -> searcher.findSinglePath(p))
                .collect(Collectors.toList());
    }
}
