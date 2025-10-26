package edu.cwru.sepia.agent.planner;

/**
 * STRIPS-benzeri aksiyon arayüzü.
 * Her aksiyon:
 *  - Bu durumda yasal mı? (arePreconditionsMet)
 *  - Uygulanırsa yeni durum ne olur? (apply)
 *  - Maliyeti ne? (getCost)
 */
public interface StripsAction {

    /**
     * Bu aksiyon mevcut durumda uygulanabilir mi?
     */
    boolean arePreconditionsMet(GameState state);

    /**
     * Aksiyonun bu duruma uygulanmasıyla ortaya çıkan YENİ durumu döndür.
     * ÖNEMLİ: 'state' parametresini MUTASYONLAMA.
     * Yeni GameState kopyası oluşturup değişiklikleri orada yap.
     */
    GameState apply(GameState state);

    /**
     * Aksiyonun maliyeti.
     * A* araması g(n) += cost ile ilerliyor.
     */
    double getCost();
}
