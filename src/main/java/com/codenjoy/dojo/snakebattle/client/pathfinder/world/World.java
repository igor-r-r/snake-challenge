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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.SnakeState.getStateByElement;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.buildAreas;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyHead;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getGroup;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateEnemyLength;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils.calculateSnakeLengthStupid;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.toPathPointList;
import static com.codenjoy.dojo.snakebattle.model.Elements.APPLE;
import static com.codenjoy.dojo.snakebattle.model.Elements.FLYING_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.FURY_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static java.util.Collections.singletonList;

@Data
@NoArgsConstructor
public class World {

    private Board board;
    private Snake mySnake = new Snake();

    private Map<AreaCoordinates, Area> areas;
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
        updateAreas();
    }

    private void updateAreas() {
        areas = buildAreas(mySnake.getHead());
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
        valuablePathPoints = Stream.of(apples, gold, fury, stones, flight)
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
        mySnake.setHead(buildPathPoint(board.getMe()));
        updateMySnakeState();
        updateMySnakeLength();

    }

    private void updateMySnakeLength() {
        mySnake.setLength(calculateSnakeLengthStupid());
    }

    private void updateMySnakeState() {
        mySnake.setState(getStateByElement(board.getAt(board.getMe())));
    }

    public void updateEnemies() {
        this.enemies = toPathPointList(enemyHead).stream().map(this::toEnemy).collect(Collectors.toList());
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
}
