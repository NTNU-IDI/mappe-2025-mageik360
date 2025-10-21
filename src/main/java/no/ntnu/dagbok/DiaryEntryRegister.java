package no.ntnu.dagbok;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Overview of all diary entries
 * The ArrayList stores diary entries.
 *
 */
public class DiaryEntryRegister {
  private final List<DiaryEntry> entries = new ArrayList<>();

  /**
   * Adds a diary entry
   * Filtering fixed by chatGPT
   * @param diaryEntry diaryEntry
   * @return false if an entry from the same author has the same timestamp
   */
  public boolean addDiaryEntry(DiaryEntry diaryEntry){
    Objects.requireNonNull(diaryEntry, "Entry must be non-null");
    boolean exists = entries.stream().anyMatch(x->
        x.getAuthor().equals(diaryEntry.getAuthor()) && x.getDateTime().equals(diaryEntry.getDateTime()));
    if (exists) return false;
    entries.add(diaryEntry);
    return true;
  }

  /**
   * Finds all entries entries
   * @return read-only copy of entries
   */
  public List<DiaryEntry> getAllEntries(){
    return List.copyOf(entries);
  }

  /**
   * Finds entries from a specific date
   * @param date Calendar date
   * @return List of entries from the date
   */
  public List<DiaryEntry> findFromDate(LocalDate date){
    Objects.requireNonNull(date, "Date must be non-null");
    return entries.stream().filter(e -> e.getDateTime().toLocalDate().isEqual(date))
        .toList();
  }

  /**
   * Delete diary entry based on timestamp
   * @param dateTime Exact timestamp of diary entry
   * @return Boolean of success
   */
  public boolean deleteByDateTime(LocalDateTime dateTime){
    Objects.requireNonNull(dateTime, "Date/Time must be non-null");
    LocalDateTime time = dateTime.truncatedTo(ChronoUnit.MINUTES);
    return entries.removeIf(e -> e.getDateTime()
        .truncatedTo(ChronoUnit.MINUTES)
        .equals(time));
  }
}
