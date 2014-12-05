import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by gomezk on 05.12.14.
 */
public class ColorMapCreator {

    private Map<Integer, String> colorMap;

    public ColorMapCreator() {
        this.colorMap = new HashMap<Integer, String>();
    }


    public void createColorMap(String path, String pattern) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader((path)));
        Pattern LINE_TOKEN_SEPARATOR = Pattern.compile(pattern);


        String line;
        while ((line = br.readLine()) != null) {
            String[] lineTokens = LINE_TOKEN_SEPARATOR.split(line);
            if (!colorMap.containsKey(Integer.parseInt(lineTokens[0]))) {


                Random randomGenerator = new Random();
                int r = randomGenerator.nextInt(250);
                int g = randomGenerator.nextInt(250);
                int b = randomGenerator.nextInt(250);
                String color = Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b);


                colorMap.put(Integer.parseInt(lineTokens[0]), color);


            }


        }
    }
    public void printColorMap(String destpath) throws IOException {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(destpath));

        if (colorMap.isEmpty()) {
            System.out.println("colorMap ist leer");
        } else {
            for (Map.Entry<Integer,String> entry: colorMap.entrySet()) {
                fileWriter.write(String.format("%s %s",
                        entry.getKey(),
                        entry.getValue()


                ));
                fileWriter.newLine();
            }
        }
        fileWriter.close();

    }
}
