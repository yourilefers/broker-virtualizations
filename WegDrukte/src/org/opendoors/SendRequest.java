package org.opendoors;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
    private HttpClient httpClient;
    private CreateObject west, zuid, haaks, hengelose, deurninger;

    public SendRequest() {
        httpClient = HttpClientBuilder.create().build(); //Use this instead
        west = new CreateObject(org.opendoors.Main.ROAD_WESTERVAL, 80,(float) 52.218364, (float) 6.869316);
        zuid = new CreateObject(org.opendoors.Main.ROAD_ZUIDERVAL, 80, (float) 52.208590, (float) 6.889442);
        haaks = new CreateObject(org.opendoors.Main.ROAD_HAAKSBERGERSTRAAT, 80, (float) 52.210550, (float) 6.878260);
        hengelose = new CreateObject(org.opendoors.Main.ROAD_HENGELOSESTRAAT, 50, (float) 52.227166, (float) 6.878294);
        deurninger = new CreateObject(org.opendoors.Main.ROAD_DEURNINGERSTRAAT, 50, (float) 52.229364, (float) 6.889527);
    }

    /**
     * Do a request to Orion for a given object
     * @param item (which sensor needs to be updated)
     */
    public void doRequest(String item) {
        HttpPost postRequest = new HttpPost(org.opendoors.Main.REQUEST_URL);
        StringEntity input = null;
        try {
            switch (item) {
                case org.opendoors.Main.ROAD_WESTERVAL: input = new StringEntity(west.newObject()); break;
                case org.opendoors.Main.ROAD_ZUIDERVAL: input = new StringEntity(zuid.newObject()); break;
                case org.opendoors.Main.ROAD_HAAKSBERGERSTRAAT: input = new StringEntity(haaks.newObject()); break;
                case org.opendoors.Main.ROAD_HENGELOSESTRAAT: input = new StringEntity(hengelose.newObject()); break;
                default: input = new StringEntity(deurninger.newObject()); break;
            }

            if(org.opendoors.Main.NETWORK_REQUEST) {
                input.setContentType("application/json");
                postRequest.setEntity(input);

                HttpResponse response = httpClient.execute(postRequest);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String output;
                StringBuffer totalOutput = new StringBuffer();

                if (Main.DEBUG) {
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                        totalOutput.append(output);
                    }
                    System.out.println(totalOutput.toString());
                }

                br.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(postRequest != null)
                postRequest.releaseConnection();
        }
    }
}
