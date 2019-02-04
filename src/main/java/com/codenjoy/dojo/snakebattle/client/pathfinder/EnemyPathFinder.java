package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.SnakeLengthHelper.calculateEnemyLength;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.isPriorityHigher;

public class EnemyPathFinder extends PathFinder {

    public EnemyPathFinder(Finder finder) {
        super(finder);
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
        return p -> world.getMySnake().getLength() > calculateEnemyLength(p) + 1
                || world.getMySnake().getState().equals(SnakeState.FURY);
    }

    public List<PathFinderResult> getAllResults(List<PathPoint> pathPoints, Predicate<PathPoint> filter) {
        return pathPoints.stream()
                .filter(filter)
                .map(p -> finder.findSinglePath(p))
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
                    currentResult.setDirection(anyDirection());
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
