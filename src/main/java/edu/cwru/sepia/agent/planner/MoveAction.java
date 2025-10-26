package edu.cwru.sepia.agent.planner;

/**
 * MoveAction:
 * Köylünün (peasant) bulunduğu (currentX,currentY) konumundan
 * hedef (targetX,targetY) konumuna "gitmesini" soyutlar.
 *
 * Bu aksiyon state içinde sadece köylünün koordinatlarını günceller.
 * Hareket süresinin cost'u olarak Chebyshev distance (max(dx,dy)) kullanıyoruz.
 * Bu, SEPIA’daki diagonal hareket modeline daha yakın bir alt bound’dur.
 */
public class MoveAction implements StripsAction {

    public final int startX;
    public final int startY;
    public final int targetX;
    public final int targetY;

    public MoveAction(int startX, int startY, int targetX, int targetY) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public boolean arePreconditionsMet(GameState state) {
        // Mantık:
        // - Zaten hedefteysen bu hareket gereksiz.
        // - Aksi halde yapılabilir.
        return !(state.peasantX == targetX && state.peasantY == targetY);
    }

    @Override
    public GameState apply(GameState state) {
        // Yeni bir kopya oluşturuyoruz, orijinali bozmuyoruz.
        GameState newState = new GameState(state);

        // Köylüyü yeni koordinata "ışınla" (planlama soyutlaması).
        newState.peasantX = targetX;
        newState.peasantY = targetY;

        return newState;
    }

    @Override
    public double getCost() {
        int dx = Math.abs(targetX - startX);
        int dy = Math.abs(targetY - startY);
        // Chebyshev distance = max(dx,dy)
        return Math.max(dx, dy);
    }
}
