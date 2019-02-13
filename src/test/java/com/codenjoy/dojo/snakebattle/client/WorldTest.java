package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.EnemyPathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.AStar;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class WorldTest extends BaseTest {

    @Before
    public void setup() {
        pathFinder = new EnemyPathFinder(new AStar());
        dice = mock(Dice.class);
    }

    @Test
    @Ignore
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
        System.out.println(world.getBoard().getMe());

        Map<AreaCoordinates, Area> areas = world.getAreas();

        Area areaActual = areas.get(new AreaCoordinates(0, 3));
        assertEquals(0, areaActual.getWeight());

        areaActual = areas.get(new AreaCoordinates(8, 23));
        assertEquals(10, areaActual.getWeight());

        areaActual = areas.get(new AreaCoordinates(13, 23));
        assertEquals(5, areaActual.getWeight());
    }


    @Test
    public void shouldCalculateAreasRewardsWhenCanNotEatStone() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                        + "☼☼         ○                 ☼"
                        + "☼#                        ×> ☼"
                        + "☼☼  ○    ●         ○         ☼"
                        + "☼☼                      ○    ☼"
                        + "☼☼ ○         ●               ☼"
                        + "☼☼     ☼☼☼☼☼●                ☼"
                        + "☼☼     ☼         $           ☼"
                        + "☼#     ☼☼☼        ☼☼☼☼#      ☼"
                        + "☼☼     ☼         ○☼○● ☼  ●   ☼"
                        + "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼"
                        + "☼☼                ☼          ☼"
                        + "☼☼○               ☼         $☼"
                        + "☼☼            ˄              ☼"
                        + "☼#            │○      ○      ☼"
                        + "☼☼       ╔►   │              ☼"
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
                        + "☼☼  ○            ○           ☼"
                        + "☼☼$●                         ☼"
                        + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        long startTime = System.nanoTime();
        world.updateWorldState(board);
        System.out.println("!!!! Update world time: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

        Map<AreaCoordinates, Area> areas = world.getConstantAreas();
        AreaCoordinates areaCoordinates = new AreaCoordinates(0, 0);
        Area areaActual = areas.get(areaCoordinates);

        assertNotNull(areaActual);
        assertEquals(1 + 5 + 10, areaActual.getReward().getNonEnemyReward());
        assertEquals(1 + 10, areaActual.getReward().getPossibleReward());
        assertEquals(0, areaActual.getReward().getEnemyReward());

        areaCoordinates = new AreaCoordinates(15, 20);
        areaActual = areas.get(areaCoordinates);

        assertNotNull(areaActual);
        assertEquals(1 + 1 + 10, areaActual.getReward().getNonEnemyReward());
        assertEquals(1 + 10, areaActual.getReward().getPossibleReward());
        assertEquals(0, areaActual.getReward().getEnemyReward());

        System.out.println(areas.values().stream().map(area -> Integer.toString(area.getReward().getPossibleReward())).collect(Collectors.joining(", ")));
        System.out.println(areas.values().stream().map(area -> Integer.toString(area.getReward().getNonEnemyReward())).collect(Collectors.joining(", ")));

    }

    @Test
    public void shouldCalculateAreasRewardsWhenCanEatStone() {
        Board board = board(
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                        + "☼☼         ○                 ☼"
                        + "☼#                        ×> ☼"
                        + "☼☼  ○    ●         ○         ☼"
                        + "☼☼                      ○    ☼"
                        + "☼☼ ○         ●               ☼"
                        + "☼☼     ☼☼☼☼☼●                ☼"
                        + "☼☼     ☼         $           ☼"
                        + "☼#     ☼☼☼        ☼☼☼☼#      ☼"
                        + "☼☼     ☼         ○☼○● ☼  ●   ☼"
                        + "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼"
                        + "☼☼                ☼          ☼"
                        + "☼☼○               ☼         $☼"
                        + "☼☼       ╔►   ˄              ☼"
                        + "☼#       ║    │○      ○      ☼"
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
                        + "☼☼  ○            ○           ☼"
                        + "☼☼$●                         ☼"
                        + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        long startTime = System.nanoTime();
        world.updateWorldState(board);
        System.out.println("!!!! Update world time: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

        Map<AreaCoordinates, Area> areas = world.getConstantAreas();
        AreaCoordinates areaCoordinates = new AreaCoordinates(0, 0);
        Area areaActual = areas.get(areaCoordinates);

        assertNotNull(areaActual);
        assertEquals(1 + 5 + 10, areaActual.getReward().getNonEnemyReward());
        assertEquals(1 + 5 + 10, areaActual.getReward().getPossibleReward());
        assertEquals(0, areaActual.getReward().getEnemyReward());

        areaCoordinates = new AreaCoordinates(15, 20);
        areaActual = areas.get(areaCoordinates);

        assertNotNull(areaActual);
        assertEquals(1 + 1 + 10, areaActual.getReward().getNonEnemyReward());
        assertEquals(1 + 10, areaActual.getReward().getPossibleReward());
        assertEquals(0, areaActual.getReward().getEnemyReward());

        System.out.println(areas.values().stream().map(area -> Integer.toString(area.getReward().getPossibleReward())).collect(Collectors.joining(", ")));
        System.out.println(areas.values().stream().map(area -> Integer.toString(area.getReward().getNonEnemyReward())).collect(Collectors.joining(", ")));
    }
}
