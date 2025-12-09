package com.cleanteam.mandarinplayer.Service;

import com.cleanteam.mandarinplayer.DTO.ThemeDTO;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Repository.ThemeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.cleanteam.mandarinplayer.DTO.WordDTO;
import com.cleanteam.mandarinplayer.Model.Word;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeDTO> getAllThemes() {
        return themeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ThemeDTO createTheme(ThemeDTO themeDTO) {
        Theme theme = new Theme();
        theme.setName(themeDTO.getName());
        theme.setDescription(themeDTO.getDescription());
        Theme savedTheme = themeRepository.save(theme);
        return mapToDTO(savedTheme);
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    public ThemeDTO getThemeById(Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        return mapToDTO(theme);
    }

    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }

    private ThemeDTO mapToDTO(Theme theme) {
        ThemeDTO dto = new ThemeDTO();
        dto.setId(theme.getId());
        dto.setName(theme.getName());
        dto.setDescription(theme.getDescription());
        if (theme.getWords() != null) {
            dto.setVocabulary(theme.getWords().stream()
                    .map(word -> {
                        com.cleanteam.mandarinplayer.DTO.WordDTO wordDTO = new com.cleanteam.mandarinplayer.DTO.WordDTO();
                        wordDTO.setId(word.getId());
                        wordDTO.setCharacter(word.getCharacter());
                        wordDTO.setPinyin(word.getPinyin());
                        wordDTO.setTranslation(word.getTranslation());
                        wordDTO.setThemeId(theme.getId());
                        return wordDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}