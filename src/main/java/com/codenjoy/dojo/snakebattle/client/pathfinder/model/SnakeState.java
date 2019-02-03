package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Arrays;
import java.util.Set;

import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_SLEEP;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_DEAD;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_EVIL;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_FLY;
import static com.codenjoy.dojo.snakebattle.model.Elements.HEAD_SLEEP;

public enum SnakeState {
    NORMAL(),
    FURY(HEAD_EVIL, ENEMY_HEAD_EVIL),
    FLIGHT(HEAD_FLY, ENEMY_HEAD_FLY),
    SLEEP(HEAD_SLEEP, ENEMY_HEAD_SLEEP),
    DEAD(HEAD_DEAD, ENEMY_HEAD_DEAD);
    //HAS_STONE;

    Set<SnakeState> incompatibleStates;
    Set<Elements> elements;

    SnakeState(Elements... elements) {
        this.elements = Set.of(elements);
    }

    private void setRules() {
        //NORMAL.setIncompatibleStates(FURY, FLIGHT);
        //FURY.setIncompatibleStates(NORMAL, FLIGHT);
        //FLIGHT.setIncompatibleStates(NORMAL, FLIGHT);
    }

    public static SnakeState getStateByElement(Elements element) {
        return Arrays.stream(values())
                .filter(p -> p.elements.contains(element))
                .findFirst()
                .orElse(NORMAL);
    }

    public void setIncompatibleStates(SnakeState... incompatibleStates) {
        this.incompatibleStates = Set.of(incompatibleStates);
    }


}
