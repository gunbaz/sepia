package planner;

import java.util.*;

/**
 * Planner:
 *  - forward state-space search
 *  - A* with uniform action cost = 1
 *  - returns a sequential plan (list of Actions)
 */
public class Planner {

    private final int requiredGold;
    private final int requiredWood;

    public Planner(int requiredGold, int requiredWood) {
        this.requiredGold = requiredGold;
        this.requiredWood = requiredWood;
    }

    /**
     * Public entry point:
     * A* aramasını yap, planı (aksiyon listesini sırayla) döndür.
     */
    public List<Action> plan() {

        State start = State.initialState();

        // open set (frontier) = priority queue by f = g+h
        PriorityQueue<Node> open = new PriorityQueue<>();

        // closed set = already visited best g for a given state
        // State -> en iyi gCost
        Map<State, Integer> bestG = new HashMap<>();

        Node startNode = new Node(
                start,
                null,
                null,
                0,
                heuristic(start)
        );

        open.add(startNode);
        bestG.put(start, 0);

        while (!open.isEmpty()) {
            Node current = open.poll();

            // goal check
            if (current.state.isGoal(requiredGold, requiredWood)) {
                // planı reconstruct et
                List<Action> reversed = new ArrayList<>();
                current.buildPlanReversed(reversed);
                // buildPlanReversed parent'tan başlayıp eklediği için aksiyonlar tersten
                Collections.reverse(reversed);
                return reversed;
            }

            // successorlar
            for (Successor succ : generateSuccessors(current.state)) {
                State nextState = succ.nextState;
                int newG = current.gCost + 1; // her adımın cost'u =1
                if (!bestG.containsKey(nextState) || newG < bestG.get(nextState)) {
                    int h = heuristic(nextState);
                    Node nextNode = new Node(
                            nextState,
                            current,
                            succ.action,
                            newG,
                            h
                    );
                    open.add(nextNode);
                    bestG.put(nextState, newG);
                }
            }
        }

        // hiçbir plan bulunamadıysa
        return null;
    }

    /**
     * Heuristic:
     * Basit tahmin: eksik altın kaç tur, eksik odun kaç tur.
     * Her turda max 100 birim taşıyabiliyoruz.
     * Ayrıca taşıdığımız yükü townhall'a götürmemiz gerekiyor ama burada kaba tahmin yeterli.
     */
    private int heuristic(State s) {
        int goldShort = Math.max(0, requiredGold - s.goldInTownhall);
        int woodShort = Math.max(0, requiredWood - s.woodInTownhall);

        // Her seferde 100 getiriyoruz. Kaç "yük" daha lazım?
        int tripsGold = (int) Math.ceil(goldShort / 100.0);
        int tripsWood = (int) Math.ceil(woodShort / 100.0);

        // çok kaba: her trip ~3-4 aksiyon (move to source, harvest, move back, deposit)
        // burada sabit katsayı kullanıyoruz ki A* yönlensin
        int estPerTrip = 4;

        return (tripsGold + tripsWood) * estPerTrip;
    }

    /**
     * Successor = (action, newState) çifti
     */
    private static class Successor {
        final Action action;
        final State nextState;
        Successor(Action a, State s) {
            this.action = a;
            this.nextState = s;
        }
    }

    /**
     * Bir state'ten uygulanabilecek tüm aksiyonları üret.
     * STRIPS precondition/effect kurallarını burada koda döküyoruz.
     */
    private List<Successor> generateSuccessors(State s) {
        List<Successor> list = new ArrayList<>();

        // 1) MOVE
        list.addAll(generateMoveSuccessors(s));

        // 2) HARVEST GOLD
        list.addAll(generateHarvestGoldSuccessors(s));

        // 3) HARVEST WOOD
        list.addAll(generateHarvestWoodSuccessors(s));

        // 4) DEPOSIT GOLD
        Successor depG = generateDepositGoldSuccessor(s);
        if (depG != null) list.add(depG);

        // 5) DEPOSIT WOOD
        Successor depW = generateDepositWoodSuccessor(s);
        if (depW != null) list.add(depW);

        return list;
    }

    // -------------------------
    // MOVE
    // -------------------------
    private List<Successor> generateMoveSuccessors(State s) {
        List<Successor> moves = new ArrayList<>();

        String[] allLocs = new String[]{
                "Townhall",
                "Mine1","Mine2","Mine3",
                "Forest1","Forest2","Forest3","Forest4","Forest5"
        };

        for (String target : allLocs) {
            if (!target.equals(s.peasantLocation)) {
                // Preconditions:
                // PeasantLocation == currentLocation (her zaman true by construction)
                // Effect:
                // PeasantLocation := target
                State ns = new State(
                        target,
                        s.cargoType,
                        s.cargoAmount,
                        s.goldInTownhall,
                        s.woodInTownhall,
                        s.goldRemaining,
                        s.woodRemaining
                );
                Action a = Action.move(s.peasantLocation, target);
                moves.add(new Successor(a, ns));
            }
        }

        return moves;
    }

