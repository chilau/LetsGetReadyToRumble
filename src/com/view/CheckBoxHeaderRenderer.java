package com.view;


import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CheckBoxHeaderRenderer extends JToggleButton implements TableCellRenderer {

    private JTable _table = null;
    private MouseEventReposter _reposter = null;
    private JCheckBox _component;

    private TableModel _tableModel;
    private TableColumnModel _tableColumnModel;
    private JTableHeader _tableHeader;

    private int _viewColumn;
    private int _targetColumn;


    CheckBoxHeaderRenderer(JTable table, int targetColumn) {
        _component = new JCheckBox("");
        _component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        _targetColumn = targetColumn;

        _tableModel = table.getModel();
        _tableHeader = table.getTableHeader();
        _tableColumnModel = table.getColumnModel();

        addItemListener(new CheckBoxHeaderRenderer.ItemHandler());
        
        _tableHeader.addMouseListener(new CheckBoxHeaderRenderer.MouseHandler());
        _tableModel.addTableModelListener(new CheckBoxHeaderRenderer.ModelHandler());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (table != null && _table != table) {
            _table = table;

            final JTableHeader tableHeader = table.getTableHeader();
            if (tableHeader != null) {
                _component.setForeground(tableHeader.getForeground());
                _component.setBackground(tableHeader.getBackground());
                _component.setFont(tableHeader.getFont());
                _reposter = new MouseEventReposter(tableHeader, col, _component);

                tableHeader.addMouseListener(_reposter);
            }
        }

        if (_reposter != null) {
            _reposter.setColumn(col);
        }

        return _component;
    }

    private class ItemHandler implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (_tableModel == null) {
                return;
            }

            if (_tableModel.getClass().equals(RacersTableModel.class)) {
                RacersTableModel tableModel = (RacersTableModel) _tableModel;
                if (tableModel.getRowCount() == 0) {
                    return;
                }
            }

            boolean state = itemEvent.getStateChange() == ItemEvent.SELECTED;
            _component.setSelected(state);

            if (_table != null) {
                for (int r = 0; r < _table.getRowCount(); r++) {
                    _table.setValueAt(state, r, _viewColumn);
                }
            }
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            _viewColumn = _tableHeader.columnAtPoint(mouseEvent.getPoint());
            int modelColumn = _tableColumnModel.getColumn(_viewColumn).getModelIndex();
            if (modelColumn == _targetColumn) {
                doClick();
            }
        }
    }

    private class ModelHandler implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (needsToggle()) {
                doClick();

                _tableHeader.repaint();
            }
        }
    }

    private boolean needsToggle() {
        boolean allTrue = true;
        boolean allFalse = true;

        for (int rowIndex = 0; rowIndex < _tableModel.getRowCount(); rowIndex++) {
            boolean value = (Boolean) _tableModel.getValueAt(rowIndex, _targetColumn);

            allTrue &= value;
            allFalse &= !value;
        }

        return allTrue && !isSelected() || allFalse && isSelected();
    }


    static public class MouseEventReposter extends MouseAdapter {

        private Component _dispatchComponent;
        private JTableHeader _header;
        private int _column  = -1;
        private Component _editor;

        public MouseEventReposter(JTableHeader header, int column, Component editor) {
            _header = header;
            _column = column;
            _editor = editor;
        }

        public void setColumn(int column) {
            _column = column;
        }

        private void setDispatchComponent(MouseEvent mouseEvent) {
            int column = _header.getTable().columnAtPoint(mouseEvent.getPoint());
            if (column != _column || column == -1) {
                return;
            }

            Point point = mouseEvent.getPoint();
            Point point2 = SwingUtilities.convertPoint(_header, point, _editor);

            _dispatchComponent = SwingUtilities.getDeepestComponentAt(_editor, point2.x, point2.y);
        }

        private void repostEvent(MouseEvent mouseEvent) {
            if (_dispatchComponent == null) {
                return;
            }

            MouseEvent convertedMouseEvent = SwingUtilities.convertMouseEvent(_header, mouseEvent, _dispatchComponent);
            _dispatchComponent.dispatchEvent(convertedMouseEvent);
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (_header.getResizingColumn() == null) {
                Point point = mouseEvent.getPoint();

                int column = _header.getTable().columnAtPoint(point);
                if (column != _column || column == -1) {
                    return;
                }

                int columnIndex = _header.getColumnModel().getColumnIndexAtX(point.x);
                if (columnIndex == -1) {
                    return;
                }

                _editor.setBounds(_header.getHeaderRect(columnIndex));
                _header.add(_editor);
                _editor.validate();

                setDispatchComponent(mouseEvent);
                repostEvent(mouseEvent);
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            repostEvent(mouseEvent);

            _dispatchComponent = null;
            _header.remove(_editor);
        }
    }

}
