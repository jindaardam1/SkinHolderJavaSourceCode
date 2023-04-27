package es.skinholder.utils;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author jagobainda
 *
 */
public class Consultas {
    /**
     * Este método muestra el historial de precios de un item seleccionado por el usuario.
     * Se muestran los precios almacenados en archivos de registro en la carpeta "Registros".
     *
     */
    public static void mostarHistorialPreciosItems() {
        try {
            // Limpia la pantalla de la consola
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            // Accede a la carpeta "Registros"
            File carpeta = new File("Registros");
            // Obtiene la lista de archivos de texto en la carpeta "Registros"
            File[] fileList = carpeta.listFiles();
            // Convierte la lista de archivos en un ArrayList
            assert fileList != null;
            ArrayList<File> archivosTxt = new ArrayList<>(Arrays.asList(fileList));
            // Crea un TreeMap para almacenar los registros de precios por fecha, ordenados de manera descendente por fecha.
            TreeMap<String, TreeMap<String, Float>> registros = new TreeMap<>(Comparator.reverseOrder());
            // Itera sobre la lista de archivos y agrega los registros al TreeMap "registros"
            for (File archivo : archivosTxt) {
                registros.put(archivo.getName().replace(".xml", ""), registrosAHashMapPreciosItem(archivo));
            }
            // Crea un objeto Scanner para obtener la entrada del usuario
            Scanner scanner = new Scanner(System.in);
            // Crea un objeto Items para obtener la lista de items disponibles
            Items items = new Items();
            // Obtiene el conjunto de items
            Set<String> itemsSet = items.items.keySet();
            // Crea un TreeSet para ordenar los items alfabéticamente
            TreeSet<String> itemsSetOrdenado = new TreeSet<>(itemsSet);
            int contador = 1;
            // Muestra la lista de items disponibles
            items.mostrarItems();
            System.out.println("\u001B[32m*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("¿Qué item quieres ver?", 88) + "\u001B[32m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90) + "\u001B[0m");
            // Obtiene la selección del usuario
            int itemSeleccionado = scanner.nextInt();
            String itemABuscar = null;
            // Itera sobre el conjunto de items y encuentra el item seleccionado por el usuario
            for (String s : itemsSetOrdenado) {
                if (contador == itemSeleccionado) {
                    itemABuscar = s;
                }
                contador++;
            }
            // Pide al usuario que introduzca la cantidad de registros que desea ver
            System.out.println("\u001B[32m*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("Introduce cuántos de los últimos registros quieres ver:", 88) + "\u001B[32m*");
            System.out.println("*\u001B[0m" + StringUtils.center("\u001B[34m(Máximo de registros disponibles: " + registros.size() + ")\u001B[0m", 97) + "\u001B[32m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90) + "\u001B[0m");
            int cantidad = scanner.nextInt();
            // Limita la cantidad de registros a la cantidad disponible si el usuario introduce un número mayor
            if (cantidad > registros.size()) { cantidad = registros.size(); }
            // Itera sobre el TreeMap "registros" y muestra los registros correspondientes al item seleccionado por el usuario
            int iteraciones = 0;
            System.out.println("\u001B[33m*".repeat(90));
            for (Map.Entry<String, TreeMap<String, Float>> entry : registros.entrySet()) {
                if (iteraciones >= cantidad) {
                    break;
                }
                TreeMap<String, Float> treeMap = entry.getValue();
                if (treeMap.containsKey(itemABuscar)) {
                    System.out.println("\u001B[33m*" + " ".repeat(88) + "*");
                    System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32m" + entry.getKey().replace("_", ":")
                            + "\u001B[0m  \u001B[31m->\u001B[0m  \u001B[34m" + treeMap.get(itemABuscar) + "€\u001B[0m", 115) + "\u001B[33m*");
                    System.out.println("*" + " ".repeat(88) + "*");
                    System.out.println("*".repeat(90) + "\u001B[0m");
                }
                iteraciones++;
            }
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        }
    }


    /**
     * Convierte un archivo XML que contiene información sobre los precios de ciertos elementos
     * en un TreeMap que asocia el nombre de cada elemento con su precio correspondiente.
     * @param archivoXML el archivo XML que se desea convertir.
     * @return un TreeMap que asocia el nombre de cada elemento con su precio correspondiente.
     */
    private static TreeMap<String, Float> registrosAHashMapPreciosItem(File archivoXML) {
        // Se crea un TreeMap para almacenar los nombres de los elementos y sus precios correspondientes
        TreeMap<String, Float> items = new TreeMap<>();

        try {
            // Se crea una instancia de DocumentBuilderFactory para crear una instancia de DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Se parsea el archivo XML para crear un objeto Document
            Document doc = factory.newDocumentBuilder().parse(archivoXML);

            // Se crea una instancia de XPathFactory para crear una instancia de XPath
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Se compila una expresión XPath que selecciona todos los nodos "item" del documento
            XPathExpression expr = xpath.compile("//item");
            // Se ejecuta la expresión XPath sobre el documento para obtener una lista de nodos "item"
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            // Se recorre la lista de nodos "item" y se agrega cada elemento y su precio al TreeMap
            for (int i = 0; i < nodeList.getLength(); i++) {
                // Se obtiene el nombre del elemento del nodo "nombre" usando XPath
                String nombre = xpath.evaluate("nombre", nodeList.item(i));
                // Se obtiene el precio del elemento del nodo "precio" y se convierte a float usando Float.parseFloat()
                float precio = Float.parseFloat(xpath.evaluate("precio", nodeList.item(i)));
                // Se agrega el elemento y su precio correspondiente al TreeMap
                items.put(nombre, precio);
            }


        }  catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        }
        return items;
    }

    /**
     * Muestra el historial de precios totales de todos los registros guardados en la carpeta "Registros".
     * Cada registro se presenta en orden descendente por su fecha de creación y se muestra el precio total
     * y el precio total sin la comisión de Steam.
     *
     */
    public static void mostarHistorialPreciosTotales() {
        try {
            // Limpia la consola.
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Obtiene la carpeta "Registros".
            File carpeta = new File("Registros");

            // Obtiene la lista de archivos de texto en la carpeta "Registros".
            File[] fileList = carpeta.listFiles();

            // Convierte la lista de archivos en un ArrayList.
            assert fileList != null;
            ArrayList<File> archivosTxt = new ArrayList<>(Arrays.asList(fileList));

            // Crea un TreeMap para almacenar los registros ordenados por su fecha de creación.
            TreeMap<String, Float> registros = new TreeMap<>(Comparator.reverseOrder());

            // Obtiene el precio total de cada registro y lo agrega al TreeMap.
            for (File file : archivosTxt) {
                registros.put(file.getName().replace(".xml", "").replace("_", ":"), registrosAFloatPreciosTotales(file));
            }

            // Obtiene la cantidad de registros que se mostrarán.
            Scanner scanner = new Scanner(System.in);
            System.out.println("\u001B[31m*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("Introduce cuántos de los últimos registros quieres ver: ", 88) + "\u001B[31m*");
            System.out.println("*\u001B[0m" + StringUtils.center("\u001B[34m(Máximo de registros disponibles: " + registros.size() + ")\u001B[0m", 97) + "\u001B[31m*");
            System.out.println("*" + " ".repeat(88) + "* ");
            System.out.println("*".repeat(90) + "\u001B[0m");
            int cantidad = scanner.nextInt();
            if (cantidad > registros.size()) { cantidad = registros.size(); }

            // Muestra los registros en orden descendente por su fecha de creación.
            int iteraciones = 0;
            System.out.println("\u001B[33m*".repeat(90));
            for (Map.Entry<String, Float> entry : registros.entrySet()) {
                if (iteraciones >= cantidad) {
                    break;
                }

                System.out.println("\u001B[33m*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32m" + entry.getKey().replace(".txt", "").replace("_", ":") +
                        "\u001B[0m \u001B[31m->\u001B[0m Total: \u001B[34m" + entry.getValue() +
                        "€\u001B[0m \u001B[31m->\u001B[0m Total sin la comisión de Steam: \u001B[34m" +
                        String.format("%.2f", (entry.getValue() * 0.8333)) + "€\u001B[0m", 133) + "\u001B[33m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*".repeat(90) + "\u001B[0m");

                iteraciones++;
            }

            // Espera a que el usuario presione una tecla para continuar.
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Convierte el valor del atributo "total" del primer elemento "registro" del archivo XML
     * especificado a un valor de tipo float y lo retorna.
     *
     * @param archivoXML el archivo XML del cual se desea obtener el valor del atributo "total".
     * @return el valor del atributo "total" del primer elemento "registro" del archivo XML como float.
     */
    private static float registrosAFloatPreciosTotales(File archivoXML) {
        String totalString = "";
        try {
            // Crea un objeto DocumentBuilderFactory y un DocumentBuilder para procesar el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Carga el archivo XML en un objeto Document
            Document doc = builder.parse(archivoXML);

            // Crea un objeto XPathFactory y un objeto XPath para obtener el valor del atributo "total"
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            // Obtiene el valor del atributo "total" del primer elemento "registro" del archivo XML
            totalString = xpath.compile("/registro/@total").evaluate(doc);
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        }

        // Convierte el valor del atributo "total" a un valor de tipo float y lo retorna
        return Float.parseFloat(totalString);
    }

    /**
     * Muestra el contenido de un registro concreto en la consola.
     * Elige el archivo XML del registro a mostrar y lo procesa para obtener información sobre las compras.
     *
     */
    public static void mostrarRegistroConcreto() {
        try {
            // Limpiar la consola
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Obtener la carpeta "Registros" donde se encuentran los archivos XML
            File carpeta = new File("Registros");

            // Obtener la lista de archivos XML en la carpeta "Registros"
            File[] fileList = carpeta.listFiles();

            // Crear un ArrayList a partir del array de archivos
            assert fileList != null;
            ArrayList<File> archivosXML = new ArrayList<>(Arrays.asList(fileList));

            // Seleccionar el archivo XML del registro a mostrar
            File registro = escogerRegistroAMostrar(archivosXML);

            // Crear un objeto DocumentBuilderFactory para parsear el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(registro);

            // Crear un objeto XPath para ejecutar consultas XPath en el documento XML
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            // Compilar expresiones XPath para obtener información sobre las compras
            XPathExpression xPathItems = xpath.compile("//item");
            XPathExpression xPathFallidas = xpath.compile("/registro/@fallidas");
            XPathExpression xPathTotal = xpath.compile("/registro/@total");

            // Obtener los nodos que contienen información sobre los elementos "item"
            NodeList resultsItems = (NodeList) xPathItems.evaluate(document, XPathConstants.NODESET);

            // Obtener el número de peticiones fallidas y el total gastado
            String fallidas = (String) xPathFallidas.evaluate(document, XPathConstants.STRING);
            String total = (String) xPathTotal.evaluate(document, XPathConstants.STRING);

            // Mostrar la información en la consola
            System.out.println("\u001B[34m" + "*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*\u001B[0m");
            System.out.println("\u001B[34m" + "*" + "\u001B[0m" + StringUtils.center("El total es de \u001B[32m" + total + "€\u001B[0m", 97) + "\u001B[34m" + "*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90));
            DecimalFormat formato = new DecimalFormat("#.##");
            System.out.println("*" + " ".repeat(88) + "*\u001B[0m");
            System.out.println("\u001B[34m" + "*" + "\u001B[0m" + StringUtils.center("El total al venderlo en Steam es de \u001B[32m" + formato.format((Float.parseFloat(total) * 0.8333)) + "€\u001B[0m", 97) + "\u001B[34m" + "*" + "\u001B[0m");
            System.out.println("\u001B[34m*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*\u001B[0m");
            System.out.println("\u001B[34m" + "*" + "\u001B[0m" + StringUtils.center("Peticiones fallidas -> \u001B[31m" + fallidas + "\u001B[0m", 97) + "\u001B[34m" + "*" + "\u001B[0m");
            System.out.println("\u001B[34m*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90) + "\u001B[0m\n");

            for (int i = 0; i < resultsItems.getLength(); i++) {
                Node itemNode = resultsItems.item(i);
                // Obtiene el nombre y el precio de cada item del nodo actual.
                String nombre = (String) xpath.evaluate("concat(nombre, ';', precio)", itemNode, XPathConstants.STRING);
                String[] nombrePrecio = nombre.split(";");

                // Imprime una línea de asteriscos para separar cada item.
                System.out.println("*".repeat(90));
                // Imprime en consola el nombre y el precio de cada item con colores.
                System.out.println("*" + StringUtils.center("\u001B[32m" + nombrePrecio[0] + "\u001B[0m -> \u001B[33m" + nombrePrecio[1] + "€\u001B[0m", 106) + "*");
            }
            // Imprime una línea de asteriscos al final para separar los resultados del resto de la salida.
            System.out.println("*".repeat(90));

            // Espera a que el usuario presione una tecla para continuar.
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (XPathExpressionException | IOException | ParserConfigurationException | InterruptedException | SAXException e) {
            e.printStackTrace();
            GeneradorLogs.errorLogManager(e);
        }
    }

    /**
     * Método que muestra por consola una lista de archivos XML y permite al usuario elegir cuál quiere visualizar.
     *
     * @param archivosXML una lista de archivos XML disponibles para visualizar.
     * @return el archivo XML elegido por el usuario.
     */
    private static File escogerRegistroAMostrar(ArrayList<File> archivosXML) {
        // Se recorre la lista de archivos y se imprime por consola cada uno de ellos.
        int c = 0;
        System.out.println("\u001B[31m*".repeat(90));
        for (File archivo : archivosXML) {
            System.out.println("\u001B[31m*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("\u001B[34m" + c + "\u001B[0m -> \u001B[32m" +
                    archivo.getName().replace(".xml", "").replace("_", ":") + "\u001B[0m", 106) + "\u001B[31m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90) + "\u001B[0m");
            c++;
        }

        // Se solicita al usuario que elija el archivo que desea visualizar.
        System.out.println("\u001B[32m*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("¿Qué registro quieres ver?", 88) + "\u001B[32m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90) + "\u001B[0m");
        Scanner scanner = new Scanner(System.in);
        int op = scanner.nextInt();

        // Se retorna el archivo elegido por el usuario.
        return archivosXML.get(op);
    }
}
