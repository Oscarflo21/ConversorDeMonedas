import java.util.Scanner;


public class ConversorMonedaPrincipal { // Clase principal que maneja la interacción con el usuario.

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do { // ciclo que ejecuta hasta que el usuario elige salir.
            ConversorMoneda.mostrarMenu(); // Muestra el menú de opciones al usuario.
            opcion = ConversorMoneda.obtenerOpcion(scanner); // Obtiene la opción del usuario y valida la entrada.

            switch (opcion) {  // Estructura switch-case para manejar las diferentes opciones del menú.
                case 1:
                    ConversorMoneda.convertir("USD", "ARS", scanner); // Convierte de Dolar a Peso Argentino.
                    break;
                case 2:
                    ConversorMoneda.convertir("ARS", "USD", scanner); // Convierte de Peso Argentino a Dolar.
                    break;
                case 3:
                    ConversorMoneda.convertir("USD", "BRL", scanner); // Convierte de Dolar a Real Brasilero.
                    break;
                case 4:
                    ConversorMoneda.convertir("BRL", "USD", scanner); // Convierte de Real Brasilero a Dolar.
                    break;
                case 5:
                    ConversorMoneda.convertir("USD", "COP", scanner); // Convierte de Dolar a Peso Colombiano.
                    break;
                case 6:
                    ConversorMoneda.convertir("COP", "USD", scanner); // Convierte de Peso Colombiano a Dolar.
                    break;
                case 7:
                    System.out.println("Saliendo del conversor de monedas."); // Mensaje si se sale del programa
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente de nuevo."); // No debería llegar aquí, pero se incluye por precaución.
            }
        } while (opcion != 7); // Continúa el bucle hasta que el usuario elige la opción 7.

        scanner.close(); // Cierra el scanner para liberar recursos.
    }
}