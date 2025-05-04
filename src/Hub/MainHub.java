package Hub;

import javax.swing.*;
import Developer_Website.*;
import GUI.*;
import Game.*;
import Runes.*;

/**
 * This class acts as a main hub for every other runnable class. From here, users can access and run any runnable class they want by clicking a button
 *
 * @author Anthony (Tony) Youssef
 */
public class MainHub extends JFrame
{
    private JPanel panel;
    private JLabel label;
    private JButton mainButton;
    private JButton autoPlayButton;
    private JButton testOneTeamButton;
    private JButton runesButton;
    private JButton readResultsButton;
    private JButton runeParserButton;
    private JButton continueAutoPlayButton;
    private JButton websiteButton;
    
    /**
     * Initializes the GUIS and links each button to its respective class
     */
    public MainHub()
    {
        mainButton.setFocusable(false);
        mainButton.addActionListener(_ -> {
            dispose();
            new Message("Please continue in the terminal", false, () -> new Main().main());
        });
        
        autoPlayButton.setFocusable(false);
        autoPlayButton.addActionListener(_ -> {
            dispose();
            new Message("Please continue in the terminal", false, () -> new Auto_Play().main());
        });
        
        testOneTeamButton.setFocusable(false);
        testOneTeamButton.addActionListener(_ -> {
            dispose();
            new Message("Please continue in the terminal", false, () -> new Test_One_Team().main());
        });
        
        runesButton.setFocusable(false);
        runesButton.addActionListener(_ -> {
            dispose();
            new Runes().main();
        });
        
        readResultsButton.setFocusable(false);
        readResultsButton.addActionListener(_ -> {
            dispose();
            new Read_Results().main();
        });
        
        continueAutoPlayButton.setFocusable(false);
        continueAutoPlayButton.addActionListener(_ -> {
            dispose();
            new Continue_Auto_Play().main();
        });
        
        websiteButton.setFocusable(false);
        websiteButton.addActionListener(_ -> {
            dispose();
            new WebsiteRunner().main();
        });
        
        runeParserButton.setFocusable(false);
        runeParserButton.addActionListener(_ -> {
            dispose();
            new Rune_Parser().main();
        });
        
        add(panel);
        setTitle("Choose program");
        setSize(600, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Designates this class as runnable in Java 23 Preview. This method does not do anything else.
     */
    public void main()
    {
    }
}