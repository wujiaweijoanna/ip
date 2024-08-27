import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Storage {
    private final String path;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HHmm");

    public Storage(String path) {
        this.path = path;
        File file = new File(path);
        file.getParentFile().mkdirs();
    }

    public void save(TaskList taskList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Task task : taskList.getTask()) {
                writer.write(task.saveString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error when saving tasks" + e.getMessage());
        }
    }

    public LocalDateTime setTime(String time) {
        try {
            return LocalDateTime.parse(time, formatter);
        } catch (Exception e) {
            System.out.println("Wrong format for the time: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Task> load() {
        ArrayList<Task> list = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return list;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split(" \\| ");
                if (parts.length < 3) {
                    System.out.println("Invalid record" + line);
                    continue;
                }
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                switch (type) {
                    case "T":
                        list.add(new Todo(description, isDone));
                        break;
                    case "D":
                        list.add(new Deadline(description, setTime(parts[3]), isDone));
                        break;
                    case "E":
                        list.add(new Event(description, setTime(parts[3]), setTime(parts[4]), isDone));
                    default:
                        System.out.println("Unknown task type" + type + line);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing files, pls add new tasks :)");
        } catch (Exception e) {
            System.out.println("File is corrupted");
        }
        return list;
    }
}
