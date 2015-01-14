package org.galpha.graph.visualizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Class to create .dot output of the given graph.
 * Create by Galpha
 */
public class DotCreator {
  private static final String DOT_DIGRAPH_HEADER = "digraph";
  private static final String DOT_BLOCK_OPEN = "{";
  private static final String DOT_BLOCK_CLOSE = "}";
  private static final String DOT_OUT_EDGE = "->";
  private static final String DOT_FILL_COLOR_OPEN = "[fillcolor =\"";
  private static final String DOT_FILL_COLOR_CLOSE = "\"]";
  private static final String DOT_LINE_ENDING = ";";

  /**
   * Used to separate partition id and color in color map file.
   */
  private static final Pattern COLOR_MAP_TOKEN_SEPARATOR =
    Pattern.compile("\t");

  private static final String COLOR_MAP_FILE_POSTFIX = "_cm";
  private static final String DOT_FILE_POSTFIX = ".dot";

  /**
   * In-memory adjacency list of the input graph.
   */
  private final Map<Integer, List<Integer>> graph;
  /**
   * Stores the RGP string for a partition.
   */
  private final Map<Integer, String> partitionColorMap;
  /**
   * Maps a vertex to a partition according to the input graph.
   */
  private final Map<Integer, Integer> vertexPartitionMap;
  /**
   * Contains all partition IDs.
   */
  private final Set<Integer> partitionIDs;

  private final String inputGraphFile;
  private final String separatorToken;

  private final int edgeOffset;
  private final int partitionTokenIndex;

  private Pattern LINE_TOKEN_SEPARATOR;
  private BufferedWriter fileWriter;

  /**
   * Creates a new DotCreater
   *
   * @param inputGraphFile path to the input graph
   * @param separatorToken separator token used in the input graph
   * @param edgeOffset     token index where the edge lists starts
   */
  public DotCreator(final String inputGraphFile, final String separatorToken,
    int edgeOffset, int partitionTokenIndex) {
    this.inputGraphFile = inputGraphFile;
    this.separatorToken = separatorToken;
    this.edgeOffset = edgeOffset;
    this.partitionTokenIndex = partitionTokenIndex;
    this.partitionColorMap = new HashMap<>();
    this.graph = new HashMap<>();
    this.partitionIDs = new HashSet<>();
    this.vertexPartitionMap = new HashMap<>();
  }

  /**
   * Method to read the input graph into memory.
   *
   * @throws IOException
   */
  public void readGraph() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(this.inputGraphFile));
    this.LINE_TOKEN_SEPARATOR = Pattern.compile(this.separatorToken);
    String line;
    while ((line = br.readLine()) != null) {
      String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
      // read vertex id
      int vertexID = Integer.parseInt(lineTokens[0]);
      // read vertex value (= partition id)
      int partitionID = Integer.parseInt(lineTokens[this.partitionTokenIndex]);
      partitionIDs.add(partitionID);
      vertexPartitionMap.put(vertexID, partitionID);
      // read edges
      List<Integer> edges = new ArrayList<>();
      for (int i = this.edgeOffset; i < lineTokens.length; i++) {
        edges.add(Integer.parseInt(lineTokens[i]));
      }
      graph.put(vertexID, edges);
    }
    System.out.println("Input graph loaded to memory.");
  }

  /**
   * Method to create and store a color map.
   *
   * @throws IOException
   */
  public void createColorMap() throws IOException {
    for (Integer partitionID : partitionIDs) {
      int r = nextInt(250);
      int g = nextInt(250);
      int b = nextInt(250);
      String color = Integer.toString(r) + "," + Integer.toString(g) + "," +
        Integer.toString(b);
      partitionColorMap.put(partitionID, color);
    }
    System.out.println("Color map created.");
    storeColorMap();
  }

  /**
   * Stores a color map into a file. Filename is created based on the input
   * graph filename and a _cm postfix.
   *
   * @throws IOException
   */
  private void storeColorMap() throws IOException {
    BufferedWriter fileWriter = new BufferedWriter(
      new FileWriter(this.inputGraphFile + COLOR_MAP_FILE_POSTFIX));


    for (Map.Entry<Integer, String> entry : partitionColorMap.entrySet()) {
      fileWriter
        .write(String.format("%s\t%s", entry.getKey(), entry.getValue()));
      fileWriter.newLine();
    }
    fileWriter.close();
    System.out.println("Color map stored.");
  }

  /**
   * Loads a color map from a given file.
   *
   * @param colorMapFile filename where color map is stored
   * @throws IOException
   */
  public void loadColorMap(final String colorMapFile) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(colorMapFile));
    String line;
    while ((line = br.readLine()) != null) {
      String tokens[] = COLOR_MAP_TOKEN_SEPARATOR.split(line);
      this.partitionColorMap.put(Integer.parseInt(tokens[0]), tokens[1]);
    }
    br.close();
    System.out.println("Color map loaded from file.");
  }


  /**
   * Write the .dot format of the given graph
   *
   * @throws IOException
   */
  public void createDot() throws IOException {
    this.fileWriter = new BufferedWriter(
      new FileWriter(this.inputGraphFile + DOT_FILE_POSTFIX));

    // digraph header
    fileWriter.write(
      String.format("%s %s %s", DOT_DIGRAPH_HEADER, "dg", DOT_BLOCK_OPEN));
    fileWriter.newLine();

    // vertices
    for (Map.Entry<Integer, List<Integer>> graphEntry : graph.entrySet()) {
      writeVertex(graphEntry.getKey());
    }
    // edges
    for (Map.Entry<Integer, List<Integer>> graphEntry : graph.entrySet()) {
      writeEdges(graphEntry.getKey(), graphEntry.getValue());
    }
    // digraph footer
    fileWriter.write(DOT_BLOCK_CLOSE);
    fileWriter.newLine();

    fileWriter.close();
    System.out.println(".dot created");
  }


  /**
   * Writes the nodes
   *
   * @param vertexID vertex
   * @throws IOException
   */
  private void writeVertex(int vertexID) throws IOException {
    fileWriter.write(String
      .format("\t%s %s%s%s%s", vertexID, DOT_FILL_COLOR_OPEN,
        partitionColorMap.get(vertexPartitionMap.get(vertexID)),
        DOT_FILL_COLOR_CLOSE, DOT_LINE_ENDING));
    fileWriter.newLine();
  }

  /**
   * Write edges
   *
   * @param key   Vertex ID
   * @param nodes all edges between vertexID and the nodes
   * @throws IOException
   */
  private void writeEdges(int key, List<Integer> nodes) throws IOException {
    for (int nodeID : nodes) {
      // writes
      // 0 -> 1;
      // 0 -> 2;
      fileWriter.write(String
        .format("\t%s%s%s%s", key, DOT_OUT_EDGE, nodeID, DOT_LINE_ENDING));
      fileWriter.newLine();
    }
  }

  /**
   * Method counts edges between different partitions
   *
   * @throws IOException
   */
  public void calculateEdgeCut() throws IOException {
    long edgeCut = 0;

    for (Map.Entry<Integer, List<Integer>> vertex : graph.entrySet()) {
      int vertexPartition = vertexPartitionMap.get(vertex.getKey());
      for (Integer neighbourID : vertex.getValue()) {
        if (vertexPartitionMap.get(neighbourID) != vertexPartition) {
          edgeCut++;
        }
      }
    }
    System.out.println("Edge cut: " + edgeCut);
  }
}
