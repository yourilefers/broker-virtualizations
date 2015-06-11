package org.opendoors.gemini.sinks;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.common.Logger;
import org.opendoors.gemini.interfaces.Sink;
import org.opendoors.gemini.network.NetworkHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Ckan implements Sink {

    /** The organization of this user */
    private String organization;

    /** The API key of this user */
    private String apiKey;

    /** The list of known packages */
    private HashMap<String, String> packages;

    /**
     * Setup CKAN.
     *
     * @throws Exception
     */
    public void setup() throws Exception {

        // Setup orion
        if(url.isEmpty()) {
            throw new Exception("The url of the CKAN server has not been defined in config.conf. Please add 'ckan_url' to your config.");
        }

        // Try to connect
        String connectResult = new NetworkHelper(url + "/api/util/status")
                .response();
        Logger.debug("CKAN connect test result: " + connectResult);
        if(connectResult.isEmpty()) {
            throw new IOException("Ckan did not respond correctly!");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(connectResult);
        Logger.info("Found CKAN version " + jsonObject.optString("ckan_version"));

        // Find the organization
        if(config.get("ckan_organization", "").isEmpty()) {
            throw new Exception("No organization defined in the config file. Please add 'ckan_organization' to your config.");
        } else {
            organization = config.get("ckan_organization");
        }

        // Find the organization
        if(config.get("ckan_api_key", "").isEmpty()) {
            throw new Exception("No API key defined in the config file. Please add 'ckan_api_key' to your config.");
        } else {
            apiKey = config.get("ckan_api_key");
        }

        // Try to connect
        String organizations = new NetworkHelper(url + "/api/3/action/organization_list")
                .response();
        if(organizations.isEmpty()) {
            throw new IOException("Ckan did not return the list of organizations!");
        }

        // Make a JSON object of it and retrieve the version
        jsonObject = new JSONObject(organizations);
        JSONArray jsonOrgs = jsonObject.getJSONArray("result");

        // Does the org exist?
        boolean orgExists = false;
        for(int i = 0; i < jsonOrgs.length(); i++) {
            if(jsonOrgs.getString(i).equals(organization)) {
                orgExists = true;
            }
        }

        // Found?
        if(!orgExists) {

            // Create the organization
            String orgs = new NetworkHelper(url + "/api/3/action/organization_create")
                    .setPost()
                    .setHeader("Authorization", apiKey)
                    .setPostData("name=" + organization)
                    .response();

            // DEBUG
            Logger.debug(orgs);

        }

        // Setup packages
        packages = new HashMap<>();

        // Try to connect
        String packagesList = new NetworkHelper(url + "/api/3/action/current_package_list_with_resources")
                .response();
        if(packagesList.isEmpty()) {
            throw new IOException("Ckan did not return the list of packages!");
        }

        // Make a JSON object of it and retrieve the version
        jsonObject = new JSONObject(packagesList);
        JSONArray jsonPackagesList = jsonObject.getJSONArray("result");
        for(int i = 0; i < jsonPackagesList.length(); i++) {

            // Get the name
            String name = jsonPackagesList.optJSONObject(i).optString("name");

            // Get the ID
            String id = "";
            JSONArray resources = jsonPackagesList.optJSONObject(i).optJSONArray("resources");
            if(resources.length() > 0) {
                id = resources.optJSONObject(0).optString("id");
            }

            // Add it
            packages.put(name, id);

        }
        Logger.debug("CKAN packages: " + Arrays.toString(packages.keySet().toArray()));

    }

    /**
     * Publish a new result to Ckan.
     *
     * @param result The result to publish in the Orion format
     */
    public void publish(JSONObject result) {

        // Does the package exist?
        if (!packages.containsKey(result.optString("type"))) {
            try {

                // Create the package
                createPackage(result);

            } catch(IOException e) {

                // Oops?
                Logger.error("CKAN (" + e.getMessage() + ") : Could not create the package: " + e.getLocalizedMessage());

            }
            try {

                // Create the datastore
                createDatastore(packages.get(result.optString("type")), result);

            } catch(IOException e) {

                // Oops?
                Logger.error("CKAN : Could not create the data store: " + e.getLocalizedMessage());

            }
        }

        try {

            // Insert the thing
            insertData(packages.get(result.optString("type")), result);

        } catch(IOException e) {

            // Oops?
            Logger.error("CKAN : Could not insert the data: " + e.getLocalizedMessage());

        } catch(NullPointerException e) {

            // Oops?
            Logger.error("CKAN : INSERT : Null pointer: " + e.getLocalizedMessage());
            e.printStackTrace();

        }

    }

    /**
     * Create the package.
     *
     * @param object
     * @throws IOException
     */
    private void createPackage(JSONObject object) throws IOException {

        // Add the name
        JSONObject data = new JSONObject();
        data.put("name", object.optString("type").toLowerCase());

        Logger.debug("CKAN : CREATE PACKAGE : Data to insert: \n" + data.toString(4));

        // Create the organization
        String orgs = new NetworkHelper(url + "/api/3/action/package_create")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(data.toString(4))
                .response();

        Logger.info("CKAN : CREATE PACKAGE : Package created");
        Logger.debug("CKAN : CREATE PACKAGE : Inserted data: \n" + orgs);

        // Add the JSON object
        JSONObject jsonObject = new JSONObject(orgs).optJSONObject("result");
        packages.put(object.optString("type"), jsonObject.optString("id"));

    }

    /**
     * Create the datastore.
     *
     * @param packageId
     * @param object
     * @throws IOException
     */
    private void createDatastore(String packageId, JSONObject object) throws IOException {

        Logger.debug("CKAN : CREATE DATA STORE : Object to insert: \n" + object.toString(4));

        // Create the list of fields
        JSONArray attributes = object.optJSONArray("attributes");
        JSONArray fields = new JSONArray();
        fields.put(new JSONObject().put("id", "id").put("type", "bigint"));
        fields.put(new JSONObject().put("id", "name").put("type", "text"));
        for(int i = 0; i < attributes.length(); i++) {

            // Some objects
            JSONObject attribute = attributes.optJSONObject(i);

            // Location?
            if(attribute.optString("name").equals("location")) {
                JSONArray temp = attribute.optJSONArray("value");
                for(int j = 0; j < temp.length(); j++) {
                    JSONObject obj = new JSONObject();
                    JSONObject tem = temp.getJSONObject(j);
                    obj.put("id", tem.optString("name"));
                    obj.put("type", "float");
                    fields.put(obj);
                }
            } else {

                // The JSON object to insert
                JSONObject obj = new JSONObject();
                obj.put("id", attribute.optString("name"));

                // Get and set type
                String type = attribute.optString("type");
                if (type.equals("int") || type.equals("float")) {
                    obj.put("type", type);
                } else if (type.equals("String") || type.equals("string") || type.equals("unix timestamp")) {
                    obj.put("type", "text");
                } else if (type.equals("array")) {
                    obj.put("type", "json");
                }

                // Add fields/records
                fields.put(obj);
            }

        }

        // Add the name
        JSONObject data = new JSONObject();
        data.put("fields", fields);
        data.put("resource", new JSONObject().put("package_id", packageId));

        Logger.debug("CKAN : CREATE DATA STORE : Data to insert: \n" + data.toString(4));

        // Create the organization
        String orgs = new NetworkHelper(url + "/api/3/action/datastore_create")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(data.toString(4))
                .response();

        Logger.info("CKAN : CREATE DATA STORE : Datestore created");
        Logger.debug("CKAN : CREATE DATA STORE : Inserted data: \n" + orgs);

        // Add the JSON object
        JSONObject jsonObject = new JSONObject(orgs).optJSONObject("result");
        packages.put(object.optString("type"), jsonObject.optString("resource_id"));

    }

    /**
     * Insert data.
     *
     * @param resourceId
     * @param object
     * @throws IOException
     */
    private void insertData(String resourceId, JSONObject object) throws IOException {

        // Create the list of fields
        JSONArray attributes = object.optJSONArray("attributes");
        JSONArray records = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("id", System.nanoTime());
        obj.put("name", object.optString("id"));
        for(int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = attributes.optJSONObject(i);

            // Location
            if(attribute.optString("name").equals("location")) {
                JSONArray temp = attribute.optJSONArray("value");
                for(int j = 0; j < temp.length(); j++) {
                    JSONObject tem = temp.getJSONObject(j);
                    obj.put(tem.optString("name"), Float.valueOf(tem.optString("value")));
                }
            } else {

                // The JSON object to insert
                String type = attribute.optString("type");
                if (type.equals("int") || type.equals("float")) {
                    obj.put(attribute.optString("name"), Integer.parseInt(attribute.optString("value")));
                } else if (type.equals("String") || type.equals("string") || type.equals("unix timestamp")) {
                    obj.put(attribute.optString("name"), attribute.optString("value"));
                } else if (type.equals("array")) {
                    obj.put(attribute.optString("name"), attribute.optJSONArray("value").toString());
                }

            }
        }
        records.put(obj);

        // Add the name
        JSONObject data = new JSONObject();
        data.put("resource_id", resourceId);
        data.put("records", records);
        data.put("method", "insert");

        Logger.debug("CKAN : INSERT DATA : Data to insert: \n" + data.toString(4));

        // Create the organization
        String orgs = new NetworkHelper(url + "/api/3/action/datastore_upsert")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(data.toString(4))
                .response();

        Logger.info("CKAN : INSERT DATA : Data inserted into CKAN");
        Logger.debug("CKAN : INSERT DATA : Inserted data: \n" + orgs);

    }

}
