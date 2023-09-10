package es.skinholder.utils;

import es.skinholder.records.ParDivisas;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;

import static java.lang.System.out;

public class ConversionDivisas {

    private static final String BASE_URL = "https://www.xe.com/es/currencyconverter/convert/?Amount=1&From=%s&To=%s";

    public static double codigoACambio(String codigo) {
        Document document = Jsoup.parse(codigo);
        Element pElement = document.selectFirst("p.result__BigRate-sc-1bsijpp-1.iGrAod");

        if (pElement != null) {
            String text = pElement.text();
            String doubleValue = text.replaceAll("[^\\d.,]", "").replace(',', '.');

            try {
                return Double.parseDouble(doubleValue);
            } catch (NumberFormatException e) {
                System.out.println("No se pudo convertir a double.");
            }
        } else {
            System.out.println("No se encontró el elemento <p> con la clase proporcionada.");
        }
        return -1;
    }

    public static void menuDivisas() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            ArrayList<Double> tasas = calcularTasas(conseguirEnlaces());

            ArrayList<ParDivisas> divisas = obtenerDivisas();

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

    private static ArrayList<Double> calcularTasas(ArrayList<String> enlaces) {
        ArrayList<Double> cambios = new ArrayList<>();

        for (String enlace : enlaces) {
            cambios.add(obtenerCambio(enlace));
        }

        return cambios;
    }

    private static double obtenerCambio(String enlace) {
        return codigoACambio(Conexiones.getCodigoFuente(enlace));
    }

    private static String construirEnlace(String monedaOrigen, String monedaDestino) {
        return String.format(BASE_URL, monedaOrigen, monedaDestino);
    }

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
