package edu.cwru.sepia.agent.planner;

import java.util.Objects;

/**
 * Encapsulates the information stored for each node in the A* search frontier.
 */
public class AStarNode implements Comparable<AStarNode> {

    /** The game state associated with this node. */
    private final GameState state;

    /** Cost accumulated from the start node to reach this node (g(n)). */
    private final double pathCost;

    /** Heuristic estimate of the cost from this node to the goal (h(n)). */
    private final double heuristicCost;

    /** Parent node in the search tree, used to reconstruct the resulting plan. */
    private final AStarNode parent;

    /** Action applied to the parent to produce this node. */
    private final StripsAction generatingAction;

    /**
     * Constructs a new {@code AStarNode} instance.
     *
     * @param state           the game state associated with this node
     * @param pathCost        accumulated path cost from the start state
     * @param heuristicCost   estimated remaining cost to the goal
     * @param parent          parent node from which this node was generated
     * @param generatingAction action applied to the parent to reach this state
     */
    public AStarNode(GameState state,
                     double pathCost,
                     double heuristicCost,
                     AStarNode parent,
                     StripsAction generatingAction) {
        this.state = state;
        this.pathCost = pathCost;
        this.heuristicCost = heuristicCost;
        this.parent = parent;
        this.generatingAction = generatingAction;
    }

    /**
     * @return the underlying game state represented by this node
     */
    public GameState getState() {
        return state;
    }

    /**
     * @return the path cost (g(n))
     */
    public double getPathCost() {
        return pathCost;
    }

    /**
     * @return the heuristic cost (h(n))
     */
    public double getHeuristicCost() {
        return heuristicCost;
    }

    /**
     * @return the parent node
     */
    public AStarNode getParent() {
        return parent;
    }

    /**
     * @return the action that generated this node
     */
    public StripsAction getGeneratingAction() {
        return generatingAction;
    }

    /**
     * Computes the total estimated cost f(n) = g(n) + h(n).
     *
     * @return the total estimated cost
     */
    public double getTotalCost() {
        return pathCost + heuristicCost;
    }

    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(getTotalCost(), other.getTotalCost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AStarNode)) {
            return false;
        }
        AStarNode that = (AStarNode) o;
        // Nodes are considered equal if they reference the same game state.
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
