package storygenerator.menu;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.environment.AgentMapping;
import storygenerator.menu.actions.*;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Main class for the interaction with GUI elements.
 * Stores built {@link Event}, {@link EventTransitionTrigger}, {@link State}.
 *
 * @author Svenja
 */
public class Controller {
    //GUI elements
    private A_MenuMain menuMain;
    private BehaviourEditorFrame frame;
    private StateEditor stateEditor;
    private BehaviourPanel eventEditor;
    private BehaviourPanel ettEditor;

    //for the interaction with BehaviourEditorFrame
    private Object current;
    private boolean chooseEvent;

    //Overview on existing classes
    private LinkedList<Class> ettList; // list of all EventTransitionTrigger classes
    private LinkedList<Class> eventList; //list of all events classes

    //Storing created objects
    private HashMap<String, Event> events; //all created event objects with their names
    private HashMap<String, EventTransitionTrigger> etts; //all created event transition trigger objects with their names
    private HashMap<String, State> states; //all built states with names
    private HashMap<String, State> currentStates; //keys of disks and corresponding current states (for running the simulation)

    //for running
    private HashMap<String, double[]> oldDiskPositions;
    private HashMap<String, Double> momentOfLastTrigger; //stores the moment of the last event transition of each disk
    private boolean interferingCollisions = true; //true if controller shall do some basic collision handling

    public Controller() {
        //initialising hash maps and list of all classes
        /**********************************************************************
         * **ADD NEW EVENT / EVENT TRANSITION TRIGGER CLASSES HERE*************
         *********************************************************************/
        eventList = new LinkedList<>();
        Collections.addAll(eventList, Attack.class, Avoidance.class, AvoidanceAndRotation.class, Chasing.class,
                IntelligentChasing.class, IntelligentTrajectory.class, MovementToPoint.class, PathFinder.class,
                RandomMovement.class, Rotation.class, RotationAroundDisk.class,
                Stopping.class, Trajectory.class, TurnTo.class);
        ettList = new LinkedList<>();
        Collections.addAll(ettList, Always.class, BecameInvisible.class, BecameVisible.class, HasStopped.class, IsClose.class, IsNotClose.class,
                OtherDiskStopped.class, PeriodOfTime.class, SameXPosition.class, SameYPosition.class, SecSinceStart.class, TouchDisk.class);
        events = new HashMap<>(10);
        etts = new HashMap<>(10);
        states = new HashMap<>(10);
        currentStates = new HashMap<>(10);
        oldDiskPositions = new HashMap<>(10);
        momentOfLastTrigger = new HashMap<>(10);
    }

    public void setInterferingCollisions(boolean interferingCollisions) {
        this.interferingCollisions = interferingCollisions;
    }

    public HashMap<String, State> getStates() {
        return states;
    }

    public HashMap<String, Event> getEvents() {
        return events;
    }

    public HashMap<String, EventTransitionTrigger> getEtts() {
        return etts;
    }

    public String[] getMenuDiskNames() {
        return menuMain.getMenuDiskNames();
    }

    public void setMenuMain(A_MenuMain menuMain) {
        this.menuMain = menuMain;
    }

    public void setEventEditor(BehaviourPanel eventEditor) {
        this.eventEditor = eventEditor;
    }

    public void setStateEditor(StateEditor stateEditor) {
        this.stateEditor = stateEditor;
    }

    public void setEttEditor(BehaviourPanel ettEditor) {
        this.ettEditor = ettEditor;
    }

    public void setChooseEvent(boolean chooseEvent) {
        this.chooseEvent = chooseEvent;
    }

    public int getCounter() {
        if (chooseEvent) return events.size();
        return etts.size();
    }

/***************************************************************************************
 * *************** METHODS FOR THE INTERACTION WITH {@link BehaviourEditorFrame}.*******
 ***************************************************************************************
 */


    /**
     * Informs the {@link BehaviourEditorFrame} about existing event /trigger classes.
     *
     * @return list of classes of events or eventTransitionTriggers, according to boolean chooseEvent
     */
    public LinkedList<Class> getClassList() {
        if (chooseEvent) return eventList;
        else return ettList;
    }


