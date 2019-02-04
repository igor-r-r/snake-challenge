package com.codenjoy.dojo.snakebattle.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.EnemyPathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.PathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.StonePathFinder;

import java.util.concurrent.TimeUnit;

/**
 * User: Igor Igor
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;
    private PathFinder pathFinder;

    YourSolver(Dice dice, PathFinder pathFinder) {
        this.dice = dice;
        this.pathFinder = pathFinder;
    }

    @Override
    public String get(Board board) {
        long startTime = System.nanoTime();
        this.board = board;
        if (board.isGameOver()) return "";
        PathFinder.world.updateWorldState(board);

        String direction = pathFinder.findPath();

        System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
        return direction;
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game1.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497",
                new YourSolver(new RandomDice(), new EnemyPathFinder(new AStar())),
                new Board());
    }

}
