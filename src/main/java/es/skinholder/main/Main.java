package es.skinholder.main;

import es.skinholder.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
            boolean fin = false;
            while (!fin) {
                Scanner scanner = new Scanner(in);
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                pinta();
                int op = scanner.nextInt();
                switch (op) {
                    case 1 -> Registros.nuevoRegistro();
                    case 2 -> Registros.anadirItem();
                    case 3 -> Registros.modificarEliminarItem();
                    case 4 -> Consultas.mostarHistorialPreciosItems();
                    case 5 -> Consultas.mostarHistorialPreciosTotales();
                    case 6 -> Consultas.mostrarRegistroConcreto();
                    case 7 -> mostrarItems();
                    case 8 -> fin = true;
                    case 11 -> OpcionesDesarrollador.mostrarLogs(); // Opción oculta para el usuario
                    default -> out.println("Introduce una opción válida");
                }
            }
        } catch (IOException | InterruptedException e) {
            GeneradorLogs.errorLogManager(e);
        }
    }
    public static void mostrarItems() {
        try {
            Items items = new Items();
            items.mostrarItems();
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        } catch (Exception e) {
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Imprime por pantalla el menú principal y la cabecera del programa.
     */
    private static void pinta() {
        out.println("\u001B[33m" + "*".repeat(90));
        out.println("*" + " ".repeat(88) + "*");
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
        out.println("*".repeat(90) + "\u001B[0m");
    }
}
