package com.codenjoy.dojo.snakebattle.client.pathfinder;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Map;
import java.util.Set;

public class EnemyBehavior implements Behavior {

    @Override
    public Map<PathPointPriority, Set<Elements>> getAllowedElements() {

        return Map.of();
    }
}
