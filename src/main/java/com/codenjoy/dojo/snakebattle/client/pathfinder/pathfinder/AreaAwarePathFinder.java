package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.Searcher;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AreaAwarePathFinder extends PathFinder {

    public AreaAwarePathFinder(Searcher searcher) {
        super(searcher);
    }

    @Deprecated
    @Override
    public Optional<PathFinderResult> findNextResult() {
        List<Area> allAreasSorted = getAllAreasSorted();

        for (Area area : allAreasSorted) {
            List<PathPoint> valuablesSorted = area.getValuables().stream()
                    .sorted(Comparator.comparing(p -> PathPointPriority.getPriority(p.getElementType())))
                    .collect(Collectors.toList());

            List<PathFinderResult> results = getResults(valuablesSorted);

            Optional<PathFinderResult> result = results.stream().max(Comparator.comparingInt(PathFinderResult::getWeight));
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

}
