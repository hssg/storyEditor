package storygenerator.menu;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Container which stores chains of {@link storygenerator.menu.actions.Event} and {@link storygenerator.menu.actions.EventTransitionTrigger} by their (key) names.
 * A state consists of an event and a variable number of successors, which each consist of a possible event transition trigger and a following state.
 * Example:
 * Event is to move randomly (call it event R); 2 successors: when trigger A occurs, state C shall follow;
 * otherwise, if trigger B occurs, state D shall follow. Such tree-like compositions can be stored in a state.
 *
 * Problems can occur when two disks are in the same state at the same time, because only one event/trigger object exists.
 *
 * @author Svenja
 */
public class State implements Serializable {
    private static final long serialVersionUID = 1L;
    private String currentEvent; //specifies the current Event
    private HashMap<String, String> successors; //for each entry, the key string specifies the event transition trigger, the value string the successive state

    public State(String currentEvent) {
        this.currentEvent = currentEvent;
        this.successors = new HashMap<>(10);
    }

    public String getCurrentEvent() {
        return currentEvent;
    }

    /**
     * @param key the name of the event transition trigger
     * @return the state which follows that trigger
     */
    public String getSuccessiveState(String key) {
        return this.successors.get(key);
    }

    /**
     * @return all event transition triggers stored in the successor hashmap
     */
    public Set<String> getEventTransitionTriggers() {
        return this.successors.keySet();
    }

    /**
     * adds a new transition - state pair to the list of successors
     *
     * @param key   the name of the event transition trigger
     * @param value the name of the state
     */
    public void addSuccessor(String key, String value) {
        this.successors.put(key, value);
    }
}
