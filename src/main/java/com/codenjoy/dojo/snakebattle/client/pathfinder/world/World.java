package com.codenjoy.dojo.snakebattle.client.pathfinder.world;

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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.Searcher;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.FURY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.SLEEP;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.getStateByElement;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinderPredicate.canAttackEnemy;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.buildConstantAreas;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canEatStoneLongest;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.convertToProjectedHeadPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getAllPossibleEnemyPositions;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getGroup;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.isRestricted;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myBody;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.myTail;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateEnemyLength;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.toPathPointList;
import static com.codenjoy.dojo.snakebattle.model.Elements.APPLE;
import static com.codenjoy.dojo.snakebattle.model.Elements.FLYING_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.FURY_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class World {

    public static List<PathPoint> restrictedPathPoints;
    private static final int TICK_LIMIT = 300;
    private static final int STONE_TICK_TIME = 200;


    private int tickCounter;
    private Board board;
    private Snake mySnake = new Snake();
    private Searcher searcher = new AStar();

    private Map<AreaCoordinates, Area> areas;
    private Map<AreaCoordinates, Area> constantAreas;
    private List<PathPoint> apples;
    private List<PathPoint> stones;
    private List<PathPoint> gold;
    private List<PathPoint> fury;
    private List<PathPoint> flight;
    private List<PathPoint> valuablePathPoints;
    private List<PathPoint> allProjectedEnemyPositions;
    private Map<PathPoint, List<Enemy>> possibleEnemyPositions;
    private TreeMap<Integer, List<PathPoint>> groups;

    private List<Enemy> enemies;
    private List<Snake> allSnakes;

    private List<PathFinderResult> allValuableResults;
    private List<PathFinderResult> allEnemyResults;
    private Map<Integer, List<PathFinderResult>> groupsResults;

    public World(Board board) {
        this.board = board;

    }

    public void updateRestricted() {
        restrictedPathPoints = new ArrayList<>();
        restrictedPathPoints.add(buildPathPoint(9, 22));
        restrictedPathPoints.add(buildPathPoint(10, 22));
        restrictedPathPoints.add(buildPathPoint(9, 20));
        restrictedPathPoints.add(buildPathPoint(10, 20));
        restrictedPathPoints.add(buildPathPoint(19, 10));
        restrictedPathPoints.add(buildPathPoint(21, 10));
        restrictedPathPoints.add(buildPathPoint(23, 10));
    }

    public void updateWorldState(Board board) {
        setBoard(board);

        if (board.size() == 30) {
            updateRestricted();
        }
        updateValuablePathPointsSeparate();
        updateValuablePathPoints();
        updatePathPointGroups();
        updateMySnake();
        updateEnemies();
        updateAllSnakes();
        updatePossibleEnemyPositions();
        updateAllProjectedEnemyPositions();
        updateAllValuableResults();
        updateAllEnemyResults();
        updateGroupResults();
        updateConstantAreas();
        updateTickCounter();
    }

    private void updateConstantAreas() {
        constantAreas = buildConstantAreas();
    }

    private void updateValuablePathPointsSeparate() {
        apples = toPathPointList(APPLE);
        gold = toPathPointList(GOLD);
        fury = toPathPointList(FURY_PILL);
        flight = toPathPointList(FLYING_PILL);
        //stones = canEatStoneLongest() ? toPathPointList(STONE) : emptyList();

        if (tickCounter < STONE_TICK_TIME) {
            if (canEatStoneLongest()) {
                stones = toPathPointList(STONE);
            } else {
                stones = emptyList();
            }
        } else {
            stones = toPathPointList(STONE);
        }

    }

    public void updateValuablePathPoints() {
        valuablePathPoints = Stream.of(apples, gold, fury, stones, flight)
                .flatMap(List::stream)
                .filter(p -> !isRestricted(p))
                .collect(toList());
    }

    private void updatePathPointGroups() {
        TreeMap<Integer, List<PathPoint>> groupMap = new TreeMap<>();
        Point me = board.getMe();

        System.out.println("Valuables: " + valuablePathPoints.stream().filter(p -> p.getElementType().equals(STONE)).collect(Collectors.toList()));

        for (PathPoint pathPoint : valuablePathPoints) {
            int group = getGroup(
                    calculateEstimatedDistance(me.getX(), me.getY(), pathPoint.getX(), pathPoint.getY()));
            if (groupMap.containsKey(group)) {
                groupMap.get(group).add(pathPoint);
            } else {
                groupMap.put(group, new ArrayList<>(singletonList(pathPoint)));
            }
        }

        groups = groupMap;
    }

    private void updateMySnake() {
        mySnake.setPreviousHead(mySnake.getHead());
        mySnake.setHead(buildPathPoint(board.getMe()));
        updateMySnakeDirection();
        updateMySnakeState();
        updateMySnakeLength();
        updateMySnakeParts();

    }

    private void updateMySnakeDirection() {
        if (mySnake.getPreviousHead() != null) {
            mySnake.setDirection(getCloseDirection(mySnake.getPreviousHead(), mySnake.getHead()));
        } else {
            mySnake.setDirection(RIGHT);
        }
    }

    private void updateMySnakeLength() {
        mySnake.setLength(calculateSnakeLengthStupid());
    }

    private void updateMySnakeState() {
        mySnake.setPreviousState(mySnake.getState());
        mySnake.setState(getStateByElement(board.getAt(board.getMe())));
        if (FURY.equals(mySnake.getState())) {
            mySnake.setFuryCounter(mySnake.getFuryCounter() + 1);
            //System.out.println("Fury incremented: " + mySnake.getFuryCounter());
        }
    }

    private void updateMySnakeParts() {
        this.mySnake.setParts(world.getBoard()
                .get(Stream.of(myHead, myBody, myTail)
                        .flatMap(Stream::of)
                        .toArray(Elements[]::new))
                .stream()
                .map(WorldBuildHelper::buildPathPoint)
                .collect(toList()));
    }

    private void updateEnemies() {
        this.enemies = toPathPointList(enemyHead).stream()
                .map(this::toEnemy)
                .collect(toList());
    }

    private void updateAllProjectedEnemyPositions() {
        this.allProjectedEnemyPositions = enemies.stream()
                .map(e -> convertToProjectedHeadPathPoint(e.getHead()))
                .collect(toList());
    }

    private void updatePossibleEnemyPositions() {
        Map<PathPoint, List<Enemy>> possibleEnemyPositionMap = new HashMap<>();

        for (Enemy enemy : enemies) {
            PathPoint head = enemy.getHead();
            List<PathPoint> possiblePositions = getAllPossibleEnemyPositions(head);
            for (PathPoint pathPoint : possiblePositions) {

                if (possibleEnemyPositionMap.containsKey(pathPoint)) {
                    possibleEnemyPositionMap.get(pathPoint).add(enemy);
                } else {
                    possibleEnemyPositionMap.put(pathPoint, new ArrayList<>(singletonList(enemy)));
                }
            }
        }

        this.possibleEnemyPositions = possibleEnemyPositionMap;
        System.out.println("possibleEnemyPositions:" + possibleEnemyPositions);
    }

    private void updateAllSnakes() {
        this.allSnakes = new ArrayList<>(singletonList(mySnake));
        this.allSnakes.addAll(enemies);
    }


    //
    // UPDATE RESULTS
    //
    public void updateAllValuableResults() {
        long startTime = System.nanoTime();

        this.allValuableResults = valuablePathPoints.stream().parallel()
                .map(p -> searcher.findSinglePath(p))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        System.out.println("!!!! All results time: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
    }

    public void updateGroupResults() {
        PathPoint me = mySnake.getHead();
        groupsResults = allValuableResults.stream()
                .collect(groupingBy(r -> getGroup(
                        calculateEstimatedDistance(me.getX(), me.getY(), r.getTarget().getX(), r.getTarget().getY()))));

    }

    private void updateAllEnemyResults() {
        List<PathFinderResult> enemyResults = new ArrayList<>();
        List<PathPoint> enemyHeads = enemies.stream()
                .map(Snake::getHead)
                .collect(toList());
        for (PathPoint pathPoint : enemyHeads) {
            if (canAttackEnemy().test(pathPoint)) {
                PathPoint p = convertToProjectedHeadPathPoint(pathPoint);
                Optional<PathFinderResult> singlePathResultOptional = searcher.findSinglePath(p);
                if (singlePathResultOptional.isPresent()) {
                    PathFinderResult result = singlePathResultOptional.get();
                    result.setRealTarget(pathPoint);
                    enemyResults.add(result);
                }
            }
        }

        allEnemyResults = enemyResults;
    }

    private void updateTickCounter() {
        System.out.println("Tick: " + tickCounter);
        if (SLEEP.equals(mySnake.getPreviousState())) {
            tickCounter = 0;
        } else {
            tickCounter++;
        }

    }

    /**
     * Use after all path finding operations are done.
     * Should be called right before direction string return.
     */
    public void postTick() {
        if (mySnake.getFuryCounter() > 9) {
            mySnake.setFuryCounter(0);
        }
    }

    private Enemy toEnemy(PathPoint enemyHead) {
        Enemy enemy = new Enemy();
        Point me = board.getMe();

        enemy.setHead(enemyHead);
        enemy.setParts(calculateEnemyLength(enemyHead));
        enemy.setLength(enemy.getParts().size());
        // TODO calculate length
        enemy.setDistance(calculateEstimatedDistance(me.getX(), me.getY(), enemyHead.getX(), enemyHead.getY()));
        enemy.setState(getStateByElement(enemyHead.getElementType()));

        return enemy;
    }

    public Enemy getEnemySnake(PathPoint head) {
        return enemies.stream().filter(enemy -> enemy.getHead().equals(head)).findFirst().orElse(null);
    }

    public Enemy getEnemyByPart(PathPoint part) {
        return enemies.stream().filter(e -> e.getParts().contains(part)).findFirst().orElse(null);
    }

    public Snake getSnake(PathPoint head) {
        return allSnakes.stream().filter(s -> s.getHead().equals(head)).findFirst().orElseThrow(RuntimeException::new);
    }

    public Snake getSnakeByPart(PathPoint part) {
        return allSnakes.stream()
                .filter(s -> s.getParts().contains(part) || s.getHead().equals(part))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
