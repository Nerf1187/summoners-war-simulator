package GUI;

import javax.swing.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Runes.*;
import Util.Util.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Creates a GUIS to create the code for rune classes
 *
 * @author Anthony (Tony) Youssef
 */
public class CreateRuneFile extends JFrame
{
    private JLabel mainAttributeLabel;
    private JComboBox<String> attributes;
    private JButton addAttribute;
    private JPanel panel;
    private JTextField amountTextField;
    private JLabel amountLabel;
    private JButton submit;
    private JLabel runeTypeLabel;
    private JComboBox<String> runeTypes;
    private JLabel currentRuneNumLabel;
    private JButton quitSaveButton;
    private JButton quitNoSaveButton;
    private String mainAttribute, runeType;
    private final ArrayList<String> subAttributes = new ArrayList<>();
    private static String csvRunes = "";
    
    /**
     * Creates the GUIS and implements the keyboard shortcuts
     *
     * @param runeNum    Current rune number
     * @param singleRune True if the GUIS should create one rune then stop, false otherwise
     * @param fileName   The name of the file to write to
     * @throws IndexOutOfBoundsException If runeNum is out of range
     */
    public CreateRuneFile(int runeNum, boolean singleRune, String fileName)
    {
        currentRuneNumLabel.setText("Current rune number: %d".formatted(runeNum));
        if (runeNum < 1 || runeNum > 8)
        {
            throw new IndexOutOfBoundsException("runeNum must be between 1 and 8 inclusive");
        }
        //Add all rune attributes
        GUIS.addAllAttributes(attributes);
        //Prevent keyboard inputs
        attributes.setFocusTraversalKeysEnabled(false);
        
        //Add al rune set types
        GUIS.addAllTypes(runeTypes);
        //Prevent keyboard inputs
        runeTypes.setFocusTraversalKeysEnabled(false);
        
        //General GUIS stuff
        add(panel);
        setTitle("Select main attribute");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Automatically set the rune type to an artifact and set the focus on the attribute selector if needed
        switch (runeNum)
        {
            case 7 ->
            {
                runeTypes.setSelectedItem("Element Artifact");
                runeTypes.setEditable(false);
                attributes.requestFocusInWindow();
                amountTextField.setEditable(false);
            }
            case 8 ->
            {
                runeTypes.setSelectedItem("Type Artifact");
                runeTypes.setEditable(false);
                attributes.requestFocusInWindow();
                amountTextField.setEditable(false);
            }
            default -> runeTypes.requestFocusInWindow();
        }
        
        //Click the add attribute button if the Enter key is pressed while focused on the amount text field
        amountTextField.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    addAttribute.doClick();
                }
            }
        });
        
        //Switch focus to the attribute selector if the Tab key is pressed while focused on the rune type selector
        runeTypes.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\t')
                {
                    attributes.requestFocusInWindow();
                }
            }
        });
        
        //Submit the rune if the Escape key is pressed while focused on the attribute selector
        //Automatically fill in values and submit if the current rune is an artifact and the Tab key is pressed
        attributes.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    submit.doClick();
                }
                if (e.getKeyChar() == '\t')
                {
                    switch (runeNum)
                    {
                        case 7, 8 ->
                        {
                            switch (Objects.requireNonNull(attributes.getSelectedItem()).toString())
                            {
                                case "Atk", "Def" -> amountTextField.setText("100");
                                case "HP" -> amountTextField.setText("1500");
                            }
                            addAttribute.doClick();
                            submit.doClick();
                        }
                        default -> amountTextField.requestFocusInWindow();
                    }
                }
            }
        });
        
        addAttribute.addActionListener(_ -> {
            if (!amountTextField.getText().isBlank())
            {
                if (STRINGS.stringIsInt(amountTextField.getText()))
                {
                    //Add selected attribute and amount as a main attribute if there is none yet
                    if (mainAttribute == null)
                    {
                        mainAttribute = "Rune.%s, %s".formatted(STRINGS.toEnumCase(Objects.requireNonNull(attributes.getSelectedItem()).toString()), amountTextField.getText());
                        mainAttributeLabel.setText("Add sub attributes, press \"Submit Rune\" when done.");
                        setTitle("Select sub attributes");
                    }
                    else //Add the selected attribute and amount as a sub attribute
                    {
                        subAttributes.add("Rune.%s,%s".formatted(Objects.requireNonNull(attributes.getSelectedItem()).toString().toUpperCase(), amountTextField.getText()));
                    }
                    //Remove choice
                    attributes.removeItem(attributes.getSelectedItem());
                    runeType = "Rune.%s".formatted(STRINGS.toEnumCase(Objects.requireNonNull(runeTypes.getSelectedItem()).toString()));
                    amountTextField.setText("");
                    attributes.requestFocusInWindow();
                }
                else
                {
                    amountTextField.setText("");
                    amountTextField.requestFocusInWindow();
                }
            }
            else //No amount entered
            {
                //Set focus to amount text field
                amountTextField.setText("");
                amountTextField.requestFocusInWindow();
            }
        });
        
        submit.addActionListener(_ -> {
            //Make sure at least one attribute was entered
            if (mainAttribute != null)
            {
                //Format sub attributes into String
                StringBuilder subs = new StringBuilder();
                for (String s : subAttributes)
                {
                    subs.append("%s,".formatted(STRINGS.toEnumCase(s)));
                }
                if (subs.length() > 0)
                {
                    subs = new StringBuilder(subs.substring(0, subs.length() - 1));
                }
                //Write rune to file
                if (!singleRune)
                {
                    csvRunes += RUNES.runeToCSV(RuneType.stringToType(runeType.substring(5)).getNum(), mainAttribute.substring(5, mainAttribute.indexOf(",")), Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)), subs.toString());
                }
                
                //Dispose the current rune creator
                dispose();
                if (singleRune) //Write a single rune to the file
                {
                    try
                    {
                        //Create a temp file to parse new rune from
                        File f = new File("%s/temp.csv".formatted(MonsterRunes.path));
                        FileWriter writer = new FileWriter(f);
                        String csv = RUNES.runeToCSV(RuneType.stringToType(runeType.substring(5)).getNum(),
                                mainAttribute.substring(5, mainAttribute.indexOf(",")),
                                Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)),
                                subs.toString());
                        writer.write(csv);
                        writer.close();
                        
                        Rune newRune = RUNES.getRunesFromFile("temp.csv", new Monster()).getFirst();
                        //Delete temp file
                        f.delete();
                        //Attempt to replace rune in file
                        if (FILES.editFile(fileName, runeNum, newRune))
                        {
                            new Message("Success", false);
                        }
                        else
                        {
                            //Something went wrong
                            new Message("Error", true);
                        }
                        return;
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
                //Exit after last rune
                if (runeNum == 8)
                {
                    quitSaveButton.doClick();
                }
                else //Create another window for the next rune
                {
                    new CreateRuneFile(runeNum + 1, false, fileName);
                }
            }
            else //Set focus to amount text field to get the main attribute
            {
                amountTextField.requestFocusInWindow();
            }
        });
        
        //Save and exit
        quitSaveButton.addActionListener(_ -> {
            try
            {
                FileWriter fw = new FileWriter("%s/%s".formatted(MonsterRunes.path, fileName));
                fw.write(csvRunes);
                fw.close();
                new Message("Saved", false);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        
        //Exit without saving
        quitNoSaveButton.addActionListener(_ -> new ConfirmationWindow("Are you sure you don't want to save?", () -> System.exit(0)));
        
        attributes.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent e)
            {
                if (mainAttribute == null)
                {
                    //Automatically set attribute if possible
                    switch (runeNum)
                    {
                        case 1 ->
                        {
                            attributes.setSelectedItem("Atk");
                            amountTextField.requestFocusInWindow();
                        }
                        case 3 ->
                        {
                            attributes.setSelectedItem("Def");
                            amountTextField.requestFocusInWindow();
                        }
                        case 5 ->
                        {
                            attributes.setSelectedItem("HP");
                            amountTextField.requestFocusInWindow();
                        }
                        //Only attack, defense, and HP selectable for artifacts
                        case 7, 8 ->
                        {
                            attributes.removeAllItems();
                            attributes.addItem("Atk");
                            attributes.addItem("Def");
                            attributes.addItem("HP");
                        }
                    }
                    
                    //Only allow attack, defense, and HP to be selectable if the user is creating an artifact
                    switch (Objects.requireNonNull(runeTypes.getSelectedItem()).toString())
                    {
                        case "ElementArtifact", "TypeArtifact" ->
                        {
                            attributes.removeAllItems();
                            attributes.addItem("Atk");
                            attributes.addItem("Def");
                            attributes.addItem("HP");
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Initializes all the necessary variables and starts the GUIS.
     *
     * @param fileName   The name of the file to write to if there is one, otherwise should be {"0"}
     * @param singleRune True if the GUIS creates one rune, false otherwise
     * @param startPlace The rune place number to start with
     */
    public static void run(String fileName, boolean singleRune, int startPlace)
    {
        //No file name given
        if (fileName.equals("0"))
        {
            //Get valid file name
            fileName = FILES.getFileName();
            if ((fileName).equals("tempFile") || (fileName).equals("oldTempFile"))
            {
                new Message("Please choose a different Monster name", true, () -> System.exit(1));
            }
        }
        
        new CreateRuneFile(startPlace, singleRune, fileName);
    }
}