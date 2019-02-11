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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.FURY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.BOARD_LOWER_BOUNDARY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.BOARD_UPPER_BOUNDARY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getEnemyDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getPathPointByDirection;
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
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DEAD;
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
            ENEMY_HEAD_DEAD,   // этот раунд противник проиграл
            ENEMY_HEAD_EVIL,   // противник скушал таблетку ярости
            ENEMY_HEAD_FLY,    // противник скушал таблетку полета
            ENEMY_HEAD_SLEEP};
    public static final Elements[] enemyTail = {
            ENEMY_TAIL_END_DOWN,
            ENEMY_TAIL_END_LEFT,
            ENEMY_TAIL_END_UP,
            ENEMY_TAIL_END_RIGHT,
            ENEMY_TAIL_INACTIVE};

    public static int calculateEstimatedDistance(int currentX, int currentY, int targetX, int targetY) {
        return Math.abs(currentX - targetX)
                + Math.abs(currentY - targetY);
    }

    public static boolean canEnemyPassThrough(int enemyX, int enemyY, int targetX, int targetY) {
        if (validatePathPoint(targetX, targetY)) {
            return false;
        }

        PathPoint targetPathPoint = buildPathPoint(targetX, targetY);
        Elements targetElement = targetPathPoint.getElementType();

        if (world.getBoard().isBarrierAt(targetPathPoint.getX(), targetPathPoint.getY())) {
            return false;
        }

        // TODO temporarily not allowed
        if (isMySnakePart(targetElement)) {
            return canAttack(targetPathPoint, world.getMySnake().getHead());
        }

        // TODO temporarily not allowed
        if (isEnemySnakePart(targetElement)) {
            return canAttack(world.getMySnake().getHead(), targetPathPoint);
        }

        // STONE is allowed if length is more than 3
        if (STONE.equals(targetElement) && !canEatStone()) {
            return false;
        }

        return true;
    }

    public static boolean canPassThrough(int targetX, int targetY) {
        return validatePathPoint(targetX, targetY) && canPassThrough(buildPathPoint(targetX, targetY));
    }


    public static boolean canPassThrough(PathPoint targetPathPoint) {
        if (!validatePathPoint(targetPathPoint)) {
            return false;
        }

        if (targetPathPoint.getElementType() == null) {
            targetPathPoint.setElementType(world.getBoard().getAt(targetPathPoint.getX(), targetPathPoint.getY()));
        }

        Elements targetElement = targetPathPoint.getElementType();

        if (world.getBoard().isBarrierAt(targetPathPoint.getX(), targetPathPoint.getY())) {
            return false;
        }

        // TODO temporarily not allowed
        if (isMySnakePart(targetElement)) {
            return false;
        }

        // TODO temporarily not allowed
        if (isEnemySnakePart(targetElement)) {
            return canAttack(world.getMySnake().getHead(), targetPathPoint);
        }

        // STONE is allowed if length is more than 3
        if (STONE.equals(targetElement) && !canEatStone()) {
            return false;
        }

        return true;
    }

    public static boolean canAttackEnemy(PathPoint enemyHead) {
        return canAttack(world.getMySnake().getHead(), enemyHead);
    }


    public static boolean canAttack(PathPoint from, PathPoint to) {
        Snake attacker = world.getSnake(from);
        Snake victim = world.getSnakeByPart(to);

        if (!asList(enemyHead).contains(to.getElementType())) {
            return canAttackEnemyBody(attacker, victim);
        }

        if (!attacker.isFury()) {
            return isLonger(attacker, victim);
        } else {
            return !(victim.isFury() && !isLonger(attacker, victim));
        }
    }

    public static boolean canAttackEnemyBody(Snake attacker, Snake victim) {
        return attacker.isFury() && !victim.isFury();

    }

    public static boolean isLonger(Snake from, Snake to) {
        return from.getLength() > to.getLength() + 1;
    }

    public static boolean canEatStone() {
        return world.getMySnake().getLength() > 4
                || world.getMySnake().getState().equals(FURY); //calculateEstimatedDistance(board.getMe().getX());
    }

    public static boolean isSnakeBody(Elements element) {
        return Set.of(enemyBody, myBody, enemyTail, myTail).stream()
                .flatMap(Arrays::stream)
                .anyMatch(e -> e.equals(element));
    }

    public static List<PathPoint> generateChildren(PathPoint parent, PathPoint target) {
        List<PathPoint> children = new ArrayList<>();

        for (int[] direction : childrenDirections) {

            int childX = parent.getX() + direction[0];
            int childY = parent.getY() + direction[1];

            if (!canPassThrough(childX, childY)) {
                continue;
            }

            int g = parent.getG() + 1;
            int h = calculateEstimatedDistance(childX, childY, target.getX(), target.getY());
            int f = g + h;
            Elements elementType = world.getBoard().getAt(childX, childY);

            PathPoint child = PathPoint.builder()
                    .x(childX)
                    .y(childY)
                    .g(g)
                    .h(g)
                    .f(f)
                    .parent(parent)
                    .elementType(elementType)
                    .build();

            children.add(child);
        }

        return children;
    }

    public static int getGroup(int estimatedDistance) {
        return GROUP_STEP * (estimatedDistance / GROUP_STEP) + GROUP_STEP;
    }

    public static boolean shouldDropStone(int nextX, int nextY) {
        return (world.getBoard().getAt(nextX, nextY).equals(Elements.FURY_PILL)
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

    public static boolean validatePathPoint(PathPoint pathPoint) {
        return pathPoint != null && validatePathPoint(pathPoint.getX(), pathPoint.getY());
    }

    public static boolean validatePathPoint(int x, int y) {
        return !isOutsideBoard(x, y);
    }

    public static boolean isOutsideBoard(int targetX, int targetY) {
        return targetX < BOARD_LOWER_BOUNDARY || targetX > BOARD_UPPER_BOUNDARY || targetY < BOARD_LOWER_BOUNDARY || targetY > BOARD_UPPER_BOUNDARY;
    }

    public static PathPoint convertToProjectedHeadPathPoint(PathPoint currentPathPoint) {
        Direction currentEnemyDirection = getEnemyDirection(currentPathPoint.getElementType());

        if (currentEnemyDirection == null) {
            return currentPathPoint;
        }

        PathPoint projectedPathPoint = getPathPointByDirection(currentPathPoint, currentEnemyDirection);

        if (!canPassThrough(projectedPathPoint)) {
            return currentPathPoint;
        }

        return projectedPathPoint;
    }
}


