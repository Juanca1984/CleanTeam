package com.cleanteam.mandarinplayer.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "themes")
@Getter @Setter
@NoArgsConstructor
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Basic Greetings"

    private String description; // e.g., "Learn to say hello and goodbye"

    // One-to-Many relationship: One Theme has many Words
    // mappedBy = "theme" tells Hibernate the relationship is owned by the 'theme' field in the Word class
    // orphanRemoval = true: If you remove a word from this list, it is deleted from the database
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Word> words = new ArrayList<>();

    // --- HELPER METHODS (Best practice for bidirectional relationships) ---

    // These methods ensure that both the Java list and the Entity relationship
    // are kept in sync when you add data.
    public void addWord(Word word) {
        words.add(word);
        word.setTheme(this);
    }

    public void removeWord(Word word) {
        words.remove(word);
        word.setTheme(null);
    }
}
