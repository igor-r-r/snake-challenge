package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.AStar;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.DirectionProvider;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.EnemyPathFinder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class WorldTest extends BaseTest {

    @Before
    public void setup() {
        pathFinder = new EnemyPathFinder(new AStar(), new DirectionProvider());
        dice = mock(Dice.class);
        ai = new YourSolver(dice, pathFinder);
    }

    @Test
    public void shouldBuildAreas() {
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
                + "☼☼    ●       ˄              ☼"
                + "☼#            │○      ○      ☼"
                + "☼☼            │              ☼"
                + "☼☼        ☼☼☼×┘              ☼"
                + "☼☼       ☼  ☼╔►              ☼"
                + "☼☼    ○ ☼☼☼☼#║    ☼☼   ☼#    ☼"
                + "☼☼      ☼   ☼║  ● ☼ ☼ ☼ ☼ ○  ☼"
                + "☼#      ☼   ☼║    ☼  ☼  ☼    ☼"
                + "☼☼          ╘╝    ☼     ☼    ☼"
                + "☼☼     ●          ☼     ☼    ☼"
                + "☼☼                           ☼"
                + "☼☼                 ©○        ☼"
                + "☼☼             ●         ○   ☼"
                + "☼#                           ☼"
                + "☼☼               ○           ☼"
                + "☼☼                           ☼"
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        world.updateWorldState(board);

        Map<AreaCoordinates, Area> areas = world.getAreas();
        Area areaActual = areas.get(new AreaCoordinates(14, 2));
        assertEquals(8, areaActual.getWeight());

        areaActual = areas.get(new AreaCoordinates(9, 2));
        assertEquals(0, areaActual.getWeight());

        areaActual = areas.get(new AreaCoordinates(4, 7));
        assertEquals(8, areaActual.getWeight());

        System.out.println(areas);


        //assertEquals(ENEMY_HEAD_DOWN, resultActual.getTarget().getElementType());
    }

}
