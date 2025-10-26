package edu.cwru.sepia.agent;

// Bu importları şimdilik yoruma aldık çünkü VS Code bunları build path'te göremiyor.
// Çalıştırırken dış classpath'e Sepia.jar ekleyeceğiz.
/*
import edu.cwru.sepia.environment.model.SystemConfig;
import edu.cwru.sepia.environment.model.persistence.XmlLoader;
*/

public class StartGame {
    public static void main(String[] args) {
        String configFile = "midasConfig.xml";
        System.out.println("[StartGame] Loading config: " + configFile);

        try {
            // Deneme 1: bazı SEPIA sürümlerinde sadece bu çağrı oyunu başlatır
            // XmlLoader.load(configFile);

            // Deneme 2: bazı sürümlerde bu çağrı ana oyun döngüsünü ve GUI'yi başlatır
            // SystemConfig.runGame(configFile);

            // Şimdilik sadece debug basalım. Asıl çağrıyı VS Code launch sırasında yapacağız.
            System.out.println("[StartGame] (Placeholder) Call to engine would happen here.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
