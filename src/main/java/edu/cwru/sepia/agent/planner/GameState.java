package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GameState planlayıcının (A*) arama uzayında kullandığı SOYUT durum temsilidir.
 *
 * Buradaki amaç:
 * - Köylünün (peasant) nerede olduğunu bilmek
 * - Köylünün üstünde kaynak taşıyıp taşımadığını bilmek
 * - Toplamda TownHall'a teslim edilmiş (depolanmış) altın ve odun miktarlarını bilmek
 * - Hangi kaynak düğümlerinde (maden / orman) ne kadar kaynak kaldığını bilmek
 *
 * Bu state, STRIPS-benzeri aksiyonlarla (MoveAction, HarvestAction, DepositAction)
 * yeni state'lere kopyalanarak genişletilir.
 *
 * NOT: Bu sınıf sadece tek köylü senaryosunu (RC1) temsil ediyor.
 * RC2 için çoklu köylü ve BuildPeasantAction ayrıca eklenecek.
 */
public class GameState {

    // --- Sabit dünya bilgileri / varsayımlar ---
    // TownHall koordinatları (rc_3m5t.xml’e göre)
    // Bunlar ileride dinamik alınabilir ama şu an sabit tutulmuş.
    static final int TOWN_HALL_X = 8;
    static final int TOWN_HALL_Y = 9;

    // --- Hedef gereksinimler ---
    // Bu değerleri istersen daha sonra konfigürasyondan alacak hale getirebiliriz.
    // Şu an RC1 kapsamında (200/200 gibi) sabit duruyor.
    final int requiredGold = 200;
    final int requiredWood = 200;

    // --- Dinamik durum değişkenleri ---
    // TownHall'a teslim edilmiş toplam kaynak
    int collectedGold;
    int collectedWood;

    // Agent'ın takip etmesi için kimlikler (SEPIA tarafıyla eşlemek için)
    int peasantID;
    int townhallID;

    // Köylünün haritadaki konumu
    int peasantX;
    int peasantY;

    // Köylü bir şey taşıyor mu?
    boolean peasantIsCarrying;

    // Eğer taşıyorsa bu şey altın mı? (false ise odun varsay)
    boolean peasantCarriesGold;

    // Haritadaki kaynak düğümleri (madenler / ormanlar)
    List<ResourceInfo> resources;

    /**
     * Bu constructor gerçek SEPIA durumundan (StateView) soyut planlama durumunu çıkarır.
     * RCAgent initialStep() içinde ilk planlama başlatılırken kullanılacak.
     */
    public GameState(State.StateView state) {
        Unit.UnitView peasant = null;
        Unit.UnitView townhall = null;

        // Oyuncu 0 varsayılıyor (senin agent'ın)
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

            // Köylü bir şey taşıyor mu?
            this.peasantIsCarrying = peasant.getCargoAmount() > 0;

            // Taşıdığı şeyin türünü basitçe varsayım olarak "altın mı?" diye belirliyoruz.
            // (İleride state.getUnit(peasantID).getCargoType() ile daha kesin alınabilir.)
            this.peasantCarriesGold = false;
        }

        if (townhall != null) {
            this.townhallID = townhall.getID();
        }

        // Toplam teslim edilmiş kaynakları al:
        // state.getResourceAmount(playerNum, ResourceType) -> TownHall stoğu gibi.
        this.collectedGold = state.getResourceAmount(0, ResourceType.GOLD);
        this.collectedWood = state.getResourceAmount(0, ResourceType.WOOD);

