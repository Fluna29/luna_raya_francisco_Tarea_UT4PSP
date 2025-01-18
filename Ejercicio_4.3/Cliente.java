package ejercicio_3;

import java.io.*;
import java.net.*;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 1500;

    public Cliente() {
        try (Socket socket = new Socket(HOST, PUERTO)) {
            System.out.println("Conectado al servidor en el puerto " + PUERTO);
            try {
                DataOutputStream flujo_salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream flujo_entrada = new DataInputStream(socket.getInputStream());

                BufferedReader lectorUsuario = new BufferedReader(new InputStreamReader(System.in));

                //Introducimos el usuario y la contraseña
                boolean sesionIniciada = false;
                while (!sesionIniciada) {
                    //Recibo mensaje del servidor para introducir el usuario
                    System.out.println(flujo_entrada.readUTF());
                    String usuario = lectorUsuario.readLine();
                    flujo_salida.writeUTF(usuario);

                    //Recibo mensaje del servidor para introducir la contraseña
                    System.out.println(flujo_entrada.readUTF());
                    String password = lectorUsuario.readLine();
                    flujo_salida.writeUTF(password);

                    //Recibo mensaje del servidor para saber si la autenticación ha sido correcta
                    String mensaje = flujo_entrada.readUTF();
                    if (mensaje.equals("Autenticación correcta.")) {
                        sesionIniciada = true;
                    } else {
                        System.out.println(mensaje);
                    }
                }

                boolean salir = false;
                while (!salir) {
                    System.out.println(flujo_entrada.readUTF());
                    String opcion = lectorUsuario.readLine();
                    flujo_salida.writeUTF(opcion);
                    switch (opcion) {
                        case "ls" -> {
                            System.out.println(flujo_entrada.readUTF()); // Mensaje inicial
                            String respuesta;
                            while (!(respuesta = flujo_entrada.readUTF()).equals("FIN_LISTA")) {
                                System.out.println(respuesta);
                            }
                        }

                        case "get" -> {
                            System.out.println(flujo_entrada.readUTF()); // Solicitud del servidor
                            String nombreArchivo = lectorUsuario.readLine();
                            flujo_salida.writeUTF(nombreArchivo);

                            String mensajeServidor;
                            while (!(mensajeServidor = flujo_entrada.readUTF()).equals("FIN_ARCHIVO")) {
                                System.out.println(mensajeServidor);
                                if (mensajeServidor.startsWith("ERROR")) {
                                    break; // Salir si hay un error
                                }
                            }
                        }

                        case "exit" -> {
                            System.out.println("\nDesconectando del servidor...");
                            salir = true;
                        }
                        default -> System.out.println("Comando no válido.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al usar comandos: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Cliente();
    }
}
