package GUI;

import javax.swing.*;
import java.awt.event.*;

public class ConfirmationWindow extends JFrame
{
    private JPanel panel;
    private JButton yesButton;
    private JButton noButton;
    private JLabel mainLabel;
    
    public ConfirmationWindow(String msg, Function onYes, Function onNo)
    {
        mainLabel.setText(msg);
        add(panel);
        setVisible(true);
        setSize((int) Math.max(Math.ceil(mainLabel.getWidth() * 1.2), 300), 150);
        setLocationRelativeTo(null);
        
        yesButton.addActionListener(_ -> {
            onYes.apply();
            dispose();
        });
        noButton.addActionListener(_ -> {
            onNo.apply();
            dispose();
        });
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                new Message("Please choose an option", true);
            }
        });
        
        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == 'y')
                {
                    yesButton.doClick();
                }
                else if (e.getKeyChar() == 'n')
                {
                    noButton.doClick();
                }
            }
        });
    }
    
    public ConfirmationWindow(String msg, Function onYes)
    {
        this(msg, onYes, () -> {});
    }
}
