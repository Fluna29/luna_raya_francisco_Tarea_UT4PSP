package ejercicio_2;

import java.io.*;
import java.net.*;

public class Servidor {
    static final int PUERTO = 1500;

    public void iniciarServidor() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde " + cliente.getInetAddress());

                manejarCliente(cliente);
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    private void manejarCliente(Socket cliente) {
        try (cliente) {
            DataInputStream flujo_entrada = new DataInputStream(cliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(cliente.getOutputStream());

            // Recibir nombre del archivo solicitado
            String nombreArchivo = flujo_entrada.readUTF();
            System.out.println("El cliente desea recibir el archivo: " + nombreArchivo);

            // Ruta del archivo
            String filePath = ".\\Ejercicio_3.2\\ficheros_prueba\\" + nombreArchivo;
            File archivo = new File(filePath);

            if (archivo.exists() && archivo.isFile()) {
                flujo_salida.writeUTF("Archivo encontrado. Enviando contenido...");
                try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        flujo_salida.writeUTF(linea);
                    }
                }
                flujo_salida.writeUTF("\nFIN"); // Marcador de fin de archivo
                System.out.println("Archivo enviado correctamente.");
            } else {
                flujo_salida.writeUTF("\nERROR: El archivo no existe.");
                System.out.println("Archivo no encontrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al manejar al cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciarServidor();
    }
}
