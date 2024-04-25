package GUI;

import javax.swing.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Runes.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static Game.Main.pause;

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
     * @param runeNum    current rune number
     * @param singleRune True if the GUI should create one rune then stop, false otherwise
     * @param fileName   The name of the file to write to
     * @throws IndexOutOfBoundsException if runeNum is out of range
     */
    public CreateRuneFile(int runeNum, boolean singleRune, String fileName)
    {
        currentRuneNumLabel.setText("Current rune number: " + runeNum);
        if (runeNum < 1 || runeNum > 8)
        {
            throw new IndexOutOfBoundsException("runeNum must be between 1 and 8 inclusive");
        }
        addAllAttributes(attributes);
        attributes.setFocusTraversalKeysEnabled(false);
        
        runeTypes.removeItem(0);
        
        addAllTypes(runeTypes);
        
        runeTypes.setFocusTraversalKeysEnabled(false);
        
        add(panel);
        setTitle("Select main attribute");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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
        
        amountTextField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    addAttribute.doClick();
                }
            }
        });
        
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
                    if (mainAttribute == null)
                    {
                        mainAttribute = "Rune." + Objects.requireNonNull(attributes.getSelectedItem()).toString().toUpperCase() + ", " + amountTextField.getText();
                        mainAttributeLabel.setText("Add sub attributes, press \"done\" when done.");
                        setTitle("Select sub attributes");
                    }
                    else
                    {
                        subAttributes.add("Rune." + Objects.requireNonNull(attributes.getSelectedItem()).toString().toUpperCase() + ", " + amountTextField.getText());
                    }
                    attributes.removeItem(attributes.getSelectedItem());
                    runeType = "Rune." + Objects.requireNonNull(runeTypes.getSelectedItem()).toString().toUpperCase();
                    amountTextField.setText("");
                    attributes.requestFocusInWindow();
                }
                else
                {
                    amountTextField.setText("");
                    amountTextField.requestFocusInWindow();
                }
            }
            else
            {
                amountTextField.setText("");
                amountTextField.requestFocusInWindow();
            }
        });
        submit.addActionListener(_ -> {
            if (mainAttribute != null)
            {
                String subs = "";
                for (String s : subAttributes)
                {
                    subs += s + ", ";
                }
                if (!subs.isEmpty())
                {
                    subs = subs.substring(0, subs.length() - 2);
                }
                if (!singleRune)
                {
                    writeRuneToFile(fw, Rune.stringToNum(runeType.substring(5)), mainAttribute.substring(5, mainAttribute.indexOf(",")),
                            Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)), subs);
                }
                dispose();
                if (singleRune)
                {
                    try
                    {
                        File f = new File("src/Runes/Monster_Runes/temp.csv");
                        FileWriter writer = new FileWriter(f);
                        writeRuneToFile(writer, Rune.stringToNum(runeType.substring(5)), mainAttribute.substring(5, mainAttribute.indexOf(",")),
                                Integer.parseInt(mainAttribute.substring(mainAttribute.indexOf(", ") + 2)), subs);
                        writer.close();
                        
                        Rune newRune = MonsterRunes.getRunesFromFile("temp.csv", new Monster()).getFirst();
                        f.delete();
                        if (EditRuneFile.editFile(fileName, runeNum, newRune))
                        {
                            new Message("Success", false);
                        }
                        else
                        {
                            new Message("Error", true);
                        }
                        return;
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
                if (runeNum == 8)
                {
                    System.exit(0);
                }
                else
                {
                    new CreateRuneFile(runeNum + 1, false, fileName);
                }
            }
            else
            {
                amountTextField.requestFocusInWindow();
            }
        });
        quitButton.addActionListener(_ -> System.exit(0));
        
        attributes.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent e)
            {
                if (mainAttribute == null)
                {
                    switch ("" + runeNum)
                    {
                        case "1" ->
                        {
                            attributes.setSelectedItem("Atk");
                            amountTextField.requestFocusInWindow();
                        }
                        case "3" ->
                        {
                            attributes.setSelectedItem("Def");
                            amountTextField.requestFocusInWindow();
                        }
                        case "5" ->
                        {
                            attributes.setSelectedItem("HP");
                            amountTextField.requestFocusInWindow();
                        }
                        case "7", "8" ->
                        {
                            attributes.removeAllItems();
                            attributes.addItem("Atk");
                            attributes.addItem("Def");
                            attributes.addItem("HP");
                        }
                    }
                    
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
        int mainNum = Rune.stringToNum(mainName);
        
        if (mainNum == -1 || type == -1)
        {
            throw new RuntimeException("Can not find Main Attribute or type, " + String.format("Main Attribute: %s, type: %s", mainName,
                    Rune.numToType(type)));
        }
        
        try
        {
            ArrayList<String> subAttributes = new ArrayList<>(Arrays.asList(subs.split(", ")));
            
            ArrayList<Integer> subNums = new ArrayList<>();
            int count = 0;
            if (subAttributes.size() != 1)
            {
                for (String sub : subAttributes)
                {
                    if (count % 2 == 0)
                    {
                        int temp = Rune.stringToNum(sub.substring(5));
                        if (temp == -1)
                        {
                            throw new RuntimeException("Can not find Sub Attribute: " + sub);
                        }
                        subNums.add(temp);
                    }
                    else
                    {
                        subNums.add(Integer.parseInt(sub));
                    }
                    count++;
                }
            }
            
            fw.write(String.format("%d,%d,%d", type, mainNum, mainAmount));
            for (Integer num : subNums)
            {
                fw.write("," + num);
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
        int type = runeToWrite.getType();
        int mainNum = runeToWrite.getMainAttribute().getNum();
        int mainAmount = runeToWrite.getMainAttribute().getAmount();
        
        try
        {
            fw.write(String.format("%d,%d,%d", type, mainNum, mainAmount));
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
     * Instantiates all needed variables and starts the GUI.
     *
     * @param args       Contains the name of the file to write to if there is one, otherwise should be {"0"}
     * @param singleRune True if the GUI will create one rune, false otherwise
     * @param startPlace The rune place number to start with
     */
    public static void run(String[] args, boolean singleRune, int startPlace)
    {
        String fileName;
        if (args.length > 0 && args[0].equals("0"))
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
            fileName = monName + runeSetNum;
            if ((fileName).equals("tempFile") || (fileName).equals("oldTempFile"))
            {
                new Message("Please choose a different Monster name", true);
                System.err.println("Please choose a different Monster name");
                System.exit(1);
            }
            try
            {
                fw = new FileWriter("src/Runes/Monster_Runes/" + fileName + ".csv");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try
            {
                fileName = args[0];
                if (!singleRune)
                {
                    fw = new FileWriter("src/Runes/Monster_Runes/" + args[0]);
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        
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
        Scanner read = new Scanner(Objects.requireNonNull(Rune.class.getResourceAsStream("Rune key.csv")));
        while (read.hasNextLine())
        {
            String[] line = read.nextLine().split(",");
            selector.addItem(line[1]);
        }
        
        selector.addItem("ElementArtifact");
        selector.addItem("TypeArtifact");
    }
    
    /**
     * Tests if the given String is an int
     *
     * @param s the string to test
     * @return true if s is an int
     */
    public static boolean stringIsInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}
