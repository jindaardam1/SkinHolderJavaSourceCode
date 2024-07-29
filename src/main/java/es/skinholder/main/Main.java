package es.skinholder.main;

import org.apache.commons.lang3.StringUtils;
import es.skinholder.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;


/**
 * @author jagobainda
 *
 */
public class Main {
    public static void main(String[] args) {
        try {
            crearCarpetasNecesarias();
            boolean fin = false;
            Scanner scanner = new Scanner(in);

            while (!fin) {

                int op = 0;
                while (op < 1 || op > 9) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    pinta();
                    try {
                        op = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        Logs.errorLogManager(e);
                        scanner.nextLine();
                    }
                }

                switch (op) {
                    case 1 -> {
                        if (hayItems()) {
                            Registros.nuevoRegistro();
                        } else {
                            out.println("\u001B[34mNo hay items registrados\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 2 -> Registros.anadirItem();
                    case 3 -> {
                        if (hayItems()) {
                            Registros.modificarEliminarItem();
                        } else {
                            out.println("\u001B[34mNo hay items registrados\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 4 -> {
                        if (hayRegistros()) {
                            Consultas.mostarHistorialPreciosItems();
                        } else {
                            out.println("\u001B[34mNo hay registros\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 5 -> {
                        if (hayRegistros()) {
                            Consultas.mostarHistorialPreciosTotales();
                        } else {
                            out.println("\u001B[34mNo hay registros\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 6 -> {
                        if (hayRegistros()) {
                            Consultas.mostrarRegistroConcreto();
                        } else {
                            out.println("\u001B[34mNo hay registros\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 7 -> {
                        if (hayItems()) {
                            mostrarItems();
                        } else {
                            out.println("\u001B[34mNo hay items registrados\u001B[0m");
                            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
                        }
                    }
                    case 8 -> fin = true;
                    default -> ConversionDivisas.menuDivisas();
                }

            }
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }
    public static void mostrarItems() {
        try {
            Items items = new Items();
            items.mostrarItems();
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }

    /**
     * Imprime por pantalla el menú principal y la cabecera del programa.
     */
    private static void pinta() {
        out.println("\u001B[33m" + "*".repeat(90));
        out.println("*" + String.format("%98s", "\u001B[32mv1.2.4\u001B[33m ") + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center("  \u001B[34m______   __    __\u001B[0m      \u001B[32m__    __  _______\u001B[0m  \u001B[0m", 110) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center(" \u001B[34m/      \\ |  \\  /  \\\u001B[0m    \u001B[32m|  \\  |  \\|       \\ \u001B[0m", 106) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center("\u001B[34m|  $$$$$$\\| $$ /  $$\u001B[0m \u001B[33m__\u001B[0m \u001B[32m| $$  | $$| $$$$$$$\\\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center("\u001B[34m| $$___\\$$| $$/  $$\u001B[0m \u001B[33m|  \\\u001B[0m\u001B[32m| $$__| $$| $$  | $$\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center(" \u001B[34m\\$$    \\ | $$  $$\u001B[0m   \u001B[33m\\$$\u001B[0m\u001B[32m| $$    $$| $$  | $$\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center(" \u001B[34m_\\$$$$$$\\| $$$$$\\\u001B[0m   \u001B[33m__ \u001B[0m\u001B[32m| $$$$$$$$| $$  | $$\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center("\u001B[34m|  \\__| $$| $$ \\$$\\\u001B[0m \u001B[33m|  \\\u001B[0m\u001B[32m| $$  | $$| $$__/ $$\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center(" \u001B[34m\\$$    $$| $$  \\$$\\\u001B[0m \u001B[33m\\$$\u001B[0m\u001B[32m| $$  | $$| $$    $$\u001B[0m", 115) + "\u001B[33m" + "*");
        out.println("*" + "\u001B[0m" + StringUtils.center("  \u001B[34m\\$$$$$$  \\$$   \\$$\u001B[0m     \u001B[32m\\$$   \\$$ \\$$$$$$$ \u001B[0m", 106) + "\u001B[33m" + "*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*" + String.format("%98s", "by \u001B[32mjagobainda\u001B[33m ") + "*");
        out.println("*".repeat(90) + "\n\u001B[0m");

        out.println("\u001B[33m" + "*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[32m1 - Registrar nueva entrada\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[32m2 - Añadir nuevo item\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[32m3 - Modificar/eliminar item\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90) + "\n");

        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[34m4 - Mostrar historial de precios de item\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[34m5 - Mostrar historial de precios totales\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[34m6 - Mostrar registro\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[34m7 - Mostrar items\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90) + "\n");

        out.println("*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
        out.println("*\u001B[0m" + StringUtils.center("\u001B[31m8 - Salir\u001B[0m", 97) + "\u001B[33m*");
        out.println("*" + " ".repeat(88) + "*");
        out.println("*".repeat(90) + "\n");

        out.println("*".repeat(90));
        out.println("*" + " ".repeat(35) + "*" + " ".repeat(52) + "*");
        conexionYCDivisas();
        out.println("*" + " ".repeat(35) + "*" + " ".repeat(52) + "*");
        out.println("*".repeat(90) + "\u001B[0m");
    }

    private static void conexionYCDivisas() {
        int ping = Conexiones.hayConexion("www.xe.com");

        out.println("*" + StringUtils.center(Conexiones.calidadCon(ping), 45) + "*" + StringUtils.center("\u001B[34m9 - Conversión de divisas\u001B[33m", 62) + "*");
        out.println("*" + " ".repeat(35) + "*" + " ".repeat(52) + "*");
        out.println("*" + StringUtils.center(Conexiones.calidadPing(ping) + "Ping: " + ping + "\u001B[33m", 45)
                + "*" + StringUtils.center("\u001B[32m1 EURO = " + ConversionDivisas.codigoACambio(Conexiones.getCodigoFuente("https://www.xe.com/es/currencyconverter/convert/?Amount=1&From=EUR&To=CNY")) + " YUANES\u001B[33m", 62) + "*");
        out.println("*" + " ".repeat(35) + "*" + " ".repeat(52) + "*");
        out.println("*" + StringUtils.center("\u001B[34mHost: www.xe.com\u001B[33m", 45)
                + "*" + StringUtils.center("\u001B[31m1 YUAN = " + ConversionDivisas.codigoACambio(Conexiones.getCodigoFuente("https://www.xe.com/es/currencyconverter/convert/?Amount=1&From=CNY&To=EUR")) + " EUROS\u001B[33m", 62) + "*");
    }

    private static void crearCarpetasNecesarias() {
        File carpetaItems = new File("Items");
        if (!carpetaItems.exists()) {
            if (carpetaItems.mkdir()) {
                Logs.infoLogManager("Se ha creado la carpeta Items");
            }
        }
        File carpetaRegistros = new File("Registros");
        if (!carpetaRegistros.exists()) {
            if (carpetaRegistros.mkdir()) {
                Logs.infoLogManager("Se ha creado la carpeta Registros");
            }
        }
    }

    private static boolean hayItems() {
        File carpetaItems = new File("Items");
        return Objects.requireNonNull(carpetaItems.listFiles()).length > 0;
    }

    private static boolean hayRegistros() {
        File carpetaRegistros = new File("Registros");
        return Objects.requireNonNull(carpetaRegistros.listFiles()).length > 0;
    }
}
