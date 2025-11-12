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
    LocalDateTime dateTimeTemp = toMinute(Objects.requireNonNull(entry.getDateTime(), "dateTime of entry must not be null"));
    Author author = Objects.requireNonNull(entry.getAuthor(), "author of entry must not be null");
    UUID authorId = Objects.requireNonNull(author.getId(), "id of author of entry must not be null");

    for (DiaryEntry e : entries) {
      if (toMinute(e.getDateTime()).equals(dateTimeTemp) && authorId.equals(e.getAuthor().getId())){
        throw new IllegalArgumentException("Duplicated entry in diary registry: Date/Time: "+ dateTimeTemp +" and author ID: "+ authorId);
      }
    }
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
    LocalDateTime roundedDateTime = toMinute(dateTime);
    for (DiaryEntry e: entries){
      if (roundedDateTime.equals(e.getDateTime()) && authorId.equals(e.getAuthor().getId())){
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
   * Removes entry by exact dateTime and authorId
   * @param dateTime
   * @param authorId
   * @return
   */
  public boolean removeEntry(LocalDateTime dateTime, UUID authorId){
    Objects.requireNonNull(dateTime, "dateTime must not be null");
    Objects.requireNonNull(authorId, "authorId must not be null");
    LocalDateTime roundedDateTime = toMinute(dateTime);
    ListIterator<DiaryEntry> iterator = entries.listIterator();
    while (iterator.hasNext()){
      DiaryEntry entry = iterator.next();
      if (roundedDateTime.equals(entry.getDateTime())
          && authorId.equals(entry.getAuthor().getId())) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  /**
   * Removes entry by entry
   * @param entry
   * @return
   */
  public boolean removeEntry(DiaryEntry entry){
    Objects.requireNonNull(entry, "entry must not be null");
    return removeEntry(entry.getDateTime(), entry.getAuthor().getId());
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

  private static LocalDateTime toMinute(LocalDateTime dateTimeInput){
    return dateTimeInput.truncatedTo(ChronoUnit.MINUTES);
  }

}