    /**
     * Is called by {@link BehaviourEditorFrame} when the user is done with editing the action
     * and presses ok.
     * Changes the event's / trigger's fields to the desired settings by Reflection.
     *
     * @param name name of the event
     */
    public void notifyActionEditingCompleted(String name) {
        //fetches variable values from the frame
        HashMap<String, Double> values = frame.getValues();
        String refDiskName = frame.getRefDiskName();
        String props = "Props: "; //description of object's properties, for GUI display
        for (String s : values.keySet()) {
            props += s + ": " + values.get(s) + " ";
            String methodName = "set" + s.substring(0, 1).toUpperCase() + s.substring(1);
            try {
                Method method = current.getClass().getMethod(methodName, values.get(s).TYPE);
                try {
                    method.invoke(current, values.get(s));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (refDiskName != null) {
            String methodNameRef = "setReferenceDiskName";
            props += " ref disk: " + refDiskName;
            try {
                Method method = current.getClass().getMethod(methodNameRef, refDiskName.getClass());
                try {
                    method.invoke(current, refDiskName);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Double[][]> valuesArray2d = frame.getValues2D();
        if (!valuesArray2d.isEmpty()) {
            for (String s : valuesArray2d.keySet()) {
                String methodName = "set" + s.substring(0, 1).toUpperCase() + s.substring(1);
                try {
                    Method method = current.getClass().getMethod(methodName, Double[][].class);
                    try {
                        method.invoke(current, (Object) (valuesArray2d.get(s)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        HashMap<String, Double[]> valuesArray1d = frame.getValues1D();
        if (!valuesArray1d.isEmpty()) {
            for (String s : valuesArray1d.keySet()) {
                String methodName = "set" + s.substring(0, 1).toUpperCase() + s.substring(1);
                try {
                    Method method = current.getClass().getMethod(methodName, Double[].class);
                    try {
                        method.invoke(current, (Object) (valuesArray1d.get(s)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        //store the created object
        if (current instanceof Event) {
            events.put(name, (Event) current);
            this.eventEditor.addBehaviour(name, props);
        } else if (current instanceof EventTransitionTrigger) {
            etts.put(name, (EventTransitionTrigger) current);
            this.ettEditor.addBehaviour(name, props);
        }
        stateEditor.updateTables();
    }


    /**
     * Is called by {@link BehaviourEditorFrame} when an event / eventTransition has been chosen in the combobox.
     * Builds new object, according to choice.
     *
     * @param selectedItem Item selected in the {@link BehaviourEditorFrame}
     */
    public void notifyActionChosen(String selectedItem) {
        Class c = null;
        try {
            c = Class.forName("storygenerator.menu.actions" + "." + selectedItem);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        Object o = null;
        try {
            o = c.newInstance(); //build new object via reflection
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        if (o != null && o instanceof EventTransitionTrigger) {
            ((EventTransitionTrigger) o).editMenuFrame(frame);
            current = o; //store the object temporarily
        }
        if (o != null && o instanceof Event) {
            ((Event) o).editMenuFrame(frame);
            current = o;
        }
    }

    public boolean actionNameIsUsed(String name) {
        if (chooseEvent) {
            return this.events.containsKey(name);
        } else return this.etts.containsKey(name);
    }


    /***************************************************************************************
     * *************** METHODS FOR THE INTERACTION WITH {@link BehaviourPanel}.*************
     ***************************************************************************************
     */


    /**
     * is called by {@link BehaviourPanel}, when the user wants to add a new event {@link Event} or new eventTransitionTrigger {@link EventTransitionTrigger}.
     * Invokes opening {@link BehaviourEditorFrame} to edit the new event / ett.
     *
     * @param isEventEditor if true, event is added. if false, event transition trigger is added
     */
    public void notifyActionAdded(boolean isEventEditor) {
        if (isEventEditor) {
            this.setChooseEvent(true);
        } else {
            this.setChooseEvent(false);
        }
        BehaviourEditorFrame editorFrame = new BehaviourEditorFrame(this, menuMain.getLeftPaintPanel());
        this.frame = editorFrame;
        editorFrame.showFrame();
    }

    /**
     * is called by {@link BehaviourPanel}, when an event / ett is deleted by the user.
     * Deleting is only possible, when the event/ trigger is not used in states.
     *
     * @param key           name of the object to be deleted
     * @param isEventEditor true when an event shall be deleted, false when a trigger shall be deleted
     */
    public boolean notifyActionDeleted(String key, boolean isEventEditor) {
        boolean isUsed = false;
        String foundUsage = "";
        if (isEventEditor) {
            //look for usages in built states
            for (String s : states.keySet()) {
                State state = states.get(s);
                if (state.getCurrentEvent().equals(key)) {
                    isUsed = true;
                    foundUsage += s + " ";
                }
            }
            if (isUsed)
                new ErrorMessageFrame("Action is used in States", "Action cannot be deleted because it's used in states " + foundUsage);
            else
                events.remove(key);
        } else {
            for (String s : states.keySet()) {
                State state = states.get(s);
                if (state.getEventTransitionTriggers().contains(key)) {
                    isUsed = true;
                    foundUsage += s + " ";
                }
            }
            if (isUsed)
                new ErrorMessageFrame("Action is used in States", "Action cannot be deleted because it's used in states " + foundUsage);
            else
                etts.remove(key);
        }
        if (!isUsed) this.stateEditor.updateTables();
        return !isUsed;
    }

    /**
     * is called by {@link BehaviourPanel}, if the user wishes to change an event / ett (Event transition trigger).
     * Invokes opening a {@link BehaviourEditorFrame} to make the changes.
     *
     * @param key           name of the object that shall be changed
     * @param isEventEditor true when the specified object is an event, false when it's an ett
     */
    public void notifyActionChanged(String key, boolean isEventEditor) {
        BehaviourEditorFrame editorFrame = new BehaviourEditorFrame(this, menuMain.getLeftPaintPanel(), key);
        this.frame = editorFrame;
        if (isEventEditor) {
            this.setChooseEvent(true);
            Event event = events.get(key);
            event.editMenuFrame(editorFrame);
            current = event;
        } else {
            this.setChooseEvent(false);
            EventTransitionTrigger ett = etts.get(key);
            ett.editMenuFrame(frame);
            current = ett;
        }
        JComboBox comboBox = editorFrame.getComboBox();
        comboBox.setEnabled(false);
        editorFrame.showFrame();
    }


    /***************************************************************************************
     * *************** METHODS FOR THE INTERACTION WITH {@link StateEditor}.****************
     ***************************************************************************************
     */


    /**
     * Is called by {@link StateEditor}, when user wishes to save the states.
     *
     * @param statesData Table storing the data of the states.
     */
    public void saveStates(Vector<String[][]> statesData) {
        this.states.clear();
        for (String[][] data : statesData) {
            String event = data[0][1];
            State state = new State(event);
            for (int i = 0; i < data.length; i++) {
                //successors have to consist of a trigger plus a new state.
                if (!data[i][2].equals("") && !data[i][3].equals("")) {
                    state.addSuccessor(data[i][2], data[i][3]);
                }
            }
            if (!event.equals(""))
                states.put(data[0][0], state);
        }
        //printing
        for (String s : states.keySet()) {
            State state = states.get(s);
            System.out.print("State " + s);
            System.out.print(" Event " + state.getCurrentEvent());
            Set<String> ett = state.getEventTransitionTriggers();
            for (String descendant : ett) {
                System.out.print(", ETT " + descendant + " -> State " + state.getSuccessiveState(descendant));
            }
            System.out.println();
        }
        menuMain.updateStates(states.keySet());

    }

    /**
     * Is called by the state editor when the user has changed a state name.
     * Informs menu main.
     *
     * @param oldValue old name
     * @param value    new name
     */
    public void stateNameHasChanged(String oldValue, String value) {
        menuMain.updateInitialStates(oldValue, value);
    }

    /***************************************************************************************
     * *************** METHODS FOR LOADING AND SAVING DATA / STORIES ***********************
     ***************************************************************************************
     */


    /**
     * Deletes all created objects.
     */
    public void clearData() {
        events.clear();
        etts.clear();
        states.clear();
    }

    /**
     * Load data from an existing story and update the GUI elements.
     *
     * @param myStory
     */
    public void loadData(StoryData myStory) {
        this.events = myStory.getEvents();
        this.etts = myStory.getEtts();
        this.states = myStory.getStates();
        updateGUI(myStory.getEventDescriptions(), myStory.getTriggerDescriptions());
    }

    private void updateGUI(HashMap<String, String> eventDescriptions, HashMap<String, String> triggerDescriptions) {
        Vector<String[][]> stateStrings = new Vector<>();
        for (String s : states.keySet()) {
            State state = states.get(s);
            Set<String> trigger = state.getEventTransitionTriggers();
            if (trigger.size() != 0) {
                String[][] stateString = new String[trigger.size()][4];
                stateString[0][0] = s;
                stateString[0][1] = state.getCurrentEvent();
                int counter = 0;
                for (String t : trigger) {
                    stateString[counter][2] = t;
                    stateString[counter++][3] = state.getSuccessiveState(t);
                }
                stateStrings.add(stateString);
            }
        }
        Collections.sort(stateStrings, new Comparator<String[][]>() {
            @Override
            public int compare(String[][] o1, String[][] o2) {
                return o1[0][0].compareTo(o2[0][0]);
            }
        });
        stateEditor.loadStates(stateStrings);
        if (stateStrings.size() == 0) stateEditor.addStateTable("S0");

        ArrayList<String> eventList = new ArrayList<String>(events.keySet());
        Collections.sort(eventList);
        for (String s : eventList) {
            eventEditor.addBehaviour(s, eventDescriptions.get(s));

        }
        ArrayList<String> ettList = new ArrayList<>(etts.keySet());
        Collections.sort(ettList);
        for (String s : ettList) {
            ettEditor.addBehaviour(s, triggerDescriptions.get(s));

        }
    }

    /**
     * builds a {@link StoryData} with all relevant data of a story.
     *
     * @param menuDisks
     * @param walls
     * @param initialStates
     * @param storyName
     * @return
     */
    public StoryData buildStory(Vector<MenuDisk> menuDisks, double[][] walls, HashMap<String, String> initialStates, String storyName) {
        return new StoryData(menuDisks, this.events, this.etts, this.states, initialStates, walls, storyName, this.eventEditor.getProperties(), this.ettEditor.getProperties());
    }


    /**
     * Changes reference disk names when the name of a disk has been changed.
     *
     * @param oldName
     * @param newName
     */
    public void updateReferenceDiskUsages(String oldName, String newName) {
        for (Event event : events.values()) {
            if (event instanceof Event2Disks) {
                if (((Event2Disks) event).getReferenceDiskName().equals(oldName)) {
                    ((Event2Disks) event).setReferenceDiskName(newName);
                }
            }
        }
        for (EventTransitionTrigger trigger : etts.values()) {
            if (trigger instanceof EventTransitionTrigger2Disks) {
                if (((EventTransitionTrigger2Disks) trigger).getReferenceDiskName().equals(oldName)) {
                    ((EventTransitionTrigger2Disks) trigger).setReferenceDiskName(newName);
                }
            }
        }
    }


    /***************************************************************************************
     * *************** METHODS FOR RUNNING STORIES****************** ***********************
     * **************************************************************************************
     */


    public boolean isReadyForRunning() {
        return !(existEmptyRefDiskUsages() || existUnsavedStates());
    }

    /**
     * Make sure all reference disks are specified before running.
     *
     * @return true if there exist null references
     */
    private boolean existEmptyRefDiskUsages() {
        if (!menuMain.getMenuDisks().isEmpty()) {
            for (Event event : events.values()) {
                if (event instanceof Event2Disks) {
                    if (((Event2Disks) event).getReferenceDiskName() == null) {
                        new ErrorMessageFrame("Null Pointer Reference Disks", "Please first specify all reference disk usages");
                        return true;
                    }
                }
            }
            for (EventTransitionTrigger trigger : etts.values()) {
                if (trigger instanceof EventTransitionTrigger2Disks) {
                    if (((EventTransitionTrigger2Disks) trigger).getReferenceDiskName() == null) {
                        new ErrorMessageFrame("Null Pointer Reference Disks", "Please first specify all reference disk usages");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Make sure the states are saved before running.
     *
     * @return True if they have not yet been saved.
     */
    private boolean existUnsavedStates() {
        if (this.stateEditor.isNotSavedYet()) {
            new ErrorMessageFrame("Unsaved States", "Please save your states first");
            return true;
        }
        return false;
    }

    public void prepareRunning(HashMap<String, DefaultComboBoxModel> initialStateCombos, HashMap<String, Disk> disks) {
        initialiseStates(initialStateCombos);
        initialiseDiskPositions(disks);
        setReferenceDisks(disks);
        for (String s : disks.keySet()) {
            momentOfLastTrigger.put(s, 0.0);
        }
        for (Event event : events.values()) {
            event.initialise();
        }
        for (EventTransitionTrigger eventTransitionTrigger : etts.values()) {
            eventTransitionTrigger.initialise();
        }
    }

    /**
     * Set reference disks for the {@link Event} and {@link EventTransitionTrigger}, according to the names.
     *
     * @param disks
     */
    private void setReferenceDisks(HashMap<String, Disk> disks) {
        for (Event event : events.values()) {
            if (event instanceof Event2Disks) {
                Disk disk = disks.get(((Event2Disks) event).getReferenceDiskName()); //get name
                ((Event2Disks) event).setReferenceDisk(disk); //set disk with that name
            }
        }
        for (EventTransitionTrigger trigger : etts.values()) {
            if (trigger instanceof EventTransitionTrigger2Disks) {
                Disk disk = disks.get(((EventTransitionTrigger2Disks) trigger).getReferenceDiskName());
                ((EventTransitionTrigger2Disks) trigger).setReferenceDisk(disk);
            }
        }
    }

    /**
     * Initialise current states with the start states of the disks.
     *
     * @param comboBoxModelList comboboxes from the menu, where the user could choose the initial states
     */
    private void initialiseStates(HashMap<String, DefaultComboBoxModel> comboBoxModelList) {
        currentStates.clear();
        for (String disk : comboBoxModelList.keySet()) {
            String state = (String) comboBoxModelList.get(disk).getSelectedItem();
            this.currentStates.put(disk, this.states.get(state));
        }
    }

    /**
     * @param disks
     */
    private void initialiseDiskPositions(HashMap<String, Disk> disks) {
        for (String s : disks.keySet()) {
            Disk disk = disks.get(s);
            this.oldDiskPositions.put(s, new double[]{disk.getX(), disk.getY(), disk.getOrientation() - Math.PI});
        }
    }

    /**
     * @param currentTime
     * @param mappings
     * @param disks
     * @param env
     * @param collision
     */
    public void doTimeStep(double currentTime, HashMap<String, AgentMapping> mappings, HashMap<String, Disk> disks, Environment env, HashMap<String, Boolean> collision) {
        for (String diskKey : currentStates.keySet()) {
            Disk disk = disks.get(diskKey);
            double[] oldPos = oldDiskPositions.get(diskKey);
            State state = currentStates.get(diskKey);
            if (state != null && disk != null) {
                double[] timeStepValues = this.events.get(state.getCurrentEvent()).getTimeStepValues(disk, env);
                AgentMapping mapping = mappings.get(diskKey);
                if (mapping != null && timeStepValues != null) {
                    oldPos[0] = disk.getX();
                    oldPos[1] = disk.getY();
                    oldPos[2] = disk.getAngle();
                    double[] values = mapping.getActuatorValues();

                    //teleporter version
                    // values[1] = timeStepValues[0] / env.getMaxX();
                    //  values[2] = timeStepValues[1] / env.getMaxY();
                    //values[3] = timeStepValues[2] / Math.PI;


                    //mover version
                    Boolean coll = collision.get(diskKey);
                    if (interferingCollisions && coll != null && coll) {
                        //interfere when disks collide with walls or other disks
                        collision.put(diskKey, false);
                        values[0] = -0.1 / DiskWorldStory.MOVER_MAX_VALUE;
                        values[1] = Math.random() * 2 * Math.PI / (double) DiskWorldStory.MOVER_MAX_VALUE;
                    } else {
                        double dx = timeStepValues[0] - oldPos[0];
                        double dy = timeStepValues[1] - oldPos[1];

                        //TODO movement is distorted when angle is different from atan2value
                        //(disk cannot look in other than heading direction)
                        //safer would be to not use the events value, instead:
                        //   double newAngle = Math.atan2(dy, dx);
                        //here, timeStepValues[2] is used, to keep the TurnTo event (disk does not move, turns to other disk)
                        double newAngle = timeStepValues[2];


                        double angleDiff = newAngle - oldPos[2];
                        double num = Math.floor(angleDiff / 2.0 / Math.PI);
                        angleDiff -= num * 2.0 * Math.PI;
                        if (angleDiff > Math.PI)
                            angleDiff -= 2.0 * Math.PI;

                        values[0] = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) / (double) DiskWorldStory.MOVER_MAX_VALUE;
                        double v = angleDiff / (double) DiskWorldStory.MOVER_MAX_VALUE;
                        values[1] = v;
                    }

                }
                Set<String> triggers = state.getEventTransitionTriggers();
                for (String trigger : triggers) {
                    if (this.etts.get(trigger).hasOccurred(disk, currentTime, momentOfLastTrigger.get(diskKey), env)) {
                        momentOfLastTrigger.put(diskKey, currentTime);
                        String newState = state.getSuccessiveState(trigger);
                        this.currentStates.put(diskKey, this.states.get(newState));
                        break;
                    }
                }
            }
        }
    }


}
