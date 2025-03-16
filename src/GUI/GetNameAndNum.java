package GUI;

import javax.swing.*;
import Monsters.*;
import java.awt.event.*;

/**
 * This class is a GUI to get the file name from the user
 *
 * @author Anthony (Tony) Youssef
 */

public class GetNameAndNum extends JFrame
{
    public JTextField monNameText;
    public JTextField runeSetNumText;
    private JLabel monNameLabel;
    private JLabel runeSetNumLabel;
    private JPanel infoFrame;
    private JButton submitButton;
    
    /**
     * Creates the GUI
     *
     * @param mainRunes The main Runes class that called this constructor
     */
    public GetNameAndNum(Runes mainRunes)
    {
        //General GUI stuff
        add(infoFrame);
        setTitle("Enter Monster name and rune set number");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Submit data
        submitButton.addActionListener(_ -> {
            //Make sure input fields have been filled
            if (!monNameText.getText().isEmpty() && !runeSetNumText.getText().isEmpty())
            {
                try
                {
                    String fileName = formatFileName(monNameText.getText(), Integer.parseInt(runeSetNumText.getText()));
                    mainRunes.startRunes(fileName);
                    
                    //Remove the current window
                    dispose();
                }
                catch (NumberFormatException ignored)
                {
                }
            }
        });
        
        //Allow the user to press the Enter button to submit the form
        runeSetNumText.addKeyListener(new KeyAdapter()
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
    
    private String formatFileName(String name, int num)
    {
        //Get the proper Monster name
        String monName = Monster.toProperName(name);
        
        //Try to set the rune number
        int runeSetNum = 0;
        try
        {
            runeSetNum = num;
        }
        catch (NumberFormatException e) //Unable to parse the input to an int
        {
            System.err.println("Unable to read the rune set number");
            System.exit(1);
        }
        
        //Return the formatted file name
        return "%s%d.csv".formatted(monName, runeSetNum);
    }
}
