package kelore;

import java.io.IOException;
import java.nio.file.Path;

import kelore.exception.KeloreInputException;
import kelore.parser.Parser;
import kelore.storage.Storage;
import kelore.task.TaskList;

/** Processes commands for the Kelore task-tracking chatbot. */
public class Kelore {
    private static final Path DATA_FILE_PATH = Path.of("data", "kelore.txt");
    private static final String WELCOME_MESSAGE = "Hello! I'm Kelore.\nWhat can I do for you?";

    private final Parser parser = new Parser();
    private final Storage storage;
    private final TaskList taskList;
    private final String loadMessage;

    /** Creates a Kelore chatbot that stores tasks in the default data file. */
    public Kelore() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Kelore chatbot that stores tasks in the specified data file.
     *
     * @param dataFilePath Path of the data file.
     */
    public Kelore(Path dataFilePath) {
        assert dataFilePath != null : "The data file path must not be null";
        storage = new Storage(dataFilePath);
        TaskList loadedTasks;
        String loadingError = "";
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadingError = "\nOops! I could not load your saved tasks.\n" + e.getMessage();
        }
        assert loadedTasks != null : "Loading must produce a task list";
        taskList = loadedTasks;
        loadMessage = loadingError;
    }

    /**
     * Returns the greeting shown when the chatbot starts.
     *
     * @return Greeting and any data-loading error.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE + loadMessage;
    }

    /**
     * Executes a user command and returns Kelore's response.
     *
     * @param input Complete command entered by the user.
     * @return Displayable response to the command.
     */
    public String getResponse(String input) {
        return getResponseDetails(input).message();
    }

    /**
     * Executes a user command and returns its message together with its display type.
     *
     * @param input Complete command entered by the user.
     * @return Response containing the displayable message and whether it is an error.
     */
    public Response getResponseDetails(String input) {
        try {
            switch (parser.parseCommand(input)) {
                case BYE:
                    return Response.success("Bye. Hope to see you again soon!");
                case LIST:
                    return Response.success(taskList.display());
                case MARK:
                    return Response.success(saveAfter(taskList.mark(parser.parseTaskNumber(input))));
                case UNMARK:
                    return Response.success(saveAfter(taskList.unmark(parser.parseTaskNumber(input))));
                case DELETE:
                    return Response.success(saveAfter(taskList.delete(parser.parseTaskNumber(input))));
                case TODO:
                    return Response.success(saveAfter(taskList.addTodo(input)));
                case DEADLINE:
                    return Response.success(saveAfter(taskList.addDeadline(input)));
                case EVENT:
                    return Response.success(saveAfter(taskList.addEvent(input)));
                case ON:
                    return Response.success(taskList.displayTasksOn(input));
                case FIND:
                    return Response.success(taskList.find(input));
                default:
                    throw new AssertionError("Unhandled command");
            }
        } catch (KeloreInputException e) {
            return Response.error("Oops! " + e.getMessage());
        } catch (IOException e) {
            return Response.error("Oops! I could not save your tasks.\n" + e.getMessage());
        }
    }

    private String saveAfter(String response) throws IOException {
        storage.save(taskList);
        return response;
    }

    /**
     * Represents a message from Kelore and how the GUI should present it.
     *
     * @param message Displayable response text.
     * @param isError Whether the response reports an error.
     */
    public record Response(String message, boolean isError) {
        /**
         * Creates a normal response.
         *
         * @param message Displayable response text.
         * @return Normal response containing the text.
         */
        public static Response success(String message) {
            return new Response(message, false);
        }

        /**
         * Creates an error response.
         *
         * @param message Displayable error text.
         * @return Error response containing the text.
         */
        public static Response error(String message) {
            return new Response(message, true);
        }
    }
}
