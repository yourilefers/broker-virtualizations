package org.opendoors.test;

import org.junit.Test;
import org.opendoors.Main;
import org.opendoors.SendRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by Ruben on 4-6-2015.
 */
public class SendRequestTest {

    /**
     * Test a request to the server
     * @throws Exception <- not Expected
     */
    @Test
    public void testDoRequest() throws Exception {
        SendRequest request = new SendRequest();
        request.doRequest(Main.GARAGE_MST);
    }

    /**
     * Test two sequential request
     * @throws Exception <- not Expected
     */
    @Test
    public void testDoRequestTwice() throws Exception {
        SendRequest request = new SendRequest();
        request.doRequest(Main.GARAGE_MST);
        request.doRequest(Main.GARAGE_MST);
    }

    /**
     * Test a null object request
     * @throws Exception <- Expected
     */
    @Test(expected = NullPointerException.class)
    public void testDoFailRequest() throws Exception {
        SendRequest request = new SendRequest();
        request.doRequest(null);
    }

    /**
     * Test a normal request, followed by a null object request
     * @throws Exception <- Expected
     */
    @Test(expected = NullPointerException.class)
    public void testDoFailRequestTwice() throws Exception {
        SendRequest request = new SendRequest();
        request.doRequest(Main.GARAGE_MST);
        request.doRequest(null);
    }
}