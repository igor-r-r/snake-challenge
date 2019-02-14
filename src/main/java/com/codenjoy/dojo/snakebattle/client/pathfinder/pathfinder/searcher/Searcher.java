package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher;

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

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.childrenDirections;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static java.util.Collections.singleton;

public abstract class Searcher {
    public abstract Optional<PathFinderResult> findSinglePath(PathPoint target);

    protected List<PathPoint> generateChildren(PathPoint parent, PathPoint target) {
        List<PathPoint> children = new ArrayList<>();

        for (int[] direction : childrenDirections) {

            int childX = parent.getX() + direction[0];
            int childY = parent.getY() + direction[1];

            if (!canPassThrough(childX, childY)) {
                continue;
            }

            int g = parent.getG() + 1;
            int h = calculateEstimatedDistance(childX, childY, target.getX(), target.getY());
            int f = g + h;
            Elements elementType = world.getBoard().getAt(childX, childY);

            PathPoint child = PathPoint.builder()
                    .x(childX)
                    .y(childY)
                    .g(g)
                    .h(g)
                    .f(f)
                    .parent(new ArrayList<>(singleton(parent)))
                    .elementType(elementType)
                    .build();

            children.add(child);
        }

        return children;
    }
}
