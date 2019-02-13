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

import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_SLEEP;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_SLEEP;

public enum SnakeState {
    NORMAL(),
    FURY(HEAD_EVIL, ENEMY_HEAD_EVIL),
    FLIGHT(HEAD_FLY, ENEMY_HEAD_FLY),
    SLEEP(HEAD_SLEEP, ENEMY_HEAD_SLEEP),
    DEAD(HEAD_DEAD, ENEMY_HEAD_DEAD);
    //HAS_STONE;

    Set<SnakeState> incompatibleStates;
    Set<Elements> elements;

    SnakeState(Elements... elements) {
        this.elements = Set.of(elements);
    }

    public static SnakeState getStateByElement(Elements element) {
        return Arrays.stream(values())
                .filter(p -> p.elements.contains(element))
                .findFirst()
                .orElse(NORMAL);
    }

    public void setIncompatibleStates(SnakeState... incompatibleStates) {
        this.incompatibleStates = Set.of(incompatibleStates);
    }



}
