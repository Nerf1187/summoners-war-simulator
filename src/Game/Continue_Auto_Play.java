package Game;

import Monsters.*;
import Runes.*;
import Util.Util.*;
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
        if (file == null)
        {
            System.out.println("No file selected, exiting program.");
            return;
        }
        
        //Ask if the user wants to save time or memory
        String response = CONSOLE_INTERFACE.INPUT.getSpecificString("Do you want to prioritize speed or memory? (\"s\" for speed, \"m\" for memory, \"info\" to learn more)",
                "s", "m", "info");
        
        while (response.equalsIgnoreCase("info"))
        {
            System.out.println("Choosing speed is much faster but takes up more RAM and takes longer to create the teams.");
            System.out.println("Choosing memory takes up less RAM and creates the teams faster but takes much longer to finish.");
            response = CONSOLE_INTERFACE.INPUT.getSpecificString("Do you want to prioritize speed or memory? (\"s\" for speed, \"m\" for memory, \"info\" to learn more)",
                    "s", "m", "info");
        }
        boolean prioritizeSpd = response.equalsIgnoreCase("s");
        ArrayList<Team> teams = FILES.readFile(file, prioritizeSpd);
        if (teams == null)
        {
            System.exit(1);
        }
        
        //Initialize error message variables
        int lastLineIndex = 0;
        String lastLine = "";
        int lastColumn = 1;
        
        //This section figures out where to start running and tries to find how much time passed in the previous program run
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
                Monster m = MONSTERS.createNewMonFromName(name.split(":")[0], false);
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
                System.err.println("Can not continue simulations from this file.");
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
            new Auto_Play().main(teams, monLibrary, totalTime, battleTime, i, j, prioritizeSpd);
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
            
            System.err.println(" ".repeat(Math.max(0, lastColumn - 1)) + "^");
        }
        catch (Exception e)
        {
            System.err.printf("Unknown Error line %d: %s\n", lastLineIndex, lastLine);
        }
    }
}