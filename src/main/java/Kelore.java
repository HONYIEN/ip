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
        while (true) {
            String input = scanner.nextLine();
            System.out.println(INDENTATION + DIVIDER);

            if ("bye".equals(input)) {
                System.out.println(INDENTATION + "Bye. Hope to see you again soon!");
                System.out.println(INDENTATION + DIVIDER);
                break;
            }

            if ("list".equals(input)) {
                System.out.print(TaskList.display());
            } else {
                System.out.println(INDENTATION + TaskList.add(input));
            }
            System.out.println(INDENTATION + DIVIDER);
        }
        scanner.close();
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

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    /** Stores tasks entered during the current program run. */
    public static class TaskList {
        private static final Task[] tasks = new Task[100];
        private static int taskCount = 0;

        /** Adds a task and returns the message to show the user. */
        public static String add(String input) {
            if (taskCount >= tasks.length) {
                return "Task list is full.";
            }

            Task task = new Task(input);
            tasks[taskCount] = task;
            taskCount++;
            return "added: " + task;
        }

        /** Returns all stored tasks as a numbered, indented list. */
        public static String display() {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < taskCount; i++) {
                output.append(INDENTATION)
                        .append(i + 1)
                        .append(". ")
                        .append(tasks[i])
                        .append(System.lineSeparator());
            }
            return output.toString();
        }
    }
}
