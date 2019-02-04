package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.List;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder.world;

public class WorldBuildHelper {

    public static List<PathPoint> toPathPointList(Elements... elements) {
        return  world.getBoard().get(elements).stream()
                .map(p -> buildPathPoint(p.getX(), p.getY(), world.getBoard().getAt(p)))
                .collect(Collectors.toList());
    }

    public static PathPoint buildPathPoint(int x, int y, Elements elementType) {
        return PathPoint.builder()
                .x(x)
                .y(y)
                .elementType(elementType)
                .pathPointPriority(PathPointPriority.getByElementType(elementType))
                .build();
    }

}
