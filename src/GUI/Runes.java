package GUI;

import javax.swing.*;
import Monsters.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static Game.Main.pause;

/**
 * This class starts all GUI related tasks
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
     * Creates original the GUI
     */
    public Runes()
    {
        add(panel);
        setTitle("Action");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        createButton.requestFocusInWindow();
        createButton.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyChar())
                {
                    case 'c':
                    {
                        createButton.doClick();
                        break;
                    }
                    case 'e':
                    {
                        editButton.doClick();
                        break;
                    }
                    case 'd':
                    {
                        deleteButton.doClick();
                        break;
                    }
                    case 'r':
                    {
                        duplicateButton.doClick();
                        break;
                    }
                }
            }
        });
        
        createButton.addActionListener(e -> {
            dispose();
            if (!validFileName(fileName, 'c'))
            {
                System.err.println("Error, can not create file (file may already exist)");
                System.exit(1);
            }
            String[] temp = {fileName};
            CreateRuneFile.run(temp);
        });
        editButton.addActionListener(e -> {
            dispose();
            if (!validFileName(fileName, 'e'))
            {
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            EditRuneFile.run(fileName);
        });
        deleteButton.addActionListener(e -> {
            dispose();
            if (!validFileName(fileName, 'd'))
            {
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            DeleteRuneFile.run(fileName);
        });
        viewButton.addActionListener(e -> {
            dispose();
            if (!validFileName(fileName, 'v'))
            {
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            ViewRunes.run(fileName);
        });
        duplicateButton.addActionListener(e -> {
            dispose();
            if (!validFileName(fileName, 'r'))
            {
                System.err.println("Error, cannot find file");
                System.exit(1);
            }
            DuplicateRuneFile.run(fileName);
        });
    }
    
    /**
     * Runs this program
     */
    public static void main(String[] args)
    {
        fileName = getFileName();
        new Runes();
    }
    
    /**
     * Calls {@link GetNameAndNum} to get the file name from the user
     *
     * @return the file name as given by the user
     */
    public static String getFileName()
    {
        GetNameAndNum nameAndNum = new GetNameAndNum();
        while (nameAndNum.isVisible())
        {
            pause(5);
        }
        String monName = Monster.toProperName(nameAndNum.monNameTxt.getText());
        int runeSetNum = 0;
        try
        {
            runeSetNum = Integer.parseInt(nameAndNum.runeSetNumText.getText());
        }
        catch (NumberFormatException e)
        {
            System.err.println(e);
            System.exit(1);
        }
        
        return monName + runeSetNum + ".csv";
    }
    
    /**
     * Tests whether a given String is a valid file name
     * @param fileName The text to check
     * @param action The requested action from the user
     * @return True if the text is a valid file name in the Runes/Monster_Runes directory, false otherwise
     */
    public static boolean validFileName(String fileName, char action)
    {
        if (action != 'c' && action != 'e' && action != 'd' && action != 'v' && action != 'r')
        {
            System.err.println("Error, cannot distinguish action \"" + action + "\"");
            return false;
        }
        
        File folder = new File("src/Runes/Monster_Runes");
        List<File> runeSets = Arrays.stream(Objects.requireNonNull(folder.listFiles())).filter(file -> file.getName().contains(".csv")).toList();
        
        if (action == 'c')
        {
            for (File runeSet : runeSets)
            {
                if (runeSet.getName().equals(fileName))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
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
