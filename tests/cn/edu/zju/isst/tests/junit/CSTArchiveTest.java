package cn.edu.zju.isst.tests.junit;

import java.io.IOException;

import org.junit.Test;

import cn.edu.zju.isst.model.CSTArchive;

/**
 * Created by i308844 on 7/17/14.
 */
public class CSTArchiveTest {

    @Test
    public void testCreate() {

        byte[] data = null;
        
        for (int i = 0; i < 1000000; i++) {
            CSTArchive.create(data);
        }
    }
}
