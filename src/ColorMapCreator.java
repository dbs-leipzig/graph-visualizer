import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by gomezk on 05.12.14.
 */
public class ColorMapCreator {

    private static final String DOT_DIGRAPH_HEADER = "digraph";
    private static final String DOT_BLOCK_OPEN = "{";
    private static final String DOT_BLOCK_CLOSE = "}";
    private static final String DOT_OUT_EDGE = "->";
    private static final String DOT_FILLCOLOR_OPEN = "[fillcolor =\"";
    private static final String DOT_FILLCOLOR_CLOSE = "\"]";
    private static final String DOT_LINE_ENDING = ";";

    private Map<Integer, String> colorMap;
    private Map<Integer, List<Integer>> graph;
    private boolean created = false;
    private boolean matched = false;

    private Pattern LINE_TOKEN_SEPARATOR;

    private BufferedWriter fileWriter;

    public ColorMapCreator() {
        this.colorMap = new HashMap<>();
        this.graph = new HashMap<>();
    }


    public void readGraph(String path, String pattern) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader((path)));
        this.LINE_TOKEN_SEPARATOR = Pattern.compile(pattern);


        String line;
        while ((line = br.readLine()) != null) {
            String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);

            int ID = Integer.parseInt(lineTokens[0]);
            //System.out.println("ID " + ID);
            List<Integer> edges = new ArrayList<>();

            for (int i = 3; i < lineTokens.length; i++) {
                //System.out.println(lineTokens[i]);
                edges.add(Integer.parseInt(lineTokens[i]));
            }
            graph.put(ID, edges);

        }
    }


    public void createColorMap(String destpath) throws IOException {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(destpath + "_cm"));

        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            if (!colorMap.containsKey(entry.getKey())) {


                Random randomGenerator = new Random();
                int r = randomGenerator.nextInt(250);
                int g = randomGenerator.nextInt(250);
                int b = randomGenerator.nextInt(250);
                String color = Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b);


                colorMap.put(entry.getKey(), color);
            }
        }

        this.created = true;


        if (colorMap.isEmpty()) {
            System.out.println("graph is empty");
        } else {
            for (Map.Entry<Integer, String> entry : colorMap.entrySet()) {
                fileWriter.write(String.format("%s"+LINE_TOKEN_SEPARATOR+"%s",
                        entry.getKey(),
                        entry.getValue()


                ));
                fileWriter.newLine();
            }
        }
        fileWriter.close();

    }

    public void createDot(String destpath) throws IOException {

        if(this.matched){
            this.fileWriter = new BufferedWriter(new FileWriter(destpath + "_matched.dot"));
        }else{
            this.fileWriter = new BufferedWriter(new FileWriter(destpath + ".dot"));
        }



        if (graph.isEmpty()) {
            System.out.println("Map is empty!");
        } else {
            // digraph header
            fileWriter.write(String.format("%s %s %s", DOT_DIGRAPH_HEADER, "dg", DOT_BLOCK_OPEN));
            fileWriter.newLine();

            for (Map.Entry<Integer, List<Integer>> graphEntry : graph.entrySet()) {


                // nodes
                writeNodes(graphEntry.getKey(), destpath);


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

    }

    private void writeNodes(int node, String destpath) throws IOException {

        if (this.created) {
            fileWriter.write(String.format("\t%s %s%s%s%s",
                    node,

                    DOT_FILLCOLOR_OPEN,
                    colorMap.get(node),
                    DOT_FILLCOLOR_CLOSE,


                    DOT_LINE_ENDING));
            fileWriter.newLine();
        } else {
            BufferedReader br = new BufferedReader(new FileReader((destpath + "_cm")));

            String line;
            while ((line = br.readLine()) != null) {
                String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);

                int ID = Integer.parseInt(lineTokens[0]);
                //System.out.println("ID " + ID);
                String color = lineTokens[1];


                colorMap.put(ID, color);

            }

            fileWriter.write(String.format("\t%s %s %s %s%s",
                    node,

                    DOT_FILLCOLOR_OPEN,
                    colorMap.get(node),
                    DOT_FILLCOLOR_CLOSE,


                    DOT_LINE_ENDING));
            fileWriter.newLine();


        }


    }

    private void writeEdges(int key, List<Integer> nodes) throws IOException {
        for (int nodeID : nodes) {

            // writes
            // 0 -> 1 [label="edgeLabel"];
            fileWriter.write(String.format("\t%s%s%s%s",
                    key,
                    DOT_OUT_EDGE,
                    nodeID,
                    DOT_LINE_ENDING));
            fileWriter.newLine();

        }
    }

    public void matchGraph(String input, String destpath) throws IOException {

        this.matched=true;

        BufferedReader br = new BufferedReader(new FileReader((destpath + "_cm")));
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

        BufferedReader br2 = new BufferedReader(new FileReader(input));

        String line2;
        while ((line2 = br2.readLine()) != null) {
            String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line2);
            int ID = Integer.parseInt(lineTokens[0]);
            int value = Integer.parseInt(lineTokens[1]);
            output.put(ID, value);
        }
        br2.close();

        for(Map.Entry<Integer,Integer> entry: output.entrySet()){
            colorMap.put(entry.getKey(), oldCM.get(entry.getValue()));
        }

        this.created=true;

    }
}
