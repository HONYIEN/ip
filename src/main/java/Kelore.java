import java.util.Scanner;

/** Runs the Kelore task-tracking chatbot. */
public class Kelore {
    private static final String INDENTATION = "    ";
    private static final String DIVIDER = "_".repeat(60);

    /** Represents an invalid command or command argument entered by a Kelore user. */
    public static class KeloreInputError extends Exception {
        /** Creates an input error with a message that explains how to correct the input. */
        public KeloreInputError(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        String banner = " _  __ _____ _       ___  ____  _____\n"
                + "| |/ /| ____| |     / _ \\|  _ \\| ____|\n"
                + "| ' / |  _| | |    | | | | |_) |  _|\n"
                + "| . \\ | |___| |___ | |_| |  _ <| |___\n"
                + "|_|\\_\\|_____|_____| \\___/|_| \\_\\_____|";

        System.out.println(INDENTATION + DIVIDER);
        System.out.println(INDENTATION + banner.replace("\n", "\n" + INDENTATION));
        System.out.println(INDENTATION + "Hello! I'm Kelore.");
        System.out.println(INDENTATION + "What can I do for you?");
        System.out.println(INDENTATION + DIVIDER);

        Scanner scanner = new Scanner(System.in);
        TaskList taskList = new TaskList();
        while (true) {
            String input = scanner.nextLine();
            System.out.println(INDENTATION + DIVIDER);

            if ("bye".equals(input)) {
                System.out.println(INDENTATION + "Bye. Hope to see you again soon!");
                System.out.println(INDENTATION + DIVIDER);
                break;
            }

            try {
                if ("list".equals(input)) {
                    System.out.print(taskList.display());
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    System.out.println(INDENTATION + updateTaskStatus(taskList, input, true));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    System.out.println(INDENTATION + updateTaskStatus(taskList, input, false));
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    System.out.println(INDENTATION + taskList.addToDos(input));
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    System.out.println(INDENTATION + taskList.addDeadline(input));
                } else if (input.equals("event") || input.startsWith("event ")) {
                    System.out.println(INDENTATION + taskList.addEvent(input));
                } else {
                    throw new KeloreInputError("I don't recognise that command.");
                }
            } catch (KeloreInputError e) {
                System.out.println(INDENTATION + "Oops! " + e.getMessage());
            }
            System.out.println(INDENTATION + DIVIDER);
        }
        scanner.close();
    }

    /** Parses a task number and applies either the mark or unmark operation. */
    private static String updateTaskStatus(TaskList taskList, String input, boolean markAsDone)
            throws KeloreInputError {
        try {
            int taskNumber = Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
            return markAsDone ? taskList.mark(taskNumber) : taskList.unmark(taskNumber);
        } catch (NumberFormatException e) {
            throw new KeloreInputError("Please provide a valid task number after the command.");
        }
    }

    public static String echoString(String input) {
        return input;
    }

