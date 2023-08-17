package es.skinholder.utils;

import org.apache.commons.lang3.StringUtils;
import es.skinholder.records.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Clase para almacenar los items.
 * @author jagobainda
 */
public class Items {
    /**
     * Mapa que almacena los items con su respectivo nombre como clave.
     */
    public TreeMap<String, Item> items;

    /**
     * Constructor de la clase Items que inicializa el mapa y agrega los items existentes.
     */
    public Items() {
        items = new TreeMap<>();
        anadirItems();
    }

    /**
     * Método para obtener el mapa de items.
     *
     * @return el mapa de items.
     */
    public TreeMap<String, Item> getItems() {
        return items;
    }

    /**
     * Método privado que lee los archivos XML de la carpeta Items y agrega los items al mapa.
     */
    private void anadirItems() {
        try {
            File carpeta = new File("Items");
            File[] archivosXML = carpeta.listFiles();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            assert archivosXML != null;
            for (File file : archivosXML) {
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                Element root = doc.getDocumentElement();

                // Obtener el nombre, cantidad y enlace de cada item del archivo XML.
                String nombre = xpath.compile("/item/nombre").evaluate(root);
                int cantidad = Integer.parseInt(xpath.compile("/item/cantidad").evaluate(root));
                String enlace = xpath.compile("/item/id").evaluate(root);

                // Crear el item y agregarlo al mapa de items.
                Item item = new Item(enlace, cantidad);
                items.put(nombre, item);
            }
        } catch (XPathExpressionException | ParserConfigurationException | IOException | SAXException e) {
            Logs.errorLogManager(e);
        }
    }

    /**
     * Muestra todos los items almacenados en el objeto items de la clase.
     */
    public void mostrarItems() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            // variable que se utiliza para llevar un contador del número de items que se están mostrando
            int c = 1;
            System.out.println("\u001B[33m*".repeat(90));
            // se itera sobre los elementos del objeto items utilizando la estructura Map Entry
            for (Map.Entry<String, Item> entry : items.entrySet()) {
                // se muestra el número de item, su nombre y cantidad en consola, utilizando códigos ANSI para darle color a la salida
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center(c + " - \u001B[32m" + entry.getKey() + "\u001B[0m -> \u001B[34mCantidad: "
                        + entry.getValue().CANTIDAD() + "\u001B[0m", 106) + "\u001B[33m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*".repeat(90));
                // se incrementa el contador para el siguiente item
                c++;
            }
            System.out.println("\u001B[0m");
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }
}
