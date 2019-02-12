package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;

@Getter
public enum Strategy {
    ENEMY("enemyPathFinder"),
    STONE("stonePathFinder"),
    AREA("constantAreaAwarePathFinder");

    public static final int ENEMY_ATTACK_DISTANCE = 10;
    private String pathFinderName;

    Strategy(String pathFinderName) {

        this.pathFinderName = pathFinderName;
    }

    public static Strategy chooseStrategy() {
        List<Enemy> enemies = world.getEnemies();

        System.out.println("Can attack: " + enemies.stream()
                .map(e -> Boolean.toString(PathFinderUtils.canAttackEnemy(e.getHead())))
                .collect(Collectors.joining(", ")));

        if (enemies.stream().anyMatch(e -> e.getDistance() < ENEMY_ATTACK_DISTANCE
                && PathFinderUtils.canAttackEnemy(e.getHead()))) {
            return ENEMY;
        } else {
            return AREA;
        }

    }



}
