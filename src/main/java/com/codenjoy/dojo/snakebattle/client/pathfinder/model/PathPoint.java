package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import com.codenjoy.dojo.snakebattle.model.Elements;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    //private boolean closedListed;
    //private boolean openListed;

    private PathPoint parent;

    @Override
    public int compareTo(PathPoint o) {
        return Integer.compare(this.f, o.f);
    }
}
