package storygenerator.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.environment.AgentMapping;
import diskworld.environment.FloorCellType;
import diskworld.environment.Wall;
import diskworld.linalg2D.Line;
import storygenerator.menu.actions.Event;
import storygenerator.menu.actions.Event2Disks;
import storygenerator.menu.actions.EventTransitionTrigger;
import storygenerator.menu.actions.EventTransitionTrigger2Disks;

/**
 * Main class of the storygenerator menu
 *
 * @author robert & Svenja
 */
public class A_MenuMain extends JFrame {

    //some default values
    public static final String DEFAULT_STRING = "-----";
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final String DEFAULT_STORYNAME = "HeiderSimmelStory";

    //story details
    private LinkedList<Line> wallList = new LinkedList<Line>(); //stores the walls
    private Vector<MenuDisk> menuDisks; //stores disk information in a menu-compatible form
    private String storyName;
    private boolean showVisualizationOfDiskDirection; //if true, disk heading direction is visualized during simulation
    private boolean randomized; //if true, parameters like disk positions are randomized for simulation

    //for menu use
    private static JButton addDiskButton;
    private static JButton changeDiskButton;
    private static JButton deleteDiskButton;
    private static JButton showDiskDirectionButton;
    private static JButton deleteWallsButton;
    private static JButton randomizeButton;
    private static JButton runButton;
    private Environment env;
    private JSplitPane splitpane;
    private JLabel storyNameLabel;
    private JTabbedPane tabpane = new JTabbedPane();
    private LeftEnvironmentPanel leftPanelEnvironment = new LeftEnvironmentPanel();
    private LeftPaintPanel leftPaintPanel;
    private HashMap<String, DefaultComboBoxModel> initialStateCombos; //comboboxes for the selection of initial states for the disks


    //for thread safety
    private AtomicBoolean running = new AtomicBoolean(false);

    private Random random = new Random(13094);

    private Controller controller;

    public A_MenuMain() {
        this.controller = new Controller();
        controller.setMenuMain(this);
        initialStateCombos = new HashMap<>(10);
        //frame
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setTitle("Storygenerator");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBackground(BACKGROUND_COLOR);
        this.storyName = DEFAULT_STORYNAME;
        this.menuDisks = new Vector<>(10);
        createButtonsAndLabel();

        MenuBar bar = new MenuBar();
        this.setJMenuBar(bar);

        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftPaintPanel = new LeftPaintPanel(this, this.menuDisks, wallList);
        setDefaultEnvironment();

        boolean tryNew = true;

        if (!tryNew) {
            splitpane.setLeftComponent(leftPanelEnvironment);
        } else {
            splitpane.setLeftComponent(leftPaintPanel);
        }
        //		addMouseListenerToLeftPanel();
        splitpane.setRightComponent(getRightPanel());
        this.add(splitpane);

        this.addMenuActions(bar);

        this.setVisible(true);
        this.setResizable(false);
        restoreDefaultSplitpaneDividerLocation(splitpane);
        this.validate();
    }

    public LeftPaintPanel getLeftPaintPanel() {
        return leftPaintPanel;
    }

    public Vector<MenuDisk> getMenuDisks() {
        return menuDisks;
    }


    public String[] getMenuDiskNames() {
        String[] names = new String[this.menuDisks.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = menuDisks.get(i).diskName;
        }
        return names;
    }

    /**
     * Informs menu about names of the existing states.
     *
     * @param strings
     */
    public void updateStates(Set<String> strings) {
        for (String s : initialStateCombos.keySet()) {
            DefaultComboBoxModel model = initialStateCombos.get(s);
            String selectedItem = (String) model.getSelectedItem();
            model.removeAllElements();
            for (String string : strings) {
                model.addElement(string);
            }
            if (strings.contains(selectedItem)) {
                model.setSelectedItem(selectedItem);
            }
        }
    }

    /**
     * Updates initial states when the user has changed a state name.
     *
     * @param oldValue old name
     * @param value    new name
     */
    public void updateInitialStates(String oldValue, String value) {
        for (DefaultComboBoxModel comboBoxModel : initialStateCombos.values()) {
            if (comboBoxModel.getSelectedItem().equals(oldValue)) {
                comboBoxModel.setSelectedItem(value);
            }
        }
    }

