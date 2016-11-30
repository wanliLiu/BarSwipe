package com.barswipe.Test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Soli on 2016/11/30.
 */
public class FunctionTestTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void initRequstMonth() throws Exception {
        FunctionTest test = new FunctionTest();
        String[] data = test.initRequstMonth(1);

        assertEquals("2016 - 01",data[3]);
        assertEquals("2015 - 12",data[2]);
        assertEquals("2015 - 11",data[1]);
        assertEquals("2015 - 10",data[0]);
    }

}