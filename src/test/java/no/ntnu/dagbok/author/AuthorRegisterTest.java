package no.ntnu.dagbok.author;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorRegisterTest {

  private AuthorRegister reg;
  private final String dummyPassword = "dummyPassword";

  @BeforeEach
  void setUp(){
    reg = new AuthorRegister();
  }
  @Test
  void add_and_findByName_are_normalized(){
    Author author = reg.addAuthor("Lars",dummyPassword);
    Optional<Author> found = reg.findByName("   lARs   ");
    assertTrue(found.isPresent());
    assertEquals(author.getId(), found.get().getId());
  }
  @Test
  void getAll_returns_sorted_and_unmodifiable(){
    reg.addAuthor("Bjarne",dummyPassword);
    reg.addAuthor("anne",dummyPassword);
    reg.addAuthor("Georg",dummyPassword);

    List<Author> all = reg.getAll();
    assertEquals(3, all.size());
    assertEquals("anne", all.getFirst().getDisplayName());
    assertEquals("Bjarne",all.get(1).getDisplayName());
    assertEquals("Georg", all.get(2).getDisplayName());
    assertThrows(UnsupportedOperationException.class, () -> all.add(new Author("X",dummyPassword)));

  }

  @Test
  void rename_updates_display_name_successfully(){

    Author a = reg.addAuthor("OldName", "password");

    Author updated = reg.rename(a.getId(), "NewName");

    assertEquals("NewName", updated.getDisplayName());
    assertEquals("NewName", reg.getById(a.getId()).get().getDisplayName());
  }

  @Test
  void rename_throws_if_name_is_taken(){

    Author a1 = reg.addAuthor("Lars","pass");
    Author a2 = reg.addAuthor("Lisa", "pass");

    assertThrows(RuntimeException.class, () -> reg.rename(a1.getId(), "Lisa"));
    assertThrows(RuntimeException.class, () -> reg.rename(a1.getId(), "LISA"));
  }

  @Test
  void rename_throws_if_author_id_not_found(){
    assertThrows(RuntimeException.class, () -> reg.rename(UUID.randomUUID(), "NewName"));
  }

  @Test
  void remove_deletes_author(){
    Author a = reg.addAuthor("ToBeDeleted", "pass");

    assertTrue(reg.getById(a.getId()).isPresent());

    boolean removed = reg.remove(a.getId());

    assertTrue(removed, "Removed should return true when successful");
    assertTrue(reg.getById(a.getId()).isEmpty(), "Author should be removed from register");
  }
}
