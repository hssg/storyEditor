package storygenerator.menu;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Class providing a Panel which displays the existing {@link Event} or {@link storygenerator.menu.actions.EventTransitionTrigger}
 * and allows adding, changing and deleting.
 *
 * @author Svenja
 */
public class BehaviourPanel extends JPanel
        implements ListSelectionListener {
    private JList list;  //list of the events / triggers
    private DefaultListModel listModel;
    private static final String addString = "Add";
    private static final String deleteString = "Delete";
    private static final String changeString = "Change";
    private Controller controller;
    private JButton deleteButton;
    private JButton changeButton;
    private boolean isEventEditor; //true if the panel is used for events, false if it's used for event transition triggers
    private HashMap<String, String> properties; //provides tooltips / description for list items

    public BehaviourPanel(Controller controller, boolean isEventEditor) {
        super(new BorderLayout());
        this.controller = controller;
        if (isEventEditor) {
            controller.setEventEditor(this);
        } else {
            controller.setEttEditor(this);
        }
        this.isEventEditor = isEventEditor;
        properties = new HashMap<>(10);
        initialise();
    }

    private void initialise() {
        listModel = new DefaultListModel();
        //Create the list and put it in a scroll pane.
        list = new JList(listModel) {
            @Override
            public String getToolTipText(MouseEvent event) {
                int index = locationToIndex(event.getPoint());
                if (index >= 0) {
                    String item = (String) getModel().getElementAt(index);
                    return properties.get(item);
                }
                return ("");
            }
        };
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        String description;
        if (isEventEditor)
            description = "Event";
        else description = "Event Transition Trigger";

        JButton addButton = new JButton(addString + " " + description);
        AddListener addListener = new AddListener(addButton, isEventEditor);
        addButton.setActionCommand(addString);
        addButton.addActionListener(addListener);
        addButton.setEnabled(true);

        deleteButton = new JButton(deleteString);
        deleteButton.setActionCommand(deleteString);
        deleteButton.addActionListener(new DeleteListener(deleteButton, isEventEditor));
        deleteButton.setEnabled(false);

        changeButton = new JButton(changeString);
        changeButton.setActionCommand(changeString);
        changeButton.setEnabled(false);
        changeButton.addActionListener(new ChangeListener(changeButton, isEventEditor));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(deleteButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(changeButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(addButton, BorderLayout.PAGE_START);
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    /**
     * Is called when a new event / trigger is added.
     *
     * @param key   the name of the event
     * @param props description of the properties, to be displayed as tooltip
     */
    public void addBehaviour(String key, String props) {
        if (!listModel.contains(key))
            listModel.addElement(key);
        properties.put(key, props);

    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    class ChangeListener implements ActionListener {
        private boolean isEventEditor;

        public ChangeListener(JButton changeButton, boolean isEventEditor) {
            this.isEventEditor = isEventEditor;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            String key = (String) listModel.getElementAt(index);
            controller.notifyActionChanged(key, isEventEditor); //calls the controller when the user wants to change an event/trigger
        }
    }

    class DeleteListener implements ActionListener {
        private JButton button;
        private boolean isEventEditor;

        public DeleteListener(JButton deleteButton, boolean sort) {
            this.button = deleteButton;
            this.isEventEditor = sort;
        }

        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            String key = (String) listModel.getElementAt(index);
            //inform controller
            boolean hasBeenDeleted = controller.notifyActionDeleted(key, isEventEditor);
            if (hasBeenDeleted) {
                listModel.remove(index);
                int size = listModel.getSize();

                if (size == 0) { //Nobody's left, disable firing.
                    deleteButton.setEnabled(false);

                } else { //Select an index.
                    if (index == listModel.getSize()) {
                        //removed item in last position
                        index--;
                    }

                    list.setSelectedIndex(index);
                    list.ensureIndexIsVisible(index);
                }
            }

        }
    }

    class AddListener implements ActionListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public AddListener(JButton button, boolean isEventEditor) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            //inform controller
            controller.notifyActionAdded(isEventEditor);
        }


        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }


    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                deleteButton.setEnabled(false);
                changeButton.setEnabled(false);

            } else {
                //Selection, enable the fire button.
                deleteButton.setEnabled(true);
                changeButton.setEnabled(true);
            }
        }
    }


}
