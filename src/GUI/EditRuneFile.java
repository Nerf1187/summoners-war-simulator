package GUI;

import javax.swing.*;
import Monsters.*;
import Runes.*;
import Runes.Monster_Runes.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static GUI.CreateRuneFile.*;

/**
 * Allows the user to easily edit their rune set.
 *
 * @author Anthony (Tony) Youssef
 */
public class EditRuneFile extends JFrame
{
    private JLabel chooseRuneLabel;
    private JComboBox<Integer> runeSelector;
    private JLabel chooseAttributeLabel;
    private JComboBox<String> attributeSelector;
    private JTextField newAmountTxt;
    private JComboBox<String> newAttributeSelector;
    private JPanel panel;
    private JLabel newAmountLabel;
    private JButton confirm;
    private JLabel newAttributeLabel;
    private JButton doneButton;
    private JButton toViewButton;
    private JButton replaceButton;
    private ArrayList<Rune> runes;
    
    
    private boolean changeType = false;
    
    /**
     * Creates the GUI to edit the rune file
     *
     * @param fileName     The name of the file to edit
     * @param selectedRune The default rune to be selected
     */
    public EditRuneFile(String fileName, int selectedRune)
    {
        runes = MonsterRunes.getRunesFromFile(fileName, new Monster());
        for (int i = 0; i < runes.size(); i++)
        {
            runeSelector.addItem(i + 1);
        }
        runeSelector.setSelectedIndex(selectedRune);
        addAllAttributes(newAttributeSelector);
        
        add(panel);
        setTitle("Edit " + fileName);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        runeSelector.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                //Filter list to only show attributes the specific rune has
                addAllAttributes(attributeSelector);
                Rune toEdit = runes.get(runeSelector.getSelectedIndex());
                for (int i = attributeSelector.getItemCount() - 1; i >= 0; i--)
                {
                    int attributeToCheck = Rune.stringToNum(attributeSelector.getItemAt(i));
                    if (toEdit.getMainAttribute().getNum() == attributeToCheck)
                    {
                        continue;
                    }
                    boolean runeHasAttribute = false;
                    for (SubAttribute subAttribute : toEdit.getSubAttributes())
                    {
                        if (subAttribute.getNum() == attributeToCheck)
                        {
                            runeHasAttribute = true;
                            break;
                        }
                    }
                    if (runeHasAttribute)
                    {
                        continue;
                    }
                    attributeSelector.removeItemAt(i);
                }
                //Show Rune type
                attributeSelector.addItem(Rune.numToType(toEdit.getType()));
            }
            
