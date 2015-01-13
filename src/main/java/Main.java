import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main Class to control the functions of the DotCreator Class
 * Create by Galpha
 */
public class Main {
  public static final String OPTION_HELP = "h";
  public static final String INPUT_GRAPH = "ig";
  public static final String PARTITIONED_GRAPH = "pg";
  public static final String OUTPUT_GRAPH = "og";
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
    OPTIONS.addOption(OUTPUT_GRAPH, "output-graph", true,
      "Path to output " + "Graph");
    OPTIONS.addOption(CREATE_COLOR_MAP, "create-color-map", false,
      "Create color-map for Graph output");
    OPTIONS.addOption(PATTERN, "pattern", true,
      "Needed Pattern to load the " + "Graph");
    OPTIONS.addOption(COUNT_EDGE_CUT, "count-edge-cut", false,
      "Count edge " + "cut between partitions");
    OPTIONS.addOption(CREATE_DOT, "create-dot", false,
      "Create .dot file of " + "the given graph");
    OPTIONS.addOption(CREATE_MATCHED_DOT, "create-dot", false, "Create " +
      "matched .dot file of the given graph and the partitioned graph");
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
    if (cmd.getArgs().length == 0 || cmd.hasOption(OPTION_HELP)) {
      printHelp();
    }
    String input = cmd.getOptionValue(INPUT_GRAPH);
    String inputCM = input;
    String pattern = cmd.getOptionValue(PATTERN);
    String partitionedGraph = cmd.getOptionValue(PARTITIONED_GRAPH);
    if (cmd.hasOption(CREATE_DOT)) {
      DotCreator dC = new DotCreator();
      dC.createColorMap(input);
      dC.createDot(input, pattern);
    }
    if (cmd.hasOption(CREATE_MATCHED_DOT)) {
      DotCreator dC = new DotCreator();
      dC.matchGraph(input, pattern, partitionedGraph, inputCM);
      dC.createDot(input, pattern);
    }
    if (cmd.hasOption(CREATE_COLOR_MAP)) {
      DotCreator dC = new DotCreator();
      dC.createColorMap(input);
    }
    if (cmd.hasOption(COUNT_EDGE_CUT)) {
      DotCreator dC = new DotCreator();
      dC.countCutEdges(input, pattern, partitionedGraph);
    }
    performSanityCheck(cmd);
  }

  /**
   * Prints a help menu for the defined options.
   */
  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Main.class.getName(), OPTIONS, true);
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
    if (!cmd.hasOption(OUTPUT_GRAPH)) {
      throw new IllegalArgumentException("Define output Path(-og)");
    }
  }
}
