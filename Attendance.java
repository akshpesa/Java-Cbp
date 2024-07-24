package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class Attendance {
    private JTextField nameData;
    private JTextField totalClasses;
    private JTable table1;
    private JButton ADDRECORDButton;
    private JButton UPDATERECORDButton;
    private JPanel resultPanel;
    private JComboBox<String> subject;
    private JTextField attendance;
    private int totalMarks = 0;
    JFrame attendF = new JFrame();

    public Attendance() {
        attendF.setContentPane(resultPanel);
        attendF.pack();
        attendF.setLocationRelativeTo(null);
        attendF.setVisible(true);
        tableData();
        
        ADDRECORDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameData.getText().equals("") || attendance.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please Fill NAME and Attendance Fields to add Record.");
                } else {
                    try {
                        String sql = "INSERT INTO attendance (NAME, SUBJECT, TOTAL_CLASSES, CLASSES_ATTENDED, TOTAL_ATTENDANCE) VALUES (?, ?, ?, ?, ?)";
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "your_username", "your_password");
                        PreparedStatement statement = connection.prepareStatement(sql);
                        double attend = (Double.parseDouble(attendance.getText()) / Double.parseDouble(totalClasses.getText())) * 100.0;
                        statement.setString(1, nameData.getText());
                        statement.setString(2, (String) subject.getSelectedItem());
                        statement.setString(3, totalClasses.getText());
                        statement.setString(4, attendance.getText());
                        statement.setString(5, String.format("%.2f", attend) + "%");
                        statement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "STUDENT ADDED SUCCESSFULLY");
                        attendance.setText("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                    tableData();
                }
            }
        });
        
        UPDATERECORDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "";
                double attend = (Double.parseDouble(attendance.getText()) / Double.parseDouble(totalClasses.getText())) * 100.0;
                String attend1 = String.format("%.2f", attend) + "%";
                try {
                    String subjectSelected = (String) subject.getSelectedItem();
                    sql = "UPDATE attendance SET CLASSES_ATTENDED = ?, TOTAL_ATTENDANCE = ? WHERE NAME = ? AND SUBJECT = ?";
                    
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "your_username", "your_password");
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, attendance.getText());
                    statement.setString(2, attend1);
                    statement.setString(3, nameData.getText());
                    statement.setString(4, subjectSelected);
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Attendance Updated Successfully");
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(null, e2.getMessage());
                }
                tableData();
            }
        });
        
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel dm = (DefaultTableModel) table1.getModel();
                int rowIndex = table1.getSelectedRow();
                nameData.setText(dm.getValueAt(rowIndex, 0).toString());
                attendance.setText(dm.getValueAt(rowIndex, 3).toString());
                totalClasses.setText(dm.getValueAt(rowIndex, 2).toString());
            }
        });
    }

    public void tableData() {
        try {
            String a = "SELECT * FROM attendance";
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "your_username", "your_password");
            Statement resultQuery = connection.createStatement();
            ResultSet queryResult = resultQuery.executeQuery(a);
            table1.setModel(buildTableModel(queryResult));
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(null, ex1.getMessage());
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}
