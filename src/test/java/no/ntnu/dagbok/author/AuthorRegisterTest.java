package no.ntnu.dagbok.author;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorRegisterTest {

  private AuthorRegister reg;

  @BeforeEach
  void setUp(){
    reg = new AuthorRegister();
  }
  @Test
  void add_and_findByName_are_normalized(){
    Author author = reg.addAuthor("Lars");
    Optional<Author> found = reg.findByName("   lARs   ");
    assertTrue(found.isPresent());
    assertEquals(author.getId(), found.get().getId());
  }
  @Test
  void getAll_returns_sorted_and_unmodifiable(){
    reg.addAuthor("Bjarne");
    reg.addAuthor("anne");
    reg.addAuthor("Georg");

    List<Author> all = reg.getAll();
    assertEquals(3, all.size());
    assertEquals("anne", all.getFirst().getDisplayName());
    assertEquals("Bjarne",all.get(1).getDisplayName());
    assertEquals("Georg", all.get(2).getDisplayName());
    assertThrows(UnsupportedOperationException.class, () -> all.add(new Author("X")));

  }
}
