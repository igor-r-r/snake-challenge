package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Enemy extends Snake {

    private int distance;

}
