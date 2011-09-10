package com.t_oster.visicut.gui.mappingdialog;

import com.t_oster.visicut.gui.beans.ZoomablePanel;
import com.t_oster.visicut.model.LaserProfile;
import com.t_oster.visicut.model.mapping.Mapping;
import com.t_oster.visicut.model.MaterialProfile;
import com.t_oster.visicut.model.VectorProfile;
import com.t_oster.visicut.model.graphicelements.GraphicObject;
import com.t_oster.visicut.model.graphicelements.GraphicSet;
import com.t_oster.visicut.model.graphicelements.ShapeObject;
import com.t_oster.visicut.model.mapping.FilterSet;
import com.t_oster.visicut.model.mapping.MappingSet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class implements a JPanel which is able
 * to display Parts of an SVG file matching
 * certain criteria
 * 
 * @author thommy
 */
public class MatchingPartsPanel extends ZoomablePanel implements MouseMotionListener, MouseListener
{

  public MatchingPartsPanel()
  {
    this.renderThread.start();
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }

  public void refreshRendering()
  {
    this.needRefresh = true;
    synchronized (renderThread)
    {
      this.renderThread.notify();
    }
  }
  protected int renderingProgress = 100;
  public static final String PROP_RENDERINGPROGRESS = "renderingProgress";

  /**
   * Get the value of renderingProgress.
   * Values between 0 and 100 mean progress in percent.
   * -1 means rendering without possibility to calculate percent value
   * (intermediate)
   *
   * @return the value of renderingProgress
   */
  public int getRenderingProgress()
  {
    return renderingProgress;
  }

  /**
   * Set the value of renderingProgress
   *
   * @param renderingProgress new value of renderingProgress
   */
  private void setRenderingProgress(int renderingProgress)
  {
    int oldRenderingProgress = this.renderingProgress;
    this.renderingProgress = renderingProgress;
    firePropertyChange(PROP_RENDERINGPROGRESS, oldRenderingProgress, renderingProgress);
    this.repaint();
  }
  protected boolean previewMode = false;
  public static final String PROP_PREVIEWMODE = "previewMode";

  /**
   * Get the value of previewMode
   *
   * @return the value of previewMode
   */
  public boolean isPreviewMode()
  {
    return previewMode;
  }

  /**
   * Set the value of previewMode
   *
   * @param previewMode new value of previewMode
   */
  public void setPreviewMode(boolean previewMode)
  {
    boolean oldPreviewMode = this.previewMode;
    this.previewMode = previewMode;
    firePropertyChange(PROP_PREVIEWMODE, oldPreviewMode, previewMode);
    this.refreshRendering();
  }
  protected MappingSet mappings = new MappingSet();

  /**
   * Get the value of mappings
   *
   * @return the value of mappings
   */
  public MappingSet getMappings()
  {
    return mappings;
  }

  /**
   * Set the value of mappings
   *
   * @param mappings new value of mappings
   */
  public void setMappings(MappingSet mappings)
  {
    this.mappings = mappings;
  }
  protected GraphicSet graphicElements = null;

  /**
   * Get the value of graphicElements
   *
   * @return the value of graphicElements
   */
  public GraphicSet getGraphicElements()
  {
    return graphicElements;
  }

  /**
   * Set the value of graphicElements
   *
   * @param graphicElements new value of graphicElements
   */
  public void setGraphicElements(GraphicSet graphicElements)
  {
    this.graphicElements = graphicElements;
    if (graphicElements != null)
    {
      Rectangle2D bb = graphicElements.getBoundingBox();
      if (bb != null)
      {
        this.setOuterBounds(new Dimension((int) (bb.getWidth()), (int) (bb.getHeight())));
        this.renderBuffer = new BufferedImage((int) (bb.getWidth()), (int) (bb.getHeight()), BufferedImage.TYPE_INT_RGB);
      }
    }
    this.refreshRendering();
  }
  protected FilterSet selectedFilterSet = null;

  /**
   * Get the value of selectedFilterSet
   *
   * @return the value of selectedFilterSet
   */
  public FilterSet getSelectedFilterSet()
  {
    return selectedFilterSet;
  }

  /**
   * Set the value of selectedFilterSet
   *
   * @param selectedFilterSet new value of selectedFilterSet
   */
  public void setSelectedFilterSet(FilterSet selectedFilterSet)
  {
    this.selectedFilterSet = selectedFilterSet;
    if (selectedFilterSet != null)
    {
      selectedMapping = null;
    }
    this.refreshRendering();
  }
  protected Mapping selectedMapping = null;

  /**
   * Get the value of selectedMapping
   *
   * @return the value of selectedMapping
   */
  public Mapping getSelectedMapping()
  {
    return selectedMapping;
  }

