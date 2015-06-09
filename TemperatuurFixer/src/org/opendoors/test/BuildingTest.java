package org.opendoors.test;

import org.junit.Test;
import org.opendoors.Building;

/**
 * Created by Ruben on 4-6-2015.
 */
public class BuildingTest {

    /**
     * Test if a correctly made object works
     * @throws Exception
     */
    @Test
    public void testNewGoodObject() throws Exception {
        Building object = new Building("ITC gebouw", (float) 52.223852, (float) 6.885874);
    }
}