    private int getEnvironmentPanelSize() {
        return (int) (splitpane.getSize().width / 2);
    }

    private void createButtonsAndLabel() {
        addDiskButton = new JButton("Add a disk");
        changeDiskButton = new JButton("Change a disk");
        deleteDiskButton = new JButton("Delete a disk");
        deleteWallsButton = new JButton("Delete walls");
        runButton = new JButton(" Run ");
        runButton.setPreferredSize(new Dimension(200, 80));
        runButton.setFont(new Font("Arial", Font.PLAIN, 30));
        randomizeButton = new JButton("Randomize positions");
        randomizeButton.setToolTipText("Randomize the disk positions for the simulation.");
        showDiskDirectionButton = new JButton("Show disk direction");
        showDiskDirectionButton.setToolTipText("Show disks' heading direction during simulation.");
        storyNameLabel = new JLabel();
        addDiskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == addDiskButton) {
                    leftPaintPanel.setCurrentDrawItemToDisk();
                    final DiskAdder d = new DiskAdder(menuDisks.size());
                    leftPaintPanel.setAssignMenuDisk(d);

                    d.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            leftPaintPanel.setCurrentDrawItemToDefault();
                        }
                    });
                    d.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getSource() == d.buttonOK) {
                                d.diskName = d.tf1.getText();

                                if (diskNameExists(d.diskName) != -1) {
                                    chooseDifferentName();

                                } else {
                                    int index = menuDisks.size();
                                    MenuDisk newDisk = new MenuDisk(d.diskName, d.x, d.y, d.radius, d.col);
                                    addMenuDisk(index, newDisk);
                                    updateTabpane(menuDisks.size() - 1, newDisk);
                                    d.dispose();
                                }
                            }
                            leftPaintPanel.setAssignMenuDisk(null);
                            leftPaintPanel.setCurrentDrawItemToWall();
                        }
                    });
                }
            }

        });

        changeDiskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == changeDiskButton) {

                    if (menuDisks.isEmpty()) {
                        new ErrorMessageFrame("Error - create disks first",
                                "You need to create disks before "
                                        + "changing or deleting them.");
                    } else if (menuDisks.size() == 1) {
                        //only 1 disk -> no need to
                        //ask user to select a disk
                        changeDisk(0);
                        //updateLeftPanel();
                    } else { //more than 1 disk -> select a disk

                        final DiskSelectionFrame chooser = new DiskSelectionFrame(menuDisks,
                                "Choose disk to change");
                        chooser.buttonOK.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (e.getSource() == chooser.buttonOK) {
                                    Object diskName = chooser.tablemod.getValueAt(0, 0);
                                    for (int i = 0; i < menuDisks.size(); i++) {
                                        if (diskName.equals(menuDisks.get(i).diskName)) {
                                            changeDisk(i);
                                            chooser.frame.setVisible(false);
                                            //                  updateLeftPanel();
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        deleteDiskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isUsed;
                if (e.getSource() == deleteDiskButton) {
                    if (menuDisks.isEmpty()) {
                        //no disks exist
                        new ErrorMessageFrame("Error - create disks first", "You need to create disks before "
                                + "changing or deleting them.");
                    } else if (menuDisks.size() == 1) {
                        //only one disk exists
                        String diskName = menuDisks.elementAt(0).diskName;
                        isUsed = testIfDiskIsUsed(diskName);
                        if (!isUsed) {
                            final ConfirmationFrame okSureWindow = new ConfirmationFrame("Sure?", "Are you sure?");
                            okSureWindow.buttonOK.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (e.getSource() == okSureWindow.buttonOK) {
                                        okSureWindow.isChosen = true;
                                        okSureWindow.frame.setVisible(false);
                                        deleteDisk(diskName, 0);
                                        updateLeftPanel();
                                    }
                                }
                            });
                        }

                    } else {
                        //more than 1 disk -> select a disk
                        final DiskSelectionFrame chooser = new DiskSelectionFrame(menuDisks,
                                "Choose disk to delete");

                        chooser.buttonOK.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (e.getSource() == chooser.buttonOK) {
                                    String diskName = (String) chooser.tablemod.getValueAt(0, 0);
                                    boolean isUsed = testIfDiskIsUsed(diskName);
                                    if (!isUsed) {
                                        boolean doDelete = false;
                                        for (int i = 0; i < menuDisks.size(); i++) {
                                            if (diskName.equals(menuDisks.get(i).diskName)) {
                                                doDelete = true;
                                                chooser.frame.setVisible(false);
                                                deleteDisk(diskName, i);
                                                updateLeftPanel();
                                                break;
                                            }
                                        }
                                        if (!doDelete) {
                                            System.out.println("Error at Menu-submenu-deleteButton");
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }

            private boolean testIfDiskIsUsed(String diskName) {
                //look for usages as reference disk
                //disk can only be deleted, if it is not used as a reference disk
                boolean isUsed = false;
                String usages = "";
                HashMap<String, Event> events = controller.getEvents();
                for (String s : events.keySet()) {
                    Event event = events.get(s);
                    if (event instanceof Event2Disks) {
                        if (((Event2Disks) event).getReferenceDiskName().equals(diskName)) {
                            isUsed = true;
                            usages += s + " ";
                        }
                    }
                }
                HashMap<String, EventTransitionTrigger> etts = controller.getEtts();
                for (String s : etts.keySet()) {
                    EventTransitionTrigger trigger = etts.get(s);
                    if (trigger instanceof EventTransitionTrigger2Disks) {
                        if (((EventTransitionTrigger2Disks) trigger).getReferenceDiskName().equals(diskName)) {
                            isUsed = true;
                            usages += s + " ";
                        }
                    }
                }
                if (isUsed)
                    new ErrorMessageFrame("Disk is used", "Disk cannot be deleted because it's used as a reference disk in " + usages);
                return isUsed;
            }
        });


        showDiskDirectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == showDiskDirectionButton) {

                    showVisualizationOfDiskDirection = !showVisualizationOfDiskDirection;
                    if (showVisualizationOfDiskDirection) {
                        showDiskDirectionButton.setText("Do not show disk direction");
                    } else {
                        showDiskDirectionButton.setText("Show disk direction");
                    }
                }

            }
        });

        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == randomizeButton) {
                    randomized = !randomized;
                    if (randomized) randomizeButton.setText("Do not randomize");
                    else randomizeButton.setText("Randomize");
                }
            }
        });

        deleteWallsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == deleteWallsButton) {
                    wallList.clear();
                    updateLeftPanel();
                }
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(runButton)) {
                    if (!running.get()) {
                        if (controller.isReadyForRunning()) {
                            changeLeftPanel(false);

                            if (running.get()) {
                                killSimulationThread();
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            printNewSimulationStarted();
                                            runButton.setText(" Stop ");
                                            runDemo();
                                        }
                                    }) {
                                    }.start();
                                }
                            });
                        }
                    } else {
                        killSimulationThread();
                        changeLeftPanel(true);
                        runButton.setText(" Run ");
                    }
                }
            }
        });

    }

    private void deleteDisk(String diskName, int index) {
        initialStateCombos.remove(diskName);
        this.menuDisks.remove(index);
        tabpane.removeTabAt(index);
    }

    private JPanel getRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel editorPanel = new JPanel();
        JPanel editorPanelBehaviour = new JPanel();
        editorPanelBehaviour.setLayout(new BoxLayout(editorPanelBehaviour, BoxLayout.Y_AXIS));
        editorPanelBehaviour.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        editorPanelBehaviour.add(new BehaviourPanel(controller, true));
        editorPanelBehaviour.add(Box.createVerticalStrut(5));
        editorPanelBehaviour.add(new BehaviourPanel(controller, false));
        editorPanel.add(editorPanelBehaviour, BorderLayout.LINE_START);
        editorPanel.add(new StateEditor(controller), BorderLayout.CENTER);

        JPanel panelUpper = new JPanel();
        JPanel storyNamePanel = getStoryNamePanel();
        JPanel diskActionPanel = new JPanel();
        diskActionPanel.setBackground(BACKGROUND_COLOR);
        diskActionPanel.add(addDiskButton);
        diskActionPanel.add(changeDiskButton);
        diskActionPanel.add(deleteDiskButton);
        diskActionPanel.add(deleteWallsButton);
        diskActionPanel.add(showDiskDirectionButton);
        diskActionPanel.add(randomizeButton);
        panelUpper.setLayout(new BorderLayout());
        panelUpper.add(storyNamePanel, BorderLayout.CENTER);
        panelUpper.add(diskActionPanel, BorderLayout.SOUTH);

        JPanel panelLower = new JPanel();
        tabpane = getTable();
        panelLower.setLayout(new BorderLayout());
        panelLower.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLower.add(tabpane, BorderLayout.NORTH);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(editorPanel);
        JPanel panel = new JPanel();
        panel.add(runButton);
        mainPanel.add(panel);
        panelLower.add(mainPanel, BorderLayout.CENTER);

        rightPanel.add(panelUpper, BorderLayout.NORTH);
        rightPanel.add(panelLower, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel getStoryNamePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new BorderLayout());

        storyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        storyNameLabel.setText("The name of your story: " + storyName);
        panel.add(storyNameLabel, BorderLayout.CENTER);

        JPanel rightNestedPanel = new JPanel();
        rightNestedPanel.setLayout(new GridLayout(1, 1));
        final JButton changeStoryNameButton = new JButton("Change name");
        changeStoryNameButton.setToolTipText("Change the story's name");
        rightNestedPanel.add(changeStoryNameButton);
        panel.add(rightNestedPanel, BorderLayout.EAST);

        changeStoryNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == changeStoryNameButton) {
                    String title = "Choosing a story name";
                    String labeltext = "Please enter the name of your story";
                    final TextSelectionFrame chooser = new TextSelectionFrame(storyName, title, labeltext);

                    chooser.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getSource() == chooser.buttonOK) {
                                setStoryName(chooser.tf1.getText());
                                chooser.frame.setVisible(false);
                            }
                        }
                    });
                }
            }
        });
        return panel;
    }

    private void setStoryName(String newStoryname) {
        storyName = newStoryname;
        storyNameLabel.setText("The name of your story: " + storyName);
    }

    private void createNewStory() {
        tabpane.removeAll();
        wallList = new LinkedList<Line>();
        menuDisks.clear();
        controller.clearData();
        initialStateCombos.clear();
        splitpane.setRightComponent(getRightPanel());
        setStoryName(DEFAULT_STORYNAME);
        setDefaultEnvironment();
        leftPaintPanel = new LeftPaintPanel(this, this.menuDisks, wallList);
        changeLeftPanel(true);
        this.validate();
    }

    private void changeLeftPanel(boolean showPaintPanel) {
        if (showPaintPanel) {
            splitpane.setLeftComponent(leftPaintPanel);
        } else {
            splitpane.setLeftComponent(leftPanelEnvironment);
        }
        restoreDefaultSplitpaneDividerLocation(splitpane);
    }

    private void addMenuActions(MenuBar bar) {
        bar.menuNewStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuDisks.isEmpty() && controller.getStates().isEmpty()) {
                    new ErrorMessageFrame("Done!", "New story created.");
                    createNewStory();
                } else {
                    String message = "<html>If you create a new story, "
                            + "the current one will be lost.<br/>Please "
                            + "press cancel and save the story if you "
                            + "want to keep it.</html>";
                    final ConfirmationFrame oksure = new ConfirmationFrame("Discard current story?", message);

                    oksure.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            oksure.frame.setVisible(false);
                            createNewStory();
                        }
                    });
                }
            }
        });

        bar.menuLoadStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuDisks.isEmpty() && controller.getStates().isEmpty()) {
                    // no existing story
                    openFileChooser();
                } else {
                    //story exists -> ask user if he wants to delete this one and load another story
                    String message = "<html>If you load a new story, "
                            + "the current one will be lost.<br/>Please "
                            + "press cancel and save the story if you "
                            + "want to keep it.</html>";
                    final ConfirmationFrame oksure = new ConfirmationFrame("Discard current story?", message);

                    oksure.buttonOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            oksure.isChosen = true;
                            oksure.frame.setVisible(false);
                            openFileChooser();
                        }
                    });
                }
            }
        });

        bar.menuSaveStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuDisks.isEmpty() && controller.getStates().isEmpty()) {
                    //no existing story
                    new ErrorMessageFrame("Nothing to save", "There is no story "
                            + "to save - please create disks first.");
                } else {
                    //story exists -> save data
                    HashMap<String, String> initialStates = new HashMap<>();
                    for (String s : initialStateCombos.keySet()) {
                        String state = (String) initialStateCombos.get(s).getSelectedItem();
                        initialStates.put(s, state);
                    }
                    double[][] walls = new double[wallList.size()][];
                    int i = 0;
                    for (Line line : wallList) {
                        walls[i++] = new double[]{line.getX1(), line.getY1(), line.getX2(), line.getY2()};
                    }
                    StoryData data = controller.buildStory(menuDisks, walls, initialStates, storyName);
                    FileHandler.openFileSaver(storyName, data);
                }
            }
        });

        bar.menuEditStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leftPanelEnvironment.isShowing()) {
                    changeLeftPanel(true);
                } else if (leftPaintPanel.isShowing()) {
                    new ErrorMessageFrame("Already edit mode", "You are already in "
                            + "the edit mode. To change to simulation mode, "
                            + "click 'Run Simulation'.");
                }
            }
        });

        bar.menuHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InfoFrame();
            }
        });


    }

    private void updateTabpane(int index, MenuDisk newDisk) {
        updateTabpane(index, newDisk, null);
    }

    /**
     * updates the tabpane when a disk has been created / changed.
     *
     * @param index   index where to add / change a tab
     * @param newDisk the disk
     * @param oldName old name of the disk, in case it has been changed
     */
    private void updateTabpane(int index, MenuDisk newDisk, String oldName) {
        JPanel getPanel = getPanelForDisk(newDisk, oldName);
        if (tabpane.getTabCount() <= index)
            tabpane.addTab(newDisk.diskName, getPanel);
        else
            tabpane.setComponentAt(index, getPanel);
        tabpane.setTitleAt(index, newDisk.diskName);
        tabpane.setBackgroundAt(index, newDisk.col);
        restoreDefaultSplitpaneDividerLocation(splitpane);
        tabpane.setSelectedIndex(index);
    }

    /**
     * Returns complete "tabPanel" for a MenuDisk d.
     */
    private JPanel getPanelForDisk(MenuDisk currentDisk, String oldName) {
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        bigPanel.setBackground(Color.white);
        JPanel diskDataPanel = getPanelForDiskHelper(currentDisk, oldName);
        bigPanel.add(diskDataPanel, BorderLayout.NORTH);
        return bigPanel;
    }

    private JPanel getPanelForDiskHelper(MenuDisk d, String oldName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(d.col);

        JPanel leftNestedPanel = new JPanel();
        leftNestedPanel.setLayout(new GridLayout(5, 1));
        leftNestedPanel.setBackground(Color.white);

        JLabel label1 = new JLabel("Diskname: " + d.diskName);
        JLabel label2 = new JLabel("x pos:    " + d.x);
        JLabel label3 = new JLabel("y pos:    " + d.y);
        JLabel label4 = new JLabel("radius:   " + d.radius);
        JPanel statePanel = new JPanel();
        statePanel.setBackground(Color.white);
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.X_AXIS));
        JLabel stateLabel = new JLabel("Initial state: ");
        JComboBox<Object> comboBox = new JComboBox<>();
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(controller.getStates().keySet().toArray());
        if (oldName != null) { //gets old selected item after name change
            DefaultComboBoxModel oldModel = initialStateCombos.get(oldName);
            if (oldModel != null) {
                comboBoxModel.setSelectedItem(oldModel.getSelectedItem());
            }
            initialStateCombos.remove(oldName);
        }
        DefaultComboBoxModel model = initialStateCombos.get(d.diskName);
        if (model != null) {
            String item = (String) model.getSelectedItem();
            if (item != null) comboBoxModel.setSelectedItem(item); //gets old selected item after loading story
        }
        comboBox.setModel(comboBoxModel);
        initialStateCombos.put(d.diskName, comboBoxModel);
        statePanel.add(stateLabel);
        statePanel.add(comboBox);

        leftNestedPanel.add(label1);
        leftNestedPanel.add(label2);
        leftNestedPanel.add(label3);
        leftNestedPanel.add(label4);
        leftNestedPanel.add(statePanel);

        panel.add(leftNestedPanel, BorderLayout.WEST);

        return panel;
    }

    /**
     * returns complete JTabbedPane in which every disk gets a tab.
     * The respective tab contains the disk data (such as x,y pos and name).
     *
     * @return
     */
    private JTabbedPane getTable() {
        tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        for (int i = 0; i < menuDisks.size(); i++) {
            updateTabpane(i, menuDisks.get(i));
        }
        return tabpane;
    }

    private void updateLeftPanel() {
        leftPaintPanel = new LeftPaintPanel(this, this.menuDisks, wallList);
        splitpane.setLeftComponent(leftPaintPanel);
        restoreDefaultSplitpaneDividerLocation(splitpane);
    }

    private int diskNameExists(String diskName) {
        String[] names = getMenuDiskNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(diskName)) {
                return i;
            }
        }
        return -1;
    }

    private void addMenuDisk(int index, MenuDisk newDisk) {
        if (index < this.menuDisks.size()) {
            this.menuDisks.set(index, newDisk);

        } else this.menuDisks.add(index, newDisk);
    }

    private static void chooseDifferentName() {
        new ErrorMessageFrame("Choose different name!", "Please choose"
                + " a different diskname since the one chosen is already"
                + " used.");
    }

    private void changeDisk(final int index) {
        final DiskAdder d = new DiskAdder(this.menuDisks.get(index));
        String oldName = this.menuDisks.get(index).diskName;

        leftPaintPanel.setCurrentDrawItemToDisk();
        leftPaintPanel.setAssignMenuDisk(d);
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                leftPaintPanel.setCurrentDrawItemToDefault();
            }
        });
        d.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == d.buttonOK) {
                    d.diskName = d.tf1.getText();
                    try {
                        d.x = Double.parseDouble(d.tf2.getText()) / 100.0;
                        d.y = Double.parseDouble(d.tf3.getText()) / 100.0;
                        d.radius = Double.parseDouble(d.tf4.getText()) / 100.0;
                        d.setVisible(false);
                    } catch (NumberFormatException nfe) {
                    }
                    MenuDisk menuDisk = new MenuDisk(d.diskName, d.x, d.y, d.radius, d.col);
                    if (diskNameExists(d.diskName) != index && diskNameExists(d.diskName) != -1) {
                        //name of the disk can be changed, but not to a name of another disk
                        chooseDifferentName();
                    } else {
                        addMenuDisk(index, menuDisk);
                        controller.updateReferenceDiskUsages(oldName, d.diskName);
                        updateTabpane(index, menuDisk, oldName);
                    }
                    leftPaintPanel.setCurrentDrawItemToDefault();
                }
            }
        });
    }

    private static void restoreDefaultSplitpaneDividerLocation(final JSplitPane splitpane) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitpane.setDividerLocation(splitpane.getSize().width / 2);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new A_MenuMain();
            }
        });
    }


    /***************************************************************
     ********** METHODS FOR ENVIRONMENT (PANEL)  *******************
     **************************************************************/


    /**
     * Runs the given demo in the environment panel
     */
    private void runDemo() {
        final double DT_PER_STEP = 0.01;
        final long miliSecondsPerStep = 5;
        DiskWorldStory story = new DiskWorldStory(this.menuDisks, wallList, storyName, showVisualizationOfDiskDirection, randomized, random.nextLong());
        Environment env = story.getEnvironment();

        leftPanelEnvironment.setEnvironment(env);
        leftPanelEnvironment.getSettings().setFullView(env);
        leftPanelEnvironment.setPreferredSize(new Dimension(getEnvironmentPanelSize(), getEnvironmentPanelSize()));
        validate();
        //		addKeyListener(getKeyListener(running));
        HashMap<String, AgentMapping> mappings = story.getAgentMappings();
        HashMap<String, Disk> disks = story.getDisks();
        controller.prepareRunning(initialStateCombos, disks);
        running = new AtomicBoolean(true);
        boolean settingsAdapted = false;

        while (running.get()) {
            if (!settingsAdapted) {
                settingsAdapted = story.adaptVisualisationSettings(leftPanelEnvironment.getSettings());
            }
            long ts = System.currentTimeMillis();
            env.doTimeStep(DT_PER_STEP, mappings.values().toArray(new AgentMapping[0]));

            controller.doTimeStep(env.getTime(), mappings, disks, env, story.getCollision());
            long time = System.currentTimeMillis() - ts;
            long sleep = miliSecondsPerStep - time;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void killSimulationThread() {
        running.set(false);
    }

    private static void showNames(String title, AgentMapping[] mappings) {
        System.out.println(title);
        System.out.println();
        for (int i = 0; i < mappings.length; i++) {
            System.out.print("Agent " + i + " sensors:");
            for (String s : mappings[i].getSensorNames()) {
                System.out.print(" " + s);
            }
            System.out.println();
            System.out.print("Agent " + i + " actuators:");
            for (String s : mappings[i].getActuatorNames()) {
                System.out.print(" " + s);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static LinkedList<Wall> getBoundaryWalls() {
        LinkedList<Wall> walls = new LinkedList<Wall>();
        // these 4 walls are the outside walls of the frame
        walls.add(new Wall(new Line(new diskworld.linalg2D.Point(0, 0), new diskworld.linalg2D.Point(1, 0)), 0.01));
        walls.add(new Wall(new Line(new diskworld.linalg2D.Point(1, 0), new diskworld.linalg2D.Point(1, 1)), 0.01));
        walls.add(new Wall(new Line(new diskworld.linalg2D.Point(1, 1), new diskworld.linalg2D.Point(0, 1)), 0.01));
        walls.add(new Wall(new Line(new diskworld.linalg2D.Point(0, 1), new diskworld.linalg2D.Point(0, 0)), 0.01));
        return walls;
    }

    private void setDefaultEnvironment() {
        int panelSize = getEnvironmentPanelSize();
        int size = DiskWorldStory.SIZE_OF_ENVIRONMENT;
        env = new Environment(size, size,
                getBoundaryWalls());
        env.getFloor().fill(FloorCellType.EMPTY);
        leftPanelEnvironment.setEnvironment(env);
        leftPanelEnvironment.getSettings().setFullView(env);
        leftPanelEnvironment.setPreferredSize(new Dimension(panelSize, panelSize));
        leftPanelEnvironment.setMinimumSize(new Dimension(panelSize / 2, panelSize / 2));
        leftPanelEnvironment.setMaximumSize(new Dimension(panelSize, panelSize));
        //		killSimulationThread();
        env.doTimeStep(0.01);
        leftPanelEnvironment.revalidate();
    }


    private void printNewSimulationStarted() {
        System.out.println();
        System.err.println("New Simulation started.");
    }

    /***************************************************************
     * ********* HELPER METHODS FOR FILE HANDLING *******************
     **************************************************************/


    private void openFileChooser() {

        FileChooser chooser = new FileChooser();

        try {
            while (!chooser.isChosen) {
                Thread.sleep(1);
            }

            if (chooser.isCorrect) {
                createNewStory();
                StoryData myStory = chooser.data;
                controller.loadData(myStory);
                menuDisks = myStory.getMenuDisks();
                HashMap<String, String> initialStates = myStory.getInitialStates();
                initialStateCombos.clear();
                for (String s : initialStates.keySet()) {
                    DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>(controller.getStates().keySet().toArray());
                    model.setSelectedItem(initialStates.get(s));
                    initialStateCombos.put(s, model);
                }

                double[][] wallList = myStory.getWallList();
                for (double[] coordinates : wallList) {
                    this.wallList.add(new Line(coordinates[0], coordinates[1], coordinates[2], coordinates[3]));
                }

                setStoryName(myStory.getStoryName());
                setDefaultEnvironment();

                leftPaintPanel = new LeftPaintPanel(this, this.menuDisks, this.wallList);
                changeLeftPanel(true);
                int counter = 0;
                for (MenuDisk menuDisk : this.menuDisks) {
                    updateTabpane(counter++, menuDisk);
                }
            }

        } catch (InterruptedException e) {
        }
    }


}
