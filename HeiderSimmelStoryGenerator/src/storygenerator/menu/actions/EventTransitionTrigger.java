package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import storygenerator.menu.BehaviourEditorFrame;
import storygenerator.menu.DiskWorldStory;

import java.io.Serializable;

/**
 * Abstract class for conditions that can trigger transitions from one event to another.
 * To add a new trigger, just extend this class.
 * Caution: Also inform the {@link storygenerator.menu.Controller} about the new class by adding it in the contructor.
 *
 * @author Svenja
 */
public abstract class EventTransitionTrigger implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double environmentSize = DiskWorldStory.SIZE_OF_ENVIRONMENT;

    public EventTransitionTrigger() {
    }

    /**
     * Method that checks whether, in the given time step, the eventTransitionTrigger is provoked.
     *
     * @param disk                the referred disk
     * @param currentTime
     * @param momentOfLastTrigger time when the last transition (behaviour change) of that disk took place
     * @param env
     * @return true when the trigger has occured, otherwise false
     */
    public abstract boolean hasOccurred(Disk disk, double currentTime, double momentOfLastTrigger, Environment env);

    /**
     * Method to customize a {@link BehaviourEditorFrame} according to the event transition trigger's fields.
     * The frame shall be edited such, that all relevant fields of the trigger are editable in the frame.
     * This can be done by calling the add... methods of the frame:
     * {@link BehaviourEditorFrame#addText(String)}, just for text,
     * {@link BehaviourEditorFrame#addValueChooser(double, double, double, String, String)}, for doubles with min/max value,
     * {@link BehaviourEditorFrame#addValueChooser(double, double, String, String)}, for doubles with only min value,
     * {@link BehaviourEditorFrame#addBooleanChooser(double, String, String)}, for booleans (in double form ->
     * make a double field and a boolean field; call this method; the double field will be set to 1 or 0,
     * change your boolean accordingly in the setter of the double.),
     * {@link BehaviourEditorFrame#addReferenceDiskChooser(String, String)}, for reference Disks.
     * {@link BehaviourEditorFrame#addGUILineChooser(Double[][], String, String)}, if the user shall be able to draw a line
     * (multiple 2d points, can be null in the beginning)  e.g. for a trajectory,
     * {@Link BehaviourEditorFrame#addCoordinateChooser}, if the user shall be able to choose a coordinate. The corresponding location
     * is visualized in the environment screen.
     * <p>
     * !!! Be careful: for all add-methods except for addText, you have to pass the (original) name of the field!
     * !!! Also be sure to provide setters for the fields you want to be editable.
     *
     * @param frame Menuframe to edit
     */
    public abstract void editMenuFrame(BehaviourEditorFrame frame);

    /**
     * Method to reset fields before the start of a simulation. Is called by the controller.
     * Overwrite this method if you have fields to reset or computing to be done before the start of a simulation.
     */
    public void initialise() {

    }


}
