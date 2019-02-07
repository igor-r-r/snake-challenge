package com.codenjoy.dojo.snakebattle.client;

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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.DirectionProvider;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.EnemyPathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.SnakeLengthUtils;
import com.codenjoy.dojo.snakebattle.model.Elements;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.world.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_RIGHT;
import static com.codenjoy.dojo.snakebattle.model.Elements.NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EnemyPathFinderTest extends BaseTest {

    @Before
    public void setup() {
        pathFinder = new EnemyPathFinder(new AStar(), new DirectionProvider());
        dice = mock(Dice.class);
        ai = new YourSolver(dice, pathFinder);
    }

    @Test
    public void shouldFindEnemies() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼      ˄ ☼" +
                "☼☼  ○   ¤ ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼ æ      ☼" +
                "☼☼ ˅   ○  ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");


        world.updateWorldState(board);

        List<Enemy> enemiesActual = world.getEnemies();

        assertEquals(2, enemiesActual.size());

        assertEquals(1, enemiesActual.stream().filter(enemy -> enemy.getHead().getElementType().equals(ENEMY_HEAD_DOWN)).count());
        assertEquals(1, enemiesActual.stream().filter(enemy -> enemy.getHead().getElementType().equals(ENEMY_HEAD_UP)).count());
        assertEquals(0, enemiesActual.stream().filter(enemy -> enemy.getHead().getElementType().equals(ENEMY_HEAD_DEAD)).count());

    }

    @Test
    public void shouldCalculateTotalEnemyLength() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼      ˄ ☼" +
                "☼☼      │ ☼" +
                "☼☼      │ ☼" +
                "☼☼ æ    ¤ ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");


        world.updateWorldState(board);


        //assertEquals(7, world.getTotalEnemyLength());
    }

    @Test
    public void shouldChooseHigherPriorityTargetWhenLengthIsLess() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼      ˄ ☼" +
                "☼☼      │ ☼" +
                "☼☼      │ ☼" +
                "☼☼ æ    ¤ ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        EnemyPathFinder pathFinder = new EnemyPathFinder(new AStar(), new DirectionProvider());
        world.updateWorldState(board);

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual.getTarget().getElementType());
        assertEquals(GOLD, resultActual.getTarget().getElementType());
    }

    @Test
    public void shouldReturnCorrectEnemyResult() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ║      ☼" +
                "☼☼ ║      ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼ æ      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        EnemyPathFinder pathFinder = new EnemyPathFinder(new AStar(), new DirectionProvider());
        world.updateWorldState(board);

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());
        assertEquals(ENEMY_HEAD_DOWN, resultActual.getRealTarget().getElementType());

    }

    @Test
    public void shouldCalculateEnemyLength() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ║      ☼" +
                "☼☼ ║      ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼ æ      ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);

        int enemyX = 3;
        int enemyY = 2;
        Elements enemyElement = world.getBoard().getAt(3, 2);

        List<PathPoint> enemyParts = SnakeLengthUtils.calculateEnemyLength(buildPathPoint(enemyX, enemyY, enemyElement));

        assertEquals(3, enemyParts.size());

    }

    @Test
    public void shouldCalculateEnemyLengthComplex() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ║      ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼  ┌┐    ☼" +
                "☼☼ æ││    ☼" +
                "☼☼ ││˅    ☼" +
                "☼☼ └┘  ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);

        int enemyX = 5;
        int enemyY = 3;
        Elements enemyElement = world.getBoard().getAt(enemyX, enemyY);

        List<PathPoint> enemyParts = SnakeLengthUtils.calculateEnemyLength(buildPathPoint(enemyX, enemyY, enemyElement));

        assertEquals(10, enemyParts.size());

    }

    @Test
    public void shouldAttackEnemyIfMySnakeIsInFuryState() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼ ╘♥     ☼" +
                "☼☼        ☼" +
                "☼☼ æ      ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);
        assertEquals(HEAD_EVIL, world.getBoard().getAt(world.getBoard().getMe()));

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());
        
        assertEquals(1, resultActual.getTarget().getY());

        //assertEquals(ENEMY_HEAD_DOWN, resultActual.getTarget().getElementType());
    }

    @Test
    public void shouldoNotAttackEnemyIfMySnakeIsNotInFuryState() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼ æ      ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);
        assertEquals(HEAD_RIGHT, world.getBoard().getAt(world.getBoard().getMe()));

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());

        assertEquals(GOLD, resultActual.getTarget().getElementType());
    }
    

    @Test
    public void shouldNotHitEnemiesBody() {
        Board board = board(""
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                + "☼☼            ○              ☼"
                + "☼#                           ☼"
                + "☼☼                 ○         ☼"
                + "☼☼                      ○    ☼"
                + "☼☼           ●    ○          ☼"
                + "☼☼     ☼☼☼☼☼                 ☼"
                + "☼☼     ☼                     ☼"
                + "☼#     ☼☼☼        ☼☼☼☼#      ☼"
                + "☼☼     ☼         æ☼   ☼  ●   ☼"
                + "☼☼     ☼☼☼☼#     │☼☼☼☼#      ☼"
                + "☼☼            ╔═►│☼          ☼"
                + "☼☼○           ║  │☼          ☼"
                + "☼☼    ●  ○    ║  ˅  ®        ☼"
                + "☼#            ║○             ☼"
                + "☼☼           ○║              ☼"
                + "☼☼        ☼☼☼╘╝          ©   ☼"
                + "☼☼   ○   ☼  ☼                ☼"
                + "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼"
                + "☼☼ ○    ☼   ☼   ● ☼○☼ ☼ ☼    ☼"
                + "☼#      ☼   ☼     ☼  ☼  ☼    ☼"
                + "☼☼                ☼     ☼    ☼"
                + "☼☼     ●          ☼     ☼    ☼"
                + "☼☼   ○                       ☼"
                + "☼☼                           ☼"
                + "☼☼             ●             ☼"
                + "☼#                           ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);
        assertEquals(HEAD_RIGHT, world.getBoard().getAt(world.getBoard().getMe()));

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());

        assertEquals(ENEMY_HEAD_DOWN, resultActual.getRealTarget().getElementType());
        assertEquals(NONE, resultActual.getTarget().getElementType());
        assertEquals(DOWN, resultActual.getDirection());
    }

    @Test
    public void shouldNotHitTail() {
        Board board = board("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                + "☼☼         ○                 ☼"
                + "☼#                        ×> ☼"
                + "☼☼  ○    ●         ○         ☼"
                + "☼☼                      ○    ☼"
                + "☼☼ ○         ●    ○          ☼"
                + "☼☼     ☼☼☼☼☼●                ☼"
                + "☼☼     ☼                     ☼"
                + "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼"
                + "☼☼     ☼          ☼   ☼  ●   ☼"
                + "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼"
                + "☼☼                ☼          ☼"
                + "☼☼○               ☼         $☼"
                + "☼☼    ●       ˄              ☼"
                + "☼#            │○      ○      ☼"
                + "☼☼            │              ☼"
                + "☼☼        ☼☼☼×┘              ☼"
                + "☼☼       ☼  ☼╔►              ☼"
                + "☼☼    ○ ☼☼☼☼#║    ☼☼   ☼#    ☼"
                + "☼☼      ☼   ☼║  ● ☼ ☼ ☼ ☼ ○  ☼"
                + "☼#      ☼   ☼║    ☼  ☼  ☼    ☼"
                + "☼☼          ╘╝    ☼     ☼    ☼"
                + "☼☼     ●          ☼     ☼    ☼"
                + "☼☼                           ☼"
                + "☼☼                 ©○        ☼"
                + "☼☼             ●         ○   ☼"
                + "☼#                           ☼"
                + "☼☼               ○           ☼"
                + "☼☼                           ☼"
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);
        assertEquals(HEAD_RIGHT, world.getBoard().getAt(world.getBoard().getMe()));

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());

        assertEquals(ENEMY_HEAD_UP, resultActual.getRealTarget().getElementType());
        assertEquals(NONE, resultActual.getTarget().getElementType());
        assertEquals(RIGHT, resultActual.getDirection());
    }

    @Test
    public void shouldFindMostValuablePathToEnemy() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼     ╘♥ ☼" +
                "☼☼        ☼" +
                "☼☼    $   ☼" +
                "☼☼        ☼" +
                "☼☼ æ  ○   ☼" +
                "☼☼ │      ☼" +
                "☼☼ ˅   ○$ ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);
        assertEquals(HEAD_EVIL, world.getBoard().getAt(world.getBoard().getMe()));

        PathFinderResult resultActual = pathFinder.findNextResult().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTarget().getElementType());

        assertEquals(7, resultActual.getWeight());

        //assertEquals(ENEMY_HEAD_DOWN, resultActual.getTarget().getElementType());
    }
}