    // -------------------------
    // HARVEST GOLD
    // -------------------------
    private List<Successor> generateHarvestGoldSuccessors(State s) {
        List<Successor> res = new ArrayList<>();

        // köylü boş mu?
        if (!s.cargoType.equals("None")) {
            return res; // boş değilse altın toplayamaz
        }

        // Hangi madenin yanındayız?
        // s.peasantLocation "Mine1", "Mine2" veya "Mine3" olmalı
        int mineIdx = -1;
        if (s.peasantLocation.equals("Mine1")) mineIdx = 0;
        else if (s.peasantLocation.equals("Mine2")) mineIdx = 1;
        else if (s.peasantLocation.equals("Mine3")) mineIdx = 2;

        if (mineIdx == -1) {
            return res; // maden değilsek hiçbir şey ekleme
        }

        // Bu madende yeterli altın var mı?
        if (s.goldRemaining[mineIdx] < 100) {
            return res;
        }

        // Effect:
        // cargoType := "Gold"
        // cargoAmount := 100
        // goldRemaining[mineIdx] -= 100
        int[] newGoldRem = Arrays.copyOf(s.goldRemaining, s.goldRemaining.length);
        newGoldRem[mineIdx] -= 100;

        State ns = new State(
                s.peasantLocation,
                "Gold",
                100,
                s.goldInTownhall,
                s.woodInTownhall,
                newGoldRem,
                s.woodRemaining
        );

        Action a = Action.harvestGold(mineIdx);
        res.add(new Successor(a, ns));
        return res;
    }

    // -------------------------
    // HARVEST WOOD
    // -------------------------
    private List<Successor> generateHarvestWoodSuccessors(State s) {
        List<Successor> res = new ArrayList<>();

        // köylü boş mu?
        if (!s.cargoType.equals("None")) {
            return res;
        }

        // hangi ormandayız?
        int forestIdx = -1;
        if (s.peasantLocation.equals("Forest1")) forestIdx = 0;
        else if (s.peasantLocation.equals("Forest2")) forestIdx = 1;
        else if (s.peasantLocation.equals("Forest3")) forestIdx = 2;
        else if (s.peasantLocation.equals("Forest4")) forestIdx = 3;
        else if (s.peasantLocation.equals("Forest5")) forestIdx = 4;

        if (forestIdx == -1) {
            return res; // ormanda değilsek boş liste
        }

        // yeterli odun var mı?
        if (s.woodRemaining[forestIdx] < 100) {
            return res;
        }

        // Effect:
        // cargoType := "Wood"
        // cargoAmount := 100
        // woodRemaining[forestIdx] -= 100
        int[] newWoodRem = Arrays.copyOf(s.woodRemaining, s.woodRemaining.length);
        newWoodRem[forestIdx] -= 100;

        State ns = new State(
                s.peasantLocation,
                "Wood",
                100,
                s.goldInTownhall,
                s.woodInTownhall,
                s.goldRemaining,
                newWoodRem
        );

        Action a = Action.harvestWood(forestIdx);
        res.add(new Successor(a, ns));
        return res;
    }

    // -------------------------
    // DEPOSIT GOLD
    // -------------------------
    private Successor generateDepositGoldSuccessor(State s) {
        // Preconditions:
        // location == Townhall
        // cargoType == Gold
        // cargoAmount == 100
        if (!s.peasantLocation.equals("Townhall")) return null;
        if (!s.cargoType.equals("Gold")) return null;
        if (s.cargoAmount != 100) return null;

        // Effect:
        // GoldInTownhall += 100
        // cargoType := None
        // cargoAmount := 0
        State ns = new State(
                s.peasantLocation,
                "None",
                0,
                s.goldInTownhall + 100,
                s.woodInTownhall,
                s.goldRemaining,
                s.woodRemaining
        );

        Action a = Action.depositGold();
        return new Successor(a, ns);
    }

    // -------------------------
    // DEPOSIT WOOD
    // -------------------------
    private Successor generateDepositWoodSuccessor(State s) {
        // Preconditions:
        // location == Townhall
        // cargoType == Wood
        // cargoAmount == 100
        if (!s.peasantLocation.equals("Townhall")) return null;
        if (!s.cargoType.equals("Wood")) return null;
        if (s.cargoAmount != 100) return null;

        // Effect:
        // WoodInTownhall += 100
        // cargoType := None
        // cargoAmount := 0
        State ns = new State(
                s.peasantLocation,
                "None",
                0,
                s.goldInTownhall,
                s.woodInTownhall + 100,
                s.goldRemaining,
                s.woodRemaining
        );

        Action a = Action.depositWood();
        return new Successor(a, ns);
    }

    // -------------------------------------------------
    // Basit test main'i (opsiyonel)
    // Bu fonksiyon, planlamayı standalone çalıştırmak için.
    // Derleyip java planner.Planner çalıştırırsan planı yazdırır.
    // (Package path'i ayarlaman gerekebilir.)
    // -------------------------------------------------
    public static void main(String[] args) {

        // örnek: 1000 gold / 1000 wood hedefi
        Planner p = new Planner(1000, 1000);
        List<Action> plan = p.plan();

        if (plan == null) {
            System.out.println("Plan bulunamadı.");
        } else {
            System.out.println("Plan bulundu! Adım sayısı = " + plan.size());
            for (int i = 0; i < plan.size(); i++) {
                System.out.println((i+1) + ". " + plan.get(i));
            }
        }
    }
}
