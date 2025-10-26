package edu.cwru.sepia.agent.planner;

/**
 * Represents an abstract STRIPS-style action that can be applied to a {@link GameState}.
 *
 * <p>Implementations of this interface encapsulate the preconditions and effects required to
 * manipulate a {@code GameState}. Examples include moving to a resource node, harvesting gold or wood,
 * and depositing resources at the town hall.</p>
 */
public interface StripsAction {

    /**
     * Determines whether this action can be performed in the provided state.
     *
     * @param state the state being evaluated
     * @return {@code true} if the action's preconditions are met; {@code false} otherwise
     */
    boolean arePreconditionsMet(GameState state);

    /**
     * Applies the effects of this action to the provided state and returns the successor state.
     *
     * @param state the state to modify
     * @return the new {@link GameState} resulting from applying this action
     */
    GameState apply(GameState state);
}
