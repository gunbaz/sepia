package edu.cwru.sepia.agent.planner;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

/**
 * AStarPlanner:
 * - Verilen bir başlangıç GameState'ten başlayarak
 * - isGoal() true yapan bir GameState'e ulaşana kadar
 * - A* araması yapar.
 *
 * Çıkış olarak, hedefe giden StripsAction listesini (Stack halinde) verir.
 */
public class AStarPlanner {

    /**
     * Verilen başlangıç durumundan hedefe giden planı bulur.
     * Başarılıysa aksiyonları içeren bir Stack döner (ilk uygulanacak en altta olacak şekilde).
     * Başarısızsa null döner.
     */
    public Stack<StripsAction> findPlan(GameState startState) {

        // openSet: keşfedilecek düğümler (f = g + h sırasına göre önceliklendirilecek)
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();

        // closedSet: zaten expand edilmiş GameState'ler
        Set<GameState> closedSet = new HashSet<>();

        // Başlangıç düğümü
        double initialHeuristic = calculateHeuristic(startState);
        AStarNode startNode = new AStarNode(startState,
                                            0.0,                 // g(n) başlangıçta 0
                                            initialHeuristic,    // h(n)
                                            null,                // parent yok
                                            null);               // aksiyon yok
        openSet.add(startNode);

        // A* ana döngüsü
        while (!openSet.isEmpty()) {
            // En düşük f = g+h değerli düğümü seç
            AStarNode currentNode = openSet.poll();
            GameState currentState = currentNode.getGameState();

            // Hedefe ulaştık mı?
            if (currentState.isGoal()) {
                // Planı reconstruct edip döndürüyoruz
                return reconstructPlan(currentNode);
            }

            // Bu state'i daha önce expand ettiysek tekrar uğraşma
            if (closedSet.contains(currentState)) {
                continue;
            }
            closedSet.add(currentState);

            // Geçerli durumdan uygulanabilecek aksiyonları al
            List<StripsAction> applicableActions = currentState.generateApplicableActions();

            // Her uygulanabilir aksiyonu dene
            for (StripsAction action : applicableActions) {
                // Preconditions kontrolü (güvenlik için tekrar)
                if (!action.arePreconditionsMet(currentState)) {
                    continue;
                }

                // Aksiyon sonucu yeni bir durum üret
                GameState successorState = action.apply(currentState);

                // Eğer bu durum zaten kapalıysa (expand edildi) atla
                if (closedSet.contains(successorState)) {
                    continue;
                }

                // g'yi (şimdiye kadarki gerçek maliyet) güncelle
                double tentativeG = currentNode.getCostG() + action.getCost();

                // Heuristic hesapla
                double h = calculateHeuristic(successorState);

                // Yeni düğüm oluştur
                AStarNode successorNode = new AStarNode(
                        successorState,
                        tentativeG,
                        h,
                        currentNode,
                        action
                );

                // openSet'e ekle
                openSet.add(successorNode);
            }
        }

        // Open set bitti, hedef bulunamadı
        return null;
    }

    /**
     * Heuristic fonksiyonu:
     *
     * Amaç: Hâlâ toplanması gereken altın/odun miktarı için kaç "toplama turu"
     * (harvest + deposit döngüsü) gerektiğini alt sınır olarak tahmin etmek.
     *
     * Neden güvenli? (admissible)
     * - Hareket maliyetlerini (MoveAction) küçümsemiyoruz, hatta yok sayıyoruz.
     *   Bu A* için iyidir, yani heuristic hedefe olan gerçek maliyeti asla aşmaz.
     *
     * - Her hasat/depolama döngüsünde 100 birim kaynak işleniyor
     *   ve bu en az iki aksiyon demek:
     *     HarvestAction (1 cost)
     *     DepositAction (1 cost)
     *   Yani bir tur ≈ 2 cost.
     */
    private double calculateHeuristic(GameState state) {
        // Hedefe kalan miktarları hesapla
        double goldNeeded = Math.max(0, state.requiredGold - state.collectedGold);
        double woodNeeded = Math.max(0, state.requiredWood - state.collectedWood);

        // Kaç "100-lük paket" daha lazım?
        double goldTrips = Math.ceil(goldNeeded / 100.0);
        double woodTrips = Math.ceil(woodNeeded / 100.0);

        // Her trip kabaca:
        //   HarvestAction (1)
        //   DepositAction (1)
        // => 2 maliyet
        double baseHeuristic = (goldTrips + woodTrips) * 2.0;

        // Ek küçük iyileştirme:
        // Eğer köylü zaten yük taşıyorsa, muhtemelen sıradaki adım Deposit olacak,
        // yani en azından Harvest'i yapmış durumda sayabiliriz. Bu biraz maliyeti düşürür.
        if (state.peasantIsCarrying) {
            // Tamamen aşırı agresif olmayalım, -1 yeter.
            baseHeuristic = baseHeuristic - 1.0;
        }

        // Negatif dönmesin
        if (baseHeuristic < 0) {
            baseHeuristic = 0;
        }

        return baseHeuristic;
    }

    /**
     * Hedef node'a ulaşıldığında, parent zincirini takip edip
     * uygulanmış aksiyonları tersten alırız.
     * Çıktı: plan[0] ilk yapılacak aksiyon olacak şekilde Stack olarak döndürülür.
     */
    private Stack<StripsAction> reconstructPlan(AStarNode goalNode) {
        Stack<StripsAction> reverseStack = new Stack<>();
        AStarNode cursor = goalNode;

        while (cursor != null && cursor.getAction() != null) {
            reverseStack.push(cursor.getAction());
            cursor = cursor.getParent();
        }

        // reverseStack şu anda hedef -> başlangıç yönünde.
        // Stack LIFO olduğu için doğrudan döndürmek senin RCAgent'ındaki kullanım
        // ile uyumluysa değiştirmeye gerek yok.
        //
        // Senin RCAgent şu mantıkla çalışıyordu:
        //   StripsAction nextAction = plan.peek();
        //   ... sonra plan.pop();
        //
        // Bu durumda reverseStack'in en üstündeki aksiyon "ilk uygulanacak" olmalı.
        // Şu an öyle. Çünkü parent'tan çocuğa gelirken push ettik.
        //
        // Yani ekstra ters çevirme gerekmiyor.
        return reverseStack;
    }
}
