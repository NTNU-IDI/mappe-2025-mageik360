package no.ntnu.dagbok.entry;
import java.time.LocalDateTime;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiaryEntryTest {

  private final String dummyPassword = "dummyPassword";

  /**
   * Test to show that the diary entry constructor accepts valid inputs.
   *
   * <p>
   * assertEquals for author object, title, text and time.
   * </p>
   */
  @Test void constructor_acceptsValidValues(){
    Author a = new Author("Lars", dummyPassword);
    LocalDateTime dateTime = LocalDateTime.of(2025,12,12,12,12);
    DiaryEntry diaryEntry = new DiaryEntry(a,"Title", "Text", dateTime);

    assertEquals(a, diaryEntry.getAuthor());
    assertEquals("Title", diaryEntry.getTitle());
    assertEquals("Text", diaryEntry.getText());
    assertEquals(dateTime,diaryEntry.getDateTime());
  }

  /**
   * Test to check that diary entry constructor rejects invalid inputs.
   * <p>
   * assertThrows for null and empty strings.
   * </p>
   */
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

  /**
   * Test to check that diary entry constructor properly trims and validates input.
   *
   * <p>
   * assertEquals for trimmed text and title.
   * assertThrows for empty/null text.
   * </p>
   */
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

  /**
   * Helper method to create long strings.
   *
   * @param c character to repeat.
   * @param n the number of times to repeat character.
   * @return Repeated characters in a string.
   */
  private static String repeat(char c, int n){
    return String.valueOf(c).repeat(Math.max(0,n));
  }

  /**
   * Test to check that constructor accepts titles and texts of the maximum length.
   *
   * <p>
   * assertEquals for valid text and title.
   * </p>
   */
  @Test
  void constructor_accepts_exact_max_lengths(){
    Author a = new Author("Lars", dummyPassword);
    String title = repeat('A', DiaryEntry.TITLE_MAX_LENGTH);
    String text = repeat('B', DiaryEntry.TEXT_MAX_LENGTH);
    DiaryEntry e = new DiaryEntry(a, title,text,LocalDateTime.of(2025,1,1,10,10));
    assertEquals(title,e.getTitle());
    assertEquals(text,e.getText());
  }

  /**
   * Test to check that constructor rejects title and text that are too long.
   *
   * <p>
   * assertThrows for too long text.
   * assertThrows for too long title.
   * </p>
   */
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

  /**
   * Test to check that setters enforce limits and trimming.
   *
   * <p>
   * assertEquals for valid trimmed title and text.
   * assertThrows for title and text exceeding maximum length.
   * </p>
   */
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