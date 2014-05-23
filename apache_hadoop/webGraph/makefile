compile:
	rm -rf bin/part1_classes
	rm -rf part1.jar
	mkdir bin/part1_classes
	javac -classpath /usr/local/hadoop-blast/0.1/lib/hadoop-0.20.2-core.jar:bin/part1_classes -d bin/part1_classes src/WebGraph.java src/CompositeWriteable.java
	jar -cvf part1.jar -C bin/part1_classes/ .
part1: compile
	rm -rf output/part1
	hadoop dfs -rmr part1_output
	hadoop jar part1.jar WebGraph input/ part1_output/
	hadoop dfs -cat part1_output/p* > output/part1
compile2:
	rm -rf bin/part2_classes
	rm -rf part2.jar
	mkdir bin/part2_classes
	javac -classpath /usr/local/hadoop-blast/0.1/lib/hadoop-0.20.2-core.jar:bin/part2_classes -d bin/part2_classes src/Source.java
	jar -cvf part2.jar -C bin/part2_classes/ .
part2: compile2
	rm -rf output/part2
	hadoop dfs -rmr part2_output
	hadoop jar part2.jar Source input/ part2_output/
	hadoop dfs -cat part2_output/p* > output/part2
clean:
	rm -rf *.jar
	rm -rf bin/*
