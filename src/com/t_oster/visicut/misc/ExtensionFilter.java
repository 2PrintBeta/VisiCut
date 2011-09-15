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
package com.t_oster.visicut.misc;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author thommy
 */
public class ExtensionFilter extends FileFilter
  {

    private String extension = ".xml";
    private String description = "XML Document";
    
    public ExtensionFilter(String extension, String description)
    {
      this.extension = extension;
      this.description = description;
    }

    @Override
    public boolean accept(File file)
    {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(extension);
    }

    @Override
    public String getDescription()
    {
      return description;
    }
}
