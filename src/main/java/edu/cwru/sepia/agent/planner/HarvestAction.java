package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.GameState.ResourceInfo;

/**
 * HarvestAction, bir köylünün bitişiğindeki bir kaynak düğümünden
 * 100 birim kaynak toplamasını temsil eden STRIPS eylemidir.
 */
public class HarvestAction implements StripsAction {

    public ResourceInfo resource;

    public HarvestAction(ResourceInfo resource) {
        this.resource = resource;
    }

    @Override
    public boolean arePreconditionsMet(GameState state) {
        return state.peasantX == resource.x &&
               state.peasantY == resource.y &&
               !state.peasantIsCarrying &&
               resource.amountRemaining >= 100;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newGameState = new GameState(state);
        newGameState.peasantIsCarrying = true;
        newGameState.peasantCarriesGold = resource.isGoldMine;

        for (ResourceInfo res : newGameState.resources) {
            if (res.x == this.resource.x && res.y == this.resource.y) {
                res.amountRemaining -= 100;
                break;
            }
        }
        return newGameState;
    }

    @Override
    public double getCost() {
        return 1.0;
    }
}