@echo off

set first=src/com/vincentcodes/simulator/*.java
:: .java files are in encoding UTF-8
javac --release 11 -encoding UTF-8 -d classes -cp ./lib/*;./src/ %first%

pause