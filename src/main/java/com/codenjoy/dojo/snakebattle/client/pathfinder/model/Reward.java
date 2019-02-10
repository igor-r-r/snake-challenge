package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Reward {

    @EqualsAndHashCode.Include
    private int possibleReward;
    private int nonEnemyReward;
    private int enemyReward;
    private int totalReward;

}
