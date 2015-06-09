package org.opendoors;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Ruben on 28-5-2015.
 */
public class SendRequest {

    // The client to use
    private HttpClient httpClient;

    public SendRequest() {

        // Setup http client
        httpClient = HttpClientBuilder
                .create()
                .build();

    }

    /**
     * Do a request to Orion for a given object
     * @param road (which sensor needs to be updated)
     */
    public void doRequest(Road road) {

        // Setup post request
        HttpPost postRequest = new HttpPost(Main.REQUEST_URL);

        try {

            // Setup input
            StringEntity input = new StringEntity(road.newObject());

            // May I make a request?
            if(Main.NETWORK_REQUEST) {
                try {

                    // Set content type
                    input.setContentType("application/json");
                    postRequest.setEntity(input);

                    // Make the request
                    HttpResponse response = httpClient.execute(postRequest);

                    // Request was not successful
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatusLine().getStatusCode());
                    }

                    // Debug
                    if (Main.DEBUG) {
                        try {

                            // Setup reader
                            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                            StringBuilder totalOutput = new StringBuilder();
                            String output = null;

                            // Print output
                            System.out.println("Output from Server .... \n");
                            while ((output = br.readLine()) != null) {
                                totalOutput.append(output);
                            }
                            System.out.println(totalOutput.toString());

                            // Close reader
                            br.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch(IOException e) {
                    System.out.println("Could not make the POST request!");
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {

            // Release
            postRequest.releaseConnection();

        }
    }
}
