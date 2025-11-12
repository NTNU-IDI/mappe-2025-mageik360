package no.ntnu.dagbok.entry;
import java.time.LocalDateTime;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiaryEntryTest {
  @Test void constructor_acceptsValidValues(){
    Author a = new Author("Lars");
    LocalDateTime dateTime = LocalDateTime.of(2025,12,12,12,12);
    DiaryEntry diaryEntry = new DiaryEntry(a,"Title", "Text", dateTime);

    assertEquals(a, diaryEntry.getAuthor());
    assertEquals("Title", diaryEntry.getTitle());
    assertEquals("Text", diaryEntry.getText());
    assertEquals(dateTime,diaryEntry.getDateTime());
  }

  @Test
  void constructor_rejectsInvalidValues(){
    Author a = new Author("Lisa");
    LocalDateTime dateTime = LocalDateTime.of(2025,1,1,10,0);
    assertThrows(NullPointerException.class, () -> new DiaryEntry(null,"title", "text", dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry(a,null, "text", dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry(a,"title", null, dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry(a,"title", "text", null));
    assertThrows(IllegalArgumentException.class, () -> new DiaryEntry(a," ", "text", dateTime));
    assertThrows(IllegalArgumentException.class, () -> new DiaryEntry(a,"title", " ", dateTime));
  }

  @Test
  void setters_trim_and_validate(){
    DiaryEntry diaryEntry = new DiaryEntry(new Author("Linda"), "Title","Text", LocalDateTime.now());
    diaryEntry.setTitle("  New Title  ");
    diaryEntry.setText("  New Text  ");

    assertEquals("New Title", diaryEntry.getTitle());
    assertEquals("New Text", diaryEntry.getText());

    assertThrows(IllegalArgumentException.class, () -> diaryEntry.setTitle("   "));
    assertThrows(NullPointerException.class, () -> diaryEntry.setTitle(null));
    assertThrows(IllegalArgumentException.class, () -> diaryEntry.setText("   "));
    assertThrows(NullPointerException.class, () -> diaryEntry.setText(null));

  }
}