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
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.DirectionProvider;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.Strategy.chooseStrategy;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;

/**
 * User: Igor Igor
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
@Service
public class YourSolver implements Solver<Board> {

    private static final String GAME_1 = "https://game1.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497";
    private static final String GAME_2 = "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/fordou37@gmail.com?code=1380899103789497";
    private static final String GAME_3 = "https://game3.epam-bot-challenge.com.ua/codenjoy-contest/board/player/9f8zeyltn8doktebyp38?code=5656420843129828798";
    private static final String TEST_GAME = "https://snakebattle.tk/codenjoy-contest/board/player/igorigor@gmail.com?code=5720714372095653995";

    private Map<String, PathFinder> pathFinders;


    private DirectionProvider directionProvider = new DirectionProvider();

    @Autowired
    public YourSolver(Map<String, PathFinder> pathFinders) {
        this.pathFinders = pathFinders;

        runWebSocket();
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

            Optional<PathFinderResult> result = pathFinders.get(chooseStrategy().getPathFinderName()).findNextResult();
            direction = directionProvider.getFinalDirectionString(result.orElse(null));

            world.postTick();
        } catch (Exception e) {
            e.printStackTrace();
            return directionProvider.anyDirection();
        }

        System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));
        return direction;
    }

    private void runWebSocket() {
        WebSocketRunner.runClient(
                GAME_3,
                this,
                new Board());
    }

}
