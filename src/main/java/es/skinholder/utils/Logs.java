package es.skinholder.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Clase encargada de general los logs del programa.
 */
public class Logs {
    /**
     * Registra un error en el registro de errores.
     *
     * @param e La excepción que se registrará en el log.
     */
    public static void errorLogManager(Exception e) {
        addLog(crearStringLog("ERROR", Arrays.toString(e.getStackTrace())));
    }

    /**
     * Registra un error personalizado en el registro de errores.
     *
     * @param errorPersonalizado El mensaje de error personalizado a registrar en el log.
     */
    public static void errorLogManager(String errorPersonalizado) {
        addLog(crearStringLog("ERROR", errorPersonalizado));
    }

    /**
     * Registra un mensaje de depuración en el registro de eventos.
     *
     * @param mensaje El mensaje de depuración a registrar en el log.
     */
    public static void debugLogManager(String mensaje) {
        addLog(crearStringLog("DEBUG", mensaje));
    }

    /**
     * Registra un mensaje de información en el registro de eventos.
     *
     * @param mensaje El mensaje de información a registrar en el log.
     */
    public static void infoLogManager(String mensaje) {
        addLog(crearStringLog("INFO", mensaje));
    }


    /**
     * Crea una cadena de registro formateada.
     *
     * @param tipoLog El tipo de registro (por ejemplo, "INFO", "ERROR", "DEBUG").
     * @param mensajeLog El mensaje de registro.
     * @return La cadena de registro formateada.
     */
    private static String crearStringLog(String tipoLog, String mensajeLog) {
        String logFormat = "%s [%s] -> %s";
        return String.format(logFormat, getFechaHoraActual(), tipoLog, mensajeLog);
    }

    /**
     * Agrega un registro de log al archivo de registro del mes actual.
     *
     * @param nuevoLog El registro de log a agregar.
     */
    private static void addLog(String nuevoLog) {
        File archivoLog = cargarLogMesActual();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoLog, true))) {
            bw.write(nuevoLog);
            bw.newLine();
            //System.out.println("Log agregado al archivo '" + archivoLog.getName() + "'.");
        } catch (IOException e) {
            //System.out.println("Error al agregar el log al archivo: " + e.getMessage());
        }
    }

    /**
     * Carga el archivo de registro correspondiente al mes actual.
     * @return El archivo de registro del mes actual.
     */
    private static File cargarLogMesActual() {
        File carpetaLogs = cargarCarpetaLogs();
        LocalDate fechaActual = LocalDate.now();
        String nombreArchivo = fechaActual.format(DateTimeFormatter.ofPattern("yyyy-MM")) + ".log";
        File archivoLog = new File(carpetaLogs, nombreArchivo);

        try {
            if (archivoLog.createNewFile()) {
                //System.out.println("Archivo '" + nombreArchivo + "' creado exitosamente.");
            } else {
                //System.out.println("El archivo '" + nombreArchivo + "' ya existe.");
            }
        } catch (IOException e) {
            //System.out.println("No se ha podido cargar el archivo de log.");
        }

        return archivoLog;
    }

    /**
     * Carga la carpeta de registros (logs).
     * @return Objeto File que representa la carpeta de registros.
     */
    private static File cargarCarpetaLogs() {
        File carpetaLogs = new File("logs");

        if (!carpetaLogs.exists()) {
            if (carpetaLogs.mkdir()) {
                //System.out.println("Carpeta 'logs' creada exitosamente.");
            } else {
                //System.out.println("No se pudo crear la carpeta 'logs'.");
            }
        } else {
            //System.out.println("La carpeta 'logs' ya existe.");
        }

        return carpetaLogs;
    }

    /**
     * Devuelve la fecha y hora actual formateada como una cadena.
     *
     * @return La fecha y hora actual en el formato "yyyy-MM-dd HH:mm:ss".
     */
    private static String getFechaHoraActual() {
        // Obtiene la fecha y hora actual.
        LocalDateTime now = LocalDateTime.now();
        // Crea un objeto de formateo de fecha y hora.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Devuelve la fecha y hora formateada como una cadena.
        return now.format(formatter);
    }
}
