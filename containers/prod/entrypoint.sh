#Build jar
./gradlew jar
cp ./build/libs/*.jar ./

#Run program
java -jar *.jar
