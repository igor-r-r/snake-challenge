package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.ConstantAreaAwarePathFinder;
import com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.searcher.AStar;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static org.mockito.Mockito.mock;

public class SearcherTest extends BaseTest {

    @Before
    public void setup() {
        pathFinder = new ConstantAreaAwarePathFinder(new AStar());
        dice = mock(Dice.class);
    }

    @Test
    public void shouldFindBestPathToPrioritizedTarget() {
        Board board = board(
                  "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼"
                + "☼☼                           ☼"
                + "☼#                           ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼☼     ☼☼☼☼☼                 ☼"
                + "☼☼     ☼                     ☼"
                + "☼#     ☼☼☼        ☼☼☼☼#      ☼"
                + "☼☼     ☼          ☼   ☼      ☼"
                + "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼"
                + "☼☼  $ ○  ○        ☼          ☼"
                + "☼☼    ○           ☼          ☼"
                + "☼☼       ╔►   ˄              ☼"
                + "☼#       ║    │              ☼"
                + "☼☼       ║    │              ☼"
                + "☼☼      ╘╝ ☼☼×┘              ☼"
                + "☼☼       ☼  ☼                ☼"
                + "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼"
                + "☼☼      ☼   ☼     ☼ ☼ ☼ ☼    ☼"
                + "☼#      ☼   ☼     ☼  ☼  ☼    ☼"
                + "☼☼                ☼     ☼    ☼"
                + "☼☼                ☼     ☼    ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼#                           ☼"
                + "☼☼                           ☼"
                + "☼☼                           ☼"
                + "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        long startTime = System.nanoTime();
        world.updateWorldState(board);
        System.out.println("!!!! Update world time: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

        
        
    }

}
