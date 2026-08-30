package kelore.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;

import kelore.exception.KeloreInputException;

/** Stores and manages the user's tasks. */
public class TaskList {
    private static final String INDENTATION = "    ";
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu", Locale.ENGLISH);
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Creates an empty task list. */
    public TaskList() {
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Initial tasks to include.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    /**
     * Returns all tasks in the line-based format used by the data file.
     *
     * @return Serialized task records in list order.
     */
    public ArrayList<String> toStorageLines() {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toStorageString());
        }
        return lines;
    }

    /**
     * Parses and adds a to-do in the form {@code todo DESCRIPTION}.
     *
     * @param input Complete command containing the to-do details.
     * @return Displayable confirmation of the added to-do.
     * @throws KeloreInputException If the description is missing or cannot be stored.
     */
    public String addTodo(String input) throws KeloreInputException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KeloreInputException("The todo description cannot be empty.");
        }
        ensureFieldsCanBeStored(description);
        return addTask(new Todo(description));
    }

    /**
     * Parses and adds a deadline in the form {@code deadline DESCRIPTION /by DATE}.
     *
     * @param input Complete command containing the deadline details.
     * @return Displayable confirmation of the added deadline.
     * @throws KeloreInputException If required details are missing, invalid, or cannot be stored.
     */
    public String addDeadline(String input) throws KeloreInputException {
        String details = input.substring("deadline".length()).trim();
        int separatorIndex = details.indexOf("/by");
        if (separatorIndex < 0) {
            throw new KeloreInputException("Please specify the deadline using /by.");
        }
        String description = details.substring(0, separatorIndex).trim();
        String byText = details.substring(separatorIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new KeloreInputException("The deadline description cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new KeloreInputException("The deadline date/time cannot be empty.");
        }
        ensureFieldsCanBeStored(description);
        return addTask(new Deadline(description, parseDateTime(byText)));
    }

    /**
     * Parses and adds an event in the form {@code event DESCRIPTION /from START /to END}.
     *
     * @param input Complete command containing the event details.
     * @return Displayable confirmation of the added event.
     * @throws KeloreInputException If required details are missing, invalid, or cannot be stored.
     */
    public String addEvent(String input) throws KeloreInputException {
        String details = input.substring("event".length()).trim();
        int fromIndex = details.indexOf("/from");
        if (fromIndex < 0) {
            throw new KeloreInputException("Please specify the event start using /from.");
        }
        int toIndex = details.indexOf("/to", fromIndex + "/from".length());
        if (toIndex < 0) {
            throw new KeloreInputException("Please specify the event end using /to.");
        }
        String description = details.substring(0, fromIndex).trim();
        String fromText = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String toText = details.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new KeloreInputException("The event description cannot be empty.");
        }
        if (fromText.isEmpty()) {
            throw new KeloreInputException("The event start date/time cannot be empty.");
        }
        if (toText.isEmpty()) {
            throw new KeloreInputException("The event end date/time cannot be empty.");
        }
        ensureFieldsCanBeStored(description);
        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);
        if (to.isBefore(from)) {
            throw new KeloreInputException(
                    "The event end date/time cannot be before its start date/time.");
        }
        return addTask(new Event(description, from, to));
    }

    /**
     * Returns a date and time parsed from user input.
     *
     * @param text Date and time in {@code d/M/yyyy HHmm} format.
     * @return Parsed date and time.
     * @throws KeloreInputException If the text is not a valid date and time.
     */
    private LocalDateTime parseDateTime(String text) throws KeloreInputException {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new KeloreInputException(
                    "Please use a valid date and time in the format d/M/yyyy HHmm.");
        }
    }

    /**
     * Returns the deadlines and events occurring on a user-specified date.
     *
     * @param input Complete {@code on d/M/yyyy} command.
     * @return Displayable list of matching tasks.
     * @throws KeloreInputException If the date is missing or invalid.
     */
    public String displayTasksOn(String input) throws KeloreInputException {
        LocalDate date;
        try {
            date = LocalDate.parse(input.substring("on".length()).trim(), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new KeloreInputException("Please use a valid date in the format d/M/yyyy.");
        }
        StringBuilder output = new StringBuilder(INDENTATION)
                .append("Here are the deadlines and events on ")
                .append(date.format(DISPLAY_DATE_FORMAT)).append(":")
                .append(System.lineSeparator());
        int matchNumber = 1;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                output.append(INDENTATION).append(matchNumber).append(".").append(task)
                        .append(System.lineSeparator());
                matchNumber++;
            }
        }
        if (matchNumber == 1) {
            output.append(INDENTATION).append("No matching tasks.")
                    .append(System.lineSeparator());
        }
        return output.toString();
    }

    /**
     * Returns tasks whose descriptions contain the keyword in {@code find KEYWORD}, falling
     * back to close word matches only when there are no exact matches.
     */
    public String find(String input) throws KeloreInputException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KeloreInputException("The search keyword cannot be empty.");
        }

        ArrayList<Task> matches = findMatches(keyword, false);
        if (matches.isEmpty()) {
            matches = findMatches(keyword, true);
        }

        StringBuilder output = new StringBuilder(INDENTATION)
                .append("Here are the matching tasks in your list:")
                .append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            output.append(INDENTATION).append(i + 1).append(".").append(matches.get(i))
                    .append(System.lineSeparator());
        }
        if (matches.isEmpty()) {
            output.append(INDENTATION).append("No matching tasks.")
                    .append(System.lineSeparator());
        }
        return output.toString();
    }

    private ArrayList<Task> findMatches(String keyword, boolean allowCloseMatches) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            boolean isMatch = allowCloseMatches
                    ? task.containsCloseKeyword(keyword)
                    : task.containsKeyword(keyword);
            if (isMatch) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns tasks whose descriptions contain the keyword in {@code find KEYWORD}, falling
     * back to close word matches only when there are no exact matches.
     */
    public String find(String input) throws KeloreInputException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KeloreInputException("The search keyword cannot be empty.");
        }

        ArrayList<Task> matches = findMatches(keyword, false);
        if (matches.isEmpty()) {
            matches = findMatches(keyword, true);
        }

        StringBuilder output = new StringBuilder(INDENTATION)
                .append("Here are the matching tasks in your list:")
                .append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            output.append(INDENTATION).append(i + 1).append(".").append(matches.get(i))
                    .append(System.lineSeparator());
        }
        if (matches.isEmpty()) {
            output.append(INDENTATION).append("No matching tasks.")
                    .append(System.lineSeparator());
        }
        return output.toString();
    }

    private ArrayList<Task> findMatches(String keyword, boolean allowCloseMatches) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            boolean isMatch = allowCloseMatches
                    ? task.containsCloseKeyword(keyword)
                    : task.containsKeyword(keyword);
            if (isMatch) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Ensures that task fields do not contain the storage delimiter.
     *
     * @param fields Task fields to validate.
     * @throws KeloreInputException If a field contains the storage delimiter.
     */
    private void ensureFieldsCanBeStored(String... fields) throws KeloreInputException {
        for (String field : fields) {
            if (field.contains(" | ")) {
                throw new KeloreInputException("Task details cannot contain the text ' | '.");
            }
        }
    }

    /**
     * Adds a task and returns a confirmation message.
     *
     * @param task Task to add.
     * @return Displayable confirmation of the added task and new task count.
     */
    private String addTask(Task task) {
        tasks.add(task);
        return "Got it. I've added this task:" + System.lineSeparator()
                + INDENTATION + "  " + task + System.lineSeparator()
                + INDENTATION + "Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Marks the specified task as completed and returns a confirmation message.
     *
     * @param taskNumber One-based number of the task to mark.
     * @return Displayable confirmation of the updated task.
     * @throws KeloreInputException If no task has the specified number.
     */
    public String mark(int taskNumber) throws KeloreInputException {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return "Nice! I've marked this task as done:" + System.lineSeparator()
                + INDENTATION + "  " + task;
    }

    /**
     * Marks the specified task as incomplete and returns a confirmation message.
     *
     * @param taskNumber One-based number of the task to unmark.
     * @return Displayable confirmation of the updated task.
     * @throws KeloreInputException If no task has the specified number.
     */
    public String unmark(int taskNumber) throws KeloreInputException {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return "OK, I've marked this task as not done yet:" + System.lineSeparator()
                + INDENTATION + "  " + task;
    }

    /**
     * Deletes the specified task and returns a confirmation message.
     *
     * @param taskNumber One-based number of the task to delete.
     * @return Displayable confirmation of the deleted task and new task count.
     * @throws KeloreInputException If no task has the specified number.
     */
    public String delete(int taskNumber) throws KeloreInputException {
        getTask(taskNumber);
        Task removedTask = tasks.remove(taskNumber - 1);
        return "Noted. I've removed this task:" + System.lineSeparator()
                + INDENTATION + "  " + removedTask + System.lineSeparator()
                + INDENTATION + "Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Returns the task with the specified one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Task with the specified number.
     * @throws KeloreInputException If no task has the specified number.
     */
    private Task getTask(int taskNumber) throws KeloreInputException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new KeloreInputException("There is no task with that number.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns a displayable numbered list of all tasks.
     *
     * @return Numbered task list.
     */
    public String display() {
        StringBuilder output = new StringBuilder(INDENTATION)
                .append("Here are the tasks in your list:").append(System.lineSeparator());
        for (int i = 0; i < tasks.size(); i++) {
            output.append(INDENTATION).append(i + 1).append(".").append(tasks.get(i))
                    .append(System.lineSeparator());
        }
        return output.toString();
    }
}
