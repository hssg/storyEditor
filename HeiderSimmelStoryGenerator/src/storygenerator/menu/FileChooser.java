package storygenerator.menu;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser extends JFileChooser {

    public StoryData data;
    public boolean isChosen = false;
    public boolean isCorrect = false;

    public FileChooser() {
        //		fc.setFileFilter( new FileNameExtensionFilter(".txt",
        //				"*.html", "*.log" ));

        int state = showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            File chosenFile = getSelectedFile();

            if (FileHandler.isStory(chosenFile)) {

                data = FileHandler.getStoryFromFile(chosenFile);
                isCorrect = true;

            } else {
                new ErrorMessageFrame("Error - no story", "The file you "
                        + "chose did not contain a story. Please"
                        + " select a file that contains a readable"
                        + " story!");
            }

        } else if (state != JFileChooser.CANCEL_OPTION) {
            new ErrorMessageFrame("Error - wrong file", "Please choose a "
                    + ".txt file!");
        }

        isChosen = true;
    }
}
