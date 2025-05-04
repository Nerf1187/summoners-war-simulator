package GUI;

import javax.swing.*;
import Runes.Monster_Runes.*;
import Util.Util.*;
import java.awt.event.*;
import java.io.*;

/**
 * This class starts all Rune GUIS related tasks
 *
 * @author Anthony (Tony) Youssef
 */

public class Runes extends JFrame
{
    public enum FileAction
    {
        CREATE,
        EDIT,
        DELETE,
        VIEW,
        DUPLICATE
    }
    
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel mainLabel;
    private JPanel panel;
    private JButton viewButton;
    private JButton duplicateButton;
    
   
    /**
     * Creates original the Rune GUIS.
     *
     * @param fileName The name of the file to use
     */
    public void startRunes(String fileName)
    {
        //General GUIS stuff
        add(panel);
        setTitle("FileAction");
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
            if (!FILES.isValidFileName(fileName, FileAction.CREATE))
            {
                //Show an error and exit the program
                new Message("Error, cannot create file (file may already exist)", true, () -> System.exit(1));
                return;
            }
            //Start the rune creation process
            CreateRuneFile.run(fileName, false, 1);
        });
        
        //Try to edit a file
        editButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!FILES.isValidFileName(fileName, FileAction.EDIT))
            {
                //Show an error and exit the program
                new Message("Error, cannot find file", true, () -> System.exit(1));
                return;
            }
            //Start the rune editing process
            EditRuneFile.run(fileName, 0);
        });
        
        //Try to delete a rune file
        deleteButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!FILES.isValidFileName(fileName, FileAction.DELETE))
            {
                //Show an error and exit the program
                new Message("Error, cannot find file", true, () -> System.exit(1));
                return;
            }
            //Start the rune deletion process
            new ConfirmationWindow("Are you sure you want to delete %s? This action can not be undone".
                    formatted(fileName), () -> {
                //Try to delete the file and show a message displaying the result
                if (new File("%s/%s".formatted(MonsterRunes.path, fileName)).delete())
                {
                    new Message("Success", false);
                }
                else
                {
                    new Message("Error", true);
                }
                dispose();
            }, () -> {
                //Show message that the action was canceled
                new Message("Aborted", false);
                dispose();
            });
        });
        
        //Try to view a rune file
        viewButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!FILES.isValidFileName(fileName, FileAction.VIEW))
            {
                //Show an error and exit the program
                new Message("Error, cannot find file", true, () -> System.exit(1));
                return;
            }
            //Start the rune viewing process
            ViewRunes.run(fileName);
        });
        
        //Try to duplicate a rune file
        duplicateButton.addActionListener(_ -> {
            dispose();
            //Make sure the file name is valid
            if (!FILES.isValidFileName(fileName, FileAction.DUPLICATE))
            {
                //Show an error and exit the program
                new Message("Error, cannot find file", true, () -> System.exit(1));
                return;
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
        new GetNameAndNum(this);
    }
}