package com.codenjoy.dojo.snakebattle.client.pathfinder.world;

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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.List;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;

public class WorldBuildHelper {

    public static List<PathPoint> toPathPointList(Elements... elements) {
        return  world.getBoard().get(elements).stream()
                .map(p -> buildPathPoint(p.getX(), p.getY(), world.getBoard().getAt(p)))
                .collect(Collectors.toList());
    }

    public static PathPoint buildPathPoint(Point me) {
        return buildPathPoint(me.getX(), me.getY());
    }

    public static PathPoint buildPathPoint(int x, int y) {
        Elements headElement = world.getBoard().getAt(x, y);

        return buildPathPoint(x, y, headElement);
    }

    public static PathPoint buildPathPoint(int x, int y, Elements elementType) {
        return PathPoint.builder()
                .x(x)
                .y(y)
                .elementType(elementType)
                .pathPointPriority(PathPointPriority.getByElementType(elementType))
                .build();
    }

}
