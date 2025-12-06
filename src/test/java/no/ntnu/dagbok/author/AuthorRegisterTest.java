package no.ntnu.dagbok.author;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests author register functionality. */
public class AuthorRegisterTest {

  private final String dummyPassword = "dummyPassword";
  private AuthorRegister reg;

  @BeforeEach
  void setUp() {
    reg = new AuthorRegister();
  }

  /**
   * Tests that addAuthor and findByName do proper normalization.
   *
   * <p>assertTrue for differently formatted name being found. assertEquals for identical id.
   */
  @Test
  void add_and_findByName_are_normalized() {
    Author author = reg.addAuthor("Lars", dummyPassword);
    Optional<Author> found = reg.findByName("   lARs   ");
    assertTrue(found.isPresent());
    assertEquals(author.getId(), found.get().getId());
  }

  /**
   * Tests that getAll returns a sorted and unmodifiable list.
   *
   * <p>assertEquals for list length. assertEquals for each alphabetically sorted name. assertThrows
   * for trying to edit the returned list. Made with help from chatGPt
   */
  @Test
  void getAll_returns_sorted_and_unmodifiable() {
    reg.addAuthor("Bjarne", dummyPassword);
    reg.addAuthor("anne", dummyPassword);
    reg.addAuthor("Georg", dummyPassword);

    List<Author> all = reg.getAll();
    assertEquals(3, all.size());
    assertEquals("anne", all.getFirst().getDisplayName());
    assertEquals("Bjarne", all.get(1).getDisplayName());
    assertEquals("Georg", all.get(2).getDisplayName());
    assertThrows(
        UnsupportedOperationException.class, () -> all.add(new Author("X", dummyPassword)));
  }

  /**
   * Tests that rename successfully updates the display name.
   *
   * <p>assertEquals for direct use of getDisplayName assertEquals for getDisplayName from register.
   */
  @Test
  void rename_updates_display_name_successfully() {

    Author a = reg.addAuthor("OldName", "password");

    Author updated = reg.rename(a.getId(), "NewName");

    assertEquals("NewName", updated.getDisplayName());
    Optional<Author> foundAuthor = reg.getById(a.getId());
    assertTrue(foundAuthor.isPresent(), "Author should be found in register.");
    assertEquals("NewName", foundAuthor.get().getDisplayName());
  }

  /**
   * Tests that rename throws a runtime exception if a name is taken.
   *
   * <p>assertThrows for adding name already present. assertThrows for adding name already present
   * with differently formatted name.
   */
  @Test
  void rename_throws_if_name_is_taken() {

    Author a1 = reg.addAuthor("Lars", "pass");
    reg.addAuthor("Lisa", "pass");

    assertThrows(RuntimeException.class, () -> reg.rename(a1.getId(), "Lisa"));
    assertThrows(RuntimeException.class, () -> reg.rename(a1.getId(), "LISA"));
  }

  /**
   * Tests that rename throws runtime exception is author id is not found.
   *
   * <p>assertThrows for renaming an author with id not in register.
   */
  @Test
  void rename_throws_if_author_id_not_found() {
    assertThrows(RuntimeException.class, () -> reg.rename(UUID.randomUUID(), "NewName"));
  }

  /**
   * Tests that remove actually deletes an author.
   *
   * <p>assertTrue that added author is in register. assertTrue for removed boolean being true.
   * assertTrue for author id not being in register after removal.
   */
  @Test
  void remove_deletes_author() {
    Author a = reg.addAuthor("ToBeDeleted", "pass");

    assertTrue(reg.getById(a.getId()).isPresent());

    boolean removed = reg.remove(a.getId());

    assertTrue(removed, "Removed should return true when successful");
    assertTrue(reg.getById(a.getId()).isEmpty(), "Author should be removed from register");
  }
}
