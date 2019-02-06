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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.DirectionProvider;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.StonePathFinder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PathFinderTest extends BaseTest {

    @Before
    public void setup() {
        dice = mock(Dice.class);
        pathFinder = new StonePathFinder(new AStar(), new DirectionProvider());
        ai = new YourSolver(dice, pathFinder);
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

        TreeMap<Integer, List<PathPoint>> groupMapActual = world.getRegularPathPointGroups();

        assertEquals(1, groupMapActual.size());
        assertEquals(2, groupMapActual.get(10).size());
        assertEquals(4, groupMapActual.get(10).get(0).getX());
        assertEquals(6, groupMapActual.get(10).get(0).getY());
        assertEquals(7, groupMapActual.get(10).get(1).getX());
        assertEquals(2, groupMapActual.get(10).get(1).getY());

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

        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextResult();

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

        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextResult();

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

        world.updateWorldState(board);
        Optional<PathFinderResult> resultActual = pathFinder.findNextResult();

        assertEquals(10, resultActual.get().getDistance());

    }



}
