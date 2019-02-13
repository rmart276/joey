import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Flooring extends JFrame implements ActionListener, ChangeListener {

    JTextField nameTextField, addressTextField, lengthTextField, widthTextField, areaTextField, costTextField; // Text fields
    JButton listButton = new JButton("Order List"); // Order List Button
    JButton calculateButton = new JButton("Calculate"); // Calculate Button
    JButton submitButton = new JButton("Submit Order"); // Submit Button
    ButtonGroup btngroup = new ButtonGroup();
    JTextArea summaryTextArea = new JTextArea(5, 25); // dispay summary
    String floorType = "";
    int cost;

    Flooring() {
        super("Flooring Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(getContentPane());
        setSize(320, 200);
        //pack();
        setVisible(true);
    }

    public void addComponentsToPane(Container contentPane) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add customer tab to the frame
        JPanel customerPanel = customerTab();
        tabbedPane.addTab("Customer", customerPanel);

        // Add flooring tab to the frame
        JPanel floorPanel = floorTab();
        tabbedPane.addTab("Flooring", floorPanel);

        // Add calculate tab to the frame
        JPanel calculatePanel = calculateTab();
        tabbedPane.addTab("Calculate", calculatePanel);

        // Add total tab to the frame
        JPanel summaryPanel = summaryTab();
        tabbedPane.addTab("Summary", summaryPanel);

        tabbedPane.addChangeListener(this);

        add(tabbedPane);
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int index = sourceTabbedPane.getSelectedIndex();

        if (sourceTabbedPane.getTitleAt(index).equalsIgnoreCase("Summary")) {
            // Get type of the floor
            ButtonModel b = btngroup.getSelection();
            if (b != null) {
                floorType = b.getActionCommand();
            }

            String result = "Customer name: " + nameTextField.getText() + "\nCustomer Address: " + addressTextField.getText() + "\nFlooring Type: " + floorType + "\nFloor Length: " + lengthTextField.getText() + " ft" + "\nFloor Width: " + widthTextField.getText() + " ft" + "\nFlooring Cost: " + costTextField.getText() + "\nFlooring Area: " + areaTextField.getText();
            summaryTextArea.setText(result);

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String action = e.getActionCommand();
        if (action.equalsIgnoreCase("Order List")) {
            listOrders();
        }

        if (action.equalsIgnoreCase("Calculate")) {
            calculateOrder();
        }

        if (action.equalsIgnoreCase("Submit Order")) {
            submitOrders();
        }

    }

    /**
     * Create a database connection
     */
    public static Connection getConnection() {
        // JDBC driver name and database URL
        String DB_CONN_STRING = "jdbc:mysql://localhost:3306/test";
        String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

        // Database credentials
        Properties info = new Properties();
        info.put("user", "root");
        info.put("password", "");

        Connection result = null;
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        } catch (Exception ex) {
            System.out.println("Error loading driver: " + DRIVER_CLASS_NAME);
        }

        try {
            result = DriverManager.getConnection(DB_CONN_STRING, info);
        } catch (SQLException ex) {
            System.out.println("Driver loaded, but cannot connect to db: " + DB_CONN_STRING);
        }
        return result;
    }

    public void submitOrders() {

        int length, width, area;
        // Get type of the floor
        ButtonModel b = btngroup.getSelection();
        if (b != null) {
            floorType = b.getActionCommand();
        }

        if (nameTextField.getText().isEmpty() || addressTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Input name and address of the customer");
            return;
        }

        if (floorType.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Select type of flooring");
            return;
        }

        try {
            length = Integer.parseInt(lengthTextField.getText());
            width = Integer.parseInt(widthTextField.getText());
            area = length * width;
            //areaTextField.setText(String.valueOf(area) + " ft2");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid Length and width of floor");
            return;
        }

        calculateOrder();

        Connection conn = getConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "INSERT INTO Flooring "
                    + "VALUES ('" + nameTextField.getText() + "', '" + addressTextField.getText() + "', '" + floorType + "','" + area + "','" + cost + "')";

            stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(null, "Order Added");
            stmt.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public void calculateOrder() {
        // Area = length x width
        int length, width, area;

        // Get type of the floor
        ButtonModel b = btngroup.getSelection();
        if (b != null) {
            floorType = b.getActionCommand();
        }

        try {
            length = Integer.parseInt(lengthTextField.getText());
            width = Integer.parseInt(widthTextField.getText());
            area = length * width;
            areaTextField.setText(String.valueOf(area) + " ft2");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "PLease enter valid Length and width of floor");
            return;
        }

        if (floorType.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Select type of flooring");
        } else {
            if (floorType.equalsIgnoreCase("Wood")) {
                cost = area * 20;
                costTextField.setText("$" + String.valueOf(area * 20));
                //area*20
            } else {
                cost = area * 10;
                costTextField.setText("$" + String.valueOf(area * 10));
                //area*10
            }
        }

    }

    public void listOrders() {
        JFrame frame = new JFrame("Order Summary");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 200);
        JTextArea summaryTextArea = new JTextArea();
        //JTable table = new JTable();
        String result = "";

        frame.setVisible(true);

        String[] columnNames = {"Name",
            "Address",
            "Floor Type",
            "Area",
            "Cost"};

        Object[][] data = null;

        Connection conn = getConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            String select;
            select = "SELECT * from flooring";
            ResultSet rs = stmt.executeQuery(select);
            int i = 0;

            int totalRows = 0;
            try {
                rs.last();
                totalRows = rs.getRow();
                rs.beforeFirst();
            } catch (Exception ex) {
            }
            
            data = new Object[totalRows][5];

            while (rs.next()) {
                for (int j = 0; j < 5; j++) {
                    data[i][j] = rs.getString(j + 1);
                }
                i += 1;
            }
            stmt.close();
            conn.close();

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollpane = new JScrollPane(table);
            frame.getContentPane().add(scrollpane);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        summaryTextArea.setText(result);
    }

    public JPanel customerTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Welcome to Flooring Application"));
        panel.add(new JLabel("Customer Name: "));
        nameTextField = new JTextField(15);
        panel.add(nameTextField);
        panel.add(new JLabel("Customer Address: "));
        addressTextField = new JTextField(15);
        panel.add(addressTextField);
        panel.add(new JLabel(" "));
        panel.add(new JLabel(" "));

        return panel;
    }

    public JPanel floorTab() {
        JPanel panel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        panel2.setLayout(new BorderLayout());
        panel.setLayout(new FlowLayout());
        panel1.setLayout(new GridLayout(0, 2));

        panel2.add(panel, BorderLayout.CENTER);
        panel2.add(panel1, BorderLayout.NORTH);

        panel.add(new JLabel("Flooring Type? ", JLabel.CENTER));
        JRadioButton smallOption = new JRadioButton("Wood");
        smallOption.setActionCommand("Wood");
        JRadioButton mediumOption = new JRadioButton("Carpet");
        mediumOption.setActionCommand("Carpet");

        btngroup.add(smallOption);
        btngroup.add(mediumOption);

        panel.add(smallOption);
        panel.add(mediumOption);

        panel1.add(new JLabel("Floor Length: "));
        lengthTextField = new JTextField(15);
        panel1.add(lengthTextField);
        panel1.add(new JLabel("Floor Width: "));
        widthTextField = new JTextField(15);
        panel1.add(widthTextField);

        return panel2;
    }

    public JPanel calculateTab() {
        JPanel panel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        panel.setLayout(new BorderLayout());
        panel1.setLayout(new GridLayout(0, 2));
        panel2.setLayout(new FlowLayout());

        panel1.add(new JLabel("Floor Area: "));
        areaTextField = new JTextField(15);
        panel1.add(areaTextField);
        panel1.add(new JLabel("Flooring Cost: "));
        costTextField = new JTextField(15);
        panel1.add(costTextField);

        panel2.add(calculateButton);
        calculateButton.addActionListener(this);
        panel2.add(submitButton);
        submitButton.addActionListener(this);
        panel2.add(listButton);
        listButton.addActionListener(this);

        panel.add(panel1, BorderLayout.NORTH);
        panel.add(panel2, BorderLayout.WEST);

        return panel;
    }

    public JPanel summaryTab() {
        JPanel panel = new JPanel();
        panel.add(summaryTextArea);
        return panel;
    }

    public static void main(String[] args) {
        Flooring floor = new Flooring();

    }
}
