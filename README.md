# SEPIA Planlama ve Yürütme Projesi

Bu proje, SEPIA (Strategy Engine for Programming Intelligent Agents) framework'ü kullanılarak geliştirilmiş bir planlama ve yürütme sistemi içerir. Sistem, bir oyun ortamında kaynakları verimli bir şekilde toplayan bir ajan uygulamasıdır.

## Proje Yapısı

- `src/agent/` - Ajan uygulaması ve SEPIA entegrasyonu
  - `ExecutionAgent.java` - Ana ajan sınıfı
- `src/planner/` - Planlama sistemi
  - `Action.java` - Aksiyon tanımlamaları
  - `Node.java` - A* algoritması düğüm yapısı
  - `Planner.java` - A* planlama algoritması
  - `State.java` - Durum temsili

## Kurulum

1. Java 8 veya üzeri gereklidir
2. SEPIA framework JAR dosyalarını `lib/` klasörüne yerleştirin:
   - sepia.jar
   - sepiaframe.jar

## Çalıştırma

1. Projeyi derlemek için:
```bash
cd src
javac -cp ".;lib/*" agent/*.java planner/*.java
```

2. Uygulamayı çalıştırmak için:
```bash
java -cp ".;lib/*" edu.cwru.sepia.Main2 midasConfig.xml
```

## Yapılandırma

- `midasConfig.xml`: Ajan ve oyun parametreleri
- `rc_3m5t.xml`: Harita ve birim tanımlamaları

## Özellikler

- A* algoritması ile kaynak toplama planlaması
- SEPIA framework entegrasyonu
- Kaynakları verimli toplama ve depolama
- 100 episode test desteği

## Geliştirici

[Adınız]

## Lisans

Bu proje [lisans adı] altında lisanslanmıştır.