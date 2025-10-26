package edu.cwru.sepia.agent.planner;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

/**
 * A* arama algoritmasını kullanarak en düşük maliyetli planı bulan planlayıcı.
 */
public class AStarPlanner {

    /**
     * Verilen bir başlangıç durumundan hedef durumuna bir plan bulur.
     * @param startState Planlamanın başlayacağı başlangıç durumu.
     * @return Hedefe giden eylemleri içeren bir Stack. Plan bulunamazsa null döner.
     */
    public Stack<StripsAction> findPlan(GameState startState) {

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<GameState> closedSet = new HashSet<>();

        double initialHeuristic = calculateHeuristic(startState);
        AStarNode startNode = new AStarNode(startState, 0, initialHeuristic, null, null);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            GameState currentGameState = currentNode.getGameState();

            if (currentGameState.isGoal()) {
                System.out.println("Hedefe ulaşıldı! Plan oluşturuluyor...");
                return reconstructPlan(currentNode);
            }

            if (closedSet.contains(currentGameState)) {
                continue;
            }
            closedSet.add(currentGameState);

            List<StripsAction> applicableActions = currentGameState.generateApplicableActions();
            for (StripsAction action : applicableActions) {
                GameState successorState = action.apply(currentGameState);

                if (closedSet.contains(successorState)) {
                    continue;
                }

                double tentativeGCost = currentNode.getCostG() + action.getCost();
                double successorHeuristic = calculateHeuristic(successorState);
                AStarNode successorNode = new AStarNode(successorState, tentativeGCost, successorHeuristic, currentNode, action);
                
                openSet.add(successorNode);
            }
        }

        System.out.println("Hedefe ulaşılamadı. Plan bulunamadı.");
        return null;
    }

    /**
     * A* algoritması için sezgisel (heuristic) fonksiyon.
     * Hedefe ulaşmak için gereken minimum eylem sayısını tahmin eder.
     * @param state Değerlendirilecek durum.
     * @return Tahmini maliyet.
     */
    private double calculateHeuristic(GameState state) {
        double goldNeeded = Math.max(0, state.requiredGold - state.collectedGold);
        double woodNeeded = Math.max(0, state.requiredWood - state.collectedWood);

        // Gereken toplama döngüsü sayısı (her döngü 100 birim toplar).
        double goldTrips = Math.ceil(goldNeeded / 100.0);
        double woodTrips = Math.ceil(woodNeeded / 100.0);

        // Her döngü en az bir Harvest (maliyet 1) ve bir Deposit (maliyet 1) eylemi gerektirir.
        // Hareket maliyetlerini dahil etmemek, heuristic'in "admissible" olmasını sağlar.
        double heuristic = (goldTrips + woodTrips) * 2;
        
        // Eğer köylü bir şey taşıyorsa, bir sonraki eylemi muhtemelen Deposit'tir.
        // Bu durumu da heuristic'e yansıtarak daha iyi bir tahmin yapabiliriz.
        if (state.peasantIsCarrying) {
             // Deposit eyleminin maliyetini (1) çıkarıp, belediye binasına hareket maliyetini ekleyebiliriz.
             // Şimdilik basit tutalım: Elinde bir şey varken 1 maliyet daha az gibi düşünebiliriz.
             heuristic -= 1;
        }

        return Math.max(0, heuristic);
    }

    /**
     * Hedef düğüme ulaşıldığında, ebeveynleri takip ederek
     * başlangıçtan hedefe giden planı oluşturur.
     * @param goalNode Hedef düğüm.
     * @return Eylemleri içeren bir Stack.
     */
    private Stack<StripsAction> reconstructPlan(AStarNode goalNode) {
        Stack<StripsAction> plan = new Stack<>();
        AStarNode currentNode = goalNode;
        while (currentNode != null && currentNode.getAction() != null) {
            plan.push(currentNode.getAction());
            currentNode = currentNode.getParent();
        }
        return plan;
    }
}