import java.util.Scanner;

/**
 * Starts the Kelore chatbot application.
 */
public class Kelore {
    public static void main(String[] args) {
        String banner = " _  __ _____ _       ___  ____  _____\n"
                + "| |/ /| ____| |     / _ \\|  _ \\| ____|\n"
                + "| ' / |  _| | |    | | | | |_) |  _|\n"
                + "| . \\ | |___| |___ | |_| |  _ <| |___\n"
                + "|_|\\_\\|_____|_____| \\___/|_| \\_\\_____|";
        String divider = "_".repeat(60);
        String indentation = "    ";

        System.out.println(indentation + divider);
        System.out.println(indentation + banner.replace("\n", "\n" + indentation));
        System.out.println(indentation + "Hello! I'm Kelore.");
        System.out.println(indentation + "What can I do for you?");
        System.out.println(indentation + divider);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println(indentation + divider);

            if ("bye".equals(input)) {
                System.out.println(indentation + "Bye. Hope to see you again soon!");
                System.out.println(indentation + divider);
                break;
            }

            System.out.println(indentation + echoString(input));
            System.out.println(indentation + divider);
        }
    }

    public static String echoString(String input) {
        return input;
    }
}
