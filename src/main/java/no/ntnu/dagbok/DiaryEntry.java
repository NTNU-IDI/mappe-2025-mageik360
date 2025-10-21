package no.ntnu.dagbok;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import java.time.LocalDateTime;

public class DiaryEntry {
  private String author;
  private String title;
  private String text;
  private LocalDateTime dateTime;

  /**
   * Constructor for a new DiaryEntry
   *
   * @param author The author of the diary entry. Non-null. Not blank.
   * @param title The title of the diary entry. Non-null. Not blank.
   * @param text The text of the diary entry. Non-null. Not blank.
   * @param dateTime The date and time of the diary entry. Non-null value.
   *
   */
  public DiaryEntry(String author, String title, String text, LocalDateTime dateTime){
    this.author = validateEmptyInput(author,"author");
    this.title = validateEmptyInput(title,"title");
    this.text = validateEmptyInput(text, "text");
    this.dateTime = Objects.requireNonNull(dateTime, "dateTime cannot be null");

  }

  /**
   * Reusable validator method for diary constructor and setters.
   * Suggested improvement by chatGPT
   * @param value value of field for constructor or setter
   * @param field name of field used in message
   * @return validated non-empty field
   */
  private static String validateEmptyInput(String value, String field){
    Objects.requireNonNull(value, field + "cannot be null");
    String trimmed = value.trim();
    if (trimmed.isEmpty()){
      throw new IllegalArgumentException(field + " cannot be blank");
    }
    return trimmed;
  }

  // setter methods

  /**
   * setter for title
   * @param title non-blank
   */
  public void setTitle(String title){
    this.title = validateEmptyInput(title, "title");
  }

  /**
   * setter for title
   * @param text non-blank
   */
  public void setText(String text){
    this.title = validateEmptyInput(text, "text");
  }

  // getter methods
  /** getter for author (immutable field)
   * @return author
   */
  public String getAuthor() { return author; }

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
