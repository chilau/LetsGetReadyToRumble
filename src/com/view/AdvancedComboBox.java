package com.view;


import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class AdvancedComboBox<E> extends JComboBox implements MouseWheelListener {


    public AdvancedComboBox(E[] items) {
        super(items);

        addMouseWheelListener(this);
    }


    @Override
    public void setSelectedIndex(int anIndex) {
        if (anIndex < 0) {
            anIndex = 0;
        }

        if (anIndex > getItemCount() - 1) {
            anIndex = getItemCount() - 1;
        }

        super.setSelectedIndex(anIndex);
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        int wheelRotation = mouseWheelEvent.getWheelRotation();
        if (wheelRotation < 0) {
            if (getSelectedIndex() > 0) {
                setSelectedIndex(getSelectedIndex() - 1);
            }
        } else {
            if (getSelectedIndex() < getItemCount() - 1) {
                setSelectedIndex(getSelectedIndex() + 1);
            }
        }
    }

}
