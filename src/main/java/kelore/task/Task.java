package kelore.task;

import java.time.LocalDate;
import java.util.Locale;

import kelore.storage.Storage;

/** Represents a task and whether it has been completed. */
public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns {@code 1} when completed and {@code 0} otherwise for file storage. */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /** Converts this task into the text format used in the data file. */
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /** Returns whether this task occurs on the specified calendar date. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether any word in this task's description is a close match for the keyword.
     * A small length-based edit-distance limit avoids overly broad matches for short keywords.
     */
    public boolean containsCloseKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        int allowedDistance = getAllowedDistance(normalizedKeyword.length());
        for (String word : description.toLowerCase(Locale.ENGLISH).split("\\s+")) {
            if (levenshteinDistance(word, normalizedKeyword) <= allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private int getAllowedDistance(int keywordLength) {
        if (keywordLength <= 2) {
            return 0;
        }
        if (keywordLength <= 7) {
            return 1;
        }
        return 2;
    }

    /** Calculates the minimum number of single-character edits between two strings. */
    private int levenshteinDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previousRow[j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                currentRow[j] = Math.min(
                        Math.min(currentRow[j - 1] + 1, previousRow[j] + 1),
                        previousRow[j - 1] + substitutionCost);
            }
            previousRow = currentRow;
        }
        return previousRow[second.length()];
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
