package kelore.ui;

import java.util.Scanner;

/** Handles console interactions between Kelore and the user. */
public class Ui {
    private static final String INDENTATION = "    ";
    private static final String DIVIDER = "_".repeat(60);
    private final Scanner scanner = new Scanner(System.in);

    /** Creates a console user interface that reads from standard input. */
    public Ui() {
    }

    /** Displays the welcome banner and initial greeting. */
    public void showWelcome() {
        String banner = " _  __ _____ _       ___  ____  _____\n"
                + "| |/ /| ____| |     / _ \\|  _ \\| ____|\n"
                + "| ' / |  _| | |    | | | | |_) |  _|\n"
                + "| . \\ | |___| |___ | |_| |  _ <| |___\n"
                + "|_|\\_\\|_____|_____| \\___/|_| \\_\\_____|";
        showDivider();
        showIndentedLine(banner.replace("\n", "\n" + INDENTATION));
        showIndentedLine("Hello! I'm Kelore.");
        showIndentedLine("What can I do for you?");
        showDivider();
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return Next line read from standard input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a message without adding a line break.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.print(message);
    }

    /**
     * Displays an indented message followed by a line break.
     *
     * @param message Message to display.
     */
    public void showIndentedLine(String message) {
        System.out.println(INDENTATION + message);
    }

    /**
     * Displays an indented error message.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showIndentedLine("Oops! " + message);
    }

    /** Displays a horizontal divider. */
    public void showDivider() {
        showIndentedLine(DIVIDER);
    }

    /** Displays the farewell message and a divider. */
    public void showGoodbye() {
        showIndentedLine("Bye. Hope to see you again soon!");
        showDivider();
    }

    /** Closes the console input scanner. */
    public void close() {
        scanner.close();
    }
}
