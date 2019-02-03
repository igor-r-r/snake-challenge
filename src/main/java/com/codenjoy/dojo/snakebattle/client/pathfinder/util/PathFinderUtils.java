package com.codenjoy.dojo.snakebattle.client.pathfinder.util;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static final int GROUP_STEP = 5;
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
            ENEMY_HEAD_DEAD,   // этот раунд противник проиграл
            ENEMY_HEAD_EVIL,   // противник скушал таблетку ярости
            ENEMY_HEAD_FLY,    // противник скушал таблетку полета
            ENEMY_HEAD_SLEEP};

    public static final Elements[] optionalObstacles = {STONE};

    // needed to define which point should be visited first
    public static PathPointPriority getPathPointPriority(Elements elementType) {
        return PathPointPriority.getByElementType(elementType);
    }

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

        return Direction.STOP;

    }

    public static PathPoint buildPathPoint(Point p, Elements elementType) {
        PathPoint pathPoint = new PathPoint();
        pathPoint.setX(p.getX());
        pathPoint.setY(p.getY());
        pathPoint.setElementType(elementType);
        pathPoint.setPathPointPriority(getPathPointPriority(elementType));

        return pathPoint;
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
        if (Arrays.asList(enemyHead).contains(board.getAt(targetX, targetY))) {
            return false;
        }

        // STONE is allowed if length is more than 3
        if (Arrays.asList(STONE).contains(board.getAt(targetX, targetY)) && !canEatStone(board)) {
            return false;
        }

        return true;
    }

    public static boolean canEatStone(Board board) {
        int length = calculateSnakeLengthStupid(board);
        return length > 4
                || PathFinder.snake.getState().equals(SnakeState.FURY); //calculateEstimatedDistance(board.getMe().getX());
    }


    public static int calculateSnakeLengthStupid(Board board) {
        List<Point> head = board.get(myHead);
        List<Point> body = board.get(myBody);
        List<Point> tail = board.get(myTail);

        return head.size() + body.size() + tail.size();
    }

    //private static int calculateSnakeLength(Board board) {
    //    Point me = board.getMe();
    //
    //    Elements current = board.getAt(me);
    //    Point previous = null;
    //    int length = 1;
    //
    //    while (!Arrays.asList(myTail).contains(current)) {
    //
    //    }
    //
    //    return 0;
    //}


    //public static PathPoint findNextSnakePiece(Board board, PathPoint previous, PathPoint target) {
    //    for (int[] direction : childrenDirections) {
    //        int childX = target.getX() + direction[0];
    //        int childY = target.getY() + direction[1];
    //
    //        Elements element = board.getAt(childX, childY);
    //
    //        if (!Arrays.asList(myBody, myTail).contains(element) ) {
    //            continue;
    //        }
    //
    //        if ()
    //
    //        if (!canPassThrough(board, target.getX() + direction[0], target.getY() + direction[1])) {
    //            continue;
    //        }
    //
    //        PathPoint child = new PathPoint();
    //        child.setX(target.getX() + direction[0]);
    //        child.setY(target.getY() + direction[1]);
    //        child.setParent(target);
    //
    //        children.add(child);
    //    }
    //
    //    return null;
    //}


    public static List<PathPoint> generateChildren(Board board, PathPoint parent, PathPoint target) {
        List<PathPoint> children = new ArrayList<>();

        for (int[] direction : childrenDirections) {

            if (!canPassThrough(board, parent.getX() + direction[0], parent.getY() + direction[1])) {
                continue;
            }

            PathPoint child = new PathPoint();
            child.setX(parent.getX() + direction[0]);
            child.setY(parent.getY() + direction[1]);
            child.setParent(parent);
            child.setG(parent.getG() + 1);
            child.setH(calculateEstimatedDistance(child.getX(), child.getY(), target.getX(), target.getY()));
            child.setF(child.getG() + child.getH());

            children.add(child);
        }

        return children;
    }

    public static int getGroup(int estimatedDistance) {
        return GROUP_STEP * (estimatedDistance / GROUP_STEP) + GROUP_STEP;
    }
}
