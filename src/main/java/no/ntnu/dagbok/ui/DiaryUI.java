package no.ntnu.dagbok.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import no.ntnu.dagbok.author.Author;
import no.ntnu.dagbok.author.AuthorRegister;
import no.ntnu.dagbok.entry.DiaryEntry;
import no.ntnu.dagbok.entry.DiaryEntryRegister;

/**
 * Console-based user interface for diary application.
 *
 * <p> Responsibilities:
 * <ul>
 * <li>Prompt the user for interaction and parse input.</li>
 * <li>Handle input robustly without throwing errors for bad input.</li>
 * <li>Delegates actual operations to {@link no.ntnu.dagbok.entry.DiaryEntryRegister} and {@link no.ntnu.dagbok.author.AuthorRegister}.</li>
 * </ul>
 * </p>
 * <p> Conventions:
 * <ul>
 *   <li>Date/Time precision is in minutes. All time related prompts are in the pattern {@code yyyy-MM-dd HH:mm}.</li>
 *   <li>Lists are sorted by ascending date/time, then ascending author id. Lists are read-only.</li>
 *   <li>Author id and date/time identify unique entries for search/editing/deletion.</li>
 * </ul>
 *
 * </p>
 */
public class DiaryUI {
  private static final int ADD_ENTRY = 1;
  private static final int LIST_ALL = 2;
  private static final int SEARCH_BY_DATE = 3;
  private static final int DELETE_ENTRY = 4;
  private static final int LIST_BY_AUTHOR = 5;
  private static final int EDIT_ENTRY = 6;
  private static final int TODAY_ENTRIES = 7;
  private static final int EXIT_PROGRAM = 0;

  private static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
  private static final String PATTERN_DATE = "yyyy-MM-dd";

