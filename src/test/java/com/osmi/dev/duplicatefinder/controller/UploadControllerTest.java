package com.osmi.dev.duplicatefinder.controller;

import com.osmi.dev.duplicatefinder.model.Contact;
import com.osmi.dev.duplicatefinder.model.DuplicateMatch;
import com.osmi.dev.duplicatefinder.service.ContactService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas para el UploadController.
 * @WebMvcTest carga solo la capa web (el controlador) para hacer pruebas rápidas y enfocadas.
 */
@WebMvcTest(UploadController.class)
class UploadControllerTest {

    /**
     * MockMvc es una herramienta de Spring que nos permite simular peticiones HTTP (GET, POST, etc.)
     * sin necesidad de levantar un servidor real.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * @MockBean crea un "doble de prueba" o un mock del ContactService.
     * Esto nos permite aislar el controlador y decirle al servicio falso
     * cómo debe comportarse durante las pruebas.
     */
    @MockBean
    private ContactService contactService;

    @Test
    @DisplayName("showUploadForm debe devolver la vista 'upload' con estado OK")
    void showUploadForm_shouldReturnUploadView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/")) // Simula una petición GET a la raíz
                .andExpect(status().isOk()) // Esperamos que la respuesta sea un HTTP 200 OK
                .andExpect(view().name("upload")); // Esperamos que se renderice la vista "upload.html"
    }

    @Test
    @DisplayName("handleFileUpload debe procesar un archivo válido y redirigir con resultados")
    void handleFileUpload_shouldProcessValidFileAndRedirect() throws Exception {
        // Arrange: Preparamos un archivo falso y definimos el comportamiento de nuestro servicio mock
        MockMultipartFile file = new MockMultipartFile(
                "file", "contacts.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "Contenido Falso del Excel".getBytes()
        );

        List<DuplicateMatch> fakeDuplicates = List.of(new DuplicateMatch("1001", "1002", "Alta", 90));

        // Le decimos al mock que devuelva una lista vacía cuando se parsea el excel (para evitar NullPointerException)
        when(contactService.parseContactsFromExcel(any(InputStream.class))).thenReturn(new ArrayList<Contact>());
        // Le decimos al mock que devuelva nuestra lista de duplicados falsos cuando se le pida
        when(contactService.findDuplicates(any())).thenReturn(fakeDuplicates);

        // Act & Assert
        mockMvc.perform(multipart("/upload").file(file)) // Simula una subida de archivo a /upload
                .andExpect(status().is3xxRedirection()) // Esperamos una redirección (HTTP 302)
                .andExpect(redirectedUrl("/")) // Esperamos que la redirección sea a la página principal
                .andExpect(flash().attributeExists("duplicates")); // Verificamos que los resultados se pasaron a la vista
    }

    @Test
    @DisplayName("handleFileUpload debe manejar un archivo vacío y redirigir con un mensaje de error")
    void handleFileUpload_shouldHandleEmptyFileAndRedirect() throws Exception {
        // Arrange: Creamos un archivo falso que está vacío
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "original-filename.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[0] // Contenido vacío
        );

        // Act & Assert
        mockMvc.perform(multipart("/upload").file(emptyFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorMessage")); // Verificamos que se pasó un mensaje de error
    }
}