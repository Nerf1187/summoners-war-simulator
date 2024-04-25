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
        add(panel);
        setTitle("Confirm");
        setSize(450, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        yesButton.addActionListener(_ -> {
            if (new File("src/Runes/Monster_Runes/" + fileName).delete())
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
        noButton.addActionListener(_ -> {
            new Message("Aborted", false);
            dispose();
        });
        
        panel.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyChar())
                {
                    case 'y':
                    {
                        yesButton.doClick();
                        break;
                    }
                    case 'n':
                    {
                        noButton.doClick();
                        break;
                    }
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
