# SEPIA Tabanlı Planlama Projesi

Bu proje, SEPIA (Simple Environment for Producing Intelligent Agents) framework'ü kullanılarak geliştirilmiş bir planlama sistemidir.

## Proje Yapısı

Proje aşağıdaki ana bileşenlerden oluşmaktadır:

- `src/agent/`: Agent implementasyonları
  - `ExecutionAgent.java`: Ana agent sınıfı
- `src/planner/`: Planlama algoritması implementasyonları
  - `Action.java`: Aksiyon tanımlamaları
  - `Node.java`: Plan ağacı düğüm yapısı
  - `Planner.java`: Ana planlama algoritması
  - `State.java`: Durum temsili

## Kurulum

1. Java JDK 8 veya üzeri gereklidir
2. SEPIA framework'ünün kurulu olması gereklidir
3. Projeyi klonlayın:
   ```bash
   git clone https://github.com/gunbaz/sepia.git
   ```

## Kullanım

1. Projeyi derleyin
2. Planlayıcıyı çalıştırın:
   ```java
   java -cp . agent.ExecutionAgent
   ```

## Katkıda Bulunma

1. Bu depoyu forklayın
2. Feature branch'i oluşturun (`git checkout -b feature/YeniOzellik`)
3. Değişikliklerinizi commit edin (`git commit -am 'Yeni özellik: XYZ eklendi'`)
4. Branch'inizi push edin (`git push origin feature/YeniOzellik`)
5. Pull Request oluşturun

## Lisans

Bu proje açık kaynak olarak MIT lisansı altında lisanslanmıştır.
