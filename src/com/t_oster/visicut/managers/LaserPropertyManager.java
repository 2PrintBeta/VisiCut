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
package com.t_oster.visicut.managers;

import com.t_oster.liblasercut.LaserProperty;
import com.t_oster.visicut.misc.Helper;
import com.t_oster.visicut.model.LaserDevice;
import com.t_oster.visicut.model.LaserProfile;
import com.t_oster.visicut.model.MaterialProfile;
import com.t_oster.visicut.model.Raster3dProfile;
import com.t_oster.visicut.model.RasterProfile;
import com.t_oster.visicut.model.VectorProfile;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

/**
 * This class manages the available Material Profiles
 * 
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class LaserPropertyManager
{

  private static LaserPropertyManager instance;
  
  public static LaserPropertyManager getInstance()
  {
    if (instance == null)
    {
      instance = new LaserPropertyManager();
    }
    return instance;
  }
  
  /*
   * Need a public constructior for UI manager
   * Do not use. Use getInstance instead
   */
  public LaserPropertyManager()
  {
    if (instance != null)
    {
      System.err.println("ProfileManager should not be instanciated directly");
    }
  }

  private File getLaserPropertiesFile(LaserDevice ld, MaterialProfile mp, LaserProfile lp)
  {
    File laserprofiles = new File(Helper.getBasePath(), "laserprofiles");
    File deviceprofiles = new File(laserprofiles, Helper.toPathName(ld.getName()));
    File material = new File(new File(deviceprofiles, Helper.toPathName(mp.getName())), mp.getDepth()+"mm");
    File profile = new File(material, Helper.toPathName(lp.getName())+".xml");
    return profile;
  }
  
  public List<LaserProperty> getLaserProperties(LaserDevice ld, MaterialProfile mp, LaserProfile lp) throws FileNotFoundException, IOException
  {
    File f = getLaserPropertiesFile(ld, mp, lp);
    if (f.exists())
    {
      List<LaserProperty> result = this.loadProperties(f);
      //check if it is still the correct type for the laser-cutter
      Class expectedClass = null;
      if (lp instanceof RasterProfile)
      {
        expectedClass = ld.getLaserCutter().getLaserPropertyForRasterPart().getClass();
      }
      else if (lp instanceof VectorProfile)
      {
        expectedClass = ld.getLaserCutter().getLaserPropertyForVectorPart().getClass();
      }
      else if (lp instanceof Raster3dProfile)
      {
        expectedClass = ld.getLaserCutter().getLaserPropertyForRaster3dPart().getClass();
      }
      for(LaserProperty p : result)
      {
        if (!expectedClass.isAssignableFrom(p.getClass()))
        {
          System.err.println("Tried to load a laser-property of class "+p.getClass().toString()+", but lasercutter expects "+expectedClass.toString());
          return null;
        }
      }
      return result;
    }
    return null;
  }
  
  public void deleteLaserProperties(LaserDevice ld, MaterialProfile mp, LaserProfile lp)
  {
    File f = getLaserPropertiesFile(ld, mp, lp);
    if (f.exists())
    {
      f.delete();
    }
  }

  public void saveLaserProperties(LaserDevice ld, MaterialProfile mp, LaserProfile lp, List<LaserProperty> lps) throws FileNotFoundException
  {
    File f = getLaserPropertiesFile(ld, mp, lp);
    if (!f.getParentFile().exists())
    {
      f.getParentFile().mkdirs();
    }
    FileOutputStream out = new FileOutputStream(f);
    XMLEncoder enc = new XMLEncoder(out);
    enc.writeObject(lps);
    enc.close();
  }
  
  public List<LaserProperty> loadProperties(File f) throws FileNotFoundException, IOException
  {
    FileInputStream fin = new FileInputStream(f);
    List<LaserProperty> result = this.loadProperties(fin);
    fin.close();
    return result;
  }

  public List<LaserProperty> loadProperties(InputStream in)
  {
    XMLDecoder dec = new XMLDecoder(in);
    List<LaserProperty> result = (List<LaserProperty>) dec.readObject();
    return result;
  }

}
