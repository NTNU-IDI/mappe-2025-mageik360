package no.ntnu.dagbok;

import java.time.LocalDateTime;

public class DiaryEntry {
  private String author;
  private String title;
  private String text;
  private LocalDateTime dateTime;

  public String getAuthor() { return author; }
  public String getTitle() { return title; }
  public String getText() { return text; }
  public LocalDateTime getDateTime() {return dateTime;}

}
