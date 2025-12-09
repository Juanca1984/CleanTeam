package com.cleanteam.mandarinplayer.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cleanteam.mandarinplayer.DTO.ThemeDTO;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Model.Word;
import com.cleanteam.mandarinplayer.Repository.ThemeRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para ThemeService")
class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeService themeService;

    private Theme testTheme;
    private ThemeDTO testThemeDTO;

    @BeforeEach
    void setUp() {
        testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setName("Basic Greetings");
        testTheme.setDescription("Learn to say hello and goodbye");

        testThemeDTO = new ThemeDTO();
        testThemeDTO.setId(1L);
        testThemeDTO.setName("Basic Greetings");
        testThemeDTO.setDescription("Learn to say hello and goodbye");
    }

    @Test
    @DisplayName("Debe obtener todos los temas")
    void testGetAllThemes() {
        List<Theme> themes = new ArrayList<>();
        themes.add(testTheme);

        Theme theme2 = new Theme();
        theme2.setId(2L);
        theme2.setName("Numbers");
        theme2.setDescription("Learn numbers 1-10");
        themes.add(theme2);

        when(themeRepository.findAll()).thenReturn(themes);

        List<ThemeDTO> result = themeService.getAllThemes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Basic Greetings", result.get(0).getName());
        assertEquals("Numbers", result.get(1).getName());
        verify(themeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener lista vacía cuando no hay temas")
    void testGetAllThemesEmpty() {
        when(themeRepository.findAll()).thenReturn(new ArrayList<>());

        List<ThemeDTO> result = themeService.getAllThemes();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(themeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe crear un tema correctamente")
    void testCreateTheme() {
        when(themeRepository.save(any(Theme.class))).thenReturn(testTheme);

        ThemeDTO result = themeService.createTheme(testThemeDTO);

        assertNotNull(result);
        assertEquals("Basic Greetings", result.getName());
        assertEquals("Learn to say hello and goodbye", result.getDescription());
        verify(themeRepository, times(1)).save(any(Theme.class));
    }

    @Test
    @DisplayName("Debe obtener tema por ID correctamente")
    void testGetThemeById() {
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        ThemeDTO result = themeService.getThemeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Basic Greetings", result.getName());
        verify(themeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tema no existe")
    void testGetThemeByIdNotFound() {
        when(themeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> themeService.getThemeById(999L));
        verify(themeRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe eliminar tema correctamente")
    void testDeleteTheme() {
        themeService.deleteTheme(1L);

        verify(themeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe obtener todos los temas sin convertir a DTO")
    void testFindAll() {
        List<Theme> themes = new ArrayList<>();
        themes.add(testTheme);

        when(themeRepository.findAll()).thenReturn(themes);

        List<Theme> result = themeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Basic Greetings", result.get(0).getName());
        verify(themeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe mapear tema con palabras a DTO correctamente")
    void testMapThemeWithWordsToDTO() {
        List<Word> words = new ArrayList<>();
        Word word1 = new Word();
        word1.setId(1L);
        word1.setCharacter("一");
        word1.setPinyin("yi");
        word1.setTranslation("One");
        words.add(word1);

        testTheme.setWords(words);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        ThemeDTO result = themeService.getThemeById(1L);

        assertNotNull(result);
        assertNotNull(result.getVocabulary());
        assertEquals(1, result.getVocabulary().size());
    }

    @Test
    @DisplayName("Debe mapear tema sin palabras a DTO")
    void testMapThemeWithoutWordsToDTO() {
        testTheme.setWords(null);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        ThemeDTO result = themeService.getThemeById(1L);

        assertNotNull(result);
        assertNull(result.getVocabulary());
    }

    @Test
    @DisplayName("Debe mapear tema con lista vacía de palabras")
    void testMapThemeWithEmptyWordsToDTO() {
        testTheme.setWords(new ArrayList<>());
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        ThemeDTO result = themeService.getThemeById(1L);

        assertNotNull(result);
        assertNotNull(result.getVocabulary());
        assertEquals(0, result.getVocabulary().size());
    }

    @Test
    @DisplayName("Debe crear tema con descripción null")
    void testCreateThemeWithNullDescription() {
        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setName("Nueva Categoría");
        themeDTO.setDescription(null);

        Theme savedTheme = new Theme();
        savedTheme.setId(3L);
        savedTheme.setName("Nueva Categoría");
        savedTheme.setDescription(null);

        when(themeRepository.save(any(Theme.class))).thenReturn(savedTheme);

        ThemeDTO result = themeService.createTheme(themeDTO);

        assertNotNull(result);
        assertEquals("Nueva Categoría", result.getName());
        assertNull(result.getDescription());
    }
}
