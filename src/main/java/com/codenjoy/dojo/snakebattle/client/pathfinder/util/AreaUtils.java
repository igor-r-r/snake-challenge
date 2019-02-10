package com.codenjoy.dojo.snakebattle.client.pathfinder.util;

import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Area;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.AreaCoordinates;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Enemy;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPoint;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Reward;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.Snake;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.ENEMY_HEAD;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.getByElementType;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.getReward;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.RewardUtils.calculateAreaWeight;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.RewardUtils.calculateEnemyReward;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.RewardUtils.calculateNonEnemyReward;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.RewardUtils.calculatePossibleReward;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.RewardUtils.calculateTotalReward;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class AreaUtils {

    public static List<AreaCoordinates> constantAreaCoordinates;

    public static final int AREA_SIZE = 5;
    public static final int BOARD_SIZE = 30;
    public static final int BOARD_LOWER_BOUNDARY = 0;
    public static final int BOARD_UPPER_BOUNDARY = 29;

    private static int[] UP_COORDS = {0, AREA_SIZE};
    private static int[] DOWN_COORDS = {0, -AREA_SIZE};
    private static int[] RIGHT_COORDS = {AREA_SIZE, 0};
    private static int[] LEFT_COORDS = {-AREA_SIZE, 0};

    public static Set<int[]> coordinatesSet = new HashSet<>();

    static {
        coordinatesSet.add(UP_COORDS);
        coordinatesSet.add(DOWN_COORDS);
        coordinatesSet.add(RIGHT_COORDS);
        coordinatesSet.add(LEFT_COORDS);
    }

    static {
        List<AreaCoordinates> areaCoordinates = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE / AREA_SIZE; i++) {
            for (int j= 0; j < BOARD_SIZE / AREA_SIZE; j++) {
                int x = i * AREA_SIZE;
                int y = j * AREA_SIZE;

                areaCoordinates.add(new AreaCoordinates(x, y));
            }
        }

        constantAreaCoordinates = areaCoordinates;
    }

    public static Map<AreaCoordinates, Area> buildConstantAreas() {
        return buildAreas(constantAreaCoordinates);
    }

    public static Map<AreaCoordinates, Area> buildAreas(PathPoint head) {
        List<AreaCoordinates> areaCoordinates = new ArrayList<>();
        getChildrenCoordinatesRecursive(new AreaCoordinates(head.getX() - 2, head.getY() - 2), areaCoordinates);

        return buildAreas(areaCoordinates);
    }

    public static Map<AreaCoordinates, Area> buildAreas(List<AreaCoordinates> areaCoordinates) {
        Snake mySnake = world.getMySnake();
        return areaCoordinates.stream()
                .map(c -> buildArea(mySnake.getHead().getX(), mySnake.getHead().getY(), c))
                .collect(toMap(Area::getCoordinates, Function.identity()));
    }

    private static void getChildrenCoordinatesRecursive(AreaCoordinates current, List<AreaCoordinates> visited) {
        visited.add(current);

        for (AreaCoordinates c : generateChildrenCoordinates(current)) {
            if (!visited.contains(c) && !isAreaOutOfBounds(c)) {
                getChildrenCoordinatesRecursive(c, visited);
            }
        }
    }

    private static boolean isAreaOutOfBounds(AreaCoordinates coordinates) {
        return coordinates.getLeftX() <= BOARD_LOWER_BOUNDARY - AREA_SIZE
                || coordinates.getRightX() > BOARD_UPPER_BOUNDARY + AREA_SIZE
                || coordinates.getBottomY() <= BOARD_LOWER_BOUNDARY - AREA_SIZE
                || coordinates.getTopY() > BOARD_UPPER_BOUNDARY + AREA_SIZE;
    }

    private static Set<AreaCoordinates> generateChildrenCoordinates(AreaCoordinates parent) {
        return coordinatesSet.stream()
                .map(i -> new AreaCoordinates(
                        i[0] + parent.getLeftX(),
                        i[1] + parent.getBottomY(),
                        i[0] + parent.getRightX(),
                        i[1] + parent.getTopY()))
                .collect(Collectors.toSet());
    }

    public static Area buildArea(int headX, int headY, AreaCoordinates coordinates) {
        return buildArea(headX, headY, coordinates.getLeftX(), coordinates.getBottomY(), coordinates.getRightX(), coordinates.getTopY());
    }

    public static Area buildArea(int headX, int headY, int leftX, int bottomY, int rightX, int topY) {
        int distance = PathFinderUtils.calculateEstimatedDistance(headX, headY, leftX + 2, bottomY + 2);

        AreaCoordinates coordinates = AreaCoordinates.builder()
                .leftX(limit(leftX))
                .rightX(limit(rightX))
                .bottomY(limit(bottomY))
                .topY(limit(topY))
                .build();

        List<PathPoint> valuablePathPoints = getAreaValuables(coordinates);

        Area area = Area.builder()
                .coordinates(coordinates)
                .distance(distance)
                .valuables(valuablePathPoints)
                .enemies(getAreaEnemies(coordinates))
                .build();

        area.setWeight(calculateAreaWeight(area));

        Reward reward = new Reward();
        reward.setPossibleReward(calculatePossibleReward(area));
        reward.setTotalReward(calculateTotalReward(area));
        reward.setNonEnemyReward(calculateNonEnemyReward(area));
        reward.setEnemyReward(calculateEnemyReward(area));

        area.setReward(reward);

        return area;
    }

    public static List<PathPoint> getAreaValuables(AreaCoordinates coordinates) {
        return world.getValuablePathPoints().stream()
                .filter(p -> isInsideArea(p.getX(), p.getY(), coordinates))
                .collect(toList());
    }

    public static List<Enemy> getAreaEnemies(AreaCoordinates coordinates) {
        return world.getEnemies().stream().filter(e -> isInsideArea(e.getHead().getX(), e.getHead().getY(), coordinates)).collect(Collectors.toList());
    }


    private static int limit(int coordinate) {
        if (coordinate < BOARD_LOWER_BOUNDARY) {
            return BOARD_LOWER_BOUNDARY;
        } else if (coordinate > BOARD_UPPER_BOUNDARY) {
            return BOARD_UPPER_BOUNDARY;
        } else {
            return coordinate;
        }
    }



    public static boolean isInsideArea(int targetX, int targetY, AreaCoordinates areaCoordinates) {
        return isBetween(targetX, areaCoordinates.getLeftX(), areaCoordinates.getRightX())
                && isBetween(targetY, areaCoordinates.getBottomY(), areaCoordinates.getTopY());

    }

    public static boolean isBetween(int target, int start, int end) {
        return target >= start && target < end;
    }

}
