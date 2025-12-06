package no.ntnu.dagbok.entry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import no.ntnu.dagbok.author.Author;

/**
 * Represents a single entry in the diary.
 *
 * <p>Each entry consists of an author, a timestamp, a title, and text content.
 * The ID, Author and Timestamp are immutable after creation, while
 * the Title and Text can be updated.</p>
 */
public class DiaryEntry {

  /** Maximum allowed characters for the entry title. */
  public static final int TITLE_MAX_LENGTH = 200;

  /** Maximum allowed characters for the entry text. */
  public static final int TEXT_MAX_LENGTH = 10_000;
  private final Author author;
  private final LocalDateTime dateTime;
  private final UUID entryId;
  private String title;
  private String text;

  /**
   * Creates a new diary entry with a unique ID.
   *
   * @param author The author of the diary entry. Must not be null.
   * @param title The title of the diary entry. Must not be null, blank, or exceed max length.
   * @param text The text of the diary entry. Must not be null, blank, or exceed max length.
   * @param dateTime The date and time of the diary entry. Must not be null.
   * @throws NullPointerException if the author, title, text, or dateTime is null.
   * @throws IllegalArgumentException if the title or text is blank or too long.
   */
  public DiaryEntry(Author author, String title, String text, LocalDateTime dateTime) {
    this.entryId = UUID.randomUUID();
    this.author = Objects.requireNonNull(author, "author cannot be null");
    this.title = validateEmptyInput(title, "title", TITLE_MAX_LENGTH);
    this.text = validateEmptyInput(text, "text", TEXT_MAX_LENGTH);
    this.dateTime = Objects.requireNonNull(dateTime, "dateTime cannot be null");
  }

  /**
   * Validates string input to ensure it is not null, empty, or too long.
   *
   * <p><i>Reusable validator method suggested by ChatGPT-</i></p>
   *
   * @param value The string value to check.
   * @param field The name of the field (used for error messages).
   * @param max The maximum allowed length for the string.
   * @return The trimmed and validated string.
   * @throws NullPointerException if the value is null.
   * @throws IllegalArgumentException if the value is blank or exceeds max length.
   */
  private static String validateEmptyInput(String value, String field, int max) {
    Objects.requireNonNull(value, field + " cannot be null");
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException(field + " cannot be blank");
    }
    if (trimmed.length() > max) {
      throw new IllegalArgumentException(field + " must be shorter than " + max);
    }
    return trimmed;
  }

  // getter methods

  /**
   * Returns the author of this entry.
   *
   * @return The author object.
   */
  public Author getAuthor() {
    return author;
  }

  /**
   * Returns the title of the entry.
   *
   * @return The entry title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the title of the diary entry.
   *
   * @param title The new title. Must not be null, blank, or exceed max length.
   * @throws NullPointerException if the title is null.
   * @throws IllegalArgumentException if title is blank or too long.
   */
  public void setTitle(String title) {
    this.title = validateEmptyInput(title, "title", TITLE_MAX_LENGTH);
  }

  /**
   * Returns the main text content of the entry.
   *
   * @return The entry text.
   */
  public String getText() {
    return text;
  }

  /**
   * Updates the text content of the diary entry.
   *
   * @param text The new text content. Must not be null, blank, or exceed max length.
   * @throws NullPointerException if text is null.
   * @throws IllegalArgumentException if text is blank or too long.
   */
  public void setText(String text) {
    this.text = validateEmptyInput(text, "text", TEXT_MAX_LENGTH);
  }

  /**
   * Returns the creation timestamps of the entry.
   *
   * @return The date and time the entry was created.
   */
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  // setter methods

  /**
   * Returns the unique identifier for this diary entry.
   *
   * @return The entry's UUID.
   */
  public UUID getEntryId() {
    return entryId;
  }

  /**
   * Calculates the total number of words in the entry's text.
   *
   * <p>Words are determined by splitting the text by whitespace.</p>
   *
   * <p><i>Trimming made with help from AI.</i></p>
   *
   * @return The total word count. Returns 0 if text is empty.
   */
  public int getWordCount() {
    if (text == null || text.isBlank()) {
      return 0;
    }
    return text.trim().split("\\s+").length;
  }

  /**
   * Returns a string representation of the diary entry.
   *
   * @return A formatted string containing timestamp, author, title, and text.
   */
  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return "[" + dateTime.format(formatter) + "] " + "\n" + author + "\n" + title + "\n" + text;
  }
}
