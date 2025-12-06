package no.ntnu.dagbok.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.Test;

/** Tests for diary entry register ordering functionality. */
public class DiaryEntryRegisterOrderTest {

  /**
   * Test class to check that entries are correctly sorted by date.
   *
   * <p>assert equals for each entry in the correct position.
   */
  @Test
  void entries_are_always_sorted_chronologically() {
    DiaryEntryRegister register = new DiaryEntryRegister();
    Author author = new Author("Tester", "pass");

    DiaryEntry morning =
        new DiaryEntry(author, "Morning", "...", LocalDateTime.of(2025, 1, 1, 8, 0));
    DiaryEntry evening =
        new DiaryEntry(author, "Evening", "...", LocalDateTime.of(2025, 1, 1, 20, 0));
    DiaryEntry noon = new DiaryEntry(author, "Noon", "...", LocalDateTime.of(2025, 1, 1, 12, 0));

    register.addEntry(evening);
    register.addEntry(morning);
    register.addEntry(noon);

    List<DiaryEntry> all = register.getAll();

    assertEquals(morning, all.getFirst(), "First entry should be morning");
    assertEquals(noon, all.get(1), "Second entry should be noon");
    assertEquals(evening, all.get(2), "Last entry should be evening");
  }

  /**
   * Test to check that findByDate only returns correctly dated entries.
   *
   * <p>assertEquals for correct results length. assertEquals for correct title.
   */
  @Test
  void findByDate_returns_only_entries_for_specific_date() {
    DiaryEntryRegister register = new DiaryEntryRegister();
    Author author = new Author("Tester", "pass");

    LocalDateTime today = LocalDateTime.now();
    LocalDateTime yesterday = today.minusDays(1);

    register.addEntry(new DiaryEntry(author, "T1", "Text", today));
    register.addEntry(new DiaryEntry(author, "T2", "Text", yesterday));

    List<DiaryEntry> results = register.findByDate(today.toLocalDate());

    assertEquals(1, results.size());
    assertEquals("T1", results.getFirst().getTitle());
  }

  /**
   * Test to check that findByDate returns an empty list if there are no search matches, not a null
   * result.
   *
   * <p>Asserts not null. Asserts true for empty list.
   */
  @Test
  void findByDate_returns_empty_list_if_no_match() {
    DiaryEntryRegister register = new DiaryEntryRegister();

    List<DiaryEntry> results = register.findByDate(java.time.LocalDate.now());

    assertNotNull(results, "Should return empty list, not null");
    assertTrue(results.isEmpty());
  }
}
