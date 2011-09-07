/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.visicut.model.graphicelements.svgsupport;

import com.kitfox.svg.Circle;
import com.kitfox.svg.Line;
import com.kitfox.svg.Path;
import com.kitfox.svg.Rect;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.ShapeElement;
import com.kitfox.svg.Text;
import com.kitfox.svg.Tspan;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;
import com.t_oster.visicut.model.graphicelements.ShapeObject;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thommy
 */
public class SVGShape extends SVGObject implements ShapeObject
{

  private ShapeElement decoratee;

  public SVGShape(ShapeElement s)
  {
    this.decoratee = s;
  }

  /**
   * Returns the first StyleAttribute with the given name in the
   * Path from the current Node to the Root node
   * @param name
   * @return 
   */
  private StyleAttribute getStyleAttributeRecursive(String name)
  {
    StyleAttribute sa = null;
    for (SVGElement node : this.getPathToRoot())
    {
      try
      {
        if (node.hasAttribute(name, AnimationElement.AT_CSS))
        {
          return node.getStyleAbsolute(name);
        }
      }
      catch (SVGElementException ex)
      {
        Logger.getLogger(SVGShape.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return null;
  }

  @Override
  public List<Object> getAttributeValues(String a)
  {
    List<Object> result = super.getAttributeValues(a);
    switch (Attribute.valueOf(a))
    {
      case Stroke_Width:
      {
        StyleAttribute sa = getStyleAttributeRecursive("stroke-width");
        if (sa != null)
        {
          result.add("" + sa.getFloatValue());
        }
        break;
      }
      case Type:
      {
        if (this.getDecoratee() instanceof Tspan)
        {
          result.add("Tspan");
        }
        if (this.getDecoratee() instanceof Circle)
        {
          result.add("Circle");
        }
        if (this.getDecoratee() instanceof Rect)
        {
          result.add("Rect");
        }
        if (this.getDecoratee() instanceof Text)
        {
          result.add("Text");
        }
        if (this.getDecoratee() instanceof com.kitfox.svg.Ellipse)
        {
          result.add("Ellipse");
        }
        if (this.getDecoratee() instanceof Line)
        {
          result.add("Line");
        }
        result.add("Shape");
        break;
      }
      case Stroke_Color:
      {
        StyleAttribute sa = getStyleAttributeRecursive("stroke");
        if (sa != null)
        {
          result.add(sa.getColorValue());
        }
        break;
      }
      case Fill_Color:
      {
        StyleAttribute sa = getStyleAttributeRecursive("fill");
        if (sa != null)
        {
          result.add(sa.getColorValue());
        }
        break;
      }
    }
    return result;
  }

  public ShapeElement getDecoratee()
  {
    return this.decoratee;
  }

  public Shape getShape()
  {
    if (false && this.getDecoratee() instanceof Path)
    {
      try
      {
        /**
         * Seems as if the Transformations for non-Path elements
         * are already handled correct by Kitfox, but Path has to
         * be transformed manually
         */
        AffineTransform at = this.getAbsoluteTransformation();
        return at.createTransformedShape(this.getDecoratee().getShape());
      }
      catch (SVGException ex)
      {
        Logger.getLogger(SVGShape.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return this.getDecoratee().getShape();
  }
}
