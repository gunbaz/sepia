package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType; // DÜZELTME: Doğru import eklendi.
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameState {

    // --- Sabit Dünya Bilgileri ---
    static final int TOWN_HALL_X = 8;
    static final int TOWN_HALL_Y = 9;

    // --- Hedef ---
    final int requiredGold = 200;
    final int requiredWood = 200;

    // --- Değişken Durum Bilgileri ---
    int collectedGold;
    int collectedWood;
    int peasantID;
    int townhallID;
    int peasantX, peasantY;
    boolean peasantIsCarrying;
    boolean peasantCarriesGold;
    List<ResourceInfo> resources;

    /**
     * Başlangıç durumunu SEPIA'nın StateView'inden oluşturan constructor.
     */
    public GameState(State.StateView state) {
        Unit.UnitView peasant = null;
        Unit.UnitView townhall = null;
        for (Unit.UnitView unit : state.getUnits(0)) {
            String unitTypeName = unit.getTemplateView().getName();
            if (unitTypeName.equals("Peasant")) {
                peasant = unit;
            } else if (unitTypeName.equals("TownHall")) {
                townhall = unit;
            }
        }

        if (peasant != null) {
            this.peasantID = peasant.getID();
            this.peasantX = peasant.getXPosition();
            this.peasantY = peasant.getYPosition();
            this.peasantIsCarrying = peasant.getCargoAmount() > 0;
            this.peasantCarriesGold = false;
        }
        if (townhall != null) {
            this.townhallID = townhall.getID();
        }

        // DÜZELTME: ResourceNode.Type yerine ResourceType kullanıldı.
        this.collectedGold = state.getResourceAmount(0, ResourceType.GOLD);
        this.collectedWood = state.getResourceAmount(0, ResourceType.WOOD);
        
        this.resources = new ArrayList<>();
        for (ResourceNode.ResourceView resource : state.getAllResourceNodes()) {
            boolean isGold = resource.getType() == ResourceNode.Type.GOLD_MINE;
            resources.add(new ResourceInfo(resource.getXPosition(), resource.getYPosition(), isGold, resource.getAmountRemaining(), resource.getID()));
        }
    }
    
    /**
     * Kopyalama constructor'ı.
     */
     public GameState(GameState other) {
        this.collectedGold = other.collectedGold;
        this.collectedWood = other.collectedWood;
        this.peasantID = other.peasantID;
        this.townhallID = other.townhallID;
        this.peasantX = other.peasantX;
        this.peasantY = other.peasantY;
        this.peasantIsCarrying = other.peasantIsCarrying;
        this.peasantCarriesGold = other.peasantCarriesGold;
        this.resources = new ArrayList<>();
        for(ResourceInfo res : other.resources) {
            this.resources.add(new ResourceInfo(res));
        }
    }
    
    public boolean isGoal() {
        return collectedGold >= requiredGold && collectedWood >= requiredWood;
    }
    
    public List<StripsAction> generateApplicableActions() {
        List<StripsAction> actions = new ArrayList<>();
        if (peasantIsCarrying) {
            if (peasantX == TOWN_HALL_X && peasantY == TOWN_HALL_Y) {
                actions.add(new DepositAction());
            } else {
                actions.add(new MoveAction(peasantX, peasantY, TOWN_HALL_X, TOWN_HALL_Y));
            }
        } else {
            for (ResourceInfo resource : resources) {
                if (peasantX == resource.x && peasantY == resource.y) {
                    HarvestAction harvest = new HarvestAction(resource);
                    if (harvest.arePreconditionsMet(this)) {
                        actions.add(harvest);
                    }
                } else {
                     actions.add(new MoveAction(peasantX, peasantY, resource.x, resource.y));
                }
            }
        }
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return collectedGold == gameState.collectedGold &&
                collectedWood == gameState.collectedWood &&
                peasantX == gameState.peasantX &&
                peasantY == gameState.peasantY &&
                peasantIsCarrying == gameState.peasantIsCarrying &&
                peasantCarriesGold == gameState.peasantCarriesGold &&
                Objects.equals(resources, gameState.resources);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(collectedGold, collectedWood, peasantX, peasantY, peasantIsCarrying, peasantCarriesGold, resources);
    }
    
    public static class ResourceInfo {
        public int x;
        public int y;
        public boolean isGoldMine;
        public int amountRemaining;
        public int resourceID;

        public ResourceInfo(int x, int y, boolean isGoldMine, int amount, int id) {
            this.x = x;
            this.y = y;
            this.isGoldMine = isGoldMine;
            this.amountRemaining = amount;
            this.resourceID = id;
        }

        public ResourceInfo(ResourceInfo toCopy) {
            this.x = toCopy.x;
            this.y = toCopy.y;
            this.isGoldMine = toCopy.isGoldMine;
            this.amountRemaining = toCopy.amountRemaining;
            this.resourceID = toCopy.resourceID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResourceInfo that = (ResourceInfo) o;
            return x == that.x && y == that.y && isGoldMine == that.isGoldMine && amountRemaining == that.amountRemaining && resourceID == that.resourceID;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, isGoldMine, amountRemaining, resourceID);
        }
    }
}