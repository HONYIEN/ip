package kelore.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import kelore.task.Deadline;
import kelore.task.Event;
import kelore.task.Task;
import kelore.task.TaskList;
import kelore.task.Todo;

/** Loads and saves Kelore tasks in a human-readable text file. */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    public TaskList load() throws IOException {
        if (!Files.exists(filePath)) {
            return new TaskList();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                tasks.add(parseTask(lines.get(i), i + 1));
            }
        }
        return new TaskList(tasks);
    }

    public void save(TaskList taskList) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.write(filePath, taskList.toStorageLines());
    }

    private Task parseTask(String line, int lineNumber) throws IOException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw corruptedFileError(lineNumber);
        }
        Task task;
        switch (fields[0]) {
        case "T":
            requireFieldCount(fields, 3, lineNumber);
            task = new Todo(fields[2]);
            break;
        case "D":
            requireFieldCount(fields, 4, lineNumber);
            task = new Deadline(fields[2], parseDateTime(fields[3], lineNumber));
            break;
        case "E":
            requireFieldCount(fields, 5, lineNumber);
            LocalDateTime from = parseDateTime(fields[3], lineNumber);
            LocalDateTime to = parseDateTime(fields[4], lineNumber);
            if (to.isBefore(from)) {
                throw corruptedFileError(lineNumber);
            }
            task = new Event(fields[2], from, to);
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

    private LocalDateTime parseDateTime(String value, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw corruptedFileError(lineNumber);
        }
    }

    private void requireFieldCount(String[] fields, int expected, int lineNumber)
            throws IOException {
        if (fields.length != expected) {
            throw corruptedFileError(lineNumber);
        }
    }

    private IOException corruptedFileError(int lineNumber) {
        return new IOException("The data file is corrupted at line " + lineNumber + ".");
    }

    /** Joins fields using the delimiter understood by the storage parser. */
    public static String joinFields(String... fields) {
        return String.join(FIELD_SEPARATOR, fields);
    }
}
