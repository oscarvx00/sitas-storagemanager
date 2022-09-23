#Build jar
./gradlew jar
cp ./build/libs/*.jar ./

#Run program in background
java -jar *.jar &
sleep 5
#Run test in foreground
python3 e2etest.py