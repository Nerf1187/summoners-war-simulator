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
        setTitle(message);
        if (isError)
        {
            setTitle("Error");
            mainLabel.setForeground(Color.RED);
        }
        else
        {
            mainLabel.setForeground(Color.BLACK);
        }
        mainLabel.setText(message);
        add(panel);
        setSize(250, 150);
        setLocationRelativeTo(null);
        setVisible(true);
        
        okButton.addActionListener(e -> dispose());
    }
}
