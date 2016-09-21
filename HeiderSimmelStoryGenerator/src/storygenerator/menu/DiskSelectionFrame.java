package storygenerator.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

public class DiskSelectionFrame extends SelectionFrame {

    protected MenuDisk selectedDisk;
    protected javax.swing.table.TableModel tablemod;


    public DiskSelectionFrame(Vector<MenuDisk> menuDisks, String title) {
        if (menuDisks.size() > 0) {

            selectedDisk = menuDisks.get(0);

            frame.setTitle(title);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            panel.add(buttonOK, BorderLayout.SOUTH);

            final String[] columnNames = {"choose a disk"};

            final Object[][] data = {{title
                    + ""
                    + ""}};

            final JTable table = new JTable(data, columnNames);
            table.setPreferredScrollableViewportSize(new Dimension(300, 150));
            table.setFillsViewportHeight(true);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            TableColumn disksCol = table.getColumnModel().getColumn(0);
            JComboBox<String> comboBox0 = new JComboBox<String>();

            for (int i = 0; i < menuDisks.size(); i++) {
                comboBox0.addItem(menuDisks.get(i).diskName);
            }

            disksCol.setCellEditor(new DefaultCellEditor(comboBox0));

            table.setCellSelectionEnabled(true);

            tablemod = table.getModel();

            //Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(table);

            panel.add(scrollPane, BorderLayout.CENTER);
            frame.getContentPane().add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);


        } else {
            new ErrorMessageFrame("Error - create disks first",
                    "You need to create disks before "
                            + "changing or deleting them.");
        }
    }
}
