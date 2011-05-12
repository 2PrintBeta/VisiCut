/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.liblasercut.epilog;

import com.t_oster.liblasercut.LaserCutter;
import com.t_oster.liblasercut.LaserJob;
import com.t_oster.liblasercut.RasterPart;
import com.t_oster.liblasercut.VectorPart;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thommy
 */
public class EpilogCutter implements LaserCutter {

    public static boolean SIMULATE_COMMUNICATION = false;
    private static final int[] RESOLUTIONS = new int[]{500};
    private static final int BED_WIDTH = 1000;
    private static final int BED_HEIGHT = 500;
    private String hostname;
    private Socket connection;
    private InputStream in;
    private OutputStream out;

    public EpilogCutter(String hostname) {
        this.hostname = hostname;
    }

    private void waitForResponse(int expected) throws IOException, Exception {
        waitForResponse(expected, 3);
    }

    private void waitForResponse(int expected, int timeout) throws IOException, Exception {
        if (SIMULATE_COMMUNICATION) {
            System.out.println("Response simulated");
            return;
        }
        int result = -1;
        out.flush();
        System.out.println("Waiting for response...");
        for (int i = 0; i < timeout; i++) {
            if (in.available() > 0) {
                result = in.read();
                System.out.println("Got Response: " + result);
                if (result == -1) {
                    throw new IOException("End of Stream");
                }
                if (result != expected) {
                    throw new Exception("unexpected Response: " + result);
                }
                return;
            } else {
                Thread.sleep(100 * timeout);
            }
        }
        throw new Exception("Timeout");

    }

    private void initJob(LaserJob job) throws Exception {

        String localhost = java.net.InetAddress.getLocalHost().getHostName();
        //Use PrintStream for getting prinf methotd
        //and autoflush because we're watiting for responses
        PrintStream out = new PrintStream(this.out, true, "US-ASCII");
        out.print("\002\n");
        System.out.println("sending init");
        waitForResponse(0);
        System.out.println("got response");
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        PrintStream stmp = new PrintStream(tmp, true, "US-ASCII");
        stmp.printf("H%s\n", localhost);
        stmp.printf("P%s\n", job.getUser());
        stmp.printf("J%s\n", job.getTitle());
        stmp.printf("ldfA%s%s\n", job.getName(), localhost);
        stmp.printf("UdfA%s%s\n", job.getName(), localhost);
        stmp.printf("N%s\n", job.getTitle());

        out.printf("\002%d cfA%s%s\n", tmp.toString("US-ASCII").length(), job.getName(), localhost);
        System.out.println("sending jobheader");
        waitForResponse(0);
        out.print(tmp.toString("US-ASCII"));
        out.append((char) 0);
        System.out.println("sending job");
        waitForResponse(0);
        System.out.println("got jobresponse");
    }

    private String generatePjlHeader(LaserJob job) throws UnsupportedEncodingException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(result, true, "US-ASCII");

        /* Print the printer job language header. */
        out.printf("\033%%-12345X@PJL JOB NAME=%s\r\n", job.getTitle());
        out.printf("\033E@PJL ENTER LANGUAGE=PCL\r\n");
        /* Set autofocus off. */
        out.printf("\033&y0A");
        /* Left (long-edge) offset registration.  Adjusts the position of the
         * logical page across the width of the page.
         */
        out.printf("\033&l0U");
        /* Top (short-edge) offset registration.  Adjusts the position of the
         * logical page across the length of the page.
         */
        out.printf("\033&l0Z");

        /* Resolution of the print. Number of Units/Inch*/
        out.printf("\033&u%dD", job.getResolution());
        /* X position = 0 */
        out.printf("\033*p0X");
        /* Y position = 0 */
        out.printf("\033*p0Y");
        /* PCL/RasterGraphics resolution. */
        out.printf("\033*t%dR", job.getResolution());
        
        /* FIXME unknown purpose. */
        out.printf("\033&y0C");
        try {
            return result.toString("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EpilogCutter.class.getName()).log(Level.SEVERE, null, ex);
            return result.toString();
        }
    }

    private String generatePjlFooter() throws UnsupportedEncodingException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(result, true, "US-ASCII");

        /* Footer for printer job language. */
        /* Reset */
        out.printf("\033E");
        /* Exit language. */
        out.printf("\033%%-12345X");
        /* End job. */
        out.printf("@PJL EOJ \r\n");
        try {
            return result.toString("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EpilogCutter.class.getName()).log(Level.SEVERE, null, ex);
            return result.toString();
        }
    }

