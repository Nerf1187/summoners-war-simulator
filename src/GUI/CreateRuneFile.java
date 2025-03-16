package GUI;

import javax.swing.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Runes.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Creates a GUI to create the code for rune classes
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
    private JButton quitButton;
    private String mainAttribute, runeType;
    private final ArrayList<String> subAttributes = new ArrayList<>();
    private static FileWriter fw = null;
    
    /**
     * Creates the GUI and implements the keyboard shortcuts
     *
     * @param runeNum    Current rune number
     * @param singleRune True if the GUI should create one rune then stop, false otherwise
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
        addAllAttributes(attributes);
        //Prevent keyboard inputs
        attributes.setFocusTraversalKeysEnabled(false);
        
        //Remove blank option
        runeTypes.removeItem(0);
        
        //Add al rune set types
        addAllTypes(runeTypes);
        //Prevent keyboard inputs
        runeTypes.setFocusTraversalKeysEnabled(false);
        
        //General GUI stuff
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
                runeTypes.setSelectedItem("ElementArtifact");
                attributes.requestFocusInWindow();
            }
            case 8 ->
            {
                runeTypes.setSelectedItem("TypeArtifact");
                attributes.requestFocusInWindow();
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
                if (stringIsInt(amountTextField.getText()))
                {
                    //Add selected attribute and amount as a main attribute if there is none yet
                    if (mainAttribute == null)
                    {
                        mainAttribute = "Rune.%s, %s".formatted(Objects.requireNonNull(attributes.getSelectedItem()).toString().toUpperCase(), amountTextField.getText());
                        mainAttributeLabel.setText("Add sub attributes, press \"done\" when done.");
                        setTitle("Select sub attributes");
                    }
                    else //Add the selected attribute and amount as a sub attribute
                    {
                        subAttributes.add("Rune.%s, %s".formatted(Objects.requireNonNull(attributes.getSelectedItem()).toString().toUpperCase(), amountTextField.getText()));
                    }
                    //Remove choice
                    attributes.removeItem(attributes.getSelectedItem());
                    runeType = "Rune.%s".formatted(Objects.requireNonNull(runeTypes.getSelectedItem()).toString().toUpperCase());
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
                String subs = "";
                for (String s : subAttributes)
                {
                    subs += "%s, ".formatted(s);
                }
                if (!subs.isEmpty())
                {
                    subs = subs.substring(0, subs.length() - 2);
                }
                //Write rune to file
                if (!singleRune)
                {
                    writeRuneToFile(fw, Rune.stringToNum(runeType.substring(5)), mainAttribute.substring(5, mainAttribute.indexOf(",")),
                            Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)), subs);
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
                        writeRuneToFile(writer, Rune.stringToNum(runeType.substring(5)), mainAttribute.substring(5, mainAttribute.indexOf(",")),
                                Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)), subs);
                        writer.close();
                        
                        Rune newRune = MonsterRunes.getRunesFromFile("temp.csv", new Monster()).getFirst();
                        //Delete temp file
                        f.delete();
                        //Attempt to replace rune in file
                        if (EditRuneFile.editFile(fileName, runeNum, newRune))
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
                    System.exit(0);
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
        //Stop creating runes
        quitButton.addActionListener(_ -> System.exit(0));
        
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
     * Writes a rune to a csv file
     *
     * @param fw         The FileWriter to use
     * @param type       The type of the rune
     * @param mainName   The name of the rune's main attribute
     * @param mainAmount The amount of the rune's main attribute
     * @param subs       The rune's sub attributes. Format: "name, amount, name, amount..."
     */
    public static void writeRuneToFile(FileWriter fw, int type, String mainName, int mainAmount, String subs)
    {
        //Get the main attribute number
        int mainNum = Rune.stringToNum(mainName);
        
        //Unknown attribute or type
        if (mainNum == -1 || type == -1)
        {
            throw new RuntimeException("Can not find Main Attribute or type, %s".formatted(String.format("Main Attribute: %s, type: %s", mainName, Rune.numToType(type))));
        }
        
        try
        {
            //Format sub attributes
            ArrayList<String> subAttributes = new ArrayList<>(Arrays.asList(subs.split(", ")));
            
            ArrayList<Integer> subNums = new ArrayList<>();
            int count = 0;
            if (subAttributes.size() != 1)
            {
                for (String sub : subAttributes)
                {
                    //Attempt to add attribute number
                    if (count % 2 == 0)
                    {
                        int temp = Rune.stringToNum(sub.substring(5));
                        if (temp == -1)
                        {
                            throw new RuntimeException("Can not find Sub Attribute: %s".formatted(sub));
                        }
                        subNums.add(temp);
                    }
                    else //Add attribute amount
                    {
                        subNums.add(Integer.parseInt(sub));
                    }
                    count++;
                }
            }
            
            //Write the rune type and main attribute to file
            fw.write(String.format("%d,%d,%d", type, mainNum, mainAmount));
            //Write sub attributes to file
            for (Integer num : subNums)
            {
                fw.write(",%d".formatted(num));
            }
            fw.write("\n");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Writes a rune to a file
     *
     * @param fw          The FileWriter to use
     * @param runeToWrite The rune to write to the file
     */
    public static void writeRuneToFile(FileWriter fw, Rune runeToWrite)
    {
        //Get the rune type and main attribute
        int type = runeToWrite.getType();
        int mainNum = runeToWrite.getMainAttribute().getNum();
        int mainAmount = runeToWrite.getMainAttribute().getAmount();
        try
        {
            //Write the rune type and main attribute
            fw.write(String.format("%d,%d,%d", type, mainNum, mainAmount));
            //Write each sub attribute
            for (SubAttribute subAttribute : runeToWrite.getSubAttributes())
            {
                fw.write(String.format(",%d,%d", subAttribute.getNum(), subAttribute.getAmount()));
            }
            fw.write("\n");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Initializes all the necessary variables and starts the GUI.
     *
     * @param fileName   The name of the file to write to if there is one, otherwise should be {"0"}
     * @param singleRune True if the GUI creates one rune, false otherwise
     * @param startPlace The rune place number to start with
     */
    public static void run(String fileName, boolean singleRune, int startPlace)
    {
        //No file name given
        if (fileName.equals("0"))
        {
            //Get valid file name
            fileName = Runes.getFileName();
            if ((fileName).equals("tempFile") || (fileName).equals("oldTempFile"))
            {
                new Message("Please choose a different Monster name", true, () -> System.exit(1));
            }
            try
            {
                //Create a new FileWriter for the file name
                fw = new FileWriter("%s/%s.csv".formatted(MonsterRunes.path, fileName));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else //File name provided
        {
            try
            {
                if (!singleRune)
                {
                    //Create a FileWriter for the provided file name if the program is creating more than one rune
                    fw = new FileWriter("%s/%s".formatted(MonsterRunes.path, fileName));
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        
        //Close the FileWriter if the program unnaturally stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                fw.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }));
        new CreateRuneFile(startPlace, singleRune, fileName);
    }
    
    /**
     * Adds all the rune attributes to the given JComboBox
     *
     * @param selector The JComboBox to add the attributes to
     */
    public static void addAllAttributes(JComboBox<String> selector)
    {
        //Remove all previous items then add all the attributes
        selector.removeAllItems();
        selector.addItem("Atk");
        selector.addItem("AtkPercent");
        selector.addItem("Def");
        selector.addItem("DefPercent");
        selector.addItem("HP");
        selector.addItem("HPPercent");
        selector.addItem("Spd");
        selector.addItem("CritRate");
        selector.addItem("CritDmg");
        selector.addItem("Res");
        selector.addItem("Acc");
    }
    
    /**
     * Adds all the rune types to the given JComboBox
     *
     * @param selector The JComboBox to add the types to
     */
    public static void addAllTypes(JComboBox<String> selector)
    {
        //Read from the rune key file
        Scanner read = new Scanner(Objects.requireNonNull(Rune.class.getResourceAsStream("Rune key.csv")));
        //Add each rune type
        while (read.hasNextLine())
        {
            String[] line = read.nextLine().split(",");
            selector.addItem(line[1]);
        }
        
        //Add artifact types
        selector.addItem("ElementArtifact");
        selector.addItem("TypeArtifact");
    }
    
    /**
     * Tests if the given String is an int
     *
     * @param s The string to test
     * @return True if s is an int
     */
    public static boolean stringIsInt(String s)
    {
        try
        {
            //Parse the string for an int and return true if no errors
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            //Return false if String cannot be parsed
            return false;
        }
    }
}