### Server
Server je psán v jazyce C a jedná se o konzolovou aplikaci.
#### Build a spuštění
Pro následující příkazy předpokládáme, že se vyskytujeme v adresáři `/server/`.
Pro jednodušší buildění je využíván nástroj CMake, který nám vytvoří příslušný Makefile.
Spustíme tedy příkaz `cmake` a zvolíme výstup do složky `build`:
```
cmake -B ./build/
```
Přemístíme se do adresáře ´build/´:
```
cd build
```
Nyní pomocí Makefilu sestavíme spustitelný soubor:
```
make
```

Vytvoří se nám spustitelný soubor ```server```. Ten spustíme společně se zadaným portem, na kterým má server poslouchat:
```
./server --port 9123
```

### Klient
Klient je napsán v jazyce Java a jedná se o okenní aplikaci využívající JavaFX.
#### Build a spuštění
Předpokládá se, že před zadáním následujících příkazů se nacházíme v adresáři `/client/`
Pro spuštění můžeme použít wrapper Gradle. Aplikaci pomocí něj sputíme zadáním příkazu:
```
./gradlew run
```
Po zadání předchozího příkazu se otevře okno pro zadání údajů pro připojení.
