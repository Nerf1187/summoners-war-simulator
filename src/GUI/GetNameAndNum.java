package GUI;

import javax.swing.*;
import java.awt.event.*;

/**
 * This class is a GUI to get the file name from the user
 *
 * @author Anthony (Tony) Youssef
 */

public class GetNameAndNum extends JFrame
{
    /**
     * Textbox for Monster name
     */
    public JTextField monNameTxt;
    /**
     * Textbox for rune set number
     */
    public JTextField runeSetNumText;
    private JLabel monNameLabel;
    private JLabel runeSetNumLabel;
    private JPanel infoFrame;
    private JButton submitButton;
    
    /**
     * Creates the GUI
     */
    public GetNameAndNum()
    {
        add(infoFrame);
        setTitle("Enter Monster name and rune set number");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setVisible(true);
        
        submitButton.addActionListener(e -> {
            if (!monNameTxt.getText().isEmpty() && !runeSetNumText.getText().isEmpty())
            {
                try
                {
                    Integer.parseInt(runeSetNumText.getText());
                    dispose();
                }
                catch (Exception ignored)
                {
                }
            }
        });
        
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
