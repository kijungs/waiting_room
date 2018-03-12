rm WRS-1.0.tar.gz
rm -rf WRS-1.0
mkdir WRS-1.0
cp -R ./{run.sh,compile.sh,package.sh,src,data.html,./resources,fastutil-7.2.0.jar,./output,Makefile,README.txt,*.jar,example_graph.txt,user_guide.pdf} ./WRS-1.0
tar cvzf WRS-1.0.tar.gz --exclude='._*' ./WRS-1.0
rm -rf WRS-1.0
echo done.