  /**
   * Set the value of selectedMapping
   *
   * @param selectedMapping new value of selectedMapping
   */
  public void setSelectedMapping(Mapping selectedMapping)
  {
    this.selectedMapping = selectedMapping;
    if (selectedMapping != null)
    {
      selectedFilterSet = null;
    }
    this.refreshRendering();
  }
  protected MaterialProfile material = null;

  /**
   * Get the value of material
   *
   * @return the value of material
   */
  public MaterialProfile getMaterial()
  {
    return material;
  }

  /**
   * Set the value of material
   *
   * @param material new value of material
   */
  public void setMaterial(MaterialProfile material)
  {
    this.material = material;
    //this.setBackground(this.material == null ? Color.white : this.material.getColor());
    this.refreshRendering();
  }
  private BufferedImage renderBuffer;
  private boolean needRefresh = true;
  private final Thread renderThread = new Thread()
  {

    @Override
    public void run()
    {
      while (true)
      {
        while (renderBuffer != null && needRefresh)
        {
          needRefresh = false;
          MatchingPartsPanel.this.setRenderingProgress(-1);
          Graphics g = renderBuffer.createGraphics();
          g.setColor(previewMode && selectedMapping != null ? material.getColor() : Color.WHITE);
          g.fillRect(0, 0, renderBuffer.getWidth(), renderBuffer.getHeight());
          MatchingPartsPanel.this.render(g);
        }
        MatchingPartsPanel.this.setRenderingProgress(100);
        synchronized (this)
        {
          try
          {
            this.wait();
          }
          catch (InterruptedException ex)
          {
            Logger.getLogger(MatchingPartsPanel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  };

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (g instanceof Graphics2D)
    {
      Graphics2D gg = (Graphics2D) g;
      if (this.getRenderingProgress() == 100)
      {//no rendering active
        if (this.renderBuffer != null)
        {//render buffer filled
          gg.drawRenderedImage(renderBuffer, null);
        }
      }
    }
  }

  protected void render(Graphics g)
  {
    Graphics2D gg = (Graphics2D) g;
    AffineTransform currentTrans = gg.getTransform();
    if (this.graphicElements != null)
    {
      AffineTransform scaleTrans = this.graphicElements.getScalePart();
      if (this.getSelectedMapping() != null)
      {
        GraphicSet set = this.getSelectedMapping().getFilterSet().getMatchingObjects(this.graphicElements);
        LaserProfile p = this.material.getLaserProfile(this.getSelectedMapping().getProfileName());
        set.setTransform(scaleTrans);
        if (this.previewMode)
        {
          if (p == null)
          {
            gg.setTransform(new AffineTransform());
            gg.drawString("Profile not available for current Material", 10, this.getHeight() / 2);
          }
          else
          {
            p.renderPreview(gg, set);
          }
        }
        else
        {
          if (p instanceof VectorProfile)
          {
            for (GraphicObject e : set)
            {
              if (e instanceof ShapeObject)
              {
                Shape s = ((ShapeObject) e).getShape();
                s = scaleTrans.createTransformedShape(s);
                gg.setColor(Color.red);
                gg.draw(s);
              }
            }
          }
          else
          {
            currentTrans.concatenate(scaleTrans);
            gg.setTransform(currentTrans);
            for (GraphicObject e : set)
            {
              e.render(gg);
            }
          }
        }
      }
      else
      {
        currentTrans.concatenate(scaleTrans);
        gg.setTransform(currentTrans);
        if (this.getSelectedFilterSet() != null)
        {
          for (GraphicObject e : this.getSelectedFilterSet().getMatchingObjects(graphicElements))
          {
            e.render(gg);
          }
        }
        else
        {
          for (GraphicObject e : graphicElements)
          {
            e.render(gg);
          }
        }
      }

    }
  }
  private Point lastMousePosition = null;

  public void mouseDragged(MouseEvent me)
  {
    if (lastMousePosition != null)
    {
      Point center = this.getCenter();
      Point diff = new Point(me.getPoint().x - lastMousePosition.x, me.getPoint().y - lastMousePosition.y);
      center.translate(-diff.x * 1000 / this.getZoom(), -diff.y * 1000 / this.getZoom());
      this.setCenter(center);
    }
    lastMousePosition = me.getPoint();
  }

  public void mouseMoved(MouseEvent me)
  {
  }

  public void mouseClicked(MouseEvent me)
  {
    //
  }

  public void mousePressed(MouseEvent me)
  {
    lastMousePosition = me.getPoint();
  }

  public void mouseReleased(MouseEvent me)
  {
    lastMousePosition = null;
  }

  public void mouseEntered(MouseEvent me)
  {
    //
  }

  public void mouseExited(MouseEvent me)
  {
    //
  }
}
