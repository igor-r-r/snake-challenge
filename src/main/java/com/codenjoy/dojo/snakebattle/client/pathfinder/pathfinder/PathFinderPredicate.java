package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;

import java.util.function.Predicate;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.isMySnakeLonger;

public class PathFinderPredicate {

    public static Predicate<PathPoint> canAttackEnemy() {
        return p -> {
            Enemy enemy = world.getEnemySnake(p);
            Snake mySnake = world.getMySnake();

            if (isMySnakeLonger(enemy)) {
                return mySnake.isFury() || !enemy.isFury();
            } else {
                return mySnake.isFury() && !enemy.isFury();
            }
        };
    }

}
