package no.ntnu.dagbok;

import no.ntnu.dagbok.author.AuthorRegister;
import no.ntnu.dagbok.entry.DiaryEntryRegister;
import no.ntnu.dagbok.ui.DiaryUi;

public class Main {
  public static void main(String[] args) {
    DiaryEntryRegister entryRegister = new DiaryEntryRegister();
    AuthorRegister authorRegister = new AuthorRegister();
    DiaryUi diaryUi = new DiaryUi(entryRegister, authorRegister);
    diaryUi.init();
    diaryUi.start();
  }
}
