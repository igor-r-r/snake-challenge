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
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.FURY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateEnemyLength;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.isMySnakeLonger;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_HORIZONTAL;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_LEFT_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_LEFT_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_RIGHT_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_RIGHT_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.BODY_VERTICAL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_HORIZONTAL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_LEFT_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_LEFT_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_RIGHT_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_RIGHT_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_BODY_VERTICAL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_LEFT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_SLEEP;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_TAIL_END_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_TAIL_END_LEFT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_TAIL_END_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_TAIL_END_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_TAIL_INACTIVE;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_LEFT;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_SLEEP;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static com.codenjoy.dojo.snakebattle.model.Elements.TAIL_END_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.TAIL_END_LEFT;
import static com.codenjoy.dojo.snakebattle.model.Elements.TAIL_END_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.TAIL_END_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.TAIL_INACTIVE;
import static java.util.Arrays.asList;

public class PathFinderUtils {

    public static final int GROUP_STEP = 10;
    public static final int[][] childrenDirections = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    public static final Elements[] myHead = {HEAD_DOWN,
            HEAD_LEFT,
            HEAD_RIGHT,
            HEAD_UP,
            HEAD_DEAD,    // этот раунд ты проиграл
            HEAD_EVIL,    // скушали таблетку ярости
            HEAD_FLY,     // скушали таблетку полета
            HEAD_SLEEP};

    public static final Elements[] myBody = {BODY_HORIZONTAL,
            BODY_VERTICAL,
            BODY_LEFT_DOWN,
            BODY_LEFT_UP,
            BODY_RIGHT_DOWN,
            BODY_RIGHT_UP};

    public static final Elements[] myTail = {TAIL_END_DOWN,
            TAIL_END_LEFT,
            TAIL_END_UP,
            TAIL_END_RIGHT,
            TAIL_INACTIVE};

    public static final Elements[] enemyBody = {ENEMY_BODY_HORIZONTAL,
            ENEMY_BODY_VERTICAL,
            ENEMY_BODY_LEFT_DOWN,
            ENEMY_BODY_LEFT_UP,
            ENEMY_BODY_RIGHT_DOWN,
            ENEMY_BODY_RIGHT_UP};

    public static final Elements[] enemyHead = {ENEMY_HEAD_DOWN,
            ENEMY_HEAD_LEFT,
            ENEMY_HEAD_RIGHT,
            ENEMY_HEAD_UP,
            //ENEMY_HEAD_DEAD,   // этот раунд противник проиграл
            ENEMY_HEAD_EVIL,   // противник скушал таблетку ярости
            ENEMY_HEAD_FLY,    // противник скушал таблетку полета
            ENEMY_HEAD_SLEEP};
    public static final Elements[] enemyTail = {
            ENEMY_TAIL_END_DOWN,
            ENEMY_TAIL_END_LEFT,
            ENEMY_TAIL_END_UP,
            ENEMY_TAIL_END_RIGHT,
            ENEMY_TAIL_INACTIVE};

    public static final Elements[] optionalObstacles = {STONE};

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

    public static int calculateEstimatedDistance(int currentX, int currentY, int targetX, int targetY) {
        return Math.abs(currentX - targetX)
                + Math.abs(currentY - targetY);
    }

    public static boolean canPassThrough(Board board, int targetX, int targetY) {
        if (targetX < 0 || targetX > 29 || targetY < 0 || targetY > 29) {
            return false;
        }

        Elements targetElement = board.getAt(targetX, targetY);
        PathPoint targetPathPoint = buildPathPoint(targetX, targetY, targetElement);
        Snake mySnake = world.getMySnake();

        if (board.isBarrierAt(targetX, targetY)) {
            return false;
        }

        // TODO temporarily not allowed
        if (isMySnakePart(targetElement)) {
            return false;
        }

        // TODO temporarily not allowed
        if (isEnemySnakePart(targetElement)) {
            Enemy enemy = world.getEnemyByPart(targetPathPoint);

            if (!mySnake.isFury()) {
                if (!asList(enemyHead).contains(targetElement)) {
                    return false;
                } else {
                    return isMySnakeLonger(enemy);
                }

            } else {
                if (enemy.isFury() && !isMySnakeLonger(enemy)) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        if (asList(enemyHead).contains(targetElement)
                && isMySnakeLonger(targetPathPoint)) {
            if (targetElement.equals(ENEMY_HEAD_EVIL)) {
                return false;
            }
            if (asList(enemyHead).contains(targetElement)
                    && !world.getMySnake().getState().equals(FURY)) {
                return false;
            }
            if (world.getMySnake().getState().equals(FURY)) {
                return true;
            }
        } else if (!world.getMySnake().getState().equals(FURY) && targetElement.equals(ENEMY_HEAD_EVIL)) {
            return false;
        }

        if (targetElement.equals(ENEMY_HEAD_EVIL)
                && !isMySnakeLonger(world.getEnemyByPart(targetPathPoint).getHead())) {
            return false;
        }

        // STONE is allowed if length is more than 3
        if (asList(STONE).contains(targetElement) && !canEatStone()) {
            return false;
        }

        return true;
    }

    public static boolean canEatStone() {
        return world.getMySnake().getLength() > 4
                || world.getMySnake().getState().equals(FURY); //calculateEstimatedDistance(board.getMe().getX());
    }


    public static int calculateSnakeLengthStupid() {
        List<Point> head = world.getBoard().get(myHead);
        List<Point> body = world.getBoard().get(myBody);
        List<Point> tail = world.getBoard().get(myTail);

        return head.size() + body.size() + tail.size();
    }

    public static boolean isSnakeBody(Elements element) {
        return Set.of(enemyBody, myBody, enemyTail, myTail).stream()
                .flatMap(Arrays::stream)
                .anyMatch(e -> e.equals(element));
    }


    public static List<PathPoint> generateChildren(Board board, PathPoint parent, PathPoint target) {
        List<PathPoint> children = new ArrayList<>();

        for (int[] direction : childrenDirections) {

            int childX = parent.getX() + direction[0];
            int childY = parent.getY() + direction[1];

            if (!canPassThrough(board, childX, childY)) {
                continue;
            }

            PathPoint child = PathPoint.builder()
                    .x(childX)
                    .y(childY)
                    .parent(parent)
                    .g(parent.getG() + 1)
                    .h(calculateEstimatedDistance(childX, childY, target.getX(), target.getY()))
                    .build();
            child.setF(child.getG() + child.getH());

            children.add(child);
        }

        return children;
    }

    public static int getGroup(int estimatedDistance) {
        return GROUP_STEP * (estimatedDistance / GROUP_STEP) + GROUP_STEP;
    }

    public static boolean shouldDropStone(int nextX, int nextY) {
        System.out.println("Stone check: " + world.getMySnake().getStoneCount());
        System.out.println("Stone check: " + world.getMySnake().getState());
        return (world.getBoard().getAt(nextX, nextY).equals(Elements.FURY_PILL)
                || world.getMySnake().getLength() > 4
                || world.getMySnake().getState().equals(FURY))
                && world.getMySnake().getStoneCount() > 0;
    }

    public static boolean isMySnakePart(Elements targetElement) {
        return asList(myHead).contains(targetElement)
                || asList(myBody).contains(targetElement)
                || asList(myTail).contains(targetElement);
    }

    public static boolean isEnemySnakePart(Elements targetElement) {
        return asList(enemyHead).contains(targetElement)
                || asList(enemyBody).contains(targetElement)
                || asList(enemyTail).contains(targetElement);

    }


}
