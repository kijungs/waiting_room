echo compiling java sources...
rm -rf class
mkdir class

javac -cp ./fastutil-7.2.0.jar -d class $(find ./src -name *.java)

echo make jar archive...
cd class
jar cf WRS-2.0.jar ./
rm ../WRS-2.0.jar
mv WRS-2.0.jar ../
cd ..
rm -rf class

echo done.
