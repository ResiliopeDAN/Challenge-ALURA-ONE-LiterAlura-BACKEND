package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    -----------
                    Elija la opción a través de su número:
                    1- Buscar libro por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    6- Top 10 libros más descargados
                    7- Buscar autor por nombre
                    8- Estadísticas de descargas
                    0- Salir
                    -----------
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } catch (Exception e) {
                System.out.println("Opción inválida. Por favor, ingrese un número.");
                teclado.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    listarTop10Libros();
                    break;
                case 7:
                    buscarAutorPorNombre();
                    break;
                case 8:
                    mostrarEstadisticas();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void listarTop10Libros() {
        System.out.println("\n--- Top 10 libros más descargados ---");
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        topLibros.forEach(System.out::println);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor que desea buscar:");
        var nombreAutor = teclado.nextLine();
        List<Autor> autores = autorRepository.findByNombreContainingIgnoreCase(nombreAutor);
        if (autores.isEmpty()) {
            System.out.println("No se encontró ningún autor con ese nombre.");
        } else {
            autores.forEach(a -> {
                System.out.println("\nAutor: " + a.getNombre());
                System.out.println("Fecha de nacimiento: " + a.getFechaDeNacimiento());
                System.out.println("Fecha de fallecimiento: " + (a.getFechaDeFallecimiento() != null ? a.getFechaDeFallecimiento() : "N/A"));
                System.out.print("Libros: [");
                String librosStr = a.getLibros().stream()
                        .map(Libro::getTitulo)
                        .reduce((t1, t2) -> t1 + ", " + t2)
                        .orElse("");
                System.out.println(librosStr + "]");
            });
        }
    }

    private void mostrarEstadisticas() {
        System.out.println("\n--- Estadísticas de descargas ---");
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados para calcular estadísticas.");
        } else {
            java.util.DoubleSummaryStatistics est = libros.stream()
                    .mapToDouble(Libro::getNumeroDeDescargas)
                    .summaryStatistics();
            System.out.println("Cantidad de libros: " + est.getCount());
            System.out.println("Promedio de descargas: " + est.getAverage());
            System.out.println("Máximo de descargas: " + est.getMax());
            System.out.println("Mínimo de descargas: " + est.getMin());
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));
        var datos = conversor.obtenerDatos(json, Datos.class);

        if (datos.resultados().isEmpty()) {
            System.out.println("Libro no encontrado");
        } else {
            DatosLibro datosLibro = datos.resultados().get(0);
            
            // Verificar si el libro ya está registrado
            Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(datosLibro.titulo());
            if (libroExistente.isPresent()) {
                System.out.println("No se puede registrar el mismo libro más de una vez");
                return;
            }

            Libro libro = new Libro(datosLibro);
            
            // Manejo del autor
            if (!datosLibro.autores().isEmpty()) {
                DatosAutor datosAutor = datosLibro.autores().get(0);
                Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(datosAutor.nombre());
                
                Autor autor;
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();
                } else {
                    autor = new Autor(datosAutor);
                    autor = autorRepository.save(autor);
                }
                
                libro.setAutor(autor);
            }

            libroRepository.save(libro);
            System.out.println(libro);
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(a -> {
                        System.out.println("\nAutor: " + a.getNombre());
                        System.out.println("Fecha de nacimiento: " + a.getFechaDeNacimiento());
                        System.out.println("Fecha de fallecimiento: " + (a.getFechaDeFallecimiento() != null ? a.getFechaDeFallecimiento() : "N/A"));
                        System.out.print("Libros: [");
                        String librosStr = a.getLibros().stream()
                                .map(Libro::getTitulo)
                                .reduce((t1, t2) -> t1 + ", " + t2)
                                .orElse("");
                        System.out.println(librosStr + "]");
                    });
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar");
        try {
            int anio = teclado.nextInt();
            teclado.nextLine();
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnDeterminadoAnio(anio);
            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
            } else {
                autoresVivos.forEach(a -> {
                    System.out.println("\nAutor: " + a.getNombre());
                    System.out.println("Fecha de nacimiento: " + a.getFechaDeNacimiento());
                    System.out.println("Fecha de fallecimiento: " + (a.getFechaDeFallecimiento() != null ? a.getFechaDeFallecimiento() : "N/A"));
                    System.out.print("Libros: [");
                    String librosStr = a.getLibros().stream()
                            .map(Libro::getTitulo)
                            .reduce((t1, t2) -> t1 + ", " + t2)
                            .orElse("");
                    System.out.println(librosStr + "]");
                });
            }
        } catch (Exception e) {
            System.out.println("Año inválido. Por favor, ingrese un número.");
            teclado.nextLine();
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es- español
                en- inglés
                fr- francés
                pt- portugués
                """);
        var idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma registrados.");
        } else {
            librosPorIdioma.forEach(System.out::println);
        }
    }
}
