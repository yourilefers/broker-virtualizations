package org.opendoors.gemini.interfaces;

import org.json.JSONObject;
import org.opendoors.gemini.common.Config;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public interface Sink {

    /** The config file instance */
    Config config = Config.getInstance();

    /** The sink URL */
    String url = config.get("ckan_url", "");

    /**
     * The setup
     */
    void setup() throws Exception;

    /**
     * Publish a new result in the Orion format.
     *
     * @param result The result to publish in the Orion format
     */
    void publish(JSONObject result);

}
