package no.ntnu.dagbok.author;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Object representing a diary entry author
 *
 * Identity is kept unique id
 * Display name can be updated, but must have valid formatting
 *
 */
public class Author {

  public static final int MAXIMUM_NAME_LENGTH = 100;

  private final UUID id;
  private final LocalDateTime createdAt;
  private String password;

  private String displayName;
  private LocalDateTime updatedAt;

  public Author(String displayName, String password) {

    this(UUID.randomUUID(), displayName, password);
  }

  public Author(UUID id, String displayName, String password){
    this.id = Objects.requireNonNull(id, "id must not be null");
    setDisplayNameInternal(validateAndNormalizeForStorage(displayName));
    LocalDateTime presentTime = LocalDateTime.now();
    this.createdAt = presentTime;
    this.updatedAt = presentTime;
    this.password = validatePassword(password);
  }

  /**
   * Normalized key used for uniqueness checks
   * Trims and collapsed whitespace, then transforms to lowercase
   * Based on suggestion from ChatGPT on how to keep distinct authors in register
   * @param name Name
   * @return Kay
   */
  public static String normalizedKey(String name){
    String trimmed = Objects.requireNonNull(name, "displayName must not be null").trim();
    if (trimmed.isBlank()){
      throw new IllegalArgumentException("displayName must not be blank");
    }

    String collapsed = trimmed.replaceAll("\\s+", " ");
    if (collapsed.length() > MAXIMUM_NAME_LENGTH){
      throw new IllegalArgumentException("displayName length must be " + MAXIMUM_NAME_LENGTH + " or less");
    }

    String lower = collapsed.toLowerCase(Locale.ROOT);
    String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
    return decomposed.replaceAll("\\p{M}","");
  }

  /**
   * Validates and returns processed value for displayName
   * Trims and collapses spaces
   *
   * Based on suggestion from ChatGPT on how to improve input validation
   * @param name Input name for author
   * @return
   */
  private static String validateAndNormalizeForStorage(String name){
    String trimmed = Objects.requireNonNull(name, "displayName must not be null").trim();
    if (trimmed.isBlank()){
      throw new IllegalArgumentException("displayName must not be blank");
    }
    String collapsed = trimmed.replaceAll("\\s+"," ");
    if (collapsed.length() > MAXIMUM_NAME_LENGTH){
      throw new IllegalArgumentException("displayName length must be " + MAXIMUM_NAME_LENGTH + " or less");
    }
    return collapsed;
  }

  // setters

  /**
   * Setter for authors actual name
   * @param updatedDisplayName updated name of author
   */
  void setDisplayNameInternal(String updatedDisplayName){
    this.displayName = updatedDisplayName;
    this.updatedAt = LocalDateTime.now();
  }

  // getters

  /**
   * Getter for actual author name
   * @return name of author
   */
  public String getDisplayName(){
    return displayName;
  }

  /**
   * Getter for unique author ID
   * @return unique ID of author
   */
  public UUID getId(){
    return id;
  }

  /**
   * Getter for author creation date and time
   * @return author creation LocalDateTime
   */
  public LocalDateTime getCreatedAt(){
    return createdAt;
  }

  /**
   * Getter for author update date and time
   * @return author update LocalDateTime
   */
  public LocalDateTime getUpdatedAt(){
    return updatedAt;
  }

  void rename(String newDisplayName){
    setDisplayNameInternal(validateAndNormalizeForStorage(newDisplayName));
  }

  /**
   * Overridden boolean comparison for authors based on a unique ID
   * Based normalized key suggestion from ChatGPT
   * @param o author object to be compared with other author
   * @return true/false value based on author ID
   */
  @Override
  public boolean equals(Object o) {
    if ( this == o ) return true;
    if (!(o instanceof Author other)) return false;
    return id.equals(other.id);
  }

  /**
   * Overridden hashing based on author ID
   * @return hash of author ID
   */
  @Override
  public int hashCode(){
    return id.hashCode()
;  }

  /**
   * Checks in the provided password is correct.
   * @param input password to be checked.
   * @return true if password matches, false otherwise.
   */
  public boolean checkPassword(String input){
    return this.password.equals(input);
  }

  private String validatePassword(String input){
    if (input == null || input.length() < 4) {
      throw new IllegalArgumentException("Password must be at least 4 characters long");
    }
    return input;
  }

  /**
   * Overridden printing of author info
   * @return
   */
  @Override
  public String toString(){
    return "Author - Unique ID = " + id + ", Public facing name: "+displayName;
  }

}
