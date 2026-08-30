package kelore.parser;

import kelore.exception.KeloreInputException;

/** Interprets commands entered by the user. */
public class Parser {
    /** Creates a parser for Kelore commands. */
    public Parser() {
    }

    /**
     * Returns the command represented by the user's full input.
     *
     * @param input Complete user input to interpret.
     * @return Command invoked by the input.
     * @throws KeloreInputException If the input does not invoke a supported command.
     */
    public Command parseCommand(String input) throws KeloreInputException {
        for (Command command : Command.values()) {
            if (command.matches(input)) {
                return command;
            }
        }
        throw new KeloreInputException("I don't recognise that command.");
    }

    /**
     * Returns the one-based task number following a command.
     *
     * @param input Complete user input containing a task number.
     * @return Parsed one-based task number.
     * @throws KeloreInputException If the task number is not a valid integer.
     */
    public int parseTaskNumber(String input) throws KeloreInputException {
        try {
            return Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
        } catch (NumberFormatException e) {
            throw new KeloreInputException(
                    "Please provide a valid task number after the command.");
        }
    }
}
