package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.ConstantAreaAwarePathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.util.AreaUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class AreaTest extends BaseTest {

    @Before
    public void setup() {
        pathFinder = new ConstantAreaAwarePathFinder(new AStar());
        dice = mock(Dice.class);
        ai = new YourSolver();
    }

    @Test
    public void shouldGetBestArea() {
        Board board = board(
                  "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                + "☼☼         ○                 ☼"
                + "☼#                        ×> ☼"
                + "☼☼  ○    ●         ○         ☼"
                + "☼☼                      ○    ☼"
                + "☼☼ ○         ●    ○          ☼"
                + "☼☼     ☼☼☼☼☼●                ☼"
                + "☼☼     ☼                     ☼"
                + "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼"
                + "☼☼     ☼          ☼   ☼  ●   ☼"
                + "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼"
                + "☼☼                ☼          ☼"
                + "☼☼○               ☼         $☼"
                + "☼☼            ˄              ☼"
                + "☼#       ╔►   │○      ○      ☼"
                + "☼☼       ║    │              ☼"
                + "☼☼      ╘╝ ☼☼×┘              ☼"
                + "☼☼       ☼  ☼                ☼"
                + "☼☼    ○ ☼☼☼☼#     ☼☼   ☼#    ☼"
                + "☼☼      ☼   ☼   ● ☼ ☼ ☼ ☼ ○  ☼"
                + "☼#      ☼   ☼     ☼  ☼  ☼    ☼"
                + "☼☼                ☼     ☼    ☼"
                + "☼☼     ●          ☼     ☼    ☼"
                + "☼☼                           ☼"
                + "☼☼                 ©○        ☼"
                + "☼☼             ●         ○   ☼"
                + "☼#                           ☼"
                + "☼☼               ○           ☼"
                + "☼☼                           ☼"
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);

        Area area = AreaUtils.getBestArea();

        assertNotNull(area);
        assertEquals(30, area.getWeight());
        assertEquals(9, area.getDistance());

    }

}
