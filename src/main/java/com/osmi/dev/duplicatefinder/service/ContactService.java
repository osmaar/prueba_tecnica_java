package com.osmi.dev.duplicatefinder.service;

import com.osmi.dev.duplicatefinder.model.Contact;
import com.osmi.dev.duplicatefinder.model.DuplicateMatch;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ContactService {

    /**
     * Procesa una lista de contactos para encontrar posibles duplicados.
     */
    public List<DuplicateMatch> findDuplicates(List<Contact> contacts) {
        List<DuplicateMatch> matches = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                Contact c1 = contacts.get(i);
                Contact c2 = contacts.get(j);
                int score = calculateScore(c1, c2);

                if (score > 85) {
                    matches.add(new DuplicateMatch(c1.getId(), c2.getId(), "Alta", score));
                } else if (score > 20) {
                    matches.add(new DuplicateMatch(c1.getId(), c2.getId(), "Baja", score));
                }
            }
        }
        return matches;
    }
    /**
     * Calcula una puntuación de similitud entre dos contactos.
     */
    private int calculateScore(Contact c1, Contact c2) {
        int score = 0;
        if (c1.getEmail() != null && !c1.getEmail().isBlank() && c1.getEmail().equalsIgnoreCase(c2.getEmail())) {
            score += 90;
        }
        if (c1.getCodigoPostal() != null && c1.getCodigoPostal().equals(c2.getCodigoPostal()) &&
                c1.getApellido() != null && c1.getApellido().equalsIgnoreCase(c2.getApellido())) {
            score += 50;
        }
        return score;
    }

    /**
     * Lee un archivo Excel y lo convierte en una lista de objetos Contact.
     */
    public List<Contact> parseContactsFromExcel(InputStream inputStream) throws Exception {
        List<Contact> contacts = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Omitir las dos primeras filas (cabeceras)
            if (rows.hasNext()) rows.next();
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Contact contact = new Contact();

                // Extraer datos de las celdas de forma segura
                contact.setId(getCellValueAsString(currentRow.getCell(0)));
                contact.setNombre(getCellValueAsString(currentRow.getCell(1)));
                contact.setApellido(getCellValueAsString(currentRow.getCell(2)));
                contact.setEmail(getCellValueAsString(currentRow.getCell(3)));
                contact.setCodigoPostal(getCellValueAsString(currentRow.getCell(4)));
                contact.setDireccion(getCellValueAsString(currentRow.getCell(5)));

                // Solo agregar si el contacto tiene un ID
                if (contact.getId() != null && !contact.getId().isBlank()) {
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

    /**
     * Método de utilidad para leer el valor de una celda como String,
     * de forma segura, sin importar si la celda es nula, de tipo numérico o texto.
     */
    private String getCellValueAsString(Cell cell) {
        // 1. Si la celda es nula, devolvemos una cadena vacía para evitar errores.
        if (cell == null) {
            return "";
        }

        // 2. Leemos el valor según el tipo de celda
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            // Para cualquier otro tipo (fórmula, booleano, etc.), devolvemos una cadena vacía.
            default -> "";
        };
    }
}
