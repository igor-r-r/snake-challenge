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

import com.codenjoy.dojo.services.Direction;

import lombok.Data;

@Data
public class Snake {

    SnakeState state = SnakeState.NORMAL;
    protected boolean hasStone;
    protected int stoneCount;
    protected int length;
    protected int furyCounter;
    protected PathPoint previousHead;
    protected PathPoint head;
    protected Direction direction;

    public int changeStoneCount(int delta) {
        int newCount = stoneCount + delta;
        return stoneCount = newCount > 0 ? newCount : 0;
    }

    public boolean isFury() {
        return SnakeState.FURY.equals(state);
    }

}
