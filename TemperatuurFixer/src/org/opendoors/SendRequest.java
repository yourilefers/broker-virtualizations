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
    private CreateObject itc, station, glasbak;

    public SendRequest() {
        httpClient = HttpClientBuilder.create().build(); //Use this instead
        itc = new CreateObject(0, (float) 52.223852, (float) 6.885874);
        station = new CreateObject(1, (float) 52.222387, (float) 6.890195);
        glasbak = new CreateObject(2, (float) 52.219623, (float) 6.889610);
    }

    /**
     * Do a request to Orion for a given object
     * @param item (which sensor needs to be updated)
     */
    public void doRequest(String item) {
        HttpPost postRequest = new HttpPost(Main.REQUEST_URL);
        StringEntity input = null;
        try {
            if(item.equals(Main.ITC))
                input = new StringEntity(itc.newObject());
            else if(item.equals(Main.STATION))
                input = new StringEntity(station.newObject());
            else
                input = new StringEntity(glasbak.newObject());

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

            if(Main.DEBUG) {
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    totalOutput.append(output);
                }
                System.out.println(totalOutput.toString());
            }

            br.close();

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
