package ceng.bim208;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.err.println("Usage: java App spamDir docIDs ...");
            return;
        }
        
        Path spamDir = Paths.get(args[0]);
        String docIDs = args[1];
        List<String> ids = Arrays.asList(docIDs.split(","));


        Map<Path, List<String>> map = new HashMap<>();
        ids.forEach(e -> {
            try {
                String fileName = e.replace("clueweb", "cw").substring(0, 11) + ".txt";
                Path path = spamDir.resolve(fileName);

                List<String> value = map.getOrDefault(path, new ArrayList<String>());
                value.add(e);
                map.put(path, value);
            } catch (IndexOutOfBoundsException ex) {
                // We just skip this one because it does not exists.
            }
        });


        Map<String, Integer> results = new HashMap<>();
        map.keySet().forEach(path-> {
            try {
                Files.lines(path)
                    .filter(l -> stringContainsItemFromList(l, map.get(path)))
                    .forEach(s -> results.put(s.substring(s.indexOf(' ') + 1), Integer.valueOf(s.substring(0, s.indexOf(' ')))));
            } catch (IOException ex) { 
                System.err.println("Error while reading file: " + path.toString());
            }
        });


        String resultStr = "";
        for (int i = 0; i < ids.size(); i++) {
            Integer res =  results.get(ids.get(i));
            resultStr += (res != null ? res:-1) + (i + 1 < ids.size() ? ",":"");
        }
            
        System.out.println(resultStr);
    }

    public static boolean stringContainsItemFromList(String inputStr, List<String> items) {
        return items.parallelStream().anyMatch(inputStr::contains);
    }
}
