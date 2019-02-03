package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils;

import java.util.List;
import java.util.TreeMap;

public class MapHelper {

    public TreeMap<Integer, List<Enemy>> findAllEnemies(Board board) {
        TreeMap<Integer, List<Enemy>> enemiesMap = new TreeMap<>();

        List<Point> enemies = board.get(PathFinderUtils.enemyHead);

        return enemiesMap;

    }

}
