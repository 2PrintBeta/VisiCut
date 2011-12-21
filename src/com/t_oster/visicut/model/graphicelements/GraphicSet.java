/**
 * This file is part of VisiCut.
 * Copyright (C) 2011 Thomas Oster <thomas.oster@rwth-aachen.de>
 * RWTH Aachen University - 52062 Aachen, Germany
 * 
 *     VisiCut is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *    VisiCut is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
 **/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.visicut.model.graphicelements;

import com.t_oster.visicut.misc.Helper;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;

/**
 *
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class GraphicSet extends LinkedList<GraphicObject>
{

  protected AffineTransform basicTransform = new AffineTransform();

  /**
   * Get the value of basicTransform
   *
   * @return the value of basicTransform
   */
  public AffineTransform getBasicTransform()
  {
    return basicTransform;
  }

  /**
   * Set the value of basicTransform
   * This value shall be used only for the import and for resetting
   * the transform of this object. So it should be written only
   * once after creation of the set
   *
   * @param basicTransform new value of basicTransform
   */
  public void setBasicTransform(AffineTransform basicTransform)
  {
    this.basicTransform = basicTransform;
    this.setTransform((AffineTransform) basicTransform.clone());
  }

  public AffineTransform transform = null;
  public static final String PROP_TRANSFORM = "transform";

  /**
   * Get the value of transform
   *
   * @return the value of transform
   */
  public AffineTransform getTransform()
  {
    return transform;
  }

  /**
   * Set the value of transform
   *
   * @param transform new value of transform
   */
  public void setTransform(AffineTransform transform)
  {
    AffineTransform oldTransform = this.transform;
    this.transform = transform;
    propertyChangeSupport.firePropertyChange(PROP_TRANSFORM, oldTransform, transform);
    this.boundingBoxCache = null;
  }
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Rectangle2D originalBoundingBoxCache = null;

  /**
   * Returns the BoundingBox of this Set ignoring the Transform,
   * also ignoring the basicTransform
   * @return 
   */
  public Rectangle2D getOriginalBoundingBox()
  {
    if (originalBoundingBoxCache == null)
    {
      for (GraphicObject o : this)
      {
        Rectangle2D current = o.getBoundingBox();
        if (originalBoundingBoxCache == null)
        {
          originalBoundingBoxCache = current;
        }
        else
        {
          Rectangle2D.union(originalBoundingBoxCache, current, originalBoundingBoxCache);
        }
      }
    }
    return originalBoundingBoxCache;
  }
  private Rectangle2D boundingBoxCache = null;

  /**
   * Returns the BoundingBox of this Set when rendered with the current
   * Transformation.
   * @return 
   */
  public Rectangle2D getBoundingBox()
  {
    if (boundingBoxCache == null)
    {
      for (GraphicObject o : this)
      {
        Rectangle2D current = o.getBoundingBox();
        if (this.transform != null)
        {
          current = Helper.transform(current, this.transform);
        }
        if (boundingBoxCache == null)
        {
          boundingBoxCache = current;
        }
        else
        {
          Rectangle2D.union(boundingBoxCache, current, boundingBoxCache);
        }
      }
      if (boundingBoxCache == null)
      {
        return new Rectangle2D.Double();
      }
    }
    return boundingBoxCache;
  }

  /**
   * Add PropertyChangeListener.
   *
   * @param listener
   */
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Remove PropertyChangeListener.
   *
   * @param listener
   */
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  @Override
  public boolean add(GraphicObject o)
  {
    this.boundingBoxCache = null;
    return super.add(o);
  }
  
  public boolean remove(GraphicObject o)
  {
    this.boundingBoxCache = null;
    return super.remove(o);
  }
  
  @Override
  public GraphicSet clone()
  {
    GraphicSet result = new GraphicSet();
    result.addAll(this);
    result.setTransform(this.getTransform());
    result.boundingBoxCache = boundingBoxCache;
    result.originalBoundingBoxCache = originalBoundingBoxCache;
    result.basicTransform = basicTransform;
    return result;
  }
}
