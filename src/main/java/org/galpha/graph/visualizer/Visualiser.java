package org.galpha.graph.visualizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Visualiser {
  public static final String OPTION_HELP = "h";
  public static final String INPUT_GRAPH = "ig";
  public static final String COLOR_MAP = "cm";
  public static final String EDGE_OFFSET = "eo";
  public static final String PARTITION_TOKEN_INDEX = "pti";

  public static final String SEPARATOR_TOKEN = "st";
  public static final String COUNT_EDGE_CUT = "ec";

  public static final String DEFAULT_SEPARATOR_TOKEN = "\t";
  public static final int DEFAULT_EDGE_OFFSET = 2;
  public static final int DEFAULT_PARTITION_TOKEN_INDEX = 1;

  private static Options OPTIONS;

  static {
    OPTIONS = new Options();
    OPTIONS.addOption(OPTION_HELP, "help", false, "Help");
    OPTIONS.addOption(INPUT_GRAPH, "input-graph", true, "Path to input Graph");
    OPTIONS.addOption(COLOR_MAP, "color-map", true, "Path to color map");
    OPTIONS.addOption(EDGE_OFFSET, "edge-offset", true,
      "Token index in input line where the edge list starts (default: " +
        DEFAULT_EDGE_OFFSET + ")");
    OPTIONS.addOption(PARTITION_TOKEN_INDEX, "partition-token-index", true,
      "Token index of the partition id in an input line (default: " +
        DEFAULT_PARTITION_TOKEN_INDEX + ")");
    OPTIONS.addOption(SEPARATOR_TOKEN, "separator-token", true,
      "Value separator token used in input graph");
    OPTIONS.addOption(COUNT_EDGE_CUT, "edge-cut", false,
      "Calculate edge cut for input graph");
  }

  /**
   * main method to run the program
   *
   * @param args given args
   * @throws IOException
   * @throws ParseException
   */
  public static void main(String[] args) throws IOException, ParseException {
    CommandLineParser parser = new BasicParser();
    CommandLine cmd = parser.parse(OPTIONS, args);
    if (cmd.hasOption(OPTION_HELP)) {
      printHelp();
      System.exit(0);
    }
    performSanityCheck(cmd);

    String inputGraphFile = cmd.getOptionValue(INPUT_GRAPH);
    String separatorToken =
      cmd.hasOption(SEPARATOR_TOKEN) ? cmd.getOptionValue(SEPARATOR_TOKEN) :
        DEFAULT_SEPARATOR_TOKEN;
    int edgeOffset = cmd.hasOption(EDGE_OFFSET) ?
      Integer.parseInt(cmd.getOptionValue(EDGE_OFFSET)) : DEFAULT_EDGE_OFFSET;

    int partitionTokenIndex = cmd.hasOption(PARTITION_TOKEN_INDEX) ?
      Integer.parseInt(cmd.getOptionValue(PARTITION_TOKEN_INDEX)) :
      DEFAULT_PARTITION_TOKEN_INDEX;

    DotCreator dC = new DotCreator(inputGraphFile, separatorToken, edgeOffset,
      partitionTokenIndex);

    dC.readGraph();

    if (cmd.hasOption(COUNT_EDGE_CUT)) {
      dC.calculateEdgeCut();
      System.exit(0);
    }

    // create or load color map
    if (cmd.hasOption(COLOR_MAP)) {
      dC.loadColorMap(cmd.getOptionValue(COLOR_MAP));
    } else {
      dC.createColorMap();
    }
    // write colored dot output
    dC.createDot();
  }

  /**
   * Prints a help menu for the defined options.
   */
  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Visualiser.class.getName(), OPTIONS, false);
  }

  /**
   * Checks if the given arguments make sense.
   *
   * @param cmd command line
   */
  private static void performSanityCheck(final CommandLine cmd) {
    if (!cmd.hasOption(INPUT_GRAPH)) {
      throw new IllegalArgumentException("Define input Path(-ig)");
    }
  }
}
