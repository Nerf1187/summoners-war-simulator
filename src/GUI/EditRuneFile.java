package GUI;

import javax.swing.*;
import Monsters.*;
import Runes.*;
import Util.Util.*;
import java.awt.event.*;
import java.util.*;

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
     * Creates the GUIS to edit the rune file
     *
     * @param fileName     The name of the file to edit
     * @param selectedRune The default rune to be selected
     */
    public EditRuneFile(String fileName, int selectedRune)
    {
        //Get the runes from the selected file
        runes = RUNES.getRunesFromFile(fileName, new Monster());
        
        //Add runes to the selector
        for (int i = 0; i < runes.size(); i++)
        {
            runeSelector.addItem(i + 1);
        }
        //Set the default rune
        runeSelector.setSelectedIndex(selectedRune);
        //Add all the attributes
        GUIS.addAllAttributes(newAttributeSelector);
        
        //General GUIS stuff
        add(panel);
        setTitle("Edit %s".formatted(fileName));
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Refresh available attributes when the focus changes to or from the rune selector
        runeSelector.addFocusListener(new FocusAdapter()
        {
            //Filter the list to only show attributes the specific rune has
            public void focusLost(FocusEvent e)
            {
                //Add all attributes to start
                GUIS.addAllAttributes(attributeSelector);
                //Get selected rune for editing
                Rune toEdit = runes.get(runeSelector.getSelectedIndex());
                outer:
                for (int i = attributeSelector.getItemCount() - 1; i >= 0; i--)
                {
                    //Check the attribute at index i
                    RuneAttribute attributeToCheck = RuneAttribute.stringToAttribute(attributeSelector.getItemAt(i));
                    //Check if the attribute is the rune's main attribute
                    if (toEdit.getMainAttribute().getAttribute() == attributeToCheck)
                    {
                        continue;
                    }
                    //Check if the attribute is one of the rune's sub attributes
                    for (SubAttribute subAttribute : toEdit.getSubAttributes())
                    {
                        if (subAttribute.getAttribute() == attributeToCheck)
                        {
                            continue outer;
                        }
                    }
                    //Remove the attribute if the rune does not have it
                    attributeSelector.removeItemAt(i);
                }
                //Show the rune type
                attributeSelector.addItem(toEdit.getType().toString());
            }
            
            //Filter the list to only show attributes the specific rune has
            public void focusGained(FocusEvent e)
            {
                focusLost(e);
            }
        });
        
        //Refresh available new attributes when the focus leaves the selector
        newAttributeSelector.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent e)
            {
                //Get rune to edit
                Rune toEdit = runes.get(runeSelector.getSelectedIndex());
                
                //Check if the user is changing the rune type or an attribute
                if (Objects.requireNonNull(attributeSelector.getSelectedItem()).toString().equals(toEdit.getType().toString())) //Change the rune type
                {
                    newAttributeSelector.removeAllItems();
                    changeType = true;
                    GUIS.addAllTypes(newAttributeSelector);
                }
                else //Change attribute
                {
                    changeType = false;
                    GUIS.addAllAttributes(newAttributeSelector);
                    //Get attribute to change
                    RuneAttribute attributeToChange = RuneAttribute.stringToAttribute(Objects.requireNonNull(attributeSelector.getSelectedItem()).toString());
                    for (int i = newAttributeSelector.getItemCount() - 1; i >= 0; i--)
                    {
                        RuneAttribute attributeToCheck = RuneAttribute.stringToAttribute(STRINGS.toEnumCase(newAttributeSelector.getItemAt(i)));
                        //Keep attribute if it is the same as the one to change
                        if (attributeToCheck == attributeToChange)
                        {
                            continue;
                        }
                        //Remove the attribute if is the rune's main attribute
                        if (toEdit.getMainAttribute().getAttribute() == attributeToCheck)
                        {
                            newAttributeSelector.removeItemAt(i);
                            continue;
                        }
                        //Remove the attribute if it one of the rune's sub effects
                        for (SubAttribute subAttribute : toEdit.getSubAttributes())
                        {
                            if (subAttribute.getAttribute() == attributeToCheck)
                            {
                                newAttributeSelector.removeItemAt(i);
                            }
                        }
                    }
                }
            }
        });
        
        //Update rune with new information
        confirm.addActionListener(_ -> {
            //Get rune to edit and new attribute
            Rune toEdit = runes.get(runeSelector.getSelectedIndex());
            String attributeToChange = Objects.requireNonNull(attributeSelector.getSelectedItem()).toString();
            String newAttribute = Objects.requireNonNull(newAttributeSelector.getSelectedItem()).toString();
            String newAmount = newAmountTxt.getText();
            //Change type
            if (STRINGS.stringIsInt(newAmount) || changeType)
            {
                //Change the rune type
                if (newAmount.isEmpty())
                {
                    newAmount = "0";
                }
                //Attempt to change the type
                if (!replaceAttribute(toEdit, attributeToChange, newAttribute, Integer.parseInt(newAmount)))
                {
                    //Show an error message if something went wrong
                    new Message("Error, cannot change attribute", true);
                }
                else //Change an attribute
                {
                    //Attempt to change the attribute and show a message displaying the results
                    if (FILES.editFile(fileName, runeSelector.getSelectedIndex() + 1, toEdit))
                    {
                        new Message("Success", false);
                    }
                    else
                    {
                        new Message("Error, cannot update file", true);
                    }
                    //Reset amount input
                    newAmountTxt.setText("");
                    runeSelector.requestFocusInWindow();
                }
                //Update runes in case they were changed
                runes = RUNES.getRunesFromFile(fileName, new Monster());
            }
        });
        
        //Close program
        doneButton.addActionListener(_ -> {
            dispose();
            System.exit(0);
        });
        
        //Switch to viewing runes
        toViewButton.addActionListener(_ -> {
            dispose();
            ViewRunes.run(fileName);
        });
        
        //Allow the user to press the Enter key to click confirm
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
        
        //Replace a singular rune
        replaceButton.addActionListener(_ -> CreateRuneFile.run(fileName, true, runeSelector.getSelectedIndex() + 1));
    }
    
    /**
     * Starts the GUIS
     *
     * @param fileName     The name of the file to edit
     * @param selectedRune The default rune to be selected
     */
    public static void run(String fileName, int selectedRune)
    {
        //Set the default rune if there is none passed
        if (selectedRune == -1)
        {
            selectedRune = 0;
        }
        new EditRuneFile(fileName, selectedRune);
    }
    
    /**
     * Replaces a single attribute in the selected rune. Does not change any files
     *
     * @param rune               The rune to edit
     * @param oldAttributeName   The name of the old attribute
     * @param newAttributeName   The name of the new attribute
     * @param newAttributeAmount The amount for the new attribute input 0 when changing the type
     *
     * @return True if the attribute was successfully changed, false otherwise
     */
    private boolean replaceAttribute(Rune rune, String oldAttributeName, String newAttributeName, int newAttributeAmount)
    {
        RuneAttribute oldAttributeNum = RuneAttribute.stringToAttribute(oldAttributeName);
        RuneAttribute newAttributeNum = RuneAttribute.stringToAttribute(newAttributeName);
        
        //Change the type
        if (changeType)
        {
            rune.setType(RuneType.stringToType(newAttributeName));
            return true;
        }
        
        //Change the main attribute
        if (rune.getMainAttribute().getAttribute() == oldAttributeNum)
        {
            MainAttribute newAttribute = new MainAttribute(newAttributeNum, newAttributeAmount);
            rune.setMainAttribute(newAttribute);
            return true;
        }
        
        //Change one of the sub attributes
        ArrayList<SubAttribute> subAttributes = rune.getSubAttributes();
        for (int i = 0; i < subAttributes.size(); i++)
        {
            SubAttribute subAttribute = subAttributes.get(i);
            if (subAttribute.getAttribute() == oldAttributeNum)
            {
                SubAttribute newAttribute = new SubAttribute(newAttributeNum, newAttributeAmount);
                subAttributes.set(i, newAttribute);
                return true;
            }
        }
        return false;
    }
}