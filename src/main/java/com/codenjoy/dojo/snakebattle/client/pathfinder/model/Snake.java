package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.Data;

@Data
public class Snake {

    private SnakeState state = SnakeState.EMPTY;
}
