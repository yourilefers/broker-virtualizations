package org.opendoors.gemini.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.Gemini;
import org.opendoors.gemini.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Client extends Thread {

    // The socket being used by the server
    private Socket socket;

    // Reader for input from the client
    private BufferedReader in;

    //
    // Functions
    //

    /**
     * Public constructor
     *
     * @param socket
     * @throws IOException
     */
    public Client( Socket socket ) throws IOException {

        // Save the socket
        this.socket = socket;

        // Setup
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
            while (true) {

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

                }

            }

        } catch ( Exception e ) {

            // ERROR
            Logger.error("SERVER : CLIENT : Exception occurred: " + e.getLocalizedMessage());

        } finally {

            try {

                // Close socket
                this.socket.close();

            } catch (IOException e) {

                // ERROR
                Logger.error("SERVER : CLIENT : Could not close the connections: " + e.getLocalizedMessage());

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

        //Logger.debug("SERVER : CLIENT : Parsing request:\n" + request);

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

        //Logger.debug("SERVER : CLIENT : Request body:\n" + body.toString(4));

        // Check the subscription ID
        if(!body.optString("subscriptionId", "").equals(Gemini.getInstance().getOrion().getSubscriptionId())) {
            Logger.error("SERVER : CLIENT : REJECTED : Invalid subscription ID");
            return;
        }

        // Alright, let's publish the stuff
        JSONArray responses = body.optJSONArray("contextResponses");
        for(int i = 0; i < responses.length(); i++) {
            Gemini.getInstance().getCkan().publish(responses.getJSONObject(i).optJSONObject("contextElement"));
        }

    }

}