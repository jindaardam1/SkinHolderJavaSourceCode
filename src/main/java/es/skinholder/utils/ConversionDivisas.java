package es.skinholder.utils;

import es.skinholder.records.ParDivisas;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jagobainda9
 *
 * Esta clase proporciona métodos para realizar conversiones de divisas utilizando la página web www.xe.com.
 */
public class ConversionDivisas {

    /**
     * URL base utilizada para construir los enlaces de conversión de divisas.
     */
    private static final String BASE_URL = "https://www.xe.com/es/currencyconverter/convert/?Amount=1&From=%s&To=%s";

    /**
     * Convierte un código HTML en una tasa de cambio en formato double.
     *
     * @param codigo El código HTML que contiene la tasa de cambio.
     * @return La tasa de cambio en formato double. Si no se puede obtener, devuelve -1.
     */
    public static double codigoACambio(String codigo) {
        // Parsear el código HTML utilizando Jsoup
        Document document = Jsoup.parse(codigo);
        Element pElement = document.selectFirst("p.sc-295edd9f-1.jqMUXt");

        if (pElement != null) {
            String text = pElement.text();
            // Eliminar caracteres no numéricos y reemplazar comas por puntos
            String doubleValue = text.replaceAll("[^\\d.,]", "").replace(',', '.');

            try {
                // Intentar convertir la cadena en un double
                return Double.parseDouble(doubleValue);
            } catch (NumberFormatException e) {
                Logs.errorLogManager(e);
            }
        } else {
            Logs.errorLogManager("No se encontró el elemento <p> con la clase proporcionada.");
        }
        return -1;
    }

    /**
     * Muestra un menú de conversiones de divisas en la consola.
     */
    public static void menuDivisas() {
        try {
            // Limpiar la consola
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Calcular tasas y obtener pares de divisas
            ArrayList<Double> tasas = calcularTasas(conseguirEnlaces());
            ArrayList<ParDivisas> divisas = obtenerDivisas();

            // Mostrar tasas y pares de divisas
            for (int i = 0; i < tasas.size(); i += 2) {
                System.out.println("\u001B[34m" + "*".repeat(90));
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32m1 " + divisas.get(i).ENTRADA()
                        + " = " + tasas.get(i) + " " + divisas.get(i).SALIDA() + "\u001B[0m", 97) + "\u001B[34m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("\u001B[31m1 " + divisas.get(i + 1).ENTRADA()
                        + " = " + tasas.get(i + 1) + " " + divisas.get(i + 1).SALIDA() + "\u001B[0m", 97) + "\u001B[34m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("\u001B[34m" + "*".repeat(90) + "\n");
            }

            // Mostrar fuente y pausar la consola
            System.out.println("\u001B[34m" + "*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[33m" + StringUtils.center("Fuente: www.xe.com", 88) + "\u001B[34m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("\u001B[34m" + "*".repeat(90) + "\u001B[0m");

            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }

    /**
     * Obtiene una lista de enlaces de conversión de divisas.
     *
     * @return Una lista de enlaces URL.
     */
    public static ArrayList<String> conseguirEnlaces() {
        ArrayList<String> enlaces = new ArrayList<>();

        enlaces.add(construirEnlace("EUR", "CNY"));
        enlaces.add(construirEnlace("CNY", "EUR"));
        enlaces.add(construirEnlace("USD", "CNY"));
        enlaces.add(construirEnlace("CNY", "USD"));
        enlaces.add(construirEnlace("EUR", "USD"));
        enlaces.add(construirEnlace("USD", "EUR"));
        enlaces.add(construirEnlace("EUR", "BRL"));
        enlaces.add(construirEnlace("BRL", "EUR"));
        enlaces.add(construirEnlace("EUR", "RUB"));
        enlaces.add(construirEnlace("RUB", "EUR"));

        return enlaces;
    }

    /**
     * Calcula las tasas de cambio para una lista de enlaces de conversión de divisas.
     *
     * @param enlaces Una lista de enlaces URL.
     * @return Una lista de tasas de cambio en formato double.
     */
    private static ArrayList<Double> calcularTasas(ArrayList<String> enlaces) {
        ArrayList<Double> cambios = new ArrayList<>();

        for (String enlace : enlaces) {
            cambios.add(obtenerCambio(enlace));
        }

        return cambios;
    }

    /**
     * Obtiene una tasa de cambio a partir de un enlace de conversión de divisas.
     *
     * @param enlace El enlace URL de conversión de divisas.
     * @return La tasa de cambio en formato double.
     */
    private static double obtenerCambio(String enlace) {
        return codigoACambio(Conexiones.getCodigoFuente(enlace));
    }

    /**
     * Construye un enlace de conversión de divisas con las monedas de origen y destino.
     *
     * @param monedaOrigen La moneda de origen.
     * @param monedaDestino La moneda de destino.
     * @return El enlace URL de conversión de divisas.
     */
    private static String construirEnlace(String monedaOrigen, String monedaDestino) {
        return String.format(BASE_URL, monedaOrigen, monedaDestino);
    }

    /**
     * Obtiene una lista de pares de divisas.
     *
     * @return Una lista de pares de divisas.
     */
    private static ArrayList<ParDivisas> obtenerDivisas() {
        ArrayList<ParDivisas> divisas = new ArrayList<>();

        divisas.add(new ParDivisas("EURO", "YUANES"));
        divisas.add(new ParDivisas("YUAN", "EUROS"));
        divisas.add(new ParDivisas("DÓLAR", "YUANES"));
        divisas.add(new ParDivisas("YUAN", "DÓLARES"));
        divisas.add(new ParDivisas("EURO", "DÓLARES"));
        divisas.add(new ParDivisas("DÓLAR", "EUROS"));
        divisas.add(new ParDivisas("EURO", "LIRAS BRASILEÑAS"));
        divisas.add(new ParDivisas("LIRA BRASILEÑA", "EUROS"));
        divisas.add(new ParDivisas("EURO", "RUBLOS"));
        divisas.add(new ParDivisas("RUBLO", "EUROS"));

        return divisas;
    }
}
