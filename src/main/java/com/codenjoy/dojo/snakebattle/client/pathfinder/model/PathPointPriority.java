package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

    public static boolean isPriorityHigher(Elements current, Elements previous) {
        return getByElementType(current).priority > getByElementType(previous).priority;
    }
}
