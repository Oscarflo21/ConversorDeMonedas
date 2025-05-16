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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Clase que contiene la lógica de conversión de moneda y la interacción con la API.
public class ConversorMoneda {

    // Reemplaza con tu API key. Es importante que este valor sea secreto y no se comparta públicamente.
    private static final String API_KEY = "effe1240153930679ba99289";
    // Reemplaza con la URL base de la API que elijas.
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/"; // Ejemplo: ExchangeRate-API
    // private static final String API_URL = "https://api.currencyapi.com/v3/latest"; // Ejemplo CurrencyAPI
    // private static final String API_URL = "https://openexchangerates.org/api/latest.json"; // Ejemplo Open Exchange Rates

    // Mapa para almacenar los nombres de las monedas. Esto hace que el código sea más fácil de leer y mantener.
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

    // Método para obtener la opción del usuario con validación de entrada.
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
            if (opcion < 1 || opcion > 7) {
                System.out.println("Opción inválida. Por favor, ingrese un número del 1 al 7.");
            }
        }
        return opcion;
    }

    // Método para realizar la conversión de moneda.
    public static void convertir(String monedaOrigen, String monedaDestino, Scanner scanner) {
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

    // Método para obtener la tasa de cambio de la API.
    public static double obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) throws IOException {
        // Construye la URL de la API para la conversión específica.
        // String urlString = API_URL + "?base=" + monedaOrigen + "&symbols=" + monedaDestino + "&apikey=" + API_KEY; // Para
        // fixer.io
        // String urlString = API_URL + "?base_currency=" + monedaOrigen + "&currencies="
        // + monedaDestino + "&api_key=" + API_KEY; //CurrencyAPI
        String urlString = API_URL + monedaOrigen;
        URL url = new URL(urlString); // Crea un objeto URL con la URL construida.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Abre una conexión HTTP a la URL.
        connection.setRequestMethod("GET"); // Establece el método de la petición como GET.

        // Lee la respuesta de la API.
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line); // Agrega cada línea de la respuesta al StringBuilder.
            }
        } finally {
            connection.disconnect(); // Cierra la conexión HTTP.
        }

        // Parsea la respuesta JSON para obtener la tasa de cambio.
        JSONObject jsonResponse = new JSONObject(response.toString());
        // La estructura del JSON depende de la API que uses. Aquí hay ejemplos para algunas APIs comunes.
        double tasaCambio = 0.0;

        if (API_URL.contains("exchangerate-api")) {
            JSONObject rates = jsonResponse.getJSONObject("data").getJSONObject(monedaDestino);
            tasaCambio = rates.getDouble("value");
        } else if (API_URL.contains("currencyapi")) {
            tasaCambio = jsonResponse.getJSONObject("data").getJSONObject(monedaDestino).getDouble("value");
        } else if (API_URL.contains("openexchangerates")) {
            JSONObject rates = jsonResponse.getJSONObject("rates");
            tasaCambio = rates.getDouble(monedaDestino);
        } else {
            throw new IOException(
                    "API no soportada o respuesta JSON no reconocida. Por favor, actualice el código para manejar la estructura de la respuesta de la API.");
        }
        return tasaCambio;
    }

    // Método para obtener el nombre completo de una moneda a partir de su código.
    public static String obtenerNombreMoneda(String codigoMoneda) {
        return NOMBRE_MONEDAS.getOrDefault(codigoMoneda, codigoMoneda); // Devuelve el nombre de la moneda o el código si
        // no se encuentra.
    }
}