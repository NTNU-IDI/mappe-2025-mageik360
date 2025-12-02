package no.ntnu.dagbok.entry;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import no.ntnu.dagbok.author.Author;
import java.time.temporal.ChronoUnit;


/**
 * Stores diary entries based on (dateTime, authorId) key
 * Ordered first by date/time ascending, then author id ascending
 *
 */
public class DiaryEntryRegister{

  private static final Comparator<DiaryEntry> ORDER = Comparator.comparing(DiaryEntry::getDateTime).thenComparing(e -> e.getAuthor().getId());

  private final List<DiaryEntry> entries = new ArrayList<>();

  /**
   *
   * @param entry
   */
  public void addEntry(DiaryEntry entry){
    Objects.requireNonNull(entry, "entry must not be null");
    //LocalDateTime dateTimeTemp = Objects.requireNonNull(entry.getDateTime(), "dateTime of entry must not be null");
    Author author = Objects.requireNonNull(entry.getAuthor(), "author of entry must not be null");
    //UUID authorId = Objects.requireNonNull(author.getId(), "id of author of entry must not be null");


    entries.add(entry);
  }

  /**
   * Get entry by exact dateTime and authorId
   * @param dateTime
   * @param authorId
   * @return
   */
  public Optional<DiaryEntry> getEntry(LocalDateTime dateTime, UUID authorId){
    Objects.requireNonNull(dateTime, "dateTime must not be null");
    Objects.requireNonNull(authorId, "authorId must not be null");
    for (DiaryEntry e: entries){
      if (dateTime.equals(e.getDateTime()) && authorId.equals(e.getAuthor().getId())){
  return Optional.of(e);
      }
    }
    return Optional.empty();
  }

  /**
   * Find a list of entries from a calendar date
   * Submapping provided by ChatGPT
   * @param date
   * @return
   */
  public List<DiaryEntry> findByDate(LocalDate date){
    Objects.requireNonNull(date, "date must not be null");
    List<DiaryEntry> day = new ArrayList<>();
    for (DiaryEntry e : entries){
      if (e.getDateTime().toLocalDate().equals(date)){
        day.add(e);
      }
    }
    day.sort(ORDER);
    return Collections.unmodifiableList(day);
  }

  /**
   * Returns a list of entries made between two dates
   * Made with help from chatGPT
   * @param fromInclusive
   * @param toExclusive
   * @return
   */
  public List<DiaryEntry> findBetween(LocalDateTime fromInclusive, LocalDateTime toExclusive){
    Objects.requireNonNull(fromInclusive, "Time from must not be null");
    Objects.requireNonNull(toExclusive, "Time to must not be null");
    if (!fromInclusive.isBefore(toExclusive)){
      throw new IllegalArgumentException("From time must be before after time");
    }
    List<DiaryEntry> out = new ArrayList<>();
    for (DiaryEntry entry : entries){
      LocalDateTime dateTime = entry.getDateTime();
      if ((dateTime.equals(fromInclusive) || dateTime.isAfter(fromInclusive)) && dateTime.isBefore(toExclusive)){
        out.add(entry);
      }
    }
    out.sort(ORDER);
    return Collections.unmodifiableList(out);
  }

  /**
   * Removes entry by entryID
   * @param entryID UUID of DiaryEntry
   * @return
   */
  public boolean removeEntry(UUID entryID){
    Objects.requireNonNull(entryID, "entryID must not be null");
    return entries.removeIf(e -> e.getEntryID().equals(entryID));
  }

  /**
   * Removes entry by entry
   * @param entry DiaryEntry object
   * @return
   */
  public boolean removeEntry(DiaryEntry entry){
    Objects.requireNonNull(entry, "entry must not be null");
    return removeEntry(entry.getEntryID());
  }

  public List<DiaryEntry> getAll(){
    List<DiaryEntry> listCopy = new ArrayList<>(entries);
    listCopy.sort(ORDER);
    return Collections.unmodifiableList(listCopy);
  }

  public List<DiaryEntry> findByAuthor(UUID authorId){
    Objects.requireNonNull(authorId, "authorId must not be null");
    List<DiaryEntry> output = new ArrayList<>();
    for (DiaryEntry entry : entries) {
      if (authorId.equals(entry.getAuthor().getId())){
        output.add(entry);
      }
    }
    output.sort(ORDER);
    return Collections.unmodifiableList(output);
  }

  public long countByAuthor(UUID authorId){
    Objects.requireNonNull(authorId, "authorId must not be null");
    long count = 0;
    for (DiaryEntry entry: entries){
      if (authorId.equals(entry.getAuthor().getId())) count++;
    }
    return count;
  }


  public int getNumberOfEntries(){
    return entries.size();
  }

  /**
   * Removes entries in diary entry register
   */
  public void clearDiaryEntryRegister(){
    entries.clear();
  }

}
