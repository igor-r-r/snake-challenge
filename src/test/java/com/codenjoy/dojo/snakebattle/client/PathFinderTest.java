package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.MapHelper;
import com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder.world;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PathFinderTest {

    private Dice dice;
    private Solver ai;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        ai = new YourSolver(dice, new PathFinder());
    }

    private Board board(String board) {
        return (Board) new Board().forString(board);
    }

    @Test
    public void shouldFindAndGroupValuablePathPoints() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼  ○     ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼     ○  ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);

        TreeMap<Integer, List<PathPoint>> groupMapActual = world.getPathPointGroups();

        assertEquals(2, groupMapActual.size());
        assertEquals(1, groupMapActual.get(5).size());
        assertEquals(1, groupMapActual.get(10).size());
        assertEquals(4, groupMapActual.get(5).get(0).getX());
        assertEquals(6, groupMapActual.get(5).get(0).getY());
        assertEquals(7, groupMapActual.get(10).get(0).getX());
        assertEquals(2, groupMapActual.get(10).get(0).getY());

    }


    @Test
    public void shouldFindAppleFirstGroup() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼ ○      ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        PathFinder pathFinder = new PathFinder();
        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextDirection();

        assertEquals(3, resultActual.get().getDistance());

    }

    @Test
    public void shouldFindAppleSecondGroup() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼ ╘►     ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼     ○  ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        PathFinder pathFinder = new PathFinder();
        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextDirection();

        assertEquals(9, resultActual.get().getDistance());

    }

    @Test
    public void shouldFindAppleWithBarrier() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼" +
                "☼☼        ☼" +
                "☼☼  ╘►    ☼" +
                "☼☼        ☼" +
                "☼☼ ☼☼☼☼☼  ☼" +
                "☼☼        ☼" +
                "☼☼   ○    ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼        ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼");

        PathFinder pathFinder = new PathFinder();
        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextDirection();

        assertEquals(10, resultActual.get().getDistance());

    }



}
