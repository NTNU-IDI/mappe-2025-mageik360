package no.ntnu.dagbok.author;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests for author class. */
public class AuthorTest {

  /**
   * Checks that short/null passwords are not accepted
   *
   * <p>assertThrows for too short username. assertThrwos for null username.
   */
  @Test
  void password_validation_works() {
    assertThrows(IllegalArgumentException.class, () -> new Author("ShortUser", "123"));
    assertThrows(IllegalArgumentException.class, () -> new Author("NullUser", null));
  }

  /**
   * Positive and negative tests for password verification.
   *
   * <p>Checks that correct passwords are accepted. Checks that incorrect passwords are not
   * accepted. Checks that password matching is case-sensitive.
   */
  @Test
  void checkPassword_verifies_author_correctly() {
    Author author = new Author("Lars", "password123");

    assertTrue(author.checkPassword("password123"), "Correct password should succeed");

    assertFalse(author.checkPassword("wrongPassword"), "Incorrect password should fail");
    assertFalse(author.checkPassword("PASSWORD123"), "Password check should be case-sensitive");
  }

  /**
   * Checks that whitespace and case are ignored when using normalizedKey.
   *
   * <p>assertEquals for identical keys after formatting.
   */
  @Test
  void normalizedKey_handles_messy_input() {
    String key1 = Author.normalizedKey("  Lars  ");
    String key2 = Author.normalizedKey("lars");

    assertEquals(key1, key2, "Keys should be identical despite case or whitespace");
  }

  /** Tests for valid comparisons for hash codes. */
  @Test
  void testEqualsAndHashCode() {
    UUID id = UUID.randomUUID();
    Author a1 = new Author(id, "Lars", "pass123");
    Author a2 = new Author(id, "Lars", "pass123");
    Author a3 = new Author(UUID.randomUUID(), "Lars", "pass123");

    assertEquals(a1, a2, "Authors with same ID should be equal.");
    assertEquals(a1.hashCode(), a2.hashCode(), "HashCodes must match for equal objects.");

    assertNotEquals(a1, a3, "Authors with different IDs should not be equal.");
    assertNotEquals(null, a1, "Should not equal null.");
  }

  /** Tests for correct output from testToString. */
  @Test
  void testToString() {
    Author a = new Author("TestName", "pass");
    String output = a.toString();
    assertTrue(output.contains("TestName"));
    assertTrue(output.contains("Unique ID"));
  }

  /** Checks that author constructor sets timestamps. */
  @Test
  void constructor_sets_timestamps() {
    Author a = new Author("User", "pass");

    assertNotNull(a.getCreatedAt(), "CreatedAt should be initialized");
    assertNotNull(a.getUpdatedAt(), "UpdatedAt should be initialized");

    assertNotNull(a.getId(), "ID should be generated");
  }

  /** Tests that equals overwrite returns true when run on the same object. */
  @Test
  void equals_returns_true_for_same_object_reference() {
    Author a = new Author("User", "pass");
    assertTrue(a.equals(a));
  }

  /** Tests that equals returns false when run on object of different class. */
  @Test
  void equals_returns_false_for_different_class() {
    Author a = new Author("Name", "pass");
    assertFalse(a.equals("String"));
  }
}
