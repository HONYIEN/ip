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
            } else if (input.startsWith("mark ")) {
                try {
                    int taskNumber = Integer.parseInt(input.substring(5).trim());
                    System.out.println(INDENTATION + TaskList.mark(taskNumber));
                } catch (NumberFormatException e) {
                    System.out.println(INDENTATION + "Please provide a valid task number.");
                }
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

        /** Marks this task as completed. */
        public void markAsDone() {
            isDone = true;
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

        /** Marks the task at the given one-based position as completed. */
        public static String mark(int taskNumber) {
            int taskIndex = taskNumber - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                return "There is no task with that number.";
            }

            Task task = tasks[taskIndex];
            task.markAsDone();
            return "Nice! I've marked this task as done:"
                    + System.lineSeparator()
                    + INDENTATION + "  " + task;
        }

        /** Returns all stored tasks as a numbered, indented list. */
        public static String display() {
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
