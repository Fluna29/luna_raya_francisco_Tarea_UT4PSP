package ejercicio_1;

import java.io.*;
import java.net.*;

public class Servidor extends Thread{

    //Contador de clientes conectados
    private static int contadorClientes = 0;
    private int idCliente;

    //Socket del cliente y puerto a conectarse
    Socket skCliente;
    static final int PUERTO = 2000;

    //Constructor
    public Servidor (Socket sCliente){
        skCliente = sCliente;
        idCliente = ++contadorClientes;
    }

    //Creación de un servidor que escucha en el puerto 2000
    public static void main (String[] args){
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true){
                Socket skCliente = servidor.accept();
                System.out.println("Cliente " + (contadorClientes + 1) + " conectado desde " + skCliente.getInetAddress());
                //Manejo la conexión de un nuevo hilo
                new Servidor(skCliente).start();
            }
        }catch (IOException e){
            System.out.println("Error en el servidor: " + e);
        }
    }

    //Función run que se ejecuta al iniciar el hilo, contiene la lógica del servidor
    public void run(){
        try {
            InputStream in = skCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);

            OutputStream out = skCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);

            //Envío un mensaje al cliente
            flujo_salida.writeUTF("¡Bienvenido al servidor! Adivina el número entre 0 y 100");

            //El servidor genera un número aleatorio entre 0 y 100, ambos inclusive
            int numRandom = (int) (Math.random() * 101);
            System.out.println("Número aleatorio generado: " + numRandom);

            boolean numCorrecto = false;
            int numCliente = 0;
            //Creo un bucle para que el servidor esté siempre escuchando
            while (!numCorrecto){
                numCliente = flujo_entrada.readInt();
                System.out.println("Número recibido del cliente: " + numCliente);

                //Comparo el número del cliente con el número aleatorio

                if (numCliente == numRandom) {
                    try {
                        //En este caso el número del servidor es igual al del cliente
                        flujo_salida.writeUTF("¡Enhorabuena! Has acertado el número");
                        numCorrecto = true;
                    } catch (IOException e) {
                        System.out.println("Error al enviar mensaje 'enhorabuena' desde Servidor : " + e.getMessage());
                    }

                }else if(numCliente > numRandom) {
                    //En este caso el número del servidor es menor que el del cliente
                    try {

                        flujo_salida.writeUTF("El número es menor que " + numCliente);
                    } catch (IOException e) {
                        System.out.println("Error al enviar mensaje 'menor que' desde Servidor: " + e.getMessage());
                    }
                }else{
                    //En este caso el número del servidor es mayor que el número del cliente
                    try {

                        flujo_salida.writeUTF("El número es mayor que " + numCliente);
                    } catch (IOException e) {
                        System.out.println("Error al enviar mensaje 'mayor que' desde Servidor: " + e.getMessage());
                    }
                }
            }

            //Cierro la conexión con el cliente
            synchronized (Servidor.class) {
                skCliente.close();
                System.out.println("Cliente " + idCliente + " desconectado");
            }


        } catch (IOException e) {
            System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
        }
    }


}