            public void focusGained(FocusEvent e)
            {
                focusLost(e);
            }
        });
        
        newAttributeSelector.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent e)
            {
                Rune toEdit = runes.get(runeSelector.getSelectedIndex());
                if (Objects.requireNonNull(attributeSelector.getSelectedItem()).toString().equals(Rune.numToType(toEdit.getType())))
                {
                    newAttributeSelector.removeAllItems();
                    changeType = true;
                    addAllTypes(newAttributeSelector);
                }
                else
                {
                    changeType = false;
                    addAllAttributes(newAttributeSelector);
                    int attributeToChange = Rune.stringToNum(Objects.requireNonNull(attributeSelector.getSelectedItem()).toString());
                    for (int i = newAttributeSelector.getItemCount() - 1; i >= 0; i--)
                    {
                        int attributeToCheck = Rune.stringToNum(newAttributeSelector.getItemAt(i));
                        if (attributeToCheck == attributeToChange)
                        {
                            continue;
                        }
                        if (toEdit.getMainAttribute().getNum() == attributeToCheck)
                        {
                            newAttributeSelector.removeItemAt(i);
                            continue;
                        }
                        for (SubAttribute subAttribute : toEdit.getSubAttributes())
                        {
                            if (subAttribute.getNum() == attributeToCheck)
                            {
                                newAttributeSelector.removeItemAt(i);
                            }
                        }
                    }
                }
            }
        });
        
        confirm.addActionListener(_ -> {
            Rune toEdit = runes.get(runeSelector.getSelectedIndex());
            String attributeToChange = Objects.requireNonNull(attributeSelector.getSelectedItem()).toString();
            String newAttribute = Objects.requireNonNull(newAttributeSelector.getSelectedItem()).toString();
            String newAmount = newAmountTxt.getText();
            if (stringIsInt(newAmount) || changeType)
            {
                if (newAmount.isEmpty())
                {
                    newAmount = "0";
                }
                if (!replaceAttribute(toEdit, attributeToChange, newAttribute, Integer.parseInt(newAmount)))
                {
                    new Message("Error, cannot change attribute", true);
                }
                else
                {
                    if (editFile(fileName, runeSelector.getSelectedIndex() + 1, toEdit))
                    {
                        new Message("Success", false);
                    }
                    else
                    {
                        new Message("Error, cannot update file", true);
                    }
                    newAmountTxt.setText("");
                    runeSelector.requestFocusInWindow();
                }
                runes = MonsterRunes.getRunesFromFile(fileName, new Monster());
            }
        });
        
        doneButton.addActionListener(_ -> {
            dispose();
            System.exit(0);
        });
        
        toViewButton.addActionListener(_ -> {
            dispose();
            ViewRunes.run(fileName);
        });
        
        newAmountTxt.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    confirm.doClick();
                }
            }
        });
        
        replaceButton.addActionListener(_ -> CreateRuneFile.run(new String[]{fileName}, true, runeSelector.getSelectedIndex() + 1));
    }
    
    /**
     * Starts the GUI
     *
     * @param fileName    The name of the file to edit
     * @param selectedRune The default rune to be selected
     */
    public static void run(String fileName, int selectedRune)
    {
        if (selectedRune == -1)
        {
            selectedRune = 0;
        }
        new EditRuneFile(fileName, selectedRune);
    }
    
    private boolean replaceAttribute(Rune rune, String oldAttributeName, String newAttributeName, int newAttributeAmount)
    {
        int oldAttributeNum = Rune.stringToNum(oldAttributeName);
        int newAttributeNum = Rune.stringToNum(newAttributeName);
        
        if (changeType)
        {
            rune.setType(newAttributeNum);
            return true;
        }
        
        if (rune.getMainAttribute().getNum() == oldAttributeNum)
        {
            MainAttribute newAttribute = new MainAttribute(newAttributeNum, newAttributeAmount);
            rune.setMainAttribute(newAttribute);
            return true;
        }
        ArrayList<SubAttribute> subAttributes = rune.getSubAttributes();
        for (int i = 0; i < subAttributes.size(); i++)
        {
            SubAttribute subAttribute = subAttributes.get(i);
            if (subAttribute.getNum() == oldAttributeNum)
            {
                SubAttribute newAttribute = new SubAttribute(newAttributeNum, newAttributeAmount);
                subAttributes.set(i, newAttribute);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Edits the chosen line in the chosen rune file. It does this by writing to a temporary file, saving the original to a different name,
     * changing the new file's name to the requested name, and deletes the original file.
     *
     * @param fileName The name of the file to edit. Should end in ".csv"
     * @param lineNum The line number to edit
     * @param newRune The new Rune to replace the old one with
     * @return True if and only if the file was successfully edited
     */
    public static boolean editFile(String fileName, int lineNum, Rune newRune)
    {
        File newFile = null;
        try
        {
            newFile = new File("src/Runes/Monster_Runes/tempFile.csv");
            FileWriter newFileWriter = new FileWriter(newFile);
            File oldFile = new File("src/Runes/Monster_Runes/" + fileName);
            Scanner read = new Scanner(oldFile);
            
            for (int i = 1; read.hasNextLine(); i++)
            {
                if (i == lineNum)
                {
                    CreateRuneFile.writeRuneToFile(newFileWriter, newRune);
                    read.nextLine();
                }
                else
                {
                    newFileWriter.write(read.nextLine() + "\n");
                }
            }
            newFileWriter.close();
            read.close();
            if (oldFile.renameTo(new File("src/Runes/Monster_Runes/oldTempFile.csv")))
            {
                if (newFile.renameTo(new File("src/Runes/Monster_Runes/" + fileName)))
                {
                    /*Delete the original file  ****DO NOT REMOVE**** */
                    File temp = new File("src/Runes/Monster_Runes/oldTempFile.csv");
                    temp.delete();
                    
                    return true;
                }
                else
                {
                    oldFile.renameTo(new File("src/Runes/Monster_Runes/" + fileName));
                    return false;
                }
            }
            else
            {
                newFile.delete();
                return false;
            }
        }
        catch (Exception e)
        {
            newFile.delete();
            return false;
        }
    }
}
