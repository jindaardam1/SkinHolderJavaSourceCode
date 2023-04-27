package es.skinholder.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpcionesDesarrollador {
    /**
     * Muestra los logs en la consola del sistema operativo.
     */
    public static void mostrarLogs() {
        // Muestra mensaje de bienvenida
        bienvenidaLogs();

        // Formatea los logs para mostrarlos correctamente
        formatearLogs();

        try {
            // Crea un nuevo proceso con el comando "cmd /c pause" para detener la consola
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "pause");
            pb.inheritIO();
            Process p = pb.start();

            // Espera a que el proceso termine para continuar
            p.waitFor();
        } catch (InterruptedException | IOException e) {
            // Registra cualquier excepción en el archivo de error de logs
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Método que muestra un mensaje de bienvenida y los logs disponibles en la consola de comandos.
     * Este método no recibe parámetros y no devuelve ningún valor.
     * Utiliza la librería StringUtils para centrar los mensajes en la pantalla.
     * Si ocurre un error al ejecutar alguno de los comandos, se registra en el archivo de logs de errores.
     */
    private static void bienvenidaLogs() {
        try {
            // Ejecuta el comando para borrar la pantalla de la consola
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            // Registra el error en el archivo de logs de errores
            GeneradorLogs.errorLogManager(e);
        }
        // Muestra el mensaje de bienvenida en la consola
        System.out.println("\u001B[34m" + "*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32mBIENVENIDO A LAS OPCIONES DE DESARROLLADOR\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32mA CONTINUACIÓN SE MOSTRARÁN LOS \u001B[31mLOGS\u001B[32m DISPONIBLES\u001B[0m", 107) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90) + "\u001B[0m");
        try {
            // Espera a que el usuario presione una tecla antes de continuar
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            // Registra el error en el archivo de logs de errores
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Lee el archivo "logs.log" y formatea cada una de sus líneas.
     * Se utiliza un bloque try-with-resources para garantizar que el recurso se cierra correctamente al finalizar su uso.
     * Si se produce una excepción al intentar leer el archivo, se registra un error en el archivo de logs de errores.
     */
    private static void formatearLogs() {
        try (BufferedReader br = new BufferedReader(new FileReader("logs.log"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                formatearLinea(linea);
            }
        } catch (IOException e) {
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Formatea una línea de log en un formato legible para el usuario.
     * La línea debe tener el siguiente formato:
     * YYYY-MM-DD HH:mm:ss [NIVEL_LOG]  -> MENSAJE
     *
     * @param linea La línea de log a formatear.
     */
    private static void formatearLinea(String linea) {
        // Compilar el patrón para buscar los elementos de la línea de log.
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) \\[(INFO|DEBUG|ERROR)] {2}-> (.*)");
        Matcher matcher = pattern.matcher(linea);
        if (matcher.find()) {
            // Obtener la fecha y hora, el nivel de log y el mensaje.
            String fechaHora = matcher.group(1);
            String nivelLog = matcher.group(2);
            String mensaje = matcher.group(3);

            // Definir los colores para la fecha y hora y el nivel de log.
            String colorFechaHora = "\u001B[34m";
            String colorNivelLog = switch (nivelLog) {
                case "INFO" -> "\u001B[32m";
                case "DEBUG" -> "\u001B[33m";
                case "ERROR" -> "\u001B[31m";
                default -> "";
            };

            // Definir los formatos para la fecha y hora y el nivel de log.
            String formatoFechaHora = "%s%s\u001B[0m";
            String formatoNivelLog = "%s[%s]\u001B[0m";

            // Formatear la línea de log con los colores y formatos correspondientes.
            String salida = String.format(formatoFechaHora + " " + formatoNivelLog + " -> %s", colorFechaHora, fechaHora, colorNivelLog, nivelLog, mensaje);
            System.out.println(salida);
        }
    }
}
