package planner;

import java.util.Arrays;
import java.util.Objects;

public class State {

    // Konumsal bilgi
    // Örn: "Townhall", "Mine1", "Forest3", ...
    public final String peasantLocation;

    // Yük bilgisi
    // "None", "Gold", "Wood"
    public final String cargoType;

    // 0 veya 100
    public final int cargoAmount;

    // Kasada biriken kaynaklar
    public final int goldInTownhall;
    public final int woodInTownhall;

    // Kalan kaynaklar (haritadaki maden ve orman durumları)
    // goldRemaining[0] -> Mine1
    // goldRemaining[1] -> Mine2
    // goldRemaining[2] -> Mine3
    public final int[] goldRemaining;

    // woodRemaining[0] -> Forest1
    // ...
    // woodRemaining[4] -> Forest5
    public final int[] woodRemaining;

    // Constructor
    public State(String peasantLocation,
                 String cargoType,
                 int cargoAmount,
                 int goldInTownhall,
                 int woodInTownhall,
                 int[] goldRemaining,
                 int[] woodRemaining) {

        this.peasantLocation = peasantLocation;
        this.cargoType = cargoType;
        this.cargoAmount = cargoAmount;
        this.goldInTownhall = goldInTownhall;
        this.woodInTownhall = woodInTownhall;

        // defensive copy
        this.goldRemaining = Arrays.copyOf(goldRemaining, goldRemaining.length);
        this.woodRemaining = Arrays.copyOf(woodRemaining, woodRemaining.length);
    }

    // Başlangıç state'i kolay oluşturmak için helper
    public static State initialState() {
        return new State(
                "Townhall",    // peasantLocation
                "None",        // cargoType
                0,             // cargoAmount
                0,             // goldInTownhall
                0,             // woodInTownhall
                new int[]{100, 500, 5000},             // goldRemaining (Mine1,2,3)
                new int[]{400, 400, 400, 400, 400}     // woodRemaining (Forest1..5)
        );
    }

    // Goal testi
    public boolean isGoal(int requiredGold, int requiredWood) {
        return (goldInTownhall >= requiredGold) &&
               (woodInTownhall >= requiredWood);
    }

    // equals & hashCode -> A* closed set için şart
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return cargoAmount == state.cargoAmount &&
                goldInTownhall == state.goldInTownhall &&
                woodInTownhall == state.woodInTownhall &&
                Objects.equals(peasantLocation, state.peasantLocation) &&
                Objects.equals(cargoType, state.cargoType) &&
                Arrays.equals(goldRemaining, state.goldRemaining) &&
                Arrays.equals(woodRemaining, state.woodRemaining);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(peasantLocation, cargoType, cargoAmount, goldInTownhall, woodInTownhall);
        result = 31 * result + Arrays.hashCode(goldRemaining);
        result = 31 * result + Arrays.hashCode(woodRemaining);
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "loc=" + peasantLocation +
                ", cargoType=" + cargoType +
                ", cargoAmount=" + cargoAmount +
                ", goldTH=" + goldInTownhall +
                ", woodTH=" + woodInTownhall +
                ", goldRem=" + Arrays.toString(goldRemaining) +
                ", woodRem=" + Arrays.toString(woodRemaining) +
                '}';
    }
}
