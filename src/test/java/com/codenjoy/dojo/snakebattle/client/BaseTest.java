package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder;

public class BaseTest {

    protected Dice dice;
    protected Solver ai;
    protected PathFinder pathFinder;

    protected Board board(String board) {
        return (Board) new Board().forString(board);
    }


}
