package edu.cwru.sepia.agent.planner;

/**
 * DepositAction:
 * Köylü TownHall üzerinde duruyorsa ve elinde kaynak varsa,
 * bu kaynağı kasaya bırakır.
 *
 * Etkiler:
 * - peasantIsCarrying = false
 * - Eğer altın taşıyorsa collectedGold += 100
 *   değilse collectedWood += 100
 */
public class DepositAction implements StripsAction {

    @Override
    public boolean arePreconditionsMet(GameState state) {
        // 1. Köylü TownHall koordinatında olmalı
        // 2. Köylü bir şey taşıyor olmalı
        return state.peasantX == GameState.TOWN_HALL_X &&
               state.peasantY == GameState.TOWN_HALL_Y &&
               state.peasantIsCarrying;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);

        // Artık eller boş
        newState.peasantIsCarrying = false;

        // Ne taşıyorsa o kaydı kasaya ekle
        if (state.peasantCarriesGold) {
            newState.collectedGold += 100;
        } else {
            newState.collectedWood += 100;
        }

        // Kaynağı bıraktıktan sonra eller boş, taşıdığı şeyin türü resetlenebilir
        // ama peasantCarriesGold bayrağını sıfırlamak zorunda değiliz.
        // Bu bayrak anlamını "şu anda taşıdığı şey altın mıydı?" gibi kullanıyoruz.
        // İstersen güvenli olsun diye false yapabiliriz, ama gerekmiyor.
        // newState.peasantCarriesGold = false;

        return newState;
    }

    @Override
    public double getCost() {
        // Depo etmek tek zaman adımı.
        return 1.0;
    }
}