  private static final DateTimeFormatter DF_MINUTE = DateTimeFormatter.ofPattern(PATTERN_MINUTE);
  private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);

  private final Scanner scanner = new Scanner(System.in);
  private final DiaryEntryRegister register;
  private final AuthorRegister authors;

  public DiaryUI(DiaryEntryRegister register, AuthorRegister authors){
    this.register = register;
    this.authors = authors;
  }

  /**
   * Seeds demo author and entries on starting the program.
   */
  public void init(){

    Author lars = authors.addAuthor("Lars");
    Author lisa = authors.addAuthor("Lisa");
    DiaryEntry larsEntry1 = new DiaryEntry(lars,"Title 1", "Text 1 Lars", LocalDateTime.now());
    DiaryEntry lisaEntry1 = new DiaryEntry(lisa,"Title 2", "Text 2 Lisa", LocalDateTime.now().minusDays(3).withHour(12).withMinute(20));
    DiaryEntry larsEntry2 = new DiaryEntry(lars, "Title 3", "Text 3 Lars", LocalDateTime.now().minusDays(4).withHour(15).withMinute(22));
    register.addEntry(larsEntry1);
    register.addEntry(lisaEntry1);
    register.addEntry(larsEntry2);
  }

  /**
   * Run the main menu loop until user exits.
   * Doesn't throw due to invalid input. Instead, ask for new input.
   */
  public void start(){
    boolean finished = false;
    while (!finished) {
      int choice = displayMenu();
      switch (choice){
        case ADD_ENTRY -> addEntry();
        case LIST_ALL -> listAll();
        case SEARCH_BY_DATE -> searchByDate();
        case DELETE_ENTRY -> deleteEntry();
        case LIST_BY_AUTHOR -> listByAuthor();
        case EDIT_ENTRY -> editEntry();
        case TODAY_ENTRIES -> todayEntries();
        case EXIT_PROGRAM -> {
          System.out.println("Exiting program");
          finished = true;
        }
        default -> System.out.println("Invalid option");
      }
    }
  }

  /**
   * Displays the menu options in the console.
   * @return readInt
   */
  private int displayMenu(){
    System.out.println("--- Diary-Software ---");
    System.out.println("1. Add diary entry");
    System.out.println("2. List all entries ");
    System.out.println("3. Search by date");
    System.out.println("4. Delete diary entry");
    System.out.println("5. List entries by author");
    System.out.println("6. Edit diary entry (title/text)");
    System.out.println("7. Show today's diary entries");
    System.out.println("0. Exit program");
    return readInt("Pick option");
  }

  /**
   * Prompts for author, title, text, and date/time (yyyy-MM-dd HH:mm), then add the entry.
   * Creates the author if not found in register (asks for confirmation).
   * Uses minute-level time precision.
   * On invalid data or duplicate (identical author and time), prints errror and returns without throwing error.
   */
  private void addEntry(){
    try {
    String authorName = readLine("Author name: ");
    Author author = authors.findByName(authorName).orElse(null);
    if (author == null) {
      boolean create = readYesNo("Author not found. Add " + authorName + " as author? (y/n): ");
      if (!create){
        System.out.println("Operation cancelled");
        return;
      }
      author = authors.addAuthor(authorName);
    }
    String title = readLine("Title: ");
    String text = readLine("Text: ");
    LocalDateTime dateTime = readDateTime("Date/Time (" + PATTERN_MINUTE + "): ");
    DiaryEntry inputEntry = new DiaryEntry(author,title,text,dateTime);
    register.addEntry(inputEntry);
    System.out.println("Entry added");}
    catch (RuntimeException e) {
      System.out.println("Could not add diary entry: " +e.getMessage());
    }
  }

  /**
   * Method to edit existing diary entries
   */
  private void editEntry(){
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> foundEntries = register.findByDate(date);

    if (foundEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to edit?");
    for (int i = 0; i < foundEntries.size(); i++){
      DiaryEntry diaryEntry = foundEntries.get(i);
      System.out.printf("%d: %s (%s)%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime())
      );
    }

    int choice = readInt("Write the number of the entry");

    if (choice < 1 || choice > foundEntries.size()){
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToEdit = foundEntries.get(choice - 1);

    System.out.println("---Entry to Edit---");
    showEntry(entryToEdit);
    System.out.println();

    if (readYesNo("Do you want to edit the title? (y/n): ")){
      try {
      String newTitle = readLine("New title: ");
      entryToEdit.setTitle(newTitle); }
      catch (IllegalArgumentException e) {
        System.out.println("Error updating title: " + e.getMessage());
      }
    } else{
      System.out.println("Entry title not edited");
    }

    if (readYesNo("Do you want to edit the text? (y/n): ")){
      try {
      String newText = readLine("New text: ");
      entryToEdit.setText(newText);
      } catch (IllegalArgumentException e) {
        System.out.println("Error updating text: " + e.getMessage());
      }
    } else{
      System.out.println("Entry text not edited");
    }
  }

  /**
   * Method to show a list of diary entries made today.
   */
  private void todayEntries(){
    list(register.findByDate(LocalDate.now()));
  }

  /**
   * Method to show a list of all diary entries.
   */
  private void listAll(){
    list(register.getAll());
  }

  /**
   * Method to show a list of entries from a given author.
   */
  private void listByAuthor(){
    String authorName = readLine("Author's name: ");
    Optional<Author> a = authors.findByName(authorName);
    if (a.isEmpty()){
      System.out.println("No author found by that name.");
      return;
    }
    list(register.findByAuthor(a.get().getId()));
  }

  /**
   * Method to show a list of all entries from a given date.
   */
  private void searchByDate(){
    LocalDate date = readDate("Date ("+ PATTERN_DATE +"):");
    List<DiaryEntry> found = register.findByDate(date);
    if (found.isEmpty()) System.out.println("No diary entries from this date");
    else list(found);
  }

  /**
   * Method to provide a menu to delete entries from diary entry register
   */
  private void deleteEntry(){
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> foundEntries = register.findByDate(date);

    if (foundEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to preview/delete?");
    for (int i = 0; i < foundEntries.size(); i++){
      DiaryEntry diaryEntry = foundEntries.get(i);
      System.out.printf("%d: %s (%s)%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime())
      );
    }

    int choice = readInt("Write the number of the entry");

    if (choice < 1 || choice > foundEntries.size()){
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToDelete = foundEntries.get(choice - 1);

    System.out.println("*** Entry Selected for Deletion ***");
    System.out.println("_______________________________");
    showEntry(entryToDelete);
    System.out.println("--___________________________--");
    if (readYesNo("Are you certain you want to delete this entry? (y/n): ")){
      boolean removed = register.removeEntry(entryToDelete.getEntryID());
      if (removed){
        System.out.println("Diary entry deleted");
      } else {
        System.out.println("Error: Entry not deleted");
      }

    } else{
      System.out.println("Entry not deleted");
    }
  }

  /**
   * Helper method for listing entries.
   * Filtering fixed with chatGPT
   * @param entries A list of diary entries
   */
  public void list(List<DiaryEntry> entries){
    if (entries.isEmpty()){
      System.out.println("No entries found");
      return;
    }
    entries.forEach(e -> {
      showEntry(e);
      System.out.println();
    });
  }

  /**
   * Helper method for rendering entries
   * @param entry The entry to be shown
   */
  private void showEntry(DiaryEntry entry){
    String timeStamp = DF_MINUTE.format(entry.getDateTime());
    System.out.println("[" + timeStamp + "] " + entry.getAuthor().getDisplayName());
    System.out.println(entry.getTitle());
    System.out.println(entry.getText());
  }

  // Input helper methods

  private String readLine(String input){
    System.out.print(input);
    String s = scanner.nextLine();
    while (s == null || s.trim().isEmpty()){
      System.out.println("Cannot be empty: " + input);
      s = scanner.nextLine();
    }
    return s.trim();
  }

  private int readInt(String input){
    while (true) {
      System.out.println(input);
      String s = scanner.nextLine();
      try { return Integer.parseInt(s.trim());
      } catch (NumberFormatException e){
        System.out.println("Input must be a number");
      }
    }
    }
  private LocalDate readDate(String input){
    while (true){
      System.out.print(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDate.parse(s,DF_DATE);
      } catch (Exception e){
        System.out.println("Invalid date format. Use " + DF_DATE);
      }
    }
  }

  private LocalDateTime readDateTime(String input){
    while (true){
      System.out.print(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDateTime.parse(s, DF_MINUTE);
      } catch (Exception e){
        System.out.println("Invalid format. Use "+ DF_MINUTE);
      }
    }
  }

  private boolean readYesNo(String input){
    while (true){
      System.out.print(input);
      String s = scanner.nextLine().trim().toLowerCase();
      if (s.equals("y")) return true;
      if (s.equals("n")) return false;
      System.out.println("Type y/n for yes or no");
    }
  }


}
