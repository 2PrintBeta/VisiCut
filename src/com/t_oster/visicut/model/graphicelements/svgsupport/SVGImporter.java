/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.visicut.model.graphicelements.svgsupport;

import com.kitfox.svg.Group;
import com.t_oster.visicut.model.graphicelements.Importer;
import com.kitfox.svg.ImageSVG;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.ShapeElement;
import com.t_oster.visicut.misc.ExtensionFilter;
import com.t_oster.visicut.model.graphicelements.GraphicObject;
import com.t_oster.visicut.model.graphicelements.GraphicSet;
import com.t_oster.visicut.model.graphicelements.ImportException;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author thommy
 */
public class SVGImporter implements Importer
{

  private SVGUniverse u = new SVGUniverse();

  private void importNode(SVGElement e, List<GraphicObject> result)
  {
    if (e instanceof ShapeElement && !(e instanceof Group))
    {
      if (((ShapeElement) e).getShape() != null)
      {
        result.add(new SVGShape((ShapeElement) e));
      }
      else
      {
        System.err.println("Ignoring SVGShape: " + e + " because can't get Shape");
      }
    }
    else
    {
      if (e instanceof ImageSVG)
      {
        result.add(new SVGImage((ImageSVG) e));
      }
    }
    for (int i = 0; i < e.getNumChildren(); i++)
    {
      importNode(e.getChild(i), result);
    }
  }

  public GraphicSet importFile(InputStream in, String name) throws IOException
  {
    URI svg = u.loadSVG(in, name);
    SVGRoot root = u.getDiagram(svg).getRoot();
    GraphicSet result = new GraphicSet();
    result.setTransform(AffineTransform.getScaleInstance(500 / 96, 500 / 96));
    importNode(root, result);
    return result;

  }

  @Override
  public GraphicSet importFile(File inputFile) throws ImportException
  {
    try
    {
      return this.importFile(new FileInputStream(inputFile), inputFile.getName());
    }
    catch (Exception ex)
    {
      throw new ImportException(ex);
    }
  }

  public FileFilter getFileFilter()
  {
    return new ExtensionFilter(".svg", "Scalable Vector Graphic (*.svg)");
  }
}
