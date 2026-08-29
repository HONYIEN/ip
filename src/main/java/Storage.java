import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves Kelore tasks in a human-readable text file. */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /** Creates storage that reads from and writes to the given project-relative path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks, or returns an empty task list if the data file does not exist.
     */
    public Kelore.TaskList load() throws IOException {
        if (!Files.exists(filePath)) {
            return new Kelore.TaskList();
        }

        ArrayList<Kelore.Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                tasks.add(parseTask(line, i + 1));
            }
        }
        return new Kelore.TaskList(tasks);
    }

    /** Saves the complete task list, creating the data directory when necessary. */
    public void save(Kelore.TaskList taskList) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.write(filePath, taskList.toStorageLines());
    }

    /** Converts one validated storage line into its corresponding task subtype. */
    private Kelore.Task parseTask(String line, int lineNumber) throws IOException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw corruptedFileError(lineNumber);
        }

        Kelore.Task task;
        switch (fields[0]) {
        case "T":
            requireFieldCount(fields, 3, lineNumber);
            task = new Kelore.ToDos(fields[2]);
            break;
        case "D":
            requireFieldCount(fields, 4, lineNumber);
            task = new Kelore.Deadline(fields[2], parseDateTime(fields[3], lineNumber));
            break;
        case "E":
            requireFieldCount(fields, 5, lineNumber);
            LocalDateTime from = parseDateTime(fields[3], lineNumber);
            LocalDateTime to = parseDateTime(fields[4], lineNumber);
            if (to.isBefore(from)) {
                throw corruptedFileError(lineNumber);
            }
            task = new Kelore.Event(fields[2], from, to);
            break;
        default:
            throw corruptedFileError(lineNumber);
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw corruptedFileError(lineNumber);
        }
        return task;
    }

    /** Parses an ISO date-time stored in the data file. */
    private LocalDateTime parseDateTime(String value, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw corruptedFileError(lineNumber);
        }
    }

    /** Ensures a stored task contains exactly the fields required by its task type. */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber)
            throws IOException {
        if (fields.length != expectedCount) {
            throw corruptedFileError(lineNumber);
        }
    }

    /** Creates a consistent error for malformed data while identifying its location. */
    private IOException corruptedFileError(int lineNumber) {
        return new IOException("The data file is corrupted at line " + lineNumber + ".");
    }

    /** Joins storage fields using the delimiter used by {@link #parseTask(String, int)}. */
    public static String joinFields(String... fields) {
        return String.join(FIELD_SEPARATOR, fields);
    }
}
