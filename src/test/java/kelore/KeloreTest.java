package kelore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing performed by {@link Kelore}. */
public class KeloreTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenReload_persistsAddedTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kelore kelore = new Kelore(dataFile);

        kelore.getResponse("todo read a book");

        assertTrue(new Kelore(dataFile).getResponse("list").contains("read a book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsFriendlyError() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(kelore.getResponse("dance").startsWith("Oops!"));
    }
}
