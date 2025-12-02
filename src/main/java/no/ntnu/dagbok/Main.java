package no.ntnu.dagbok;


import no.ntnu.dagbok.author.Author;
import no.ntnu.dagbok.author.AuthorRegister;
import no.ntnu.dagbok.entry.DiaryEntryRegister;
import no.ntnu.dagbok.ui.DiaryUI;

public class Main {
  public static void main(String[] args) {
    DiaryEntryRegister entryRegister = new DiaryEntryRegister();
    AuthorRegister authorRegister = new AuthorRegister();
    DiaryUI diaryUI = new DiaryUI(entryRegister, authorRegister);
    diaryUI.init();
    diaryUI.start();
  }
}
