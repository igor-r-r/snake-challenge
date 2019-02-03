package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.model.Elements;

public class FuryHandler extends PathFinder {

    public Direction move(Board board) {
        if (!board.getAt(board.getMe()).equals(Elements.HEAD_EVIL)) {

        }

        return null;
    }

}
