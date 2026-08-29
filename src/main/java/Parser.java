/** Interprets commands entered by the user. */
public class Parser {

    /** Represents a command that Kelore can execute. */
    public enum Command {
        BYE("bye", false),
        LIST("list", false),
        MARK("mark", true),
        UNMARK("unmark", true),
        DELETE("delete", true),
        TODO("todo", true),
        DEADLINE("deadline", true),
        EVENT("event", true),
        ON("on", true);

        private final String commandWord;
        private final boolean acceptsArguments;

        Command(String commandWord, boolean acceptsArguments) {
            this.commandWord = commandWord;
            this.acceptsArguments = acceptsArguments;
        }
    }

    /** Returns the command represented by the user's full input. */
    public Command parseCommand(String input) throws Kelore.KeloreInputError {
        for (Command command : Command.values()) {
            boolean isExactCommand = input.equals(command.commandWord);
            boolean hasArguments = command.acceptsArguments
                    && input.startsWith(command.commandWord + " ");
            if (isExactCommand || hasArguments) {
                return command;
            }
        }
        throw new Kelore.KeloreInputError("I don't recognise that command.");
    }

    /** Parses the one-based task number following a command. */
    public int parseTaskNumber(String input) throws Kelore.KeloreInputError {
        try {
            return Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
        } catch (NumberFormatException e) {
            throw new Kelore.KeloreInputError(
                    "Please provide a valid task number after the command.");
        }
    }
}
