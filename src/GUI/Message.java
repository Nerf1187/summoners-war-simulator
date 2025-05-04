package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class displays a GUIS message to the user
 */
public class Message extends JFrame
{
    private JPanel panel;
    private JLabel mainLabel;
    private JButton okButton;
    
    /**
     * Creates the GUIS
     *
     * @param message The message to display
     * @param isError True if the message represents an error, false otherwise
     */
    public Message(String message, boolean isError)
    {
        this(message, isError, () -> {});
    }
    
    /**
     * Creates the GUIS. This window will do nothing when closed
     *
     * @param message The message to display
     * @param isError True if the message represents an error false otherwise
     * @param applyOnClose The function to apply when closing the GUIS through the close button
     */
    public Message(String message, boolean isError, Function applyOnClose)
    {
        //Set the GUIS title
        setTitle((isError) ? "Error" : "Info");
        
        //Check if the message is an error and change the font color accordingly
        mainLabel.setForeground(isError ? Color.RED : Color.BLACK);
        
        //General GUIS stuff
        mainLabel.setText(message);
        add(panel);
        setVisible(true);
        setSize((int) Math.max(Math.ceil(mainLabel.getWidth() * 1.2), 250), 150);
        setLocationRelativeTo(null);
        
        //Remove the current window and apply the function
        okButton.addActionListener(_ -> {
            dispose();
            applyOnClose.apply();
        });
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                okButton.doClick();
            }
        });
    }
}
