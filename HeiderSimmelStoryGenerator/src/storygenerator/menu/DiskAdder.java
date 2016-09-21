package storygenerator.menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;

public class DiskAdder extends JFrame {

    protected JButton buttonOK;
    protected JButton assignDiskColorButton;
    protected boolean colorChosen = false;

    protected String diskName;
    protected double x;
    protected double y;
    protected double radius;
    protected Color col;

    protected JTextField tf1;
    protected JFormattedTextField tf2;
    protected JFormattedTextField tf3;
    protected JFormattedTextField tf4;

    public DiskAdder() {
        x = 0.0;
        y = 0.0;
    }

    /**
     * changing a MenuDisk
     *
     * @param d disk to be changed
     */
    public DiskAdder(MenuDisk d) {
        initialise(0, d);
    }

    /**
     * creating a new MenuDisk
     */
    public DiskAdder(int num) {
        initialise(num, null);
    }

    private void initialise(int num, MenuDisk d) {
        String diskName = "DiskName" + Integer.toString(num + 1);
        String defaultX = "50";
        String defaultY = "50";
        String defaultRadius = "2";
        String defaultTitle = "Creating a disk";
        if (d != null) {
            diskName = d.diskName;
            defaultX = Integer.toString((int) (d.x * 100.0));
            defaultY = Integer.toString((int) (d.y * 100.0));
            defaultRadius = Integer.toString((int) (d.radius * 100.0));
            defaultTitle = "Changing a disk";
        }
        setTitle(defaultTitle);
        setSize(600, 100);
        JPanel panel = new JPanel();


        tf1 = new JTextField(diskName, 15);
        tf2 = new JFormattedTextField(new DecimalFormat("00"));
        tf2.setColumns(4);
        tf2.setText(defaultX);
        x = parse(tf2, 0);
        tf3 = new JFormattedTextField(new DecimalFormat("00"));
        tf3.setColumns(4);
        tf3.setText(defaultY);
        y = parse(tf3, 0);
        tf4 = new JFormattedTextField(new DecimalFormat("00"));
        tf4.setColumns(4);
        tf4.setText(defaultRadius);
        radius = parse(tf4, 0);

        tf2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                x = parse(tf2, x);
            }
        });
        tf2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                y = parse(tf3, y);
            }
        });
        tf3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radius = parse(tf3, radius);
            }
        });

        designDefaultTextFieldsAndLabel(panel);

        col = DiskWorldStory.DEFAULT_DISK_COLOR;

        designColorChooseButton(panel);

        add(panel);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);
    }

    private double parse(JFormattedTextField tf, double fallBack) {
        try {
            return Double.parseDouble(tf.getText()) / 100.0;
        } catch (NumberFormatException e) {
            return fallBack;
        }
    }

    private void designColorChooseButton(JPanel panel) {
        buttonOK = new JButton("OK");
        assignDiskColorButton = new JButton("Color");
        getRootPane().setDefaultButton(buttonOK);

        panel.add(buttonOK);
        panel.add(assignDiskColorButton);
        DiskAdder adder = this;

        assignDiskColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == assignDiskColorButton) {
                    col = JColorChooser.showDialog(adder, "Select Color", Color.BLUE);
                    colorChosen = true;
                }
            }
        });
    }

    private void designDefaultTextFieldsAndLabel(JPanel panel) {
        tf1.setForeground(Color.BLUE);
        tf1.setBackground(Color.YELLOW);
        panel.add(tf1);

        tf2.setForeground(Color.BLUE);
        tf2.setBackground(Color.YELLOW);
        panel.add(tf2);

        tf3.setForeground(Color.BLUE);
        tf3.setBackground(Color.YELLOW);
        panel.add(tf3);

        tf4.setForeground(Color.BLUE);
        tf4.setBackground(Color.YELLOW);
        panel.add(tf4);

        JLabel jlabel1 = new JLabel("Please enter x, y and radius e[0, 1] without `.´  e.g. 13 instead of 0.13");
        panel.add(jlabel1);
    }


    public void setCircleData(double x, double y, double r) {
        tf2.setText("" + (int) Math.round(100 * x));
        tf3.setText("" + (int) Math.round(100 * y));
        tf4.setText("" + (int) Math.round(100 * r));
        this.x = x;
        this.y = y;
        this.radius = r;
    }


}
