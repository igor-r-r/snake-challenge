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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.LEFT;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.services.Direction.UP;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyTail;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.isSnakeBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myTail;
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
import static java.util.Set.of;

public class SnakeLengthUtils {

    private static Map<Elements, Allowed> elementsMap = new HashMap<>();



    static class Allowed {
        private Set<Elements> allowedElements;
        private int[][] allowedDirectionCoords;
        private Set<Direction> allowedDirections;

        public Allowed(Set<Elements> allowedElements, Set<Direction> allowedDirections) {
            this.allowedElements = allowedElements;
            this.allowedDirections = allowedDirections;
        }
    }

    static {



        Set<Elements> anyAlive =
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_DOWN, ENEMY_TAIL_INACTIVE);
        Set<Direction> any = of(UP, DOWN, RIGHT, LEFT);

        Allowed anyAllowed = new Allowed(anyAlive, any);

        elementsMap.put(ENEMY_HEAD_EVIL, anyAllowed);
        elementsMap.put(ENEMY_HEAD_FLY, anyAllowed);
        elementsMap.put(ENEMY_HEAD_SLEEP, anyAllowed);

        elementsMap.put(ENEMY_HEAD_UP, new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_DOWN), of(DOWN)));
        elementsMap.put(ENEMY_HEAD_DOWN, new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_TAIL_END_UP), of(UP)));
        elementsMap.put(ENEMY_HEAD_LEFT, new Allowed(of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_UP, ENEMY_BODY_LEFT_DOWN, ENEMY_TAIL_END_RIGHT), of(RIGHT)));
        elementsMap.put(ENEMY_HEAD_RIGHT, new Allowed(of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_RIGHT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_TAIL_END_LEFT), of(LEFT)));

        elementsMap.put(ENEMY_BODY_VERTICAL,
                new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_UP,
                        ENEMY_TAIL_END_DOWN), of(UP, DOWN)));
        elementsMap.put(ENEMY_BODY_HORIZONTAL,
                new Allowed(of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_LEFT,
                        ENEMY_TAIL_END_RIGHT), of(RIGHT, LEFT)));

        elementsMap.put(ENEMY_BODY_LEFT_UP,
                new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP), of(UP, LEFT)));
        elementsMap.put(ENEMY_BODY_LEFT_DOWN,
                new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_DOWN), of(DOWN, LEFT)));

        elementsMap.put(ENEMY_BODY_RIGHT_UP,
                new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_UP), of(UP, RIGHT)));
        elementsMap.put(ENEMY_BODY_RIGHT_DOWN,
                new Allowed(of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_DOWN), of(DOWN, RIGHT)));
    }

    public static List<PathPoint> calculateEnemyLength(PathPoint head) {
        PathPoint current = head;
        List<PathPoint> visited = new ArrayList<>();
        visited.add(current);
        int length = 1;

        while (!of(enemyTail).contains(current.getElementType())) {
            boolean found = false;

            for (int[] direction : childrenDirections) {

                int childX = current.getX() + direction[0];
                int childY = current.getY() + direction[1];

                Elements element = world.getBoard().getAt(childX, childY);

                PathPoint child = PathPoint.builder()
                        .x(childX)
                        .y(childY)
                        .parent(current)
                        .elementType(element)
                        .build();


                if (visited.contains(child)) {
                    //System.out.println("Visited: " + child);
                    continue;
                }

                if (!isSnakeBody(element)) {
                    continue;
                }

                if (isAllowedPart(child.getElementType(),
                        current.getElementType(),
                        getCloseDirection(current, child))
                    && !child.equals(current)) {

                    visited.add(child);
                    current = child;
                    length++;
                    found = true;
                }

            }
            if (!found) {
                System.out.println("Length enemy(not found): " + length + ", path point: " + head);
                return visited;
            }
        }

        //System.out.println("Length enemy: " + length + "\n my length: " + world.getMySnake().getLength());
        return visited;
    }

    public static int calculateSnakeLengthStupid() {
        List<Point> head = world.getBoard().get(myHead);
        List<Point> body = world.getBoard().get(myBody);
        List<Point> tail = world.getBoard().get(myTail);

        return head.size() + body.size() + tail.size();
    }

    public static int calculateTotalEnemyLengthStupid() {
        List<Point> head = world.getBoard().get(enemyHead);
        List<Point> body = world.getBoard().get(enemyBody);
        List<Point> tail = world.getBoard().get(enemyTail);

        return head.size() + body.size() + tail.size();
    }

    private static boolean isAllowedPart(Elements child, Elements parent, Direction direction) {
        Allowed allowance = elementsMap.get(parent);

        if (allowance != null) {
            return allowance.allowedElements.contains(child)
                    && allowance.allowedDirections.contains(direction);
        }

        return false;
    }

    public static boolean isMySnakeLonger(PathPoint enemyHead) {
        return world.getMySnake().getLength() > world.getEnemySnake(enemyHead).getLength() + 1;
    }

    public static boolean isMySnakeLonger(Enemy enemySnake) {
        return world.getMySnake().getLength() > enemySnake.getLength() + 1;
    }

}
