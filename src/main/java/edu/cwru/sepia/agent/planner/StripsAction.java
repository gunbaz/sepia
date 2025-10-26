package edu.cwru.sepia.agent.planner;

/**
 * Bu arayüz, planlayıcının kullanabileceği tüm STRIPS-benzeri eylemler için
 * bir şablon tanımlar. Her eylem bu arayüzü uygulamalıdır.
 */
public interface StripsAction {

    /**
     * Bir eylemin belirli bir durumda uygulanabilir olup olmadığını kontrol eder.
     * @param state Eylemin uygulanacağı mevcut durum.
     * @return Eylemin önkoşulları karşılanıyorsa true, aksi halde false.
     */
    boolean arePreconditionsMet(GameState state);

    /**
     * Eylemi mevcut duruma uygular ve sonuçta ortaya çıkan yeni durumu döndürür.
     * DİKKAT: Bu metod, parametre olarak gelen 'state' nesnesini DEĞİŞTİRMEMELİ,
     * bunun yerine eylemin etkilerini içeren YENİ bir GameState nesnesi oluşturmalıdır.
     * @param state Eylemin uygulanacağı mevcut durum.
     * @return Eylem uygulandıktan sonraki yeni durum.
     */
    GameState apply(GameState state);
    
    /**
     * Bu eylemi gerçekleştirmenin maliyetini döndürür.
     * A* algoritması bu maliyeti en düşük maliyetli yolu bulmak için kullanır.
     * @return Eylemin maliyeti (örn: hareket için mesafe).
     */
    double getCost();
}