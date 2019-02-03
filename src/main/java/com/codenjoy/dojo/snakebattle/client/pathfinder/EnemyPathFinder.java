package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.isPriorityHigher;

public class EnemyPathFinder extends PathFinder {

    public EnemyPathFinder(Finder finder) {
        super(finder);
    }

    public Optional<PathFinderResult> findNextDirection() {
        if (calculateSnakeLengthStupid() - world.getTotalEnemyLength() > 2) {
            List<Enemy> enemies = world.getEnemies();

            List<PathFinderResult> enemyResults = getAllResults(enemies.stream()
                    .map(e -> buildPathPoint(
                            e.getHead().getX(),
                            e.getHead().getY(),
                            e.getHead().getElementType()))
                    .collect(Collectors.toList()));

            PathFinderResult enemyResult = getNextDirectionResult(enemyResults);

            return Optional.of(enemyResult);
        } else {
            List<PathFinderResult> appleAndGoldResults = getAllResults(world.getApplesAndGold());
            PathFinderResult appleAndGoldResult = getNextDirectionResult(appleAndGoldResults);
            return Optional.of(appleAndGoldResult);
        }
    }

    public List<PathFinderResult> getAllResults(List<PathPoint> pathPoints) {
        return pathPoints.stream()
                .map(p -> finder.findSinglePath(p))
                .collect(Collectors.toList());
    }

    private PathFinderResult getNextDirectionResult(List<PathFinderResult> results) {
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
