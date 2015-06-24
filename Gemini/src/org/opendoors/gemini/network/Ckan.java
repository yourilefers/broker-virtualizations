package org.opendoors.gemini.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opendoors.gemini.common.Config;
import org.opendoors.gemini.common.Constants;
import org.opendoors.gemini.common.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Ckan {

    /** The config */
    private Config config;

    /** The organization of this user */
    private String organization;

    /** The API key of this user */
    private String apiKey;

    /** The list of known packages */
    private HashMap<String, String> packages;

    /** The sink URL */
    private String url;

    //
    // SETUP

    /**
     * Setup CKAN.
     *
     * @throws Exception
     */
    public void setup() throws Exception {

        // Setup config
        config = Config.getInstance();

        // Set url
        url = config.get(Constants.CONFIG_CKAN_URL, "");

        // Setup orion
        if(url.isEmpty()) {
            throw new Exception("The url of the CKAN server has not been defined in config.conf. Please add 'ckan_url' to your config.");
        }

        // Try to connect
        connect();

        // Find the organization
        setupOrganization();

        // Setup packages
        setupPackages();

    }

    /**
     * Try to connect to CKAN
     * @throws Exception
     */
    private void connect() throws Exception {

        // Try to connect
        String connectResult = new NetworkHelper(url + "/api/util/status").response();
        Logger.debug("CKAN connect test result: " + connectResult);
        if(connectResult.isEmpty()) {
            throw new IOException("Ckan did not respond correctly!");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(connectResult);
        Logger.info("Found CKAN version " + jsonObject.optString("ckan_version"));

    }

    /**
     * Setup the organization section of CKAN
     * @throws Exception
     */
    private void setupOrganization() throws Exception {

        if(config.get(Constants.CONFIG_CKAN_ORGANIZATION, "").isEmpty()) {
            throw new Exception("No organization defined in the config file. Please add 'ckan_organization' to your config.");
        } else {
            organization = config.get(Constants.CONFIG_CKAN_ORGANIZATION);
        }

        // Find the organization
        if(config.get(Constants.CONFIG_CKAN_API_KEY, "").isEmpty()) {
            throw new Exception("No API key defined in the config file. Please add 'ckan_api_key' to your config.");
        } else {
            apiKey = config.get(Constants.CONFIG_CKAN_API_KEY);
        }

        // Try to connect
        String organizations = new NetworkHelper(url + "/api/3/action/organization_list").response();
        if(organizations.isEmpty()) {
            throw new IOException("Ckan did not return the list of organizations.");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(organizations);
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

    }

    /**
     * Setup packages from the CKAN API.
     *
     * @throws Exception
     */
    private void setupPackages() throws Exception {

        // Create the packages hashmap
        packages = new HashMap<>();

        // Try to connect
        String packagesList = new NetworkHelper(url + "/api/3/action/current_package_list_with_resources")
                .response();
        if(packagesList.isEmpty()) {
            throw new IOException("Ckan did not return the list of packages!");
        }

        // Make a JSON object of it and retrieve the version
        JSONObject jsonObject = new JSONObject(packagesList);
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

    //
    // Publishing
    //

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
                Logger.error("CKAN : Could not create the package: " + e.getLocalizedMessage());
                Logger.error("CKAN :\n" + Arrays.toString(e.getStackTrace()));

            }
            try {

                // Create the datastore
                createDatastore(packages.get(result.optString("type")), result);

            } catch(IOException e) {

                // Oops?
                Logger.error("CKAN : Could not create the data store: " + e.getLocalizedMessage());
                Logger.error("CKAN :\n" + Arrays.toString(e.getStackTrace()));

            }
        }

        try {

            // Insert the thing
            insertData(packages.get(result.optString("type")), result);

        } catch(IOException e) {

            // Oops?
            Logger.error("CKAN : PUBLISH : IO | Could not insert the data: " + e.getLocalizedMessage());
            Logger.error("CKAN : PUBLISH : IO |\n" + Arrays.toString(e.getStackTrace()));

        } catch(NullPointerException e) {

            // Oops?
            e.printStackTrace();
            Logger.error("CKAN : PUBLISH : Null | " + e.getLocalizedMessage());
            Logger.error("CKAN : PUBLISH : Null |\n" + Arrays.toString(e.getStackTrace()));

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
        JSONObject dataToPost = new JSONObject();
        dataToPost.put("name", object.optString("type").toLowerCase());

        Logger.debug("CKAN : CREATE PACKAGE : Data to insert: \n" + dataToPost.toString(4));

        // Create the organization
        String createdPackageAnswer = new NetworkHelper(url + "/api/3/action/package_create")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(dataToPost.toString(4))
                .response();

        Logger.info("CKAN : CREATE PACKAGE : Package created");
        Logger.debug("CKAN : CREATE PACKAGE : Inserted data: \n" + createdPackageAnswer);

        // Add the JSON object
        JSONObject result = new JSONObject(createdPackageAnswer).optJSONObject("result");
        packages.put(object.optString("type"), result.optString("id"));

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
        JSONArray fields = new JSONArray();

        // Add the name field for identification
        fields.put(new JSONObject().put("id", "name").put("type", "text"));

        // The list of attributes to go through
        JSONArray attributes = object.optJSONArray("attributes");
        for(int i = 0; i < attributes.length(); i++) {

            // The current attribute
            JSONObject attribute = attributes.optJSONObject(i);

            // The object to add
            JSONObject obj = new JSONObject();

            // In case the attribute defines a location
            if(attribute.optString("name").equals("location")) {

                // The location attribute
                JSONArray locationArray = attribute.optJSONArray("value");
                for(int j = 0; j < locationArray.length(); j++) {

                    // The object to add
                    obj = new JSONObject();

                    // Retrieve the current location type and add it
                    JSONObject locationType = locationArray.getJSONObject(j);
                    obj.put("id", locationType.optString("name"));
                    obj.put("type", "float");
                    fields.put(obj);

                }
            } else {

                // The JSON object to insert
                obj.put("id", attribute.optString("name"));

                // Get and set type
                switch(attribute.optString("type")) {
                    case "String":
                    case "string":
                        obj.put("type", "text");
                        break;
                    case "array":
                        obj.put("type", "json");
                        break;
                    case "unix timestamp":
                        obj.put("type", "bigint");
                        break;
                    default:
                        obj.put("type", attribute.optString("type"));
                        break;
                }

                // Add fields/records
                fields.put(obj);
            }

        }

        // Add the name
        JSONObject dataToPost = new JSONObject();
        dataToPost.put("fields", fields);
        dataToPost.put("resource", new JSONObject().put("package_id", packageId));

        Logger.debug("CKAN : CREATE DATA STORE : Data to insert: \n" + dataToPost.toString(4));

        // Create the organization
        String dataStoreAnswer = new NetworkHelper(url + "/api/3/action/datastore_create")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(dataToPost.toString(4))
                .response();

        Logger.info("CKAN : CREATE DATA STORE : Datestore created");
        Logger.debug("CKAN : CREATE DATA STORE : Inserted data: \n" + dataStoreAnswer);

        // Add the JSON object
        JSONObject result = new JSONObject(dataStoreAnswer).optJSONObject("result");
        packages.put(object.optString("type"), result.optString("resource_id"));

    }

    /**
     * Insert data.
     *
     * @param resourceId
     * @param object
     * @throws IOException
     */
    private void insertData(String resourceId, JSONObject object) throws IOException {

        // The list of records to insert
        JSONArray records = new JSONArray();

        // The current record to insert and add the name already
        JSONObject obj = new JSONObject();
        obj.put("name", object.optString("id"));

        // Go through the list of attributes
        JSONArray attributes = object.optJSONArray("attributes");
        for(int i = 0; i < attributes.length(); i++) {

            // The current attribute
            JSONObject attribute = attributes.optJSONObject(i);

            // If the attribute contains the location
            if(attribute.optString("name").equals("location")) {

                // The location array to go through
                JSONArray locationArray = attribute.optJSONArray("value");
                for(int j = 0; j < locationArray.length(); j++) {

                    // The current location and add it as a float
                    JSONObject locationObject = locationArray.getJSONObject(j);
                    obj.put(locationObject.optString("name"), Float.valueOf(locationObject.optString("value")));

                }

            } else {

                // Add the value
                switch(attribute.optString("type")) {
                    case "int":
                    case "float":
                        obj.put(attribute.optString("name"), attribute.optInt("value"));
                        break;
                    case "array":
                        obj.put(attribute.optString("name"), attribute.optJSONArray("value").toString());
                        break;
                    case "unix timestamp":
                        obj.put(attribute.optString("name"), attribute.optLong("value"));
                        break;
                    default:
                        obj.put(attribute.optString("name"), attribute.optString("value"));
                        break;
                }

            }
        }

        // Add the object with attributes
        records.put(obj);

        // Add the name
        JSONObject dataToPost = new JSONObject();
        dataToPost.put("resource_id", resourceId);
        dataToPost.put("records", records);
        dataToPost.put("method", "insert");

        Logger.debug("CKAN : INSERT DATA : Data to insert: \n" + dataToPost.toString(4));

        // Create the organization
        String dataUpsertAnswer = new NetworkHelper(url + "/api/3/action/datastore_upsert")
                .setPost()
                .setHeader("Authorization", apiKey)
                .setPostData(dataToPost.toString(4))
                .response();

        Logger.info("CKAN : INSERT DATA : Data inserted into CKAN for entity " + object.optString("id"));
        Logger.debug("CKAN : INSERT DATA : Inserted data: \n" + dataUpsertAnswer);

    }

}
