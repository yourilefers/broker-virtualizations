package org.opendoors.gemini.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Constants;
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
        if(config.get(Constants.CONFIG_ORION_URL, "").isEmpty()) {
            throw new Exception("The url of the Orion server has not been defined in config.conf. Please add 'orion_url' to your config.");
        } else {
            url = config.get(Constants.CONFIG_ORION_URL);
        }

        // Try to connect
        connect();

        // Setup arrays
        types = new ArrayList<>();

    }

    /**
     * Try to connect to the Orion server.
     *
     * @throws IOException
     */
    private void connect() throws IOException {

        // Try to connect
        String connectResult = new NetworkHelper(url + "/version").response();
        Logger.debug("Orion connect test result: " + connectResult);
        if(connectResult.isEmpty()) {
            throw new IOException("Orion did not respond correctly!");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(connectResult).optJSONObject("orion");
        Logger.info("Found Orion version " + jsonObject.optString("version"));

    }

    /**
     * Index all types.
     *
     * @throws IOException
     */
    public void indexTypes() throws IOException {

        // Retrieve all types
        String connectResult = new NetworkHelper(url + "/v1/contextEntities").response();
        Logger.debug("Orion index types result:\n" + new JSONObject(connectResult).toString(4));
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

        // The JSON object to post
        JSONObject dataToPost = new JSONObject();

        // Add the reference
        dataToPost.put("reference", config.get(Constants.CONFIG_SERVER_URL, Constants.CONFIG_SERVER_URL_DEFAULT) + ":" + config.get(Constants.CONFIG_SERVER_PORT, Integer.toString(Constants.CONFIG_SERVER_PORT_DEFAULT)));

        // Add the duration
        dataToPost.put("duration", "P1M");

        // Throttling; how many calls per minute/second/etc.
        dataToPost.put("throttling", "PT0S");

        // Get the list of valid types
        ArrayList<String> validTypes = getValidTypes();

        // Add all entity types
        JSONArray entities = new JSONArray();
        JSONArray attributes = new JSONArray();
        for(int i = 0; i < types.size(); i++) {

            // Get the type
            String type = types.get(i).optString("type");

            // In the list?
            if(validTypes.size() == 0 || (validTypes.size() > 0 && validTypes.contains(type))) {

                // Add the thing
                JSONObject entity = new JSONObject();
                entity.put("type", type);
                entity.put("isPattern", types.get(i).optString("isPattern"));
                entity.put("id", types.get(i).optString("id"));
                entities.put(entity);

                // Add the list of attributes
                addAttributes(types.get(i).optJSONArray("attributes"), attributes);

            }
        }
        dataToPost.put("entities", entities);
        dataToPost.put("attributes", attributes);

        // Setup notify condition
        dataToPost.put("notifyConditions", new JSONArray()
                        .put(new JSONObject()
                                        .put("type", "ONCHANGE")
                                        .put("condValues", attributes)
                        )
        );

        Logger.debug("Data to post:\n" + dataToPost.toString(4));

        // Subscribe to Orion
        String result = new NetworkHelper(url + "/v1/subscribeContext")
                .setPost()
                .setHeader("Content-Type", "application/json")
                .setPostData(dataToPost.toString())
                .response();
        Logger.info("Subscribed to Orion");
        Logger.debug("Orion answer:\n" + result);

        // Retrieve subscription ID
        JSONObject subscriptionObject = new JSONObject(result);
        subscriptionId = subscriptionObject.optJSONObject("subscribeResponse").optString("subscriptionId");
        Logger.debug("Orion subscription ID: " + subscriptionId);
        return false;

    }

    /**
     * Add attributes to an existing list of attributes.
     *
     * @param attributes
     * @param existing
     * @return
     */
    private JSONArray addAttributes(JSONArray attributes, JSONArray existing) {
        ArrayList<String> existingArray = new ArrayList<>();
        for(int i = 0; i < existing.length(); i++) {
            existingArray.add(existing.getString(i));
        }
        for(int i = 0; i < attributes.length(); i++) {

            // Get the attribute
            String attribute = attributes.optJSONObject(i).optString("name");

            // Added?
            if(!existingArray.contains(attribute)) {
                existing.put(attribute);
            }

        }
        return existing;
    }

    /**
     * Retrieve the list of valid types.
     *
     * @return
     */
    private ArrayList<String> getValidTypes() {

        // The arraylist with the set of types
        ArrayList<String> types = new ArrayList<>();

        // Get the config
        String configTypesRaw = config.get(Constants.CONFIG_ORION_ENTITIES, "");

        // Check
        if(!configTypesRaw.isEmpty()) {

            // Split
            String[] configTypesSplitted = configTypesRaw.split(",\\s*");

            // Add all types
            for(int i = 0; i < configTypesSplitted.length; i++) {
                types.add(configTypesSplitted[i]);
            }

        }

        return types;

    }

    /**
     * Unsubscribe Orion.
     * @return
     * @throws IOException
     */
    public boolean unsubscribe() throws IOException {
        return unsubscribe(subscriptionId);
    }

    /**
     * Make Gemini subscribe on Orion.
     */
    public boolean unsubscribe(String subscriptionId) throws IOException {

        // The data to post
        JSONObject dataToPost = new JSONObject()
                .put("subscriptionId", subscriptionId);

        // Subscribe to Orion
        String result = new NetworkHelper(url + "/v1/unsubscribeContext")
                .setPost()
                .setHeader("Content-Type", "application/json")
                .setPostData(dataToPost.toString())
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

    /**
     * Get all type names
     *
     * @return
     */
    public ArrayList<String> typeNames() {

        // The list to return
        ArrayList<String> typeNames = new ArrayList<>();

        for(int i = 0; i < types.size(); i++) {

            // Get the type
            JSONObject type = types.get(i);

            // Add the name
            if(!typeNames.contains(type.optString("type"))) typeNames.add(type.optString("type"));

        }

        return typeNames;

    }

}
