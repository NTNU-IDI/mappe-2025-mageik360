package no.ntnu.dagbok.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import no.ntnu.dagbok.author.Author;
import org.junit.jupiter.api.Test;

public class DiaryEntryRegisterFindBetweenTest {

  private final String dummyPassword = "dummyPassword";

  /**
   * Tests to check that returned entries from register are sorted and unmodifiable
   *
   * <p>assertTrue for correct order. assertThrows for adding entry to returned result. Made with
   * help from chatGPT.
   */
  @Test
  void returns_entries_in_half_open_interval_sorted_and_unmodifiable() {
    DiaryEntryRegister reg = new DiaryEntryRegister();
    Author a = new Author("Lars", dummyPassword);
    Author b = new Author("Linda", dummyPassword);

    LocalDateTime t0900 = LocalDateTime.of(2025, 11, 8, 9, 0, 30);
    LocalDateTime t0930 = LocalDateTime.of(2025, 11, 8, 9, 30, 5);
    LocalDateTime t1000 = LocalDateTime.of(2025, 11, 8, 10, 0, 0);
    LocalDateTime t1030 = LocalDateTime.of(2025, 11, 8, 10, 30, 59);

    reg.addEntry(new DiaryEntry(a, "A2", "a", t0930));
    reg.addEntry(new DiaryEntry(b, "B1", "a", t1000));
    reg.addEntry(new DiaryEntry(a, "A1", "a", t0900));
    reg.addEntry(new DiaryEntry(b, "B2", "a", t1030));

    LocalDateTime from = LocalDateTime.of(2025, 11, 8, 9, 30);
    LocalDateTime to = LocalDateTime.of(2025, 11, 8, 10, 30);

    List<DiaryEntry> result = reg.findBetween(from, to);
    assertEquals(2, result.size());

    assertTrue(!result.get(0).getDateTime().isAfter(result.get(1).getDateTime()));

    assertThrows(UnsupportedOperationException.class, () -> result.add(result.getFirst()));
  }

  /**
   * Tests that invalid time intervals throw an IllegalArgumentException.
   *
   * <p>assertThrows for start time equal to end time. assertThrows for start time after end time.
   */
  @Test
  void throws_if_from_not_before_to() {
    DiaryEntryRegister reg = new DiaryEntryRegister();
    LocalDateTime t = LocalDateTime.of(2025, 11, 8, 9, 0);
    assertThrows(IllegalArgumentException.class, () -> reg.findBetween(t, t));
    assertThrows(IllegalArgumentException.class, () -> reg.findBetween(t.plusMinutes(1), t));
  }

  /**
   * Tests for findBetween search functionality respects exact time precision after refactored code.
   *
   * <p>assertEquals for correct number of returned entries. assertEquals for matching time.
   */
  @Test
  void findBetween_respects_exact_time_precision() {
    DiaryEntryRegister reg = new DiaryEntryRegister();
    Author a = new Author("Karoline", dummyPassword);
    LocalDateTime exactTime = LocalDateTime.of(2025, 11, 8, 9, 0, 45);
    reg.addEntry(new DiaryEntry(a, "A", "a", exactTime));
    List<DiaryEntry> result =
        reg.findBetween(
            LocalDateTime.of(2025, 11, 8, 9, 0, 10), LocalDateTime.of(2025, 11, 8, 9, 1, 50));
    assertEquals(1, result.size());
    assertEquals(exactTime, result.getFirst().getDateTime());
  }
}
