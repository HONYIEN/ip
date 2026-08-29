import java.util.Scanner;

/** Handles console interactions between Kelore and the user. */
public class Ui {
    private static final String INDENTATION = "    ";
    private static final String DIVIDER = "_".repeat(60);

    private final Scanner scanner;

    /** Creates a UI that reads user commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Displays Kelore's greeting and command prompt. */
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

    /** Reads the user's next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays text that has already been formatted for the user. */
    public void showMessage(String message) {
        System.out.print(message);
    }

    /** Displays a single indented line. */
    public void showIndentedLine(String message) {
        System.out.println(INDENTATION + message);
    }

    /** Displays an input error in Kelore's standard error format. */
    public void showError(String message) {
        showIndentedLine("Oops! " + message);
    }

    /** Displays the divider used before and after command responses. */
    public void showDivider() {
        showIndentedLine(DIVIDER);
    }

    /** Displays Kelore's farewell. */
    public void showGoodbye() {
        showIndentedLine("Bye. Hope to see you again soon!");
        showDivider();
    }

    /** Closes the console input reader. */
    public void close() {
        scanner.close();
    }
}
