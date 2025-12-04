package no.ntnu.dagbok.entry;
import java.time.LocalDateTime;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiaryEntryTest {

  private final String dummyPassword = "dummyPassword";
  @Test void constructor_acceptsValidValues(){
    Author a = new Author("Lars", dummyPassword);
    LocalDateTime dateTime = LocalDateTime.of(2025,12,12,12,12);
    DiaryEntry diaryEntry = new DiaryEntry(a,"Title", "Text", dateTime);

    assertEquals(a, diaryEntry.getAuthor());
    assertEquals("Title", diaryEntry.getTitle());
    assertEquals("Text", diaryEntry.getText());
    assertEquals(dateTime,diaryEntry.getDateTime());
  }

  @Test
  void constructor_rejectsInvalidValues(){
    Author a = new Author("Lisa", dummyPassword);
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
    DiaryEntry diaryEntry = new DiaryEntry(new Author("Linda", dummyPassword), "Title","Text", LocalDateTime.now());
    diaryEntry.setTitle("  New Title  ");
    diaryEntry.setText("  New Text  ");

    assertEquals("New Title", diaryEntry.getTitle());
    assertEquals("New Text", diaryEntry.getText());

    assertThrows(IllegalArgumentException.class, () -> diaryEntry.setTitle("   "));
    assertThrows(NullPointerException.class, () -> diaryEntry.setTitle(null));
    assertThrows(IllegalArgumentException.class, () -> diaryEntry.setText("   "));
    assertThrows(NullPointerException.class, () -> diaryEntry.setText(null));

  }
  private static String repeat(char c, int n){
    return String.valueOf(c).repeat(Math.max(0,n));
  }
  @Test
  void constructor_accepts_exact_max_lengths(){
    Author a = new Author("Lars", dummyPassword);
    String title = repeat('A', DiaryEntry.TITLE_MAX_LENGTH);
    String text = repeat('B', DiaryEntry.TEXT_MAX_LENGTH);
    DiaryEntry e = new DiaryEntry(a, title,text,LocalDateTime.of(2025,1,1,10,10));
    assertEquals(title,e.getTitle());
    assertEquals(text,e.getText());
  }

  @Test
  void constructor_reject_over_max_lengths(){
    Author a = new Author("Hanna", dummyPassword);
    String tooLongTitle = repeat('A', DiaryEntry.TITLE_MAX_LENGTH+1);
    String okText = "Text";
    assertThrows(IllegalArgumentException.class,()-> new DiaryEntry(a,tooLongTitle,okText,LocalDateTime.of(2025,1,1,10,10)));
    String okTitle = "Title";
    String tooLongText = repeat('A', DiaryEntry.TEXT_MAX_LENGTH +1);
    assertThrows(IllegalArgumentException.class,()-> new DiaryEntry(a,okTitle,tooLongText,LocalDateTime.of(2025,1,1,10,10)));

  }
  @Test
  void setters_enforce_limits_and_trimming(){
    DiaryEntry e = new DiaryEntry(new Author("Leonora", dummyPassword),"Title", "Text", LocalDateTime.of(2025,1,1,10,0));
    e.setTitle("  Title  ");
    e.setText("  Text  ");
    assertEquals("Title",e.getTitle());
    assertEquals("Text",e.getText());

    String tooLongTitle = repeat('A', DiaryEntry.TITLE_MAX_LENGTH +1);
    String tooLongText = repeat('B', DiaryEntry.TEXT_MAX_LENGTH +1);

    assertThrows(IllegalArgumentException.class, () -> e.setTitle(tooLongTitle));
    assertThrows(IllegalArgumentException.class, () -> e.setText(tooLongText));
  }
}