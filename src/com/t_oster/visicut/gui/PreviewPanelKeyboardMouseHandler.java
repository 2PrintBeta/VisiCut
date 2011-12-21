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
package com.t_oster.visicut.gui;

import com.t_oster.visicut.gui.beans.EditRectangle;
import com.t_oster.visicut.gui.beans.EditRectangle.Button;
import com.t_oster.visicut.gui.beans.PreviewPanel;
import com.t_oster.visicut.misc.Helper;
import com.t_oster.visicut.model.graphicelements.GraphicSet;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This class handles the transformations to the background and the
 * selected graphics set, which are applyable via mouse in the preview panel
 * (Main View)
 * 
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class PreviewPanelKeyboardMouseHandler implements MouseListener, MouseMotionListener, KeyListener
{

  private PreviewPanel previewPanel;
  private JPopupMenu menu = new JPopupMenu();
  private JMenuItem resetMenuItem = new JMenuItem("Reset Transformation");

  public PreviewPanelKeyboardMouseHandler(PreviewPanel panel)
  {
    this.previewPanel = panel;
    this.previewPanel.addMouseListener(this);
    this.previewPanel.addMouseMotionListener(this);
    this.previewPanel.addKeyListener(this);
    menu.add(resetMenuItem);
    resetMenuItem.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent ae)
      {
        PreviewPanelKeyboardMouseHandler.this.getGraphicObjects().setTransform(
          PreviewPanelKeyboardMouseHandler.this.getGraphicObjects().getBasicTransform());
        PreviewPanelKeyboardMouseHandler.this.previewPanel.setEditRectangle(new EditRectangle(getGraphicObjects().getBoundingBox()));
        PreviewPanelKeyboardMouseHandler.this.previewPanel.repaint();
      }
    });
  }

  private EditRectangle getEditRect()
  {
    return this.previewPanel.getEditRectangle();
  }

  private GraphicSet getGraphicObjects()
  {
    return this.previewPanel.getGraphicObjects();
  }

  /**
   * For now this just returns all objects, but later maybe
   * multiple input files are supported and this should
   * only return the currently selected one
   * @return 
   */
  private GraphicSet getSelectedSet()
  {
    return this.previewPanel.getGraphicObjects();
  }

  public void keyTyped(KeyEvent key)
  {
  }

  public void keyPressed(KeyEvent ke)
  {
    if (this.getEditRect() != null)
    {
      int diffx=0;
      int diffy=0;
      switch (ke.getKeyCode())
      {
        case KeyEvent.VK_LEFT: diffx-=10;break;
        case KeyEvent.VK_RIGHT: diffx+=10;break;
        case KeyEvent.VK_UP: diffy-=10;break;
        case KeyEvent.VK_DOWN: diffy+=10;break;
      }
      this.moveSet(diffx,diffy);
    }
  }

  public void keyReleased(KeyEvent ke)
  {
  }

  private enum MouseAction
  {

    movingBackground,
    movingSet,
    resizingSet,
    rotatingSet
  };
  private Point lastMousePosition = null;
  private MouseAction currentAction = null;
  private Button currentButton = null;

  public void mouseClicked(MouseEvent me)
  {
    this.previewPanel.requestFocus();
    if (me.getButton() == MouseEvent.BUTTON1)
    {
      boolean onGraphic = false;
      if (getGraphicObjects() != null && getGraphicObjects().getBoundingBox() != null)
      {
        Rectangle2D bb = getGraphicObjects().getBoundingBox();
        Rectangle2D e = Helper.transform(bb, this.previewPanel.getLastDrawnTransform());
        onGraphic = e.contains(me.getPoint());
      }
      if (onGraphic)
      {//clicked on the graphic
        if (getEditRect() != null)
        {//Already selected => toggle rotate/scale mode
          //getEditRect().setRotateMode(!getEditRect().isRotateMode());
          //this.previewPanel.repaint();
        }
        else
        {//not yet select => select in scale mode
          this.previewPanel.setEditRectangle(new EditRectangle(getGraphicObjects().getBoundingBox()));
        }
      }
      else
      {//clicked next to graphic => clear selection
        this.previewPanel.setEditRectangle(null);
      }
    }
    else if (getEditRect() != null && me.getButton() == MouseEvent.BUTTON3)
    {
      Rectangle2D bb = getGraphicObjects().getBoundingBox();
      Rectangle2D e = Helper.transform(bb, this.previewPanel.getLastDrawnTransform());
      if (e.contains(me.getPoint()))
      {
        this.menu.show(this.previewPanel, me.getX(), me.getY());
      }
    }
  }

  public void mousePressed(MouseEvent evt)
  {
    lastMousePosition = evt.getPoint();
    currentAction = MouseAction.movingBackground;
    if (getEditRect() != null)
    {//something selected
      Rectangle2D curRect = Helper.transform(getEditRect(), this.previewPanel.getLastDrawnTransform());
      Button b = getEditRect().getButtonByPoint(lastMousePosition, this.previewPanel.getLastDrawnTransform());
      if (b != null)
      {//a button selected
        currentButton = b;
        currentAction = getEditRect().isRotateMode() ? MouseAction.rotatingSet : MouseAction.resizingSet;
      }
      else
      {//no button selected
        if (curRect.contains(lastMousePosition))
        {//selection in the rectangle
          currentAction = MouseAction.movingSet;
        }
        else
        {
          currentAction = MouseAction.movingBackground;
        }
      }
    }
  }

  public void mouseReleased(MouseEvent evt)
  {
    if (currentAction == MouseAction.resizingSet)
    {
      //Apply changes to the EditRectangle to the getSelectedSet()
      Rectangle2D src = getSelectedSet().getOriginalBoundingBox();
      getSelectedSet().setTransform(Helper.getTransform(src, getEditRect()));
      this.previewPanel.repaint();
    }
    lastMousePosition = evt.getPoint();
  }

  public void mouseEntered(MouseEvent me)
  {
  }

  public void mouseExited(MouseEvent me)
  {
  }

  public void mouseDragged(MouseEvent evt)
  {
    if (lastMousePosition != null)
    {
      Point diff = new Point(evt.getPoint().x - lastMousePosition.x, evt.getPoint().y - lastMousePosition.y);
      try
      {
        switch (currentAction)
        {
          case rotatingSet:
          {
            Rectangle2D bb = getGraphicObjects().getBoundingBox();
            Point2D middle = new Point.Double(bb.getCenterX(), bb.getCenterY());
            //move back
            AffineTransform tr = AffineTransform.getTranslateInstance(middle.getX(), middle.getY());
            //rotate
            tr.concatenate(AffineTransform.getRotateInstance(1.0 / 100 * Math.max(diff.x, diff.y)));
            //center
            tr.concatenate(AffineTransform.getTranslateInstance(-middle.getX(), -middle.getY()));
            //apply current
            tr.concatenate(getGraphicObjects().transform);
            getGraphicObjects().setTransform(tr);
            break;
          }
          case resizingSet:
          {
            this.previewPanel.getLastDrawnTransform().createInverse().deltaTransform(diff, diff);
            switch (currentButton)
            {
              case BOTTOM_RIGHT:
              {
                int offset = Math.abs(diff.x) > Math.abs(diff.y) ? diff.x : diff.y;
                getEditRect().height += (offset * getEditRect().height / getEditRect().width);
                getEditRect().width += offset;
                break;
              }
              case BOTTOM_LEFT:
              {
                int offset = Math.abs(diff.x) > Math.abs(diff.y) ? diff.x : diff.y;
                getEditRect().height -= (offset * getEditRect().height / getEditRect().width);
                getEditRect().x += offset;
                getEditRect().width -= offset;
                break;
              }
              case TOP_RIGHT:
              {
                int offset = Math.abs(diff.x) > Math.abs(diff.y) ? diff.x : -diff.y;
                getEditRect().y -= (offset * getEditRect().height / getEditRect().width);
                getEditRect().height += (offset * getEditRect().height / getEditRect().width);
                getEditRect().width += offset;
                break;
              }
              case TOP_LEFT:
              {
                int offset = Math.abs(diff.x) > Math.abs(diff.y) ? diff.x : diff.y;
                getEditRect().y += (offset * getEditRect().height / getEditRect().width);
                getEditRect().height -= (offset * getEditRect().height / getEditRect().width);
                getEditRect().x += offset;
                getEditRect().width -= offset;
                break;
              }
              case CENTER_RIGHT:
              {
                this.getEditRect().width += diff.x;
                break;
              }
              case TOP_CENTER:
              {
                this.getEditRect().y += diff.y;
                this.getEditRect().height -= diff.y;
                break;
              }
              case BOTTOM_CENTER:
              {
                this.getEditRect().height += diff.y;
                break;
              }
              case CENTER_LEFT:
              {
                this.getEditRect().x += diff.x;
                this.getEditRect().width -= diff.x;
                break;
              }
            }
            this.previewPanel.setEditRectangle(getEditRect());
            break;
          }
          case movingSet:
          {
            this.previewPanel.getLastDrawnTransform().createInverse().deltaTransform(diff, diff);
            this.moveSet(diff.x, diff.y);
            break;
          }
          case movingBackground:
          {
            this.previewPanel.getLastDrawnTransform().createInverse().deltaTransform(diff, diff);
            Point center = this.previewPanel.getCenter();
            center.translate(-diff.x, -diff.y);
            this.previewPanel.setCenter(center);
            break;
          }
        }
        this.previewPanel.repaint();
      }
      catch (NoninvertibleTransformException ex)
      {
        Logger.getLogger(PreviewPanelKeyboardMouseHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
      lastMousePosition = evt.getPoint();
    }
  }

  private void moveSet(int diffX, int diffY)
  {
    if (diffX == 0 && diffY == 0)
    {
      return;
    }
    if (getSelectedSet().getTransform() != null)
    {
      AffineTransform tr = AffineTransform.getTranslateInstance(diffX, diffY);
      tr.concatenate(getSelectedSet().getTransform());
      getSelectedSet().setTransform(tr);
    }
    else
    {
      getSelectedSet().setTransform(AffineTransform.getTranslateInstance(diffX, diffY));
    }
    Rectangle2D bb = getSelectedSet().getBoundingBox();
    this.previewPanel.setEditRectangle(new EditRectangle(bb));
  }

  public void mouseMoved(MouseEvent evt)
  {
    setCursor(evt.getPoint());
  }

  private void setCursor(Point p)
  {
    int cursor = Cursor.DEFAULT_CURSOR;
    cursorcheck:
    {
      if (this.previewPanel.getGraphicObjects() != null)
      {
        if (getEditRect() != null)
        {
          Button b = getEditRect().getButtonByPoint(p, this.previewPanel.getLastDrawnTransform());
          if (b != null)
          {
            if (getEditRect().isRotateMode())
            {
              for (Button bb : getEditRect().rotateButtons)
              {
                if (bb.equals(b))
                {//TODO: Create rotate cursor
                  cursor = Cursor.CROSSHAIR_CURSOR;
                  break;
                }
              }
              break cursorcheck;
            }
            switch (b)
            {
              case TOP_RIGHT:
                cursor = Cursor.NE_RESIZE_CURSOR;
                break cursorcheck;
              case CENTER_RIGHT:
                cursor = Cursor.E_RESIZE_CURSOR;
                break cursorcheck;
              case BOTTOM_RIGHT:
                cursor = Cursor.SE_RESIZE_CURSOR;
                break cursorcheck;
              case BOTTOM_CENTER:
                cursor = Cursor.S_RESIZE_CURSOR;
                break cursorcheck;
              case BOTTOM_LEFT:
                cursor = Cursor.SW_RESIZE_CURSOR;
                break cursorcheck;
              case CENTER_LEFT:
                cursor = Cursor.W_RESIZE_CURSOR;
                break cursorcheck;
              case TOP_LEFT:
                cursor = Cursor.NW_RESIZE_CURSOR;
                break cursorcheck;
              case TOP_CENTER:
                cursor = Cursor.N_RESIZE_CURSOR;
                break cursorcheck;
            }
          }
        }
        Rectangle2D bb = this.previewPanel.getGraphicObjects().getBoundingBox();
        if (bb != null)
        {
          Rectangle2D e = Helper.transform(bb, this.previewPanel.getLastDrawnTransform());
          if (e.contains(p))
          {
            cursor = this.getEditRect() == null ? Cursor.HAND_CURSOR : Cursor.MOVE_CURSOR;
            break cursorcheck;
          }
        }
      }
    }
    this.previewPanel.setCursor(Cursor.getPredefinedCursor(cursor));
  }
}
