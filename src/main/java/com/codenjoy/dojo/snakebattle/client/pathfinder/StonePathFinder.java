package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getCloseDirection;

public class StonePathFinder extends PathFinder {

    public StonePathFinder(Finder finder) {
        super(finder);
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
                .map(p -> finder.findSinglePath(p));

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

}
