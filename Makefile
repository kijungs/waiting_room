all: compile demo
compile:
	-chmod u+x ./*.sh
	./compile.sh
demo:
	-chmod u+x ./*.sh
	rm -rf output
	mkdir output
	@echo [DEMO] running WRS...
	./run_ins.sh example_graph.txt output_ins 35000 0.1
	./run_del.sh example_graph_dynamic.txt output_del 35000 0.1
	@echo [DEMO] estimated global and local triangle counts are saved in output
