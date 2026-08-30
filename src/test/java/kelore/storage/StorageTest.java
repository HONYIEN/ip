package kelore.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kelore.task.TaskList;

/** Tests persistence and validation of Kelore's task data file. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load().toStorageLines());
    }

    @Test
    public void saveThenLoad_allTaskTypesAndStatuses_preservesTasks() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.addTodo("todo read a book");
        original.addDeadline("deadline submit report /by 2/9/2026 1800");
        original.addEvent("event conference /from 3/9/2026 0900 /to 4/9/2026 1700");
        original.mark(2);

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(original.toStorageLines(), loaded.toStorageLines());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("", "T | 0 | read a book", "   "));

        TaskList loaded = new Storage(file).load();

        assertEquals(List.of("T | 0 | read a book"), loaded.toStorageLines());
    }

    @Test
    public void load_corruptedRecords_throwsIOExceptionWithLineNumber() throws Exception {
        List<String> corruptedRecords = List.of(
                "T | 0",
                "T | 0 | description | extra",
                "D | 0 | description | invalid-date",
                "E | 0 | description | 2026-09-04T10:00 | 2026-09-03T10:00",
                "X | 0 | description",
                "T | maybe | description");

        for (int i = 0; i < corruptedRecords.size(); i++) {
            Path file = temporaryDirectory.resolve("corrupted-" + i + ".txt");
            Files.writeString(file, System.lineSeparator() + corruptedRecords.get(i));

            IOException exception = assertThrows(IOException.class,
                    () -> new Storage(file).load());
            assertEquals("The data file is corrupted at line 2.", exception.getMessage());
        }
    }
}
