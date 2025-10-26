package edu.cwru.sepia.agent.planner;

/**
 * DepositAction, bir köylünün taşıdığı kaynağı belediye binasına
 * teslim etmesini temsil eden STRIPS eylemidir.
 */
public class DepositAction implements StripsAction {

    @Override
    public boolean arePreconditionsMet(GameState state) {
        // Önkoşullar:
        // 1. Köylü, belediye binasının yanında olmalı.
        // 2. Köylünün elinde bir şey olmalı.
        return state.peasantX == GameState.TOWN_HALL_X &&
               state.peasantY == GameState.TOWN_HALL_Y &&
               state.peasantIsCarrying;
    }

    @Override
    public GameState apply(GameState state) {
        // Yeni bir durum kopyası oluştur.
        GameState newGameState = new GameState(state);

        // Etkiler:
        // 1. Köylünün elleri artık boş.
        newGameState.peasantIsCarrying = false;
        
        // 2. Taşıdığı kaynağa göre toplam kaynak miktarını 100 artır.
        if (newGameState.peasantCarriesGold) {
            newGameState.collectedGold += 100;
        } else {
            newGameState.collectedWood += 100;
        }

        return newGameState;
    }

    @Override
    public double getCost() {
        // Bu eylem birim zaman alır.
        return 1.0;
    }
}