package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.Searcher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.getBestArea;

@Component
public class ConstantAreaAwarePathFinder extends PathFinder {

    public ConstantAreaAwarePathFinder(@Qualifier("AStar") Searcher searcher) {
        super(searcher);
    }

    @Override
    public Optional<PathFinderResult> findNextResult() {
        Area bestArea = getBestArea();

        PathFinderResult result = world.getAllValuableResults().stream()
                .filter(r -> bestArea.getValuables().contains(r.getTarget()))
                .max(Comparator.comparing(PathFinderResult::getWeight))
                .get();

        return Optional.of(result);
    }



}
