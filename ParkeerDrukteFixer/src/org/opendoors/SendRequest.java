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
    private CreateObject mst, ledeboer, heekplein, qEnschede, qStation, irene;

    public SendRequest() {
        httpClient = HttpClientBuilder.create().build(); //Use this instead
        mst = new CreateObject(Main.GARAGE_MST, 1000, (float) 52.215253, (float) 6.890297);
        ledeboer = new CreateObject(Main.GARAGE_LEDEBOER, 1000, (float) 52.216039, (float) 6.899552);
        heekplein = new CreateObject(Main.GARAGE_HEEKPLEIN, 1650, (float) 52.217144, (float) 6.898072);
        qEnschede = new CreateObject(Main.GARAGE_QPARK_ENSCHEDE, 1000, (float) 52.218925, (float) 6.892128);
        qStation = new CreateObject(Main.GARAGE_QPARK_STATION, 1000, (float) 52.221723, (float) 6.892316);
        irene = new CreateObject(Main.GARAGE_IRENE, 525, (float) 52.222361, (float) 6.898219);
    }

    /**
     * Do a request to Orion for a given object
     * @param item (which sensor needs to be updated)
     */
    public void doRequest(String item) {
        HttpPost postRequest = new HttpPost(Main.REQUEST_URL);
        StringEntity input = null;
        try {
            switch (item) {
                case Main.GARAGE_MST: input = new StringEntity(mst.newObject()); break;
                case Main.GARAGE_LEDEBOER: input = new StringEntity(ledeboer.newObject()); break;
                case Main.GARAGE_HEEKPLEIN: input = new StringEntity(heekplein.newObject()); break;
                case Main.GARAGE_QPARK_ENSCHEDE: input = new StringEntity(qEnschede.newObject()); break;
                case Main.GARAGE_QPARK_STATION: input = new StringEntity(qStation.newObject()); break;
                case Main.GARAGE_IRENE: input = new StringEntity(irene.newObject()); break;
            }

            if(Main.NETWORK_REQUEST) {
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
