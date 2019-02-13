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
import java.util.Set;

import lombok.Getter;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.enemyHead;
import static java.util.Set.of;

@Getter
public enum PathPointPriority {

    EMPTY_OR_OBSTACLE(0, 0, null),
    FLYING_PILL(1, 0, of(Elements.FLYING_PILL)),
    FURY_PILL(7, 0, of(Elements.FURY_PILL)),
    APPLE(2, 1, of(Elements.APPLE)),
    GOLD(5, 10, of(Elements.GOLD)),
    STONE(3, 5, of(Elements.STONE)),
    ENEMY_HEAD(6, 0, of(enemyHead));

    int priority;
    int reward;
    Set<Elements> elements;

    PathPointPriority(int priority, int reward, Set<Elements> elements) {
        this.priority = priority;
        this.reward = reward;
        this.elements = elements;
    }

    public static PathPointPriority getByElementType(Elements element) {
        return Arrays.stream(values())
                .filter(p -> p.elements != null && p.elements.contains(element))
                .findAny()
                .orElse(EMPTY_OR_OBSTACLE);
    }

    public static boolean isPriorityHigher(Elements current, Elements previous) {
        return getByElementType(current).priority > getByElementType(previous).priority;
    }

    public static boolean isPriorityEquals(Elements current, Elements previous) {
        return getByElementType(current).priority == getByElementType(previous).priority;
    }

    public static boolean checkPriorityHigher(PathFinderResult current, PathFinderResult previous) {
        return previous == null
                || isPriorityHigher(current.getTarget().getElementType(), previous.getTarget().getElementType());
    }

    public static boolean checkPriorityEquals(PathFinderResult current, PathFinderResult previous) {
        return previous != null
                && isPriorityEquals(current.getTarget().getElementType(), previous.getTarget().getElementType());
    }

    public static Integer getPriority(Elements element) {
        return Arrays.stream(values())
                .filter(p -> p.elements != null && p.elements.contains(element))
                .map(p -> p.priority)
                .findAny()
                .orElse(0);
    }

    public static Integer getReward(Elements element) {
        return Arrays.stream(values())
                .filter(p -> p.elements != null && p.elements.contains(element))
                .map(p -> p.reward)
                .findAny()
                .orElse(0);
    }


}
