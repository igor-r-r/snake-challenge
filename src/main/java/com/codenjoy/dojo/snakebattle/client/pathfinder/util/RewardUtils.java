package com.codenjoy.dojo.snakebattle.client.pathfinder.util;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.ENEMY_HEAD;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.getByElementType;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.getReward;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.AREA_SIZE;

public class RewardUtils {

    public static final int SNAKE_PART_REWARD = 10;

    public static int calculatePossibleReward(Area area) {
        return area.getValuables().stream()
                .filter(p -> world.getAllValuableResults().stream().anyMatch(r -> p.equals(r.getTarget())))
                .mapToInt(p -> getReward(p.getElementType()))
                .sum();
    }

    public static int calculateNonEnemyReward(Area area) {
        return area.getValuables().stream()
                .mapToInt(p -> getReward(p.getElementType()) )
                .sum();
    }

    public static int calculateEnemyReward(Area area) {
        return area.getValuables().stream()
                .filter(p -> getByElementType(p.getElementType()).equals(ENEMY_HEAD))
                .mapToInt(p -> world.getEnemySnake(p).getLength() * SNAKE_PART_REWARD)
                .sum();
    }

    public static int calculateTotalReward(Area area) {
        return calculateNonEnemyReward(area) + calculateEnemyReward(area);
    }

    public static int calculateAreaWeight(Area area) {
        return area.getReward().getPossibleReward() * ((AREA_SIZE * 10) / (area.getDistance() + AREA_SIZE));
    }
}
