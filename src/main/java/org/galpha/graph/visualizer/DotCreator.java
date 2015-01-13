package org.galpha.graph.visualizer;

import java.io.*;
import java.util.*;
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
  private Map<Integer, String> colorMap;
  private Map<Integer, List<Integer>> graph;
  private boolean created = false;
  private boolean matched = false;
  private Pattern LINE_TOKEN_SEPARATOR;
  private BufferedWriter fileWriter;

  /**
   * Constructor
   */
  public DotCreator() {
    this.colorMap = new HashMap<>();
    this.graph = new HashMap<>();
  }

  /**
   * Method to read the given graph
   *
   * @param path    path to the given graph
   * @param pattern how the given graph is structured e.g {tab \t or space " "}
   * @throws IOException
   */
  public void readGraph(String path, String pattern) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader((path)));
    this.LINE_TOKEN_SEPARATOR = Pattern.compile(pattern);
    String line;
    while ((line = br.readLine()) != null) {
      String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
      int ID = Integer.parseInt(lineTokens[0]);
      List<Integer> edges = new ArrayList<>();
      for (int i = 3; i < lineTokens.length; i++) {
        edges.add(Integer.parseInt(lineTokens[i]));
      }
      graph.put(ID, edges);
    }
    System.out.println("Graph stored");
  }

  /**
   * Method to create a color map
   *
   * @param destpath destination path to the color_map file
   * @throws IOException
   */
  public void createColorMap(String destpath) throws IOException {
    BufferedWriter fileWriter =
      new BufferedWriter(new FileWriter(destpath + "_cm"));
    for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
      if (!colorMap.containsKey(entry.getKey())) {
        int r = nextInt(250);
        int g = nextInt(250);
        int b = nextInt(250);
        String color = Integer.toString(r) + "," + Integer.toString(g) + "," +
          Integer.toString(b);
        colorMap.put(entry.getKey(), color);
      }
    }
    this.created = true;
    if (colorMap.isEmpty()) {
      System.out.println("graph is empty");
    } else {
      for (Map.Entry<Integer, String> entry : colorMap.entrySet()) {
        fileWriter
          .write(String.format("%s\t%s", entry.getKey(), entry.getValue()));
        fileWriter.newLine();
      }
    }
    fileWriter.close();
    System.out.println("CM created");
  }

  /**
   * Write the .dot format of the given graph
   *
   * @param inputGraph destination path to the .dot file
   * @throws IOException
   */
  public void createDot(String inputGraph) throws IOException {
    if (this.matched) {
      this.fileWriter =
        new BufferedWriter(new FileWriter(inputGraph + "_matched.dot"));
    } else {
      this.fileWriter = new BufferedWriter(new FileWriter(inputGraph + ".dot"));
    }
    if (graph.isEmpty()) {
      System.out.println("Map is empty!");
      this.fileWriter.close();
    } else {
      // digraph header
      fileWriter.write(
        String.format("%s %s %s", DOT_DIGRAPH_HEADER, "dg", DOT_BLOCK_OPEN));
      fileWriter.newLine();
      for (Map.Entry<Integer, List<Integer>> graphEntry : graph.entrySet()) {
        // nodes
        writeNodes(graphEntry.getKey(), inputGraph);
      }
      for (Map.Entry<Integer, List<Integer>> graphEntry : graph.entrySet()) {
        //edges
        writeEdges(graphEntry.getKey(), graphEntry.getValue());
      }
      // digraph footer
      fileWriter.write(DOT_BLOCK_CLOSE);
      fileWriter.newLine();
    }
    fileWriter.close();
    System.out.println(".dot created");
  }

  /**
   * Writes the nodes
   *
   * @param node   vertex
   * @param cmPath path to the given color map
   * @throws IOException
   */
  private void writeNodes(int node, String cmPath) throws IOException {
    if (this.created) { //if the color map was created just now
      fileWriter.write(String
        .format("\t%s %s%s%s%s", node, DOT_FILL_COLOR_OPEN, colorMap.get(node),
          DOT_FILL_COLOR_CLOSE, DOT_LINE_ENDING));
      fileWriter.newLine();
    } else {//if the colorMap exist in a file
      BufferedReader br = new BufferedReader(new FileReader((cmPath + "_cm")));
      String line;
      while ((line = br.readLine()) != null) {
        String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
        int ID = Integer.parseInt(lineTokens[0]);
        String color = lineTokens[1];
        colorMap.put(ID, color);
      }
      //writes
      //0 [fillcolor ="r,g,b"];
      fileWriter.write(String
        .format("\t%s %s %s %s%s", node, DOT_FILL_COLOR_OPEN,
          colorMap.get(node), DOT_FILL_COLOR_CLOSE, DOT_LINE_ENDING));
      fileWriter.newLine();
    }
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
   * Matches the start graph and the calculated partitioning input
   *
   * @param partitionedInput partitioning
   * @param cmPath
   * @throws IOException
   */
  public void matchGraph(String graphInput, String pattern,
    String partitionedInput, String cmPath) throws IOException {
    readGraph(graphInput, pattern);
    this.matched = true;
    BufferedReader br = new BufferedReader(new FileReader((cmPath + "_cm")));
    Map<Integer, String> oldCM = new HashMap<>();
    String line;
    while ((line = br.readLine()) != null) {
      String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
      int ID = Integer.parseInt(lineTokens[0]);
      String color = lineTokens[1];
      oldCM.put(ID, color);
    }
    br.close();
    Map<Integer, Integer> output = new HashMap<>();
    BufferedReader br2 = new BufferedReader(new FileReader(partitionedInput));
    String line2;
    while ((line2 = br2.readLine()) != null) {
      String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line2);
      int ID = Integer.parseInt(lineTokens[0]);
      int value = Integer.parseInt(lineTokens[1]);
      output.put(ID, value);
    }
    br2.close();
    for (Map.Entry<Integer, Integer> entry : output.entrySet()) {
      colorMap.put(entry.getKey(), oldCM.get(entry.getValue()));
    }
    this.created = true;
  }

  /**
   * Method counts edges between different partitions
   *
   * @param partitionedGraph partitioned graph input
   * @throws IOException
   */
  public void calculateEdgeCut(String partitionedGraph) throws IOException {
    Map<Integer, Map<Integer, Integer>> edgeCut = new HashMap<>();
    Map<Integer, Integer> output = new HashMap<>();
    BufferedReader br2 = new BufferedReader(new FileReader(partitionedGraph));
    String line2;
    while ((line2 = br2.readLine()) != null) {
      String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line2);
      int ID = Integer.parseInt(lineTokens[0]);
      int value = Integer.parseInt(lineTokens[1]);
      output.put(ID, value);
    }
    br2.close();
    for (Map.Entry<Integer, List<Integer>> graphMap : graph.entrySet()) {
      int partition = output.get(graphMap.getKey());
      if (!edgeCut.containsKey(partition)) {
        Map<Integer, Integer> innerMap = new HashMap<>();
        innerMap.put(partition, 0);
        edgeCut.put(partition, innerMap);
      }
      for (Integer edgeTo : graphMap.getValue()) {
        int partitionTo = output.get(edgeTo);
        Map<Integer, Integer> innerMap = edgeCut.get(partition);
        if (innerMap.containsKey(partitionTo)) {
          innerMap.put(partitionTo, innerMap.get(partitionTo) + 1);
        } else {
          innerMap.put(partitionTo, 1);
        }
      }
    }
    int countEdges = 0;
    for (Map.Entry<Integer, Map<Integer, Integer>> cuts : edgeCut.entrySet()) {
      Map<Integer, Integer> innerMap = cuts.getValue();
      for (Map.Entry<Integer, Integer> inner : innerMap.entrySet()) {
        countEdges += inner.getValue();
        System.out.println(cuts.getKey() + " to " + inner.getKey() + " : " +
          inner.getValue());
      }
    }
    System.out.println("Edges: " + countEdges);
  }
}
