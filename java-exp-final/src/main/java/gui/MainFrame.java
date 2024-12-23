package gui;

import service.StudentService;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static constant.CommonConstant.*;

public class MainFrame extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JTextField searchField;
    private Vector<Vector<String>> tempData;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle(Title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // 初始化组件
        initComponents();

        setLayout(new BorderLayout());
        add(new JScrollPane(studentTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
    }

    private void initComponents() {
        // 初始化表格数据
        tempData =  StudentService.loads();

        // 创建表格数据模型
        tableModel = new DefaultTableModel(tempData, Header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // 第一列不可编辑
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.getTableHeader().setReorderingAllowed(false);

        addButton = new JButton("添加");
        deleteButton = new JButton("删除");
        clearButton = new JButton("重置");
        searchButton = new JButton("查询");
        searchField = new JTextField(15);

        addButton.addActionListener(e -> addStudentBnClicked());
        deleteButton.addActionListener(e -> deleteStudentBnClicked());
        clearButton.addActionListener(e -> clearBnClicked());
        searchButton.addActionListener(e -> searchStudentBnClicked());

        tableModel.addTableModelListener(this::onTableChanged);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(new JLabel("关键字:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(clearButton);
        return controlPanel;
    }

    private Vector<Vector<String>> getTableData() {
        Vector<Vector<String>> data = new Vector<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Vector<String> row = new Vector<>();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                row.add((String) tableModel.getValueAt(i, j));
            }
            data.add(row);
        }
        return data;
    }

    private boolean isAdding() {
        for (Vector<String> row : getTableData()) {
            if (row.contains(Filling)) {
                return true;
            }
        }
        return false;
    }

    public void setData(Vector<Vector<String>> data) {
        tableModel.setDataVector(data, Header);
    }

    private void addStudentBnClicked() {
        if (!isAdding()) {
            Vector<String> newRow = new Vector<>(List.of(
                    StudentService.randomId(), Filling, Filling, Filling
            ));
            tableModel.addRow(newRow);
        }
    }

    private void deleteStudentBnClicked() {
        List<String> ids = getSelectedStudentIds();
        if (ids == null) {
            JOptionPane.showMessageDialog(this, "未选中");
            return;
        }
        tempData.removeIf(row -> ids.contains(row.get(0)));
        reload();
    }

    private void reload() {
        setData(tempData);
    }

    private void clearBnClicked() {
        searchField.setText("");
        searchStudentBnClicked();
    }

    private void searchStudentBnClicked() {
        String query = searchField.getText();
        Vector<Vector<String>> filteredData = new Vector<>();
        for (Vector<String> row : tempData) {
            if (row.toString().contains(query)) {
                filteredData.add(row);
            }
        }
        setData(filteredData);
    }

    private void onTableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (column != TableModelEvent.ALL_COLUMNS && row >= 0) {
            String newValue = (String) tableModel.getValueAt(row, column);
            tempData.get(row).set(column, newValue);
        }
        save();
    }

    private void save() {
        if (isAdding()) {
            StudentService.save(new Vector<>(tempData.subList(0, tempData.size() - 1)));
        } else {
            StudentService.save(tempData);
        }
    }



    private List<String> getSelectedStudentIds() {
        int[] selectedRows = studentTable.getSelectedRows();
        if (selectedRows.length == 0) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (int row : selectedRows) {
            ids.add((String) studentTable.getValueAt(row, 0));
        }
        return ids;
    }
}
