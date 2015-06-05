package org.opendoors.test;

import org.junit.Test;
import org.opendoors.CreateObject;
import org.opendoors.Main;

import static org.junit.Assert.*;

/**
 * Created by Ruben on 4-6-2015.
 */
public class CreateObjectTest {

    /**
     * Test if a correctly made object works
     * @throws Exception
     */
    @Test
    public void testNewGoodObject() throws Exception {
        CreateObject object = new CreateObject(Main.GARAGE_MST, 1000, (float) 52.215253, (float) 6.890297);
    }

    /**
     * Test if a wrongly made object crashes
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewWrongObject() throws Exception {
        CreateObject object = new CreateObject(Main.GARAGE_MST, 0, (float) 0, (float) 0);
    }
}