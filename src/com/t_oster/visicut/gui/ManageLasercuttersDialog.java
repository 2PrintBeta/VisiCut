/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManageLasercuttersDialog.java
 *
 * Created on 06.09.2011, 21:24:37
 */
package com.t_oster.visicut.gui;

import com.t_oster.visicut.gui.EditLaserDeviceDialog;
import com.t_oster.visicut.model.LaserDevice;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author thommy
 */
public class ManageLasercuttersDialog extends javax.swing.JDialog
{

  public static final String PROP_DEFAULTLASERCUTTER = "defaultLaserCutter";

  /**
   * Get the value of defaultLaserCutter
   *
   * @return the value of defaultLaserCutter
   */
  public int getDefaultLaserCutter()
  {
    return defaultIndex;
  }

  /**
   * Set the value of defaultLaserCutter
   *
   * @param defaultLaserCutter new value of defaultLaserCutter
   */
  public void setDefaultLaserCutter(int idx)
  {
    int oldIdx = this.defaultIndex;
    this.defaultIndex = idx;
    firePropertyChange(PROP_DEFAULTLASERCUTTER, oldIdx, idx);
  }
  
  private int defaultIndex;
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  protected List<LaserDevice> laserCutters = null;
  public static final String PROP_LASERCUTTERS = "laserCutters";

  /**
   * Get the value of laserCutters
   *
   * @return the value of laserCutters
   */
  public List<LaserDevice> getLaserCutters()
  {
    return laserCutters;
  }

  /**
   * Set the value of laserCutters
   *
   * @param laserCutters new value of laserCutters
   */
  public void setLaserCutters(List<LaserDevice> laserCutters)
  {
    List<LaserDevice> oldLaserCutters = this.laserCutters;
    this.laserCutters = laserCutters;
    firePropertyChange(PROP_LASERCUTTERS, oldLaserCutters, laserCutters);
    List<LaserDevice> cur = new LinkedList<LaserDevice>();
    if (laserCutters != null)
    {
      for (LaserDevice l : laserCutters)
      {
        cur.add(((LaserDevice) l).clone());
      }
    }
    this.setCurrentLaserCutters(cur);
  }
  protected List<LaserDevice> currentLaserCutters = null;
  public static final String PROP_CURRENTLASERCUTTERS = "currentLaserCutters";

  /**
   * Get the value of currentLaserCutters
   *
   * @return the value of currentLaserCutters
   */
  public List<LaserDevice> getCurrentLaserCutters()
  {
    return currentLaserCutters;
  }

  /**
   * Set the value of currentLaserCutters
   *
   * @param currentLaserCutters new value of currentLaserCutters
   */
  public void setCurrentLaserCutters(List<LaserDevice> currentLaserCutters)
  {
    List<LaserDevice> oldCurrentLaserCutters = this.currentLaserCutters;
    this.currentLaserCutters = currentLaserCutters;
    firePropertyChange(PROP_CURRENTLASERCUTTERS, oldCurrentLaserCutters, currentLaserCutters);
  }

  private DefaultTableModel model;
  
  /** Creates new form ManageLasercuttersDialog */
  public ManageLasercuttersDialog(java.awt.Frame parent, boolean modal)
  {
    super(parent, modal);
    initComponents();
    model = new DefaultTableModel()
    {

      private String[] columns = new String[]
      {
        "Name", "IP", "Port", "Treiber", "Default"
      };

      @Override
      public Class<?> getColumnClass(int i)
      {
        return i<4 ? String.class : Boolean.class;
      }

      @Override
      public int getColumnCount()
      {
        return columns.length;
      }

      @Override
      public String getColumnName(int i)
      {
        return columns[i];
      }

      @Override
      public int getRowCount()
      {
        return ManageLasercuttersDialog.this.currentLaserCutters == null ? 0 : ManageLasercuttersDialog.this.currentLaserCutters.size();
      }

      @Override
      public void setValueAt(Object o, int y, int x)
      {
        LaserDevice c = ManageLasercuttersDialog.this.currentLaserCutters.get(y);
        switch (x)
        {
          case 0:
            c.setName((String) o);
            return;
          case 1:
            c.getLaserCutter().setHostname((String) o);
            return;
          case 2:
            c.getLaserCutter().setPort(Integer.parseInt((String) o));
            return;
          case 4:
            if ((Boolean) o.equals(true))
            {
              int old = ManageLasercuttersDialog.this.defaultIndex;
              ManageLasercuttersDialog.this.defaultIndex = y;
              this.fireTableCellUpdated(old, 4);
              this.fireTableCellUpdated(y, 4);
            }
            return;
        }
      }

      @Override
      public Object getValueAt(int y, int x)
      {
        LaserDevice c = ManageLasercuttersDialog.this.currentLaserCutters.get(y);
        switch (x)
        {
          case 0:
            return c.getName();
          case 1:
            return c.getLaserCutter().getHostname();
          case 2:
            return c.getLaserCutter().getPort();
          case 3:
          {
            String cls = c.getLaserCutter().getClass().toString();
            String[] parts = cls.split("\\.");
            if (parts.length > 1)
            {

              cls = parts[parts.length - 1];
            }
            return cls;
          }
          case 4:
            return (y==ManageLasercuttersDialog.this.defaultIndex);

        }
        return null;
      }

      @Override
      public boolean isCellEditable(int y, int x)
      {
        return x < 3 || x==4;
      }
    };
    this.jTable1.setModel(model);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.t_oster.visicut.gui.VisicutApp.class).getContext().getResourceMap(ManageLasercuttersDialog.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void jButton4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton4ActionPerformed
  {//GEN-HEADEREND:event_jButton4ActionPerformed
    this.setLaserCutters(this.getCurrentLaserCutters());
    this.setVisible(false);
  }//GEN-LAST:event_jButton4ActionPerformed

  private void jButton5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton5ActionPerformed
  {//GEN-HEADEREND:event_jButton5ActionPerformed
    this.setLaserCutters(null);
    this.setVisible(false);
  }//GEN-LAST:event_jButton5ActionPerformed

  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton3ActionPerformed
  {//GEN-HEADEREND:event_jButton3ActionPerformed
    int idx = this.jTable1.getSelectedRow();
    if (idx >= 0)
    {
      EditLaserDeviceDialog d = new EditLaserDeviceDialog(null, true);
      d.setLaserDevice(this.currentLaserCutters.get(idx));
      d.setVisible(true);
      LaserDevice result = d.getLaserDevice();
      if (result != null)
      {
        this.currentLaserCutters.set(idx, result);
        this.model.fireTableRowsUpdated(idx, idx);
      }
    }

  }//GEN-LAST:event_jButton3ActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
  {//GEN-HEADEREND:event_jButton1ActionPerformed
    LaserDevice neu = new LaserDevice();
    this.currentLaserCutters.add(neu);
    int idx = this.currentLaserCutters.size()-1;
    this.model.fireTableRowsInserted(idx, idx);
    this.jTable1.getSelectionModel().setSelectionInterval(idx, idx);
    this.jButton3ActionPerformed(evt);
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
  {//GEN-HEADEREND:event_jButton2ActionPerformed
    int idx = this.jTable1.getSelectedRow();
    if (idx >= 0)
    {
      this.currentLaserCutters.remove(idx);
      this.model.fireTableRowsDeleted(idx, idx);
    }
  }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
