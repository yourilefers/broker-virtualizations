package org.opendoors.gemini.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Orion {

    /** The config file instance */
    private Config config;

    /** The Orion URL */
    private String url;

    /** The subscription ID */
    private String subscriptionId;

    /** The list of types */
    private ArrayList<JSONObject> types;

    /**
     * Orion constructor.
     *
     * @throws Exception
     */
    public Orion() throws Exception {

        // Get config
        config = Config.getInstance();

        // Setup orion
        if(config.get("orion_url", "").isEmpty()) {
            throw new Exception("The url of the Orion server has not been defined in config.conf. Please add 'orion_url' to your config.");
        } else {
            url = config.get("orion_url");
        }

        // Try to connect
        String connectResult = new NetworkHelper(url + "/version").response();
        Logger.debug("Orion connect test result: " + connectResult);
        if(connectResult.isEmpty()) {
            throw new IOException("Orion did not respond correctly!");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(connectResult).optJSONObject("orion");
        Logger.info("Found Orion version " + jsonObject.optString("version"));

        // Setup arrays
        types = new ArrayList<>();

    }

    /**
     * Index all types.
     *
     * @throws IOException
     */
    public void indexTypes() throws IOException {

        // Retrieve all types
        String connectResult = new NetworkHelper(url + "/v1/contextEntities").response();
        Logger.debug("Orion index types result: " + connectResult);
        if(connectResult.isEmpty()) {
            throw new IOException("Orion did not respond correctly!");
        }

        // Make a JSON object of it and retrieve the version
        JSONArray jsonArray = new JSONObject(connectResult).optJSONArray("contextResponses");
        for(int i = 0; i < jsonArray.length(); i++) {

            // Add the thing
            types.add(jsonArray.optJSONObject(i).optJSONObject("contextElement"));

        }
        Logger.debug("All types in Orion:\n" + Arrays.toString(types.toArray()));

    }

    /**
     * Make Gemini subscribe on Orion.
     */
    public boolean subscribe() throws IOException {

        // Add all entity types
        JSONArray entities = new JSONArray();
        for(int i = 0; i < types.size(); i++) {

            // Add the thing
            JSONObject entity = new JSONObject();
            entity.put("type", types.get(i).optString("type"));
            entity.put("isPattern", types.get(i).optString("isPattern"));
            entity.put("id", types.get(i).optString("id"));
            entities.put(entity);

        }

        // Subscribe to Orion
        String result = new NetworkHelper(url + "/v1/subscribeContext")
                .setPost()
                .setHeader("Content-Type", "application/json")
                .setPostData("{\n" +
                        "    \"entities\": " + entities.toString(4) + ",\n" +
                        //"    \"attributes\": [\n" +
                        //"        \"temperature\"\n" +
                        //"    ],\n" +
                        "    \"reference\": \"" + config.get("server_url", "http://localhost") + ":" + config.get("server_port", "2048") + "\",\n" +
                        "    \"duration\": \"P1M\",\n" +
                        "    \"notifyConditions\": [\n" +
                        "        {\n" +
                        "            \"type\": \"ONTIMEINTERVAL\",\n" +
                        "            \"condValues\": [\n" +
                        "                \"PT10S\"\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}")
                .response();
        Logger.info("Subscribed to Orion");
        Logger.debug("Orion answer: " + result);

        // Retrieve subscription ID
        JSONObject subscriptionObject = new JSONObject(result);
        subscriptionId = subscriptionObject.optJSONObject("subscribeResponse").optString("subscriptionId");
        Logger.debug("Orion subscription ID: " + subscriptionId);
        return false;

    }

    /**
     * Make Gemini subscribe on Orion.
     */
    public boolean unsubscribe() throws IOException {

        // Subscribe to Orion
        String result = new NetworkHelper(url + "/v1/unsubscribeContext")
                .setPost()
                .setHeader("Content-Type", "application/json")
                .setPostData("{\n" +
                        "  \"subscriptionId\": \"" + subscriptionId + "\"\n" +
                        "}")
                .response();
        Logger.info("Unsubscribed from Orion");
        Logger.debug(result);

        return false;
    }

    //
    // Getters
    //

    /**
     * Get the subscription ID.
     * @return
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

}
