package no.ntnu.dagbok;

import no.ntnu.dagbok.author.AuthorRegister;
import no.ntnu.dagbok.entry.DiaryEntryRegister;
import no.ntnu.dagbok.ui.DiaryUi;

/**
 * Represents the entry point of the Diary Application.
 *
 * <p>This class acts as the composition root of the application. It is responsible for
 * instantiating the data registers ({@link AuthorRegister} and {@link DiaryEntryRegister}) and
 * injecting them into the user interface ({@link DiaryUi})
 *
 * <p>This design promotes low coupling by ensuring the UI does not create its own dependencies
 * internally.
 *
 * <p>Why:</p>
 * Decouples the User Interface from the creation of its dependencies.
 * Strictly separates the creation from business logic. Helps achieves low coupling.
 *
 * <p>How:</p>
 * Instantiates the storage classes.
 * Injects instances to {@link DiaryUi} constructor.
 * Initiates the application sate with test data.
 * Transfers control to the UI by calling {@code start()}
 */
public class Main {

  /**
   * Starts the application.
   *
   * <p>The method performs the following setup:
   *
   * <ol>
   *   <li>Creates the data register instances.
   *   <li>Initializes the {@link DiaryUi} using dependency injection.
   *   <li>Seeds the application data with demo data via {@code init()}
   *   <li>Launches the main menu loop via {@code start()}
   * </ol>
   *
   * <p><i>Dependency injection based on suggestion from Google Gemini.</i>
   *
   * <p>Why:</p>
   * Bootstraps the application, creates a valid state before user interaction begins.
   *
   * <p>How:</p>
   * Creates and empty {@code DiaryEntryRegister}.
   * Creates an empty {@code AuthorRegister}.
   * Creates {@code diaryUi.init()} to populate the register with demo data.
   * Calls {@code diaryUi.start()} to enter the main event loop.
   *
   * @param args Unused command-line arguments.
   */
  public static void main(String[] args) {
    DiaryEntryRegister entryRegister = new DiaryEntryRegister();
    AuthorRegister authorRegister = new AuthorRegister();
    DiaryUi diaryUi = new DiaryUi(entryRegister, authorRegister);
    diaryUi.init();
    diaryUi.start();
  }
}
