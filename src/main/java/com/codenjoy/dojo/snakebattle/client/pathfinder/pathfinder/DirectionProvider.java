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

import org.springframework.stereotype.Component;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.buildDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getOppositeDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.isMySnakePart;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;

@Component
public class DirectionProvider {

    public String anyDirection() {
        Point me = world.getBoard().getMe();

        for (int[] direction : childrenDirections) {
            if (canPassThrough(me.getX() + direction[0], me.getY() + direction[1])) {
                return getDirection(direction).toString();
            }
        }

        for (int[] direction : childrenDirections) {
            Elements element = world.getBoard().getAt(me.getX() + direction[0], me.getY() + direction[1]);

            if (isMySnakePart(element)) {
                return getDirection(direction).toString();
            }
        }

        return ACT(0);
    }

    @Deprecated
    public String furyDirection() {
        if (world.getMySnake().isFury()
                && world.getMySnake().getFuryCounter() < 9
                && world.getMySnake().getFuryCounter() > 0) {
            Direction[] turnAroundDirection = getOppositeDirection(world.getMySnake().getDirection());
            System.out.println("Fury: counter: " + world.getMySnake().getFuryCounter() + ", stones: " + world.getMySnake().getStoneCount());
            return buildDirection(turnAroundDirection[0], turnAroundDirection[1], ACT);
        }

        return anyDirection();
    }

    public String getFinalDirectionString(PathFinderResult result) {
        System.out.println("Final result: " + result);
        if (result != null) {
            if (result.getDirection().equals(ACT) || result.getNextPoint() == null) {
                return anyDirection();
            }

            if (STONE.equals(result.getNextPoint().getElementType())) {
                world.getMySnake().changeStoneCount(1);
            }

            if (STONE.equals(result.getNextPoint().getElementType())) {
                world.getMySnake().setFuryCounter(1);
            }

            if (!canPassThrough(result.getNextPoint())) {
                return Direction.ACT(0);
            }

            if (world.getMySnake().isFury()) {
                return buildDirection(result.getDirection(), ACT);
            }

            return result.getDirection().toString();
        } else {
            return anyDirection();
        }
    }
}
