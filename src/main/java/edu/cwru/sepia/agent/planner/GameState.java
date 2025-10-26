package edu.cwru.sepia.agent.planner;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single configuration of the resource collection scenario for planning purposes.
 *
 * <p>The {@code GameState} tracks the peasant's location, the amount of resources collected, and the
 * resources remaining in the environment. Instances of this class are immutable to simplify reasoning
 * in the search space.</p>
 */
public class GameState {

    /** Total amount of gold collected so far. */
    private final int collectedGold;

    /** Total amount of wood collected so far. */
    private final int collectedWood;

    /** Mapping of gold resource node identifiers to the amount of gold remaining. */
    private final Map<Integer, Integer> goldNodes;

    /** Mapping of wood resource node identifiers to the amount of wood remaining. */
    private final Map<Integer, Integer> woodNodes;

    /** Position of the peasant in grid coordinates. */
    private final int peasantX;

    /** Position of the peasant in grid coordinates. */
    private final int peasantY;

    /** Current cargo carried by the peasant. */
    private final CargoType cargoType;

    /**
     * Enum describing the possible cargo states for the peasant.
     */
    public enum CargoType {
        NONE,
        GOLD,
        WOOD
    }

    /**
     * Constructs a new {@code GameState}.
     *
     * @param collectedGold total gold collected
     * @param collectedWood total wood collected
     * @param goldNodes map of gold node ids to remaining quantities
     * @param woodNodes map of wood node ids to remaining quantities
     * @param peasantX peasant x-coordinate
     * @param peasantY peasant y-coordinate
     * @param cargoType current cargo carried by the peasant
     */
    public GameState(int collectedGold,
                     int collectedWood,
                     Map<Integer, Integer> goldNodes,
                     Map<Integer, Integer> woodNodes,
                     int peasantX,
                     int peasantY,
                     CargoType cargoType) {
        this.collectedGold = collectedGold;
        this.collectedWood = collectedWood;
        this.goldNodes = goldNodes;
        this.woodNodes = woodNodes;
        this.peasantX = peasantX;
        this.peasantY = peasantY;
        this.cargoType = cargoType;
    }

    /**
     * Generates all STRIPS actions that can be applied in this state.
     *
     * @return list of applicable {@link StripsAction} instances
     */
    public List<StripsAction> getApplicableActions() {
        // TODO: Inspect cargo, location, and resource availability to determine legal actions.
        return null;
    }

    /**
     * Checks whether this state satisfies the planning goal.
     *
     * @return {@code true} if sufficient resources have been collected; {@code false} otherwise
     */
    public boolean isGoal() {
        // TODO: Compare collected resources to the goal thresholds (e.g., 200 gold and 200 wood).
        return false;
    }

    /**
     * @return the total gold collected in this state
     */
    public int getCollectedGold() {
        return collectedGold;
    }

    /**
     * @return the total wood collected in this state
     */
    public int getCollectedWood() {
        return collectedWood;
    }

    /**
     * @return immutable view of remaining gold nodes
     */
    public Map<Integer, Integer> getGoldNodes() {
        return goldNodes;
    }

    /**
     * @return immutable view of remaining wood nodes
     */
    public Map<Integer, Integer> getWoodNodes() {
        return woodNodes;
    }

    /**
     * @return current x-coordinate of the peasant
     */
    public int getPeasantX() {
        return peasantX;
    }

    /**
     * @return current y-coordinate of the peasant
     */
    public int getPeasantY() {
        return peasantY;
    }

    /**
     * @return type of cargo carried by the peasant
     */
    public CargoType getCargoType() {
        return cargoType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameState)) {
            return false;
        }
        GameState gameState = (GameState) o;
        // TODO: Ensure equality covers all relevant features that identify unique states.
        return collectedGold == gameState.collectedGold
                && collectedWood == gameState.collectedWood
                && peasantX == gameState.peasantX
                && peasantY == gameState.peasantY
                && cargoType == gameState.cargoType
                && Objects.equals(goldNodes, gameState.goldNodes)
                && Objects.equals(woodNodes, gameState.woodNodes);
    }

    @Override
    public int hashCode() {
        // TODO: Ensure hash code aligns with equals implementation.
        return Objects.hash(collectedGold, collectedWood, goldNodes, woodNodes, peasantX, peasantY, cargoType);
    }
}
