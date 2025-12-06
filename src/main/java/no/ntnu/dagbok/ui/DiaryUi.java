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
 * Represents the console-based user interface for the Diary Application.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Handling user interaction and parsing console input.</li>
 *   <li>Ensuring robust input handling (loops until valid input is received).</li>
 *   <li>Delegating business logic to {@link DiaryEntryRegister} and {@link AuthorRegister}.</li>
 *   <li>Managing the user session (login/logout) and access control.</li>
 * </ul>
 *
 * <p>Conventions:
 * <ul>
 *   <li>Date/Time precision visible to user is in minutes. Pattern {@code yyyy-MM-dd HH:mm}.</li>
 *   <li>Lists are displayed sorted by date/time.</li>
 *   <li>Admin users have global access, while regular users are isolated to their own data.</li>
 * </ul>
 */
public class DiaryUi {
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
  private static final int SYSTEM_RESET = 12;
  private static final int EXIT_PROGRAM = 0;
  // Formatting suggestion from ChatGPT
  private static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
  private static final String PATTERN_DATE = "yyyy-MM-dd";

  private static final DateTimeFormatter DF_MINUTE = DateTimeFormatter.ofPattern(PATTERN_MINUTE);
  private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);

  private final Scanner scanner = new Scanner(System.in);
  private final DiaryEntryRegister register;
  private final AuthorRegister authors;

  private Author currentUser;

  /**
   * Creates a new instance of the User Interface.
   *
   * @param register The register handling diary entries. Must not be null.
   * @param authors The register handling authors. Must not be null.
   */
  public DiaryUi(DiaryEntryRegister register, AuthorRegister authors) {
    this.register = register;
    this.authors = authors;
  }

  /**
   * Initializes the application with demo data.
   *
   * <p>Seeds the registers with sample authors and entries for testing purposes.
   * Also creates non-test admin user.</p>
   */
  public void init() {

    Author lars = authors.addAuthor("Lars", "password");
    Author lisa = authors.addAuthor("Lisa", "password");
    authors.addAuthor("admin", "admin123");
    DiaryEntry larsEntry1 = new DiaryEntry(lars, "Title 1", "Text 1 Lars", LocalDateTime.now());
    DiaryEntry lisaEntry1 =
        new DiaryEntry(
            lisa,
            "Title 2",
            "Text 2 Lisa",
            LocalDateTime.now().minusDays(3).withHour(12).withMinute(20));
    DiaryEntry larsEntry2 =
        new DiaryEntry(
            lars,
            "Title 3",
            "Text 3 Lars",
            LocalDateTime.now().minusDays(4).withHour(15).withMinute(22));
    register.addEntry(larsEntry1);
    register.addEntry(lisaEntry1);
    register.addEntry(larsEntry2);
  }

  /**
   * Starts the main application loop.
   *
   * <p>Handles the user login process and continuously processes menu selections.
   * until the user chooses to exit. Input errors are handled without crashing the application.</p>
   */
  public void start() {

    System.out.println("Diary Application Starting...");

    loginOrRegister();

    System.out.println("\n\nWelcome, " + currentUser.getDisplayName() + "!");

    boolean finished = false;
    while (!finished) {
      int choice = displayMenu();
      switch (choice) {
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
          if (isAdmin()) {
            listByAuthor();
          } else {
            System.out.println("Invalid option.");
          }
        }
        case GLOBAL_STATISTICS -> {
          if (isAdmin()) {
            showGlobalStatsTable();
          } else {
            System.out.println("Invalid option.");
          }
        }
        case SYSTEM_RESET -> {
          if (isAdmin()){
            resetSystem();
          } else {
            System.out.println("Invalid option.");
          }
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
   * Displays the main menu options in the console.
   *
   * <p>Adapts the menu based on the user's role (Admin options are hidden for regular users).</p>
   *
   * @return The integer corresponding to the user's choice.
   */
  private int displayMenu() {
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
    if (isAdmin()) {
      System.out.println("10. List entries by author (Admin)");
      System.out.println("11. View global statistics (Admin)");
    }
    System.out.println("0. Exit program");
    return readInt("Pick option ");
  }

  /**
   * Guides the logged-in user through creating and adding a new diary entry.
   *
   * <p>Allows the user to choose between the current system time or a custom timestamp.</p>
   */
  private void addEntry() {
    System.out.println("Creating entry as: " + currentUser.getDisplayName());

    String title = readLine("Title: ");
    String text = readLine("Text: ");
    LocalDateTime dateTime;
    if (readYesNo("Use current time (" + DF_MINUTE.format(LocalDateTime.now()) + ")? (y/n): ")) {
      dateTime = LocalDateTime.now();
    } else {
      dateTime = readDateTime("Enter date/time (" + PATTERN_MINUTE + "): ");
    }

    DiaryEntry inputEntry = new DiaryEntry(currentUser, title, text, dateTime);
    register.addEntry(inputEntry);
    System.out.println("Entry successfully added");
  }

  /**
   * Facilitates editing an existing diary entry.
   *
   * <p>The user selects a date, chooses an entry from a filtered list,
   * and can optionally update the title and/or text.</p>
   */
  private void editEntry() {
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> foundEntries = register.findByDate(date);
    List<DiaryEntry> viewableEntries = applyAccessControl(foundEntries);

    if (foundEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to edit?");
    for (int i = 0; i < viewableEntries.size(); i++) {
      DiaryEntry diaryEntry = viewableEntries.get(i);
      System.out.printf(
          "%d: %s (%s) - [%s]%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime()),
          diaryEntry.getAuthor().getDisplayName());
    }

    int choice = readInt("Write the number of the entry: ");

    if (choice < 1 || choice > viewableEntries.size()) {
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToEdit = viewableEntries.get(choice - 1);

    System.out.println("---Entry to Edit---");
    showEntry(entryToEdit);
    System.out.println();

    if (readYesNo("Do you want to edit the title? (y/n): ")) {
      try {
        String newTitle = readLine("New title: ");
        entryToEdit.setTitle(newTitle);
      } catch (IllegalArgumentException e) {
        System.out.println("Error updating title: " + e.getMessage());
      }
    } else {
      System.out.println("Entry title not edited");
    }

    if (readYesNo("Do you want to edit the text? (y/n): ")) {
      try {
        String newText = readLine("New text: ");
        entryToEdit.setText(newText);
      } catch (IllegalArgumentException e) {
        System.out.println("Error updating text: " + e.getMessage());
      }
    } else {
      System.out.println("Entry text not edited");
    }
  }

  /**
   * Searches for entries containing a specific keyword or phrase.
   *
   * <p>Results are filtered based on the current user's access rights.</p>
   */
  private void searchByKeyword() {
    String keyword = readLine("Search for word/phrase: ");
    List<DiaryEntry> results = register.searchByKeyword(keyword);

    List<DiaryEntry> visibleResults = applyAccessControl(results);
    if (results.isEmpty()) {
      System.out.println("No entries found containing keyword: " + keyword);
    } else {
      System.out.println("Found " + visibleResults.size() + " matches in diary entries: ");
      list(visibleResults);
    }
  }

  /**
   * Displays statistics for the currently logged-in user.
   */
  private void showStatistics() {
    System.out.println(register.getStatistics(currentUser.getId()));
    System.out.println("Press enter to go back to menu.");
    scanner.nextLine();
  }

  /**
   * Lists all diary entries created today.
   *
   * <p>Results are filtered based on access rights.</p>
   */
  private void todayEntries() {
    List<DiaryEntry> allToday = register.findByDate(LocalDate.now());
    list(applyAccessControl(allToday));
  }

  /**
   * Lists diary entries available to the user.
   *
   * <ul>
   *   <li><b>Admin: </b> Sees all entries in the system.</li>
   *   <li><b>Regular User: </b> Sees only their own entries.</li>
   * </ul>
   */
  private void listAll() {
    List<DiaryEntry> entriesToShow;

    if (isAdmin()) {
      System.out.println("--- *** ADMIN ACCESS: All Entries Visisble *** ---");
      entriesToShow = register.getAll();
    } else {
      System.out.println("--- All entries for " + currentUser.getDisplayName() + " ---");
      entriesToShow = register.findByAuthor(currentUser.getId());
    }
    list(entriesToShow);
  }

  /**
   * Lists entries for a specific author (Admin only).
   */
  private void listByAuthor() {
    String authorName = readLine("Author's name: ");
    Optional<Author> a = authors.findByName(authorName);
    if (a.isEmpty()) {
      System.out.println("No author found by that name.");
      return;
    }
    list(register.findByAuthor(a.get().getId()));
  }

  /**
   * Lists entries from a specific date.
   */
  private void searchByDate() {
    LocalDate date = readDate("Date (" + PATTERN_DATE + "):");
    List<DiaryEntry> allFound = register.findByDate(date);

    List<DiaryEntry> viewable = applyAccessControl(allFound);
    if (viewable.isEmpty()) {
      System.out.println("No diary entries from this date");
    } else {
      list(viewable);
    }
  }

  /**
   * Handles the deletion of a diary entry.
   *
   * <p>Users select an entry from a filtered list of their own entries (or all entries for admin),
   * verify the selection, and confirm deletion.</p>
   */
  private void deleteEntry() {
    LocalDate date = readDate("What is the date of the entry? (" + PATTERN_DATE + ")");
    List<DiaryEntry> allEntriesFound = register.findByDate(date);
    List<DiaryEntry> viewableEntries = applyAccessControl(allEntriesFound);

    if (viewableEntries.isEmpty()) {
      System.out.println("No diary entries found for this date");
      return;
    }
    System.out.println("Which entry would you like to delete?");
    for (int i = 0; i < viewableEntries.size(); i++) {
      DiaryEntry diaryEntry = viewableEntries.get(i);
      System.out.printf(
          "%d: %s (%s) - [%s]%n",
          (i + 1),
          diaryEntry.getTitle(),
          DF_MINUTE.format(diaryEntry.getDateTime()),
          diaryEntry.getAuthor().getDisplayName());
    }

    int choice = readInt("Write the number of the entry");

    if (choice < 1 || choice > viewableEntries.size()) {
      System.out.println("Invalid choice");
      return;
    }
    DiaryEntry entryToDelete = viewableEntries.get(choice - 1);

    System.out.println("*** Entry Selected for Deletion ***");
    System.out.println("_______________________________");
    showEntry(entryToDelete);
    System.out.println("--___________________________--");
    if (readYesNo("Are you certain you want to delete this entry? (y/n): ")) {
      boolean removed = register.removeEntry(entryToDelete.getEntryId());
      if (removed) {
        System.out.println("Diary entry deleted");
      } else {
        System.out.println("Error: Entry not deleted");
      }

    } else {
      System.out.println("Entry not deleted");
    }
  }

  /**
   * Searches for and displays entries within a specific date/time range.
   */
  private void searchBetweenDates() {
    System.out.println("--- Search by Time Range ---");
    LocalDateTime from = readDateTime("From date/time (" + PATTERN_MINUTE + "): ");
    LocalDateTime to = readDateTime("To date/time (" + PATTERN_MINUTE + "): ");

    try {
      List<DiaryEntry> allFound = register.findBetween(from, to);
      List<DiaryEntry> viewable = applyAccessControl(allFound);
      if (viewable.isEmpty()) {
        System.out.println("No entries found in this interval");
      } else {
        System.out.println("Found " + viewable.size() + " entries:");
        list(viewable);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  /**
   * Displays a table of global statistics including all authors and their entry counts.
   * Only available to Admins.
   *
   * <p><i>Implemented with help from AI.</i></p>
   */
  private void showGlobalStatsTable() {
    System.out.println("--- Global Statistics - Admin Only ---");
    System.out.printf("%-20s | %s%n", "Author","Member Since" ,"Entries");
    System.out.println("---------------------|--------------|---------");
    List<Author> allAuthors = authors.getAll();
    long totalEntries = 0;
    for (Author a : allAuthors) {
      long count = register.countByAuthor(a.getId());
      totalEntries += count;
      String dateCreated = a.getCreatedAt().toLocalDate().toString();
      System.out.printf("%-20s | %d%n", a.getDisplayName(),dateCreated, count);
    }
    System.out.println("---------------------|--------------|---------");
    System.out.println("Total entries in system: " + totalEntries);
    System.out.println("\nPress enter to go back to main menu");
    scanner.nextLine();
  }

  /**
   * Deletes all andries an all non-admin authors.
   * Only available to Admin.
   */
  private void resetSystem(){
    if (!isAdmin()) {return;}

    System.out.println("!!! WARNING - THIS ACTION WILL DELETE ALL AUTHORS AND ENTRIES !!!");

    if (readYesNo("Are you certain you want to delete all entries and authors? (y/n): ")){
      register.clearDiaryEntryRegister();
      authors.clearExceptAdmin();

      System.out.println("System reset successful.");
      System.out.println("All entries deleted. All entries except admin deleted.");
    } else {
      System.out.println("System reset cancelled.");
    }
  }

  /**
   * Handles the login or registration flow at application startup.
   * Forces the user to authenticate before accessing the main menu.
   */
  private void loginOrRegister() {
    while (currentUser == null) {
      System.out.println("1. Login");
      System.out.println("2. New User");
      int choice = readInt("Choose option: ");

      if (choice == 1) {
        performLogin();
      } else if (choice == 2) {
        performRegistration();
      } else {
        System.out.println("Invalid choice.");
      }
    }
  }

  /** Prompts for username and password to log in an existing user. */
  private void performLogin() {
    String name = readLine("Username: ");
    Optional<Author> authorOpt = authors.findByName(name);

    if (authorOpt.isEmpty()) {
      System.out.println("User not found.");
      return;
    }

    Author author = authorOpt.get();
    String password = readLine("Password: ");

    if (author.checkPassword(password)) {
      this.currentUser = author;
    } else {
      System.out.println("Incorrect password.");
    }
  }

  /**
   * Prompts for username and password to register a new user.
   */
  private void performRegistration() {
    String name = readLine("Choose username: ");
    if (authors.findByName(name).isPresent()) {
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
   * Helper method to iterate through and display a list of entries.
   *
   * <p><i>Filtering logic fixed with AI</i></p>
   *
   * @param entries The list of diary entries to display.
   */
  public void list(List<DiaryEntry> entries) {
    if (entries.isEmpty()) {
      System.out.println("No entries found");
      return;
    }
    entries.forEach(
        e -> {
          showEntry(e);
          System.out.println();
        });
  }

  /**
   * Formats and prints a single diary entry to the console.
   *
   * @param entry The entry to be shown.
   */
  private void showEntry(DiaryEntry entry) {
    String timeStamp = DF_MINUTE.format(entry.getDateTime());
    System.out.println("[" + timeStamp + "] " + entry.getAuthor().getDisplayName());
    System.out.println(entry.getTitle());
    System.out.println(entry.getText());
  }

  // Input helper methods

  /**
   * Reads a string from the console. Loops until non-empty input is received.
   *
   * @param input The message to display to the user.
   * @return A trimmed, non-empty string.
   */
  private String readLine(String input) {
    System.out.print(input);
    String s = scanner.nextLine();
    while (s == null || s.trim().isEmpty()) {
      System.out.println("Cannot be empty: " + input);
      s = scanner.nextLine();
    }
    return s.trim();
  }

  /**
   * Reads an integer from the console. Loops until a valid number is entered.
   *
   * @param input The message to display to the user.
   * @return A valid integer.
   */
  private int readInt(String input) {
    while (true) {
      System.out.println(input);
      String s = scanner.nextLine();
      try {
        return Integer.parseInt(s.trim());
      } catch (NumberFormatException e) {
        System.out.println("Input must be a number");
      }
    }
  }

  /**
   * Reads a date from the console in the format yyyy-MM-dd
   * Loops until valid input is received.
   *
   * @param input The message to display to the user.
   * @return A valid LocalDate object.
   */
  private LocalDate readDate(String input) {
    while (true) {
      System.out.print(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDate.parse(s, DF_DATE);
      } catch (Exception e) {
        System.out.println("Invalid date format. Use " + DF_DATE);
      }
    }
  }


  /**
   * Reads a date and time from the console in the format yyyy-MM-dd HH:mm.
   * Loops until valid input is received.
   *
   * @param input The message to display to the user.
   * @return A valid LocalDateTime object.
   */
  private LocalDateTime readDateTime(String input) {
    while (true) {
      System.out.print(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDateTime.parse(s, DF_MINUTE);
      } catch (Exception e) {
        System.out.println("Invalid format. Use " + DF_MINUTE);
      }
    }
  }

  /**
   * Prompts the user for a Yes/No answer.
   *
   * <p><i>Suggestion from ChatGPT.</i></p>
   *
   * @param input The messag to display.
   * @return {@code true} for 'y', {@code false} for 'n'.
   */
  private boolean readYesNo(String input) {
    while (true) {
      System.out.print(input);
      String s = scanner.nextLine().trim().toLowerCase();
      if (s.equals("y")) {
        return true;
      }
      if (s.equals("n")) {
        return false;
      }
      System.out.println("Type y/n for yes or no");
    }
  }

  /**
   * Checks if the currently logged-in user has administrative privileges.
   *
   * <p><i>Admin-role designed with help from Google Gemini.</i></p>
   *
   * @return {@code true} if user is named "admin", {@code false} otherwise.
   */
  private boolean isAdmin() {
    return currentUser != null && currentUser.getDisplayName().equalsIgnoreCase("admin");
  }

  /**
   * Filters a list of entries based on the logged-in user's profile.
   *
   * <ul>
   *   <li><b>Admin:</b>Has full access (returns all entries).</li>
   *   <li><b>Regular User:</b>Has access only to their own entries.</li>
   * </ul>
   *
   * <p><i>Admin-role and streaming made with help from Google Gemini.</i></p>
   *
   * @param entries The list of diary entries to check.
   * @return A filtered list of diary entries viewable by the current user.
   */
  private List<DiaryEntry> applyAccessControl(List<DiaryEntry> entries) {
    if (entries.isEmpty()) {
      return entries;
    }

    if (isAdmin()) {
      return entries;
    }

    return entries.stream().filter(e -> e.getAuthor().equals(currentUser)).toList();
  }
}