    private void sendPjlJob(LaserJob job) throws UnknownHostException, UnsupportedEncodingException, IOException, Exception {

        String localhost = java.net.InetAddress.getLocalHost().getHostName();
        /* Generate complete PJL Job */
        ByteArrayOutputStream pjlJob = new ByteArrayOutputStream();
        PrintStream wrt = new PrintStream(pjlJob, true, "US-ASCII");

        wrt.append(generatePjlHeader(job));
        //if (job.containsRaster()) {
            wrt.append(generateRasterPCL(job.getRasterPart()));
        //}
        if (job.containsVector()) {
            wrt.append(generateVectorPCL(job.getVectorPart()));
        }
        wrt.append(generatePjlFooter());
        /* Pad out the remainder of the file with 0 characters. */
        for (int i = 0; i < 4096; i++) {
            wrt.append((char) 0);
        }
        wrt.flush();

        //dump pjl into file for debugging
        new PrintStream(new FileOutputStream(new File("/tmp/last.pjl")), true, "US-ASCII").print(pjlJob.toString("US-ASCII"));

        /*
        pjlJob = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(new File("/tmp/working.pjl"));
        while(is.available()>0){
            pjlJob.write(is.read());
        }
        is.close
         * 
         */
        //Use PrintStream for getting prinf methotd
        //and autoflush because we're watiting for responses
        PrintStream out = new PrintStream(this.out, true, "US-ASCII");
        /* Send the Job length and name to the queue */
        out.printf("\003%d dfA%s%s\n", pjlJob.toString("US-ASCII").length(), job.getName(), localhost);
        System.out.println("Sending dataFileHeader");
        waitForResponse(0);
        System.out.println("Accepted. Sending Data File");
        /* Send the real PJL Job */
        out.print(pjlJob.toString("US-ASCII"));
        //out.append((char) 0);
        System.out.println("Data file sent");
        waitForResponse(0);
    }

    private void connect() throws IOException {
        if (SIMULATE_COMMUNICATION) {
            out = System.out;
        } else {
            connection = new Socket(hostname, 515);
            in = new BufferedInputStream(connection.getInputStream());
            out = new BufferedOutputStream(connection.getOutputStream());
        }
    }

    private void disconnect() throws IOException {
        if (!SIMULATE_COMMUNICATION) {
            in.close();
            out.close();
        }
    }

    private boolean isConnected() {
        return (connection != null && connection.isConnected());
    }

    public void sendJob(LaserJob job) {
        try {
            boolean wasConnected = isConnected();
            if (!wasConnected) {
                connect();
            }
            initJob(job);
            sendPjlJob(job);
            if (!wasConnected) {
                disconnect();
            }
            System.out.println("Successfully disconnected");
        } catch (Exception ex) {
            Logger.getLogger(EpilogCutter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] getResolutions() {
        return RESOLUTIONS;
    }

    public int getBedWidth() {
        return BED_WIDTH;
    }

    public int getBedHeight() {
        return BED_HEIGHT;
    }

    private String generateRasterPCL(RasterPart job) throws UnsupportedEncodingException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(result, true, "US-ASCII");
        /* FIXME unknown purpose. */
        out.printf("\033&y0C");

        /* We're going to perform a raster print. */

        //TODO: translate method
        /* Raster Orientation */
        out.printf("\033*r0F");
        /* Raster power */
        out.printf("\033&y%dP", 100);//TODO real rasterpower
        /* Raster speed */
        out.printf("\033&z%dS", 50);//TODO real raster_speed);
        out.printf("\033*r%dT", 501);//height * y_repeat);
        out.printf("\033*r%dS", 1000);//width * x_repeat);
        /* Raster compression */
        out.printf("\033*b%dM", 2);
        /* Raster direction (1 = up) */
        out.printf("\033&y1O");
        /* start at current position */
        out.printf("\033*r1A");
        
        //TODO: raster image
        
        out.printf("\033*rC");       // end raster
        out.write((char) 26);
        out.write((char) 4); // some end of file markers
        try {
            return result.toString("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EpilogCutter.class.getName()).log(Level.SEVERE, null, ex);
            return result.toString();
        }
    }

    private String generateVectorPCL(VectorPart job) throws UnsupportedEncodingException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(result, true, "US-ASCII");

        out.printf("\033E@PJL ENTER LANGUAGE=PCL\r\n");
        /* Page Orientation */
        out.printf("\033*r0F");
        out.printf("\033*r%dT", 1001);// if not dummy, then job.getHeight());
        out.printf("\033*r%dS", 501);// if not dummy then job.getWidth());
        out.printf("\033*r1A");
        out.printf("\033*rC");
        out.printf("\033%%1B");// Start HLGL

        /* We're going to perform a vector print. */
        //TODO: Translate Method
        //generate_vector(pjl_file, vector_file);

        /*
        fprintf(pjl_file, "IN;");
        fprintf(pjl_file, "XR%04d;", vector_freq);
        fprintf(pjl_file, "YP%03d;", vector_power);
        fprintf(pjl_file, "ZS%03d;", vector_speed);
         * PU = Pen up, PD = Pen Down
         */

        //Dummy Data from captured printjob
        out.print("IN;XR5000;YP100;ZS060;PU0,0;"
                + "PD1000,0,1000,500,0,500,0,0;PU500,250;PD0,0;");

        //TODO: Shouldn't be any need to first exit HLGL and then get back in..
        out.printf("\033%%0B");// end HLGL
        out.printf("\033%%1BPU");  // start HLGL, and pen up, end
        try {
            return result.toString("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EpilogCutter.class.getName()).log(Level.SEVERE, null, ex);
            return result.toString();
        }
    }
}
