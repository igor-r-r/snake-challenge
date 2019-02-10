package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;

import java.util.Map;
import java.util.Optional;

public class ConstantAreaAwarePathFinder extends PathFinder {

    @Override
    public Optional<PathFinderResult> findNextResult() {
        Map<AreaCoordinates, Area> areas = world.getConstantAreas();

        //Area bestArea = areas.values().stream().filter(a -> a.setPossibleReward());



        return Optional.empty();
    }

}
