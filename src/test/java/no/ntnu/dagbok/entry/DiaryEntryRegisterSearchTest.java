package no.ntnu.dagbok.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiaryEntryRegisterSearchTest {

  private DiaryEntryRegister register;
  private Author author;

  @BeforeEach
  void setUp(){
    register = new DiaryEntryRegister();
    author = new Author("TestAuthor", "password");
  }

  /**
   * Tests that searchKeyword finds matches both in the title and the text.
   */
  @Test
  void searchByKeyword_finds_matches_in_title_and_text(){
    register.addEntry(new DiaryEntry(author, "Ferien min", "Reiste til Spania", LocalDateTime.now()));
    register.addEntry(new DiaryEntry(author,"Arbeid","Møte om planlegging", LocalDateTime.now()));

    List<DiaryEntry> res1 = register.searchByKeyword("Ferien");
    assertEquals(1, res1.size());

    List<DiaryEntry> res2 = register.searchByKeyword("Møte");
    assertEquals(1, res2.size());
  }

  @Test
  void searchByKeyword_is_case_insensitive(){
    register.addEntry(new DiaryEntry(author, "Title", "TEXT", LocalDateTime.now()));

    List<DiaryEntry> res = register.searchByKeyword("text");
    assertEquals(1,res.size());
  }

  @Test
  void searchByKeyword_returns_empty_list_for_no_match(){
    register.addEntry(new DiaryEntry(author, "Title", "Text", LocalDateTime.now()));

    List<DiaryEntry> res = register.searchByKeyword("Fake");
    assertTrue(res.isEmpty(), "Should return empty list, not null");
  }

  @Test
  void removeEntry_deletes_correct_entry_by_uuid(){
    DiaryEntry e1 = new DiaryEntry(author, "Title1", "Text1", LocalDateTime.now());
    DiaryEntry e2 = new DiaryEntry(author, "Title2", "Text2", LocalDateTime.now());
    register.addEntry(e1);
    register.addEntry(e2);

    boolean removed = register.removeEntry(e1.getEntryID());

    assertTrue(removed);
    assertEquals(1, register.getNumberOfEntries());

    assertEquals("Title2", register.getAll().getFirst().getTitle());
  }

  @Test
  void getStatistics_calculates_correct_totals(){
    register.addEntry(new DiaryEntry(author, "Title1", "word1 word2", LocalDateTime.now()));
    register.addEntry(new DiaryEntry(author, "Title2", "word3 word4 word5", LocalDateTime.now()));

    String stats = register.getStatistics(author.getId());

    assertTrue(stats.contains("Total entries: 2"));
    assertTrue(stats.contains("Total word count: 5"));
  }

}
