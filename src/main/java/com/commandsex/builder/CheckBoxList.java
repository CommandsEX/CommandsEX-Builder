package com.commandsex.builder;

/** http://www.java2s.com/Tutorial/Java/0240__Swing/CheckBoxList.htm **/

/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved. 
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 *
 *
 *
 *
 * CheckBoxList.java
 * 
 * Created on October 5, 2006, 9:53 AM
 *
 */

/**
 *
 * @author gtoffoli
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckBoxList extends JList {

    public CheckBoxList() {
        super();

        setModel(new DefaultListModel());
        setCellRenderer(new CheckboxCellRenderer());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    Object obj = getModel().getElementAt(index);
                    if (obj instanceof JCheckBox) {
                        JCheckBox checkbox = (JCheckBox) obj;

                        if (checkbox.isEnabled()){
                            checkbox.setSelected(!checkbox.isSelected());
                            repaint();
                        }
                    }
                }
            }
        }

        );

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @SuppressWarnings("unchecked")
    public int[] getCheckedIdexes() {
        java.util.List list = new java.util.ArrayList();
        ListModel dlm = getModel();
        for (int i = 0; i < dlm.getSize(); ++i) {
            Object obj = getModel().getElementAt(i);
            if (obj instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) obj;
                if (checkbox.isSelected()) {
                    list.add(new Integer(i));
                }
            }
        }

        int[] indexes = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            indexes[i] = ((Integer) list.get(i)).intValue();
        }

        return indexes;
    }

    public List<JCheckBox> getAllItems(){
        List<JCheckBox> list = new ArrayList<JCheckBox>();
        ListModel dlm = getModel();
        for (int i = 0; i < dlm.getSize(); i++){
            Object obj = getModel().getElementAt(i);
            if (obj instanceof JCheckBox){
                JCheckBox checkBox = (JCheckBox) obj;
                list.add(checkBox);
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public java.util.List getCheckedItems() {
        java.util.List list = new java.util.ArrayList();

        for (JCheckBox checkBox : getAllItems()){
            if (checkBox.isSelected()){
                list.add(checkBox);
            }
        }

        return list;
    }
}

/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from JasperSoft, the
 * following license terms apply:
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see http://www.gnu.org/licenses/gpl.txt or write to:
 * 
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA USA
 * 02111-1307
 * 
 * 
 * 
 * 
 * CheckboxCellRenderer.java
 * 
 * Created on October 5, 2006, 10:03 AM
 * 
 */

/**
 *
 * @author gtoffoli
 */
class CheckboxCellRenderer extends DefaultListCellRenderer {
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (value instanceof CheckBoxListEntry) {
            CheckBoxListEntry checkbox = (CheckBoxListEntry) value;
            checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            if (checkbox.isRed()) {
                checkbox.setForeground(Color.red);
            } else if (!isEnabled()) {
                checkbox.setForeground(Color.gray);
            } else {
                checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            }

            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder")
                    : noFocusBorder);

            return checkbox;
        } else {
            return super.getListCellRendererComponent(list, value.getClass().getName(), index,
                    isSelected, cellHasFocus);
        }
    }
}

/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from JasperSoft, the
 * following license terms apply:
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see http://www.gnu.org/licenses/gpl.txt or write to:
 * 
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA USA
 * 02111-1307
 * 
 * 
 * 
 * 
 * CheckBoxListEntry.java
 * 
 * Created on October 5, 2006, 10:19 AM
 * 
 */

/**
 *
 * @author gtoffoli
 */
class CheckBoxListEntry extends JCheckBox {

    private Object value = null;

    private boolean red = false;

    public CheckBoxListEntry(Object itemValue, boolean selected) {
        super(itemValue == null ? "" : "" + itemValue, selected);
        setValue(itemValue);
    }

    public boolean isSelected() {
        return super.isSelected();
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRed() {
        return red;
    }

    public void setRed(boolean red) {
        this.red = red;
    }

}