/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.visicut.model.mapping;

import com.t_oster.visicut.model.graphicelements.GraphicObject;
import com.t_oster.visicut.model.graphicelements.GraphicSet;
import com.t_oster.visicut.model.graphicelements.ShapeDecorator;
import com.t_oster.visicut.model.graphicelements.ShapeObject;
import java.awt.geom.Area;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;

/**
 *
 * @author thommy
 */
public class FilterSet extends LinkedList<MappingFilter>
{

  public GraphicSet getMatchingObjects(GraphicSet elements)
  {
    GraphicSet result = new GraphicSet();
    result.setTransform(elements.getTransform());
    final Area outerShape = new Area();
    for (GraphicObject o : elements)
    {
      boolean passed = true;
      for (MappingFilter filter : this)
      {
        if (!filter.matches(o))
        {
          passed = false;
          break;
        }
      }
      if (passed)
      {
        if (!useOuterShape)
        {
          result.add(o);
        }
        else
        {
          if (useOuterShape)
          {
            if (o instanceof ShapeObject)
            {
              outerShape.add(new Area(((ShapeObject) o).getShape()));
            }
            else
            {
              outerShape.add(new Area(o.getBoundingBox()));
            }
          }
        }
      }
    }
    if (useOuterShape)
    {
      result.add(new ShapeDecorator(outerShape));
    }
    return result;
  }
  protected boolean useOuterShape = false;
  public static final String PROP_USEOUTERSHAPE = "useOuterShape";

  /**
   * Get the value of useOuterShape
   *
   * @return the value of useOuterShape
   */
  public boolean isUseOuterShape()
  {
    return useOuterShape;
  }

  /**
   * Set the value of useOuterShape
   * If set to True and this mapping contains Shape
   * Elements, the outer shape of all those will be rendered
   *
   * @param useOuterShape new value of useOuterShape
   */
  public void setUseOuterShape(boolean useOuterShape)
  {
    boolean oldUseOuterShape = this.useOuterShape;
    this.useOuterShape = useOuterShape;
    propertyChangeSupport.firePropertyChange(PROP_USEOUTERSHAPE, oldUseOuterShape, useOuterShape);
  }
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
  public String toString()
  {
    if (this.size() == 0)
    {
      return "Everything";
    }
    else
    {
      return this.get(this.size() - 1).toString();
    }
  }
}
