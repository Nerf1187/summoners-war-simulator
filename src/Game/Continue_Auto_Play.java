package Game;

import Monsters.*;
import Runes.*;
import java.io.*;
import java.util.*;

/**
 * This class allows the user to continue the Auto Play program from a previous run. Only saves from v1.1.4 and after can be used.
 * @author Anthony (Tony) Youssef
 */
public class Continue_Auto_Play
{
    /**
     * Runs the program
     */
    public void main()
    {
        //Get the file from the user and read it
        File file = Rune_Parser.getFileFromUser("Select file to continue", "*.csv");
        ArrayList<Team> teams = Read_Results.readFile(file);
        if (teams == null)
        {
            System.exit(1);
        }
        
        //Initialize error message variables
        int lastLineIndex = 0;
        String lastLine = "";
        int lastColumn = 1;
        
        try
        {
            lastLineIndex++;
            int i, j;
            long totalTime = -1, battleTime = -1;
            Scanner read = new Scanner(file);
            
            //Read the library
            lastLine = read.nextLine();
            String[] nameLibrary = lastLine.split(",");
            ArrayList<String> monLibrary = new ArrayList<>();
            for (String name : nameLibrary)
            {
                //Ensure the Monster exists
                Monster m = Monster.createNewMonFromName(name.split(":")[0], false);
                if (m == null)
                {
                    throw new NumberFormatException();
                }
                
                //Add the Monster to the library
                monLibrary.add(m.getName(false, false));
                lastColumn += name.length() + 1;
            }
            
            lastLineIndex++;
            lastColumn = 1;
            lastLine = read.nextLine();
            
            //Read the progress from the previous run
            String[] line = lastLine.split(",");
            if (line.length != 2)
            {
                System.out.println("Can not continue simulations from this file.");
                System.exit(1);
            }
            i = Integer.parseInt(line[0]);
            lastColumn += line[0].length() + 1 ;
            j = Integer.parseInt(line[1]);
            lastColumn += line[1].length();
            
            lastLineIndex++;
            lastColumn = 1;
            
            //Read the time from the previous run if available
            lastLine = read.nextLine();
            String[] times = lastLine.split(",");
            if (times.length == 2)
            {
                totalTime = Long.parseLong(times[0]);
                lastColumn += times[0].length() + 1;
                
                battleTime = Long.parseLong(times[1]);
                lastColumn += times[1].length();
            }
            
            //Continue the simulations
            new Auto_Play().main(teams, monLibrary, totalTime, battleTime, i, j);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error, could not find file.");
            System.exit(1);
        }
        catch (NumberFormatException e)
        {
            //Generate and display a relevant error message
            System.err.printf("Error line %d: %s\n", lastLineIndex, lastLine);
            lastColumn += "Error line %d: ".formatted(lastLineIndex).length();
            String temp = "";
            for (int i = 0; i < lastColumn - 1; i++)
            {
                temp += " ";
            }
            
            System.err.println(temp + "^");
        }
        catch (Exception e)
        {
            System.err.printf("Unknown Error line %d: %s\n", lastLineIndex, lastLine);
        }
    }
}