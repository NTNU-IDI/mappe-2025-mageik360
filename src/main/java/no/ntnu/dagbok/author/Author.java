package no.ntnu.dagbok.author;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a diary entry author in the system.
 *
 * <p>Each author is identified by a unique UUID. Public display name can be updated,
 * but must follow valid formatting. The class also implements a simple password.</p>
 */
public class Author {

  public static final int MAXIMUM_NAME_LENGTH = 100;

  private final UUID id;
  private final LocalDateTime createdAt;
  private String password;

  private String displayName;
  private LocalDateTime updatedAt;

  /**
   * Creates a new author with the provided display name and password.
   *
   * <p>A unique UUID is created and
   * timestamp is set to the current time.</p>
   *
   * @param displayName The publicly facing display name of the author. Must not be null or blank.
   * @param password The user's password. Must meet length requirement.
   * @throws IllegalArgumentException if display name is invalid or password is too short.
   * @throws IllegalArgumentException if id or display name is null.
   */
  public Author(String displayName, String password) {
    this(UUID.randomUUID(), displayName, password);
  }

  /**
   * Internal constructor to create an Author with a specific id.
   *
   * <p>Initializes creation and update
   * timestamps for current time.</p>
   *
   * @param id The unique identifier for the author. Must not be null.
   * @param displayName The display name of the author. Must not be null or blank.
   * @param password The password of the author. Must meet length requirement.
   * @throws IllegalArgumentException if display name is invalid or password is too short.
   * @throws IllegalArgumentException if id or display name is null.
   */
  public Author(UUID id, String displayName, String password) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    setDisplayNameInternal(validateAndNormalizeForStorage(displayName));
    LocalDateTime presentTime = LocalDateTime.now();
    this.createdAt = presentTime;
    this.updatedAt = presentTime;
    this.password = validatePassword(password);
  }

  /**
   * Generates a normalized key for uniqueness checks.
   *
   * <p>The normalized process trims spaces, removes certain text symbols. Ensures
   * distinct authors in the register despite formatting changes.</p>
   *
   * <p><i>Implementation based on suggestion from ChatGPT.</i></p>
   *
   * @param name The name input to normalize.
   * @return A normalized string key.
   * @throws IllegalArgumentException if the name is blank or exceeds maximum length.
   * @throws NullPointerException if the name is null.
   */
  public static String normalizedKey(String name) {
    String trimmed = Objects.requireNonNull(name, "displayName must not be null").trim();
    if (trimmed.isBlank()) {
      throw new IllegalArgumentException("displayName must not be blank");
    }

    String collapsed = trimmed.replaceAll("\\s+", " ");
    if (collapsed.length() > MAXIMUM_NAME_LENGTH) {
      throw new IllegalArgumentException(
          "displayName length must be " + MAXIMUM_NAME_LENGTH + " or less");
    }

    String lower = collapsed.toLowerCase(Locale.ROOT);
    String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
    return decomposed.replaceAll("\\p{M}", "");
  }

  /**
   * Validates and normalizes the display name for storage.
   *
   * <p>Trims leading/trailing whitespace and reduces internal spaces to one.</p>
   *
   * <p><i>Based on suggestion from ChatGPT on how to improve input validation.</i></p>
   *
   * @param name Input name for author
   * @return A validated and cleaned string for storage.
   * @throws IllegalArgumentException if the name is blank or exceeds maximum length.
   * @throws NullPointerException if the name is null.
   */
  private static String validateAndNormalizeForStorage(String name) {
    String trimmed = Objects.requireNonNull(name, "displayName must not be null").trim();
    if (trimmed.isBlank()) {
      throw new IllegalArgumentException("displayName must not be blank");
    }
    String collapsed = trimmed.replaceAll("\\s+", " ");
    if (collapsed.length() > MAXIMUM_NAME_LENGTH) {
      throw new IllegalArgumentException(
          "displayName length must be " + MAXIMUM_NAME_LENGTH + " or less");
    }
    return collapsed;
  }

  // setters

  /**
   * Updates the author's display name and refreshes the update timestamp.
   *
   * @param updatedDisplayName The new, validated author name.
   */
  void setDisplayNameInternal(String updatedDisplayName) {
    this.displayName = updatedDisplayName;
    this.updatedAt = LocalDateTime.now();
  }

  // getters

  /**
   * Returns the display name of the author.
   *
   * @return The author's name.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns the unique identifier of the author.
   *
   * @return The author's UUID.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Returns the date and time when the author was created.
   *
   * @return The creation timestamp.
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Returns the date and time when the author was last updated.
   *
   * @return The last author update timestamp.
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Renames the author.
   *
   * <p>Validates and normalizes the new name before updating.</p>
   *
   * @param newDisplayName The new name to set.
   * @throws IllegalArgumentException if the new name is invalid.
   */
  void rename(String newDisplayName) {
    setDisplayNameInternal(validateAndNormalizeForStorage(newDisplayName));
  }

  /**
   * Indicates if some other object is equal to this one.
   *
   * <p>Equality is overwritten to be determined by unique author ID</p>
   *
   * @param o The reference object to compare with.
   * @return {@code true} if the object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Author other)) {
      return false;
    }
    return id.equals(other.id);
  }

  /**
   * Returns a hash code value for the author.
   *
   * <p>The hash code is based on the unique Author ID</p>
   *
   * @return A hash code value for this object.
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Checks in the provided password matches the author's password.
   *
   * @param input The password string to verify.
   * @return {@code true} if the password matches, {@code false} otherwise.
   */
  public boolean checkPassword(String input) {
    return this.password.equals(input);
  }

  /**
   * Validates the format of a password.
   *
   * @param input The password to validate.
   * @return The validated password.
   * @throws IllegalArgumentException if the password is too short or null.
   */
  private String validatePassword(String input) {
    if (input == null || input.length() < 4) {
      throw new IllegalArgumentException("Password must be at least 4 characters long");
    }
    return input;
  }

  /**
   * Returns a string representation of the author.
   *
   * @return A string containing the author's ID and display name.
   */
  @Override
  public String toString() {
    return "Author - Unique ID = " + id + ", Public facing name: " + displayName;
  }
}
