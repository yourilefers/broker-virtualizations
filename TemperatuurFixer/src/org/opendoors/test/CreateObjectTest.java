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
        CreateObject object = new CreateObject(0, (float) 52.223852, (float) 6.885874);
    }
}