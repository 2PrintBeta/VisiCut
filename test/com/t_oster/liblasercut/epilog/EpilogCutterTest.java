/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.liblasercut.epilog;

import com.t_oster.liblasercut.LaserJob;
import org.junit.Test;

/**
 *
 * @author thommy
 */
public class EpilogCutterTest {
   

    /**
     * Test of sendJob method, of class EpilogCutter.
     */
    @Test
    public void testSendJob() {
        System.out.println("sendJob");
        LaserJob job = new LaserJob("peter", "124", "bla", 600);
        EpilogCutter instance = new EpilogCutter("137.226.56.228");
        instance.sendJob(job);
    }
    
}
