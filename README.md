### Graph Visualizer

##### Requirements

* Maven 3
* JDK 7 (Oracle or OpenJDK)

##### What it does?

The Graph-Visualizer is a little tool to visualise a given graph.
The graph as adjacency list will be parsed to .dot format.


The Visualizer is able to:
- parse a adjacency list to .dot format with random generated color for nodes
- match a given graph with an calculated output
- calculate edge-cut


##### How to use?

To use the tool you need to compile an executable .jar file, like this:
mvn clean package

To use the .jar file you can specify what the tool should do:
java -jar generated_jarfile.jar
-h to display the help
-cd create .dot (require: input-graph, pattern)
-cm create a colorMap (require: input-graph, pattern)
-cmd create matched .dot (require: input-graph, pattern, graph-result)
-ec calculate edge-cut (require: input-graph, pattern, graph-result)

input-graph: a input graph as adjacency list
pattern: e.g. \t or " "(spaces) how the adjacency list is structured
graph-result: an calculated result based on a graph algorithm. For example
the output of an giraph based graph algorithm.


