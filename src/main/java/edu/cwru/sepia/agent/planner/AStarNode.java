package edu.cwru.sepia.agent.planner;

/**
 * A* arama algoritmasında bir düğümü temsil eden yardımcı sınıf.
 * Bir GameState'i ve o duruma ulaşmak için gereken ek bilgileri içerir.
 */
public class AStarNode implements Comparable<AStarNode> {

    private GameState gameState;
    private double costG; // Başlangıçtan bu düğüme olan gerçek maliyet (g(n))
    private double heuristicH; // Bu düğümden hedefe olan tahmini maliyet (h(n))
    private AStarNode parent; // Bu düğüme hangi düğümden gelindiği
    private StripsAction action; // Parent'tan bu düğüme hangi eylemle gelindiği

    /**
     * AStarNode oluşturur.
     * @param gameState Bu düğümün temsil ettiği durum.
     * @param costG Başlangıçtan bu düğüme maliyet.
     * @param heuristicH Bu düğümden hedefe tahmini maliyet.
     * @param parent Bu düğümün ebeveyni.
     * @param action Ebeveynden bu düğüme getiren eylem.
     */
    public AStarNode(GameState gameState, double costG, double heuristicH, AStarNode parent, StripsAction action) {
        this.gameState = gameState;
        this.costG = costG;
        this.heuristicH = heuristicH;
        this.parent = parent;
        this.action = action;
    }

    /**
     * Toplam tahmini maliyeti (f(n) = g(n) + h(n)) döndürür.
     * A* bu değere göre en iyi düğümü seçer.
     * @return Toplam tahmini maliyet.
     */
    public double getTotalCostF() {
        return costG + heuristicH;
    }

    public GameState getGameState() {
        return gameState;
    }

    public double getCostG() {
        return costG;
    }

    public AStarNode getParent() {
        return parent;
    }

    public StripsAction getAction() {
        return action;
    }

    /**
     * Düğümleri f(n) değerine göre karşılaştırır.
     * Bu, PriorityQueue'nin en düşük maliyetli düğümü en üste koymasını sağlar.
     */
    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.getTotalCostF(), other.getTotalCostF());
    }
}
