package no.ntnu.dagbok.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
  private static final int EDIT_ENTRY = 5;
  private static final int TODAY_ENTRIES = 6;
  private static final int SEARCH_BY_KEYWORD = 7;
  private static final int SEARCH_BETWEEN = 8;
  private static final int STATISTICS = 9;
  private static final int LIST_BY_AUTHOR = 10;
  private static final int GLOBAL_STATISTICS = 11;
  private static final int EXIT_PROGRAM = 0;

  private static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
  private static final String PATTERN_DATE = "yyyy-MM-dd";

  private static final DateTimeFormatter DF_MINUTE = DateTimeFormatter.ofPattern(PATTERN_MINUTE);
  private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);

  private final Scanner scanner = new Scanner(System.in);
  private final DiaryEntryRegister register;
  private final AuthorRegister authors;

  private Author currentUser;

  public DiaryUI(DiaryEntryRegister register, AuthorRegister authors){
    this.register = register;
    this.authors = authors;
  }

  /**
   * Seeds demo author and entries on starting the program.
   */
  public void init(){

    Author lars = authors.addAuthor("Lars", "password");
    Author lisa = authors.addAuthor("Lisa", "password");
    Author admin = authors.addAuthor("admin", "admin123");
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

    System.out.println("Diary Application Starting...");

    loginOrRegister();

    System.out.println("\n\nWelcome, " + currentUser.getDisplayName() + "!" );

    boolean finished = false;
    while (!finished) {
      int choice = displayMenu();
      switch (choice){
        case ADD_ENTRY -> addEntry();
        case LIST_ALL -> listAll();
        case SEARCH_BY_DATE -> searchByDate();
        case DELETE_ENTRY -> deleteEntry();
        case EDIT_ENTRY -> editEntry();
        case TODAY_ENTRIES -> todayEntries();
        case SEARCH_BY_KEYWORD -> searchByKeyword();
        case SEARCH_BETWEEN -> searchBetweenDates();
        case STATISTICS -> showStatistics();
        case LIST_BY_AUTHOR -> {
          if (isAdmin()) listByAuthor();
          else System.out.println("Invalid option.");
        }
        case GLOBAL_STATISTICS -> {
            if (isAdmin()) showGlobalStatsTable();
            else System.out.println("Invalid option.");
        }
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

    System.out.println("5. Edit diary entry (title/text)");
    System.out.println("6. Show today's diary entries");
    System.out.println("7. Search by keyword/phrase");
    System.out.println("8. View by date range");
    System.out.println("9. View diary statistics");
    if (isAdmin()){
      System.out.println("10. List entries by author (Admin)");
      System.out.println("11. View global statistics (Admin)");
    }
    System.out.println("0. Exit program");
    return readInt("Pick option ");
  }

  /**
   * Method to allow logged-in user to add diary entry.
   */
  private void addEntry(){
    System.out.println("Creating entry as: " + currentUser.getDisplayName());

    String title = readLine("Title: ");
    String text = readLine("Text: ");
    LocalDateTime dateTime;
    if (readYesNo("Use current time (" + DF_MINUTE.format(LocalDateTime.now()) +")? (y/n): ")){
      dateTime = LocalDateTime.now();
    } else {
      dateTime = readDateTime("Enter date/time (" + PATTERN_MINUTE + "): ");
    }

    DiaryEntry inputEntry = new DiaryEntry(currentUser, title, text, dateTime);
    register.addEntry(inputEntry);
    System.out.println("Entry successfully added");
  }

  /**
   * Method to edit existing diary entries
   */
  private void editEntry(){
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> foundEntries = register.findByDate(date);
    List<DiaryEntry> viewableEntries = applyAccessControl(foundEntries);

    if (foundEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to edit?");
    for (int i = 0; i < viewableEntries.size(); i++){
      DiaryEntry diaryEntry = viewableEntries.get(i);
      System.out.printf(
          "%d: %s (%s) - [%s]%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime()),
          diaryEntry.getAuthor().getDisplayName()
      );
    }

    int choice = readInt("Write the number of the entry: ");

    if (choice < 1 || choice > viewableEntries.size()){
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToEdit = viewableEntries.get(choice - 1);

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
   * Method to let user search for keyword/phrase.
   */
  private void searchByKeyword(){
    String keyword = readLine("Search for word/phrase: ");
    List<DiaryEntry> results = register.searchByKeyword(keyword);

    List<DiaryEntry> visibleResults = applyAccessControl(results);
    if (results.isEmpty()){
      System.out.println("No entries found containing keyword: " + keyword);
    } else {
      System.out.println("Found " + visibleResults.size() + " matches in diary entries: ");
      list(visibleResults);
    }
  }

  /**
   * Method to show user's diary statistics.
   */
  private void showStatistics(){
    System.out.println(register.getStatistics(currentUser.getId()));
    System.out.println("Press enter to go back to menu.");
    scanner.nextLine();
  }

  /**
   * Method to show a list of diary entries made today.
   * Filter based on access rights.
   */
  private void todayEntries(){
    List<DiaryEntry> allToday = register.findByDate(LocalDate.now());
    list(applyAccessControl(allToday));
  }

  /**
   * Method to show a list of diary entries.
   * Admin sees all entries. Normal users see their own entries.
   */
  private void listAll(){
    List<DiaryEntry> entriesToShow;

    if (isAdmin()){
      System.out.println("--- *** ADMIN ACCESS: All Entries Visisble *** ---");
      entriesToShow = register.getAll();
    } else {
      System.out.println("--- All entries for " + currentUser.getDisplayName() + " ---");
      entriesToShow = register.findByAuthor(currentUser.getId());
    }
    list(entriesToShow);
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
    List<DiaryEntry> allFound = register.findByDate(date);

    List<DiaryEntry> viewable = applyAccessControl(allFound);
    if (viewable.isEmpty()) System.out.println("No diary entries from this date");
    else list(viewable);
  }

  /**
   * Method to provide a menu to delete entries from diary entry register
   */
  private void deleteEntry(){
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> allEntriesFound = register.findByDate(date);
    List<DiaryEntry> viewableEntries = applyAccessControl(allEntriesFound);

    if (viewableEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to delete?");
    for (int i = 0; i < viewableEntries.size(); i++){
      DiaryEntry diaryEntry = viewableEntries.get(i);
      System.out.printf("%d: %s (%s) - [%s]%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime()),
          diaryEntry.getAuthor().getDisplayName()
      );
    }

    int choice = readInt("Write the number of the entry");

    if (choice < 1 || choice > viewableEntries.size()){
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToDelete = viewableEntries.get(choice - 1);

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
   * Searches for entries between a given time range.
   */
  private void searchBetweenDates(){
    System.out.println("--- Search by Time Range ---");
    LocalDateTime from = readDateTime("From date/time (" + PATTERN_MINUTE + "): ");
    LocalDateTime to = readDateTime("To date/time (" + PATTERN_MINUTE + "): ");

    try {
      List<DiaryEntry> allFound = register.findBetween(from, to);
      List<DiaryEntry> viewable = applyAccessControl(allFound);
      if (viewable.isEmpty()){
        System.out.println("No entries found in this interval");
      } else {
        System.out.println("Found " + viewable.size() + " entries:");
        list(viewable);
      }
    } catch (IllegalArgumentException e){
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void showGlobalStatsTable(){
    System.out.println("--- Global Statistics - Admin Only ---");
    System.out.printf("%-20s | %s%n","Author","Entries");
    System.out.println("---------------------|---------");
    List<Author> allAuthors = authors.getAll();
    long totalEntries = 0;
    for (Author a : allAuthors){
      long count = register.countByAuthor(a.getId());
      totalEntries += count;
      System.out.printf("%-20s | %d%n", a.getDisplayName(), count);
    }
    System.out.println("---------------------|---------");
    System.out.println("Total entries in system: " + totalEntries);
    System.out.println("\nPress enter to go back to main menu");
    scanner.nextLine();

  }

  private void loginOrRegister(){
    while (currentUser == null) {
      System.out.println("1. Login");
      System.out.println("2. New User");
      int choice = readInt("Choose option: ");

      if (choice == 1){
        performLogin();
      } else if (choice  == 2){
        performRegistration();
      } else {
        System.out.println("Invalid choice.");
      }
    }
  }

  /**
   * Gives user the option to log in.
   */
  private void performLogin(){
    String name = readLine("Username: ");
    Optional<Author> authorOpt = authors.findByName(name);

    if (authorOpt.isEmpty()){
      System.out.println("User not found.");
      return;
    }

    Author author = authorOpt.get();
    String password = readLine("Password: ");

    if (author.checkPassword(password)){
      this.currentUser = author;
    } else {
      System.out.println("Incorrect password.");
    }
  }

  private void performRegistration(){
    String name = readLine("Choose username: ");
    if (authors.findByName(name).isPresent()){
      System.out.println("User already exists");
      return;
    }
    String password = readLine("Choose password: ");
    try {
      this.currentUser = authors.addAuthor(name, password);
      System.out.println("User successfully created.");
    } catch (IllegalArgumentException e) {
      System.out.println("Could not create user: " + e.getMessage());
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

  /**
   * Checks if the user should have access to administrative powers.
   *
   * @return true if user is called "admin", false otherwise.
   */
  private boolean isAdmin(){
    return currentUser != null && currentUser.getDisplayName().equalsIgnoreCase("admin");
  }

  /**
   * Filters a list of entries based on logged-in user's role
   * - Admin: Has full access.
   * - Normal user: Only has access to their own entries.
   *
   * Streaming made with help from AI.
   * @param entries A list of diary entries to check.
   * @return A filtered list of diary entries based on user status.
   */
  private List<DiaryEntry> applyAccessControl(List<DiaryEntry> entries){
    if (entries.isEmpty()) return entries;

    if (isAdmin()){
      return entries;
    }

    return entries.stream().filter(e -> e.getAuthor().equals(currentUser)).toList();
  }


}
