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

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns tasks loaded from the data file, or an empty list if the file is absent.
     *
     * @return Tasks represented by the data file.
     * @throws IOException If the file cannot be read or contains corrupted data.
     */
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

    /**
     * Saves all tasks to the data file, creating its parent directory when needed.
     *
     * @param taskList Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(TaskList taskList) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.write(filePath, taskList.toStorageLines());
    }

    /**
     * Returns the task represented by one data-file record.
     *
     * @param line Record to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed task.
     * @throws IOException If the record is malformed.
     */
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

    /**
     * Returns a date and time parsed from its stored ISO-8601 representation.
     *
     * @param value Stored date and time to parse.
     * @param lineNumber One-based line number used in error messages.
     * @return Parsed date and time.
     * @throws IOException If the value is not a valid date and time.
     */
    private LocalDateTime parseDateTime(String value, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw corruptedFileError(lineNumber);
        }
    }

    /**
     * Ensures that a stored task record contains the expected number of fields.
     *
     * @param fields Fields in the stored record.
     * @param expected Required number of fields.
     * @param lineNumber One-based line number used in error messages.
     * @throws IOException If the field count differs from the expected count.
     */
    private void requireFieldCount(String[] fields, int expected, int lineNumber)
            throws IOException {
        if (fields.length != expected) {
            throw corruptedFileError(lineNumber);
        }
    }

    /**
     * Returns an exception identifying a corrupted data-file record.
     *
     * @param lineNumber One-based number of the corrupted line.
     * @return Exception describing the corrupted line.
     */
    private IOException corruptedFileError(int lineNumber) {
        return new IOException("The data file is corrupted at line " + lineNumber + ".");
    }

    /**
     * Returns fields joined using the delimiter understood by the storage parser.
     *
     * @param fields Fields to join.
     * @return Delimited storage record.
     */
    public static String joinFields(String... fields) {
        return String.join(FIELD_SEPARATOR, fields);
    }
}
