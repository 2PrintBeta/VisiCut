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
package com.t_oster.visicut.model;

import com.t_oster.liblasercut.LaserCutter;
import com.t_oster.liblasercut.drivers.EpilogZing;
import com.t_oster.visicut.gui.ImageListable;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A wrapper for the LaserCutter class which adds some Attributes
 * 
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class LaserDevice implements ImageListable
{

  protected String jobSentText = "Job was sent as '$jobname'\nPlease:\n-Close the lid\n-Turn on the Ventilation\n-And press 'start' on the Lasercutter $name";
  public static final String PROP_JOBSENTTEXT = "jobSentText";

  /**
   * Get the value of jobSentText
   *
   * @return the value of jobSentText
   */
  public String getJobSentText()
  {
    return jobSentText;
  }

  /**
   * Set the value of jobSentText
   *
   * @param jobSentText new value of jobSentText
   */
  public void setJobSentText(String jobSentText)
  {
    String oldJobSentText = this.jobSentText;
    this.jobSentText = jobSentText;
    getPropertyChangeSupport().firePropertyChange(PROP_JOBSENTTEXT, oldJobSentText, jobSentText);
  }

  
  protected String jobPrefix = "visicut ";
  public static final String PROP_JOBPREFIX = "jobPrefix";

  /**
   * Get the value of jobPrefix
   *
   * @return the value of jobPrefix
   */
  public String getJobPrefix()
  {
    return jobPrefix;
  }

  /**
   * Set the value of jobPrefix
   *
   * @param jobPrefix new value of jobPrefix
   */
  public void setJobPrefix(String jobPrefix)
  {
    String oldJobPrefix = this.jobPrefix;
    this.jobPrefix = jobPrefix;
    getPropertyChangeSupport().firePropertyChange(PROP_JOBPREFIX, oldJobPrefix, jobPrefix);
  }
  
  protected LaserCutter laserCutter = new EpilogZing();
  public static final String PROP_LASERCUTTER = "laserCutter";

  /**
   * Get the value of laserCutter
   *
   * @return the value of laserCutter
   */
  public LaserCutter getLaserCutter()
  {
    return laserCutter;
  }

  /**
   * Set the value of laserCutter
   *
   * @param laserCutter new value of laserCutter
   */
  public void setLaserCutter(LaserCutter laserCutter)
  {
    LaserCutter oldLaserCutter = this.laserCutter;
    this.laserCutter = laserCutter;
    getPropertyChangeSupport().firePropertyChange(PROP_LASERCUTTER, oldLaserCutter, laserCutter);
  }

  protected AffineTransform cameraCalibration = null;
  public static final String PROP_CAMERACALIBRATION = "cameraCalibration";

  /**
   * Get the value of cameraCalibration
   *
   * @return the value of cameraCalibration
   */
  public AffineTransform getCameraCalibration()
  {
    return cameraCalibration;
  }

  /**
   * Set the value of cameraCalibration
   *
   * @param cameraCalibration new value of cameraCalibration
   */
  public void setCameraCalibration(AffineTransform cameraCalibration)
  {
    AffineTransform oldCameraCalibration = this.cameraCalibration;
    this.cameraCalibration = cameraCalibration;
    getPropertyChangeSupport().firePropertyChange(PROP_CAMERACALIBRATION, oldCameraCalibration, cameraCalibration);
  }

  protected String cameraURL = null;
  public static final String PROP_CAMERAURL = "cameraURL";

  /**
   * Get the value of cameraURL
   *
   * @return the value of cameraURL
   */
  public String getCameraURL()
  {
    return cameraURL;
  }

  /**
   * Set the value of cameraURL
   *
   * @param cameraURL new value of cameraURL
   */
  public void setCameraURL(String cameraURL)
  {
    String oldCameraURL = this.cameraURL;
    this.cameraURL = cameraURL;
    getPropertyChangeSupport().firePropertyChange(PROP_CAMERAURL, oldCameraURL, cameraURL);
  }

  protected String thumbnailPath = null;
  public static final String PROP_THUMBNAILPATH = "thumbnailPath";

  /**
   * Get the value of thumbnailPath
   *
   * @return the value of thumbnailPath
   */
  public String getThumbnailPath()
  {
    return thumbnailPath;
  }

  /**
   * Set the value of thumbnailPath
   *
   * @param thumbnailPath new value of thumbnailPath
   */
  public void setThumbnailPath(String thumbnailPath)
  {
    String oldThumbnailPath = this.thumbnailPath;
    this.thumbnailPath = thumbnailPath;
    getPropertyChangeSupport().firePropertyChange(PROP_THUMBNAILPATH, oldThumbnailPath, thumbnailPath);
  }

  protected String description = null;
  public static final String PROP_DESCRIPTION = "description";

  /**
   * Get the value of description
   *
   * @return the value of description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Set the value of description
   *
   * @param description new value of description
   */
  public void setDescription(String description)
  {
    String oldDescription = this.description;
    this.description = description;
    getPropertyChangeSupport().firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
  }

  protected String name = null;
  public static final String PROP_NAME = "name";

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name)
  {
    String oldName = this.name;
    this.name = name;
    getPropertyChangeSupport().firePropertyChange(PROP_NAME, oldName, name);
  }
  private PropertyChangeSupport pcs = null;
  private PropertyChangeSupport getPropertyChangeSupport()
  {
    if (pcs == null)
    {
      pcs = new PropertyChangeSupport(this);
    }
    return pcs;
  }

  /**
   * Add PropertyChangeListener.
   *
   * @param listener
   */
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    getPropertyChangeSupport().addPropertyChangeListener(listener);
  }

  /**
   * Remove PropertyChangeListener.
   *
   * @param listener
   */
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    getPropertyChangeSupport().removePropertyChangeListener(listener);
  }

  @Override
  public LaserDevice clone()
  {
    LaserDevice result = new LaserDevice();
    result.cameraCalibration = cameraCalibration;
    result.cameraURL = cameraURL;
    result.description = description;
    result.name = name;
    result.laserCutter = laserCutter.clone();
    result.thumbnailPath = thumbnailPath;
    result.jobPrefix = jobPrefix;
    return result;
  }

  @Override
  public String toString()
  {
    return getName();
  }
  
}
