all: compile demo
compile:
	-chmod u+x ./*.sh
	./compile.sh
demo:
	-chmod u+x ./*.sh
	rm -rf output
	mkdir output
	@echo [DEMO] running WRS...
	./run.sh example_graph.txt output 35000 0.1
	@echo [DEMO] estimated global and local triangle counts are saved in output
