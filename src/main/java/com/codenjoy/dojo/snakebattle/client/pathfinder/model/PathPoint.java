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

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static java.lang.Integer.compare;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
public class PathPoint implements Comparable<PathPoint> {

    @EqualsAndHashCode.Include
    private int x;
    @EqualsAndHashCode.Include
    private int y;

    private int g; // distance from start
    private int h; // estimated distance to target ignoring obstacles
    private int f; // total movement cost

    private Elements elementType;
    private PathPointPriority pathPointPriority; // 0 - empty or obstacle, 1 - flying pill, 2 - fury pill, 3 - apple, 4 - gold

    private List<PathPoint> parent;

    @Override
    public int compareTo(PathPoint o) {
        return compare(this.f, o.f);
    }
}
