package Game;

import javax.swing.*;
import javax.swing.filechooser.*;
import Util.Util.*;
import java.io.*;
import java.util.*;

/**
 * This class allows the user to view and run search commands on previous runs from {@link Auto_Play}
 */
public class Read_Results extends JFrame
{
    private JPanel panel;
    private JFileChooser fileChooser;
    
    File chosenFile = null;
    
    /**
     * Creates the GUIS
     */
    public Read_Results()
    {
        //Get the results file to read from
        int i = fileChooser.showOpenDialog(null);
        if (i == JFileChooser.APPROVE_OPTION)
        {
            chosenFile = fileChooser.getSelectedFile();
            dispose();
            //Read file and view results
            ArrayList<Team> teams = FILES.readFile(chosenFile, true);
            if (teams == null)
            {
                return;
            }
            CONSOLE_INTERFACE.pauseMenu(teams);
            System.exit(0);
        }
        else if (i == JFileChooser.CANCEL_OPTION)
        {
            dispose();
            System.exit(0);
        }
        
        //General GUIS stuff
        add(panel);
        setTitle("Choose file");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Runs the class.
     */
    public void main()
    {
    }
    
    /**
     * Creates the JFileChooser for the GUIS
     */
    private void createUIComponents()
    {
        fileChooser = new JFileChooser(new File("src/Game/Results"));
        //Allow only 1 .csv file to be chosen
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Only .csv files", "csv");
        fileChooser.addChoosableFileFilter(filter);
    }
}