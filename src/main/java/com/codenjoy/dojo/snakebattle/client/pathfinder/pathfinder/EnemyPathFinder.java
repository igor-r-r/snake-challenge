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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateEnemyLength;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.isPriorityHigher;

public class EnemyPathFinder extends PathFinder {

    public EnemyPathFinder(Searcher searcher, DirectionProvider directionProvider) {
        super(searcher, directionProvider);
    }

    public Optional<PathFinderResult> findNextDirection() {

            List<Enemy> enemies = world.getEnemies();

            List<PathFinderResult> enemyResults = getAllResults(enemies.stream()
                    .map(e -> buildPathPoint(
                            e.getHead().getX(),
                            e.getHead().getY(),
                            e.getHead().getElementType()))
                    .collect(Collectors.toList()), canAttackEnemy());

            System.out.println("Enemies results : " + enemyResults);

            PathFinderResult result = getNextDirectionResult(enemyResults);

            if (result == null || result.getNextPoint() == null) {
                List<PathPoint> targets = Stream.of(world.getApples(), world.getGold(), world.getFury())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

                List<PathFinderResult> appleAndGoldResults = getAllResults(targets, p -> true);

                result = getNextDirectionResult(appleAndGoldResults);
            }

        return Optional.of(result);
    }

    public static Predicate<PathPoint> canAttackEnemy() {
        return p -> (SnakeLengthUtils.isMySnakeLonger(p)
                || world.getMySnake().isFury());
    }

    public List<PathFinderResult> getAllResults(List<PathPoint> pathPoints, Predicate<PathPoint> predicate) {
        return pathPoints.stream()
                .filter(predicate)
                .map(p -> searcher.findSinglePath(p))
                .collect(Collectors.toList());
    }

    private PathFinderResult getNextDirectionResult(List<PathFinderResult> results) {
        System.out.println("Path finder result: " + results.stream()
                .map(PathFinderResult::getTargetElementType)
                .collect(Collectors.toList()));
        int minDistance = Integer.MAX_VALUE;
        PathFinderResult result = null;

        for (PathFinderResult currentResult : results) {

            if (currentResult.getDistance() < minDistance ||
                    ((currentResult.getDistance() < minDistance + 2)
                            && checkPriorityHigher(currentResult, result))) {

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

    private boolean checkPriorityHigher(PathFinderResult current, PathFinderResult previous) {
        return previous == null
                || isPriorityHigher(current.getTargetElementType(), previous.getTargetElementType());
    }

}
