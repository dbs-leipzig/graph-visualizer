import java.io.IOException;

/**
 * Created by gomezk on 05.12.14.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ColorMapCreator generator = new ColorMapCreator();
        String despath = "/home/gomezk/graphs/facebook/facebook_input";
        String input = "/home/gomezk/graphs/facebook/kway_output";
        generator.readGraph(despath,"\t");
//        generator.createColorMap(despath);
        generator.matchGraph(input, despath);
        generator.createDot(despath);
    }
}
