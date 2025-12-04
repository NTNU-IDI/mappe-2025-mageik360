package no.ntnu.dagbok.author;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class AuthorTest {

  /**
   * Checks that short/null passwords are not accepted
   */
  @Test
  void password_validation_works(){
    assertThrows(IllegalArgumentException.class, () -> new Author("ShortUser", "123"));
    assertThrows(IllegalArgumentException.class, () -> new Author("NullUser", "null"));

  }

  /**
   * Positive and negative tests for password verification.
   * Checks that correct passwords are accepted.
   * Checks that incorrect passwords are not accepted.
   * Checks that password matching is case-sensitive.
   */
  @Test
  void checkPassword_verifies_author_correctly(){
    Author author = new Author("Lars","password123");

    assertTrue(author.checkPassword("password123"), "Correct password should succeed");

    assertFalse(author.checkPassword("wrongPassword"), "Incorrect password should fail");
    assertFalse(author.checkPassword("PASSWORD123"), "Password check should be case-sensitive");
  }

  /**
   * Checks that whitespace and case are ignored when using normalizedKey.
   */
  @Test
  void normalizedKey_handles_messy_input(){
    String key1 = Author.normalizedKey("  Lars  ");
    String key2 = Author.normalizedKey("lars");

    assertEquals(key1,key2, "Keys should be identical despite case or whitespace");
  }
}
