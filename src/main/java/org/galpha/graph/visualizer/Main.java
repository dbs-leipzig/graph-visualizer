package org.galpha.graph.visualizer;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * org.galpha.graph.visualizer.Main Class to control the functions of the org
 * .galpha.graph.visualizer.DotCreator Class
 * Create by Galpha
 */
public class Main {
  public static final String OPTION_HELP = "h";
  public static final String INPUT_GRAPH = "ig";
  public static final String PARTITIONED_GRAPH = "pg";
  public static final String CREATE_COLOR_MAP = "cm";
  public static final String PATTERN = "p";
  public static final String COUNT_EDGE_CUT = "ec";
  public static final String CREATE_DOT = "cd";
  public static final String CREATE_MATCHED_DOT = "cmd";
  private static Options OPTIONS;

  static {
    OPTIONS = new Options();
    OPTIONS.addOption(OPTION_HELP, "help", false, "Help");
    OPTIONS.addOption(INPUT_GRAPH, "input-graph", true, "Path to input Graph");
    OPTIONS.addOption(PARTITIONED_GRAPH, "partitioned-graph", true,
      "Path to " + "partitioned Graph");
    OPTIONS.addOption(CREATE_COLOR_MAP, "create-color-map", false,
      "Create color-map of the given graph e.g. -cm -ig <graph-input> -p " +
        "<pattern>");
    OPTIONS.addOption(PATTERN, "pattern", true,
      "Needed Pattern to load the " + "Graph");
    OPTIONS.addOption(COUNT_EDGE_CUT, "count-edge-cut", false,
      "Count edge " + "cut between partitions e.g. -ec -ig <graph-input> -p " +
        "<pattern> -pg <partitioned-graph>");
    OPTIONS.addOption(CREATE_DOT, "create-dot", false,
      "Create .dot file of the given graph e.g. -cd -ig <graph-input> -p " +
        "<pattern>");
    OPTIONS.addOption(CREATE_MATCHED_DOT, "create-dot", false, "Create " +
      "matched .dot file of the given graph and the partitioned graph e.g. " +
      "-cmd -ig <graph-input> -p <pattern> -pg <partitioned-graph>");
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
    }
    String input = cmd.getOptionValue(INPUT_GRAPH);
    String pattern = cmd.getOptionValue(PATTERN);
    String partitionedGraph = cmd.getOptionValue(PARTITIONED_GRAPH);
    if (cmd.hasOption(CREATE_DOT)) {
      performSanityCheck(cmd);
      DotCreator dC = new DotCreator();
      dC.readGraph(input, pattern);
      dC.createColorMap(input);
      dC.createDot(input);
    }
    if (cmd.hasOption(CREATE_MATCHED_DOT)) {
      performSanityCheck(cmd);
      DotCreator dC = new DotCreator();
      dC.matchGraph(input, pattern, partitionedGraph, input);
      dC.createDot(input);
    }
    if (cmd.hasOption(CREATE_COLOR_MAP)) {
      performSanityCheck(cmd);
      DotCreator dC = new DotCreator();
      dC.readGraph(input, pattern);
      dC.createColorMap(input);
    }
    if (cmd.hasOption(COUNT_EDGE_CUT)) {
      performSanityCheck(cmd);
      DotCreator dC = new DotCreator();
      dC.readGraph(input,pattern);
      dC.calculateEdgeCut(partitionedGraph);
    }
  }

  /**
   * Prints a help menu for the defined options.
   */
  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Main.class.getName(), OPTIONS, false);
  }

  /**
   * Checks if the given arguments make sense.
   *
   * @param cmd command line
   */
  private static void performSanityCheck(final CommandLine cmd) {
    if (cmd.hasOption(CREATE_DOT) || cmd.hasOption(CREATE_COLOR_MAP)) {
      if (!cmd.hasOption(INPUT_GRAPH)) {
        throw new IllegalArgumentException("Define input Path(-ig)");
      }
      if (!cmd.hasOption(PATTERN)) {
        throw new IllegalArgumentException("Define pattern of the given graph");
      }
    } else if (cmd.hasOption(CREATE_MATCHED_DOT) ||
      cmd.hasOption(COUNT_EDGE_CUT)) {
      if (!cmd.hasOption(INPUT_GRAPH)) {
        throw new IllegalArgumentException("Define input Path(-ig)");
      }
      if (!cmd.hasOption(PATTERN)) {
        throw new IllegalArgumentException("Define pattern of the given graph");
      }
      if ((!cmd.hasOption(PARTITIONED_GRAPH))) {
        throw new IllegalArgumentException("Define input partitioned graph");
      }
    }
  }
}
