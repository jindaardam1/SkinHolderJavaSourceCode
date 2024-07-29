package es.skinholder.utils;

import org.apache.commons.lang3.StringUtils;
import es.skinholder.records.Item;
import es.skinholder.records.PrecioNombreCantidad;
import es.skinholder.records.RespuestaPeticion;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Clase encargada de registrar información.
 * @author jagobainda
 */
public class Registros {
    public static void nuevoRegistro() {

        // Limpia la consola
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }

        // Crea una instancia de Items y un ArrayList vacío de PrecioNombreCantidad para ir almacenando los precios, nombres y cantidades de los productos
        Items items = new Items();
        ArrayList<PrecioNombreCantidad> precioNombreCantidadArrayList = new ArrayList<>();

        // Inicializa el total a 0, el contador de peticiones a 1 y el contador de fallos a 0
        float total = 0;
        int c = 1;
        int fallos = 0;

        infoPeticiones();

        // Inicia el contador de tiempo de ejecución
        long inicio = System.currentTimeMillis();

        // Realiza una petición por cada producto en la lista de productos
        for (Map.Entry<String, Item> entry : items.getItems().entrySet()) {
            System.out.println("*" + " ".repeat(88) + "*");

            // Realiza la petición correspondiente al producto y extrae el precio
            RespuestaPeticion respuestaPeticion = Peticion.hacerPeticion(entry.getValue().ENLACE());
            float precio = Peticion.extraerPrecioDeJSON(respuestaPeticion.RESPUESTA());

            // Determina si la petición ha fallado
            boolean fallo = respuestaPeticion.FALLO();

            // Si el precio es -1.00, la petición ha fallado
            if (precio == -1.00) {
                System.out.println("\u001B[34m*\u001B[0m" + StringUtils.center("\u001B[31mPetición " + c + " de " + items.getItems().size() + "... HA FALLADO\u001B[0m", 97) + "\u001B[34m*");
                fallos++;

                // Si la petición ha sido exitosa, se acumula el total
            } else {
                total += precio * entry.getValue().CANTIDAD();

                // Almacena el precio, nombre y cantidad del producto en el ArrayList
                precioNombreCantidadArrayList.add(new PrecioNombreCantidad(precio, entry.getKey(), entry.getValue().CANTIDAD()));

                // Imprime el resultado de la petición
                if (fallo) {
                    System.out.println("*\u001B[0m" + StringUtils.center("\u001B[33mPetición " + c + " de " + items.getItems().size() + "... DONE\u001B[0m", 97) + "\u001B[34m*");
                } else {
                    System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32mPetición " + c + " de " + items.getItems().size() + "... DONE\u001B[0m", 97) + "\u001B[34m*");
                }
            }

            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90));

            // Incrementa el contador de peticiones y espera 3 segundos antes de continuar
            c++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logs.errorLogManager(e);
            }
        }


        // Detiene el contador de tiempo de ejecución y lo convierte a segundos
        long fin = System.currentTimeMillis();
        double tiempo = (fin - inicio) / 1000.0;

        // Ordena la lista según el precio en orden descendente
        Comparator<PrecioNombreCantidad> comparator = Comparator.comparing(PrecioNombreCantidad::PRECIO);
        precioNombreCantidadArrayList.sort(comparator.reversed());

        double alVenderEnSteam = total * 0.87;

        // Imprime información relevante en consola
        System.out.println("\n\u001B[34m" + "*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("Tiempo de ejecución --> \u001B[34m" + ((int) tiempo / 60) + ":" +
                ((int) tiempo % 60) + "\u001B[0m | " + "Peticiones fallidas --> \u001B[31m" + fallos + "\u001B[0m", 106) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("El total es de \u001B[32m" + String.format("%.2f", total) + "€\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("Al vender restando la comisión de Steam \u001B[32m" + String.format("%.2f", alVenderEnSteam) + "€\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90) + "\u001B[0m\n");

        // Formatear TreeMap de precios y nombres a ItemPrecio
        formatearTreeMapPrecioNombreAItemPrecio(precioNombreCantidadArrayList);

        // Imprimir TreeMap de items y cantidad
        System.out.println(formatearTreeMapItemsAItemCantidad(items.getItems()));

        // Obtener fecha y hora actual para generar el nombre del archivo de registro
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm");
        String fechaHoraActual = now.format(formatter);
        String rutaRegistro = "Registros\\" + fechaHoraActual + ".xml";

        // Crear objeto Document para guardar los datos en formato XML
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Logs.errorLogManager(e);
        }
        assert dBuilder != null;
        Document documento = dBuilder.newDocument();

        // Crear elemento raíz y agregarlo al documento
        Element elementoRaiz = documento.createElement("registro");
        documento.appendChild(elementoRaiz);

        // Formatear el precio total y agregarlo como atributo al elemento raíz
        DecimalFormat formato = new DecimalFormat("#.##");
        Attr atributo1 = documento.createAttribute("total");
        atributo1.setValue(String.valueOf(formato.format(total)).replace(",", "."));
        elementoRaiz.setAttributeNode(atributo1);

        // Agregar el número de fallos como atributo al elemento raíz
        Attr atributo2 = documento.createAttribute("fallidas");
        atributo2.setValue(String.valueOf(fallos));
        elementoRaiz.setAttributeNode(atributo2);

        // Agregar los elementos comprados como elementos hijos del elemento raíz
        for (PrecioNombreCantidad entrada : precioNombreCantidadArrayList) {
            Element elementoItem = documento.createElement("item");
            elementoRaiz.appendChild(elementoItem);

            Element nombreItem = documento.createElement("nombre");
            elementoItem.appendChild(nombreItem);

            Text textoNombre = documento.createTextNode(entrada.NOMBRE());
            nombreItem.appendChild(textoNombre);

            Element elementoPrecio = documento.createElement("precio");
            elementoItem.appendChild(elementoPrecio);

            Text textoPrecio = documento.createTextNode(String.valueOf(entrada.PRECIO()));
            elementoPrecio.appendChild(textoPrecio);
        }

        // Guardar el documento como archivo XML en la carpeta "Registros"
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            Logs.errorLogManager(e);
        }
        assert transformer != null;
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(documento);
        StreamResult result = new StreamResult(new File(rutaRegistro));
        try {
            transformer.transform(source, result);
        } catch (Exception e) {
            Logs.errorLogManager(e);
        }

        // Imprimir mensaje de éxito al guardar el registro
        System.out.println("Registro guardado con éxito en \u001B[32mRegistros\\" + fechaHoraActual +".xml\u001B[0m");
        Logs.infoLogManager("Registro guardado con éxito en Registros/" + fechaHoraActual + ".xml");
        try {
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (Exception e) {
            Logs.errorLogManager(e);
        }
    }

    /**
     * Imprime en la consola información sobre las peticiones.
     * Se muestran diferentes mensajes y colores dependiendo del número de veces que haya fallado la petición.
     * Colores:
     * - Verde: Petición realizada correctamente a la primera.
     * - Amarillo/Naranja: Petición ha fallado entre 1 y 4 veces. El servidor de Steam o la conexión pueden ser inestables.
     * - Rojo: Petición ha fallado más de 5 veces.
     */
    private static void infoPeticiones() {
        // Imprime en la consola un cuadro de texto con la información sobre las peticiones
        System.out.println("\u001B[34m" + "*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[32mPetición en verde: realizada correctamente a la primera.\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[33mPetición en amarillo" +
                "/naranja: ha fallado entre 1 y 4 veces.\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[33mEl servidor de Steam o tu conexión pueden ser inestables.\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90));
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*\u001B[0m" + StringUtils.center("\u001B[31mPetición en rojo (más info del error concreto): ha fallado más de 5 veces.\u001B[0m", 97) + "\u001B[34m*");
        System.out.println("*" + " ".repeat(88) + "*");
        System.out.println("*".repeat(90) + "\n");
        System.out.println("*".repeat(90));
    }


    /**
     * Muestra por consola los elementos del ArrayList de PrecioNombreCantidad en un formato estilizado
     * utilizando colores en la fuente.
     *
     * @param precioNombreCantidad el ArrayList de objetos PrecioNombreCantidad que se va a formatear.
     */
    private static void formatearTreeMapPrecioNombreAItemPrecio(ArrayList<PrecioNombreCantidad> precioNombreCantidad) {
        // Itera sobre cada entrada en el ArrayList y muestra su información en un formato estilizado.
        for (PrecioNombreCantidad entrada : precioNombreCantidad) {
            // Muestra un separador de línea.
            System.out.println("*".repeat(90));
            // Muestra el nombre del objeto en verde, el precio en amarillo y la cantidad en azul.
            System.out.println("*" + StringUtils.center("\u001B[32m" + entrada.NOMBRE() + "\u001B[0m -> " +
                    "\u001B[33m" + entrada.PRECIO() + "€\u001B[0m -> " +
                    "\u001B[34mCantidad: " + entrada.CANTIDAD() + "\u001B[0m", 115) + "*");
        }
        // Muestra un separador de línea después de haber mostrado todos los elementos.
        System.out.println("*".repeat(90) + "\n");
    }

    /**
     * Formatea un TreeMap de objetos Item en una cadena de texto con información acerca de la cantidad total de artículos.
     *
     * @param items TreeMap que contiene objetos Item.
     * @return Cadena de texto con la cantidad total de artículos.
     */
    private static String formatearTreeMapItemsAItemCantidad(TreeMap<String, Item> items) {
        // Inicializamos StringBuilder y agregamos separador visual.
        StringBuilder sb = new StringBuilder();
        sb.append("\n\u001B[34m------------------------------------------------------------------------------------------\u001B[0m\n");

        // Sumamos la cantidad de artículos.
        int cantidad = 0;
        for (Item item : items.values()) {
            cantidad += item.CANTIDAD();
        }

        // Agregamos la cantidad total de artículos y otro separador visual.
        sb.append("\n").append(StringUtils.center("Cantidad total de artículos: \u001B[34m" + cantidad + "\u001B[0m", 97)).append("\n");
        sb.append("\n\u001B[34m------------------------------------------------------------------------------------------\u001B[0m\n");

        // Devolvemos la cadena de texto generada.
        return sb.toString();
    }

    /**
     * Este método permite añadir un nuevo item al inventario.
     */
    public static void anadirItem() {
        try {
            // Limpia la consola
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Crea un objeto Scanner para leer la entrada del usuario desde la consola
            Scanner scanner = new Scanner(System.in);

            // Solicita al usuario que introduzca el ID del item
            System.out.println("\u001B[32m*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("Introduce el ID del nuevo item:", 88) + "\u001B[32m*");
            System.out.println("*\u001B[0m" + StringUtils.center("\u001B[34m(Ejemplo: Sticker%20%7C%20s1mple%20%28Gold%29%20%7C%20London%202018)\u001B[0m", 97) + "\u001B[32m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90));
            String id = scanner.next();

            // Solicita al usuario que introduzca la cantidad del item
            int cantidad = 0;
            while (cantidad < 1) {
                System.out.println("\u001B[32m*".repeat(90));
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("Introduce la \u001B[34mcantidad\u001B[0m que tienes de este item:", 97) + "\u001B[32m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*".repeat(90));
                try {
                    cantidad = scanner.nextInt();
                    scanner.nextLine(); // Consume la línea en blanco que queda en el buffer después de nextInt()
                } catch (InputMismatchException e) {
                    Logs.errorLogManager(e);
                    scanner.nextLine();
                }
            }

            // Solicita al usuario que introduzca el nombre del item
            System.out.println("\u001B[32m*".repeat(90));
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*\u001B[0m" + StringUtils.center("Introduce el nombre del nuevo item:", 88) + "\u001B[32m*");
            System.out.println("*\u001B[0m" + StringUtils.center("\u001B[34m(Ejemplo: Sticker | s1mple (Gold) | London 2018)\u001B[0m", 97) + "\u001B[32m*");
            System.out.println("*" + " ".repeat(88) + "*");
            System.out.println("*".repeat(90));
            String nombre = scanner.nextLine();

            // Añade el item al archivo XML
            anadirItemXML(id, cantidad, nombre);

            // Espera a que el usuario presione Enter para continuar
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }

    /**
     * Añade un nuevo item en formato XML con el "id", cantidad y nombre especificados.
     * @param id el id del item en Steam Community Market.
     * @param cantidad la cantidad de items que se desean añadir.
     * @param nombre el nombre del item.
     */
    private static void anadirItemXML(String id, int cantidad, String nombre) {
        try {
            // Se crea una nueva instancia de DocumentBuilderFactory.
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            // Se crea un nuevo DocumentBuilder.
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Se crea un nuevo Document.
            Document documento = dBuilder.newDocument();

            // Se crea un nuevo elemento raíz llamado "item".
            Element elementoRaiz = documento.createElement("item");
            documento.appendChild(elementoRaiz);

            // Se crea un nuevo elemento llamado "nombre".
            Element elementoNombre = documento.createElement("nombre");
            elementoRaiz.appendChild(elementoNombre);

            // Se crea un nuevo nodo de texto con el nombre del item.
            Text textoNombre = documento.createTextNode(nombre);
            elementoNombre.appendChild(textoNombre);

            // Se crea un nuevo elemento llamado "cantidad".
            Element elementoCantidad = documento.createElement("cantidad");
            elementoRaiz.appendChild(elementoCantidad);

            // Se crea un nuevo nodo de texto con la cantidad de items.
            Text textoCantidad = documento.createTextNode(String.valueOf(cantidad));
            elementoCantidad.appendChild(textoCantidad);

            // Se crea un nuevo elemento llamado "id".
            Element elementoID = documento.createElement("id");
            elementoRaiz.appendChild(elementoID);

            // Se crea un nuevo nodo de texto con la URL que contiene el "id" del item.
            Text textoID = documento.createTextNode("https://steamcommunity.com/market/priceoverview/?country=ES&currency=3&appid=730&market_hash_name=" + id);
            elementoID.appendChild(textoID);

            // Se crea una nueva instancia de TransformerFactory.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // Se crea un nuevo Transformer.
            Transformer transformer = transformerFactory.newTransformer();
            // Se configura el Transformer para que produzca una salida con sangría.
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            // Se configura el Transformer para que trabaje en UTF-8.
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // Se crea una nueva fuente de datos DOM a partir del Document.
            DOMSource source = new DOMSource(documento);
            // Se crea un nuevo objeto StreamResult que representa el archivo XML a generar.
            OutputStream outputStream = new FileOutputStream("Items/" + nombre.replace("|", ",") + ".xml");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            StreamResult result = new StreamResult(writer);
            // Se realiza la transformación del Document en un archivo XML.
            transformer.transform(source, result);
            // Muestra un mensaje de confirmación al usuario
            System.out.println("\u001B[32mNuevo item añadido correctamente.\u001B[0m");
            // Guarda el log del usuario añadido
            Logs.infoLogManager("Se ha añadido un nuevo item " + nombre);
        } catch (ParserConfigurationException | TransformerException e) {
            Logs.errorLogManager(e);
        } catch (Exception e) {
            System.out.println("\u001B[31mNo se ha creado el item.\u001B[0m");
            System.out.println("No introducir caracteres con tildes ni otros caracteres especiales.");
            Logs.errorLogManager(e);
        }
    }

    /**
     * Este método muestra una lista de los items disponibles y permite al usuario modificar o eliminar un item seleccionado.
     * Si el usuario elige modificar un item, se le pedirá que ingrese la nueva cantidad.
     * Si el usuario elige eliminar un item, se eliminará el item seleccionado.
     * Este método también utiliza la clase Items para mostrar los items disponibles.
     *
     */
    public static void modificarEliminarItem() {
        try {
            // Borra la consola para mostrar la lista de items
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Crea una nueva instancia de la clase Items y muestra los items disponibles
            Items items = new Items();
            items.mostrarItems();

            // Pide al usuario que seleccione un item

            Scanner scanner = new Scanner(System.in);

            // Si el usuario ingresa un número, se procede a buscar el item correspondiente

            int op = 0;
            while (op < 1 || op > items.items.size()) {
                System.out.println("\u001B[31m*".repeat(90));
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("¿Qué item quieres \u001B[33mmodificar\u001B[0m/\u001B[31meliminar\u001B[0m?", 106) + "\u001B[31m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*".repeat(90) + "\u001B[0m");
                try {
                    op = scanner.nextInt();
                } catch (InputMismatchException e) {
                    Logs.errorLogManager(e);
                    scanner.nextLine();
                }
            }
            TreeSet<String> keySet = new TreeSet<>(items.getItems().keySet());
            String nombreItem = (String) keySet.toArray()[op - 1];

            // Pide al usuario que elija entre modificar o eliminar el item seleccionado
            int eliminarOModificar = 0;
            while (eliminarOModificar < 1 || eliminarOModificar > 2) {
                System.out.println("\u001B[31m*".repeat(90));
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*\u001B[0m" + StringUtils.center("¿Quieres eliminarlo \u001B[34m(introduce 1)\u001B[0m o modificar la cantidad \u001B[34m(introduce 2)\u001B[0m?", 106) + "\u001B[31m*");
                System.out.println("*" + " ".repeat(88) + "*");
                System.out.println("*".repeat(90) + "\u001B[0m");
                try {
                    eliminarOModificar = scanner.nextInt();
                } catch (InputMismatchException e) {
                    Logs.errorLogManager(e);
                    scanner.nextLine();
                }
            }


            // Según la elección del usuario, se llama al método eliminarItem o modificarItem
            switch (eliminarOModificar) {
                case 1 -> eliminarItem(nombreItem);
                case 2 -> {
                    int nuevaCant = 0;
                    while (nuevaCant < 1) {
                        System.out.println("\u001B[31m*".repeat(90));
                        System.out.println("*" + " ".repeat(88) + "*");
                        System.out.println("*\u001B[0m" + StringUtils.center("Ingresa la \u001B[34mnueva cantidad:\u001B[0m", 97) + "\u001B[31m*");
                        System.out.println("*" + " ".repeat(88) + "*");
                        System.out.println("*".repeat(90) + "\u001B[0m");
                        try {
                            nuevaCant = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            Logs.errorLogManager(e);
                            scanner.nextLine();
                        }
                    }
                    modificarItem(nombreItem, nuevaCant);
                }
            }

            // Pausa la consola para que el usuario tenga tiempo de leer el mensaje final
            new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            Logs.errorLogManager(e);
        }
    }


    /**
     * Elimina un item del directorio de items.
     *
     * @param nombreItem el nombre del item a eliminar.
     */
    private static void eliminarItem(String nombreItem) {
        // Crea un objeto File con el nombre del archivo a borrar.
        // El nombre del archivo se obtiene de la carpeta "Items" y se reemplazan los "|" por ",".
        File archivoABorrar = new File("Items/" + nombreItem.replace("|", ",") + ".xml");

        // Intenta eliminar el archivo y maneja las excepciones si ocurren.
        try {
            if (archivoABorrar.delete()) {
                System.out.println("\u001B[32mEl archivo se ha eliminado correctamente.\u001B[0m");
                Logs.infoLogManager("Se ha eliminado un item: " + nombreItem);
            }
        } catch (Exception e) {
            System.out.println("\u001B[31mNo se ha podido eliminar el archivo.\u001B[0m");

            Logs.errorLogManager(e);
        }
    }


    /**
     * Este método permite modificar la cantidad de un item existente en un archivo XML.
     *
     * @param nombreItem el nombre del item a modificar.
     * @param nuevaCantidad la nueva cantidad del item.
     */
    private static void modificarItem(String nombreItem, int nuevaCantidad) {
        try {
            // Se obtiene el archivo XML correspondiente al item a modificar.
            File archivoAModificar = new File("Items/" + nombreItem.replace("|", ",") + ".xml");

            // Se crea el parser de documentos XML.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Se parsea el archivo XML y se obtiene el documento.
            Document doc = db.parse(archivoAModificar);

            // Se crea el objeto XPath y se compila la expresión XPath para buscar el nodo "cantidad".
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expr = xpath.compile("//cantidad");

            // Se evalúa la expresión XPath en el documento y se obtiene el nodo "cantidad".
            Node cantidadNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

            // Se actualiza el valor del nodo "cantidad" con la nueva cantidad proporcionada.
            cantidadNode.setTextContent(String.valueOf(nuevaCantidad));

            // Se crea el objeto Transformer para guardar los cambios en el archivo XML.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Se crea el objeto DOMSource con el documento modificado y se guarda en el archivo XML.
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivoAModificar);
            transformer.transform(source, result);

            // Se muestra un mensaje indicando que la cantidad se ha actualizado correctamente.
            System.out.println("\u001B[32mLa cantidad se ha actualizado correctamente.\u001B[0m");
            Logs.infoLogManager("La cantidad del item [" + nombreItem + "] se ha actualizado correctamente a " + nuevaCantidad);
        }  catch (XPathExpressionException | ParserConfigurationException | IOException | TransformerException | SAXException e) {
            // Se muestra un mensaje de error si ocurre alguna excepción durante la ejecución del método.
            System.out.println("\u001B[31mError al modificar la cantidad del item.\u001B[0m");
            Logs.errorLogManager(e);
        }
    }
}
