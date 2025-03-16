package Game;

import javax.swing.*;
import javax.swing.filechooser.*;
import Errors.*;
import Monsters.*;
import java.io.*;
import java.nio.file.*;
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
            Auto_Play.pauseMenu(teams);
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
     *
     * @param chosenFile The to read from
     * @return A list of teams from the file
     */
    public static ArrayList<Team> readFile(File chosenFile)
    {
        //Files.lines(chosenFile.toPath()).count();
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
        
        //Get number of lines in the file
        long numLines = -1;
        try
        {
            numLines = Files.lines(chosenFile.toPath()).count();
        }
        catch (Exception ignored)
        {
        }
        
        System.out.println("Done\n");
        System.out.println("Reading file...");
        System.out.print("Progress: 0%\r");
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
                Monster m = Monster.createNewMonFromName(name, false);
                if (m == null)
                {
                    throw new InvalidClassException("Monster " + name + " not found");
                }
                library.put(key, name);
            }
            ArrayList<Team> teams = new ArrayList<>();
            
            lastI = 0;
            //Create teams
            while (read.hasNextLine())
            {
                lineNum++;
                System.out.printf("Progress: %.1f%%\r", (double) lineNum / numLines * 100);
                ArrayList<Monster> teamMonsters = new ArrayList<>();
                line = read.nextLine();
                String[] list = line.split(",");
                
                if (list.length != 6)
                {
                    if (lineNum <= 3 && list.length == 2)
                    {
                        continue;
                    }
                    else
                    {
                        String expectedLength;
                        if (lineNum > 3)
                        {
                            expectedLength = "6";
                        }
                        else
                        {
                            expectedLength = "2 or 6";
                        }
                        throw new InvalidArgumentLength(expectedLength);
                    }
                }
                
                //Add monsters to team
                for (int i = 0; i < 4; i++)
                {
                    lastI = i;
                    String name = library.get(Integer.parseInt(list[i]));
                    if (name == null)
                    {
                        throw new InvalidClassException("Unknown monster");
                    }
                    Monster m = Monster.createNewMonFromName(name, false);
                    teamMonsters.add(m);
                }
                teams.add(new Team("Team", teamMonsters));
                
                //Set number of wins and losses
                teams.getLast().setWins(Integer.parseInt(list[4]));
                teams.getLast().setLosses(Integer.parseInt(list[5]));
            }
            
            System.out.println("\nDone\n");
            return teams;
        }
        catch (Exception e)
        {
            displayErrorMessage(e, line, lineNum, lastI);
            return null;
        }
    }
    
    private static void displayErrorMessage(Exception e, String line, int lineNum, int lastI)
    {
        String[] list = line.split(",");
        String msg = switch (e)
        {
            case InvalidArgumentLength _ -> "Invalid line length";
            case NumberFormatException _ -> "Unexpected character";
            case InvalidClassException _ -> "Monster not found";
            default -> "Unexpected error";
        };
        
        //Could not read the file for some reason
        System.err.printf("Error reading file. %s on line %d. ", msg, lineNum);
        if (msg.contains("line length"))
        {
            System.err.printf("(Expected %s, got %d)\n", e.getMessage(), (!list[0].isEmpty()) ? list.length : 0);
        }
        System.err.printf("%s\n", line);
        String errorMsg = "";
        int lineLength = line.substring(0, line.indexOf(list[lastI])).length();
        if (msg.contains("line length"))
        {
            lineLength = line.length() - list[list.length - 1].length();
        }
        for (int i = 0; i < lineLength; i++)
        {
            errorMsg += " ";
        }
        System.err.println(errorMsg + "^");
    }
}