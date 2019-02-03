package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Enemy extends Snake {

    private int length;
    private int distance;

}
