package edu.cwru.sepia.agent.planner;

/**
 * MoveAction, bir köylünün haritadaki bir (x, y) konumundan
 * hedef bir (x, y) konumuna gitmesini temsil eden STRIPS eylemidir.
 */
public class MoveAction implements StripsAction {

    public int currentX, currentY;
    public int targetX, targetY;

    /**
     * MoveAction oluşturur.
     */
    public MoveAction(int currentX, int currentY, int targetX, int targetY) {
        this.currentX = currentX;
        this.currentY = currentY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public boolean arePreconditionsMet(GameState state) {
        return state.peasantX != targetX || state.peasantY != targetY;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newGameState = new GameState(state);
        newGameState.peasantX = targetX;
        newGameState.peasantY = targetY;
        return newGameState;
    }

    @Override
    public double getCost() {
        return Math.max(Math.abs(targetX - currentX), Math.abs(targetY - currentY));
    }
}