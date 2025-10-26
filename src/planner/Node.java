package planner;

import java.util.List;

public class Node implements Comparable<Node> {

    public final State state;
    public final Node parent;
    public final Action actionFromParent; // parent -> this geldiğimiz aksiyon
    public final int gCost; // başlangıçtan buraya kadar maliyet
    public final int hCost; // heuristic tahmin
    public final int fCost; // g + h

    public Node(State state,
                Node parent,
                Action actionFromParent,
                int gCost,
                int hCost) {
        this.state = state;
        this.parent = parent;
        this.actionFromParent = actionFromParent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    // Hedefe ulaştığımızda planı geri yürüyüp çıkartmak için:
    public void buildPlanReversed(List<Action> acc) {
        if (parent == null) {
            return; // root node
        }
        acc.add(actionFromParent);
        parent.buildPlanReversed(acc);
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fCost, other.fCost);
    }
}
