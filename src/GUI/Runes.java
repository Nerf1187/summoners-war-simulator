package GUI;

import javax.swing.*;
import Monsters.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static Game.Main.pause;

/**
 * This class starts all Rune GUI related tasks
 *
 * @author Anthony (Tony) Youssef
 */

public class Runes extends JFrame
{
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel mainLabel;
    private JPanel panel;
    private JButton viewButton;
    private JButton duplicateButton;
    private static String fileName;
    
    /**
     * Creates original the Rune GUI.
     */
    public void startRunes()
    {
        //General GUI stuff
        add(panel);
        setTitle("Action");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Set focus to the create button
        createButton.requestFocusInWindow();
        
        //Add keyboard shortcuts
        createButton.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyChar())
                {
                    case 'c' -> createButton.doClick();
                    case 'e' -> editButton.doClick();
                    case 'd' -> deleteButton.doClick();
                    case 'r' -> duplicateButton.doClick();
                }
            }
        });
        
        //Try to create a new rune file
        createButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!validFileName(fileName, 'c'))
            {
                //Show an error and exit the program
                System.err.println("Error, can not create file (file may already exist)");
                System.exit(1);
            }
            //Start the rune creation process
            CreateRuneFile.run(fileName, false, 1);
        });
        
        //Try to edit a file
        editButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!validFileName(fileName, 'e'))
            {
                //Show an error and exit the program
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            //Start the rune editing process
            EditRuneFile.run(fileName, 0);
        });
        
        //Try to delete a rune file
        deleteButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!validFileName(fileName, 'd'))
            {
                //Show an error and exit the program
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            //Start the rune deletion process
            DeleteRuneFile.run(fileName);
        });
        
        //Try to view a rune file
        viewButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!validFileName(fileName, 'v'))
            {
                //Show an error and exit the program
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            //Start the rune viewing process
            ViewRunes.run(fileName);
        });
        
        //Try to duplicate a rune file
        duplicateButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!validFileName(fileName, 'r'))
            {
                //Show an error and exit the program
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            //Start the rune duplicating process
            DuplicateRuneFile.run(fileName);
        });
    }
    
    /**
     * Runs this program.
     */
    public void main()
    {
        //Get the file name
        fileName = getFileName();
        startRunes();
    }
    
    /**
     * Calls {@link GetNameAndNum} to get the file name from the user
     *
     * @return The file name as given by the user
     */
    public static String getFileName()
    {
        //Get the name and rune set number from the user
        GetNameAndNum nameAndNum = new GetNameAndNum();
        
        //Prevent this function from continuing while the user is entering the information
        while (nameAndNum.isVisible())
        {
            pause(5);
        }
        
        //Get the proper Monster name
        String monName = Monster.toProperName(nameAndNum.monNameText.getText());
        
        //Try to set the rune number
        int runeSetNum = 0;
        try
        {
            runeSetNum = Integer.parseInt(nameAndNum.runeSetNumText.getText());
        }
        catch (NumberFormatException e) //Unable to parse the input to an int
        {
            System.err.println(e);
            System.exit(1);
        }
        
        //Return the formatted file name
        return "%s%d.csv".formatted(monName, runeSetNum);
    }
    
    /**
     * Tests whether a given String is a valid file name
     *
     * @param fileName The text to check
     * @param action   The requested action from the user
     * @return True if the text is a valid file name in the Runes/Monster_Runes directory, false otherwise
     */
    public static boolean validFileName(String fileName, char action)
    {
        //Checks if the name exists and does not contain "temp" in it
        if (fileName == null || fileName.contains("temp"))
        {
            return false;
        }
        
        //Make sure it is a valid action
        if (action != 'c' && action != 'e' && action != 'd' && action != 'v' && action != 'r')
        {
            System.err.printf("Error, cannot distinguish action \"%s\"%n", action);
            return false;
        }
        
        //Get the files in the Monster_Runes directory
        File folder = new File("src/Runes/Monster_Runes");
        List<File> runeSets = Arrays.stream(Objects.requireNonNull(folder.listFiles())).filter(file -> file.getName().contains(".csv")).toList();
        
        //Create
        if (action == 'c')
        {
            //Make sure the passed name is not the name of an already existing file
            for (File runeSet : runeSets)
            {
                if (runeSet.getName().equals(fileName))
                {
                    return false;
                }
            }
            return true;
        }
        else //All other actions
        {
            //Make sure the passed name is the name of an already existing file
            for (File runeSet : runeSets)
            {
                if (runeSet.getName().equals(fileName))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
}
