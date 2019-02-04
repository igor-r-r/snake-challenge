package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.Data;

@Data
public class Snake {

    SnakeState state = SnakeState.NORMAL;
    protected boolean hasStone;
    protected PathPoint head;
    protected int stoneCount;
    private int length;

    public int changeStoneCount(int delta) {
        int newCount = stoneCount + delta;
        return stoneCount = newCount > 0 ? newCount : 0;
    }
}
