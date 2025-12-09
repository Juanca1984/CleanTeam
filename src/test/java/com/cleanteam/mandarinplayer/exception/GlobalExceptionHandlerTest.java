package com.cleanteam.mandarinplayer.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("Pruebas para GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException con uk_ en mensaje")
    void testHandleDataIntegrityViolationWithUniqueConstraint() {
        DataIntegrityViolationException exception = 
            new DataIntegrityViolationException("Duplicate entry for uk_theme_name");

        ResponseEntity<String> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: El nombre del tema ya existe.", response.getBody());
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException genérica")
    void testHandleDataIntegrityViolationGeneric() {
        DataIntegrityViolationException exception = 
            new DataIntegrityViolationException("Foreign key constraint violated");

        ResponseEntity<String> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Error de integridad de datos:"));
    }

    @Test
    @DisplayName("Debe manejar RuntimeException")
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Test runtime error");

        ResponseEntity<String> response = exceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error interno:"));
        assertTrue(response.getBody().contains("Test runtime error"));
    }

    @Test
    @DisplayName("Debe manejar Exception genérica")
    void testHandleGeneralException() {
        Exception exception = new Exception("Test general error");

        ResponseEntity<String> response = exceptionHandler.handleGeneralException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error inesperado:"));
        assertTrue(response.getBody().contains("Test general error"));
    }
}
