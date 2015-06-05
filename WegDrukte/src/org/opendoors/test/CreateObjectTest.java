package org.opendoors.test;

import org.junit.Test;
import org.opendoors.CreateObject;
import org.opendoors.Main;

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
        CreateObject object = new CreateObject(Main.ROAD_WESTERVAL, 80,(float) 52.218364, (float) 6.869316);
    }
}