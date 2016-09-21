package storygenerator.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;

/**
 * various classes to handle choosing / saving / reading from files.
 */
public class FileHandler {

    protected static boolean isStory(File file) {
        InputStream fis = null;
        StoryData sampleStory = null;
        try {
            fis = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(fis);

            sampleStory = (StoryData) o.readObject();
        } catch (IOException e) {
            System.out.println(e);
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return false;
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                System.out.println("Close failed: " + e);
                return false;
            }
        }

        return true;
    }

    protected static StoryData getStoryFromFile(File file) {
        InputStream fis = null;
        StoryData sampleStory = null;
        try {
            fis = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(fis);

            sampleStory = (StoryData) o.readObject();
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return sampleStory;
    }


    protected static void openFileSaver(String nameOfFile, StoryData story) {


        JFileChooser fc = new JFileChooser();

        fc.setSelectedFile(new File(nameOfFile));

        int state = fc.showSaveDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            File chosenDirectory = fc.getSelectedFile();

            safeStoryToFile(chosenDirectory, story);

        } else if (state != JFileChooser.CANCEL_OPTION) {
            new ErrorMessageFrame("Error - wrong file", "Please choose a "
                    + ".txt file!");
        }
    }

    protected static void safeStoryToFile(File chosenDirectory, StoryData myStory) {
        OutputStream fos = null;

        System.out.println("file safed in directory: " + chosenDirectory);

        try {

            File file = new File(chosenDirectory + ".txt");

            fos = new FileOutputStream(file);

            ObjectOutputStream o = new ObjectOutputStream(fos);

            o.writeObject(myStory);

            fos.flush();
            o.close();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
