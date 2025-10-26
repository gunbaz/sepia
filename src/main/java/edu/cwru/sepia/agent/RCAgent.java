package edu.cwru.sepia.agent;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.AStarPlanner;
import edu.cwru.sepia.agent.planner.DepositAction;
import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.HarvestAction;
import edu.cwru.sepia.agent.planner.MoveAction;
import edu.cwru.sepia.agent.planner.StripsAction;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RCAgent extends Agent {

    private Stack<StripsAction> plan = null;
    private int peasantID;
    private int townhallID;

    public RCAgent(int playernum) {
        super(playernum);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        for (Unit.UnitView unit : stateView.getUnits(0)) {
            String unitName = unit.getTemplateView().getName();
            if (unitName.equals("Peasant")) {
                peasantID = unit.getID();
            } else if (unitName.equals("TownHall")) {
                townhallID = unit.getID();
            }
        }
        
        GameState initialState = new GameState(stateView);
        AStarPlanner planner = new AStarPlanner();
        System.out.println("[RCAgent] Planlama başlıyor...");
        plan = planner.findPlan(initialState);
        
        if (plan != null) {
            System.out.println("[RCAgent] Plan bulundu! Eylem sayısı: " + plan.size());
        } else {
            System.out.println("[RCAgent] UYARI: Plan bulunamadı!");
        }
        return middleStep(stateView, historyView);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        Map<Integer, Action> actions = new HashMap<>();

        // Köylünün mevcut bir eylemi yoksa ve planda eylem varsa devam et
        if (plan != null && !plan.isEmpty() && stateView.getUnit(peasantID).getCurrentDurativeAction() == null) {
            StripsAction nextAction = plan.peek(); // Eylemi stack'ten çıkarma, sadece bak
            
            System.out.println("[RCAgent] Sıradaki Eylem -> " + nextAction.getClass().getSimpleName());
            
            Action sepiaAction = createSepiaAction(nextAction);
            
            if(sepiaAction != null) {
                actions.put(peasantID, sepiaAction);
                plan.pop(); // Eylem başarıyla oluşturulduysa, stack'ten çıkar
            } else {
                System.out.println("[RCAgent] HATA: Eylem oluşturulamadı, plan iptal ediliyor.");
                plan.clear(); // Planı temizle
            }
        }
        return actions;
    }

    /**
     * StripsAction'ı SEPIA Action'a çevirir.
     */
    private Action createSepiaAction(StripsAction action) {
        if (action instanceof MoveAction) {
            MoveAction move = (MoveAction) action;
            return Action.createCompoundMove(peasantID, move.targetX, move.targetY);
        } else if (action instanceof HarvestAction) {
            HarvestAction harvest = (HarvestAction) action;
            return Action.createCompoundGather(peasantID, harvest.resource.resourceID);
        } else if (action instanceof DepositAction) {
            return Action.createCompoundDeposit(peasantID, townhallID);
        }
        // Bu noktaya gelinmemeli
        return null;
    }

    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {
        System.out.println("[RCAgent] Simülasyon bitti.");
    }
    
    @Override
    public void savePlayerData(OutputStream outputStream) {}

    @Override
    public void loadPlayerData(InputStream inputStream) {}
}
