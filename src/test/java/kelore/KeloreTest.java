package kelore;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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

    @Test
    public void getResponseDetails_invalidCommand_marksResponseAsError() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(kelore.getResponseDetails("dance").isError());
    }

    @Test
    public void getResponseDetails_validCommand_marksResponseAsSuccessful() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertFalse(kelore.getResponseDetails("list").isError());
    }

    @Test
    public void getResponse_freeCommand_returnsFreeTimeUsingInjectedClock() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-21T01:00:00Z"), ZoneId.of("Asia/Singapore"));
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"), clock);

        String response = kelore.getResponse("free 4");

        assertTrue(response.contains("Sep 21 2026, 9:00 AM to 1:00 PM"));
    }
}
