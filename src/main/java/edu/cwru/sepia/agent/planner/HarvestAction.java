package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.GameState.ResourceInfo;

/**
 * HarvestAction:
 * Köylü bulunduğu kaynaktan (maden ya da orman) 100 birim toplar
 * ve eline alır.
 *
 * Etkiler:
 * - peasantIsCarrying = true
 * - peasantCarriesGold (kaynağa göre)
 * - kaynağın amountRemaining -= 100
 *
 * Not: Burada sadece 100'lük paketlerle çalışıyoruz.
 */
public class HarvestAction implements StripsAction {

    public final ResourceInfo resource; // hedef kaynak düğümü

    public HarvestAction(ResourceInfo resource) {
        this.resource = resource;
    }

    @Override
    public boolean arePreconditionsMet(GameState state) {
        // Önkoşullar:
        // 1. Köylü bu kaynağın üstünde olmalı
        // 2. Köylünün elleri boş olmalı (aynı anda iki şey taşıyamıyor)
        // 3. Bu kaynaktan en az 100 birim kalmış olmalı
        return state.peasantX == resource.x &&
               state.peasantY == resource.y &&
               !state.peasantIsCarrying &&
               resource.amountRemaining >= 100;
    }

    @Override
    public GameState apply(GameState state) {
        GameState newState = new GameState(state);

        // Köylü artık bir şey taşıyor
        newState.peasantIsCarrying = true;

        // Neyi taşıyor? Altın mı odun mu?
        newState.peasantCarriesGold = resource.isGoldMine;

        // Kaynağın stoğunu azalt
        for (GameState.ResourceInfo r : newState.resources) {
            if (r.x == resource.x &&
                r.y == resource.y &&
                r.resourceID == resource.resourceID) {
                r.amountRemaining -= 100;
                break;
            }
        }

        return newState;
    }

    @Override
    public double getCost() {
        // Hasat tek zaman adımı (sabit 1)
        return 1.0;
    }
}
