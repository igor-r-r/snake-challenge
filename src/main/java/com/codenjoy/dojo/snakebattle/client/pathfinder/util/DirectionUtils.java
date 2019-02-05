package com.codenjoy.dojo.snakebattle.client.pathfinder.util;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.LEFT;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.services.Direction.UP;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_LEFT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_UP;

public class DirectionUtils {

    static int[] UP_COORDS = {0, 1};
    static int[] DOWN_COORDS = {0, -1};
    static int[] RIGHT_COORDS = {1, 0};
    static int[] LEFT_COORDS = {-1, 0};

    public static Map<Elements, Direction> enemyHeadToDirectionMap = new HashMap<>();
    public static Map<Direction, int[]> directionToCoordsMap = new HashMap<>();

    static {
        enemyHeadToDirectionMap.put(ENEMY_HEAD_UP, UP);
        enemyHeadToDirectionMap.put(ENEMY_HEAD_DOWN, DOWN);
        enemyHeadToDirectionMap.put(ENEMY_HEAD_RIGHT, RIGHT);
        enemyHeadToDirectionMap.put(ENEMY_HEAD_LEFT, LEFT);

        directionToCoordsMap.put(UP, UP_COORDS);
        directionToCoordsMap.put(DOWN, DOWN_COORDS);
        directionToCoordsMap.put(RIGHT, RIGHT_COORDS);
        directionToCoordsMap.put(LEFT, LEFT_COORDS);
    }

    public static String buildAct(Direction direction, boolean isDirectionBefore) {
        if (isDirectionBefore) {
            return direction.toString() + ", " + ACT.toString();
        } else {
            return ACT.toString() + ", " + direction.toString();
        }
    }

    public static Direction getCloseDirection(PathPoint from, PathPoint to) {
        return getCloseDirection(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static Direction getCloseDirection(int fromX, int fromY, int toX, int toY) {
        if (Math.abs(fromX - toX) + Math.abs(fromY - toY) == 1) {
            if (fromX < toX) {
                return Direction.RIGHT;
            } else if (fromX > toX) {
                return Direction.LEFT;
            } else if (fromY < toY) {
                return Direction.UP;
            } else if (fromY > toY) {
                return Direction.DOWN;
            }
        }

        return Direction.RIGHT;
    }

    public static Direction getEnemyDirection(Elements head) {
        return enemyHeadToDirectionMap.get(head);
    }

    public static PathPoint getPathPointByDirection(PathPoint current, Direction direction) {
        int[] nextCoordsDelta = directionToCoordsMap.get(direction);
        if (nextCoordsDelta == null) {
            return null;
        }

        int nextX = current.getX() + nextCoordsDelta[0];
        int nextY = current.getY() + nextCoordsDelta[1];
        Elements nextElement = world.getBoard().getAt(nextX, nextY);

        return buildPathPoint(nextX, nextY, nextElement);
    }

}
