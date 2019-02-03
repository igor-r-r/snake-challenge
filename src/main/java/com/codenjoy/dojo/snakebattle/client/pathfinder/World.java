package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateTotalEnemyLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.canEatStone;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getGroup;
import static com.codenjoy.dojo.snakebattle.model.Elements.APPLE;
import static com.codenjoy.dojo.snakebattle.model.Elements.FURY_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Data
@NoArgsConstructor
public class World {

    private Board board;
    private Snake mySnake = new Snake();

    private int totalEnemyLength;
    private List<PathPoint> allPathPoints;
    private List<PathPoint> applesAndGold;
    private List<PathPoint> valuablePathPoints;
    private TreeMap<Integer, List<PathPoint>> pathPointGroups;
    private List<Enemy> enemies;

    public World(Board board) {
        this.board = board;
    }

    public void updateWorldState(Board board) {
        setBoard(board);
        updateValuablePathPoints();
        updateApplesAndGold();
        updatePathPointGroups();
        updateMySnake();
        updateEnemies();
    }

    public void updateWorldState() {
        updateWorldState(board);
    }

    public void updateValuablePathPoints() {
        List<PathPoint> pathPoints = new ArrayList<>();

        pathPoints.addAll(toPathPointList(APPLE));
        //pathPoints.addAll(toPathPointList(board, FLYING_PILL));
        pathPoints.addAll(toPathPointList(FURY_PILL));
        pathPoints.addAll(toPathPointList(GOLD));
        if (canEatStone(board)) {
            pathPoints.addAll(toPathPointList(STONE));
        }

        valuablePathPoints = pathPoints;
    }

    private void updateApplesAndGold() {
        applesAndGold = toPathPointList(APPLE, GOLD);
    }

    public List<PathPoint> toPathPointList(Elements... elements) {
        return  board.get(elements).stream()
                .map(p -> buildPathPoint(p.getX(), p.getY(), board.getAt(p)))
                .collect(Collectors.toList());
    }

    private void updatePathPointGroups() {
        TreeMap<Integer, List<PathPoint>> groupMap = new TreeMap<>();
        Point me = board.getMe();

        for (PathPoint pathPoint : valuablePathPoints) {
            int group = getGroup(
                    calculateEstimatedDistance(me.getX(), me.getY(), pathPoint.getX(), pathPoint.getY()));
            if (groupMap.containsKey(group)) {
                groupMap.get(group).add(pathPoint);
            } else {
                groupMap.put(group, new ArrayList<>(asList(pathPoint)));
            }
        }

        pathPointGroups = groupMap;
    }

    private void updateMySnake() {
        updateSnakeState();

    }

    private void updateSnakeState() {
        if (!isEmpty(board.get(HEAD_EVIL))) {
            mySnake.setState(SnakeState.FURY);
        } else if (!isEmpty(board.get(HEAD_FLY))) {
            mySnake.setState(SnakeState.FLIGHT);
        } else {
            mySnake.setState(SnakeState.NORMAL);
        }
    }

    public void updateEnemies() {
        setTotalEnemyLength(calculateTotalEnemyLengthStupid());
        System.out.println("Enemy length: " + totalEnemyLength);

        List<Enemy> enemies = new ArrayList<>();
        enemies.addAll(toPathPointList(enemyHead).stream().map(this::toEnemy).collect(Collectors.toList()));

        this.enemies = enemies;
    }

    private Enemy toEnemy(PathPoint pathPoint) {
        Enemy enemy = new Enemy();
        Point me = board.getMe();

        enemy.setHead(pathPoint);
        //enemy.setLength(PathFinderUtils.calculateTotalEnemyLengthStupid()); // TODO calculate length
        enemy.setDistance(calculateEstimatedDistance(me.getX(), me.getY(), pathPoint.getX(), pathPoint.getY()));
        enemy.setState(SnakeState.getStateByElement(pathPoint.getElementType()));

        return enemy;
    }



}
