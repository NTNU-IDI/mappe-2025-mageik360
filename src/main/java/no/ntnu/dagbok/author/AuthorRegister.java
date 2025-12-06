package no.ntnu.dagbok.author;

import java.util.*;

public class AuthorRegister {

  private final Map<UUID, Author> idMap = new HashMap<>();

  /**
   * Creates a new Author and adds it to the register
   *
   * @param displayName name of author to be added
   * @throws RuntimeException when a name already exists
   * @throws RuntimeException for invalid author names
   * @return author
   */
  public Author addAuthor(String displayName, String password) {
    Objects.requireNonNull(displayName, "displayName must not be null");
    Author author = new Author(displayName, password);
    idMap.put(author.getId(), author);
    return author;
  }

  /**
   * Getter author in register
   *
   * @param id UUID of author
   * @return Author
   */
  public Optional<Author> getById(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return Optional.ofNullable(idMap.get(id));
  }

  /**
   * Getter for all authors Comparator provided by ChatGPT
   *
   * @return List of all authors
   */
  public List<Author> getAll() {
    List<Author> list = new ArrayList<>(idMap.values());
    list.sort(
        Comparator.comparing((Author a) -> Author.normalizedKey(a.getDisplayName()))
            .thenComparing(Author::getId));
    return Collections.unmodifiableList(list);
  }

  /**
   * Finds author by name Optional return value suggested by ChatGPT
   *
   * @param displayName name of author to be found
   * @return Author to be found
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
   * Rename a previously existing author with unique name
   *
   * @param id
   * @param newDisplayName
   * @return
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
   * Removes an author from the register by id.
   *
   * @param id UUID of author
   * @return true if successfully removed, false if not found
   */
  public boolean remove(UUID id) {
    Objects.requireNonNull(id, "id must not be null");
    return idMap.remove(id) != null;
  }

  /**
   * Helper method which checks for names in register
   *
   * @param normalizedKey
   * @param excludeId
   * @return
   */
  private boolean nameExists(String normalizedKey, UUID excludeId) {
    for (Author author : idMap.values()) {
      if (excludeId != null && author.getId().equals(excludeId)) {continue;}
      if (Author.normalizedKey(author.getDisplayName()).equals(normalizedKey)) {
        return true;
      }
    }
    return false;
  }

  /** Number of authors in the register */
  public int getAuthorNumber() {
    return idMap.size();
  }

  /** Completely clears registry of authors */
  public void clear() {
    idMap.clear();
  }
}
