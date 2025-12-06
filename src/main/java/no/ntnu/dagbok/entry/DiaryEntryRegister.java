package no.ntnu.dagbok.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages a collection of DiaryEntry objects.
 *
 * <p>Stores, retrieve, search, and remove diary entries. The entries are stored in a list and are
 * sorted by ascending date/time and then by author ID.
 */
public class DiaryEntryRegister {

  private static final Comparator<DiaryEntry> ORDER =
      Comparator.comparing(DiaryEntry::getDateTime).thenComparing(e -> e.getAuthor().getId());

  private final List<DiaryEntry> entries = new ArrayList<>();

  /**
   * Adds a new diary entry to the register.
   *
   * @param entry The diary entry to be added. Must not be null.
   * @throws NullPointerException if the entry is null.
   */
  public void addEntry(DiaryEntry entry) {
    Objects.requireNonNull(entry, "entry must not be null");
    // Author author = Objects.requireNonNull(entry.getAuthor(), "author of entry must not be
    // null");

    entries.add(entry);
  }

  /**
   * Finds all diary entries for a specific calendar date.
   *
   * <p><i>Submapping logic provided by ChatGPT.</i>
   *
   * @param date The date to search for.
   * @return An unmodifiable, sorted list of entries matching the date.
   * @throws NullPointerException if the date is null.
   */
  public List<DiaryEntry> findByDate(LocalDate date) {
    Objects.requireNonNull(date, "date must not be null");
    List<DiaryEntry> day = new ArrayList<>();
    for (DiaryEntry e : entries) {
      if (e.getDateTime().toLocalDate().equals(date)) {
        day.add(e);
      }
    }
    day.sort(ORDER);
    return Collections.unmodifiableList(day);
  }

  /**
   * Finds diary entries within a specific time range.
   *
   * <p>The range is half-open: [fromInclusive, toExclusive).
   *
   * <p><i>Implementation assistance from ChatGPT.</i>
   *
   * @param fromInclusive The start time of the search (inclusive).
   * @param toExclusive The end time of the search (exclusive).
   * @return An unmodifiable, sorted list of entries within the time range.
   * @throws NullPointerException if any parameter is null.
   * @throws IllegalArgumentException if 'from' time is after 'to' time.
   */
  public List<DiaryEntry> findBetween(LocalDateTime fromInclusive, LocalDateTime toExclusive) {
    Objects.requireNonNull(fromInclusive, "Time from must not be null");
    Objects.requireNonNull(toExclusive, "Time to must not be null");
    if (!fromInclusive.isBefore(toExclusive)) {
      throw new IllegalArgumentException("From time must be before after time");
    }
    List<DiaryEntry> out = new ArrayList<>();
    for (DiaryEntry entry : entries) {
      LocalDateTime dateTime = entry.getDateTime();
      if ((dateTime.equals(fromInclusive) || dateTime.isAfter(fromInclusive))
          && dateTime.isBefore(toExclusive)) {
        out.add(entry);
      }
    }
    out.sort(ORDER);
    return Collections.unmodifiableList(out);
  }

  /**
   * Removes a diary entry by its unique ID.
   *
   * @param entryId The UUID of the entry to remove.
   * @return {@code true} if the entry was found and removed, {@code false} otherwise.
   * @throws NullPointerException if entryId is null.
   */
  public boolean removeEntry(UUID entryId) {
    Objects.requireNonNull(entryId, "entryId must not be null");
    return entries.removeIf(e -> e.getEntryId().equals(entryId));
  }

  /**
   * Retrieves all diary entries in the register.
   *
   * @return An unmodifiable list of all entries, sorted by date and author.
   */
  public List<DiaryEntry> getAll() {
    List<DiaryEntry> listCopy = new ArrayList<>(entries);
    listCopy.sort(ORDER);
    return Collections.unmodifiableList(listCopy);
  }

  /**
   * Finds all diary entries written by a specific author.
   *
   * <p><i>Collections implemented with help from ChatGPT.</i>
   *
   * @param authorId The UUID of the author.
   * @return An unmodifiable, sorted list of the author's entries.
   * @throws NullPointerException if authorId is null.
   */
  public List<DiaryEntry> findByAuthor(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    List<DiaryEntry> output = new ArrayList<>();
    for (DiaryEntry entry : entries) {
      if (authorId.equals(entry.getAuthor().getId())) {
        output.add(entry);
      }
    }
    output.sort(ORDER);
    return Collections.unmodifiableList(output);
  }

