package no.ntnu.dagbok;

import java.util.Scanner;

public class DiaryUI {
  private static final int ADD_ENTRY = 1;
  private static final int LIST_ALL = 2;
  private static final int SEARCH_BY_DATE = 3;
  private static final int DELETE_ENTRY = 4;
  private static final int EXIT_PROGRAM = 0;

  private final Scanner scanner = new Scanner(System.in);

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
    System.out.println("Dummy list all");
  }
  private void searchByDate(){
    System.out.println("Dummy search by date");
  }
  private void deleteEntry(){
    System.out.println("Dummy delete entry");
  }
}
