package org.opendoors.gemini.network;

import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class NetworkHelper {

    // The connection object
    private HttpURLConnection con;

    /**
     * Initial setup
     *
     * @param url
     * @throws IOException
     */
    public NetworkHelper(String url) throws IOException {

        // Create the URL object
        URL obj = new URL(url);

        // Setup connection
        con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", Config.getInstance().get("user_agent", Constants.NETWORK_USER_AGENT));
        con.setRequestProperty("Accept", "application/json");

    }

    /**
     * Make a POST request.
     *
     * @return
     * @throws ProtocolException
     */
    public NetworkHelper setPost() throws ProtocolException {

        // optional default is GET
        con.setRequestMethod("POST");
        return this;

    }

    /**
     * Make a POST request.
     *
     * @return
     * @throws ProtocolException
     */
    public NetworkHelper setHeader(String key, String value) throws ProtocolException {

        // optional default is GET
        con.setRequestProperty(key, value);
        return this;

    }

    /**
     * Set POST data.
     *
     * @param data
     * @return
     * @throws IOException
     */
    public NetworkHelper setPostData(String data) throws IOException {

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();
        return this;

    }

    /**
     * Get the status code.
     *
     * @return
     */
    public int statusCode() throws IOException {
        return con.getResponseCode();
    }

    /**
     * Get the response.
     *
     * @return
     * @throws IOException
     */
    public String response() throws IOException {

        // Setup buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        // Setup output
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();

        // Return the response
        return response.toString();

    }

}
