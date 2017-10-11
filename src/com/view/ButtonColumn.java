package com.view;


import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;


public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {

    private JTable _table;
    private Action _action;

    private JButton _renderButton;
    private JButton _editButton;
    private Object _editorValue;
    private boolean _isButtonColumnEditor;

    //private String _title;
    private String _iconUrl;


    ButtonColumn(JTable table, Action action, int column, String iconUrl) {
        _table = table;
        _action = action;
        //_title = title;
        _iconUrl = iconUrl;

        _renderButton = new JButton();
        _editButton = new JButton();
        _editButton.setFocusPainted(false);
        _editButton.addActionListener(this);

        _renderButton.setBackground(Color.WHITE);
        _editButton.setBackground(Color.WHITE);

        _renderButton.setText("");
        _editButton.setText("");

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
        table.addMouseListener(this);
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        updateButtonText(_editButton);

        _editorValue = value;
        return _editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return _editorValue;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            _renderButton.setForeground(table.getSelectionForeground());
            _renderButton.setBackground(table.getSelectionBackground());
        } else {
            _renderButton.setForeground(table.getForeground());
            _renderButton.setBackground(UIManager.getColor("Button.background"));
        }

        updateButtonText(_renderButton);
        return _renderButton;
    }

    private void updateButtonText(JButton button) {
        button.setText("");
        button.setIcon(createImageIcon(_iconUrl));
    }

    private ImageIcon createImageIcon(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "");
        } else {
            return null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        ActionEvent event = new ActionEvent(_table, ActionEvent.ACTION_PERFORMED, String.valueOf(_editorValue));
        _action.actionPerformed(event);
    }

    public void mousePressed(MouseEvent e) {
        if (_table.isEditing() && _table.getCellEditor() == this) {
            _isButtonColumnEditor = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (_isButtonColumnEditor && _table.isEditing()) {
            _table.getCellEditor().stopCellEditing();
        }

        _isButtonColumnEditor = false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
