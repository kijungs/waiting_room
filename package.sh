rm WRS-2.0.tar.gz
rm -rf WRS-2.0
mkdir WRS-2.0
cp -R ./{run.sh,compile.sh,package.sh,src,data.html,./resources,fastutil-7.2.0.jar,./output,Makefile,README.txt,*.jar,example_graph.txt,user_guide.pdf} ./WRS-2.0
tar cvzf WRS-2.0.tar.gz --exclude='._*' ./WRS-2.0
rm -rf WRS-2.0
echo done.