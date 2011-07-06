package com.t_oster.liblasercut.examples;

import com.t_oster.liblasercut.BlackWhiteRaster;
import com.t_oster.liblasercut.BlackWhiteRaster.DitherAlgorithm;
import com.t_oster.liblasercut.IllegalJobException;
import com.t_oster.liblasercut.LaserJob;
import com.t_oster.liblasercut.utils.MaterialProperty;
import com.t_oster.liblasercut.RasterPart;
import com.t_oster.liblasercut.VectorPart;
import com.t_oster.liblasercut.drivers.EpilogCutter;
import com.t_oster.liblasercut.utils.BufferedImageAdapter;
import com.t_oster.liblasercut.platform.Point;
import com.t_oster.liblasercut.platform.Util;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is an example app which lets you select an Image File,
 * dither algorithm and target size to raster the image in normal raster
 * mode
 *
 * @author oster
 */
public class PhotoPrint {

    private static void error(String text) {
        JOptionPane.showMessageDialog(null, text, "An Error occured", JOptionPane.OK_OPTION);
        System.exit(1);
    }

    public static void main(String[] args) throws IllegalJobException, SocketTimeoutException, Exception {
        JFileChooser importFileChooser = new JFileChooser();
        importFileChooser.showOpenDialog(null);
        File toImport = importFileChooser.getSelectedFile();
        BufferedImage img = null;
        if (toImport == null) {
            error("No file selected");
        }
        try {
            img = ImageIO.read(toImport);
        } catch (IOException ex) {
            error(ex.getMessage());
        }
        int dpi = 500;
        try {
            dpi = Integer.parseInt(JOptionPane.showInputDialog(null, "Please select DPI", "" + dpi));
        } catch (NumberFormatException e) {
            error(e.getMessage());
        }
        int width = 50;
        try {
            width = Integer.parseInt(JOptionPane.showInputDialog(null, "Please select width in mm", "" + width));
        } catch (NumberFormatException e) {
            error(e.getMessage());
        }
        int oWidth = (int) Util.mm2px(width, dpi);
        int oHeight = img.getHeight() * oWidth / img.getWidth();
        final BufferedImage scaledImg = new BufferedImage(oWidth, oHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledImg.createGraphics();
        AffineTransform at =
                AffineTransform.getScaleInstance((double) oWidth / img.getWidth(),
                (double) oHeight / img.getHeight());
        g.drawRenderedImage(img, at);
        final BufferedImage outImg = new BufferedImage(scaledImg.getWidth(), scaledImg.getHeight(), BufferedImage.TYPE_INT_RGB);
        final JComboBox cbDa = new JComboBox();
        for (DitherAlgorithm da : BlackWhiteRaster.DitherAlgorithm.values()) {
            cbDa.addItem(da);
        }
        final JPanel prev = new JPanel();
        final JCheckBox cbInvert = new JCheckBox("invert");
        JCheckBox cbCut = new JCheckBox("Cut out the image");
        prev.setLayout(new BoxLayout(prev, BoxLayout.Y_AXIS));
        ImageIcon imgIc = new ImageIcon();
        final BufferedImage buf = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        imgIc.setImage(buf);
        final JLabel lab = new JLabel(imgIc);
        final JSlider filter = new JSlider(-255, 255, 0);
        filter.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                int diff = filter.getValue();

            }
        });
        prev.add(lab);
        prev.add(cbDa);
        prev.add(cbInvert);
        prev.add(cbCut);
        prev.add(filter);
        prev.add(new JLabel("Width: "+outImg.getWidth()+" Height: "+outImg.getHeight()+ " ("+Util.px2mm(outImg.getWidth(), dpi)+"x"+Util.px2mm(outImg.getHeight(), dpi)+"mm)"));
        final ActionListener list = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                lab.setText("dithering...");
                lab.repaint();
                DitherAlgorithm da = (DitherAlgorithm) cbDa.getSelectedItem();
                BufferedImageAdapter ad = new BufferedImageAdapter(scaledImg);
                ad.setColorShift(filter.getValue());
                BlackWhiteRaster bw = new BlackWhiteRaster(ad, da);
                for (int y = 0; y < bw.getHeight(); y++) {
                    for (int x = 0; x < bw.getWidth(); x++) {
                        outImg.setRGB(x, y, bw.isBlack(x, y) ^ cbInvert.isSelected() ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                    }
                }
                Graphics2D g = buf.createGraphics();
                AffineTransform at =
                        AffineTransform.getScaleInstance((double) buf.getWidth() / bw.getWidth(),
                        (double) buf.getWidth() / bw.getWidth());
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, buf.getWidth(), buf.getHeight());
                g.drawRenderedImage(outImg, at);
                lab.setText("");
                prev.repaint();
            }
        };
        filter.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ce) {
                if (!filter.getValueIsAdjusting()) {
                    list.actionPerformed(null);
                }
            }
        });
        cbInvert.addActionListener(list);
        cbDa.addActionListener(list);
        cbDa.setSelectedIndex(0);
        if (JOptionPane.showConfirmDialog(
                null, prev, "Waehlen Sie einen Algorithmus aus", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            EpilogCutter instance = new EpilogCutter("137.226.56.228");
            JComboBox material = new JComboBox();
            for (MaterialProperty mp : instance.getMaterialPropertys()) {
                material.addItem(mp);
            }
            JOptionPane.showMessageDialog(null, material);
            RasterPart rp = new RasterPart(((MaterialProperty) material.getSelectedItem()).getLaserProperty());
            rp.addImage(new BlackWhiteRaster(new BufferedImageAdapter(outImg), BlackWhiteRaster.DitherAlgorithm.AVERAGE), new Point(0, 0));
            VectorPart vp = null;
            if (cbCut.isSelected()) {
                vp = new VectorPart(((MaterialProperty) material.getSelectedItem()).getCuttingProperty());
                vp.moveto(0, 0);
                vp.lineto(outImg.getWidth(), 0);
                vp.lineto(outImg.getWidth(), outImg.getHeight());
                vp.lineto(0, outImg.getHeight());
                vp.lineto(0, 0);
            }

            LaserJob job = new LaserJob("PhotoPrint", "666", "bla", dpi, null, vp, rp);
            instance.sendJob(job);
            JOptionPane.showMessageDialog(null, "Please press START on the Lasercutter");
        }
    }
}
