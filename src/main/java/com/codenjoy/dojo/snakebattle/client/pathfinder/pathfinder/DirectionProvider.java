package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

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
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.model.Elements;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.buildAct;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getCloseDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.shouldDropStone;

public class DirectionProvider {

    public String getFinalDirectionString(PathFinderResult result) {
        if (result != null) {
            if (result.getDirection().equals(ACT) || result.getNextPoint() == null) {
                return anyDirection().toString();
            }

            if (world.getBoard().getAt(result.getNextPoint().getX(), result.getNextPoint().getY()).equals(Elements.STONE)) {
                world.getMySnake().changeStoneCount(1);
            }

            if (shouldDropStone(result.getNextPoint().getX(), result.getNextPoint().getY())) {
                world.getMySnake().changeStoneCount(-1);
                return buildAct(result.getDirection(), false);
            }
            return result.getDirection().toString();
        } else {
            return anyDirection().toString();
        }
    }

    public Direction anyDirection() {
        Point me = world.getBoard().getMe();

        for (int[] direction : childrenDirections) {
            if (canPassThrough(me.getX() + direction[0], me.getY() + direction[1])) {
                return getCloseDirection(me.getX(), me.getY(), me.getX() + direction[0], me.getY() + direction[1]);
            }
        }

        return Direction.RIGHT;
    }

}
