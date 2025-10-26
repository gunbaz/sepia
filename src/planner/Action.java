package planner;

public class Action {

    public enum Type {
        MOVE,
        HARVEST_GOLD,
        HARVEST_WOOD,
        DEPOSIT_GOLD,
        DEPOSIT_WOOD
    }

    public final Type type;

    // Parametreler:
    // MOVE -> fromLocation, toLocation
    // HARVEST_GOLD -> mineIndex (0,1,2)
    // HARVEST_WOOD -> forestIndex (0..4)
    public final String fromLocation;
    public final String toLocation;
    public final int resourceIndex; // hangi maden / orman

    private Action(Type type,
                   String fromLocation,
                   String toLocation,
                   int resourceIndex) {
        this.type = type;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.resourceIndex = resourceIndex;
    }

    // Factory metodlar (kolay oluşturmak için)
    public static Action move(String from, String to) {
        return new Action(Type.MOVE, from, to, -1);
    }

    public static Action harvestGold(int mineIdx) {
        return new Action(Type.HARVEST_GOLD, null, null, mineIdx);
    }

    public static Action harvestWood(int forestIdx) {
        return new Action(Type.HARVEST_WOOD, null, null, forestIdx);
    }

    public static Action depositGold() {
        return new Action(Type.DEPOSIT_GOLD, null, null, -1);
    }

    public static Action depositWood() {
        return new Action(Type.DEPOSIT_WOOD, null, null, -1);
    }

    @Override
    public String toString() {
        switch (type) {
            case MOVE:
                return "Move(" + fromLocation + " -> " + toLocation + ")";
            case HARVEST_GOLD:
                return "HarvestGold(Mine" + (resourceIndex+1) + ")";
            case HARVEST_WOOD:
                return "HarvestWood(Forest" + (resourceIndex+1) + ")";
            case DEPOSIT_GOLD:
                return "DepositGold()";
            case DEPOSIT_WOOD:
                return "DepositWood()";
            default:
                return "Action(?)";
        }
    }
}
