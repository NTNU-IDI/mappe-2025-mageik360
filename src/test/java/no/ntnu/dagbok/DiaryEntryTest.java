package no.ntnu.dagbok;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
class DiaryEntryTest {
  @Test void constructor_acceptsValidValues(){
    LocalDateTime dateTime = LocalDateTime.of(2025,12,12,12,12);
    DiaryEntry diaryEntry = new DiaryEntry("Lars","Title", "Text", dateTime);

    assertEquals("Lars", diaryEntry.getAuthor());
    assertEquals("Title", diaryEntry.getTitle());
    assertEquals("Text", diaryEntry.getText());
    assertEquals(dateTime,diaryEntry.getDateTime());
  }

  @Test
  void constructor_rejectsNulls(){
    LocalDateTime dateTime = LocalDateTime.now();
    assertThrows(NullPointerException.class, () -> new DiaryEntry(null,"title", "text", dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry("name",null, "text", dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry("name","title", null, dateTime));
    assertThrows(NullPointerException.class, () -> new DiaryEntry("name","title", "text", null));
  }

  @Test
  void constructor_rejectsEmptyStrings(){
    LocalDateTime dateTime = LocalDateTime.now();
    assertThrows(IllegalArgumentException.class, () -> new DiaryEntry(" ","title", "text", dateTime));
    assertThrows(IllegalArgumentException.class, () -> new DiaryEntry("name"," ", "text", dateTime));
    assertThrows(IllegalArgumentException.class, () -> new DiaryEntry("name","title", " ", dateTime));
  }

  @Test
  void setterMethods_validateAndUpdate(){
    DiaryEntry diaryEntry = new DiaryEntry("Name", "Title", "Test", LocalDateTime.now());
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