package ait.chat.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {

    private Socket socket; // Сокет для подключения к серверу.
    private BufferedReader socketReader; // Для чтения данных, отправленных сервером.
    private PrintWriter socketWriter; // Для отправки данных серверу.
    private BufferedReader userInputReader; // Для чтения данных, введенных пользователем.
    private String clientName; // Имя клиента.
    public static void main(String[] args) {
        String serverHost = "127.0.0.1"; // IP-адрес сервера, с которым мы хотим установить соединение. Здесь используется localhost.
        int port = 9000; // Номер порта на сервере, к которому мы хотим подключиться. Должен быть больше 1023 и свободен.
        String clientName = "Client1"; // Имя клиента.
        ChatClient client = new ChatClient(serverHost, port, clientName); // Создание нового объекта класса ChatClient.
        client.start(); // Запуск чата.
    }

    public ChatClient(String serverHost, int port, String clientName) {
        try {
            socket = new Socket(serverHost, port); // Создание нового сокета для соединения с сервером.
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Получение входного потока сокета для чтения данных, отправленных сервером.
            socketWriter = new PrintWriter(socket.getOutputStream(), true); // Создание объекта PrintWriter для записи данных в выходной поток сокета.
            userInputReader = new BufferedReader(new InputStreamReader(System.in)); // Создание объекта BufferedReader для чтения данных, введенных пользователем.
            this.clientName = clientName; // Установка имени клиента.
        } catch (IOException e) {
            e.printStackTrace(); // Вывод информации об исключении.
        }
    }

    public void start() {
        // Поток для чтения сообщений от сервера.
        Thread readThread = new Thread() {
            public void run() {
                String response;
                try {
                    while ((response = socketReader.readLine()) != null) {
                        System.out.println("Server response: " + response); // Вывод ответа от сервера.
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Вывод информации об исключении.
                }
            }
        };

        // Поток для отправки сообщений серверу.
        Thread writeThread = new Thread() {
            public void run() {
                String message;
                try {
                    while (true) {
                        System.out.println("Enter your message or type 'exit' for quit"); // Вывод сообщения, предлагающего пользователю ввести сообщение или ввести 'exit' для выхода.
                        message = userInputReader.readLine(); // Чтение введенного пользователем сообщения.
                        if ("exit".equalsIgnoreCase(message)) { // Если пользователь ввел 'exit', цикл прерывается.
                            break;
                        }
                        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date()); // Получение текущего времени.
                        socketWriter.println(clientName + " (" + timeStamp + "): " + message); // Отправка сообщения серверу с именем клиента и временем.
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Вывод информации об исключении.
                }
            }
        };

        readThread.start(); // Запуск потока чтения.
        writeThread.start(); // Запуск потока записи.

        try {
            readThread.join(); // Ожидание завершения потока чтения.
            writeThread.join(); // Ожидание завершения потока записи.
        } catch (InterruptedException e) {
            e.printStackTrace(); // Вывод информации об исключении.
        }
    }


}
