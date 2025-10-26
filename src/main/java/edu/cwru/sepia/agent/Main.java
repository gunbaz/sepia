package edu.cwru.sepia.agent;

import edu.cwru.sepia.experiment.Configuration;
import edu.cwru.sepia.experiment.SimpleModelEpisodicRunner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Hata: Lütfen yapılandırma dosyasını argüman olarak belirtin.");
            System.err.println("Kullanım: java edu.cwru.sepia.agent.Main <config_dosyasi.xml>");
            System.exit(1);
        }

        String configFile = args[0];
        
        try {
            // Yapılandırma dosyasından bir Configuration nesnesi oluştur
            Configuration config = new Configuration(configFile);
            
            // Deney yürütücüsünden bir nesne oluştur
            SimpleModelEpisodicRunner runner = new SimpleModelEpisodicRunner();
            
            // Deneyi, oluşturulan yapılandırma ile çalıştır
            runner.run(config);
            
        } catch (Exception e) {
            System.err.println("Simülasyon başlatılırken bir hata oluştu:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}