package org.opendoors.gemini.server;

import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Constants;
import org.opendoors.gemini.common.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Server extends Thread {

    /** The list of connected clients */
    private ArrayList<Client> clients;

    /** The server socket */
    private ServerSocket serverSocket;

    /** The configuration instance */
    private Config config;

    /**
     * Server constructor.
     */
    public Server() {

        // Setup vars
        config = Config.getInstance();

        // Setup list of clients
        clients = new ArrayList<>();

    }

    /**
     * Setup the server.
     */
    @Override
    public void run() {

        try {

            // Get the port number
            int port = Integer.parseInt(config.get(Constants.CONFIG_SERVER_PORT, Integer.toString(Constants.CONFIG_SERVER_PORT_DEFAULT)));

            // Create new server socket
            serverSocket = new ServerSocket(port);

            Logger.debug("SERVER : Listening on port " + port + "...");

            try {

                while (!isInterrupted() && !Config.getInstance().isExiting()) {

                    // Create new client
                    Client client = new Client(serverSocket.accept(), this);

                    // Create new client
                    clients.add(client);

                    Logger.debug("SERVER : New client added");

                    // Start client
                    client.start();

                }

            } catch(SocketException e) {

                // Debug
                Logger.debug("Socket closed while waiting for accept.");

            } finally {

                // Close all existing clients
                for(int i = 0; i < clients.size(); i++) {
                    if(!clients.get(i).isInterrupted()) clients.get(i).interrupt();
                }

                try {

                    // Close socket
                    if(!serverSocket.isClosed()) serverSocket.close();

                } catch (IOException e) {

                    // Error
                    Logger.error("SERVER : Could not create/close socket: " + e.getLocalizedMessage());
                    Logger.error("SERVER :\n" + Arrays.toString(e.getStackTrace()));

                }

            }

        } catch (IOException e) {

            // Error
            Logger.error("SERVER : IO exception: " + e.getLocalizedMessage());
            Logger.error("SERVER :\n" + Arrays.toString(e.getStackTrace()));

        }

    }

    /**
     * Remove a client from the list of clients.
     * @param client
     */
    public void removeClient(Client client) {
        if(!client.isInterrupted()) client.interrupt();
        clients.remove(client);
    }

    /**
     * Interrupts this thread.
     * <p>
     * <p> Unless the current thread is interrupting itself, which is
     * always permitted, the {@link #checkAccess() checkAccess} method
     * of this thread is invoked, which may cause a {@link
     * SecurityException} to be thrown.
     * <p>
     * <p> If this thread is blocked in an invocation of the {@link
     * Object#wait() wait()}, {@link Object#wait(long) wait(long)}, or {@link
     * Object#wait(long, int) wait(long, int)} methods of the {@link Object}
     * class, or of the {@link #join()}, {@link #join(long)}, {@link
     * #join(long, int)}, {@link #sleep(long)}, or {@link #sleep(long, int)},
     * methods of this class, then its interrupt status will be cleared and it
     * will receive an {@link InterruptedException}.
     * <p>
     * <p> If this thread is blocked in an I/O operation upon an {@link
     * InterruptibleChannel InterruptibleChannel}
     * then the channel will be closed, the thread's interrupt
     * status will be set, and the thread will receive a {@link
     * ClosedByInterruptException}.
     * <p>
     * <p> If this thread is blocked in a {@link Selector}
     * then the thread's interrupt status will be set and it will return
     * immediately from the selection operation, possibly with a non-zero
     * value, just as if the selector's {@link
     * Selector#wakeup wakeup} method were invoked.
     * <p>
     * <p> If none of the previous conditions hold then this thread's interrupt
     * status will be set. </p>
     * <p>
     * <p> Interrupting a thread that is not alive need not have any effect.
     *
     * @throws SecurityException if the current thread cannot modify this thread
     * @revised 6.0
     * @spec JSR-51
     */
    @Override
    public void interrupt() {
        try {

            // Stop the server
            if(!serverSocket.isClosed()) serverSocket.close();

        } catch(IOException e) {

            // Error
            Logger.error("SERVER | Could not close the server socket on interrupt: " + e.getLocalizedMessage());
            Logger.error("SERVER |\n" + Arrays.toString(e.getStackTrace()));

        }

        // Stop all clients
        for(int i = 0; i < clients.size(); i++) {
            if(!clients.get(i).isInterrupted()) clients.get(i).interrupt();
        }

        // Continue interrupt
        super.interrupt();

    }

}
