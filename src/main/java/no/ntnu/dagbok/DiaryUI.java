package no.ntnu.dagbok;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

public class DiaryUI {
  private static final int ADD_ENTRY = 1;
  private static final int LIST_ALL = 2;
  private static final int SEARCH_BY_DATE = 3;
  private static final int DELETE_ENTRY = 4;
  private static final int EXIT_PROGRAM = 0;

  private final Scanner scanner = new Scanner(System.in);
  private final DiaryEntryRegister register = new DiaryEntryRegister();

  public void init(){
    register.addDiaryEntry(new DiaryEntry("Lars", "Title 1", "Text 1 Lars", LocalDateTime.now()));
    register.addDiaryEntry(new DiaryEntry("Lisa", "Title 2", "Text 2 Lisa", LocalDateTime.now().minusDays(3).withHour(12).withMinute(20)));
    register.addDiaryEntry(new DiaryEntry("Lars", "Title 3", "Text 3 Lars", LocalDateTime.now().minusDays(4).withHour(15).withMinute(22)));
  }

  public void start(){
    boolean finished = false;
    while (!finished) {
      int choice = displayMenu();
      switch (choice){
        case ADD_ENTRY -> addEntry();
        case LIST_ALL -> listAll();
        case SEARCH_BY_DATE -> searchByDate();
        case DELETE_ENTRY -> deleteEntry();
        case EXIT_PROGRAM -> {
          System.out.println("Exiting program");
          finished = true;
        }
        default -> System.out.println("Invalid option");
      }
    }
  }

  private int displayMenu(){
    System.out.println("--- Diary-Software ---");
    System.out.println("1. Add diary entry");
    System.out.println("2. List all entries ");
    System.out.println("3. Search by date");
    System.out.println("4. Delete diary entry");
    System.out.println("0. Exit program");

    if (scanner.hasNext()){
      int value = scanner.nextInt();
      scanner.nextLine();
      return value;
    } else {
      scanner.nextLine();
      return 0;
    }
  }

  private void addEntry(){
      System.out.println("Dummy add entry");
  }
  private void listAll(){
    list(register.getAllEntries());
  }
  private void searchByDate(){
    LocalDate date = readDate("Date (yyyy-MM-dd):");
    List<DiaryEntry> found = register.findFromDate(date);
    if (found.isEmpty()) System.out.println("No diary entries from this date");
    else list(found);
  }
  private void deleteEntry(){
    LocalDateTime ldt = readDateTime("Time of entry (yyyy-MM-dd HH:mm): ");
    boolean deleted = register.deleteByDateTime(ldt);
    System.out.println(deleted ? "Deleted" : "Could not find diary entry");
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
      System.out.println(e);
      System.out.println();
    });
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
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    while (true){
      System.out.println(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDate.parse(s, df);
      } catch (Exception e) {
        System.out.println("Invalid format. Try this: yyyy-MM-dd");
      }
    }
  }

  private LocalDateTime readDateTime(String input){
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    while (true){
      System.out.println(input);
      String s = scanner.nextLine().trim();
      try {
        return LocalDateTime.parse(s, df);
      } catch (Exception e) {
        System.out.println("Invalid format. Try this: yyyy-MM-dd HH:mm");
      }
    }
  }


}
