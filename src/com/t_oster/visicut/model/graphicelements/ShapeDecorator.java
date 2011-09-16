/**
 * This file is part of VisiCut.
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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author thommy
 */
public class ShapeDecorator implements ShapeObject
{

  private Shape decoratee;

  public ShapeDecorator(Shape s)
  {
    this.decoratee = s;
  }
  
  public Shape getShape()
  {
    return decoratee;
  }

  public Rectangle2D getBoundingBox()
  {
    return decoratee.getBounds2D();
  }

  public List<Object> getAttributeValues(String name)
  {
    List<Object> result = new LinkedList<Object>();
    if ("Type".equals(name))
    {
      result.add("Shape");
    }
    return result;
  }

  public List<String> getAttributes()
  {
    List<String> result = new LinkedList<String>();
    result.add("Type");
    return result;
  }

  public void render(Graphics2D g)
  {
    g.draw(decoratee);
  }
}
