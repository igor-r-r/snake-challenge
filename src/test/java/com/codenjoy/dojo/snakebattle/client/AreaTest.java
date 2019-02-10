package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.ConstantAreaAwarePathFinder;

import org.junit.Assert;
import org.junit.Test;

public class AreaTest extends BaseTest {

    @Test
    public void shouldBuildCoordinates() {
        ConstantAreaAwarePathFinder pathFinder = new ConstantAreaAwarePathFinder();

        Assert.assertEquals(36, pathFinder.findNextResult());
    }

    //@Test

}
