package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.EnemyPathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.SnakeLengthHelper;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.model.Elements;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.WorldBuildHelper.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DOWN;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_UP;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EnemyPathFinderTest {

    private Dice dice;
    private Solver ai;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        ai = new YourSolver(dice, new EnemyPathFinder(new AStar()));
    }

    private Board board(String board) {
        return (Board) new Board().forString(board);
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

        assertEquals(7, world.getTotalEnemyLength());
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

        EnemyPathFinder pathFinder = new EnemyPathFinder(new AStar());
        world.updateWorldState(board);

        PathFinderResult resultActual = pathFinder.findNextDirection().get();

        assertNotNull(resultActual.getTargetElementType());
        assertEquals(GOLD, resultActual.getTargetElementType());
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

        EnemyPathFinder pathFinder = new EnemyPathFinder(new AStar());
        world.updateWorldState(board);

        PathFinderResult resultActual = pathFinder.findNextDirection().get();

        assertNotNull(resultActual);
        assertNotNull(resultActual.getTargetElementType());
        assertEquals(ENEMY_HEAD_DOWN, resultActual.getTargetElementType());

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

        //EnemyPathFinder pathFinder = new EnemyPathFinder(new AStar());
        world.updateWorldState(board);

        //PathFinderResult resultActual = pathFinder.findNextDirection().get();

        int enemyX = 3;
        int enemyY = 2;
        Elements enemyElement = world.getBoard().getAt(3, 2);

        int lengthActual = SnakeLengthHelper.calculateEnemyLength(buildPathPoint(enemyX, enemyY, enemyElement));

        assertEquals(3, lengthActual);

    }
}
