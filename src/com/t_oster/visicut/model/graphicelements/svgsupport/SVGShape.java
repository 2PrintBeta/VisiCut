/**
 * This file is part of VisiCut.
 * 
 *     VisiCut is free software: you can redistribute it and/or modify
 *     it under the terms of the Lesser GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *    VisiCut is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
 **/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.visicut.model.graphicelements.svgsupport;

import com.kitfox.svg.Circle;
import com.kitfox.svg.Line;
import com.kitfox.svg.Path;
import com.kitfox.svg.Rect;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.ShapeElement;
import com.kitfox.svg.Text;
import com.kitfox.svg.Tspan;
import com.kitfox.svg.xml.StyleAttribute;
import com.t_oster.visicut.model.graphicelements.ShapeObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    StyleAttribute sa = new StyleAttribute(name);
    try
    {
      if (this.getDecoratee().getStyle(sa, true))
      {
        return sa;
      }
    }
    catch (Exception ex)
    {
      Logger.getLogger(SVGShape.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  private Map<String, List<Object>> attributeValues = new LinkedHashMap<String, List<Object>>();

  @Override
  public List<Object> getAttributeValues(String a)
  {
    if (attributeValues.containsKey(a))
    {
      return attributeValues.get(a);
    }
    List<Object> result = super.getAttributeValues(a);
    switch (Attribute.valueOf(a.replace(" ", "_")))
    {
      case Stroke_Width:
      {
        StyleAttribute sa = getStyleAttributeRecursive("stroke-width");
        if (sa != null)
        {
          result.add("" + sa.getFloatValue());
        }
        else
        {
          result.add("none");
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
        if (this.getDecoratee() instanceof Path)
        {
          result.add("Path");
        }
        result.add("Shape");
        break;
      }
      case Stroke_Color:
      {
        StyleAttribute sa = getStyleAttributeRecursive("stroke");
        if (sa != null)
        {
          Color c = sa.getColorValue();
          result.add(c == null ? "none" : c);
        }
        else
        {
          result.add("none");
        }
        break;
      }
      case Fill_Color:
      {
        StyleAttribute sa = getStyleAttributeRecursive("fill");
        if (sa != null)
        {
          Color c = sa.getColorValue();
          result.add(c == null ? "none" : c);
        }
        else
        {
          result.add("none");
        }
        break;
      }
    }
    attributeValues.put(a, result);
    return result;
  }

  public ShapeElement getDecoratee()
  {
    return this.decoratee;
  }
  private static final int MAXOVERSHOOT = 10;

  @Override
  public Rectangle2D getBoundingBox()
  {
    Rectangle2D bb = this.getShape().getBounds2D();//super.getBoundingBox();
    //Add overshoot
    bb.setRect(bb.getX() - MAXOVERSHOOT, bb.getY() - MAXOVERSHOOT, bb.getWidth() + 2 * MAXOVERSHOOT, bb.getHeight() + 2 * MAXOVERSHOOT);
    return bb;
  }

  @Override
  public void render(Graphics2D g)
  {
    AffineTransform bak = g.getTransform();
    try
    {
      if (!(this.getDecoratee() instanceof Circle))
      {
        AffineTransform trans = g.getTransform();
        trans.concatenate(this.getAbsoluteTransformation());
        g.setTransform(trans);
      }
      this.getDecoratee().render(g);

    }
    catch (SVGException ex)
    {
      Logger.getLogger(SVGShape.class.getName()).log(Level.SEVERE, null, ex);
    }
    g.setTransform(bak);
  }

  public Shape getShape()
  {
    try
    {
      if (this.getDecoratee() instanceof Circle)
      {
        return this.getDecoratee().getShape();
      }
      else
      {
        AffineTransform at = this.getAbsoluteTransformation();
        return at.createTransformedShape(this.getDecoratee().getShape());
      }
    }
    catch (SVGException ex)
    {
      Logger.getLogger(SVGShape.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
