package es.skinholder.utils;

import org.apache.commons.lang3.StringUtils;
import es.skinholder.records.RespuestaPeticion;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @author jagobainda
 *
 */
public class Peticion {

    /**
     * La cantidad máxima de intentos para realizar una petición a la API de Steam.
     */
    private static final int NUMERO_MAXIMO_INTENTOS = 5;

    /**
     * Realiza una petición HTTP GET a una API de Steam con el enlace especificado.
     *
     * @param enlace el enlace de la API de Steam al que se desea realizar la petición
     * @return la respuesta de la API de Steam en un objeto RespuestaPeticion
     */
    public static RespuestaPeticion hacerPeticion(String enlace) {
        int c = 0;
        String respuesta = null;
        boolean fallo = false;
        while (c <= NUMERO_MAXIMO_INTENTOS) {
            try {
                URL steamUrl = new URL(enlace);

                // Permite verificar cualquier nombre de host en el handshake de SSL
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                // Configura SSLContext para confiar en todos los certificados
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new X509TrustManager[] {
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                }, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

                // Crea una conexión HTTP a partir de la URL
                HttpURLConnection con = (HttpURLConnection) steamUrl.openConnection();
                con.setRequestMethod("GET");

                // Lee la respuesta del servidor
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Almacena la respuesta en una cadena
                respuesta = response.toString();
                break;
            } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                if (c == NUMERO_MAXIMO_INTENTOS) {
                    System.out.println("\u001B[34m*\u001B[0m" + StringUtils.center(e.toString(), 88) + "\u001B[34m*\u001B[0m");
                    respuesta = "";
                    Logs.errorLogManager(e);
                } else {
                    Logs.debugLogManager("Petición fallo número " + (c + 1) + " por la causa: " + e.getMessage());
                }
                c++;
                fallo = true;
            }
            // Espera 3 segundos antes de realizar el siguiente intento
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logs.errorLogManager(e);
            }
        }
        // Devuelve la respuesta y un indicador de fallo
        return new RespuestaPeticion(respuesta, fallo);
    }

    /**
     * Extrae el precio más bajo de un objeto JSON.
     *
     * @param input Cadena de texto que contiene el objeto JSON.
     * @return El precio más bajo como un número de punto flotante. Si la cadena de entrada está vacía, devuelve -1.
     */
    public static float extraerPrecioDeJSON(String input) {
        if (input.isEmpty()) {
            // Si la cadena de entrada está vacía, se devuelve -1
            return -1;
        } else {
            // Se crea un objeto JSON a partir de la cadena de entrada y se extrae el precio más bajo
            JSONObject json = new JSONObject(input);
            String priceString = json.get("lowest_price").toString().replace(",", ".").replace("-", "0").replace("€", "");
            return Float.parseFloat(priceString.replace("â‚¬", "").replace(" ", ""));
        }
    }
}
