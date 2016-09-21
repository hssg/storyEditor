package storygenerator.menu;

import storygenerator.menu.actions.Event;
import storygenerator.menu.actions.EventTransitionTrigger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 * Container for the relevant data to save and load stories.
 *
 * @author Svenja
 */
public class StoryData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Vector<MenuDisk> menuDisks; //menuDisks
    private HashMap<String, Event> events; //all event objects
    private HashMap<String, EventTransitionTrigger> etts; //all event transition trigger objects
    private HashMap<String, State> states; //all states with names
    private HashMap<String, String> initialStates; //all initial states for the disks
    private double[][] wallList; //walls in the environment
    private String storyName;
    private HashMap<String, String> eventDescriptions;
    private HashMap<String, String> triggerDescriptions;

    /**
     * Constructor which gets all relevant data
     *
     * @param menuDisks           all menuDisks of the story
     * @param events              all built {@link Event} with their names
     * @param etts                all built {@link EventTransitionTrigger} with their names
     * @param states              all built {@link State} with their names
     * @param initialStates       hashmap storing the initial states for each of the disks
     * @param wallList            list of walls for the environment
     * @param storyName           name of the story
     * @param eventDescriptions   descriptions for the events, for GUI use
     * @param triggerDescriptions descriptions for the event transition triggers, for GUI use
     */
    public StoryData(Vector<MenuDisk> menuDisks, HashMap<String, Event> events, HashMap<String,
            EventTransitionTrigger> etts, HashMap<String, State> states, HashMap<String, String> initialStates,
                     double[][] wallList, String storyName,
                     HashMap<String, String> eventDescriptions, HashMap<String, String> triggerDescriptions) {
        this.menuDisks = menuDisks;
        this.events = events;
        this.etts = etts;
        this.states = states;
        this.initialStates = initialStates;
        this.wallList = wallList;
        this.storyName = storyName;
        this.eventDescriptions = eventDescriptions;
        this.triggerDescriptions = triggerDescriptions;
    }

    public Vector<MenuDisk> getMenuDisks() {
        return menuDisks;
    }

    public HashMap<String, Event> getEvents() {
        return events;
    }

    public HashMap<String, EventTransitionTrigger> getEtts() {
        return etts;
    }

    public HashMap<String, State> getStates() {
        return states;
    }

    public HashMap<String, String> getInitialStates() {
        return initialStates;
    }

    public double[][] getWallList() {
        return wallList;
    }

    public String getStoryName() {
        return storyName;
    }

    public HashMap<String, String> getEventDescriptions() {
        return eventDescriptions;
    }

    public HashMap<String, String> getTriggerDescriptions() {
        return triggerDescriptions;
    }
}
