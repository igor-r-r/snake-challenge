package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Arrays;

public enum PathPointPriority {

    EMPTY_OR_OBSTACLE(0, null),
    FLYING_PILL(1, Elements.FLYING_PILL),
    FURY_PILL(2, Elements.FURY_PILL),
    APPLE(3, Elements.APPLE),
    GOLD(4, Elements.GOLD),
    STONE(5, Elements.STONE);

    int priority;
    Elements element;

    PathPointPriority(int priority, Elements element) {
        this.priority = priority;
        this.element = element;
    }

    public static PathPointPriority getByElementType(Elements element) {
        return Arrays.stream(values()).filter(p -> element.equals(p.element)).findAny().orElse(EMPTY_OR_OBSTACLE);
    }
}
