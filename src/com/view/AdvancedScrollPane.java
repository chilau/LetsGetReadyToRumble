package com.view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class AdvancedScrollPane extends JScrollPane {


    public AdvancedScrollPane(Component view) {
        super(view);

        addMouseWheelListener(new PDMouseWheelListener());
    }

    class PDMouseWheelListener implements MouseWheelListener {

        private JScrollBar bar;
        private int previousValue = 0;
        private JScrollPane parentScrollPane;

        private JScrollPane getParentScrollPane() {
            if (parentScrollPane == null) {
                Component parent = getParent();
                while (!(parent instanceof JScrollPane) && parent != null) {
                    parent = parent.getParent();
                }
                parentScrollPane = (JScrollPane)parent;
            }
            return parentScrollPane;
        }

        public PDMouseWheelListener() {
            bar = AdvancedScrollPane.this.getVerticalScrollBar();
        }
        public void mouseWheelMoved(MouseWheelEvent event) {
            JScrollPane parent = getParentScrollPane();
            if (parent != null) {
            /*
             * Only dispatch if we have reached top/bottom on previous scroll
             */
                if (event.getWheelRotation() < 0) {
                    if (bar.getValue() == 0 && previousValue == 0) {
                        parent.dispatchEvent(cloneEvent(event));
                    }
                } else {
                    if (bar.getValue() == getMax() && previousValue == getMax()) {
                        parent.dispatchEvent(cloneEvent(event));
                    }
                }
                previousValue = bar.getValue();
            }
        /*
         * If parent scrollpane doesn't exist, remove this as a listener.
         * We have to defer this till now (vs doing it in constructor)
         * because in the constructor this item has no parent yet.
         */
            else {
                AdvancedScrollPane.this.removeMouseWheelListener(this);
            }
        }
        private int getMax() {
            return bar.getMaximum() - bar.getVisibleAmount();
        }
        private MouseWheelEvent cloneEvent(MouseWheelEvent e) {
            return new MouseWheelEvent(getParentScrollPane(), e.getID(), e
                    .getWhen(), e.getModifiers(), 1, 1, e
                    .getClickCount(), false, e.getScrollType(), e
                    .getScrollAmount(), e.getWheelRotation());
        }
    }

}
