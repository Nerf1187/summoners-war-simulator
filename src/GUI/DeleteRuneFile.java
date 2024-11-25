package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * This class is used to confirm the deletion of a file
 *
 * @author Anthony (Tony) Youssef
 */
public class DeleteRuneFile extends JFrame
{
    private JPanel panel;
    private JLabel mainLabel;
    private JButton yesButton;
    private JButton noButton;
    
    /**
     * Creates the GUI
     *
     * @param fileName The name of the file to be deleted
     */
    public DeleteRuneFile(String fileName)
    {
        //General GUI stuff
        add(panel);
        setTitle("Confirm");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        //Confirm action
        yesButton.addActionListener(_ -> {
            //Try to delete the file and show a message displaying the result
            if (new File("src/Runes/Monster_Runes/%s".formatted(fileName)).delete())
            {
                new Message("Success", false);
                dispose();
            }
            else
            {
                new Message("Error", true);
                dispose();
            }
        });
        
        //Cancel action
        noButton.addActionListener(_ -> {
            //Show message that the action was canceled
            new Message("Aborted", false);
            dispose();
        });
        
        //Add key listeners
        panel.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyChar())
                {
                    //Click confirm
                    case 'y' -> yesButton.doClick();
                    //Click cancel
                    case 'n' -> noButton.doClick();
                }
            }
        });
    }
    
    /**
     * Runs the GUI
     *
     * @param fileName The name of the file to be deleted
     */
    public static void run(String fileName)
    {
        new DeleteRuneFile(fileName);
    }
}
