import java.util.Scanner;

/** Runs the Kelore task-tracking chatbot. */
public class Kelore {
    private static final String INDENTATION = "    ";
    private static final String DIVIDER = "_".repeat(60);

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

            if ("list".equals(input)) {
                System.out.print(taskList.display());
            } else if (input.startsWith("mark ")) {
                System.out.println(INDENTATION + updateTaskStatus(taskList, input, true));
            } else if (input.startsWith("unmark ")) {
                System.out.println(INDENTATION + updateTaskStatus(taskList, input, false));
            } else {
                System.out.println(INDENTATION + taskList.add(input));
            }
            System.out.println(INDENTATION + DIVIDER);
        }
        scanner.close();
    }

    /** Parses a task number and applies either the mark or unmark operation. */
    private static String updateTaskStatus(TaskList taskList, String input, boolean markAsDone) {
        try {
            int taskNumber = Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
            return markAsDone ? taskList.mark(taskNumber) : taskList.unmark(taskNumber);
        } catch (NumberFormatException e) {
            return "Please provide a valid task number.";
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

    /** Stores tasks entered during the current program run. */
    public static class TaskList {
        private final Task[] tasks = new Task[100];
        private int taskCount = 0;

        /** Adds a task and returns the message to show the user. */
        public String add(String input) {
            if (input.isBlank()) {
                return "The task description cannot be empty.";
            }

            if (taskCount >= tasks.length) {
                return "Task list is full.";
            }

            Task task = new Task(input);
            tasks[taskCount] = task;
            taskCount++;
            return "added: " + task;
        }

        /** Marks the task at the given one-based position as completed. */
        public String mark(int taskNumber) {
            int taskIndex = taskNumber - 1;
            if (!isValidTaskNumber(taskNumber)) {
                return "There is no task with that number.";
            }

            Task task = tasks[taskIndex];
            task.markAsDone();
            return "Nice! I've marked this task as done:"
                    + System.lineSeparator()
                    + INDENTATION + "  " + task;
        }

        /** Marks the task at the given one-based position as not completed. */
        public String unmark(int taskNumber) {
            int taskIndex = taskNumber - 1;
            if (!isValidTaskNumber(taskNumber)) {
                return "There is no task with that number.";
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
