package kelore.ui;

import java.util.Scanner;

/** Handles console interactions between Kelore and the user. */
public class Ui {
    private static final String INDENTATION = "    ";
    private static final String DIVIDER = "_".repeat(60);
    private final Scanner scanner = new Scanner(System.in);

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

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.print(message);
    }

    public void showIndentedLine(String message) {
        System.out.println(INDENTATION + message);
    }

    public void showError(String message) {
        showIndentedLine("Oops! " + message);
    }

    public void showDivider() {
        showIndentedLine(DIVIDER);
    }

    public void showGoodbye() {
        showIndentedLine("Bye. Hope to see you again soon!");
        showDivider();
    }

    public void close() {
        scanner.close();
    }
}
