import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;

// Clase que contiene la lógica de conversión de moneda y la interacción con la API.
public class ConversorMoneda {

    private static final String API_KEY = "effe1240153930679ba99289"; //API_KEY asignada por la API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/"; // URL Base para ExchangeRate-API

    // Mapa para almacenar los nombres de las monedas.
    private static final Map<String, String> NOMBRE_MONEDAS = new HashMap<>();

    // Inicializa el mapa de nombres de monedas.
    static {
        NOMBRE_MONEDAS.put("USD", "dólares");
        NOMBRE_MONEDAS.put("ARS", "pesos argentinos");
        NOMBRE_MONEDAS.put("BRL", "reales brasileños");
        NOMBRE_MONEDAS.put("COP", "pesos colombianos");
    }

    // Método para mostrar el menú de opciones al usuario.
    public static void mostrarMenu() {
        System.out.println("\n*****************************************");
        System.out.println("Bienvenido/a al Conversor de Moneda =]");
        System.out.println("*****************************************");
        System.out.println("1) Dólar ==> Peso argentino");
        System.out.println("2) Peso argentino ==> Dólar");
        System.out.println("3) Dólar ==> Real brasileño");
        System.out.println("4) Real brasileño ==> Dólar");
        System.out.println("5) Dólar ==> Peso colombiano");
        System.out.println("6) Peso colombiano ==> Dólar");
        System.out.println("7) Salir");
    }

    //Método que valida la entrada del usuario y la opción.
    public static int obtenerOpcion(Scanner scanner) {
        int opcion = -1; // Inicializa con un valor inválido.
        while (opcion < 1 || opcion > 7) {
            System.out.print("Elija una opción válida: ");
            try {
                opcion = scanner.nextInt(); // Intenta leer un entero del usuario.
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número del 1 al 7.");
                scanner.next(); // Limpia la entrada inválida del scanner.
            }
            if (opcion < 1 || opcion > 7) { //selecciona una de las opciones válidas
                System.out.println("Opción inválida. Por favor, ingrese un número del 1 al 7.");
            }
        }
        return opcion;
    }


    // Método para realizar la conversión de moneda.
    public static void convertir(String monedaOrigen, String monedaDestino, Scanner scanner) { //recibe tres parámetros dos de la selección y uno del usuario
        System.out.print("Ingrese la cantidad en " + obtenerNombreMoneda(monedaOrigen) + ": ");
        double cantidad = scanner.nextDouble(); // Obtiene la cantidad a convertir del usuario.

        try {
            double tasaCambio = obtenerTasaDeCambio(monedaOrigen, monedaDestino); // Obtiene la tasa de cambio de la API.
            double resultado = cantidad * tasaCambio; // Calcula el resultado de la conversión.
            System.out.println(cantidad + " " + obtenerNombreMoneda(monedaOrigen) + " equivalen a "
                    + String.format("%.2f", resultado) + " " + obtenerNombreMoneda(monedaDestino) + "."); // Muestra el resultado formateado.
        } catch (IOException e) {
            System.out.println("Error al obtener la tasa de cambio: " + e.getMessage()); // Maneja los errores de la API.
        }
    }


    // Método para obtener la tasa de cambio entre dos monedas usando la API.
    // Lanza IOException si ocurre algún error de conexión o si la respuesta es inválida.
    public static double obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) throws IOException {
        // Construye la URL completa de la API usando la clave, el código de la moneda origen y destino.
        String urlString = API_URL + API_KEY + "/pair/" + monedaOrigen + "/" + monedaDestino;
        URL url = new URL(urlString);  // Crea un objeto URL a partir de la cadena construida.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();  // Abre una conexión HTTP a la URL
        connection.setRequestMethod("GET");  // Establece que se hará una solicitud GET (lectura de datos).
        StringBuilder response = new StringBuilder();  // Crea un StringBuilder (response) para almacenar la respuesta de la API.

        // este bloque lee la respuesta línea por línea desde el flujo de entrada de la conexión
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {  //creo un objeto
            String line;  //creo una variable para almacenar
            while ((line = reader.readLine()) != null) { // mientras la variable no sea null ejecutara
                response.append(line);// Agrega cada línea a la respuesta completa.
            }
        } finally {
            connection.disconnect();// Cierra la conexión después de recibir la respuesta.
        }

        JSONObject jsonResponse = new JSONObject(response.toString());  // Convierte la respuesta JSON en un objeto JSONObject para analizarla.
        if (!jsonResponse.getString("result").equals("success")) {  // Verifica si el resultado de la API fue exitoso; si no, lanza una excepción.
            throw new IOException("Error en la respuesta de la API: " + jsonResponse.toString());
        }
        return jsonResponse.getDouble("conversion_rate");  // Extrae y devuelve la tasa de conversión desde el campo "conversion_rate" del Json
    }


    // Método para obtener el nombre completo de una moneda a partir de su código.
    public static String obtenerNombreMoneda(String codigoMoneda) { //Recibe el código de la moneda
        return NOMBRE_MONEDAS.getOrDefault(codigoMoneda, codigoMoneda); // Devuelve el nombre de la moneda o el código si no se encuentra.
    }
}

