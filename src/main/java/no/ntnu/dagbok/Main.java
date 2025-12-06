package no.ntnu.dagbok;

import no.ntnu.dagbok.author.AuthorRegister;
import no.ntnu.dagbok.entry.DiaryEntryRegister;
import no.ntnu.dagbok.ui.DiaryUi;

/**
 * Represents the entry point of the Diary Application.
 *
 * <p>This class acts as the composition root of the application. It is responsible for
 * instantiating the data registers ({@link AuthorRegister} and {@link DiaryEntryRegister})
 * and injecting them into the user interface ({@link DiaryUi})</p>
 *
 * <p>This design promotes low coupling by ensuring the UI does not create its own
 * dependencies internally.</p>
 */
public class Main {

  /**
   * Starts the application.
   *
   * <p>The method performs the following setup:
   * <ol>
   *   <li>Creates the data register instances.</li>
   *   <li>Initializes the {@link DiaryUi} using dependency injection.</li>
   *   <li>Seeds the application data with demo data via {@code init()}</li>
   *   <li>Launches the main menu loop via {@code start()}</li>
   * </ol>
   * </p>
   *
   * <p><i>Dependency injection based on suggestion from Google Gemini.</i></p>
   *
   * @param args Unused commmand-line arguments.
   */
  public static void main(String[] args) {
    DiaryEntryRegister entryRegister = new DiaryEntryRegister();
    AuthorRegister authorRegister = new AuthorRegister();
    DiaryUi diaryUi = new DiaryUi(entryRegister, authorRegister);
    diaryUi.init();
    diaryUi.start();
  }
}
