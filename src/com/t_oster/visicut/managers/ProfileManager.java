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
package com.t_oster.visicut.managers;

import com.t_oster.liblasercut.BlackWhiteRaster.DitherAlgorithm;
import com.t_oster.liblasercut.LaserProperty;
import com.t_oster.visicut.model.LaserDevice;
import com.t_oster.visicut.model.LaserProfile;
import com.t_oster.visicut.model.MaterialProfile;
import com.t_oster.visicut.model.RasterProfile;
import com.t_oster.visicut.model.VectorProfile;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages the available Material Profiles
 * 
 * @author thommy
 */
public class ProfileManager
{

  protected List<MaterialProfile> materials;
  public static final String PROP_MATERIALS = "materials";
  private File dir;

  public ProfileManager()
  {
    this.materials = new LinkedList<MaterialProfile>();
    generateDefault();
  }

  public void loadMaterials(LaserDevice l)
  {
    this.materials = this.getMaterials(l);
  }

  private void generateDefault()
  {
    //Finnpappe
    MaterialProfile profile = new MaterialProfile();
    profile.setName("Finnpappe");
    profile.setThumbnailPath(new File(".VisiCut/materials/Finnpappe/profile.png").getAbsolutePath());
    profile.setDescription("A light paper based material.");
    profile.setColor(new Color(193, 127, 40));
    profile.setCutColor(Color.RED);
    profile.setDepth(2);
    List<LaserProfile> lprofiles = new LinkedList<LaserProfile>();
    VectorProfile vp = new VectorProfile();
    vp.setName("cut line");
    vp.setColor(new Color(150, 72, 0));
    vp.setDescription("A completely cut through line with small diameter");
    vp.setIsCut(true);
    vp.setWidth(1f);
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Finnpappe/cutline.png"));
    vp.getLaserProperties().add(new LaserProperty(100, 70));
    lprofiles.add(vp);
    vp = new VectorProfile();
    vp.setName("broad line");
    vp.setColor(new Color(150, 72, 0));
    vp.setDescription("A broad line, which is not cut through.");
    vp.setWidth(3f);
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Finnpappe/bigline.png"));
    vp.getLaserProperties().add(new LaserProperty(90, 100, 500, 150 * 0.0252f));
    lprofiles.add(vp);
    RasterProfile rp = new RasterProfile();
    rp.setColor(Color.black);
    rp.setName("Floyd Steinberg");
    rp.setColor(new Color(150, 72, 0));
    rp.setDescription("The Floyd Steinberg Algorithm is good for Fotos.");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Finnpappe/floydsteinberg.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.FLOYD_STEINBERG);
    rp.getLaserProperties().add(new LaserProperty(100, 70));
    lprofiles.add(rp);
    rp = new RasterProfile();
    rp.setName("Ordered");
    rp.setColor(new Color(150, 72, 0));
    rp.setDescription("The Ordered Algorithm adds a Pattern to the image");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Finnpappe/ordered.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.ORDERED);
    rp.getLaserProperties().add(new LaserProperty(100, 70));
    lprofiles.add(rp);
    rp = new RasterProfile();
    rp.setColor(new Color(150, 72, 0));
    rp.setName("Average");
    rp.setDescription("The Average Algorithm makes a pixel black iff its darker than the average pixel");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Finnpappe/average.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.AVERAGE);
    rp.getLaserProperties().add(new LaserProperty(100, 70));
    lprofiles.add(rp);
    profile.setLaserProfiles(lprofiles);
    this.materials.add(profile);
    //Filz
    profile = new MaterialProfile();
    profile.setName("Felt (red)");
    profile.setDepth(4.06f);
    profile.setColor(new Color(230, 130, 53));
    profile.setThumbnailPath(new File(".VisiCut/materials/Filz/profile.png").getAbsolutePath());
    profile.setDescription("A red Material");
    lprofiles = new LinkedList<LaserProfile>();
    vp = new VectorProfile();
    vp.setName("cut line");
    vp.setColor(new Color(205, 77, 4));
    vp.getLaserProperties().add(new LaserProperty(100, 80, 500));
    vp.setIsCut(true);
    vp.setWidth(1.07f);
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Filz/cutline.png"));
    lprofiles.add(vp);
    vp = new VectorProfile();
    vp.setName("broad cut line");
    vp.setColor(new Color(205, 77, 4));
    vp.setIsCut(true);
    vp.getLaserProperties().add(new LaserProperty(90, 100, 500, 150 * 0.0252f));
    vp.setWidth(2f);
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Filz/bigcut.png"));
    lprofiles.add(vp);
    rp = new RasterProfile();
    rp.setName("Average");
    rp.setColor(new Color(205, 77, 4));
    rp.setDescription("The Average Algorithm makes a pixel black iff its darker than the average pixel");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/rasterordered.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.AVERAGE);
    rp.getLaserProperties().add(new LaserProperty(100, 70));
    lprofiles.add(rp);
    profile.setLaserProfiles(lprofiles);
    this.materials.add(profile);
    //Plexiglass
    profile = new MaterialProfile();
    profile.setColor(new Color(117, 163, 209));
    profile.setName("Acrylic glass");
    profile.setDepth(3f);
    profile.setThumbnailPath(new File(".VisiCut/materials/Plexiglass/profile.png").getAbsolutePath());
    lprofiles = new LinkedList<LaserProfile>();
    vp = new VectorProfile();
    vp.setName("cut line");
    vp.getLaserProperties().add(new LaserProperty(100, 50, 5000));
    vp.setIsCut(true);
    vp.setWidth(1f);
    vp.setColor(new Color(170, 160, 160));
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/cutline.png"));
    lprofiles.add(vp);
    vp = new VectorProfile();
    vp.setColor(new Color(170, 160, 160));
    vp.setName("broad line");
    vp.getLaserProperties().add(new LaserProperty(90, 100, 500, 150 * 0.0252f));
    vp.setWidth(2f);
    vp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/bigline.png"));
    lprofiles.add(vp);
    rp = new RasterProfile();
    rp.setColor(new Color(170, 160, 160));
    rp.setName("Floyd Steinberg");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/floydsteinberg.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.FLOYD_STEINBERG);
    rp.getLaserProperties().add(new LaserProperty(80, 100));
    rp.getLaserProperties().add(new LaserProperty(80, 100));
    lprofiles.add(rp);
    rp = new RasterProfile();
    rp.setColor(new Color(170, 160, 160));
    rp.setName("Ordered");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/rasterordered.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.ORDERED);
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    lprofiles.add(rp);
    rp = new RasterProfile();
    rp.setColor(new Color(170, 160, 160));
    rp.setName("Floyd Steinberg (inverted)");
    rp.setInvertColors(true);
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/floydsteinberg.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.FLOYD_STEINBERG);
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    lprofiles.add(rp);
    rp = new RasterProfile();
    rp.setColor(new Color(170, 160, 160));
    rp.setName("Ordered (inverted)");
    rp.setPreviewThumbnail(new File(".VisiCut/materials/Plexiglass/rasterordered.png"));
    rp.setDitherAlgorithm(DitherAlgorithm.ORDERED);
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    rp.getLaserProperties().add(new LaserProperty(50, 100));
    rp.setInvertColors(true);
    lprofiles.add(rp);
    profile.setLaserProfiles(lprofiles);
    this.materials.add(profile);
  }

  private List<MaterialProfile> loadFromDirectory(File dir)
  {
    List<MaterialProfile> result = new LinkedList<MaterialProfile>();
    if (dir.isDirectory())
    {
      for (File f : dir.listFiles())
      {
        if (f.isFile() && f.getAbsolutePath().toLowerCase().endsWith(".xml"))
        {
          try
          {
            MaterialProfile prof = this.loadProfile(f);
            result.add(prof);
          }
          catch (Exception ex)
          {
            Logger.getLogger(ProfileManager.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }
    return result;
  }

  public void saveProfile(MaterialProfile mp, File f) throws FileNotFoundException
  {
    FileOutputStream out = new FileOutputStream(f);
    XMLEncoder enc = new XMLEncoder(out);
    enc.writeObject(mp);
    enc.close();
  }

  public MaterialProfile loadProfile(File f) throws FileNotFoundException, IOException
  {
    FileInputStream fin = new FileInputStream(f);
    MaterialProfile result = this.loadProfile(fin);
    fin.close();
    return result;
  }

  public MaterialProfile loadProfile(InputStream in)
  {
    XMLDecoder dec = new XMLDecoder(in);
    MaterialProfile result = (MaterialProfile) dec.readObject();
    return result;
  }

  /**
   * Get the value of materials
   *
   * @return the value of materials
   */
  public List<MaterialProfile> getMaterials()
  {
    return materials;
  }

  /**
   * Set the value of materials
   *
   * @param materials new value of materials
   */
  public void setMaterials(List<MaterialProfile> materials)
  {
    List<MaterialProfile> oldMaterials = this.materials;
    this.materials = materials;
    propertyChangeSupport.firePropertyChange(PROP_MATERIALS, oldMaterials, materials);
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

  /**
   * Saves the Material for the given LaserDevice
   * @param result
   * @param selectedLaserDevice 
   */
  public void saveProfile(MaterialProfile result, LaserDevice selectedLaserDevice) throws FileNotFoundException
  {
    this.saveProfile(result, new File(selectedLaserDevice.getMaterialsPath() + "/" + result.toString() + ".xml"));
  }

  public void deleteProfile(MaterialProfile m, LaserDevice selectedLaserDevice)
  {
    new File(selectedLaserDevice.getMaterialsPath() + "/" + m.toString() + ".xml").delete();
  }

  /**
   * Returns all Materials this Lasercutter supports
   * (without loading anything)
   * @param ld
   * @return 
   */
  public List<MaterialProfile> getMaterials(LaserDevice ld)
  {
    this.dir = new File(ld.getMaterialsPath() != null ? ld.getMaterialsPath() : ".VisiCut/materials");
    return this.loadFromDirectory(dir);
  }
}
