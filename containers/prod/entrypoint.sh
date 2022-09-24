#Build jar
./gradlew jar
cp ./build/libs/*.jar ./

#Run program in background
java -jar *.jar &
