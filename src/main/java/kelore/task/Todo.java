package kelore.task;

import kelore.storage.Storage;

/** Represents a task without a date or time. */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }
}
