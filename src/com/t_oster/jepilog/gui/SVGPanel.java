/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.jepilog.gui;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.ShapeElement;
import com.kitfox.svg.app.beans.SVGIcon;
import com.t_oster.jepilog.controller.JepilogController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.PathIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author thommy
 */
public class SVGPanel extends JPanel implements MouseListener, MouseMotionListener, PropertyChangeListener {

    private SVGIcon icon;
    private SVGDiagram diag;
    private List<Shape> highlight;
    private boolean showRaster = true;
    private boolean showVector = true;
    private JepilogController controller = JepilogApp.getApplication().getController();

    public SVGPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.highlight = new LinkedList<Shape>();
        controller.addPropertyChangeListener(this);
    }

    public void setShowRaster(boolean show) {
        if (show != showRaster){
            showRaster = show;
            repaint();
        }
    }

    public void setShowVector(boolean show) {
        if (show != showVector){
            showVector = show;
            repaint();
        }
    }

    public void setSvgUri(URI diag) {
        this.icon = new SVGIcon();
        icon.setSvgURI(diag);
        icon.setAntiAlias(true);
        icon.setClipToViewbox(true);
        icon.setScaleToFit(false);
        this.diag = icon.getSvgUniverse().getDiagram(icon.getSvgURI());
        this.highlight = new LinkedList<Shape>();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (icon == null) {
            super.paintComponent(g);
            return;
        }
        final int width = getWidth();
        final int height = getHeight();

        //Background
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);
        
        
        
        if (showRaster) {
            icon.paintIcon(this, g, 0, 0);
        }
        if (showVector) {
            int x = 0;
            int y = 0;
            for (Shape shape : controller.getVectorShapes()) {
                g.setColor(Color.red);
                //Rectangle2D bounds = shape.getBounds2D();
                //g.drawRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
                PathIterator iter = shape.getPathIterator(null, 0.4);
                while (!iter.isDone()) {
                    double[] test = new double[8];
                    int result = iter.currentSegment(test);
                    if (result == PathIterator.SEG_MOVETO) {
                        x = (int) test[0];
                        y = (int) test[1];
                    } else if (result == PathIterator.SEG_LINETO) {
                        g.drawLine(x, y, (int) test[0], (int) test[1]);
                        x = (int) test[0];
                        y = (int) test[1];
                    }
                    iter.next();
                }
            }
        }
        
        //Draw StartingPoint
        
        Point sp = movingStartPoint ? this.getMousePosition() : controller.getStartPoint();
        if (sp!=null){
            g.setColor(Color.white);
            g.drawOval(sp.x-6, sp.y-6, 12, 12);
            g.setColor(Color.red);
            g.drawLine(sp.x -4 , sp.y -4, sp.x +4, sp.y +4);
            g.drawLine(sp.x -4 , sp.y +4, sp.x +4, sp.y -4);
        }
    }

    public void mouseClicked(MouseEvent me) {
        try {
            System.out.println("Clicked at: " + me.getPoint());
            if (diag != null){
                List pickedElements = diag.pick(me.getPoint(), null);
                if (pickedElements.size() > 0)
                {
                    List first = (List) pickedElements.get(pickedElements.size() - 1);
                    Object elem = first.get(first.size() - 1);
                    if (elem instanceof ShapeElement){
                        Shape shape = ((ShapeElement) elem).getShape();
                        JepilogApp.getApplication().getController().addVectorShape(shape);
                        this.repaint();
                    }
                }
            }

        } catch (SVGException ex) {
            Logger.getLogger(SVGPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean movingStartPoint = false;
    public void mousePressed(MouseEvent me) {
        if (me.getPoint().distance(JepilogApp.getApplication().getController().getStartPoint())<= 10){
            movingStartPoint = true;
        }
    }

    public void mouseReleased(MouseEvent me) {
        if (movingStartPoint){
            movingStartPoint = false;
            JepilogApp.getApplication().getController().setStartPoint(me.getPoint());
            repaint();
        }
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
        if (movingStartPoint){
            this.repaint();
        }
    }

    public void mouseMoved(MouseEvent me) {
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(JepilogController.Property.SVG_FILE.toString())){
            if (pce.getNewValue() instanceof URI){
                this.setSvgUri((URI) pce.getNewValue());
            }
        }
        repaint();
    }
}
