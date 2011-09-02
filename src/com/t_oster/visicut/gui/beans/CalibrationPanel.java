package com.t_oster.visicut.gui.beans;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Panel displays a Set of points which are draggable
 * with the mouse
 * 
 * @author thommy
 */
public class CalibrationPanel extends GraphicObjectsPanel implements MouseListener, MouseMotionListener
{

  //The size of the Points in Pixel
  private static int SIZE = 10;
  
  public CalibrationPanel()
  {
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }
  
  protected RenderedImage backgroundImage = null;

  /**
   * Get the value of backgroundImage
   *
   * @return the value of backgroundImage
   */
  public RenderedImage getBackgroundImage()
  {
    return backgroundImage;
  }

  /**
   * Set the value of backgroundImage
   *
   * @param backgroundImage new value of backgroundImage
   */
  public void setBackgroundImage(RenderedImage backgroundImage)
  {
    this.backgroundImage = backgroundImage;
  }

  protected Point[] pointList = new Point[0];

  /**
   * Get the value of pointList
   *
   * @return the value of pointList
   */
  public Point[] getPointList()
  {
    return pointList;
  }

  /**
   * Set the value of pointList
   *
   * @param pointList new value of pointList
   */
  public void setPointList(Point[] pointList)
  {
    this.pointList = pointList;
    this.repaint();
  }

  //Contains the last Transform the component was rendered with
  AffineTransform lastTransform;
  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (g instanceof Graphics2D)
    {
      Graphics2D gg = (Graphics2D) g;
      lastTransform = gg.getTransform();
      if (backgroundImage != null)
      {
        gg.drawRenderedImage(backgroundImage, null);
      }
      gg.setColor(Color.red);
      Point2D size = new Point(SIZE, SIZE);
      try
      {
        size = gg.getTransform().createInverse().deltaTransform(size, null);
      }
      catch (NoninvertibleTransformException ex)
      {
        Logger.getLogger(CalibrationPanel.class.getName()).log(Level.SEVERE, null, ex);
      }
      for (Point p:this.pointList)
      {
        drawCross(gg, p, (int) size.getX());
        if (p==selectedPoint)
        {
          gg.drawOval((int) (p.x-size.getX()/2), (int) (p.y-size.getY()/2), (int) size.getX(), (int) size.getY());
        }
      }
    }
  }

  private void drawCross(Graphics2D g, Point p, int size)
  {
    g.drawLine(p.x - size / 2, p.y, p.x + size / 2, p.y);
    g.drawLine(p.x, p.y - size / 2, p.x, p.y + size / 2);
  }

  public void mouseClicked(MouseEvent me)
  {
  }
  public Point selectedPoint = null;

  public void mousePressed(MouseEvent me)
  {
    Point p = me.getPoint();
    selectedPoint = null;
    for (Point source : this.getPointList())
    {
      Point2D target = this.lastTransform.transform(source, null);
      if (p.distance(target) < SIZE)
      {
        selectedPoint = source;
        repaint();
        return;
      }
    }
  }

  public void mouseReleased(MouseEvent me)
  {
    selectedPoint = null;
  }

  public void mouseEntered(MouseEvent me)
  {
  }

  public void mouseExited(MouseEvent me)
  {
  }

  public void mouseDragged(MouseEvent me)
  {
    if (selectedPoint != null)
    {
      try
      {
        Point p = me.getPoint();
        lastTransform.createInverse().transform(p, p);
        selectedPoint.x = p.x;
        selectedPoint.y = p.y;
      }
      catch (NoninvertibleTransformException ex)
      {
        Logger.getLogger(CalibrationPanel.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.repaint();
    }
  }

  public void mouseMoved(MouseEvent me)
  {
  }
}
