package edu.cwru.sepia.agent.planner;

/**
 * A* aramasında her bir durumu (GameState) temsil eden düğüm sınıfı.
 * Bu düğüm:
 *  - gameState: Bu düğümün gösterdiği planlama durumu
 *  - costG: Başlangıçtan buraya kadar biriken gerçek maliyet (g(n))
 *  - heuristicH: Buradan hedefe olan tahmini maliyet (h(n))
 *  - parent: Bu düğüme gelmeden önceki düğüm
 *  - action: parent -> bu düğüm geçişinde kullanılan aksiyon
 *
 * PriorityQueue içinde compareTo ile f(n)=g+h küçük olan önce çıkar.
 */
public class AStarNode implements Comparable<AStarNode> {

    private final GameState gameState;
    private final double costG;
    private final double heuristicH;
    private final AStarNode parent;
    private final StripsAction action;

    /**
     * Yeni bir AStarNode oluştur.
     *
     * @param gameState   Bu düğümün temsil ettiği durum
     * @param costG       g(n): başlangıçtan buraya olan gerçek maliyet
     * @param heuristicH  h(n): buradan hedefe tahmini maliyet
     * @param parent      önceki düğüm (null olabilir, başlangıç düğümünde)
     * @param action      parent'tan buraya gelirken yapılan aksiyon (başlangıçta null)
     */
    public AStarNode(GameState gameState,
                     double costG,
                     double heuristicH,
                     AStarNode parent,
                     StripsAction action) {
        this.gameState = gameState;
        this.costG = costG;
        this.heuristicH = heuristicH;
        this.parent = parent;
        this.action = action;
    }

    /**
     * f(n) = g(n) + h(n)
     */
    public double getTotalCostF() {
        return costG + heuristicH;
    }

    /**
     * g(n)
     */
    public double getCostG() {
        return costG;
    }

    /**
     * Bu düğümün durumunu döndür.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Çözümü reconstruct ederken zincir takip için kullanıyoruz.
     */
    public AStarNode getParent() {
        return parent;
    }

    /**
     * parent -> bu düğüme gelirken kullanılan aksiyon.
     * reconstruct sırasında planı geri çıkarmak için kullanıyoruz.
     */
    public StripsAction getAction() {
        return action;
    }

    /**
     * PriorityQueue en düşük f(n) değerini önce alabilsin diye
     * compareTo toplam f(n) karşılaştırır.
     */
    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.getTotalCostF(), other.getTotalCostF());
    }
}
