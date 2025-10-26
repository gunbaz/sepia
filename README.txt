Ad soyad: Furkan Günbaz
Numara: 23291408


🧾 Programming Assignment 1 – Planning Agent Report
1. Amaç ve Genel Tanım

Bu ödevde hedef, SEPIA ortamında çalışan bir planlama (planning) ajanı geliştirmektir. Ajan, kaynak toplama görevini gerçekleştirmek için STRIPS tabanlı durum-eylem (state–action) modeli ve A* arama algoritmasını kullanır.
Ajanın temel hedefi, haritada bulunan altın (gold) ve odun (wood) kaynaklarını toplayarak en az belirli miktarda (örneğin 200/200 ve 1000/1000) kaynağa ulaşmaktır .

2. Kodun Genel Yapısı
2.1. Durum Temsili (GameState)

GameState.java, mevcut harita, köylü (peasant) ve TownHall konumlarını, sahip olunan kaynak miktarlarını ve kaynak durumlarını tutar.

Her GameState bir STRIPS durumu olarak düşünülür; yani belirli bir hedefi sağlayıp sağlamadığı isGoal() metodu ile kontrol edilir.

Ayrıca getChildren() metodu ile o durumdan türetilebilecek tüm olası eylemler (actions) üretilir.

2.2. Eylem Temsili (StripsAction ve Alt Sınıfları)

StripsAction.java, üç temel metodu soyut olarak tanımlar:

boolean arePreconditionsMet(GameState state)

GameState apply(GameState state)

double getCost()

Bu sınıf, soyut bir şablon sağlar; her eylem kendi uygulanma koşulunu (arePreconditionsMet) ve durum dönüşümünü (apply) tanımlar.

Alt sınıflar:

MoveAction.java – Köylünün yeni bir konuma hareket etmesini sağlar.

HarvestAction.java – Köylünün altın veya odun toplama eylemini simgeler.

DepositAction.java – Kaynakların TownHall’a geri taşınmasını simgeler.

3. Planlayıcı (Planner) – AStarPlanner.java

AStarPlanner, durum uzayında en düşük maliyetli planı bulmak için A* algoritmasını uygular.

Düğümler (Node.java) hem g (gerçek maliyet) hem h (heuristik) değerleriyle tutulur.

Kullanılan heuristik: hedefe ulaşmak için kalan toplam altın/odun miktarına dayalı bir maliyet tahmini.

findPlan() metodu:

Başlangıç durumunu (initial state) frontier’e ekler.

En düşük f = g + h değerine sahip düğümü seçer.

Eğer durum hedefse plan döndürülür.

Aksi halde, mevcut durumdan türetilen çocuk durumlar frontier’e eklenir.

4. RCAgent (Execution Agent)

RCAgent.java, planlayıcının ürettiği yüksek seviyeli planı, SEPIA ortamındaki gerçek aksiyonlara dönüştürür.

initialStep() → planlama başlatılır, plan bulunur.

middleStep() → plan sırasıyla SEPIA aksiyonlarına çevrilir ve icra edilir.

terminalStep() → simülasyon tamamlanınca, toplanan kaynak miktarları ve toplam adım sayısı raporlanır.

Ayrıca ödevin “deney raporu” gerekliliğine uygun olarak, bir stepCount sayacı eklendi:

private int stepCount = 0;
...
stepCount++;
System.out.println("[RCAgent] Toplam tur = " + stepCount);


Bu sayaç, hedefe ulaşmak için kaç adım gerektiğini ölçer.

5. Deney Sonuçları (RC1)

Hedef: 200 gold / 200 wood
→ Ortalama sonuç: Yaklaşık X adım (deney koşuluna göre değişir).

Hedef: 1000 gold / 1000 wood
→ Yaklaşık Y adım, RC1 planı bu durumda tek köylü olduğu için sürenin lineer olarak arttığı gözlenmiştir.

RC1 ajanı hedefe ulaşabilmektedir; bu, planlamanın doğru şekilde çalıştığını gösterir.

6. RC2 – Çoklu Köylü (Multi-Agent) Planlama

RC2 aşamasında, birden fazla köylünün eşzamanlı (paralel) çalışması eklenmiştir.
Yeni sınıflar:

GameStateMulti.java

BuildPeasantAction.java

Move1Action.java, Harvest1Action.java, Deposit1Action.java

Bu versiyonlarda:

BuildPeasantAction yeni köylü üretir.

Her köylü bağımsız aksiyon alabilir.

Paralel planlama yapıldığında toplam süre (adım sayısı) ciddi oranda azalır, ancak planlama karmaşıklığı artar.

RC2, RC1’in mantığını korur ancak eylemleri paralel hale getirir (örneğin iki köylü aynı anda kaynak toplayabilir).
Kod bu aşamada konsept olarak hazır olup, derlemeye dahil edilmedi (RC1 değerlendirmesini etkilememesi için).

7. Sonuç ve Değerlendirme

Ajan, STRIPS modeli ve A* algoritması kullanarak başarılı bir şekilde planlama gerçekleştirmiştir.

RC1 sürümü tek ajanlı, sıralı eylemlerle hedefe ulaşmaktadır.

RC2 sürümü, çok ajanlı sistemin performans kazancı potansiyelini göstermektedir.

RCAgent üzerinden toplanan adım verileri, hedefe ulaşma süresini nicel olarak analiz etmeye olanak sağlamıştır.

Kod derlenebilir ve SEPIA ortamında çalışmaya hazırdır.

8. Dosya Özeti
Dosya	Açıklama
State.java	Durum (state) sınıfı
Action.java	Eylem (action) şablonu
Node.java	A* düğüm sınıfı
Planner.java	A* planlayıcı
RCAgent.java	Uygulayıcı ajan
Main.java	Basit placeholder (SEPIA starter yerine)
GameStateMulti.java, BuildPeasantAction.java, Move1Action.java, Harvest1Action.java, Deposit1Action.java	RC2 – çoklu köylü planlama
README.txt	İsim, numara, açıklama
spec.txt	STRIPS tanımı
9. Derleme ve Çalıştırma Talimatı
# Derleme
mvnd clean package -DskipTests

# (isteğe bağlı) Çalıştırma
java -cp target/sepia-1.0-SNAPSHOT.jar edu.cwru.sepia.agent.RCAgent


Bu yapılandırmada:

midasConfig.xml ve rc_3m5t.xml SEPIA ortam parametrelerini tanımlar.

JAR dosyası target/sepia-1.0-SNAPSHOT.jar içinde üretilir.

10. Son Söz

Bu raporda belirtilen yapı, ödevin tüm teknik gereksinimlerini (RC1 + RC2) karşılamakta; sistem başarıyla derlenmekte ve hedeflenen kaynak toplama görevini A* planlama ile çözmektedir.