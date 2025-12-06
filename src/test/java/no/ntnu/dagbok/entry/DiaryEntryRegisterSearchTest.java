package no.ntnu.dagbok.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for diary entry register search functionality. */
public class DiaryEntryRegisterSearchTest {

  private DiaryEntryRegister register;
  private Author author;

  @BeforeEach
  void setUp() {
    register = new DiaryEntryRegister();
    author = new Author("TestAuthor", "password");
  }

  /**
   * Tests that searchKeyword finds matches both in the title and the text.
   *
   * <p>assertEquals for correct number of returned entries.
   */
  @Test
  void searchByKeyword_finds_matches_in_title_and_text() {
    register.addEntry(new DiaryEntry(author, "My Day", "Did nothing", LocalDateTime.now()));
    register.addEntry(
        new DiaryEntry(author, "Work", "Meeting about planning", LocalDateTime.now()));

    List<DiaryEntry> res1 = register.searchByKeyword("Day");
    assertEquals(1, res1.size());

    List<DiaryEntry> res2 = register.searchByKeyword("Planning");
    assertEquals(1, res2.size());
  }

  /**
   * Tests that searchByKeyword is case-insensitive
   *
   * <p>assertEquals for correct number of returned entries.
   */
  @Test
  void searchByKeyword_is_case_insensitive() {
    register.addEntry(new DiaryEntry(author, "Title", "TEXT", LocalDateTime.now()));

    List<DiaryEntry> res = register.searchByKeyword("text");
    assertEquals(1, res.size());
  }

  /**
   * Tests that searchByKeyword returns an empty list when there are no matches.
   *
   * <p>assertTrue for empty list, not null.
   */
  @Test
  void searchByKeyword_returns_empty_list_for_no_match() {
    register.addEntry(new DiaryEntry(author, "Title", "Text", LocalDateTime.now()));

    List<DiaryEntry> res = register.searchByKeyword("Fake");
    assertTrue(res.isEmpty(), "Should return empty list, not null");
  }

  /**
   * Tests that removeEntry deletes correct entry based on UUID.
   *
   * <p>assertTrue for returning true boolean. assertEquals for number of entries and title.
   */
  @Test
  void removeEntry_deletes_correct_entry_by_uuid() {
    DiaryEntry e1 = new DiaryEntry(author, "Title1", "Text1", LocalDateTime.now());
    DiaryEntry e2 = new DiaryEntry(author, "Title2", "Text2", LocalDateTime.now());
    register.addEntry(e1);
    register.addEntry(e2);

    boolean removed = register.removeEntry(e1.getEntryId());

    assertTrue(removed);
    assertEquals(1, register.getNumberOfEntries());

    assertEquals("Title2", register.getAll().getFirst().getTitle());
  }

  /**
   * Tests that getStatistics calculates the correct values.
   *
   * <p>assertTrue for strings containing the correct values.
   */
  @Test
  void getStatistics_calculates_correct_totals() {
    register.addEntry(new DiaryEntry(author, "Title1", "word1 word2", LocalDateTime.now()));
    register.addEntry(new DiaryEntry(author, "Title2", "word3 word4 word5", LocalDateTime.now()));

    String stats = register.getStatistics(author.getId());

    assertTrue(stats.contains("Total entries: 2"));
    assertTrue(stats.contains("Total word count: 5"));
  }
}
