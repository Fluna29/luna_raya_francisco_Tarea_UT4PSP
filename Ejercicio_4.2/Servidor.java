package ejercicio_2;

import java.io.*;
import java.net.*;

public class Servidor extends Thread {

    //Contador de clientes conectados
    private static int contadorClientes = 0;
    private int idCliente;

    //Socket del cliente y puerto a conectarse
    Socket skCliente;
    static final int PUERTO = 1500;

    public Servidor (Socket scliente) {
        skCliente = scliente;
        idCliente = ++contadorClientes;
    }

    public static void main(String[] args) {
        try {
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println("Escuchando en el puerto " + PUERTO);
            while (true) {
                Socket skCliente = skServidor.accept();
                System.out.println("Cliente " + (contadorClientes + 1) + " conectado desde " + skCliente.getInetAddress());
                new Servidor(skCliente).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    public void run() {
        try{
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());

            // Recibir nombre del archivo solicitado
            String nombreArchivo = flujo_entrada.readUTF();
            System.out.println("El cliente " + idCliente + " desea recibir el archivo: " + nombreArchivo);

            // Ruta del archivo
            String filePath = ".\\Ejercicio_4.2\\ficheros_prueba\\" + nombreArchivo;
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
                System.out.println("Archivo enviado correctamente al cliente: " + idCliente);
            } else {
                flujo_salida.writeUTF("\nERROR: El archivo no existe.");
                System.out.println("Archivo no encontrado.");
            }

            //Cierro la conexión con el cliente
            synchronized (ejercicio_2.Servidor.class) {
                skCliente.close();
                System.out.println("Cliente " + idCliente + " desconectado");
            }
        } catch (IOException e) {
            System.out.println("Error al manejar al cliente: " + e.getMessage());
        }
    }

}
