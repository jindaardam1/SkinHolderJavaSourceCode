package es.skinholder.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author jagobainda
 *
 * La clase Conexiones proporciona métodos para verificar la disponibilidad de conexiones de red
 * y recuperar el código fuente de una página web.
 */
public class Conexiones {

    /**
     * Comprueba si hay una conexión a un host específico y mide el tiempo de respuesta en milisegundos.
     *
     * @param host El nombre del host o dirección IP a comprobar.
     * @return El tiempo de respuesta en milisegundos si la conexión es exitosa, o -1 si no se puede establecer la conexión.
     */
    public static int hayConexion(String host) {
        long startTime = System.currentTimeMillis();
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean isConnected = address.isReachable(5000); // Timeout en milisegundos
            if (isConnected) {
                long endTime = System.currentTimeMillis();
                return (int) (endTime - startTime);
            }
        } catch (IOException e) {
            Logs.errorLogManager(e);
        }
        return -1; // -1 indica que no hubo conexión
    }

    /**
     * Devuelve una cadena que indica la calidad de la conexión en función del tiempo de ping.
     *
     * @param ping El tiempo de respuesta en milisegundos.
     * @return Una cadena que describe la calidad de la conexión con colores ANSI.
     */
    public static String calidadCon(int ping) {
        if (ping == -1) {
            Logs.infoLogManager("No hay conexión");
            return "\u001B[31mNo hay conexión\u001B[33m";
        } else if (ping < 1000) {
            Logs.infoLogManager("Conexión establecida, ping: " + ping);
            return "\u001B[32mConexión establecida\u001B[33m";
        } else if (ping < 3000) {
            Logs.infoLogManager("Conexión inestable, ping: " + ping);
            return "\u001B[33mConexión inestable\u001B[33m";
        } else {
            Logs.infoLogManager("Conexión inestable, ping: " + ping);
            return "\u001B[31mConexión inestable\u001B[33m";
        }
    }

    /**
     * Devuelve una cadena de colores ANSI para representar la calidad del ping.
     *
     * @param ping El tiempo de respuesta en milisegundos.
     * @return Una cadena de colores ANSI que indica la calidad del ping.
     */
    public static String calidadPing(int ping) {
        if (ping == -1) {
            return "\u001B[31m";
        } else if (ping < 1000) {
            return "\u001B[32m";
        } else if (ping < 3000) {
            return "\u001B[33m";
        } else {
            return "\u001B[31m";
        }
    }

    /**
     * Obtiene el código fuente de una página web dada una URL.
     *
     * @param url La URL de la página web a recuperar.
     * @return El código fuente de la página web o null en caso de error.
     */
    public static String getCodigoFuente(String url) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .execute();

            if (response.statusCode() == 200) {
                Document document = response.parse();
                return document.outerHtml(); // Devuelve el código fuente de la página
            } else {
                Logs.errorLogManager("No se pudo obtener una respuesta válida del servidor. Código de estado: " + response.statusCode());
                return null;
            }
        } catch (IOException e) {
            Logs.errorLogManager(e);
            return null; // Devuelve null en caso de error
        }
    }
}
