package GUI;

import javax.swing.*;
import Util.Util.*;
import java.awt.event.*;

/**
 * This class is a GUIS to get the file name from the user
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
     * Creates the GUIS
     *
     * @param mainRunes The main Runes class that called this constructor
     */
    public GetNameAndNum(Runes mainRunes)
    {
        //General GUIS stuff
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
                    String fileName = FILES.formatFileName(monNameText.getText(), Integer.parseInt(runeSetNumText.getText()));
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
}
