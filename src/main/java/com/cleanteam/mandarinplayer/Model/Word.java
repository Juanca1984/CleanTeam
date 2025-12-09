package com.cleanteam.mandarinplayer.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "words")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Chinese character (e.g., 你好)
    @Column(nullable = false)
    private String character;

    // The pronunciation (e.g., Nǐ hǎo)
    @Column(nullable = false)
    private String pinyin;

    // The meaning (e.g., Hello)
    @Column(nullable = false)
    private String translation;

    // Many-to-One relationship: Many words belong to one Theme
    // FetchType.LAZY improves performance by not loading the Theme unless requested
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    @JsonBackReference
    private Theme theme;

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", character='" + character + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
