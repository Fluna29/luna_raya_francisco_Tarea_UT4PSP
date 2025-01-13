package ejercicio_2;

import java.io.*;
import java.net.*;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 1500;

    public void conectar() {
        try (Socket socket = new Socket(HOST, PUERTO)) {
            System.out.println("Conectado al servidor en el puerto " + PUERTO);
            manejarComunicacion(socket);
        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }

    private void manejarComunicacion(Socket socket) {
        try {
            DataOutputStream flujo_salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream flujo_entrada = new DataInputStream(socket.getInputStream());

            // Solicitar nombre del archivo al usuario
            BufferedReader lectorUsuario = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introduce el nombre del archivo que deseas recibir: ");
            String nombreArchivo = lectorUsuario.readLine();

            flujo_salida.writeUTF(nombreArchivo);

            // Leer respuesta del servidor
            boolean archivoRecibido = false;
            while (!archivoRecibido) {
                String mensajeServidor = flujo_entrada.readUTF();

                if (mensajeServidor.equals("FIN")) {
                    System.out.println("Fin del archivo recibido.");
                    archivoRecibido = true;
                } else {
                    System.out.println(mensajeServidor);
                    if (mensajeServidor.startsWith("ERROR")) {
                        archivoRecibido = true; // Terminar si hay error
                    }
                }
            }
        } catch (EOFException e) {
            // Esto ocurre si el servidor cierra el flujo mientras el cliente intenta leer.
            System.out.println("Conexión cerrada por el servidor.");
        } catch (IOException e) {
            System.out.println("Error en la comunicación con el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.conectar();
    }
}
