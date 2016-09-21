package storygenerator.menu;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * A frame to edit an {@link Event} or an {@link storygenerator.menu.actions.EventTransitionTrigger}.
 * The frame shows an combobox of all events or triggers. For the selected action, it displays the editable variables and
 * descriptions. Furthermore, the frame contains an "OK" button, when the user is done with editing.
 *
 * @author Svenja
 */
public class BehaviourEditorFrame extends JDialog {
    private final JButton buttonOK = new JButton("Ok");
    private final String defaultString = "----";
    private final LeftPaintPanel envPanel;
    private JPanel mainPanel = new JPanel();
    private JScrollPane scrollPane;
    private JComboBox comboBox; //stores all events / trigger
    private HashMap<String, Double> values = new HashMap<>(); //stores variable names and values
    private HashMap<String, Double[][]> values2D = new HashMap<>(); //stores 2d data (e.g. trajectory)
    private HashMap<String, Double[]> values1D = new HashMap<>(); //stores 1d data (e.g. coordinate)
    private String refDiskName; //stores the reference disk name, if the event / trigger needs one
    private JTextField nameField;
    private Controller controller;

    public BehaviourEditorFrame(Controller controller, LeftPaintPanel leftPaintPanel) {
        this(controller, leftPaintPanel, null);
    }

    /**
     * @param controller
     * @param leftPaintPanel
     * @param key            name of the action to be changed (in change mode)
     */
    public BehaviourEditorFrame(Controller controller, LeftPaintPanel leftPaintPanel, String key) {
        this.controller = controller;
        this.envPanel = leftPaintPanel;
        initialise(key);
    }

    public HashMap<String, Double> getValues() {
        return values;
    }

    public HashMap<String, Double[][]> getValues2D() {
        return values2D;
    }

    public HashMap<String, Double[]> getValues1D() {
        return values1D;
    }

