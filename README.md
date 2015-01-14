### Graph Visualizer

##### Requirements

* Maven 3
* JDK 7 (Oracle or OpenJDK)

##### What it does?

The Graph-Visualizer is a little tool to visualise a given partitioned graph.
The input format is an adjacency list which will be parsed and converted to
the .dot format.

The Visualizer is able to:
- parse an adjacency list, create a random generated color for each partition
 and output a .dot file with colored vertices based on their partition
- calculate edge-cut for the input graph

##### Input format

The adjacency list must contain the information to which partition a vertex
belongs to. The following line

`0 1 2 3 4 5`

represents a vertex with id 0, a vertex values (1) and four edges (2 3 4 5).
The vertex value represents the partition id of that vertex.

For some reasons the tool also supports an input format with multiple values
per vertex. Consider the following line:

`0 1 1 2 3 4 5`

that represents a vertex with id 0, two vertex values (1, 2) and four edges
(2 3 4 5). In that case, an edge offset and a partition token index have to
be provided by the user (see examples).


##### How to use?

To use the tool you need to compile an executable .jar file, like this:

> mvn clean package

To use the .jar file you can specify what the tool should do, see:

> java -jar visualiser.jar -h

for help.

##### Examples:

* Create a colored dot file and a color map.

> java -jar visualiser.jar -ig &lt;input-graph&gt;

* Create a colored dot file based on a given color map (for comparison).

> java -jar visualiser.jar -ig &lt;input-graph-file&gt; -cm &lt;color-map-file&gt;

* Calculate the edge cut for the given input graph.

> java -jar visualiser.jar -ig &lt;input-graph-file&gt; -ec

* Read input with multiple values like `0 1 1 2 3 4 5` (index 2 is partition id)

> java -jar visualiser.jar -ig &lt;input-graph&gt; --edge-offset 3 
--partition-token-index 2


