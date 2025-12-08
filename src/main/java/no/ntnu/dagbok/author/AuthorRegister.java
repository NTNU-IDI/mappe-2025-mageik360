package no.ntnu.dagbok.author;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages a collection of Author objects.
 *
 * <p>Stores, retrieves, updates and removes authors. Authors are stored in a Map for fast retrieval
 * by ID.
 */
public class AuthorRegister {

  private final Map<UUID, Author> idMap = new HashMap<>();

  /**
   * Creates a new Author and adds it to the register.
   *
   * @param displayName The public facing name of the author. Must not be null.
   * @param password The password for the author.
   * @return The newly created and added author.
   * @throws NullPointerException if displayName is null.
   */
  public Author addAuthor(String displayName, String password) {
    Objects.requireNonNull(displayName, "displayName must not be null");
    Author author = new Author(displayName, password);
    idMap.put(author.getId(), author);
    return author;
  }

  /**
   * Retrieves an author by unique ID.
   *
   * <p><i>Optional implemented with help from ChatGPT.</i>
   *
   * <p>Why: To provide a safe way to look up an author from id. Returns an {@code Optional} to
   * force handling of non-existent authors. This reduces the risk of a {@code
   * NullPointerException}.
   *
   * <p>How: The method performs a direct lookup of the {@code HashMap} using the UUID. Result is
   * wrapped in {@code Optional.ofNullable()}. This ensures that an empty optional is returned if
   * the UUID is not found.
   *
   * @param id The UUID of the author to retrieve. Must not be null.
   * @return An Optional containing the Author if found, or empty if not.
   * @throws NullPointerException if the id is null.
   */
  public Optional<Author> getById(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return Optional.ofNullable(idMap.get(id));
  }

  /**
   * Retrieves a list of all registered authors.
   *
   * <p>The list is sorted alphabetically by normalized display name, then by ID. The returned list
   * is unmodifiable.
   *
   * <p><i>Comparator implementation provided by ChatGPT</i>
   *
   * <p>Why: Provides a safe way for the UI to display all authors. A copy wrapped in an {@code
   * unmodifiableList} ensures that external classes cannot tamper with the register state.
   *
   * <p>How: Creates a new {@code ArrayList} containing values from the {@code idMap}. Sorts the
   * list using a chain of Comparators: First sorted by alphabetical display-name. Secondarily
   * sorted by ID. Returns the list wrapped in {@code Collections.unmodifiableList}.
   *
   * @return A sorted, unmodifiable list of all authors.
   */
  public List<Author> getAll() {
    List<Author> list = new ArrayList<>(idMap.values());
    list.sort(
        Comparator.comparing((Author a) -> Author.normalizedKey(a.getDisplayName()))
            .thenComparing(Author::getId));
    return Collections.unmodifiableList(list);
  }

  /**
   * Searches for an author by display name.
   *
   * <p>The search uses a normalized key to ensure case-insensitive matching.
   *
   * <p><i>Optional return value suggested by ChatGPT.</i>
   *
   * <p>Why: Allows looking up a user based on human-readable input instead of UUID.
   *
   * <p>How: The Map is keyed by UUID, so search by name requires linear lookup. Finds {@code
   * normalizedKey} from input string. Iterates through authors in the map. Compares the normalized
   * name of each author with key. Returns the first match, wrapped in {@code Optional}.
   *
   * @param displayName The name of the author to search for. Must not be null.
   * @return An optional containing the Author if found, empty if not.
   * @throws NullPointerException if displayName is null.
   */
  public Optional<Author> findByName(String displayName) {
    Objects.requireNonNull(displayName, "displayName must not be null");
    String targetKey = Author.normalizedKey(displayName);

    for (Author author : idMap.values()) {
      if (Author.normalizedKey(author.getDisplayName()).equals(targetKey)) {
        return Optional.of(author);
      }
    }
    return Optional.empty();
  }

  /**
   * Rename an existing author, ensuring the new name is unique.
   *
   * <p><i>Exceptions implemented with help from AI.</i>
   *
   * <p>Why: Allows user to change their display name while maintaining the integrity of the sytem.
   * Enforces that two user cannot have the same display name.
   *
   * <p>How: Ensures parameters are not null. Verifies that the author ID exists. Uses {@code
   * baneExists} to check if the new name is already taken. If checks pass, calls {@code
   * author.rename()} to update object.
   *
   * @param id The UUID of the author to rename. Must not be null.
   * @param newDisplayName The new display name to set. Must not be null.
   * @return The updated Author object.
   * @throws RuntimeException if the ID is not found in the register.
   * @throws RuntimeException if an author with the new name already exists.
   * @throws NullPointerException if the id or newDisplayName is null.
   */
  public Author rename(UUID id, String newDisplayName) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(newDisplayName, "newDisplayName must not be null");
    Author author = idMap.get(id);
    if (author == null) {
      throw new RuntimeException("No registered author with this ID: " + id);
    }

    String normalizedKey = Author.normalizedKey(newDisplayName);
    if (nameExists(normalizedKey, id)) {
      throw new RuntimeException("An author with this name already exists: " + newDisplayName);
    }
    author.rename(newDisplayName);
    return author;
  }

  /**
   * Removes an author from the register by ID.
   *
   * @param id The UUID of the author to remove. Must not be null.
   * @return {@code true} if the author was successfully removed, {@code false} if not found.
   * @throws NullPointerException if the id is null.
   */
  public boolean remove(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return idMap.remove(id) != null;
  }

  /**
   * Checks if a display name already exists in the register.
   *
   * <p><i>Optional ID implementation suggested by ChatGPT.</i>
   *
   * @param normalizedKey The normalized key to check.
   * @param excludeId An optional ID to exclude from the check (used during rename).
   * @return {@code true} if the name exists on another author, {@code false} otherwise.
   */
  private boolean nameExists(String normalizedKey, UUID excludeId) {
    for (Author author : idMap.values()) {
      if (author.getId().equals(excludeId)) {
        continue;
      }
      if (Author.normalizedKey(author.getDisplayName()).equals(normalizedKey)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the total number of authors in the register.
   *
   * @return The count of registered authors.
   */
  public int getAuthorNumber() {
    return idMap.size();
  }

  /**
   * Removes all authors from the register except for the administrator.
   *
   * <p>This ensures the system remains accessible after a reset. Uses {@code removeIf} to filter
   * out non-admin users.
   *
   * <p><i>Admin-filter made with help from AI.</i>
   */
  public void clearExceptAdmin() {
    idMap.values().removeIf(author -> !author.getDisplayName().equalsIgnoreCase("admin"));
  }
}
