package kelore.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kelore.exception.KeloreInputException;

/** Stores and manages the user's tasks. */
public class TaskList {
    private static final String INDENTATION = "    ";
    private static final LocalTime FREE_TIME_START = LocalTime.of(8, 0);
    private static final LocalTime FREE_TIME_END = LocalTime.of(18, 0);
    private static final int FREE_TIME_HOURS_PER_DAY = 10;
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter
            .ofPattern("h:mm a", Locale.ENGLISH);
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
        assert tasks != null : "The initial task list must not be null";
        assert !tasks.contains(null) : "The initial task list must not contain null tasks";
        this.tasks.addAll(tasks);
    }

    /**
     * Returns all tasks in the line-based format used by the data file.
     *
     * @return Serialized task records in list order.
     */
    public ArrayList<String> toStorageLines() {
        return tasks.stream()
                .map(Task::toStorageString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns an independent copy of this task list and all its tasks.
     *
     * @return Deep copy of this task list.
     */
    public TaskList copy() {
        TaskList copiedTaskList = new TaskList();
        for (Task task : tasks) {
            copiedTaskList.tasks.add(task.copy());
        }
        return copiedTaskList;
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

        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        String heading = "Here are the deadlines and events on "
                + date.format(DISPLAY_DATE_FORMAT) + ":";
        return formatMatchingTasks(heading, matches);
    }

    /**
     * Returns the earliest free interval of the requested length between 8:00 AM and 6:00 PM.
     * Only incomplete events block time. Searches start today unless {@code /from} supplies a
     * start date, and a search involving today starts at the next whole minute.
     *
     * @param input Complete {@code free HOURS [/from d/M/yyyy]} command.
     * @param now Current local date and time used as the lower search boundary.
     * @return Displayable description of the earliest matching interval.
     * @throws KeloreInputException If the duration or optional starting date is invalid.
     */
    public String findFreeTime(String input, LocalDateTime now) throws KeloreInputException {
        assert now != null : "The current date and time must not be null";
        FreeTimeQuery query = parseFreeTimeQuery(input, now.toLocalDate());
        Duration requestedDuration = Duration.ofHours(query.hours());
        LocalDate date = query.startDate();
        LocalDateTime roundedNow = roundUpToMinute(now);

        while (true) {
            LocalDateTime searchStart = date.atTime(FREE_TIME_START);
            if (date.equals(now.toLocalDate()) && roundedNow.isAfter(searchStart)) {
                searchStart = roundedNow;
            }
            LocalDateTime searchEnd = date.atTime(FREE_TIME_END);
            LocalDateTime result = findFreeTimeOnDate(searchStart, searchEnd, requestedDuration);
            if (result != null) {
                LocalDateTime resultEnd = result.plus(requestedDuration);
                return "The nearest " + query.hours() + "-hour free slot is "
                        + result.format(DISPLAY_DATE_TIME_FORMAT) + " to "
                        + resultEnd.format(DISPLAY_TIME_FORMAT) + ".";
            }
            date = date.plusDays(1);
        }
    }

    private FreeTimeQuery parseFreeTimeQuery(String input, LocalDate today)
            throws KeloreInputException {
        String details = input.substring("free".length()).trim();
        int fromIndex = details.indexOf("/from");
        if (fromIndex >= 0
                && details.indexOf("/from", fromIndex + "/from".length()) >= 0) {
            throw new KeloreInputException("Please specify /from at most once.");
        }

        String hoursText = fromIndex < 0 ? details : details.substring(0, fromIndex).trim();
        int hours;
        try {
            hours = Integer.parseInt(hoursText);
        } catch (NumberFormatException e) {
            throw new KeloreInputException(
                    "Please specify the duration as a positive whole number of hours.");
        }
        if (hours <= 0 || hours > FREE_TIME_HOURS_PER_DAY) {
            throw new KeloreInputException(
                    "The duration must be between 1 and 10 whole hours.");
        }

        LocalDate startDate = today;
        if (fromIndex >= 0) {
            String dateText = details.substring(fromIndex + "/from".length()).trim();
            try {
                startDate = LocalDate.parse(dateText, INPUT_DATE_FORMAT);
            } catch (DateTimeParseException e) {
                throw new KeloreInputException(
                        "Please use a valid /from date in the format d/M/yyyy.");
            }
            if (startDate.isBefore(today)) {
                throw new KeloreInputException("The /from date cannot be before today.");
            }
        }
        return new FreeTimeQuery(hours, startDate);
    }

    private LocalDateTime roundUpToMinute(LocalDateTime dateTime) {
        LocalDateTime rounded = dateTime.withSecond(0).withNano(0);
        if (dateTime.getSecond() > 0 || dateTime.getNano() > 0) {
            rounded = rounded.plusMinutes(1);
        }
        return rounded;
    }

    private LocalDateTime findFreeTimeOnDate(LocalDateTime searchStart,
            LocalDateTime searchEnd, Duration requestedDuration) {
        if (!searchStart.isBefore(searchEnd)) {
            return null;
        }

        ArrayList<TimeInterval> busyIntervals = getBusyIntervals(searchStart, searchEnd);
        LocalDateTime cursor = searchStart;
        for (TimeInterval interval : busyIntervals) {
            if (Duration.between(cursor, interval.start()).compareTo(requestedDuration) >= 0) {
                return cursor;
            }
            if (interval.end().isAfter(cursor)) {
                cursor = interval.end();
            }
        }
        if (Duration.between(cursor, searchEnd).compareTo(requestedDuration) >= 0) {
            return cursor;
        }
        return null;
    }

    private ArrayList<TimeInterval> getBusyIntervals(LocalDateTime searchStart,
            LocalDateTime searchEnd) {
        ArrayList<TimeInterval> intervals = new ArrayList<>();
        for (Task task : tasks) {
            if (!(task instanceof Event event) || task.isDone) {
                continue;
            }
            LocalDateTime clippedStart = event.from.isAfter(searchStart)
                    ? event.from : searchStart;
            LocalDateTime clippedEnd = event.to.isBefore(searchEnd) ? event.to : searchEnd;
            if (clippedEnd.isAfter(clippedStart)) {
                intervals.add(new TimeInterval(clippedStart, clippedEnd));
            }
        }
        intervals.sort(Comparator.comparing(TimeInterval::start));
        return mergeIntervals(intervals);
    }

    private ArrayList<TimeInterval> mergeIntervals(ArrayList<TimeInterval> intervals) {
        ArrayList<TimeInterval> mergedIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            if (mergedIntervals.isEmpty()) {
                mergedIntervals.add(interval);
                continue;
            }
            int lastIndex = mergedIntervals.size() - 1;
            TimeInterval previous = mergedIntervals.get(lastIndex);
            if (interval.start().isAfter(previous.end())) {
                mergedIntervals.add(interval);
            } else if (interval.end().isAfter(previous.end())) {
                mergedIntervals.set(lastIndex,
                        new TimeInterval(previous.start(), interval.end()));
            }
        }
        return mergedIntervals;
    }

    private record FreeTimeQuery(int hours, LocalDate startDate) {
    }

    private record TimeInterval(LocalDateTime start, LocalDateTime end) {
    }

    /**
     * Returns tasks whose descriptions contain the keyword in {@code find KEYWORD}, falling
     * back to close word matches only when there are no exact matches.
     *
     * @param input Complete command containing the keyword.
     * @return Displayable list of matching tasks.
     * @throws KeloreInputException If the keyword is empty.
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

        return formatMatchingTasks("Here are the matching tasks in your list:", matches);
    }

    private ArrayList<Task> findMatches(String keyword, boolean allowCloseMatches) {
        return tasks.stream()
                .filter(task -> allowCloseMatches
                        ? task.containsCloseKeyword(keyword)
                        : task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
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
        assert task != null : "The task to add must not be null";
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Adding a task must increase the task count";
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
        assert taskNumber - 1 >= 0 && taskNumber - 1 < tasks.size()
                : "A validated task number must map to an existing list index";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns a displayable numbered list of all tasks.
     *
     * @return Numbered task list.
     */
    public String display() {
        return formatNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns a heading and numbered task list, with a message when there are no matches.
     *
     * @param heading Heading to show above the tasks.
     * @param matchingTasks Tasks that matched a query.
     * @return Displayable matching-task list.
     */
    private String formatMatchingTasks(String heading, List<Task> matchingTasks) {
        String output = formatNumberedTasks(heading, matchingTasks);
        if (matchingTasks.isEmpty()) {
            output += INDENTATION + "No matching tasks." + System.lineSeparator();
        }
        return output;
    }

    /**
     * Returns a heading followed by the supplied tasks as a numbered list.
     *
     * @param heading Heading to show above the tasks.
     * @param tasksToDisplay Tasks to number and display.
     * @return Displayable numbered task list.
     */
    private String formatNumberedTasks(String heading, List<Task> tasksToDisplay) {
        StringBuilder output = new StringBuilder(INDENTATION).append(heading)
                .append(System.lineSeparator());
        for (int i = 0; i < tasksToDisplay.size(); i++) {
            output.append(INDENTATION).append(i + 1).append(".").append(tasksToDisplay.get(i))
                    .append(System.lineSeparator());
        }
        return output.toString();
    }
}
