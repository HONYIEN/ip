package kelore.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kelore.exception.KeloreInputException;

/** Tests the core task creation, mutation, and date-filtering behavior of {@link TaskList}. */
public class TaskListTest {
    @Test
    public void constructor_taskCollection_copiesTasksWithoutSharingList() {
        ArrayList<Task> initialTasks = new ArrayList<>(List.of(new Todo("first")));
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.add(new Todo("second"));

        assertEquals(List.of("T | 0 | first"), tasks.toStorageLines());
        assertNotSame(initialTasks, tasks.toStorageLines());
    }

    @Test
    public void constructor_nullListOrListContainingNull_assertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));

        ArrayList<Task> tasksContainingNull = new ArrayList<>();
        tasksContainingNull.add(null);
        assertThrows(AssertionError.class, () -> new TaskList(tasksContainingNull));
    }

    @Test
    public void addTodo_validInput_addsTodo() throws Exception {
        TaskList tasks = new TaskList();

        String response = tasks.addTodo("todo read a book");

        assertEquals(List.of("T | 0 | read a book"), tasks.toStorageLines());
        assertEquals("Got it. I've added this task:" + System.lineSeparator()
                + "      [T][ ] read a book" + System.lineSeparator()
                + "    Now you have 1 tasks in the list.", response);
    }

    @Test
    public void addTodo_emptyOrUnstorableDescription_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(KeloreInputException.class, () -> tasks.addTodo("todo   "));
        assertThrows(KeloreInputException.class, () -> tasks.addTodo("todo first | second"));
    }

    @Test
    public void addDeadline_validInput_addsDeadline() throws Exception {
        TaskList tasks = new TaskList();

        tasks.addDeadline("deadline submit report /by 2/9/2026 1805");

        assertEquals(List.of("D | 0 | submit report | 2026-09-02T18:05"),
                tasks.toStorageLines());
    }

    @Test
    public void addDeadline_missingPartsOrInvalidDate_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline("deadline submit report"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline("deadline /by 2/9/2026 1800"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline("deadline submit report /by"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline(
                        "deadline submit report /by 31/2/2026 1800"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline(
                        "deadline submit | report /by 2/9/2026 1800"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addDeadline(
                        "deadline submit report /by 2/9/2026 2400"));
    }

    @Test
    public void addDeadline_leapDay_addsDeadline() throws Exception {
        TaskList tasks = new TaskList();

        tasks.addDeadline("deadline celebrate /by 29/2/2028 0000");

        assertEquals(List.of("D | 0 | celebrate | 2028-02-29T00:00"),
                tasks.toStorageLines());
    }

    @Test
    public void addEvent_validInput_addsEvent() throws Exception {
        TaskList tasks = new TaskList();

        tasks.addEvent("event conference /from 2/9/2026 0900 /to 4/9/2026 1700");

        assertEquals(List.of(
                "E | 0 | conference | 2026-09-02T09:00 | 2026-09-04T17:00"),
                tasks.toStorageLines());
    }

    @Test
    public void addEvent_missingPartsOrInvalidDates_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /to 4/9/2026 1700"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /from 2/9/2026 0900"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event /from 2/9/2026 0900 /to 4/9/2026 1700"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /from /to 4/9/2026 1700"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /from 2/9/2026 0900 /to"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /from invalid /to 4/9/2026 1700"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference /from 2/9/2026 0900 /to invalid"));
        assertThrows(
                KeloreInputException.class, () -> tasks.addEvent(
                        "event conference | online /from 2/9/2026 0900 /to 2/9/2026 1000"));
    }

    @Test
    public void addEvent_endBeforeStart_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(KeloreInputException.class, () -> tasks.addEvent(
                "event conference /from 4/9/2026 1700 /to 2/9/2026 0900"));
    }

    @Test
    public void addEvent_sameStartAndEnd_addsEvent() throws Exception {
        TaskList tasks = new TaskList();

        tasks.addEvent("event reminder /from 2/9/2026 0900 /to 2/9/2026 0900");

        assertEquals(List.of("E | 0 | reminder | 2026-09-02T09:00 | 2026-09-02T09:00"),
                tasks.toStorageLines());
    }

    @Test
    public void markAndUnmark_validTaskNumber_updatesStoredStatus() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read a book");

        String markedResponse = tasks.mark(1);
        assertEquals(List.of("T | 1 | read a book"), tasks.toStorageLines());
        assertEquals("Nice! I've marked this task as done:" + System.lineSeparator()
                + "      [T][X] read a book", markedResponse);

        String unmarkedResponse = tasks.unmark(1);
        assertEquals(List.of("T | 0 | read a book"), tasks.toStorageLines());
        assertEquals("OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "      [T][ ] read a book", unmarkedResponse);
    }

    @Test
    public void markUnmarkDelete_outOfRangeTaskNumbers_exceptionThrown() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read a book");

        assertThrows(KeloreInputException.class, () -> tasks.mark(0));
        assertThrows(KeloreInputException.class, () -> tasks.unmark(2));
        assertThrows(KeloreInputException.class, () -> tasks.delete(-1));
    }

    @Test
    public void delete_validTaskNumber_removesOnlySelectedTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo first");
        tasks.addTodo("todo second");

        String response = tasks.delete(1);

        assertEquals(List.of("T | 0 | second"), tasks.toStorageLines());
        assertEquals("Noted. I've removed this task:" + System.lineSeparator()
                + "      [T][ ] first" + System.lineSeparator()
                + "    Now you have 1 tasks in the list.", response);
    }

    @Test
    public void display_emptyAndPopulatedList_formatsNumberedTasks() throws Exception {
        TaskList tasks = new TaskList();
        assertEquals("    Here are the tasks in your list:" + System.lineSeparator(),
                tasks.display());

        tasks.addTodo("todo first");
        tasks.addDeadline("deadline second /by 2/9/2026 1800");

        assertEquals("    Here are the tasks in your list:" + System.lineSeparator()
                + "    1.[T][ ] first" + System.lineSeparator()
                + "    2.[D][ ] second (by: Sep 2 2026, 6:00 PM)"
                + System.lineSeparator(), tasks.display());
    }

    @Test
    public void displayTasksOn_matchingDate_includesDeadlinesAndSpanningEventsOnly()
            throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo undated task");
        tasks.addDeadline("deadline submit report /by 3/9/2026 1800");
        tasks.addEvent("event conference /from 2/9/2026 0900 /to 4/9/2026 1700");

        String output = tasks.displayTasksOn("on 3/9/2026");

        assertTrue(output.contains("submit report"));
        assertTrue(output.contains("conference"));
        assertFalse(output.contains("undated task"));
    }

    @Test
    public void displayTasksOn_noMatches_reportsNoMatchingTasks() throws Exception {
        TaskList tasks = new TaskList();

        assertTrue(tasks.displayTasksOn("on 1/1/2026").contains("No matching tasks."));
    }

    @Test
    public void displayTasksOn_validDate_formatsHeadingAndRenumbersMatches() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo first");
        tasks.addDeadline("deadline second /by 2/9/2026 1800");

        assertEquals("    Here are the deadlines and events on Sep 2 2026:"
                + System.lineSeparator()
                + "    1.[D][ ] second (by: Sep 2 2026, 6:00 PM)"
                + System.lineSeparator(), tasks.displayTasksOn("on 2/9/2026"));
    }

    @Test
    public void displayTasksOn_invalidDate_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(
                KeloreInputException.class, () -> tasks.displayTasksOn("on 31/2/2026"));
        assertThrows(KeloreInputException.class, () -> tasks.displayTasksOn("on"));
    }

    @Test
    public void find_matchingKeyword_returnsOnlyMatchingTasksWithMatchNumbers() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read book");
        tasks.addTodo("todo buy groceries");
        tasks.addDeadline("deadline return book /by 6/6/2026 1800");

        String output = tasks.find("find book");

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
        assertFalse(output.contains("buy groceries"));
    }

    @Test
    public void find_noMatchingKeyword_reportsNoMatchingTasks() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read book");

        assertTrue(tasks.find("find pen").contains("No matching tasks."));
    }

    @Test
    public void find_typoWithoutExactMatches_returnsCloseMatches() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read book");
        tasks.addTodo("todo buy groceries");
        tasks.addDeadline("deadline return book /by 6/6/2026 1800");

        String output = tasks.find("find bok");

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
        assertFalse(output.contains("buy groceries"));
    }

    @Test
    public void find_exactAndCloseMatches_returnsExactMatchesOnly() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo book flight");
        tasks.addTodo("todo learn to cook");

        String output = tasks.find("find book");

        assertTrue(output.contains("book flight"));
        assertFalse(output.contains("learn to cook"));
    }

    @Test
    public void find_differentCase_fallsBackToCaseInsensitiveCloseMatch() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTodo("todo Read book");

        String output = tasks.find("find read");

        assertTrue(output.contains("Read book"));
    }

    @Test
    public void find_emptyKeyword_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(KeloreInputException.class, () -> tasks.find("find"));
    }
}
