package Runes;

import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import lib.json.*;
import lib.json.parser.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static Game.Main.scan;

/**
 * This class parses a JSON file containing a user's account info (provided by <a href='https://tool.swop.one'>SWOP</a>) for rune information and either replaces existing files or creates new ones
 *
 * @author Anthony (Tony) Youssef
 */
public class Rune_Parser
{
    /**
     * Object for all Monster IDs
     */
    private JSONObject monsterIds;
    
    /**
     * Runs the Rune Parser class
     */
    public void main()
    {
        //Keeps track of Monster IDs that have already been parsed in case of duplicate Monsters
        final HashMap<Long, Integer> doneIds = new HashMap<>();
        
        File file = getFileFromUser("Select account file to open", "*.json");
        
        if (file == null)
        {
            System.err.println("No file selected.");
            System.exit(1);
        }
        
        //Ask the user if they want to replace all files if they have a replacement or add new files
        String response;
        do
        {
            System.out.println("Do you want to replace all rune files or create new ones? (\"r\" to replace, \"n\" for new files)");
            response = scan.nextLine();
        }
        while (!response.equals("r") && !response.equals("n"));
        boolean replace = response.equals("r");
        
        JSONParser parser = new JSONParser();
        try
        {
            //Get the file containing all Monster IDs and names and set to a JSONpObject
            FileReader keyReader = new FileReader("%s/name_id_keys.json".formatted(Monster.path));
            Object JsonObj2 = parser.parse(keyReader);
            monsterIds = (JSONObject) JsonObj2;
            
            //Set the user's file to a JSONObject
            FileReader fileReader = new FileReader(file);
            Object JsonObj = parser.parse(fileReader);
            JSONObject accountObj = (JSONObject) JsonObj;
            
            //Get the user's Monsters
            JSONArray units = (JSONArray) accountObj.get("unit_list");
            
            AtomicBoolean allSuccess = new AtomicBoolean(true);
            
            //Go through each available Monster to parse there runes if they are in the program
            units.forEach(k -> {
                JSONObject mon = (JSONObject) k;
                //Get the monster's ID
                long monsterId = (long) mon.get("unit_master_id");
                
                //Do nothing if the Monstr is not a part of the program
                if (!monsterIsPartOfProgram(monsterId))
                {
                    return;
                }
                
                int runeNum = 0;
                //Initialize the runes Array
                String[] lines = new String[8];
                //Add all runes
                for (int i = 0; i < 6; i++)
                {
                    lines[i] = createRuneLine(mon, runeNum);
                    runeNum++;
                }
                //Add all artifacts
                for (int i = 6; i < 8; i++)
                {
                    createArtifactLine(lines, mon, i - 6);
                    runeNum++;
                }
                
                //If all runes and artifacts are null, do not create a file and continue to the next Monster
                boolean allNull = true;
                for (String line : lines)
                {
                    if (line != null)
                    {
                        allNull = false;
                        break;
                    }
                }
                if (allNull)
                {
                    return;
                }
                
                //Create a file with a temporary path name
                File f = new File("%s/temp.csv".formatted(MonsterRunes.path));
                try
                {
                    //Initialize the writer and write each rune and artifact to the file
                    FileWriter writer = new FileWriter(f);
                    for (String line : lines)
                    {
                        writer.write(line + "\n");
                    }
                    writer.close();
                    
                    if (replace)
                    {
                        //Check if Monster ID is already done to get file name
                        int fileNum = 1;
                        if (doneIds.containsKey(monsterId))
                        {
                            fileNum = doneIds.get(monsterId) + 1;
                        }
                        
                        //Create a new path
                        String name = getMonsterName(monsterId);
                        String newPath = "%s/%s%d.csv".formatted(MonsterRunes.path, name, fileNum);
                        //Create a file with the new path in case there is already a file with the same name
                        File oldFile = new File(newPath);
                        
                        //Try and rename the old file to a temporary name
                        if (oldFile.renameTo(new File("%s/oldTemp.csv".formatted(MonsterRunes.path))))
                        {
                            //Try to rename the new file to the new path
                            if (f.renameTo(new File(newPath)))
                            {
                                //Delete the old file
                                new File("%s/oldTemp.csv".formatted(MonsterRunes.path)).delete();
                            }
                            else
                            {
                                //Undo everything.
                                //Rename the old file to its original name and delete the new one
                                oldFile.renameTo(new File(newPath));
                                f.delete();
                                //Print error message
                                System.out.printf("%sUnable to replace %s's runes%s%n", ConsoleColors.RED, name + fileNum, ConsoleColors.RESET);
                                allSuccess.set(false);
                            }
                        }
                        else
                        {
                            //Try to rename the new file to the new path in case there was no old file to rename
                            if (!f.renameTo(new File(newPath)))
                            {
                                //Delete the new file if unable to rename
                                f.delete();
                                //Print error message
                                System.out.printf("%sUnable to replace %s's runes%s%n", ConsoleColors.RED, name + fileNum, ConsoleColors.RESET);
                                allSuccess.set(false);
                            }
                        }
                    }
                    else
                    {
                        //Get path name
                        String name = getMonsterName(monsterId);
                        String newPath = "%s/%s".formatted(MonsterRunes.path, name);
                        
                        boolean success = false;
                        //Try to get file number, has a max of 15 per Monster
                        for (int i = 1; i < 15; i++)
                        {
                            //Try to rename the new file to the path name
                            if (f.renameTo(new File(newPath + i + ".csv")))
                            {
                                success = true;
                                break;
                            }
                        }
                        if (!success)
                        {
                            allSuccess.set(false);
                            //Delete new file and print error message
                            f.delete();
                            System.out.printf("%sUnable to add %s's runes%s%n", ConsoleColors.RED, name, ConsoleColors.RESET);
                        }
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                
                //Add one to the number of times the Monster ID has been parsed if the ID is already in the HashMap
                if (doneIds.containsKey(monsterId))
                {
                    doneIds.put(monsterId, doneIds.get(monsterId) + 1);
                }
                else
                {
                    //Put the Monster ID into the HashMap with an original value of 1
                    doneIds.put(monsterId, 1);
                }
            });
            
            //Print a message at the end depending on if there were any errors
            if (allSuccess.get())
            {
                System.out.printf("%sAll rune files updated/created successfully.%s", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
            else
            {
                System.out.printf("%n%sAt least 1 rune file was not able to be updated/created.%s", ConsoleColors.YELLOW, ConsoleColors.RESET);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Opens a file chooser and gets a single file from the user
     * @param title The title of the file chooser window
     * @param extension The file extension to look for
     * @return The file the user chose, or null if none is chosen
     */
    public static File getFileFromUser(String title, String extension)
    {
        //Help from StackOverflow (https://stackoverflow.com/questions/40255039/how-to-choose-file-in-java)
        //Ask the user to select the account file to open
        FileDialog dialog = new FileDialog((Frame) null, title);
        dialog.setFile(extension);
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        //Set the selected file
        File file;
        try
        {
            file = dialog.getFiles()[0];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            file = null;
        }
        dialog.dispose();
        System.out.println(file + " chosen.");
        //End help
        return file;
    }
    
    /**
     * Gets the name of the Monster with the corresponding ID
     *
     * @param id The ID to look for
     * @return The Monster's name
     */
    private String getMonsterName(String id)
    {
        //Get the names section of the JSON file
        JSONObject j1 = (JSONObject) monsterIds.get("monster");
        JSONObject j2 = (JSONObject) j1.get("names");
        
        //Try and get the name of the Monster
        String s = (String) j2.get(id);
        if (s == null)
        {
            return null;
        }
        //If the name exists, replace all spaces with underscores
        return s.replaceAll(" ", "_");
    }
    
    /**
     * Checks if the given Monster is in the program
     *
     * @param id The ID to check for
     * @return True if the Monster's name is included in the Monster database, false otherwise
     */
    private boolean monsterIsPartOfProgram(long id)
    {
        return Monster.stringIsMonsterName(getMonsterName((int) id));
    }
    
    /**
     * Gets the name of the Monster with the corresponding ID number
     *
     * @param id The ID to look for
     * @return The Monster's name
     */
    private String getMonsterName(long id)
    {
        return getMonsterName(id + "");
    }
    
    /**
     * Parses the rune information for the Monster and creates a line for the program
     *
     * @param monsterObj The JSONObject containing the Monster information
     * @param runeNum    The rune number to parse
     * @return The line containing the rune information
     */
    private String createRuneLine(JSONObject monsterObj, int runeNum)
    {
        //Initialize line
        String returnString = "";
        //Get runes section
        JSONArray runes = (JSONArray) monsterObj.get("runes");
        //Do nothing if there are no runes available
        if (runes.isEmpty())
        {
            return null;
        }
        JSONObject rune = (JSONObject) runes.get(runeNum); //Get specific rune
        JSONArray primaryEffect = (JSONArray) rune.get("pri_eff"); //Get rune primary effect
        JSONArray prefixEffect = (JSONArray) rune.get("prefix_eff"); //Get rune prefix effect if there is one
        JSONArray secondaryEffects = (JSONArray) rune.get("sec_eff"); //Get rune secondary effects
        
        //Add rune info to line.
        returnString += String.format("%d,%d,%d", programTypeNum((long) rune.get("set_id"), monsterObj, runeNum), programStatNum((long) primaryEffect.getFirst()), (long) primaryEffect.getLast());
        
        //Add prefix effect if there is one
        if ((long) prefixEffect.getFirst() != 0)
        {
            returnString += String.format(",%d,%d", programStatNum((long) prefixEffect.getFirst()), (long) prefixEffect.getLast());
        }
        
        //Add each secondary effect to the line
        for (Object s : secondaryEffects)
        {
            JSONArray stat = (JSONArray) s;
            int STAT_TYPE = 0;
            int STAT_BASE_AMOUNT = 1;
            int STAT_EXTRA_AMOUNT = 3;
            returnString += String.format(",%d,%d", programStatNum((long) stat.get(STAT_TYPE)), (long) stat.get(STAT_BASE_AMOUNT) + (long) stat.get(STAT_EXTRA_AMOUNT));
        }
        
        return returnString;
    }
    
    /**
     * Parses the artifact information for the Monster, creates a line for the program, and adds it to the lines array
     *
     * @param lines       The current lines array to add to
     * @param monsterObj  The Monster JSONObject
     * @param artifactNum The artifact number
     */
    private void createArtifactLine(String[] lines, JSONObject monsterObj, int artifactNum)
    {
        //Get the artifacts array
        JSONArray artifacts = (JSONArray) monsterObj.get("artifacts");
        //Do nothing if there are no artifacts
        if (artifacts.isEmpty())
        {
            return;
        }
        JSONObject artifact = (JSONObject) artifacts.get(artifactNum); //Get specific artifact
        JSONArray primaryEffect = (JSONArray) artifact.get("pri_effect"); //Get artifact primary effect
        long slot = (long) artifact.get("slot");
        
        //Add the line to the lines array
        lines[(slot == 2) ? 7 : 6] = String.format("%d,%d,%d", programArtifactTypeNum(slot), programArtifactStatNum((long) primaryEffect.getFirst()), (long) primaryEffect.get(1));
    }
    
    /**
     * Converts the file rune set number to the program rune set number
     *
     * @param oldNum     The file number to convert
     * @param monsterObj The current Monster JSONObject
     * @param runeNum    The current rune number
     * @return The converted number for the program
     */
    private int programTypeNum(long oldNum, JSONObject monsterObj, int runeNum)
    {
        return switch ((int) oldNum)
        {
            case 1 -> Rune.ENERGY;
            case 2 -> Rune.GUARD;
            case 3 -> Rune.SWIFT;
            case 4 -> Rune.BLADE;
            case 5 -> Rune.RAGE;
            case 6 -> Rune.FOCUS;
            case 7 -> Rune.ENDURE;
            case 8 -> Rune.FATAL;
            case 10 -> Rune.DESPAIR;
            case 11 -> Rune.VAMPIRE;
            case 13 -> Rune.VIOLENT;
            case 14 -> Rune.NEMESIS;
            case 15 -> Rune.WILL;
            case 16 -> Rune.SHIELD;
            case 17 -> Rune.REVENGE;
            case 18 -> Rune.DESTROY;
            case 19 -> Rune.FIGHT;
            case 20 -> Rune.DETERMINATION;
            case 21 -> Rune.ENHANCE;
            case 22 -> Rune.ACCURACY;
            case 23 -> Rune.TOLERANCE;
            case 24 -> Rune.SEAL;
            case 25 -> getNewRuneNumber(monsterObj, runeNum); //Intangible
            case 99 -> 99; //Immemorial
            default -> -1;
        };
    }
    
    /**
     * Converts an intangible rune to the rune set the user specifies
     *
     * @param monsterObj The current Monster JSONObject
     * @param runeNum    The current rune number
     * @return The new rune set number
     */
    private int getNewRuneNumber(JSONObject monsterObj, int runeNum)
    {
        String r;
        do
        {
            System.out.println("The program currently does not support intangible runes, please enter the set it represents.");
            System.out.println("Current Monster: " + getMonsterName((long) monsterObj.get("unit_master_id")));
            System.out.println("Current Rune: " + (runeNum + 1));
            r = scan.nextLine();
        }
        while (Rune.stringToNum(r) == -1);
        return Rune.stringToNum(r);
    }
    
    /**
     * Converts the JSON file rune stat number to the equivalent number in the program
     *
     * @param oldNum The file number to convert
     * @return The converted number for the program
     */
    private int programStatNum(long oldNum)
    {
        return switch ((int) oldNum)
        {
            case 1 -> Rune.HP;
            case 2 -> Rune.HPPERCENT;
            case 3 -> Rune.ATK;
            case 4 -> Rune.ATKPERCENT;
            case 5 -> Rune.DEF;
            case 6 -> Rune.DEFPERCENT;
            case 8 -> Rune.SPD;
            case 9 -> Rune.CRITRATE;
            case 10 -> Rune.CRITDMG;
            case 11 -> Rune.RES;
            case 12 -> Rune.ACC;
            default -> -1;
        };
    }
    
    /**
     * Converts the JSON file artifact slot number to the equivalent number in the program
     *
     * @param slotNum The file number to convert
     * @return The converted number for the program
     */
    private int programArtifactTypeNum(long slotNum)
    {
        return ((slotNum == 2) ? 23 : 22);
    }
    
    /**
     * Converts the JSON file artifact stat number to the equivalent number in the program
     *
     * @param oldNum The file number to convert
     * @return The converted number for the program
     */
    private int programArtifactStatNum(long oldNum)
    {
        return switch ((int) oldNum)
        {
            case 100 -> Rune.HP;
            case 101 -> Rune.ATK;
            case 102 -> Rune.DEF;
            default -> -1;
        };
    }
}