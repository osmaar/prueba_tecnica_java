package com.osmi.dev.duplicatefinder.service;

import com.osmi.dev.duplicatefinder.model.Contact;
import com.osmi.dev.duplicatefinder.model.DuplicateMatch;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase ContactService.
 * Se enfoca en validar la lógica de negocio de forma aislada.
 */
class ContactServiceTest {

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        // Creamos una nueva instancia del servicio antes de cada prueba
        // para asegurar que los tests no interfieran entre sí.
        contactService = new ContactService();
    }

    // --- PRUEBAS PARA EL MÉTODO findDuplicates ---

    @Test
    @DisplayName("Debe detectar coincidencia 'Alta' (score 90) por email idéntico")
    void findDuplicates_shouldReturnAltaForSameEmail() {
        // Arrange: Preparamos los datos de entrada
        Contact c1 = new Contact();
        c1.setId("1");
        c1.setEmail("juan.perez@test.com");

        Contact c2 = new Contact();
        c2.setId("2");
        c2.setEmail("juan.perez@test.com");

        List<Contact> contacts = List.of(c1, c2);

        // Act: Ejecutamos el método a probar
        List<DuplicateMatch> matches = contactService.findDuplicates(contacts);

        // Assert: Verificamos que el resultado sea el esperado
        assertEquals(1, matches.size());
        assertEquals("Alta", matches.getFirst().getPrecision());
        assertEquals(90, matches.getFirst().getScore());
    }

    @Test
    @DisplayName("Debe detectar coincidencia 'Baja' (score 50) por apellido y CP idénticos")
    void findDuplicates_shouldReturnBajaForSameLastNameAndPostalCode() {
        // Arrange
        Contact c1 = new Contact();
        c1.setId("1");
        c1.setApellido("Garcia");
        c1.setCodigoPostal("28080");
        c1.setEmail("email1@test.com");

        Contact c2 = new Contact();
        c2.setId("2");
        c2.setApellido("Garcia");
        c2.setCodigoPostal("28080");
        c2.setEmail("email2@test.com"); // Email diferente

        // Act
        List<DuplicateMatch> matches = contactService.findDuplicates(List.of(c1, c2));

        // Assert
        assertEquals(1, matches.size());
        assertEquals("Baja", matches.get(0).getPrecision());
        assertEquals(50, matches.get(0).getScore());
    }

    @Test
    @DisplayName("Debe sumar puntuaciones si se cumplen múltiples criterios")
    void findDuplicates_shouldCombineScoresForMultipleCriteria() {
        // Arrange
        Contact c1 = new Contact();
        c1.setId("1");
        c1.setApellido("Lopez");
        c1.setCodigoPostal("50001");
        c1.setEmail("ana.lopez@test.com");

        Contact c2 = new Contact();
        c2.setId("2");
        c2.setApellido("Lopez");
        c2.setCodigoPostal("50001");
        c2.setEmail("ana.lopez@test.com"); // Cumple ambos criterios

        // Act
        List<DuplicateMatch> matches = contactService.findDuplicates(List.of(c1, c2));

        // Assert
        assertEquals(1, matches.size());
        assertEquals("Alta", matches.get(0).getPrecision());
        assertEquals(140, matches.get(0).getScore(), "La puntuación debe ser la suma de 90 + 50");
    }

    @Test
    @DisplayName("No debe encontrar duplicados si no se cumplen los criterios")
    void findDuplicates_shouldReturnEmptyListForNoMatches() {
        // Arrange
        Contact c1 = new Contact();
        c1.setId("1");
        c1.setApellido("Ruiz");

        Contact c2 = new Contact();
        c2.setId("2");
        c2.setApellido("Sanz");

        // Act
        List<DuplicateMatch> matches = contactService.findDuplicates(List.of(c1, c2));

        // Assert
        assertTrue(matches.isEmpty(), "La lista de coincidencias debería estar vacía.");
    }

    // --- PRUEBA PARA EL MÉTODO parseContactsFromExcel ---

    @Test
    @DisplayName("Debe leer correctamente los contactos de un InputStream de Excel")
    void parseContactsFromExcel_shouldParseValidStream() throws Exception {
        // Arrange: Creamos un archivo Excel falso en memoria para no depender de un archivo físico.
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Contacts");
            // Creamos las dos filas de cabecera que el método original omite
            sheet.createRow(0).createCell(0).setCellValue("Header 1");
            sheet.createRow(1).createCell(0).setCellValue("Header 2");

            // Creamos una fila de datos de prueba
            Row dataRow = sheet.createRow(2);
            dataRow.createCell(0).setCellValue(123); // ID
            dataRow.createCell(1).setCellValue("Juan"); // Nombre
            dataRow.createCell(2).setCellValue("Perez"); // Apellido
            dataRow.createCell(3).setCellValue("juan.perez@test.com");
            dataRow.createCell(4).setCellValue("28080");
            dataRow.createCell(5).setCellValue("Calle Falsa 123");

            workbook.write(out);
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            // Act: Ejecutamos el método con nuestro stream falso
            List<Contact> contacts = contactService.parseContactsFromExcel(inputStream);

            // Assert: Verificamos que los datos se leyeron correctamente
            assertEquals(1, contacts.size());
            Contact parsedContact = contacts.get(0);
            assertEquals("123", parsedContact.getId());
            assertEquals("Juan", parsedContact.getNombre());
            assertEquals("Perez", parsedContact.getApellido());
        }
    }
}