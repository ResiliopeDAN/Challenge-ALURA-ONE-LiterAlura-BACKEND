# LiterAlura - Catálogo de Libros 📚

LiterAlura es una aplicación de consola desarrollada en **Java con Spring Boot** que permite gestionar un catálogo de libros consumiendo datos de la API externa **Gutendex**. El proyecto forma parte del Challenge #2 del programa Oracle Next Education (Alura ONE).

## 🚀 Funcionalidades

### Obligatorias:
- **Buscar libro por título:** Consulta la API de Gutendex, registra el libro y su autor en la base de datos local (evitando duplicados).
- **Listar libros registrados:** Muestra todos los libros almacenados en la base de datos.
- **Listar autores registrados:** Muestra los autores y los libros asociados a cada uno.
- **Listar autores vivos en un año determinado:** Filtra autores por año de nacimiento y fallecimiento.
- **Listar libros por idioma:** Filtra los libros registrados según su código de idioma (es, en, fr, pt).

### Extras Implementadas:
- **Top 10 Libros:** Muestra los 10 libros más descargados de tu base de datos local.
- **Buscar Autor por Nombre:** Permite realizar búsquedas de autores específicos por fragmentos de su nombre.
- **Estadísticas de Descargas:** Genera un resumen estadístico (media, máximo, mínimo) de las descargas de todos los libros registrados usando Java Streams.

## 🛠️ Tecnologías Utilizadas
- **Lenguaje:** Java 17.
- **Framework:** Spring Boot 3.2.x.
- **Persistencia:** Spring Data JPA.
- **Bases de Datos:** H2 (Default/Portable) y PostgreSQL.
- **Mapeo JSON:** Jackson.
- **Gestor de Dependencias:** Maven.

## 💾 Configuración de Base de Datos (Modo Dual)

La aplicación está diseñada para ser **"Plug & Play"**. Por defecto, utiliza una base de datos local para que no tengas que configurar nada al descargarla.

### 1. Opción por Defecto: H2 Database (Portable)
**¡Zero Config!** Al iniciar la aplicación, se utilizará automáticamente una base de datos local alojada en la raíz del proyecto.
- Los datos se guardan en: `./data/literalura.db`.
- Ventaja: Puedes probar la aplicación de inmediato sin instalar PostgreSQL.
- Perfil activo por defecto: `h2`.

### 2. Opción Challenge: PostgreSQL
Si deseas utilizar PostgreSQL (requisito opcional para el challenge):
1. Asegúrate de tener PostgreSQL instalado.
2. Cambia la línea en `src/main/resources/application.properties` a:
   ```properties
   spring.profiles.active=postgres
   ```
3. Verifica tus credenciales en `application-postgres.properties`.

## ⚙️ Cómo Ejecutar
1. Clona el repositorio.
2. Asegúrate de tener instalado Java 17 y Maven.
3. Ejecuta el comando en la terminal:
   ```bash
   ./mvnw spring-boot:run
   ```

## 📝 Autor
Proyecto desarrollado como parte del Challenge LiterAlura - Alura ONE.
- **Desarrollado por:** [Dario Daniel Quispe Quispe]
- **Mentoría:** Alura Latam / Oracle Next Education.
