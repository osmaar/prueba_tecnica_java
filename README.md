# Prueba técnica de duplicados ✨

Aplicación web desarrollada con Spring Boot y Thymeleaf para identificar contactos duplicados dentro de un archivo Excel. El procesamiento se realiza completamente en memoria, aplicando una lógica de puntuación personalizable.

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-blueviolet)

---
## Descripción General 📖

Este proyecto es una solución al desafío de encontrar posibles contactos duplicados en un gran conjunto de datos proporcionado en formato `.xlsx`. La aplicación ofrece una interfaz web simple para que el usuario suba el archivo. Una vez subido, el backend lo procesa en memoria, compara cada contacto con todos los demás y asigna una puntuación de similitud basada en criterios predefinidos. Finalmente, los resultados se presentan en una tabla clara que muestra los pares de contactos duplicados, la precisión de la coincidencia y la puntuación obtenida.



---
## Características Principales 🚀

* **Interfaz Web Intuitiva:** Formulario de subida de archivos construido con Thymeleaf y CSS.
* **Procesamiento de Archivos Excel:** Lectura de datos de archivos `.xlsx` utilizando la librería Apache POI.
* **Detección en Memoria:** Toda la lógica se ejecuta en memoria, sin necesidad de una base de datos.
* **Lógica de Puntuación Personalizable:** El `ContactService` implementa un sistema de puntuación para determinar la probabilidad de duplicado:
    * **Precisión Alta:** Coincidencia exacta de correo electrónico (90 puntos).
    * **Precisión Baja:** Coincidencia de apellido y código postal (50 puntos).
* **Código Estructurado y Mantenible:** Sigue el patrón Modelo-Vista-Controlador (MVC) para una clara separación de responsabilidades.
* **Cobertura de Pruebas:** Incluye un conjunto de pruebas unitarias (JUnit 5) para la lógica de negocio y pruebas de integración para la capa web.

---
## Tecnologías Utilizadas 🛠️

* **Backend:** Java 21, Spring Boot 3.x, Spring Web, Spring MVC
* **Frontend:** Thymeleaf, HTML5, CSS3
* **Build Tool:** Apache Maven
* **Testing:** JUnit 5, Mockito, Spring Boot Test
* **Librerías:** Apache POI, Lombok

---
## Requisitos Previos

Para compilar y ejecutar el proyecto, necesitas tener instalado:

* **JDK 21** o superior.
* **Apache Maven 3.8** o superior.

---
## Cómo Empezar 🏁

Sigue estos pasos para ejecutar la aplicación en tu entorno local.

1.  **Clona el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/duplicate-finder.git](https://github.com/tu-usuario/duplicate-finder.git)
    cd duplicate-finder
    ```

2.  **Compila el proyecto:**
    Usa Maven para compilar el proyecto y descargar todas las dependencias.
    ```bash
    mvn clean install
    ```

3.  **Ejecuta la aplicación:**
    Puedes ejecutar la aplicación usando el plugin de Spring Boot para Maven.
    ```bash
    mvn spring-boot:run
    ```
    El servidor se iniciará en el puerto `8080`.

4.  **Accede a la aplicación:**
    Abre tu navegador web y navega a **http://localhost:8080**.

5.  **Usa la aplicación:**
    Selecciona tu archivo `.xlsx` y haz clic en "Procesar" para ver los resultados.

---
## Ejecutar las Pruebas 🧪

El proyecto incluye un conjunto de pruebas para garantizar la calidad y el correcto funcionamiento de la lógica. Para ejecutarlas, utiliza el siguiente comando de Maven:

```bash
mvn test