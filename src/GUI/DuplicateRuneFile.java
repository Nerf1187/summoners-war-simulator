package GUI;

import javax.swing.*;
import Monsters.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import static GUI.CreateRuneFile.stringIsInt;

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
     * Creates the GUI
     *
     * @param oldFileName The name of the file to duplicate
     */
    public DuplicateRuneFile(String oldFileName)
    {
        add(panel);
        setTitle("Enter new information");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        submitButton.addActionListener(e -> {
            String name = nameTextField.getText();
            String num = numberTextField.getText();
            if (name.isEmpty())
            {
                return;
            }
            if (!num.isEmpty() && !stringIsInt(num))
            {
                return;
            }
            name = Monster.toProperName(name);
            /*Get default number*/
            if (num.isEmpty())
            {
                int numOfSameMonsterRuneSets = 0;
                int lastNum = 0;
                File runeFolder = new File("src/Runes/Monster_Runes");
                List<File> runeSets = Arrays.stream(Objects.requireNonNull(runeFolder.listFiles())).filter(file -> file.getName().contains(".csv")).toList();
                for (File set : runeSets)
                {
                    int lastNameIndex = 0;
                    String fileName = set.getName().substring(0, oldFileName.indexOf(".csv"));
                    /*Get file name without number*/
                    for (int i = fileName.length() - 1; i > 0; i--)
                    {
                        if (!stringIsInt(fileName.substring(i, i + 1)))
                        {
                            lastNameIndex = i;
                            break;
                        }
                    }
                    
                    /*Find the best number to use for the file name*/
                    if (fileName.substring(0, lastNameIndex + 1).equalsIgnoreCase(name))
                    {
                        int i = Integer.parseInt(fileName.substring(lastNameIndex + 1));
                        if (lastNum + 1 == i)
                        {
                            lastNum = i;
                            numOfSameMonsterRuneSets++;
                        }
                    }
                    
                    num = numOfSameMonsterRuneSets + 1 + "";
                }
            }
            
            String newFileName = name + num + ".csv";
            if (!duplicateFile(oldFileName, newFileName))
            {
                new Message("Error, can not duplicate file", true);
            }
            else
            {
                dispose();
                new Message("Success!", false);
            }
        });
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
     * Runs the GUI
     *
     * @param oldFileName The name of the file to duplicate
     */
    public static void run(String oldFileName)
    {
        new DuplicateRuneFile(oldFileName);
    }
    
    /**
     * Duplicates the requested file
     *
     * @param oldFileName The name of the file to duplicate
     * @param newFileName The duplicated file's new name
     * @return True if and only if the file was successfully duplicated
     */
    private boolean duplicateFile(String oldFileName, String newFileName)
    {
        if (oldFileName.equalsIgnoreCase(newFileName))
        {
            return false;
        }
        if (!Runes.validFileName(newFileName, 'c') || !Runes.validFileName(oldFileName, 'r'))
        {
            return false;
        }
        try
        {
            Scanner oldFile = new Scanner(new File("src/Runes/Monster_Runes/" + oldFileName));
            File newFile = new File("src/Runes/Monster_Runes/" + newFileName);
            FileWriter newFileWriter = new FileWriter(newFile);
            while (oldFile.hasNextLine())
            {
                newFileWriter.write(oldFile.nextLine() + "\n");
            }
            newFileWriter.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return true;
    }
}
