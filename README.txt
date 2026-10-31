Сборка:
1) Нужна Java 21.
2) Самый простой способ получить gradle wrapper: зайти на https://fabricmc.net/develop/template/,
   выбрать 1.21.4, скачать шаблон, и скопировать из него папки gradle/ и файлы gradlew, gradlew.bat в эту папку.
   (Либо установить Gradle 8.12 и выполнить `gradle build`.)
3) Выполнить:  ./gradlew build   (Windows: gradlew.bat build)
4) Готовый мод: build/libs/totemswap-1.0.0.jar -> положить в папку mods (нужен Fabric API).
Клавиша X меняется в Настройки -> Управление.
