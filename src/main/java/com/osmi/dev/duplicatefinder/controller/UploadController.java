package com.osmi.dev.duplicatefinder.controller;

import com.osmi.dev.duplicatefinder.model.Contact;
import com.osmi.dev.duplicatefinder.model.DuplicateMatch;
import com.osmi.dev.duplicatefinder.service.ContactService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.List;

@Controller
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final ContactService contactService;

    public UploadController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Muestra el formulario de carga.
     * Retorna el nombre de la vista (upload.html con Thymeleaf).
     */
    @GetMapping("/")
    public String showUploadForm() {
        return "upload"; 
    }

    /**
     * Recibe el archivo XLSX, lo parsea a contactos y ejecuta la detección de duplicados.
     * Usa RedirectAttributes para mandar mensajes flash y resultados a la misma vista.
     *
     * Flujo:
     * 1) Valida que el archivo exista y sea .xlsx
     * 2) Lee el InputStream y crea la lista de Contact
     * 3) Ejecuta findDuplicates
     * 4) Guarda resultados/mensajes en flash y redirige a "/"
     *
     * @param file archivo subido por el usuario (esperado .xlsx)
     * @param redirectAttributes contenedor de mensajes flash para la redirección
     * @return redirección al formulario
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        // Validaciones básicas de carga
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecciona un archivo para subir.");
            return "redirect:/";
        }

        try (InputStream inputStream = file.getInputStream()) {
            List<Contact> contacts = contactService.parseContactsFromExcel(inputStream);
            List<DuplicateMatch> duplicates = contactService.findDuplicates(contacts);

            redirectAttributes.addFlashAttribute("duplicates", duplicates);
            redirectAttributes.addFlashAttribute("successMessage", "Archivo procesado. Se encontraron " + duplicates.size() + " coincidencias.");
        } catch (Exception e) {
            logger.error("Error al procesar el archivo subido por el usuario.", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar el archivo. Asegúrate de que el formato sea correcto.");
        }

        return "redirect:/";
    }
}
