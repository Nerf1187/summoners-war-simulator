package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * This class displays a GUI message to the user
 */
public class Message extends JFrame
{
    private JPanel panel;
    private JLabel mainLabel;
    private JButton okButton;
    
    /**
     * Creates the GUI
     *
     * @param message The message to display
     * @param isError True if the message represents an error, false otherwise
     */
    public Message(String message, boolean isError)
    {
        //Set the GUI title
        setTitle(message);
        
        //Check if the message is an error
        if (isError)
        {
            setTitle("Error");
            mainLabel.setForeground(Color.RED);
        }
        else //General message
        {
            mainLabel.setForeground(Color.BLACK);
        }
        
        //General GUI stuff
        mainLabel.setText(message);
        add(panel);
        setSize(250, 150);
        setLocationRelativeTo(null);
        setVisible(true);
        
        //Remove the current window
        okButton.addActionListener(_ -> dispose());
    }
}
