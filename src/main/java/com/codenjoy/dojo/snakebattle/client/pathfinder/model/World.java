package com.codenjoy.dojo.snakebattle.client.pathfinder.model;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.Board;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.buildPathPoint;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.calculateEstimatedDistance;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canEatStone;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.getGroup;
import static com.codenjoy.dojo.snakebattle.model.Elements.APPLE;
import static com.codenjoy.dojo.snakebattle.model.Elements.FURY_PILL;
import static com.codenjoy.dojo.snakebattle.model.Elements.GOLD;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
public class World {

    private Board board;

    private List<PathPoint> allPathPoints;
    private List<PathPoint> valuablePathPoints;
    private TreeMap<Integer, List<PathPoint>> pathPointGroups;

    public World(Board board) {
        this.board = board;
    }

    public void updateWorldState(Board board) {
        setBoard(board);
        updateValuablePathPoints();
        updatePathPointGroups();
        updateEnemies();

    }

    public void updateValuablePathPoints() {
        List<PathPoint> pathPoints = new ArrayList<>();

        pathPoints.addAll(toPathPointList(board, APPLE));
        //pathPoints.addAll(toPathPointList(board, FLYING_PILL));
        pathPoints.addAll(toPathPointList(board, FURY_PILL));
        pathPoints.addAll(toPathPointList(board, GOLD));
        if (canEatStone(board)) {
            pathPoints.addAll(toPathPointList(board, STONE));
        }

        valuablePathPoints = pathPoints;
    }

    public List<PathPoint> toPathPointList(Board board, Elements elementType) {
        return board.get(elementType).stream()
                .map(p -> buildPathPoint(p, elementType))
                .collect(Collectors.toList());
    }

    private void updatePathPointGroups() {
        TreeMap<Integer, List<PathPoint>> groupMap = new TreeMap<>();
        Point me = board.getMe();

        for (PathPoint pathPoint : valuablePathPoints) {
            int group = getGroup(
                    calculateEstimatedDistance(me.getX(), me.getY(), pathPoint.getX(), pathPoint.getY()));
            if (groupMap.containsKey(group)) {
                groupMap.get(group).add(pathPoint);
            } else {
                groupMap.put(group, new ArrayList<>(asList(pathPoint)));
            }
        }

        pathPointGroups = groupMap;
    }

    public void updateEnemies() {

    }


}
