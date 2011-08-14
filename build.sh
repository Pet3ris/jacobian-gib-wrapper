cd `dirname $0`
mkdir jacobian-gib-wrapper
javac src/* -d jacobian-gib-wrapper
echo "Main-Class: GibPlayer\n" > Manifest.txt
cd jacobian-gib-wrapper
jar cvfm ../jacobian-gib-wrapper.jar ../Manifest.txt *
cd ..
rm -r jacobian-gib-wrapper
rm Manifest.txt
echo "Build complete :)"
