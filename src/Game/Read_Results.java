package Game;

import javax.swing.*;
import javax.swing.filechooser.*;
import Monsters.*;
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
     * Creates the GUI
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
            ArrayList<Team> teams = readFile(chosenFile);
            if (teams == null)
            {
                return;
            }
            Auto_Play.postRunOptions(teams);
            System.exit(0);
        }
        else if (i == JFileChooser.CANCEL_OPTION)
        {
            dispose();
            System.exit(0);
        }
        
        //General GUI stuff
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
        new Read_Results();
    }
    
    /**
     * Creates the JFileChooser for the GUI
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
    
    /**
     * Checks that the selected file is a valid file and reads the file.
     * @param chosenFile The to read from
     * @return A list of teams from the file
     */
    public static ArrayList<Team> readFile(File chosenFile)
    {
        System.out.println("Checking file...");
        if (chosenFile == null)
        {
            System.out.println("Error, please choose a file");
            return null;
        }
        if (!chosenFile.canRead())
        {
            System.err.println("Error, can not read file");
            return null;
        }
        
        System.out.println("Done\n");
        System.out.println("Reading file...");
        //Start reading the chosen file
        int lineNum = 1;
        int lastI = -1;
        String line = "";
        try
        {
            Scanner read = new Scanner(chosenFile);
            
            //Read the library on the first line
            HashMap<Integer, String> library = new HashMap<>();
            line = read.nextLine();
            for (String s : line.split(","))
            {
                lastI++;
                String name = s.split(":")[0];
                int key = Integer.parseInt(s.split(":")[1]);
                library.put(key, name);
            }
            ArrayList<Team> teams = new ArrayList<>();
            
            //Create teams
            while (read.hasNextLine())
            {
                lineNum++;
                ArrayList<Monster> teamMonsters = new ArrayList<>();
                line = read.nextLine();
                String[] list = line.split(",");
                
                if (list.length == 2)
                {
                    continue;
                }
                
                //Add monsters to team
                for (int i = 0; i < 4; i++)
                {
                    //TODO Add option to save time or memory
                    lastI = i;
                    String name = library.get(Integer.parseInt(list[i]));
                    if (name == null)
                    {
                        System.err.println("Error reading file, Monster not found on line " + lineNum);
                        int lineLength = line.substring(0, line.indexOf(list[i])).length();
                        System.err.println(line);
                        String errorMsg = "";
                        for (int j = 0; j < lineLength; j++)
                        {
                            errorMsg += " ";
                        }
                        System.err.println(errorMsg + "^");
                        System.exit(1);
                    }
                    Monster m = Monster.createNewMonFromName(name);
                    teamMonsters.add(m);
                }
                teams.add(new Team("Team", teamMonsters));
                //Set number of wins and losses
                teams.getLast().setWins(Integer.parseInt(list[4]));
                teams.getLast().setLosses(Integer.parseInt(list[5]));
            }
            
            System.out.println("Done\n");
            return teams;
        }
        catch (NumberFormatException e)
        {
            System.err.println("Error reading file, unexpected character on line " + lineNum);
            System.err.println(line);
            String[] list = line.split(",");
            String errorMsg = "";
            int lineLength = line.substring(0, line.indexOf(list[lastI])).length() + list[lastI].split(":")[0].length() + 1;
            for (int i = 0; i < lineLength; i++)
            {
                errorMsg += " ";
            }
            System.err.println(errorMsg + "^");
            return null;
        }
        catch (Exception e)
        {
            //Could not read the file for some reason
            System.out.println("Done");
            System.err.println("Error reading file. Please check that the selected file is a valid file created by Game/Auto_Play");
            return null;
        }
    }
}
