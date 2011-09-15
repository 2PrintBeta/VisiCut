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
package com.t_oster.visicut.model.graphicelements.jpgpngsupport;

import com.t_oster.visicut.model.graphicelements.GraphicSet;
import com.t_oster.visicut.model.graphicelements.ImportException;
import com.t_oster.visicut.model.graphicelements.Importer;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author thommy
 */
public class JPGPNGImporter implements Importer
{

  public GraphicSet importFile(File inputFile) throws ImportException
  {
    try
    {
      GraphicSet result = new GraphicSet();
      //TODO: Get Real Resolution
      result.setTransform(AffineTransform.getScaleInstance(500/72, 500/72));
      result.add(new JPGPNGImage(ImageIO.read(inputFile)));
      return result;
    }
    catch (IOException ex)
    {
      throw new ImportException(ex);
    }
  }

  public FileFilter getFileFilter()
  {
    return new FileFilter()
    {

      @Override
      public boolean accept(File file)
      {
        String name = file.getAbsolutePath().toLowerCase();
        return file.isDirectory() || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".jpeg");
      }

      @Override
      public String getDescription()
      {
        return "Rastergrafiken (jpg,png,bmp)";
      }
      
    };
  }
  
}
