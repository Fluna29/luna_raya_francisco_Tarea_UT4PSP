package ejercicio_3;

import java.io.*;
import java.net.*;

public class Servidor extends Thread {

    //Contador de clientes conectados
    private static int contadorClientes = 0;
    private int idCliente;

    //Socket del cliente y puerto a conectarse
    Socket skCliente;
    static final int PUERTO = 1500;

    private int estado = 0;

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
                new ejercicio_3.Servidor(skCliente).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    public void run() {
        try{
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());

            String user = "admin";
            String password = "pwd secreta";

            boolean sesionIniciada = false;
            while (!sesionIniciada){
                estado = 1;
                // Autenticación
                flujo_salida.writeUTF("Introduce tu usuario: ");
                String usuarioCliente = flujo_entrada.readUTF();
                flujo_salida.writeUTF("Introduce tu contraseña: ");
                String passwordCliente = flujo_entrada.readUTF();

                if (!usuarioCliente.equals(user) || !passwordCliente.equals(password)) {
                    flujo_salida.writeUTF("ERROR: Usuario o contraseña incorrectos.");
                    System.out.println("Cliente " + idCliente + " no autenticado.");
                }else{
                    flujo_salida.writeUTF("Autenticación correcta.");
                    System.out.println("Cliente " + idCliente + " autenticado.");
                    sesionIniciada = true;
                }
            }

            do {
                estado = 2;
                flujo_salida.writeUTF("\nIntroduce el comando (ls/get/exit): ");
                String comando = flujo_entrada.readUTF();

                switch (comando) {
                    case "ls" -> {
                        estado = 3;
                        System.out.println("El cliente " + idCliente + " ha utilizado el comando ls.");
                        flujo_salida.writeUTF("Listado de archivos en el directorio del servidor:");
                        File directorio = new File(".\\Ejercicio_4.3\\");
                        File[] archivos = directorio.listFiles();
                        if (archivos != null) {
                            for (File archivo : archivos) {
                                flujo_salida.writeUTF(archivo.getName());
                            }
                        }
                        flujo_salida.writeUTF("FIN_LISTA"); // Marcador para finalizar la lista de archivos
                    }
                    case "get" -> {
                        estado = 4;
                        System.out.println("El cliente " + idCliente + " ha utilizado el comando get.");
                        flujo_salida.writeUTF("\nIntroduce el nombre del archivo que deseas recibir:");
                        String nombreArchivo = flujo_entrada.readUTF();

                        String filePath = ".\\Ejercicio_4.2\\ficheros_prueba\\" + nombreArchivo;
                        File archivo = new File(filePath);

                        estado=5;
                        if (archivo.exists() && archivo.isFile()) {
                            flujo_salida.writeUTF("\nArchivo encontrado. Enviando contenido...\n");
                            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                                String linea;
                                while ((linea = lector.readLine()) != null) {
                                    flujo_salida.writeUTF(linea);
                                }
                            }
                            flujo_salida.writeUTF("FIN_ARCHIVO"); // Marcador de fin de archivo
                            System.out.println("Archivo enviado correctamente al cliente: " + idCliente);
                        } else {
                            flujo_salida.writeUTF("ERROR: El archivo no existe.");
                            System.out.println("Archivo no encontrado.");
                        }
                    }
                    case "exit" -> {
                        flujo_salida.writeUTF("\nDesconexión del servidor.");
                        estado = -1; // Salir del bucle
                    }
                    default -> System.out.println("Comando no reconocido.");
                }
            } while (estado != -1);


            //Cierro la conexión con el cliente
            synchronized (ejercicio_3.Servidor.class) {
                skCliente.close();
                System.out.println("Cliente " + idCliente + " desconectado");
            }
        } catch (IOException e) {
            System.out.println("Error al manejar al cliente: " + e.getMessage());
        }
    }

}
