import java.io.IOException;

/**
 * Created by gomezk on 05.12.14.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ColorMapCreator generator = new ColorMapCreator();
        String input = "/home/galpha/graphs/facebook/facebook_input";
        String kway_input = "/home/galpha/graphs/facebook/kway_output";
        generator.readGraph(input,"\t");
//        generator.createColorMap(despath);
        generator.matchGraph(kway_input, input);
       generator.createDot(input);
          generator.countCutEdges(kway_input);
    }
}
