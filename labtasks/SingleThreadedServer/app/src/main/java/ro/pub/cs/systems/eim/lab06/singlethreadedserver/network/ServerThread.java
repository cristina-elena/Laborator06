package ro.pub.cs.systems.eim.lab06.singlethreadedserver.network;

import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ro.pub.cs.systems.eim.lab06.singlethreadedserver.general.Constants;
import ro.pub.cs.systems.eim.lab06.singlethreadedserver.general.Utilities;

public class ServerThread extends Thread {

    private boolean isRunning;

    private ServerSocket serverSocket;
    private EditText serverTextEditText;

    public ServerThread(EditText serverEditText) {
        this.serverTextEditText = serverEditText;
    }

    public void startServer() {
        isRunning = true;
        start();
        Log.v(Constants.TAG, "startServer() method invoked " + serverSocket);
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        Log.v(Constants.TAG, "stopServer() method invoked");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());

                // TODO exercise 5c
                // simulate the fact the communication routine between the server and the client takes 3 seconds
                // by putting the current thread to sleep for 3 seconds
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptedException) {
                    Log.e(Constants.TAG, "An exception has occurred: " + interruptedException.getMessage());
                    if (Constants.DEBUG) {
                        interruptedException.printStackTrace();
                    }
                }

                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(serverTextEditText.getText().toString());
                socket.close();
                Log.v(Constants.TAG, "Connection closed");

                // TODO exercise 5d
                // move the communication routine between the server and the client on a separate thread (each)
                // by creating a new CommunicationThread instance and passing the socket as parameter
                // start the thread
                CommunicationThread communicationThread = new CommunicationThread(socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    private class CommunicationThread extends Thread{
        private Socket socket;

        public CommunicationThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Log.v(Constants.TAG, "Connection opened with "+socket.getInetAddress()+":"+socket.getLocalPort());
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(serverTextEditText.getText().toString());
                socket.close();
                Log.v(Constants.TAG, "Connection closed");
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: "+ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
