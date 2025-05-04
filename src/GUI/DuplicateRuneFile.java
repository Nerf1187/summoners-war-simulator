package GUI;

import javax.swing.*;
import Util.Util.*;
import java.awt.event.*;
import java.io.*;

/**
 * This class is used to duplicate a rune file
 *
 * @author Anthony (Tony) Youssef
 */
public class DuplicateRuneFile extends JFrame
{
    private JPanel panel;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel numberLabel;
    private JTextField numberTextField;
    private JButton submitButton;
    
    /**
     * Creates the GUIS
     *
     * @param oldFileName The name of the file to duplicate
     */
    public DuplicateRuneFile(String oldFileName)
    {
        //General GUIS stuff
        add(panel);
        setTitle("Enter new information");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        submitButton.addActionListener(_ -> {
            String name = nameTextField.getText();
            String num = numberTextField.getText();
            //Check for valid inputs
            if (name.isEmpty())
            {
                return;
            }
            if (!num.isEmpty() && !STRINGS.stringIsInt(num))
            {
                return;
            }
            name = MONSTERS.toProperName(name);
            //Get default number
            if (num.isEmpty())
            {
                int numOfSameMonsterRuneSets = 0;
                int lastNum = 0;
                
                for (File set : FILES.getRuneSets())
                {
                    int lastNameIndex = 0;
                    String fileName = STRINGS.substringUpToString(set.getName(), ".csv");
                    //Get file name without its number
                    for (int i = fileName.length() - 1; i > 0; i--)
                    {
                        if (!STRINGS.stringIsInt(fileName.substring(i, i + 1)))
                        {
                            lastNameIndex = i;
                            break;
                        }
                    }
                    
                    //Find the best number to use for the file name
                    if (fileName.substring(0, lastNameIndex + 1).equalsIgnoreCase(name))
                    {
                        int i = Integer.parseInt(fileName.substring(lastNameIndex + 1));
                        if (lastNum + 1 == i)
                        {
                            lastNum = i;
                            numOfSameMonsterRuneSets++;
                        }
                    }
                    
                    //Save number
                    num = numOfSameMonsterRuneSets + 1 + "";
                }
            }
            
            //Set duplicate file name
            String newFileName = "%s%s.csv".formatted(name, num);
            
            //Attempt to duplicate file and display a message showing the result
            if (!FILES.duplicateFile(oldFileName, newFileName))
            {
                new Message("Error, can not duplicate file", true);
            }
            else
            {
                dispose();
                new Message("Success", false, () -> new Message("Duplicate saved to %s".formatted(newFileName), false));
            }
        });
        
        //Add key listeners for the text fields
        numberTextField.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    submitButton.doClick();
                }
            }
        });
        nameTextField.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    submitButton.doClick();
                }
            }
        });
    }
    
    /**
     * Runs the GUIS
     *
     * @param oldFileName The name of the file to duplicate
     */
    public static void run(String oldFileName)
    {
        new DuplicateRuneFile(oldFileName);
    }
}
