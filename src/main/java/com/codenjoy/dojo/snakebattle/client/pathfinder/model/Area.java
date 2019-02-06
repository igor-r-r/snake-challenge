package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.AREA_SIZE;

@Data
@Builder
@EqualsAndHashCode
public class Area {

    private int[] borders = {AREA_SIZE, AREA_SIZE};

    @EqualsAndHashCode.Include
    private AreaCoordinates coordinates;

    private int weight;
    private int distance;
    private boolean hasEnemy;
    private boolean hasNonattackableEnemy;
    private List<Enemy> enemies;
    private List<PathPoint> valuables;

}
