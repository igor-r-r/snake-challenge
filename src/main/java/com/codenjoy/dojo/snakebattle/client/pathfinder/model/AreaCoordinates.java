package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils.AREA_SIZE;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@Builder
public class AreaCoordinates {

    @EqualsAndHashCode.Include
    int leftX;
    @EqualsAndHashCode.Include
    int bottomY;
    int rightX;
    int topY;

    public AreaCoordinates(int leftX, int bottomY) {
        this.leftX = leftX;
        this.bottomY = bottomY;
        this.rightX = leftX + AREA_SIZE;
        this.topY = bottomY + AREA_SIZE;
    }

}
