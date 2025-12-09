package com.cleanteam.mandarinplayer.Controller;

import com.cleanteam.mandarinplayer.DTO.ThemeDTO;
import com.cleanteam.mandarinplayer.DTO.WordDTO;
import com.cleanteam.mandarinplayer.Service.ThemeService;
import com.cleanteam.mandarinplayer.Service.WordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;
    private final WordService wordService;

    public ThemeController(ThemeService themeService, WordService wordService) {
        this.themeService = themeService;
        this.wordService = wordService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeDTO>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @PostMapping
    public ResponseEntity<ThemeDTO> createTheme(@RequestBody ThemeDTO themeDTO) {
        return ResponseEntity.ok(themeService.createTheme(themeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThemeDTO> getThemeById(@PathVariable Long id) {
        return ResponseEntity.ok(themeService.getThemeById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{themeId}/words")
    public ResponseEntity<List<WordDTO>> getWordsByTheme(@PathVariable Long themeId) {
        return ResponseEntity.ok(wordService.getWordsByTheme(themeId));
    }

    @PostMapping("/{id}/vocabulary")
    public ResponseEntity<WordDTO> addVocabulary(@PathVariable Long id, @RequestBody WordDTO wordDTO) {
        wordDTO.setThemeId(id);
        return ResponseEntity.ok(wordService.createWord(wordDTO));
    }
}