package ejercicio_1;

import java.io.*;
import java.net.*;
import java.io.InputStreamReader;

public class Cliente {

    private static final String HOST = "localhost";
    private static final int PUERTO = 2000;

    public Cliente(){
        try(Socket sCliente = new Socket(HOST, PUERTO)) {
            System.out.println("Conectado al servidor en el puerto " + PUERTO);
            // Flujo de salida (hacia el servidor)
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);

            // Flujo de entrada (desde el servidor)
            InputStream in = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);

            // Leer el mensaje de bienvenida del servidor
            String mensaje = flujo_entrada.readUTF();
            System.out.println("Mensaje del servidor: " + mensaje);

            boolean numCorrecto = false;  // Controla si se ha adivinado el número
            int numero = 0;              // Número introducido por el usuario

            while (!numCorrecto) {
                boolean numValido = false;

                // Solicitar un número válido al usuario
                while (!numValido) {
                    System.out.println("Introduce un número entre 0 y 100: (ambos inclusive)");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                    try {
                        numero = Integer.parseInt(br.readLine());
                    } catch (IOException | NumberFormatException e) {
                        System.out.println("Error al leer el número. Por favor, introduce un número válido.");
                        continue; // Reintentar en caso de error
                    }

                    if (numero >= 0 && numero <= 100) {
                        numValido = true;
                    } else {
                        System.out.println("Número no válido. Introduce un número entre 0 y 100.");
                    }
                }

                // Enviar el número al servidor
                flujo_salida.writeInt(numero);

                // Leer la respuesta del servidor
                String respuesta = flujo_entrada.readUTF();
                System.out.println("Respuesta del servidor: " + respuesta);

                // Si se adivina el número, salir del bucle
                if (respuesta.equals("¡Enhorabuena! Has acertado el número")) {
                    numCorrecto = true;
                }
            }
        }catch (IOException e){
            System.out.println("Error en el cliente: " + e);
        }
    }

    public static void main(String [] args){
        new Cliente();
    }
}