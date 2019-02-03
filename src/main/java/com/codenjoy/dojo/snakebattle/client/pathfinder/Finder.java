package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;

public interface Finder {
    PathFinderResult findSinglePath(PathPoint target);
}
