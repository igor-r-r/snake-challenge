package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyTail;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.isSnakeBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.myBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.myHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.myTail;
import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static java.util.Set.of;

public class SnakeLengthHelper {

    private static Map<Elements, Set<Elements>> elementsMap = new HashMap<>();

    static {

        // TODO all head states - dead sleep etc
        Set<Elements> anyAlive =
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_DOWN, ENEMY_TAIL_INACTIVE);

        elementsMap.put(ENEMY_HEAD_EVIL, anyAlive);
        elementsMap.put(ENEMY_HEAD_FLY, anyAlive);
        elementsMap.put(ENEMY_HEAD_SLEEP, anyAlive);

        elementsMap.put(ENEMY_HEAD_UP, of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_DOWN));
        elementsMap.put(ENEMY_HEAD_DOWN, of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_TAIL_END_UP));
        elementsMap.put(ENEMY_HEAD_LEFT, of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_UP, ENEMY_BODY_LEFT_DOWN, ENEMY_TAIL_END_RIGHT));
        elementsMap.put(ENEMY_HEAD_RIGHT, of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_RIGHT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_TAIL_END_LEFT));

        elementsMap.put(ENEMY_BODY_VERTICAL,
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_UP,
                        ENEMY_TAIL_END_DOWN));
        elementsMap.put(ENEMY_BODY_HORIZONTAL,
                of(ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP, ENEMY_TAIL_END_LEFT,
                        ENEMY_TAIL_END_RIGHT));

        elementsMap.put(ENEMY_BODY_LEFT_UP,
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP));
        elementsMap.put(ENEMY_BODY_LEFT_DOWN,
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_DOWN));

        elementsMap.put(ENEMY_BODY_RIGHT_UP,
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_UP));
        elementsMap.put(ENEMY_BODY_RIGHT_DOWN,
                of(ENEMY_BODY_VERTICAL, ENEMY_BODY_HORIZONTAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_UP,
                        ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_END_DOWN));
    }

    public static int calculateEnemyLength(PathPoint head) {
        System.out.println();
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

                if (isAllowedPart(child.getElementType(), current.getElementType())
                    && !child.equals(current)) {

                    visited.add(child);
                    current = child;
                    length++;
                    found = true;
                }

            }
            if (!found) {
                System.out.println("Length enemy(not found): " + length + ", path point: " + head);
                return length;
            }
        }

        System.out.println("Length enemy: " + length + "\n my length: " + world.getMySnake().getLength());
        return length;
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

    private static boolean isAllowedPart(Elements child, Elements parent) {
        if (elementsMap.get(parent) != null) {
            return elementsMap.get(parent).contains(child);
        }
        return false;
    }


}
