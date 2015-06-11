package org.opendoors.gemini.server;

import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Server extends Thread {

    /** The config */
    private Config config;

    /** The server socket */
    private ServerSocket serverSocket;

    /** The list of connected clients */
    private ArrayList<Client> clients;

    /**
     * Setup the server.
     */
    @Override
    public void run() {

        // Setup vars
        config = Config.getInstance();

        // Setup list of clients
        clients = new ArrayList<>();

        try {

            // Get the port number
            int port = Integer.parseInt(config.get("server_port", "2048"));

            // Create new server socket
            serverSocket = new ServerSocket(port);

            Logger.debug("SERVER : Listening on port " + port + "...");

            try {

                while( true ) {

                    // Create new client
                    Client client = new Client(serverSocket.accept());

                    Logger.debug("SERVER : New client added");

                    // Create new client
                    clients.add(client);

                    // Start client
                    client.start();

                }

            } finally {

                // Close socket
                serverSocket.close();

            }

        } catch (IOException e) {

            // Error
            Logger.error("SERVER : Could not create socket");
            Logger.debug("SERVER : " + e.getLocalizedMessage());

        }

    }

}
