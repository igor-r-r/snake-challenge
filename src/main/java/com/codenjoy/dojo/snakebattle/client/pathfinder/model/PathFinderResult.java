package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.model.Elements;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PathFinderResult {

    private boolean reachable;
    private int distance;
    private PathPoint nextPoint;
    private Direction direction;
    private Elements targetElementType;

}
