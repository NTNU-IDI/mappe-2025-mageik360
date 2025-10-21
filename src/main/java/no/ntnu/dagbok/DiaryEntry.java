package no.ntnu.dagbok;
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
   * @param author The author of the diary entry. Non-null.
   * @param title The title of the diary entry. Non-null.
   * @param text The text of the diary entry. Non-null.
   * @param dateTime The date and time of the diary entry. Non-null value.
   *
   */
  public DiaryEntry(String author, String title, String text, LocalDateTime dateTime){
    Objects.requireNonNull(author, "Must have author");
    Objects.requireNonNull(title, "Must have title");
    Objects.requireNonNull(text, "Must have text");
    Objects.requireNonNull(text, "Must have date and time");

    this.author = author;
    this.title = title;
    this.text = text;
    this.dateTime = dateTime;

  }

  public String getAuthor() { return author; }
  public String getTitle() { return title; }
  public String getText() { return text; }
  public LocalDateTime getDateTime() {return dateTime;}

}
