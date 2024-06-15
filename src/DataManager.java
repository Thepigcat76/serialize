import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    public static final String DATA_FILE = "data.txt";
    public static final DataManager INSTANCE = new DataManager();

    private final File file;

    private DataManager() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.file = file;
    }

    public void save(String id, int val) {
        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write(id + "\n" + val);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> load() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            Map<String, Integer> loaded = new HashMap<>();
            for (int i = 0; i < lines.size(); i += 2) {
                String line = lines.get(i);
                String nextLine = lines.get(i+1);
                loaded.put(line, Integer.parseInt(nextLine));
            }
            return loaded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
