package kelore.task;

import java.time.LocalDate;

import kelore.storage.Storage;

/** Represents a task and whether it has been completed. */
public class Task {
    /** Description of this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if complete; a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the completion status used by the data file.
     *
     * @return {@code 1} if complete; {@code 0} otherwise.
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the text format used by the data file.
     *
     * @return Serialized task record.
     */
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /**
     * Returns whether this task occurs on the specified calendar date.
     *
     * @param date Calendar date to check.
     * @return False because a basic task has no associated date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns a displayable description of this task and its completion status. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
