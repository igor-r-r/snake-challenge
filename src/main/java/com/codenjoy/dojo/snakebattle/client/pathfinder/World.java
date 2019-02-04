package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.calculateTotalEnemyLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinderUtils.getGroup;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.WorldBuildHelper.toPathPointList;
import static com.codenjoy.dojo.snakebattle.model.Elements.APPLE;
import static com.codenjoy.dojo.snakebattle.model.Elements.FLYING_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.FURY_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Data
@NoArgsConstructor
public class World {

    private Board board;
    private Snake mySnake = new Snake();

    private int totalEnemyLength;
    private List<PathPoint> applesAndGold;
    private List<PathPoint> apples;
    private List<PathPoint> stones;
    private List<PathPoint> gold;
    private List<PathPoint> fury;
    private List<PathPoint> flight;
    private List<PathPoint> valuablePathPoints;
    private TreeMap<Integer, List<PathPoint>> regularPathPointGroups;
    private List<Enemy> enemies;

    public World(Board board) {
        this.board = board;
    }

    public void updateWorldState(Board board) {
        setBoard(board);

        updateValuablePathPointsSeparate();
        updateValuablePathPoints();
        updatePathPointGroups();
        updateMySnake();
        updateEnemies();
    }

    public void updateWorldState() {
        updateWorldState(board);
    }

    private void updateValuablePathPointsSeparate() {
        applesAndGold = toPathPointList(APPLE, GOLD);
        apples = toPathPointList(APPLE);
        gold = toPathPointList(GOLD);
        fury = toPathPointList(FURY_PILL);
        flight = toPathPointList(FLYING_PILL);
        stones = toPathPointList(STONE);
    }

    public void updateValuablePathPoints() {
        valuablePathPoints = Stream.of(applesAndGold, apples, gold, fury, stones, flight)
                .flatMap(List::stream)
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
                groupMap.put(group, new ArrayList<>(singletonList(pathPoint)));
            }
        }

        regularPathPointGroups = groupMap;
    }

    private void updateMySnake() {
        updateMySnakeState();
        updateMySnakeLength();

    }

    private void updateMySnakeLength() {
        mySnake.setLength(calculateSnakeLengthStupid());
    }

    private void updateMySnakeState() {
        mySnake.setState(SnakeState.getStateByElement(board.getAt(board.getMe())));
    }

    public void updateEnemies() {
        setTotalEnemyLength(calculateTotalEnemyLengthStupid());

        this.enemies = toPathPointList(enemyHead).stream().map(this::toEnemy).collect(Collectors.toList());
    }

    private Enemy toEnemy(PathPoint enemyHead) {
        Enemy enemy = new Enemy();
        Point me = board.getMe();

        enemy.setHead(enemyHead);
        enemy.setLength(SnakeLengthHelper.calculateEnemyLength(enemyHead)); // TODO calculate length
        enemy.setDistance(calculateEstimatedDistance(me.getX(), me.getY(), enemyHead.getX(), enemyHead.getY()));
        enemy.setState(SnakeState.getStateByElement(enemyHead.getElementType()));

        return enemy;
    }



}
