package edu.cwru.sepia.agent.planner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Implements the A* search algorithm to construct a STRIPS plan from an initial {@link GameState}.
 */
public class AStarPlanner {

    /**
     * Attempts to find a plan that reaches a goal state starting from {@code startState}.
     *
     * @param startState initial game state
     * @return a queue of {@link StripsAction} instances representing the plan; {@code null} if no plan is found
     */
    public Queue<StripsAction> findPlan(GameState startState) {
        // TODO: Validate start state input.

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<GameState> closedSet = new HashSet<>();

        double initialHeuristic = calculateHeuristic(startState);
        openSet.add(new AStarNode(startState, 0.0, initialHeuristic, null, null));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();

            if (current.getState().isGoal()) {
                return reconstructPlan(current);
            }

            if (!closedSet.add(current.getState())) {
                continue;
            }

            // TODO: Expand successor states by applying applicable STRIPS actions.
        }

        // TODO: Handle failure to find a plan (e.g., return empty queue or throw exception).
        return null;
    }

    /**
     * Reconstructs the sequence of actions from the goal node back to the start node.
     *
     * @param goalNode the node representing a goal state
     * @return queue of actions ordered from start to goal
     */
    private Queue<StripsAction> reconstructPlan(AStarNode goalNode) {
        LinkedList<StripsAction> actions = new LinkedList<>();
        AStarNode current = goalNode;
        while (current != null && current.getGeneratingAction() != null) {
            actions.addFirst(current.getGeneratingAction());
            current = current.getParent();
        }
        return actions;
    }

    /**
     * Estimates the cost to reach the goal from the provided state.
     *
     * @param state state to evaluate
     * @return heuristic estimate of the remaining cost
     */
    protected double calculateHeuristic(GameState state) {
        // TODO: Design an admissible heuristic tailored to the resource collection problem.
        return 0.0;
    }
}