    /** Represents a task and whether it has been completed. */
    public static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "X" : " "); // mark done task with X
        }

        /** Marks this task as completed. */
        public void markAsDone() {
            isDone = true;
        }

        /** Marks this task as not completed. */
        public void markAsNotDone() {
            isDone = false;
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    /** Represents a task that must be completed by a specified date or time. */
    public static class Deadline extends Task {

        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }
    
    /** Represents a task without a date or time. */
    public static class ToDos extends Task {
        public ToDos(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    /** Represents a task that occurs between specified start and end times. */
    public static class Event extends Task {
        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    /** Stores tasks entered during the current program run. */
    public static class TaskList {
        private final Task[] tasks = new Task[100];
        private int taskCount = 0;

        /** Adds a task and returns the message to show the user. */
        public String add(String input) throws KeloreInputError {
            if (input.isBlank()) {
                throw new KeloreInputError("The task description cannot be empty.");
            }

            if (taskCount >= tasks.length) {
                throw new KeloreInputError("The task list is full.");
            }

            Task task = new Task(input);
            tasks[taskCount] = task;
            taskCount++;
            return "added: " + task;
        }

        /** Parses and adds a deadline in the form {@code deadline DESCRIPTION /by DATE}. */
        public String addDeadline(String input) throws KeloreInputError {
            String deadlineDetails = input.substring("deadline".length()).trim();
            int bySeparatorIndex = deadlineDetails.indexOf("/by");
            if (bySeparatorIndex < 0) {
                throw new KeloreInputError("Please specify the deadline using /by.");
            }

            String description = deadlineDetails.substring(0, bySeparatorIndex).trim();
            String by = deadlineDetails.substring(bySeparatorIndex + "/by".length()).trim();
            if (description.isEmpty()) {
                throw new KeloreInputError("The deadline description cannot be empty.");
            }
            if (by.isEmpty()) {
                throw new KeloreInputError("The deadline date/time cannot be empty.");
            }
            Task deadline = new Deadline(description, by);
            return addTask(deadline);
        }
        
        /** Parses and adds a to-do in the form {@code todo DESCRIPTION}. */
        public String addToDos(String input) throws KeloreInputError {
            String description = input.substring("todo".length()).trim();

            if (description.isEmpty()) {
                throw new KeloreInputError("The todo description cannot be empty.");
            }
            Task todoTask = new ToDos(description);
            return addTask(todoTask);
        }

        /** Parses and adds an event in the form {@code event DESCRIPTION /from START /to END}. */
        public String addEvent(String input) throws KeloreInputError {
            String eventDetails = input.substring("event".length()).trim();
            int fromSeparatorIndex = eventDetails.indexOf("/from");
            if (fromSeparatorIndex < 0) {
                throw new KeloreInputError("Please specify the event start using /from.");
            }

            int toSeparatorIndex = eventDetails.indexOf("/to", fromSeparatorIndex + "/from".length());
            if (toSeparatorIndex < 0) {
                throw new KeloreInputError("Please specify the event end using /to.");
            }

            String description = eventDetails.substring(0, fromSeparatorIndex).trim();
            String from = eventDetails.substring(
                    fromSeparatorIndex + "/from".length(), toSeparatorIndex).trim();
            String to = eventDetails.substring(toSeparatorIndex + "/to".length()).trim();
            if (description.isEmpty()) {
                throw new KeloreInputError("The event description cannot be empty.");
            }
            if (from.isEmpty()) {
                throw new KeloreInputError("The event start date/time cannot be empty.");
            }
            if (to.isEmpty()) {
                throw new KeloreInputError("The event end date/time cannot be empty.");
            }
            Task event = new Event(description, from, to);
            return addTask(event);
        }

        /** Stores a parsed task and creates the standard confirmation message. */
        private String addTask(Task task) throws KeloreInputError {
            if (taskCount >= tasks.length) {
                throw new KeloreInputError("The task list is full.");
            }

            tasks[taskCount] = task;
            taskCount++;
            return "Got it. I've added this task:"
                    + System.lineSeparator()
                    + INDENTATION + "  " + task
                    + System.lineSeparator()
                    + INDENTATION + "Now you have " + taskCount + " tasks in the list.";
        }

        /** Marks the task at the given one-based position as completed. */
        public String mark(int taskNumber) throws KeloreInputError {
            int taskIndex = taskNumber - 1;
            if (!isValidTaskNumber(taskNumber)) {
                throw new KeloreInputError("There is no task with that number.");
            }

            Task task = tasks[taskIndex];
            task.markAsDone();
            return "Nice! I've marked this task as done:"
                    + System.lineSeparator()
                    + INDENTATION + "  " + task;
        }

        /** Marks the task at the given one-based position as not completed. */
        public String unmark(int taskNumber) throws KeloreInputError {
            int taskIndex = taskNumber - 1;
            if (!isValidTaskNumber(taskNumber)) {
                throw new KeloreInputError("There is no task with that number.");
            }

            Task task = tasks[taskIndex];
            task.markAsNotDone();
            return "OK, I've marked this task as not done yet:"
                    + System.lineSeparator()
                    + INDENTATION + "  " + task;
        }

        /** Returns whether the one-based task number refers to a stored task. */
        private boolean isValidTaskNumber(int taskNumber) {
            return taskNumber >= 1 && taskNumber <= taskCount;
        }

        /** Returns all stored tasks as a numbered, indented list. */
        public String display() {
            StringBuilder output = new StringBuilder(INDENTATION)
                    .append("Here are the tasks in your list:")
                    .append(System.lineSeparator());
            for (int i = 0; i < taskCount; i++) {
                output.append(INDENTATION)
                        .append(i + 1)
                        .append(".")
                        .append(tasks[i])
                        .append(System.lineSeparator());
            }
            return output.toString();
        }
    }
}