    public String getRefDiskName() {
        return refDiskName;
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    private void initialise(String key) {
        this.setModalityType(ModalityType.MODELESS);
        this.setAlwaysOnTop(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                envPanel.clear();
            }
        });
        setSize(600, 400);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(100, 30));
        nameField.setMaximumSize(nameField.getPreferredSize());
        if (key != null) {
            //if action shall be changed, display old name
            nameField.setText(key);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(new JLabel("Choose a name:  "));
            panel.add(nameField);
            mainPanel.add(panel);
        }
        //setLocation
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = Integer.min((int) rect.getMaxX() - getWidth(), (int) (rect.getMaxX() / 2));
        int y = height / 2;
        this.setLocation(x, y);
        setResizable(false);

        comboBox = new JComboBox(getComboBoxItems());
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                values.clear();
                values1D.clear();
                values2D.clear();
                mainPanel.removeAll();
                envPanel.clear();
                String selectedItem = (String) comboBox.getSelectedItem();
                if (!selectedItem.equals(defaultString)) {
                    setTitle("Specifying action " + selectedItem);
                    nameField.setText(selectedItem + controller.getCounter());
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                    panel.add(new JLabel("Choose a name:  "));
                    panel.add(nameField);
                    mainPanel.add(panel);
                    //inform controller that the user has chosen an action which he wants to edit
                    controller.notifyActionChosen(selectedItem);
                } else {
                    setTitle("");
                }
                repaint();
                showFrame();
            }
        });
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                //inform the controller that the user is done with editing the action
                //name is only accepted, when it is not used yet, or  - in change mode - it's the old name
                if ((key != null && key.equals(name)) || !controller.actionNameIsUsed(name)) {
                    controller.notifyActionEditingCompleted(name);
                    envPanel.clear();
                    dispose();
                } else new ErrorMessageFrame("Name cannot be chosen", "Name is already used.");
            }
        });
    }

    private String[] getComboBoxItems() {
        LinkedList<Class> list = controller.getClassList();
        String[] strings = new String[list.size() + 1];
        strings[0] = defaultString;
        int i = 1;
        for (Class aClass : list) {
            strings[i] = aClass.getSimpleName();
            i++;
        }
        return strings;
    }

    public void showFrame() {
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.add(comboBox);
        getContentPane().add(comboBoxPane, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonOK, BorderLayout.SOUTH);
        setVisible(true);
    }


    /**
     * Adds a field to the frame which allows selecting a double value with given lower bound.
     *
     * @param lowerBound   the minimum value of the variable
     * @param defaultValue the default value of the variable
     * @param explanation  description of the variable (will appear as "please choose the desired " + explanation)
     * @param varName      name of the original double field
     */
    public void addValueChooser(double lowerBound, double defaultValue, String explanation, String varName) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Please specify the desired " + explanation);
        JTextField field = new JFormattedTextField(new DecimalFormat("00"));
        field.setColumns(4);
        field.setText(Double.toString(defaultValue));
        values.put(varName, defaultValue);
        field.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = field.getText();
                double value = defaultValue;
                try {
                    value = Double.parseDouble(text);
                } catch (NumberFormatException nfe) {
                }
                if (value < lowerBound) {
                    field.setText(Double.toString(lowerBound));
                    value = lowerBound;
                }
                values.put(varName, value);
            }
        });

        panel.add(label);
        panel.add(field);
        mainPanel.add(panel);
    }

    /**
     * Adds a slider to the frame which allows selecting a double value with given lower and upper bound.
     *
     * @param lowerBound   the minimum value
     * @param upperBound   the maximum value
     * @param defaultValue the default value
     * @param explanation  description of the variable (will appear as "please choose the desired " + explanation)
     * @param varName      original name of the double field
     */
    public void addValueChooser(double lowerBound, double upperBound, double defaultValue, String explanation, String varName) {
        values.put(varName, defaultValue);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JSlider slider = getSlider((int) lowerBound, (int) upperBound, (int) defaultValue);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = slider.getValue();
                values.put(varName, value);
            }
        });
        card.add(getSliderPanel(slider, explanation));
        mainPanel.add(card);
    }

    private JPanel getSliderPanel(JSlider slider, String explanation) {
        JPanel p = new JPanel();
        JLabel l1 = new JLabel("                             ");
        JLabel l2 = new JLabel("                             ");
        JLabel l3 = new JLabel("Please choose the desired " + explanation, SwingConstants.CENTER);
        p.setLayout(new BorderLayout());
        p.add(slider, BorderLayout.CENTER);
        p.add(l1, BorderLayout.EAST);
        p.add(l2, BorderLayout.WEST);
        p.add(l3, BorderLayout.NORTH);
        return p;
    }

    private JSlider getSlider(int ilb, int iub, int defaultValue) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, ilb, iub, (int) defaultValue);
        slider.setMajorTickSpacing((int) Math.ceil((iub - ilb) / 10.0));
        slider.setMinorTickSpacing((int) Math.ceil((iub - ilb) / 20.0));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(ilb, new JLabel(Integer.toString(ilb)));
        labelTable.put(iub, new JLabel(Integer.toString(iub)));
        slider.setLabelTable(labelTable);
        return slider;
    }

    /**
     * Adds some text to the editor frame.
     *
     * @param s text to be added
     */
    public void addText(String s) {
        JLabel label = new JLabel(s);
        JPanel panel = new JPanel();
        panel.add(label);
        mainPanel.add(panel);
    }

    /**
     * Adds a question and a yes / no combobox to the editor frame.
     * Original variable has to be a double value!
     * Conversion to a boolean has to be made in the setter of the event (or later).
     *
     * @param defaultValue the default value (1 for true, 0 for false)
     * @param explanation  description of the variable, which is displayed as "Do you wish that " plus the explanation.
     * @param varName      original name of the double field
     */
    public void addBooleanChooser(double defaultValue, String explanation, String varName) {
        values.put(varName, defaultValue);
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Do you wish that " + explanation);

        String comboBoxItems[] = new String[2];

        if (defaultValue == 1) {
            comboBoxItems[0] = "YES";
            comboBoxItems[1] = "NO";
        } else {
            comboBoxItems[0] = "NO";
            comboBoxItems[1] = "YES";
        }
        JComboBox cb = new JComboBox(comboBoxItems);

        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {

                String nameOfSelected = (String) evt.getItem();
                if (nameOfSelected.equals("NO")) {
                    values.put(varName, 0.0);
                } else {
                    values.put(varName, 1.0);
                }
            }
        });

        cb.setEditable(false);
        panel.add(label);
        panel.add(cb);
        mainPanel.add(panel);
    }

    /**
     * Adds the possibility to choose a reference disk for events that concern 2 disks.
     *
     * @param defaultDiskName the default name
     * @param explanation     explanation, which is displayed in the form "Please select a reference disk (" + explanation + ")"
     */
    public void addReferenceDiskChooser(String defaultDiskName, String explanation) {
        this.refDiskName = defaultDiskName;
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Please select a reference disk (" + explanation + ")");
        Object[] disks = controller.getMenuDiskNames();

        JComboBox cb = new JComboBox(disks);
        cb.setSelectedItem(defaultDiskName);

        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                String nameOfSelected = (String) evt.getItem();
                refDiskName = nameOfSelected;
            }
        });
        cb.setEditable(false);
        panel.add(label);
        panel.add(cb);
        mainPanel.add(panel);
    }

    /**
     * Makes it possible to create connected lines on the GUI.
     * Can be used e.g. to let the user build a trajectory.
     *
     * @param line        default, can  be null
     * @param explanation
     * @param varName     original name of the double array
     */
    public void addGUILineChooser(Double[][] line, String explanation, String varName) {
        values2D.put(varName, line);
        addText("<html>Please create a line for the " + explanation + "\n on the environment to the left</html>");
        JButton renameButton = new JButton("Ready with painting");
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double[][] line = envPanel.getTrajectory();
                values2D.put(varName, line);
            }
        });
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                envPanel.setCurrentDrawItemToTrajectory();
                envPanel.deleteTrajectory();
                values2D.put(varName, null);
            }
        });
        JPanel panel = new JPanel();
        panel.add(renameButton);
        panel.add(resetButton);
        mainPanel.add(panel);
        if (line != null) envPanel.setCurrentDrawItemToTrajectory(line);
        else envPanel.setCurrentDrawItemToTrajectory();
    }

    /**
     * Adds sliders for choosing a x and y coordinate. Coordinate is shown on the environment screen.
     * Values are between 0 and 100.
     *
     * @param coord   Double array, first value x, second y. Default, can be null.
     * @param varName original name of the field
     */
    public void addCoordinateChooser(Double[] coord, String varName) {
        if (coord != null) values1D.put(varName, coord);
        else {
            coord = new Double[]{50.0, 50.0};
            values1D.put(varName, coord);
        }
        envPanel.setCoord(new Double[]{coord[0], coord[1]});
        envPanel.repaint();
        JSlider xSlider = getSlider(0, 100, coord[0].intValue());
        JSlider ySlider = getSlider(0, 100, coord[1].intValue());
        xSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = xSlider.getValue();
                Double[] oldCoord = values1D.get(varName);
                Double[] newCoord = {Double.valueOf(value), oldCoord[1]};
                values1D.put(varName, newCoord);
                envPanel.setCoord(newCoord);
                envPanel.repaint();
            }
        });
        ySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = ySlider.getValue();
                Double[] oldCoord = values1D.get(varName);
                Double[] newCoord = {oldCoord[0], Double.valueOf(value)};
                values1D.put(varName, newCoord);
                envPanel.setCoord(newCoord);
                envPanel.repaint();
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(getSliderPanel(xSlider, "x coordinate"));
        panel.add(getSliderPanel(ySlider, "y coordinate"));
        mainPanel.add(panel);
    }


}

