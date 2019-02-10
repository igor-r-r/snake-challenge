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
import com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.AreaAwarePathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.DirectionProvider;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.EnemyPathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.StonePathFinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy.AREA;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy.ENEMY;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy.STONE;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy.chooseStrategy;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;

/**
 * User: Igor Igor
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private static final String GAME_1 = "https://game1.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497";
    private static final String GAME_2 = "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497";
    private static final String GAME_3 = "https://game3.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497";

    private Map<Strategy, PathFinder> pathFinders = new HashMap<>();
    private DirectionProvider directionProvider = new DirectionProvider();


    YourSolver() {
        pathFinders.put(ENEMY, new EnemyPathFinder(new AStar()));
        pathFinders.put(STONE, new StonePathFinder(new AStar()));
        pathFinders.put(AREA, new AreaAwarePathFinder(new AStar()));
    }

    @Override
    public String get(Board board) {
        long startTime = System.nanoTime();

        if (board.isGameOver()) {
            return "";
        }

        String direction = "";
        try {
            world.updateWorldState(board);

            Optional<PathFinderResult> result = pathFinders.get(chooseStrategy()).findNextResult();
            direction = directionProvider.getFinalDirectionString(result.orElse(null));

            world.postUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
        return direction;
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                GAME_1,
                new YourSolver(),
                new Board());
    }

}
