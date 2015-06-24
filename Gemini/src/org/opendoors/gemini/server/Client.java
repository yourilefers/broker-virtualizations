package org.opendoors.gemini.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.Gemini;
import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Logger;
import org.opendoors.gemini.exceptions.CloseClientException;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Client extends Thread {

    // The server instance
    private Server server;

    // The socket being used by the server
    private Socket socket;

    // Reader for input from the client
    private BufferedReader in;

    // Reader for input from the client
    private PrintWriter out;

    //
    // Functions
    //

    /**
     * Public constructor
     *
     * @param socket
     * @throws IOException
     */
    public Client( Socket socket, Server server ) throws IOException {

        // Svae the server
        this.server = server;

        // Save the socket
        this.socket = socket;

        // Setup
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

    }

    /**
     * The runnable of the thread
     */
    @Override
    public void run() {

        // 1. Wacht op berichten van de client.
        // 2. Stuur berichten van de clients door naar de andere
        // clients.

        try {

            // The answer
            String answer = "";

            // Wait for incoming messages
            while(!this.isInterrupted() && !Config.getInstance().isExiting()) {

                // Wait for input
                String input = in.readLine();

                // Parse input
                if(input != null && !input.equals(null) && !input.equals("null")) {

                    // Add to answer
                    answer += "\n" + input;

                } else if(answer.length() > 0) {

                    // OK done?
                    parseRequest(answer);
                    answer = "";

                } else {

                    // Write back OK
                    out.println(0);
                    out.flush();
                    throw new CloseClientException("STOP SESSION");
                }

            }

        } catch (IOException e) {

            // ERROR
            Logger.error("SERVER : CLIENT : IO | Input read exception: " + e.getLocalizedMessage());
            Logger.error("SERVER : CLIENT : IO |\n" + Arrays.toString(e.getStackTrace()));

        } catch (CloseClientException e) {

            // ERROR
            Logger.debug("SERVER : CLIENT : Client connection closed");

        } finally {

            try {

                // Close all
                in.close();
                out.close();

                // Close socket
                if(!socket.isClosed()) socket.close();

            } catch (IOException e) {

                // ERROR
                Logger.error("SERVER : CLIENT : IO | Could not close the connections: " + e.getLocalizedMessage());
                Logger.error("SERVER : CLIENT : IO |\n" + Arrays.toString(e.getStackTrace()));

            }

            try {

                // Remove client
                server.removeClient(this);

            } catch(NullPointerException e) {

                // Oops?
                Logger.error("SERVER : CLIENT : NULL | Could not remove client: " + e.getLocalizedMessage());
                Logger.error("SERVER : CLIENT : NULL |\n" + Arrays.toString(e.getStackTrace()));

            }

            // Final
            Logger.debug("SERVER : CLIENT : Connection closed with client.");

        }

    }

    /**
     * Parse the request received from Orion.
     *
     * @param request
     */
    public void parseRequest(String request) {

        // Trim the thing
        request = request.trim();

        // Is it a valid POST request?
        if(!request.startsWith("POST / HTTP/1.1")) {
            Logger.error("SERVER : CLIENT : REJECTED : Invalid request type");
            return;
        }

        // JSON?
        else if(!request.contains("Content-type: application/json")) {
            Logger.error("SERVER : CLIENT : REJECTED : Invalid body type");
            return;
        }

        // Find the first empty line
        String[] splittedRequest = request.split("\n\n", 2);

        // Parse part 2 as an JSON object
        JSONObject body = new JSONObject(splittedRequest[1]);

        // Check the subscription ID
        if(!body.optString("subscriptionId", "").equals(Gemini.getInstance().getOrion().getSubscriptionId())) {
            try {

                Logger.debug("SERVER : CLIENT : REJECTED : Invalid subscription ID. Unsubscribing this id...");
                Gemini.getInstance().getOrion().unsubscribe(body.optString("subscriptionId", ""));

            } catch(IOException e) {

                // Debug
                Logger.debug("Could not unsubscribe the invalid ID | " + e.getLocalizedMessage());

            }

            return;
        }

        // Alright, let's publish the stuff
        JSONArray responses = body.optJSONArray("contextResponses");
        for(int i = 0; i < responses.length(); i++) {
            Gemini.getInstance().getCkan().publish(responses.getJSONObject(i).optJSONObject("contextElement"));
        }

    }

}
