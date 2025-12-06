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
   * <p>This ensures the system remains accessible after a reset.
   * Uses {@code removeIf} to filter out non-admin users.</p>
   *
   * <p><i>Admin-filter made with help from AI.</i></p>
   */
  public void clearExceptAdmin() {
    idMap.values().removeIf(author -> !author.getDisplayName().equalsIgnoreCase("admin"));
  }
}
