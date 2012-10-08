/**
 * This file is part of VisiCut.
 * Copyright (C) 2012 Thomas Oster <thomas.oster@rwth-aachen.de>
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
package com.t_oster.visicut.gui.mappingdialog;

import com.t_oster.visicut.misc.Helper;
import java.io.File;
import javax.swing.JRadioButton;

/**
 * This class provides a RadioButton which contains
 * an icon in the label through html support
 * 
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class JiconRadioButton extends JRadioButton
{

  private File icon;
  private String text;
  
  public void setLabelIcon(File icon)
  {
    this.icon = icon;
    this.setHtmlText();
  }
  
  public File getLabelIcon()
  {
    return this.icon;
  }
  
  public void setLabelText(String text)
  {
    this.text = text;
    setHtmlText();
  }
  
  public String getLabelText()
  {
    return this.text;
  }
  
  private void setHtmlText()
  {
    String label = "<html><table cellpadding=0><tr><td>";
    if (icon != null)
    {
      label +=Helper.imgTag(icon, 64, 64);
    }
    label+="</td><td width=3><td>";
    if (text != null)
    {
      label+=text;
    }
    label += "</td></tr></table></html>";
    super.setText(label);
  }
}
