package Game;

import javax.swing.*;
import javax.swing.filechooser.*;
import Monsters.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

/**
 * This class allows the user to view and run search commands on previous runs from {@link Auto_Play}
 */
public class Read_Results extends  JFrame
{
    private JPanel panel;
    private JFileChooser fileChooser;
    
    File chosenFile = null;
    
    /**
     * Creates the GUI
     */
    public Read_Results()
    {
        int i = fileChooser.showOpenDialog(null);
        if (i == JFileChooser.APPROVE_OPTION)
        {
            chosenFile = fileChooser.getSelectedFile();
            dispose();
            readFile();
        }
        else if (i == JFileChooser.CANCEL_OPTION)
        {
            dispose();
            System.exit(0);
        }
        
        add(panel);
        setTitle("Choose file");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
   public static void main(String[] args)
    {
        new Read_Results();
    }
    
    /**
     * Creates the JFileChooser for the GUI
     */
    private void createUIComponents()
    {
        fileChooser = new JFileChooser(new File("src/Game/Results"));
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Only .csv files", "csv");
        fileChooser.addChoosableFileFilter(filter);
    }
    
    /**
     * Checks that the selected file is a valid file and reads the file.
     */
    private void readFile()
    {
        System.out.println("Checking file...");
        if (!chosenFile.canRead())
        {
            System.err.println("Error, can not read file");
            System.exit(1);
        }
        
        //Get expected path of "Results" directory
        try
        {
            ClassLoader loader = Read_Results.class.getClassLoader();
            String classPath = loader.getResource("Game/Read_Results.class").getPath().replaceAll("%20", " ");
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(classPath.split("/")));
            temp.removeLast();
            String expectedPathName = String.join("\\", temp).replaceAll("\\\\out\\\\production\\\\Summoners War Battle Simulator", "\\\\src").substring(1) + "\\Results\\";
            
            if (!chosenFile.getAbsolutePath().contains(expectedPathName))
            {
                System.out.println("Done\n");
                System.err.println("Error, please choose a file within the result directory");
                throw new RuntimeException();
            }
        }
        catch (Exception e)
        {
            System.exit(1);
        }
        System.out.println("Done\n");
        System.out.println("Reading file...");
        try
        {
            Scanner read = new Scanner(chosenFile);
            HashMap<Integer, String> library = new HashMap<>();
            for (String s : read.nextLine().split(","))
            {
                String name = s.split(":")[0];
                int key = Integer.parseInt(s.split(":")[1]);
                library.put(key, name);
            }
            ArrayList<Team> teams = new ArrayList<>();
            while (read.hasNextLine())
            {
                ArrayList<Monster> teamMonsters = new ArrayList<>();
                String line = read.nextLine();
                String[] list = line.split(",");
                for (int i = 0; i < 4; i++)
                {
                    teamMonsters.add(Monster.createNewMonFromName(library.get(Integer.parseInt(list[i]))));
                }
                teams.add(new Team("Team", teamMonsters));
                teams.getLast().setWins(Integer.parseInt(list[4]));
                teams.getLast().setLosses(Integer.parseInt(list[5]));
            }
            
            System.out.println("Done\n");
            Auto_Play.postRunOptions(teams);
        }
        catch (Exception e)
        {
            System.out.println("Done");
            System.err.println("Error reading file. Please check that the selected file is a valid file created by Game/Auto_Play");
            System.exit(1);
        }
    }
}
