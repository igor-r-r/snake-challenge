package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder.world;
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

    public static Direction getCloseDirection(int headX, int headY, int targetX, int targetY) {
        if (Math.abs(headX - targetX) + Math.abs(headY - targetY) == 1) {
            if (headX < targetX) {
                return Direction.RIGHT;
            } else if (headX > targetX) {
                return Direction.LEFT;
            } else if (headY < targetY) {
                return Direction.UP;
            } else if (headY > targetY) {
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
        if (board.isBarrierAt(targetX, targetY)) {
            return false;
        }

        // TODO temporarily not allowed
        if (Arrays.asList(myBody).contains(board.getAt(targetX, targetY))) {
            return false;
        }

        // TODO temporarily not allowed
        if (Arrays.asList(myTail).contains(board.getAt(targetX, targetY))) {
            return false;
        }

        // TODO temporarily not allowed
        if (Arrays.asList(enemyBody).contains(board.getAt(targetX, targetY))) {
            return false;
        }

        // TODO temporarily not allowed
        if ((Arrays.asList(enemyHead).contains(board.getAt(targetX, targetY))
                && world.getMySnake().getLength() < world.getTotalEnemyLength() + 2)
                || board.getAt(targetX, targetY).equals(ENEMY_HEAD_EVIL)) {
            return false;
        }

        // STONE is allowed if length is more than 3
        if (Arrays.asList(STONE).contains(board.getAt(targetX, targetY)) && !canEatStone()) {
            return false;
        }

        return true;
    }

    public static boolean canEatStone() {
        return world.getMySnake().getLength() > 4
                || world.getMySnake().getState().equals(SnakeState.FURY); //calculateEstimatedDistance(board.getMe().getX());
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

    public static boolean isSnakeBody(Elements element) {
        return Set.of(enemyBody, myBody, enemyTail, myTail).stream()
                .flatMap(Arrays::stream)
                .anyMatch(e -> e.equals(element));
    }


    public static List<PathPoint> generateChildren(Board board, PathPoint parent, PathPoint target) {
        List<PathPoint> children = new ArrayList<>();

        for (int[] direction : childrenDirections) {

            if (!canPassThrough(board, parent.getX() + direction[0], parent.getY() + direction[1])) {
                continue;
            }

            int childX = parent.getX() + direction[0];
            int childY = parent.getY() + direction[1];

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

    public static String buildAct(Direction direction, boolean isDirectionBefore) {
        if (isDirectionBefore) {
            return direction.toString() + ", " + ACT.toString();
        } else {
            return ACT.toString() + ", " + direction.toString();
        }
    }


}
