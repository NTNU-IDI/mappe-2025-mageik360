package no.ntnu.dagbok.entry;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import java.time.LocalDateTime;
import no.ntnu.dagbok.author.Author;

public class DiaryEntry {
  private final Author author;
  private String title;
  private String text;
  private final LocalDateTime dateTime;
  public static final int TITLE_MAX_LENGTH = 200;
  public static final int TEXT_MAX_LENGTH = 10_000;

  /**
   * Constructor for a new DiaryEntry
   *
   * @param author The author of the diary entry. Non-null.
   * @param title The title of the diary entry. Non-null. Not blank.
   * @param text The text of the diary entry. Non-null. Not blank.
   * @param dateTime The date and time of the diary entry. Non-null value.
   *
   */
  public DiaryEntry(Author author, String title, String text, LocalDateTime dateTime){
    this.author = Objects.requireNonNull(author, "author cannot be null");
    this.title = validateEmptyInput(title,"title", TITLE_MAX_LENGTH);
    this.text = validateEmptyInput(text, "text", TEXT_MAX_LENGTH);
    this.dateTime = Objects.requireNonNull(dateTime, "dateTime cannot be null").truncatedTo(
        ChronoUnit.MINUTES);

  }

  /**
   * Reusable validator method for diary constructor and setters.
   * Suggested improvement by chatGPT
   * @param value value of field for constructor or setter
   * @param field name of field used in message
   * @return validated non-empty field
   */
  private static String validateEmptyInput(String value, String field, int max){
    Objects.requireNonNull(value, field + " cannot be null");
    String trimmed = value.trim();
    if (trimmed.isEmpty()){
      throw new IllegalArgumentException(field + " cannot be blank");
    }
    if (trimmed.length() > max){
      throw new IllegalArgumentException(field + " must be shorter than " + max);
    }
    return trimmed;
  }

  // setter methods

  /**
   * setter for title
   * @param title non-blank
   */
  public void setTitle(String title){
    this.title = validateEmptyInput(title, "title", TITLE_MAX_LENGTH);
  }

  /**
   * setter for text
   * @param text non-blank
   */
  public void setText(String text){
    this.text = validateEmptyInput(text, "text", TEXT_MAX_LENGTH);
  }

  // getter methods
  /** getter for author (immutable field)
   * @return author
   */
  public Author getAuthor() { return author; }

  /**
   * getter for title (mutable field)
   * @return title
   */
  public String getTitle() { return title; }

  /**
   * getter for text (mutable field)
   * @return text
   */
  public String getText() { return text; }

  /**
   * getter for dateTime (immutable field)
   * @return dateTime
   */
  public LocalDateTime getDateTime() {return dateTime;}

  @Override
  public String toString(){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return "[" + dateTime.format(formatter) + "] " + "\n" + author + "\n" + title + "\n" + text;
  }
}