  /**
   * Searches for diary entries containing a specific keyword
   *
   * <p>The search is case-insensitive and checks both the title and the text content. The results
   * are sorted according to the standard order (date/time then authorId).
   *
   * <p><i>Stream implementation suggested by ChatGPT.</i>
   *
   * @param keyword The keyword to be searched for.
   * @return An unmodifiable list of matching diary entries. Returns an empty list if keyword is
   *     blank.
   * @throws NullPointerException if the keyword is null.
   */
  public List<DiaryEntry> searchByKeyword(String keyword) {
    Objects.requireNonNull(keyword, "Keyword cannot be null");
    String search = keyword.trim().toLowerCase();

    if (search.isEmpty()) {
      return Collections.emptyList();
    }

    return entries.stream().filter(e -> matchesKeyword(e, search)).sorted(ORDER).toList();
  }

  /**
   * Determines if a single entry matches the search criteria.
   *
   * <p><i>Use of streams in search suggested by ChatGPT.</i>
   *
   * @param entry The entry being checked.
   * @param search The term being searched for. Trimmed and in lower case.
   * @return boolean {@code true} if entry title or text contains the search term, {@code false}
   *     otherwise.
   */
  private boolean matchesKeyword(DiaryEntry entry, String search) {
    return entry.getTitle().toLowerCase().contains(search)
        || entry.getText().toLowerCase().contains(search);
  }

  /**
   * Counts the total number of entries written by a specific author.
   *
   * @param authorId The UUID of the author.
   * @return The number of entries found.
   * @throws NullPointerException if authorId is null.
   */
  public long countByAuthor(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    long count = 0;
    for (DiaryEntry entry : entries) {
      if (authorId.equals(entry.getAuthor().getId())) {
        count++;
      }
    }
    return count;
  }

  /**
   * Generates a statistical summary for a specific author.
   *
   * <p>Includes total entries, total word count, average word count, and identifies the longest and
   * shortest entries.
   *
   * <p><i>Stream formatting provided by AI.</i>
   *
   * @param authorId The UUID of the author to generate statistics for.
   * @return A formatted string containing the user statistics.
   * @throws NullPointerException if authorId is null.
   */
  public String getStatistics(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");

    List<DiaryEntry> userEntries = findByAuthor(authorId);

    if (userEntries.isEmpty()) {
      return "No statistics availale - Author has no entries.";
    }

    long totalEntries = userEntries.size();

    long totalWords = userEntries.stream().mapToInt(DiaryEntry::getWordCount).sum();

    StringBuilder sb = new StringBuilder();

    sb.append("--- Diary Statistics ---\n");
    sb.append("Total entries: ").append(totalEntries).append("\n");
    sb.append("Total word count: ").append(totalWords).append("\n");
    sb.append("Average word count: ").append(totalWords / totalEntries).append("\n");

    DiaryEntry longestEntry =
        userEntries.stream().max(Comparator.comparingInt(DiaryEntry::getWordCount)).orElse(null);

    DiaryEntry shortestEntry =
        userEntries.stream().min(Comparator.comparingInt(DiaryEntry::getWordCount)).orElse(null);

    if (longestEntry != null) {
      sb.append("Longest entry: ")
          .append(longestEntry.getTitle())
          .append(" (")
          .append(longestEntry.getWordCount())
          .append(" words)\n");
    }

    if (shortestEntry != null) {
      sb.append("Shortest entry: ")
          .append(shortestEntry.getTitle())
          .append(" (")
          .append(shortestEntry.getWordCount())
          .append(" words)\n");
    }

    return sb.toString();
  }

  /**
   * Returns the total number of entries in the register.
   *
   * @return The size of the entry list.
   */
  public int getNumberOfEntries() {
    return entries.size();
  }

  /** Removes all entries from the register. */
  public void clearDiaryEntryRegister() {
    entries.clear();
  }
}
