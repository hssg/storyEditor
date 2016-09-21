package storygenerator.menu;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * JPanel which allows building, editing and saving states.
 * Per default, it contains one table, enabling the user to build a {@link State}, that means to build chains
 * of {@link Event} and {@link storygenerator.menu.actions.EventTransitionTrigger}.
 * Provides buttons to add further state tables and to save the build states.
 * <p>
 * adapted https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableRenderDemoProject/src/components/TableRenderDemo.java
 */
public class StateEditor extends JPanel {
    private Controller controller;
    private Vector<JTable> tableList = new Vector<>(10); //stores the tables, one per built state
    private String defaultString = "";
    private JPanel mainPanel = new JPanel();
    private int stateCounter = 0; //counts number of existing /built states
    private boolean notSavedYet; //keeps track of unsaved changes of the states

    public StateEditor(Controller controller) {
        super(new GridLayout(1, 0));
        this.controller = controller;
        controller.setStateEditor(this);
        initialise();
    }

    private void initialise() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        //adds the first state table to build the first state. Thus, per default one state table is displayed
        addStateTable("S0");

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        add(scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton saveButton = new JButton("Save States");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<String[][]> statesData = new Vector<>();
                for (JTable table : tableList) {
                    String[][] data = new String[table.getRowCount()][table.getColumnCount()];
                    for (int i = 0; i < data.length; i++) {
                        for (int j = 0; j < data[i].length; j++) {
                            data[i][j] = (String) table.getValueAt(i, j);
                        }
                    }
                    statesData.add(data);
                }
                controller.saveStates(statesData); //informs the controller that the states shall be saved
                notSavedYet = false;
            }
        });

        JButton addButton = new JButton("Add State");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStateTable("S" + Integer.toString(stateCounter));
            }
        });
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPanel.add(saveButton);
        add(buttonPanel);
    }

    public boolean isNotSavedYet() {
        return notSavedYet;
    }

    /**
     * Adds a JTable which allows building a new state.
     * Updates the GUI.
     *
     * @return the new state table
     */
    protected JTable addStateTable(String name) {
        JTable table = new JTable(new StateTableModel(name));
        table.getTableHeader().setReorderingAllowed(false);
        table.setPreferredScrollableViewportSize(new Dimension(500, 50));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up column sizes.
        initColumnSizes(table);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for rename");
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        tableList.add(table);
        updateTables();

        if (stateCounter != 0) mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(scrollPane);
        mainPanel.revalidate();
        stateCounter++;
        return table;
    }

    private Set<String> getStateNames() {
        Set<String> set = new HashSet<String>();
        for (JTable table : tableList) {
            set.add((String) table.getValueAt(0, 0));
        }
        return set;
    }

    /*
     * This method picks good column sizes.
     */
    private void initColumnSizes(JTable table) {
        StateTableModel model = (StateTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer =
                table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < 2; i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(
                            table, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    private void setUpCombos(JTable table, int index, Set<String> strings) {
        TableColumn column = table.getColumnModel().getColumn(index);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(defaultString);
        for (String string : strings) {
            comboBox.addItem(string);
        }

        column.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        column.setCellRenderer(renderer);
    }

    /**
     * Has to be called when events, event transition triggers or stetes has been changed.
     * Iterates over all tables and updates the columns
     */
    public void updateTables() {
        for (JTable table : tableList) {
            updateColumnCombos(table);
        }
    }

    /**
     * Sets the comboboxes for a state table.
     * Column 1 contains comboboxes with the names of all existing events
     * Column 2 contains comboboxes with the names of all existing event transition triggers
     * Column 3 contains comboboxes with the names of all existing states
     *
     * @param table table which shall be updated
     */
    private void updateColumnCombos(JTable table) {
        setUpCombos(table, 1, controller.getEvents().keySet());
        setUpCombos(table, 2, controller.getEtts().keySet());
        setUpCombos(table, 3, getStateNames());
    }

    /**
     * Loads states Data into state tables.
     * Is called by the controller when a story is loaded.
     *
     * @param stateStrings Contains a 2d-string array per state with the data for the table
     */
    public void loadStates(Vector<String[][]> stateStrings) {
        stateCounter = 0;
        tableList.clear();
        mainPanel.removeAll();
        for (String[][] stateString : stateStrings) {
            JTable table = addStateTable(defaultString);
            table.setValueAt(stateString[0][0], 0, 0);
            table.setValueAt(stateString[0][1], 0, 1);
            for (int i = 0; i < stateString.length; i++) {
                table.setValueAt(stateString[i][2], i, 2);
                table.setValueAt(stateString[i][3], i, 3);
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Updates state names in all tables when a state name has been changed by the user.
     *
     * @param oldValue old name
     * @param value    new name
     */
    private void changeNameUsages(String oldValue, String value) {
        if (!"".equals(oldValue)) {
            for (JTable table : tableList) {
                TableModel model = table.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 3).equals(oldValue)) {
                        model.setValueAt(value, i, 3);
                    }
                }
            }
        }
    }

    class StateTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name of State", "Event",
                "Transition Trigger",
                "Following State"};


        private Vector<String[]> data = new Vector<>();

        public StateTableModel(String state) {
            String[] s = {state, defaultString, defaultString, defaultString};
            String[] s1 = {defaultString, defaultString, defaultString, defaultString};
            data.add(Arrays.copyOf(s, s.length));
            data.add(Arrays.copyOf(s1, s1.length));

        }

        public final Object[] longValues = {"AVeryLongName", "AvoidAndRotate10", "SameXPosition10", "AvoidAndRotate10"};

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data.get(row)[col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0 || col == 1) {
                //in column 0 and 1, only values in the first row are editable (state name and event)
                if (row == 0)
                    return true;
                return false;
            }
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            String[] s = data.get(row);
            String oldValue = data.get(row)[col];
            s[col] = (String) value;
            data.setElementAt(s, row);
            // data.s[row][col] = value;
            fireTableCellUpdated(row, col);
            if (row == getRowCount() - 1) {
                //if the user edits the last row of a table, a new row is added
                data.add(new String[]{defaultString, defaultString, defaultString, defaultString});
                fireTableDataChanged();
            }

            if (col == 0) {
                //if the User changes the name of a state (which is displayed in the first column), all tables (comboboxes) have to be updated
                //TODO does not yet prevent the user from choosing an already used name
                updateTables();
                changeNameUsages(oldValue, (String) value);
                controller.stateNameHasChanged(oldValue, (String) value);
            }
            if (!value.equals("")) notSavedYet = true;
        }

    }
}