        // Kaynak düğümlerini listele (altın madenleri ve ormanlar)
        this.resources = new ArrayList<>();
        for (ResourceNode.ResourceView resource : state.getAllResourceNodes()) {
            boolean isGold = (resource.getType() == ResourceNode.Type.GOLD_MINE);
            resources.add(new ResourceInfo(
                    resource.getXPosition(),
                    resource.getYPosition(),
                    isGold,
                    resource.getAmountRemaining(),
                    resource.getID()
            ));
        }
    }

    /**
     * Kopyalama constructor'ı.
     * Aksiyonlar (HarvestAction gibi) state'i MUTASYONA uğratmıyor; bunun yerine
     * yeni bir GameState kopyası oluşturuyor. Bu yüzden deep copy önemli.
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
        for (ResourceInfo res : other.resources) {
            this.resources.add(new ResourceInfo(res));
        }
    }

    /**
     * Hedef duruma ulaştık mı?
     * RC1 hedefi: en az requiredGold ve requiredWood depolanmış olmalı.
     */
    public boolean isGoal() {
        return collectedGold >= requiredGold && collectedWood >= requiredWood;
    }

    /**
     * Bu durumdan hangi STRIPS-benzeri aksiyonlar yasal olarak uygulanabilir?
     * Planner (AStarPlanner) bunları çağırıp successor state'ler üretiyor.
     */
    public List<StripsAction> generateApplicableActions() {
        List<StripsAction> actions = new ArrayList<>();

        // Eğer köylünün elinde kaynak varsa:
        if (peasantIsCarrying) {

            // 1) TownHall'daysak -> Deposit yapabiliriz
            if (peasantX == TOWN_HALL_X && peasantY == TOWN_HALL_Y) {
                DepositAction deposit = new DepositAction();
                if (deposit.arePreconditionsMet(this)) {
                    actions.add(deposit);
                }

            } else {
                // 2) Değilsek -> TownHall'a hareket edebiliriz
                actions.add(new MoveAction(
                        peasantX, peasantY,
                        TOWN_HALL_X, TOWN_HALL_Y
                ));
            }

        } else {
            // Köylü boşsa:
            // Her kaynak düğümü için iki olasılık:
            // - Eğer üstündeyiz ve yeterli miktar varsa --> Harvest
            // - Değilsek --> oraya Move
            for (ResourceInfo resource : resources) {

                boolean standingOnThisResource =
                        (peasantX == resource.x && peasantY == resource.y);

                if (standingOnThisResource) {
                    HarvestAction harvest = new HarvestAction(resource);
                    if (harvest.arePreconditionsMet(this)) {
                        actions.add(harvest);
                    }
                } else {
                    // Kaynağın yanına git (hedef = kaynağın koordinatı)
                    actions.add(new MoveAction(
                            peasantX, peasantY,
                            resource.x, resource.y
                    ));
                }
            }
        }

        return actions;
    }

    /**
     * GameState eşitliği.
     * Bu çok kritik çünkü A* closed set'te state karşılaştırırken bunu kullanıyor.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;
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
        return Objects.hash(
                collectedGold,
                collectedWood,
                peasantX,
                peasantY,
                peasantIsCarrying,
                peasantCarriesGold,
                resources
        );
    }

    /**
     * ResourceInfo: Bir kaynak düğümünün (maden/orman) planlayıcıya yansıyan hali.
     * amountRemaining azalabilir (HarvestAction bunu düşürüyor).
     */
    public static class ResourceInfo {
        public int x;
        public int y;
        public boolean isGoldMine;
        public int amountRemaining;
        public int resourceID;

        public ResourceInfo(int x, int y, boolean isGoldMine, int amountRemaining, int resourceID) {
            this.x = x;
            this.y = y;
            this.isGoldMine = isGoldMine;
            this.amountRemaining = amountRemaining;
            this.resourceID = resourceID;
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
            if (!(o instanceof ResourceInfo)) return false;
            ResourceInfo that = (ResourceInfo) o;
            return x == that.x &&
                   y == that.y &&
                   isGoldMine == that.isGoldMine &&
                   amountRemaining == that.amountRemaining &&
                   resourceID == that.resourceID;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, isGoldMine, amountRemaining, resourceID);
        }
    }
}
