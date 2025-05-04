package Util;

import javax.swing.*;
import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import Errors.*;
import GUI.*;
import Game.*;
import Monsters.Dark.*;
import Monsters.*;
import Monsters.Fire.*;
import Monsters.Light.*;
import Monsters.Water.*;
import Monsters.Wind.*;
import Runes.*;
import Runes.Monster_Runes.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static Game.Main.pause;
import static Game.Main.scan;
import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

/**
 * Utility class providing helper methods for various functions.
 * <br><br>
 * The Util class is split into several subclasses like {@link STRINGS} and {@link MONSTERS} to organize functions by their general use
 *
 * @author Anthony (Tony) Youssef
 */
public class Util
{
    /**
     * The STRINGS subclass provides utility methods for string manipulation, formatting,
     * and verification.
     */
    public static class STRINGS
    {
        /**
         * Converts the given string to title case, where the first letter of each word is capitalized,
         * and the remaining letters are in lowercase. Underscores in the string are replaced with spaces.
         *
         * @param str The input string to be converted to title case.
         * @return A new string in title case with spaces instead of underscores.
         */
        public static String toTitleCase(String str)
        {
            str = str.replaceAll("_", " ");
            StringBuilder returnStr = new StringBuilder(str.substring(0, 1).toUpperCase());
            for (int i = 1; i < str.length(); i++)
            {
                if (str.charAt(i - 1) == ' ')
                {
                    returnStr.append(str.substring(i, i + 1).toUpperCase());
                }
                else
                {
                    returnStr.append(str.substring(i, i + 1).toLowerCase());
                }
            }
            return returnStr.toString();
        }
        
        /**
         * Converts a given string into an Enum-compatible format. Spaces in the string are replaced
         * with underscores, and all characters are converted to uppercase.
         *
         * @param str The input string to be converted to a format suitable for Enum names.
         * @return A new string formatted in uppercase with underscores in place of spaces.
         */
        public static String toEnumCase(String str)
        {
            return str.replace(" ", "_").toUpperCase();
        }
        
        /**
         * Tests if the given String is an int
         *
         * @param s The string to test
         * @return True if s is an int
         */
        public static boolean stringIsInt(String s)
        {
            try
            {
                //Parse the string for an int and return true if no errors
                Integer.parseInt(s);
                return true;
            }
            catch (NumberFormatException e)
            {
                //Return false if String cannot be parsed
                return false;
            }
        }
        
        /**
         * Extracts a substring from the input string up to (but not including) the first occurrence
         * of the specified end string. If the end string is not present in the input, the entire
         * input string is returned.
         *
         * @param str The input string from which the substring is to be extracted.
         * @param end The end string that marks the limit of the substring.
         * @return A substring of the input string that ends before the first occurrence of the end string.
         * If the end string is not found, returns the entire input string.
         */
        public static String substringUpToString(String str, String end)
        {
            return !str.contains(end) ? str : str.substring(0, str.indexOf(end));
        }
        
        /**
         * Formats a string using the specified format, inserts the provided color codes around formatted
         * arguments for styling, and resets color formatting at the end.
         *
         * @param format The format string containing placeholders (e.g., %s, %d) to be replaced by the arguments.
         * @param color  The color code to be applied to the formatted arguments (e.g., ConsoleColor.RED).
         * @param args   The arguments to be inserted into the format string at the placeholders.
         * @return A formatted string with the specified color applied to the arguments and color reset afterward.
         * @throws InvalidArgumentLength if there is a mismatch between the format placeholders and the arguments provided.
         */
        public static String formatWithColor(String format, ConsoleColor color, Object... args)
        {
            //Add the color to the beginning of the args
            int count = 0;
            ArrayList<Object> updatedArgs = new ArrayList<>();
            updatedArgs.add(color.toString());
            
            //Add each original argument followed by the color
            for (Object arg : args)
            {
                updatedArgs.add(arg);
                updatedArgs.add(color);
            }
            
            //Update the format
            StringBuilder updatedFormat = new StringBuilder();
            char[] charArray = format.toCharArray();
            for (int i = 0; i < charArray.length - 1; i++)
            {
                char c = charArray[i];
                updatedFormat.append(c);
                if (c == '%')
                {
                    if (i + 1 < charArray.length && charArray[i + 1] == '%')
                    {
                        i++;
                        updatedFormat.append("%");
                        continue;
                    }
                    i++;
                    while (!Character.isAlphabetic(charArray[i]))
                    {
                        updatedFormat.append(charArray[i++]);
                    }
                    if (charArray[i] == 'n')
                    {
                        updatedArgs.add(color);
                    }
                    updatedFormat.append(charArray[i]).append("%s");
                    count++;
                }
            }
            
            //Reset the color
            updatedArgs.add(ConsoleColor.RESET);
            
            //Correct new line character
            if (format.endsWith("\n"))
            {
                updatedFormat.append("%n");
            }
            
            //Validate argument size
            if (count + 2 + args.length != updatedArgs.size())
            {
                throw new InvalidArgumentLength("Invalid format: %s\n\t%s".formatted(updatedFormat, updatedArgs));
            }
            
            //Return formatted String
            return ("%s" + updatedFormat + "%s").formatted(updatedArgs.toArray());
        }
        
        /**
         * Converts a time in nanoseconds to readable time
         *
         * @param nanoseconds Number to convert
         * @return A String conveying the time in a readable format
         */
        public static String toReadableTime(long nanoseconds)
        {
            //Initialize times
            int seconds = 0, minutes = 0, hours = 0, days = 0, weeks = 0, years = 0;
            
            while (nanoseconds >= 1e9) //1 second
            {
                //Increment seconds
                seconds++;
                nanoseconds -= (long) 1e9;
                
                //Increment times if they are overflowed
                if (seconds >= 60)
                {
                    seconds -= 60;
                    minutes++;
                }
                if (minutes >= 60)
                {
                    minutes -= 60;
                    hours++;
                }
                if (hours >= 24)
                {
                    hours -= 24;
                    days++;
                }
                if (days >= 7)
                {
                    days -= 7;
                    weeks++;
                }
                if (weeks >= 52)
                {
                    weeks -= 52;
                    years++;
                }
            }
            //Format final String
            String returnString = "";
            if (years > 0)
            {
                returnString += "%d year%s, ".formatted(years, years == 1 ? "" : "s");
            }
            if (weeks > 0)
            {
                returnString += "%d week%s, ".formatted(weeks, weeks == 1 ? "" : "s");
            }
            if (days > 0)
            {
                returnString += "%d day%s, ".formatted(days, days == 1 ? "" : "s");
            }
            if (hours > 0)
            {
                returnString += "%d hour%s, ".formatted(hours, hours == 1 ? "" : "s");
            }
            if (minutes > 0)
            {
                returnString += "%d minute%s, ".formatted(minutes, minutes == 1 ? "" : "s");
            }
            return "%s%d.%d seconds".formatted(returnString, seconds, nanoseconds);
        }
    }
    
    /**
     * The MONSTERS subclass provides various functions and operations related to Monsters in the game.
     */
    public static class MONSTERS
    {
        /**
         * Searches the provided ArrayList for a Monster whose name matches the provided String
         *
         * @param s    The name of the Monster to look for
         * @param mons The list of Monsters to look in
         * @return True if the ArrayList contains a Monster whose name equals the provided String, false otherwise.
         */
        public static boolean stringIsMonsterName(String s, ArrayList<Monster> mons)
        {
            for (Monster mon : mons)
            {
                if (mon.getName(false, false).equalsIgnoreCase(s))
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Searches the Monster database for a name that matches the provided String. This is the same as calling <br>
         * <code>Util.MONSTERS.stringIsMonsterName(s, {@link Monster#MONSTER_NAMES_DATABASE})</code>
         *
         * @param s The name to look for
         * @return True if the name is in the monster database, false otherwise
         */
        public static boolean stringIsMonsterName(String s)
        {
            //Do nothing if the given name is null
            if (s == null)
            {
                return false;
            }
            //Replace underscores with spaces
            s = s.replace("_", " ");
            
            //Search the database for the given name
            for (String string : Monster.MONSTER_NAMES_DATABASE.keySet())
            {
                if (string.equalsIgnoreCase(s))
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Formats the given String into a String readable by the program
         *
         * @param name The String to format
         * @return The formatted String
         */
        public static String toProperName(String name)
        {
            return STRINGS.toTitleCase(name).replaceAll(" ", "_");
        }
        
        /**
         * Creates a new Monster given another of the same class
         *
         * @param mon The Monster to create a new instance of
         * @return A new Monster of the same class
         */
        public static Monster createNewMonFromMon(Monster mon)
        {
            return createNewMonFromName(mon.getName(false, false), true);
        }
        
        /**
         * Creates a new Monster given its name. Uses the default rune set
         *
         * @param name     The name of the Monster
         * @param scanLine True if the function should buffer a call to <code>Scanner.nextLine()</code>, false otherwise
         * @return A new Monster with the given name and default rune set. Ex: inputting "Loren" will return a new Loren instance with rune set 1
         */
        public static Monster createNewMonFromName(String name, boolean scanLine)
        {
            return createNewMonFromName(name, 1, scanLine);
        }
        
        /**
         * Creates a new Monster given its name.
         *
         * @param name       The name of the Monster
         * @param runeSetNum The rune set number to use
         * @param scanLine   True if the function should buffer a Scanner.nextLine() call, false otherwise
         * @return A new Monster with the given name. Ex: inputting ("Loren", 2) will return a new Loren instance with rune set 2
         */
        public static Monster createNewMonFromName(String name, int runeSetNum, boolean scanLine)
        {
            //Replace spaces with underscores
            name = name.replaceAll(" ", "_");
            //Convert the name into a readable format
            name = toProperName(name);
            
            //Create a temp name to get from the database
            String temp = name.replaceAll("_", " ");
            String element = Monster.MONSTER_NAMES_DATABASE.get(temp);
            
            //Get the Monster's element
            String className = "Monsters.%s.%s".formatted(element, name);
            try
            {
                //Get the Monster's class and try to create a new instance
                Class<?> c = Class.forName(className);
                Monster m = (Monster) c.getConstructor(String.class).newInstance("%s%d.csv".formatted(name, runeSetNum));
                //Return null if the Monster could not be created properly, otherwise return the Monster
                if (m.getRunes() == null)
                {
                    System.err.println("Unable to create Monster");
                    return null;
                }
                return m;
            }
            catch (ClassNotFoundException e) //The Monster could not be created properly
            {
                if (scanLine)
                {
                    System.err.println("Unable to create Monster \"" + name + "\"");
                    scan.nextLine();
                }
                System.out.println();
                throw new RuntimeException(e);
            }
            catch (Exception e)
            {
                if (scanLine)
                {
                    System.err.println("Unable to create Monster \"" + name + "\"");
                }
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Gets a Monster name from the user and prints a detailed description of the Monster
         */
        public static void inspect()
        {
            String inputInspect;
            //Get the Monster to inspect
            do
            {
                System.out.println("Which monster do you want to inspect?");
                inputInspect = scan.nextLine();
            }
            while (!MONSTERS.stringIsMonsterName(inputInspect));
            
            //Get the rune set number
            int runeSetNum = CONSOLE_INTERFACE.INPUT.getRuneSetNum();
            
            //Try to create the Monster
            Monster m = MONSTERS.createNewMonFromName(inputInspect, Math.abs(runeSetNum), true);
            
            //Print the Monster's details
            if (m != null)
            {
                m.printWithDetails();
            }
            //Java is weird
            if (runeSetNum != -1)
            {
                scan.nextLine();
            }
        }
        
        /**
         * Formats a set of numbers into an ArrayList of Buffs
         *
         * @param args The numbers to format. Format: Buff number, number of turns, repeat as needed
         * @return The ArrayList of Buffs specified by the varargs
         */
        public static ArrayList<Buff> abilityBuffs(int... args)
        {
            //Make sure the argument length is valid
            if (args.length % 2 != 0)
            {
                throw new InvalidArgumentLength("Bad argument length: %d".formatted(args.length));
            }
            
            //Format the args into buffs
            ArrayList<Buff> buffs = new ArrayList<>();
            for (int i = 0; i < args.length; i += 2)
            {
                buffs.add(new Buff(BuffEffect.numToBuff(args[i]), args[i + 1]));
            }
            return buffs;
        }
        
        /**
         * Formats a set of numbers into an ArrayList of Debuffs based on the given parameters.
         * Ensures the input arguments are in the correct format and associates each Debuff with the provided caster.
         *
         * @param caster The Monster that casts the Debuffs.
         * @param args   The numbers to format, given in sets of three:
         *               1. Debuff number (as per {@link DebuffEffect}),
         *               2. Number of turns the Debuff lasts,
         *               3. Whether the Debuff ignores immunity (0 or 1).
         * @return An ArrayList of Debuffs created based on the input parameters.
         * @throws InvalidArgumentLength If the number of arguments in args is not a multiple of 3.
         */
        public static ArrayList<Debuff> abilityDebuffs(Monster caster, int... args)
        {
            //Make sure the argument length is valid
            if (args.length % 3 != 0)
            {
                throw new InvalidArgumentLength("Bad argument length: %d".formatted(args.length));
            }
            
            //Format the args into debuffs
            ArrayList<Debuff> debuffs = new ArrayList<>();
            for (int i = 0; i < args.length; i += 3)
            {
                Debuff d = new Debuff(DebuffEffect.numToDebuff(args[i]), args[i + 1], args[i + 2]);
                d.setCaster(caster);
                debuffs.add(d);
            }
            return debuffs;
        }
        
        /**
         * Formats a set of numbers into an ArrayList of Integers
         *
         * @param args The numbers to enter
         * @return The ArrayList of Integers
         */
        public static ArrayList<Integer> abilityChances(int... args)
        {
            //Add each number passed into the array
            ArrayList<Integer> chances = new ArrayList<>();
            for (int i : args)
            {
                chances.add(i);
            }
            return chances;
        }
        
        /**
         * Activates the next Monsters turn
         *
         * @param next       The Monster whose turn it is
         * @param targetMon  The target Monster
         * @param abilityNum The ability number
         */
        public static void applyNextTurn(Monster next, Monster targetMon, int abilityNum)
        {
            //Try to perform the Monster's turn
            //If something went wrong, does process again
            if (!next.nextTurn(targetMon, abilityNum))
            {
                System.out.println("Uh oh! Something in the turn went wrong! (Check your ability number and cooldown cooldown and make sure you're not targeting a dead monster!)");
            }
        }
        
        /**
         * Converts the given String to a Monster
         *
         * @param name The name of the Monster
         * @param mons The List of Monsters to search in
         * @return The Monster whose name equals the given String
         */
        private static Monster getMonFromName(String name, ArrayList<Monster> mons)
        {
            //Search the list for the Monster
            for (Monster mon : mons)
            {
                if (mon.getName(false, false).equalsIgnoreCase(name))
                {
                    return mon;
                }
            }
            //Return a blank Monster if the name was not found
            return new Monster();
        }
        
        /**
         * Adds the elemental color to the given String
         *
         * @param name    The name of the Monster
         * @param element The element of the Monster
         * @return The name of the Monster with the appropriate elemental color
         */
        private static String nameWithElement(String name, String element)
        {
            try
            {
                return name + Element.valueOf(element.toUpperCase()).getFontColor() + ConsoleColor.RESET;
            }
            catch (IllegalArgumentException e)
            {
                return "";
            }
        }
        
        /**
         * Activates the team-based rune effects (e.g., fight, shield) and sets the names for each Monster
         *
         * @param team1 The first Team to apply
         * @param team2 The second Team to apply
         */
        public static void setNamesAndRuneEffects(Team team1, Team team2)
        {
            //Implement ally rune effects
            setRuneEffects(team1);
            setRuneEffects(team2);
            
            //Set names for each team
            for (Monster mon : team1.getMonsters())
            {
                mon.setName("%s(1)".formatted(mon.getName(false, false)));
            }
            for (Monster mon : team2.getMonsters())
            {
                mon.setName("%s(2)".formatted(mon.getName(false, false)));
            }
        }
        
        /**
         * Sets ally rune effects for a single team
         *
         * @param team The team to apply rune effects for
         */
        private static void setRuneEffects(Team team)
        {
            for (Monster mon : team.getMonsters())
            {
                for (Monster mon1 : team.getMonsters())
                {
                    //Apply rune effects for each Monster
                    mon1.addAppliedBuff(new RuneShield((int) Math.ceil(mon.getMaxHp() * (0.15 * mon.numOfSets(RuneType.SHIELD))), 3), mon);
                    mon1.setAtk((int) Math.ceil(mon1.getAtk() + mon1.getBaseAtk() * (0.08 * mon.numOfSets(RuneType.FIGHT))));
                    mon1.setDef((int) Math.ceil(mon1.getDef() + mon1.getBaseDef() * (0.08 * mon.numOfSets(RuneType.DETERMINATION))));
                    mon1.setMaxHp((int) Math.ceil(mon1.getMaxHp() + mon1.getBaseMaxHp() * (0.08 * mon.numOfSets(RuneType.ENHANCE))));
                    mon1.setAccuracy((mon1.getAccuracy() + (10 * mon.numOfSets(RuneType.ACCURACY))));
                    mon1.setResistance(mon1.getResistance() + (10 * mon.numOfSets(RuneType.TOLERANCE)));
                    mon1.setCurrentHp(mon1.getMaxHp());
                }
            }
        }
    }
    
    /**
     * The TEAMS subclass provides a collection of methods for manipulating and processing teams.
     */
    public static class TEAMS
    {
        /**
         * Compresses a team into a line to export to a file
         *
         * @param team The team to compress
         * @return A String containing a single line containing the team
         */
        private static String compressTeam(Team team)
        {
            StringBuilder line = new StringBuilder();
            //Add each Monsters key denoted by the library
            for (Monster mon : team.getMonsters())
            {
                line.append("%d,".formatted(Auto_Play.getMonsterKeys().indexOf(mon.getName(false, false))));
            }
            //Add wins and losses
            line.append("%d,%d,".formatted(team.getWins(), team.getLosses()));
            return line.toString();
        }
        
        /**
         * Exports the results of a battle simulation to a CSV file. The file contains data such as
         * monster keys, elapsed times, and the state of each team involved in the simulation.
         *
         * @param i                An integer value indicating the first coordinate or identifier.
         * @param j                An integer value indicating the second coordinate or identifier.
         * @param completed        A boolean flag indicating whether the simulation is completed.
         * @param teamStats        An ArrayList containing the teams and their states after the simulation.
         * @param totalRunningTime A StopWatch instance tracking the total runtime of the simulation.
         * @param battleTime       A StopWatch instance tracking the elapsed battle time.
         * @return The file path as a String where the results are saved if successful, or null if the file creation fails.
         */
        public static String exportResults(int i, int j, boolean completed, ArrayList<Team> teamStats, StopWatch totalRunningTime, StopWatch battleTime)
        {
            //Initialize the list of teams
            ArrayList<String> lines = new ArrayList<>();
            
            //Add the library to the top of the file
            final String[] temp = {""};
            Auto_Play.getMonsterKeys().forEach((name) -> temp[0] += "%s:%d,".formatted(name, Auto_Play.getMonsterKeys().indexOf(name)));
            lines.add(temp[0]);
            if (!completed)
            {
                lines.add("%d,%d".formatted(i, j));
                lines.add("%d,%d".formatted(totalRunningTime.getElapsedTime(), battleTime.getElapsedTime()));
            }
            
            //Add each team to the list
            for (Team team : teamStats)
            {
                lines.add(compressTeam(team));
            }
            
            Date today = new Date();
            //Try to create a file using the current date and time as a name and write to it
            String fileName;
            try
            {
                //Create the file
                fileName = "%s/src/Game/Results/%s.csv".formatted(Auto_Play.class.getResource("Auto_Play.class").getPath().substring(0, Auto_Play.class.getResource("Auto_Play.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36), today.toString().replaceAll(":", "-")).replaceAll("%20", " ").replaceAll("file:", "");
                File f = new File(fileName);
                FileWriter writer = new FileWriter(f);
                //Add each line to file
                for (String line : lines)
                {
                    writer.write("%s\n".formatted(line));
                }
                writer.close();
                return fileName;
            }
            //Create a random seed name in case date file name is already taken
            catch (IOException e)
            {
                try
                {
                    //Create the file
                    fileName = "%ssrc/Game/Results/%s.csv".formatted(Auto_Play.class.getResource("Auto_Play.class").getPath().substring(0, Auto_Play.class.getResource("Auto_Play.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36), new Random().nextDouble(0, 10));
                    FileWriter writer = new FileWriter(fileName);
                    
                    //Add each line to file
                    for (String line : lines)
                    {
                        writer.write("%s\n".formatted(line));
                    }
                    writer.close();
                    return fileName;
                }
                catch (IOException ignored)
                {
                }
                return null;
            }
        }
        
        /**
         * Replaces each Monster on a given team with a new instance of the same Monster
         *
         * @param team The team to replace Monsters on
         */
        public static void resetTeamForMemory(ArrayList<Monster> team)
        {
            for (int i = 0; i < team.size(); i++)
            {
                try
                {
                    team.set(i, MONSTERS.createNewMonFromMon(team.get(i)));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        
        /**
         * Resets each Monster on the team
         *
         * @param team The team to reset
         */
        public static void resetTeamForTime(Team team)
        {
            team.resetTeam();
        }
        
        /**
         * Calculates any damage reduction for an attack
         *
         * @param attackedTeam The team containing the Monster being attacked
         * @param dmg          The initial damage to be dealt
         * @param target       The Monster being attacked
         * @return The new damage to be dealt
         */
        public static double dmgReduction(Team attackedTeam, double dmg, Monster target)
        {
            double lowestDmg = dmg;
            //Get the lowest possible damage reduction without stacking
            for (Monster m : attackedTeam.getMonsters())
            {
                //Get potential reduced damage
                double newDmg = m.dmgReductionProtocol(dmg, m.equals(target));
                boolean reduce = newDmg != dmg;
                //Compare damage reduction
                if (reduce && newDmg < lowestDmg)
                {
                    lowestDmg = newDmg;
                }
            }
            //Minimum of 1 damage
            return Math.max(lowestDmg, 1);
        }
        
        /**
         * Checks whether the given list has the given Monster name (case-insensitive)
         *
         * @param name       The name to search for
         * @param pickedMons The List to search in
         * @return True if at least one Monster's name in the List equals the given String, false otherwise
         */
        public static boolean teamHasMon(String name, ArrayList<Monster> pickedMons)
        {
            //Search the list for the Monster
            return MONSTERS.stringIsMonsterName(name, pickedMons);
        }
        
        /**
         * Asks for four Monsters from the user and finds the Team that has all four.
         *
         * @param pickedMons The Monsters already picked. The first call should pass an empty ArrayList.
         * @param teams      The teams in use
         * @return The position (index) of the Team in the array
         */
        public static int findTeamFromMonsters(ArrayList<Monster> pickedMons, ArrayList<Team> teams)
        {
            //Get the name of 1 one the Monster in the team
            String inputMon;
            do
            {
                System.out.printf("Enter Monster %d's name%n", pickedMons.size() + 1);
                inputMon = scan.nextLine();
            }
            while (!MONSTERS.stringIsMonsterName(inputMon) || TEAMS.teamHasMon(inputMon, pickedMons));
            
            //Try to create the Monster and add it to the array
            try
            {
                pickedMons.add(MONSTERS.createNewMonFromName(inputMon, true));
            }
            catch (Exception e)
            {
                System.out.println("Unable to find Monster");
                return findTeamFromMonsters(pickedMons, teams);
            }
            
            //Do the above until there are 4 selected Monsters
            if (pickedMons.size() < 4)
            {
                return findTeamFromMonsters(pickedMons, teams);
            }
            
            int index = teams.size() - 1;
            
            //Linear search to find the team
            outer:
            for (int i = teams.size() - 1; i >= 0; i--)
            {
                Team team = teams.get(i);
                for (int j = 0; j < 4; j++)
                {
                    //Go to the next team if the current one does not have one of the Monsters
                    if (!team.hasInstanceOf(pickedMons.get(j)))
                    {
                        index--;
                        continue outer;
                    }
                }
                break;
            }
            
            if (index < 0)
            {
                System.out.println("Oops! Team not found!");
            }
            return index;
        }
    }
    
    /**
     * The CONSOLE_INTERFACE subclass provides functionality to interact with the console
     * through input and output
     */
    public static class CONSOLE_INTERFACE
    {
        /**
         * The OUTPUT subclass provides methods for printing various game-related elements and effects
         * to the console. This includes displaying team statistics, available commands, game effects,
         * and other formatted strings for gameplay purposes.
         */
        public static class OUTPUT
        {
            /**
             * Prints a single team with its stats
             *
             * @param team The Team to print
             */
            public static void printSingleTeamStats(Team team)
            {
                //Print each Monster in the team
                for (Monster mon : team.getMonsters())
                {
                    System.out.printf("%s\t\t", mon.getName(true, false));
                }
                System.out.printf("Number of wins: %,d\tNumber of losses: %,d", team.getWins(), team.getLosses());
                
                //Calculate the win-loss ratio
                double ratio = 1.0 * team.getWins() / (team.getLosses() + team.getWins());
                ratio *= 100;
                if (team.getLosses() == 0)
                {
                    if (team.getWins() == 0)
                    {
                        ratio = 0.0;
                    }
                }
                
                //Print win-loss ratio
                System.out.printf("\tWin/Loss Ratio: %f", ratio);
                System.out.println();
            }
            
            /**
             * Prints a list of available commands in the pause menu along with their descriptions.
             */
            public static void printPauseCommands()
            {
                ArrayList<Pair<String, String>> list = new ArrayList<>();
                
                //Add each command and description to a list of Pairs
                list.add(new Pair<>("inspect", "Inspect a specific team using its Monsters"));
                list.add(new Pair<>("order", "Change how the teams are ordered"));
                list.add(new Pair<>("monsters", "View every Monster sorted by their average place"));
                list.add(new Pair<>("#", "Get the team at a specific index (starting at 0). You can use a negative number to start counting from the end of the list"));
                list.add(new Pair<>("# - #", "Use this format to get a range of teams, replacing \"#\" with a number"));
                list.add(new Pair<>("help", "Show this commands list"));
                list.add(new Pair<>("filter", "Filter the teams by whitelisting or blacklisting specific Monsters"));
                list.add(new Pair<>("exit", "Exit this menu"));
                list.add(new Pair<>("quit", "Quit the program"));
                list.add(new Pair<>("save", "Save your current progress"));
                
                //Print each command as a list
                int i = 1;
                for (Pair<String, String> p : list)
                {
                    System.out.printf("%d. \"%s\": %s%n", i++, p.getFirst(), p.getSecond());
                }
            }
            
            /**
             * Prints the stun effect to the console
             */
            public static void printStunEffect()
            {
                if (Monster.isPrint())
                {
                    System.out.println("Stunned!");
                    pause(200);
                }
            }
            
            /**
             * Prints the sleep effect to the console
             */
            public static void printSleepEffect()
            {
                if (Monster.isPrint())
                {
                    System.out.println("Zzzzz... Slept!");
                    pause(200);
                }
            }
            
            /**
             * Prints the freeze effect to the console
             */
            public static void printFreezeEffect()
            {
                if (Monster.isPrint())
                {
                    System.out.printf("%s%sBrrr... Frozen!%s%n", ConsoleColor.CYAN_BACKGROUND_BRIGHT, ConsoleColor.BLACK_BOLD, ConsoleColor.RESET);
                    pause(200);
                }
            }
            
            /**
             * Prints the bomb explosion effect
             *
             * @param target The Monster with the bomb
             */
            public static void printBombExplodeEffect(Monster target)
            {
                
                if (Monster.isPrint())
                {
                    System.out.printf("%s%sBOOM!%s Bomb exploded! You took %,f damage! %s has %,d health left!%n", ConsoleColor.RED_BACKGROUND_BRIGHT, ConsoleColor.BLACK_BOLD, ConsoleColor.RESET, target.getMaxHp() * 0.4, target.getName(true, true),
                            Math.max(target.getCurrentHp(), 0));
                }
                
                printStunEffect();
            }
            
            /**
             * Prints the Monsters that can still be picked for the Team
             *
             * @param monsPicked The Monsters that cannot be picked
             */
            public static void printMonsToPick(ArrayList<Monster> monsPicked)
            {
                ArrayList<String> toPrint = new ArrayList<>();
                ArrayList<String> fire = new ArrayList<>();
                ArrayList<String> water = new ArrayList<>();
                ArrayList<String> wind = new ArrayList<>();
                ArrayList<String> light = new ArrayList<>();
                ArrayList<String> dark = new ArrayList<>();
                try
                {
                    //Read from the Monster database
                    Scanner read = new Scanner(Objects.requireNonNull(Monster.class.getResourceAsStream("Monster database.csv")));
                    while (read.hasNextLine())
                    {
                        String line = read.nextLine();
                        if (line.isEmpty())
                        {
                            continue;
                        }
                        String[] monAndElement = line.split(",");
                        String name = MONSTERS.nameWithElement(monAndElement[0], monAndElement[1]);
                        //Monster has not been picked yet
                        if (MONSTERS.getMonFromName(monAndElement[0], monsPicked).equals(new Monster()))
                        {
                            //Add Monster to the proper array
                            Element e = Element.valueOf(monAndElement[1].toUpperCase());
                            (switch (e)
                            {
                                case FIRE -> fire;
                                case WATER -> water;
                                case WIND -> wind;
                                case LIGHT -> light;
                                default -> dark;
                            }).add(e.getFontColor() + name);
                        }
                    }
                    //Sort the arrays alphabetically
                    Collections.sort(water);
                    Collections.sort(fire);
                    Collections.sort(wind);
                    Collections.sort(light);
                    Collections.sort(dark);
                    
                    //Combine the arrays
                    toPrint.addAll(water);
                    toPrint.addAll(fire);
                    toPrint.addAll(wind);
                    toPrint.addAll(light);
                    toPrint.addAll(dark);
                    
                    //Print each Monster
                    for (String s : toPrint)
                    {
                        printfWithColor("%s      ", ConsoleColor.RESET, s);
                    }
                }
                catch (Throwable e)
                {
                    throw new RuntimeException(e);
                }
            }
            
            /**
             * A helper method to find the correct spacing for the {@link Team#toString()} method
             *
             * @param line1 The first line of the String (each entry in the list is a separate Monster)
             * @param line2 The second line of the String (each entry in the list is a separate Monster)
             * @param line3 The third line of the String (each entry in the list is a separate Monster)
             * @param line4 The fourth line of the String (each entry in the list is a separate Monster)
             * @return The String with the correct spacing
             */
            public static String toStringSpacing(ArrayList<String> line1, ArrayList<String> line2, ArrayList<String> line3, ArrayList<String> line4)
            {
                //Initialize lines
                ArrayList<ArrayList<String>> lines = new ArrayList<>();
                lines.add(line1);
                lines.add(line2);
                lines.add(line3);
                lines.add(line4);
                
                //Find the longest line and it's length
                int longestLength = 0;
                ArrayList<String> longestLine = new ArrayList<>();
                for (ArrayList<String> list : lines)
                {
                    int currentLength = 0;
                    //Calculate line length without colors
                    for (String s : list)
                    {
                        currentLength += lengthWithoutColors(s);
                    }
                    
                    //Update longest line
                    if (currentLength > longestLength)
                    {
                        longestLength = currentLength;
                        longestLine = list;
                    }
                }
                //Format the longest line
                for (int j = 0; j < longestLine.size() - 1; j++)
                {
                    longestLine.set(j, "%s        ".formatted(longestLine.get(j)));
                }
                
                //Format all other lines
                //Do it 4 times to make sure all lines are formatted properly
                for (int i = 0; i < 4; i++)
                {
                    for (ArrayList<String> line : lines)
                    {
                        if (line.equals(longestLine))
                        {
                            if (!line.equals(line1) && i == 1)
                            {
                                //Tab each entry of the line
                                for (int j = 0; j < line.size() - 1; j++)
                                {
                                    line.set(j, "       %s".formatted(line.get(j)));
                                }
                            }
                            continue;
                        }
                        int length = 0;
                        int currentLength = 0;
                        
                        for (int j = 0; j < line.size() - 1; j++)
                        {
                            //Length of the longest line at Monster j
                            length += lengthWithoutColors(longestLine.get(j));
                            
                            //Length of current line at Monster j
                            currentLength += lengthWithoutColors(line.get(j));
                            
                            //Indent lines 2-4
                            if (!line.equals(line1) && i == 1)
                            {
                                line.set(j, "       %s".formatted(line.get(j)));
                                currentLength += "       ".length();
                            }
                            
                            //Add spaces to the longest line at Monster j in case the current line at Monster j is longer than the longest line at Monster j
                            while (length < currentLength)
                            {
                                length++;
                                longestLine.set(j, "%s ".formatted(longestLine.get(j)));
                            }
                            
                            //Add spaces to the current line to correctly align it with the longest line
                            while (currentLength < length)
                            {
                                currentLength++;
                                line.set(j, "%s ".formatted(line.get(j)));
                            }
                        }
                        
                        //Make sure there is space between each Monster
                        for (int j = 0; j < line.size() - 1; j++)
                        {
                            if (!line.get(j).endsWith(" ") && !line.get(j + 1).startsWith(" "))
                            {
                                line.set(j, "%s     ".formatted(line.get(j)));
                            }
                        }
                    }
                }
                
                //This might not be necessary, but I'm honestly too scared to change it
                for (ArrayList<String> list : lines)
                {
                    //Tab the last entry in every line except the first
                    if (!list.equals(line1))
                    {
                        list.set(list.size() - 1, "       %s".formatted(list.getLast()));
                    }
                }
                
                //Format all lines to correct positioning
                return formatString(lines);
            }
            
            /**
             * A method to properly format each line in {@link #toStringSpacing(ArrayList, ArrayList, ArrayList, ArrayList)}
             *
             * @param lines Each line to format (Should be a length of 4)
             * @return The properly formatted String
             */
            public static String formatString(ArrayList<ArrayList<String>> lines)
            {
                StringBuilder s = new StringBuilder();
                //Line 1 - Monster info
                for (int i = 0; i < lines.get(0).size(); i++)
                {
                    s.append(lines.getFirst().get(i));
                }
                s.append("\n");
                
                //Line 2 - Buffs
                for (int i = 0; i < lines.get(0).size(); i++)
                {
                    s.append(ConsoleColor.BLUE).append(lines.get(1).get(i));
                }
                s.append("\n");
                
                //Line 3 - Debuffs
                for (int i = 0; i < lines.get(0).size(); i++)
                {
                    s.append(ConsoleColor.RED).append(lines.get(2).get(i));
                }
                s.append("\n");
                
                //Line 4 - Other effects
                for (int i = 0; i < lines.get(0).size(); i++)
                {
                    s.append(ConsoleColor.PURPLE).append(lines.get(3).get(i));
                }
                return s.toString();
            }
            
            /**
             * If the given String contains a color, finds the length without the color, otherwise returns the length of the String
             *
             * @param string The String to find the length of
             * @return The length of the String without colors
             */
            public static int lengthWithoutColors(String string)
            {
                int length = string.length();
                for (ConsoleColor value : ConsoleColor.values())
                {
                    if (string.contains(value.toString()))
                    {
                        length -= value.toString().length();
                    }
                }
                
                return length;
            }
            
            /**
             * Formats the lines for printing
             *
             * @param names        the names of each Monster
             * @param hp           The HP ratio of each Monster
             * @param atkBar       The attack bar ratio of each Monster
             * @param buffs        The buffs for each Monster
             * @param debuffs      The debuffs for each Monster
             * @param otherEffects Other effects for each Monster
             * @return The formatted lines
             */
            public static String formatLines(ArrayList<String> names, ArrayList<Double> hp, ArrayList<Double> atkBar, ArrayList<ArrayList<Buff>> buffs, ArrayList<ArrayList<Debuff>> debuffs, ArrayList<ArrayList<Effect>> otherEffects)
            {
                //Initialize lines
                ArrayList<String> firstLineInfo = new ArrayList<>();
                ArrayList<String> secondLineInfo = new ArrayList<>();
                ArrayList<String> thirdLineInfo = new ArrayList<>();
                ArrayList<String> fourthLineInfo = new ArrayList<>();
                
                //Add each Monster's basic info
                for (int i = 0; i < names.size(); i++)
                {
                    if (names.get(i).contains(ConsoleColor.BLACK.toString())) //Add dead Monster
                    {
                        firstLineInfo.add(names.get(i));
                    }
                    else //Add living Monster
                    {
                        firstLineInfo.add("%s (%sHp = %s%%%s, %sAttack Bar = %s%%%s)".formatted(names.get(i), ConsoleColor.GREEN, hp.get(i), ConsoleColor.RESET, ConsoleColor.CYAN, atkBar.get(i), ConsoleColor.RESET));
                    }
                }
                
                //Add each Monster's buffs
                for (int i = 0; i < names.size(); i++)
                {
                    secondLineInfo.add(buffs.get(i) == null ? "" : "Buffs: %s".formatted(buffs.get(i)));
                }
                
                //Add each Monster's debuffs
                for (int i = 0; i < names.size(); i++)
                {
                    thirdLineInfo.add(debuffs.get(i) == null ? "" : "Debuffs: %s".formatted(debuffs.get(i)));
                }
                
                //Add each Monster's other effects
                int count = 0;
                for (int i = 0; i < names.size(); i++)
                {
                    fourthLineInfo.add(otherEffects.get(i) == null ? "" : "Other Effects: %s (%d)".formatted(otherEffects.get(i), count));
                    count++;
                }
                
                //Space the lines properly and return the result
                return CONSOLE_INTERFACE.OUTPUT.toStringSpacing(firstLineInfo, secondLineInfo, thirdLineInfo, fourthLineInfo) + ConsoleColor.RESET;
            }
            
            /**
             * Displays an error message in the console with details about the encountered exception
             * and the problematic line in the file being processed.
             *
             * @param e       The exception that occurred while processing the file
             * @param line    The problematic line of text being processed
             * @param lineNum The line number in the file where the error occurred
             * @param lastI   The index of the last processed element before the error occurred
             */
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
                StringBuilder errorMsg = new StringBuilder();
                int lineLength = STRINGS.substringUpToString(line, list[lastI]).length();
                if (msg.contains("line length"))
                {
                    lineLength = line.length() - list[list.length - 1].length();
                }
                errorMsg.append(" ".repeat(Math.max(0, lineLength)));
                System.err.println(errorMsg + "^");
            }
            
            /**
             * Prints a formatted string with the specified color to the console.
             *
             * @param format The format string following standard printf syntax.
             * @param color  The color code to apply to the output.
             * @param args   The arguments referenced by the format specifiers in the format string.
             */
            public static void printfWithColor(String format, ConsoleColor color, Object... args)
            {
                System.out.println(STRINGS.formatWithColor(format, color, args));
            }
        }
        
        /**
         * The INPUT subclass provides methods to facilitate and validate user input and interaction
         * during a game session.
         */
        public static class INPUT
        {
            /**
             * Gets the ability number from the user
             *
             * @param next The Monster whose turn it is
             * @return The valid number the user selects
             */
            public static int getAbilityNum(Monster next)
            {
                int abilityNum;
                do
                {
                    //Get ability number
                    System.out.println("Type the ability number you want to use (e.g. 1,2...) or type \"effects\" to see effect descriptions");
                    try
                    {
                        abilityNum = scan.nextInt();
                        //Make sure the chosen ability is valid
                        if (!next.abilityIsValid(abilityNum))
                        {
                            System.out.println("Oops! You can not use this ability, it is automatically applied\n");
                            abilityNum = -1;
                        }
                    }
                    catch (InputMismatchException e)
                    {
                        String response = scan.nextLine();
                        //Print buff and debuff descriptions
                        abilityNum = -1;
                        if (response.equals("effects"))
                        {
                            EFFECTS.printStatDescriptions();
                        }
                    }
                    //Ability not found
                    catch (IndexOutOfBoundsException e)
                    {
                        abilityNum = -1;
                    }
                }
                while (!next.abilityIsValid(abilityNum));
                return abilityNum;
            }
            
            /**
             * Gets the target the user wants to attack/heal
             *
             * @param next       The Monster whose turn it is.
             * @param abilityNum The ability number the user has chosen
             * @param threat     Whether a Monster has the Threat buff
             * @return The target Monster
             */
            public static Monster getTarget(Monster next, int abilityNum, boolean threat)
            {
                Team other = Monster.getGame().getOtherTeam(), teamWithHighestAtkBar = Monster.getGame().getTeamWithHighestAtkBar();
                
                //Get target num/re-choose ability if wanted
                int target;
                boolean cancel = false;
                Monster monster = null;
                Team targetTeam = (next.getAbility(abilityNum).targetsEnemy()) ? other : teamWithHighestAtkBar;
                do
                {
                    System.out.println("\nChoose target (\"c\" to choose ability again)");
                    //Enemy Monster has Threat buff and current ability targets enemy
                    if (threat && next.getAbility(abilityNum).targetsEnemy())
                    {
                        System.out.println(other.getSingleMonFromTeam(other.getMonWithThreat(), false));
                        monster = other.getMonWithThreat();
                    }
                    //The enemy does not have any Threat buffs and ability targets enemy
                    else if (next.getAbility(abilityNum).targetsEnemy())
                    {
                        System.out.printf("%s\n", other.print(next.getElement(), 1));
                    }
                    //Ability targets self
                    else if (next.getAbility(abilityNum).targetsSelf())
                    {
                        System.out.println(teamWithHighestAtkBar.getSingleMonFromTeam(next, true));
                        monster = next;
                    }
                    //Ability targets allied Team
                    else
                    {
                        System.out.printf("%s\n%n", teamWithHighestAtkBar.print(next.getElement(), 0));
                    }
                    try
                    {
                        target = scan.nextInt();
                    }
                    //Cancel operation
                    catch (InputMismatchException e)
                    {
                        String s = scan.nextLine();
                        target = -1;
                        cancel = s.equals("c");
                        if (cancel)
                        {
                            break;
                        }
                    }
                }
                while (!targetTeam.viableNums(next.getAbility(abilityNum).targetsSelf() || (threat && next.getAbility(abilityNum).targetsEnemy()), monster).contains(target));
                if (cancel)
                {
                    return null;
                }
                
                //Get and return target Monster
                return next.getAbility(abilityNum).targetsEnemy() ? other.get(target) : teamWithHighestAtkBar.get(target);
            }
            
            /**
             * Allows the user to either play with preset Teams or pick the Monsters for each Team
             *
             * @return The two Teams chosen by the user
             */
            public static ArrayList<ArrayList<Monster>> selectTeams()
            {
                ArrayList<ArrayList<Monster>> teams = new ArrayList<>();
                
                //Create team ArrayLists
                ArrayList<Monster> mons1 = new ArrayList<>();
                ArrayList<Monster> mons2 = new ArrayList<>();
                
                //Skips monster selection and starts game with pre-built Teams if user answers "y"
                String test;
                if (!Monster.MONSTER_NAMES_DATABASE.isEmpty())
                {
                    System.out.println("Preset teams? (Type \"y\" for yes)");
                    test = scan.nextLine();
                }
                else
                {
                    test = "y";
                }
                
                //Preset teams (Mainly for testing/debugging)
                if (test.equalsIgnoreCase("y"))
                {
                    //Team 1
                    mons1.add(new Dominic());
                    mons1.add(new Loren());
                    mons1.add(new Alice());
                    mons1.add(new Feng_Yan());
                    
                    //Team 2
                    mons2.add(new Ariel());
                    mons2.add(new Dominic());
                    mons2.add(new Riley());
                    mons2.add(new Evan());
                }
                
                //4v4
                //Choose monsters 5 monsters for each team then ban 1
                if (!test.equals("y"))
                {
                    //Pick mons
                    while (mons1.size() < 5 || mons2.size() < 5)
                    {
                        String inputMon;
                        //Team 1 pick
                        if (mons1.size() < 5 && mons1.size() == mons2.size())
                        {
                            do
                            {
                                //Print potential Monsters
                                OUTPUT.printMonsToPick(mons1);
                                
                                //Print current team
                                System.out.print("\nTeam 1 current team: ");
                                for (Monster mon : mons1)
                                {
                                    System.out.printf("%s, ", mon.getName(true, false));
                                }
                                
                                //Get the next Monster's name
                                System.out.println("\nTeam 1 choose your monster (type \"inspect\" to inspect a monster)");
                                inputMon = scan.nextLine();
                                
                                //Inspect monster
                                if (inputMon.equals("inspect"))
                                {
                                    MONSTERS.inspect();
                                }
                            }
                            while (!MONSTERS.stringIsMonsterName(inputMon) || TEAMS.teamHasMon(inputMon, mons1));
                            
                            //Attempt to add the Monster to the team
                            if (!Main.addMonToTeam(mons1, inputMon))
                            {
                                scan.nextLine();
                                continue;
                            }
                        }
                        
                        //Team 2 pick
                        if (mons2.size() < 5 && mons1.size() == mons2.size() + 1)
                        {
                            do
                            {
                                //Print potential Monsters
                                OUTPUT.printMonsToPick(mons2);
                                
                                //Print current team
                                System.out.print("\nTeam 2 current team: ");
                                for (Monster mon : mons2)
                                {
                                    System.out.printf("%s, ", mon.getName(true, false));
                                }
                                
                                //Get the next Monster's name
                                System.out.println("\nTeam 2 choose your monster (type \"inspect\" to inspect a monster)");
                                inputMon = scan.nextLine();
                                
                                //Inspect monster
                                if (inputMon.equals("inspect"))
                                {
                                    MONSTERS.inspect();
                                }
                            }
                            while (!MONSTERS.stringIsMonsterName(inputMon) || TEAMS.teamHasMon(inputMon, mons2));
                            
                            //Attempt to add the Monster to the team
                            if (!Main.addMonToTeam(mons2, inputMon))
                            {
                                scan.nextLine();
                            }
                        }
                    }
                    
                    //Ban one Monster from the other team
                    String banName;
                    //Team 2 choosing
                    do
                    {
                        System.out.println("Team 2 choose one monster from Team 1 to ban.");
                        
                        //Print team 1 Monsters
                        for (Monster mon : mons1)
                        {
                            System.out.printf("%s, ", mon.getName(true, false));
                        }
                        System.out.println();
                        
                        //Get input
                        banName = scan.nextLine();
                        
                        //Inspect
                        if (banName.equals("inspect"))
                        {
                            MONSTERS.inspect();
                        }
                    }
                    while (!MONSTERS.stringIsMonsterName(banName, mons1));
                    
                    //Remove Monster from team 1
                    mons1.remove(MONSTERS.getMonFromName(banName, mons1));
                    
                    //Team 1 choosing
                    do
                    {
                        System.out.println("Team 1 choose one monster from Team 2 to ban.");
                        
                        //Print team 2 Monsters
                        for (Monster mon : mons2)
                        {
                            System.out.printf("%s, ", mon.getName(true, false));
                        }
                        System.out.println();
                        
                        //Get input
                        banName = scan.nextLine();
                        
                        //Inspect
                        if (banName.equals("inspect"))
                        {
                            MONSTERS.inspect();
                        }
                    }
                    while (!MONSTERS.stringIsMonsterName(banName, mons2));
                    
                    //Remove Monster from team 2
                    mons2.remove(MONSTERS.getMonFromName(banName, mons2));
                }
                
                //Return final teams
                teams.add(mons1);
                teams.add(mons2);
                return teams;
            }
            
            /**
             * Allows the user to pick leaders for each Team if a leader is available
             *
             * @param t1 The first Team
             * @param t2 The second Team
             */
            public static void pickLeaders(Team t1, Team t2)
            {
                ArrayList<Monster> mons1 = new ArrayList<>();
                ArrayList<Monster> mons2 = new ArrayList<>();
                for (Monster mon : t1.getMonsters())
                {
                    if (mon.hasLeaderSkill())
                    {
                        mons1.add(mon);
                    }
                }
                for (Monster mon : t2.getMonsters())
                {
                    if (mon.hasLeaderSkill())
                    {
                        mons2.add(mon);
                    }
                }
                Team pickLeader1 = new Team("1", mons1);
                Team pickLeader2 = new Team("2", mons2);
                
                getLeader(pickLeader1, t1);
                getLeader(pickLeader2, t2);
            }
            
            /**
             * Gets the rune set number to apply to the Monster
             *
             * @return The rune set number to apply
             */
            public static int getRuneSetNum()
            {
                int runeSetNum = 0;
                do
                {
                    System.out.println("Type rune set number or \"d\" for default");
                    try
                    {
                        runeSetNum = Math.max((scan.nextInt()), 0);
                    }
                    catch (InputMismatchException e)
                    {
                        if (scan.nextLine().equals("d"))
                        {
                            runeSetNum = -1;
                        }
                    }
                }
                while (runeSetNum == 0);
                return runeSetNum;
            }
            
            /**
             * Prompt the user to select a leader Monster for the Team if possible, prints a message otherwise.
             *
             * @param potentialLeaders A list of potential leaders.
             * @param team             The Team to apply the leader skill to.
             */
            private static void getLeader(Team potentialLeaders, Team team)
            {
                //Do nothing if there are no available leaders
                if (!potentialLeaders.getMonsters().isEmpty())
                {
                    String inputMon;
                    //Get monster name
                    do
                    {
                        System.out.printf("Team %s pick a leader by typing their name (Type \"inspect\" to inspect a Monster or \"none\" for no leader)%n", potentialLeaders.getName());
                        System.out.println(potentialLeaders);
                        inputMon = scan.nextLine();
                        if (inputMon.equals("inspect"))
                        {
                            MONSTERS.inspect();
                        }
                        else if (inputMon.equals("none"))
                        {
                            return;
                        }
                    }
                    while (!MONSTERS.stringIsMonsterName(inputMon, potentialLeaders.getMonsters()));
                    //Get leader
                    Monster leader = MONSTERS.getMonFromName(inputMon, potentialLeaders.getMonsters());
                    //Apply leader skill to the team
                    leader.applyLeaderSkill(team);
                }
                else
                {
                    System.out.println("No leader skill available.");
                }
            }
            
            /**
             * Prompts the user with a specified message and repeatedly requests input until a valid response is provided.
             *
             * @param msg            The message to display to the user.
             * @param validResponses A variable-length list of valid responses that the user can input. Must have at least one element and all values must be lowercase.
             * @return The valid user input that matches one of the specified valid responses.
             */
            public static String getSpecificString(String msg, String... validResponses)
            {
                if (validResponses.length == 0)
                {
                    System.err.println("Error getting input, no valid responses specified.");
                    return null;
                }
                String response;
                do
                {
                    System.out.println(msg);
                    response = scan.nextLine();
                }
                while (!Arrays.asList(validResponses).contains(response.toLowerCase()));
                
                return response;
            }
        }
        
        /**
         * Allows the user to look at the teams in more detail
         *
         * @param teams A list of the teams to use.
         */
        public static void pauseMenu(ArrayList<Team> teams)
        {
            final ArrayList<Team> finalTeams = new ArrayList<>(teams);
            ArrayList<Team> tempTeams = new ArrayList<>(finalTeams);
            FILTER_AND_SORT.sortTeams(tempTeams, true, "wins");
            outer:
            while (true)
            {
                if (teams.isEmpty())
                {
                    teams = new ArrayList<>(finalTeams);
                }
                System.out.println("Enter a command or type \"help\" for a list of commands.");
                String input = scan.nextLine();
                try //Single teams at an index
                {
                    boolean neg = input.contains("-");
                    int index = Math.abs(Integer.parseInt(input));
                    
                    //Start from the end of the list if the user entered with a negative number
                    if (neg)
                    {
                        Collections.reverse(tempTeams);
                    }
                    
                    //Get team at requested index
                    Team team = tempTeams.get(index);
                    
                    //Flip the list again if the user entered a negative number
                    if (neg)
                    {
                        Collections.reverse(tempTeams);
                    }
                    
                    OUTPUT.printSingleTeamStats(team);
                }
                catch (IndexOutOfBoundsException e) //Invalid index
                {
                    System.out.printf("Index out of bounds, please enter a number between 0 and %,d inclusive%n", tempTeams.size() - 1);
                }
                catch (NumberFormatException e)
                {
                    switch (switch (input.toLowerCase())
                    {
                        //Find a specific team
                        case "inspect" ->
                        {
                            //Get the index of the requested team
                            int index = TEAMS.findTeamFromMonsters(new ArrayList<>(), tempTeams);
                            
                            //Team not found
                            if (index == -1)
                            {
                                yield 1;
                            }
                            
                            //Print team
                            Team inspectTeam = tempTeams.get(index);
                            for (Monster mon : inspectTeam.getMonsters())
                            {
                                System.out.printf("%s\t\t", mon.getName(true, false));
                            }
                            //Print team info
                            System.out.printf("Number of wins: %,d\tNumber of losses: %,d\tPlace: %,d%n", inspectTeam.getWins(), inspectTeam.getLosses(), index);
                            yield 1;
                        }
                        //Order the teams
                        case "order" ->
                        {
                            int reversed = -1;
                            String sortOption = "";
                            
                            //Get the value to sort by
                            while (sortOption.isEmpty())
                            {
                                System.out.println("Type \"wins\" to sort by wins, \"losses\" to sort by losses, \"ratio\" to sort by win/loss ratio, or \"back\" to go back");
                                String sortString = scan.nextLine();
                                
                                sortOption = switch (sortString.toLowerCase())
                                {
                                    case "wins", "losses", "ratio", "back" -> sortString;
                                    default ->
                                    {
                                        System.out.println("Please enter a valid response");
                                        yield "";
                                    }
                                };
                                if (sortOption.equals("back"))
                                {
                                    yield 1;
                                }
                            }
                            
                            //Get the sort order
                            while (reversed == -1)
                            {
                                System.out.println("Type \"normal\" to sort highest to lowest, \"reversed\" to sort lowest to highest, or \"back\" to go back");
                                String reverseInput = scan.nextLine();
                                
                                switch (reverseInput.toLowerCase())
                                {
                                    case "reversed" -> reversed = 0;
                                    case "normal" -> reversed = 1;
                                    case "back" ->
                                    {
                                    }
                                    default -> System.out.println("Please enter a valid input");
                                }
                                if (reverseInput.equalsIgnoreCase("back")) //Cancel operation
                                {
                                    yield 1;
                                }
                            }
                            
                            //Sort the teams as requested
                            FILTER_AND_SORT.sortTeams(tempTeams, reversed == 1, sortOption);
                            yield 1;
                        }
                        //Filter the results
                        case "filter" ->
                        {
                            tempTeams = new ArrayList<>(finalTeams);
                            tempTeams = FILTER_AND_SORT.filterTeams(tempTeams);
                            yield 1;
                        }
                        //Order Monsters by their average placing
                        case "monsters" ->
                        {
                            ArrayList<Pair<Monster, Integer>> sortMonsByPlace = FILTER_AND_SORT.sortMonsByPlace(tempTeams);
                            if (sortMonsByPlace == null)
                            {
                                yield 1;
                            }
                            
                            //Print each monster
                            for (int i = 0; i < sortMonsByPlace.size(); i++)
                            {
                                Pair<Monster, Integer> p = sortMonsByPlace.get(i);
                                Monster m = p.getFirst();
                                System.out.printf("%d. %s (Avg place: %,d)%n", i + 1, m.getName(true, false), p.getSecond());
                            }
                            yield 1;
                        }
                        case "help" ->
                        {
                            OUTPUT.printPauseCommands();
                            yield 1;
                        }
                        case "save" ->
                        {
                            Object[] v = Auto_Play.getProgressInfo();
                            String fileName = TEAMS.exportResults((int) v[0], (int) v[1], (boolean) v[2], teams, (StopWatch) v[3], (StopWatch) v[4]);
                            
                            if (fileName == null)
                            {
                                printfWithColor("Error saving teams.\n", ConsoleColor.RED);
                            }
                            else
                            {
                                printfWithColor("Results exported to \"%s\"\n", ConsoleColor.GREEN, fileName);
                            }
                            
                            yield 1;
                        }
                        //Exit
                        case "exit" -> -1;
                        case "quit" ->
                        {
                            System.exit(0);
                            yield 1;
                        }
                        default -> 0;
                    })
                    {
                        case -1 ->
                        {
                            break outer;
                        }
                        case 1 ->
                        {
                            continue;
                        }
                        default ->
                        {
                        }
                    }
                    
                    //Range of teams
                    if (input.contains("-"))
                    {
                        //Remove whitespace
                        input = input.replaceAll(" ", "");
                        try
                        {
                            //Check if the first number is negative and remove the first dash if it is to properly parse number
                            boolean firstNeg = input.startsWith("-");
                            if (firstNeg)
                            {
                                input = input.substring(1);
                            }
                            //Get range
                            int firstNum = Integer.parseInt(Util.STRINGS.substringUpToString(input, "-"));
                            int secondNum = Integer.parseInt(input.substring(input.indexOf("-") + 1));
                            
                            //Make the first number negative if needed
                            if (firstNeg)
                            {
                                firstNum *= -1;
                            }
                            if (secondNum > tempTeams.size())
                            {
                                System.out.println("Please enter a valid range");
                                continue;
                            }
                            
                            //Start is positive
                            if (firstNum >= 0)
                            {
                                //Get end value if input is negative
                                if (secondNum < 0)
                                {
                                    secondNum = tempTeams.size() + secondNum - 1;
                                }
                                
                                //End value is less than starting value (Invalid range)
                                if (firstNum > secondNum)
                                {
                                    System.out.println("Please enter a valid range");
                                    continue;
                                }
                                
                                //Print each team in the range
                                for (int i = firstNum; i < secondNum; i++)
                                {
                                    OUTPUT.printSingleTeamStats(tempTeams.get(i));
                                }
                            }
                            //Start is negative
                            else
                            {
                                //End is positive (Invalid range)
                                if (secondNum > 0)
                                {
                                    System.out.println("Please enter a valid range");
                                    continue;
                                }
                                
                                //End is negative
                                firstNum = tempTeams.size() + firstNum - 1;
                                secondNum = tempTeams.size() + secondNum - 1;
                                //End value is less than the starting value (Invalid range)
                                if (firstNum > secondNum)
                                {
                                    System.out.println("Please enter a valid range");
                                    continue;
                                }
                                
                                //Print each team in the range
                                for (int i = firstNum; i < secondNum; i++)
                                {
                                    OUTPUT.printSingleTeamStats(tempTeams.get(i));
                                }
                            }
                        }
                        catch (NumberFormatException error) //Invalid range
                        {
                            System.out.printf("Please enter a valid range (min of 0, max of %,d)%n", tempTeams.size() - 1);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * The RUNES subclass provides methods for handling operations related to Runes in the context of the game.
     */
    public static class RUNES
    {
        /**
         * Converts rune information into a CSV formatted string. The method processes rune type,
         * main attribute, main attribute amount, and sub-attributes to build the CSV representation.
         *
         * @param type       The numerical value representing the type of the rune.
         * @param mainName   The name of the main attribute of the rune.
         * @param mainAmount The amount/value of the main attribute.
         * @param subs       A comma-separated string of sub-attributes, where each sub-attribute
         *                   alternates between attribute name and its value.
         * @return A CSV formatted string containing the rune type, main attribute, main amount,
         * and all sub-attributes. Each field is separated by a comma.
         * @throws RuntimeException If an unknown main attribute, type, or sub-attribute is encountered.
         */
        public static String runeToCSV(int type, String mainName, int mainAmount, String subs)
        {
            //Get the main attribute number
            int mainNum = RuneAttribute.stringToAttribute(mainName).getNum();
            
            //Unknown attribute or type
            if (mainNum == -1 || type == -1)
            {
                throw new RuntimeException("Can not find Main Attribute or type, %s".formatted(String.format("Main Attribute: %s, type: %s", mainName, RuneType.numToType(type))));
            }
            
            //Format sub attributes
            ArrayList<String> subAttributes = new ArrayList<>(Arrays.asList(subs.split(",")));
            
            ArrayList<Integer> subNums = new ArrayList<>();
            int count = 0;
            if (subAttributes.size() != 1)
            {
                for (String sub : subAttributes)
                {
                    //Attempt to add attribute number
                    if (count % 2 == 0)
                    {
                        int temp = RuneAttribute.stringToAttribute(sub.substring(5)).getNum();
                        if (temp == -1)
                        {
                            throw new RuntimeException("Can not find Sub Attribute: %s".formatted(sub));
                        }
                        subNums.add(temp);
                    }
                    else //Add attribute amount
                    {
                        subNums.add(Integer.parseInt(sub));
                    }
                    count++;
                }
            }
            
            //Add the rune type and main attribute
            StringBuilder s = new StringBuilder(String.format("%d,%d,%d", type, mainNum, mainAmount));
            
            //Add the sub attributes
            for (Integer num : subNums)
            {
                s.append(String.format(",%d", num));
            }
            s.append("\n");
            
            return s.toString();
        }
        
        /**
         * Converts a given Rune object into a CSV formatted string. The CSV string includes information
         * such as the rune's type, main attribute, main attribute amount, and all sub-attributes with their
         * corresponding values.
         *
         * @param rune The Rune object containing type, main attribute, main attribute amount, and a list of sub-attributes.
         * @return A CSV formatted string representing the rune's details, with fields separated by commas.
         */
        public static String runeToCSV(Rune rune)
        {
            //Get the rune type and main attribute
            int type = rune.getType().getNum();
            int mainNum = rune.getMainAttribute().getAttribute().getNum();
            int mainAmount = rune.getMainAttribute().getAmount();
            
            //Add the rune type and main attribute
            StringBuilder s = new StringBuilder(String.format("%d,%d,%d", type, mainNum, mainAmount));
            
            //Add each sub attribute
            for (SubAttribute subAttribute : rune.getSubAttributes())
            {
                s.append(String.format(",%d,%d", subAttribute.getAttribute().getNum(), subAttribute.getAmount()));
            }
            
            s.append("\n");
            
            return s.toString();
        }
        
        /**
         * Loads a rune set from memory
         *
         * @param f        The file to load from
         * @param setToMon The Monster to set the Runes to
         * @return The ArrayList of Runes
         */
        public static ArrayList<Rune> getRunesFromFile(File f, Monster setToMon)
        {
            try
            {
                Scanner read = new Scanner(f);
                ArrayList<Rune> runes = new ArrayList<>();
                int place = 1;
                
                //Read each line
                while (read.hasNextLine())
                {
                    String[] runeInfo = read.nextLine().split(",");
                    //Get rune info
                    RuneType type = RuneType.numToType(Integer.parseInt(runeInfo[0]));
                    int mainAttNum = Integer.parseInt(runeInfo[1]);
                    int mainAttAmount = Integer.parseInt(runeInfo[2]);
                    MainAttribute mainAttribute = new MainAttribute(RuneAttribute.numToAttribute(mainAttNum), mainAttAmount);
                    ArrayList<SubAttribute> subs = new ArrayList<>();
                    //Get each sub-attribute
                    for (int i = 3; i < runeInfo.length; i += 2)
                    {
                        int subNum = Integer.parseInt(runeInfo[i]);
                        int subAmount = Integer.parseInt(runeInfo[i + 1]);
                        subs.add(new SubAttribute(RuneAttribute.numToAttribute(subNum), subAmount));
                    }
                    runes.add(new Rune(type, mainAttribute, place, subs, setToMon));
                    place++;
                }
                read.close();
                
                return runes;
            }
            catch (FileNotFoundException e)
            {
                System.err.printf("Rune file not found for %s%n", setToMon.getName(true, false));
                return null;
            }
            catch (NumberFormatException e)
            {
                System.err.printf("Error reading file for %s%n", setToMon.getName(true, false));
                return null;
            }
        }
        
        /**
         * Loads a rune set from memory.
         *
         * @param fileName The name of the file to load from. Should contain only the name, not the path
         * @param setToMon The Monster to set the Runes to
         * @return The ArrayList of Runes
         */
        public static ArrayList<Rune> getRunesFromFile(String fileName, Monster setToMon)
        {
            //Open the file and read the contents
            return getRunesFromFile(new File(MonsterRunes.path + "/" + fileName), setToMon);
        }
    }
    
    /**
     * The GUIS subclass provides utility methods for GUI manipulation.
     */
    public static class GUIS
    {
        /**
         * Adds all the rune attributes to the given JComboBox
         *
         * @param selector The JComboBox to add the attributes to
         */
        public static void addAllAttributes(JComboBox<String> selector)
        {
            //Remove all previous items then add all the attributes
            selector.removeAllItems();
            for (RuneAttribute value : RuneAttribute.values())
            {
                if (value == RuneAttribute.NONE)
                {
                    continue;
                }
                selector.addItem(value.toString());
            }
        }
        
        /**
         * Adds all the rune types to the given JComboBox
         *
         * @param selector The JComboBox to add the types to
         */
        public static void addAllTypes(JComboBox<String> selector)
        {
            //Remove all previous items then add all the attributes
            selector.removeAllItems();
            for (RuneType value : RuneType.values())
            {
                if (value == RuneType.NONE)
                {
                    continue;
                }
                selector.addItem(value.toString());
            }
        }
    }
    
    /**
     * The FILES subclass provides methods for managing and manipulating program files.
     */
    public static class FILES
    {
        /**
         * The list of all rune files
         */
        private static final List<File> runeSets = Arrays.stream(Objects.requireNonNull(new File(MonsterRunes.path).listFiles())).filter(file -> file.getName().contains(".csv")).toList();
        
        /**
         * Retrieves the list of rune set files.
         *
         * @return A list of File objects representing rune sets.
         */
        public static List<File> getRuneSets()
        {
            return runeSets;
        }
        
        /**
         * Edits the chosen line in the chosen rune file. It does this by writing to a temporary file, saving the original to a different name,
         * changing the new file's name to the requested name, and deletes the original file.
         *
         * @param fileName The name of the file to edit. Should end in ".csv"
         * @param lineNum  The line number to edit
         * @param newRune  The new Rune to replace the old one with
         * @return True if and only if the file was successfully edited
         */
        public static boolean editFile(String fileName, int lineNum, Rune newRune)
        {
            File newFile = null;
            try
            {
                //Create a temp file to put new information into
                newFile = new File("%s/tempFile.csv".formatted(MonsterRunes.path));
                FileWriter newFileWriter = new FileWriter(newFile);
                //Get the old rune file
                File oldFile = new File("%s/%s".formatted(MonsterRunes.path, fileName));
                Scanner read = new Scanner(oldFile);
                
                //Write lines to the new file
                for (int i = 1; read.hasNextLine(); i++)
                {
                    //Write the edited rune to the temp file if it is the current rune
                    if (i == lineNum)
                    {
                        newFileWriter.write(RUNES.runeToCSV(newRune));
                        read.nextLine();
                    }
                    else //Write old rune to the temp file
                    {
                        newFileWriter.write("%s\n".formatted(read.nextLine()));
                    }
                }
                //Close reader and writer
                newFileWriter.close();
                read.close();
                
                //Try to rename the old file to a temporary name
                if (oldFile.renameTo(new File("%s/oldTempFile.csv".formatted(MonsterRunes.path))))
                {
                    //Attempt to rename the new file to the original file name
                    if (newFile.renameTo(new File("%s/%s".formatted(MonsterRunes.path, fileName))))
                    {
                        //Delete the original file ****DO NOT REMOVE****
                        File temp = new File("%s/oldTempFile.csv".formatted(MonsterRunes.path));
                        temp.delete();
                        return true;
                    }
                    else //Unable to rename the new file
                    {
                        //Rename the old file to its original name
                        oldFile.renameTo(new File("%s/%s".formatted(MonsterRunes.path, fileName)));
                        return false;
                    }
                }
                else //Unable to rename the original file
                {
                    //Delete the temp file
                    newFile.delete();
                    return false;
                }
            }
            catch (Exception e) //Something went wrong
            {
                //Delete the old file if it exists
                if (newFile != null)
                {
                    newFile.delete();
                }
                return false;
            }
        }
        
        /**
         * Tests whether a given String is a valid file name
         *
         * @param fileName The text to check
         * @param action   The requested action from the user
         * @return True if the text is a valid file name in the Runes/Monster_Runes directory, false otherwise
         */
        public static boolean isValidFileName(String fileName, Runes.FileAction action)
        {
            //Checks if the name exists and does not contain "temp" in it
            if (fileName == null || fileName.contains("temp"))
            {
                return false;
            }
            
            //Create
            if (action == Runes.FileAction.CREATE)
            {
                //Make sure the passed name is not the name of an already existing file
                for (File runeSet : runeSets)
                {
                    if (runeSet.getName().equals(fileName))
                    {
                        return false;
                    }
                }
                return true;
            }
            else //All other actions
            {
                //Make sure the passed name is the name of an already existing file
                for (File runeSet : runeSets)
                {
                    if (runeSet.getName().equals(fileName))
                    {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        /**
         * Calls {@link GetNameAndNum} to get the file name from the user
         *
         * @return The file name as given by the user
         */
        public static String getFileName()
        {
            //Get the name and rune set number from the user
            GetNameAndNum nameAndNum = new GetNameAndNum(new Runes());
            
            //Prevent this function from continuing while the user is entering the information
            while (nameAndNum.isVisible())
            {
                pause(5);
            }
            
            //Get the proper Monster name
            String monName = MONSTERS.toProperName(nameAndNum.monNameText.getText());
            
            //Try to set the rune number
            int runeSetNum = 0;
            try
            {
                runeSetNum = Integer.parseInt(nameAndNum.runeSetNumText.getText());
            }
            catch (NumberFormatException e) //Unable to parse the input to an int
            {
                System.err.println("Unable to read the rune set number");
                System.exit(1);
            }
            
            //Return the formatted file name
            return "%s%d.csv".formatted(monName, runeSetNum);
        }
        
        /**
         * Duplicates the requested file
         *
         * @param oldFileName The name of the file to duplicate
         * @param newFileName The duplicated file's new name
         * @return True if and only if the file was successfully duplicated
         */
        public static boolean duplicateFile(String oldFileName, String newFileName)
        {
            //Make sure file names are not the same
            if (oldFileName.equalsIgnoreCase(newFileName))
            {
                return false;
            }
            //Make sure the new file name is valid
            if (!FILES.isValidFileName(newFileName, Runes.FileAction.CREATE) || !FILES.isValidFileName(oldFileName, Runes.FileAction.DUPLICATE))
            {
                return false;
            }
            try
            {
                //Write every line from the old file to the new one
                Scanner oldFile = new Scanner(new File("%s/%s".formatted(MonsterRunes.path, oldFileName)));
                
                File newFile = new File("%s/%s".formatted(MonsterRunes.path, newFileName));
                
                FileWriter newFileWriter = new FileWriter(newFile);
                while (oldFile.hasNextLine())
                {
                    newFileWriter.write("%s\n".formatted(oldFile.nextLine()));
                }
                //Close the file reader
                oldFile.close();
                //Close the file writer
                newFileWriter.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
            return true;
        }
        
        /**
         * Formats a file name by combining a properly formatted name and a numeric identifier.
         *
         * @param name The base name to be formatted. Typically, a name representing a monster.
         * @param num  An integer value representing the identifier to be appended to the name.
         * @return A formatted file name in the form "{name}{num}.csv".
         */
        public static String formatFileName(String name, int num)
        {
            //Get the proper Monster name
            String monName = MONSTERS.toProperName(name);
            
            //Try to set the rune number
            int runeSetNum = 0;
            try
            {
                runeSetNum = num;
            }
            catch (NumberFormatException e) //Unable to parse the input to an int
            {
                System.err.println("Unable to read the rune set number");
                System.exit(1);
            }
            
            //Return the formatted file name
            return "%s%d.csv".formatted(monName, runeSetNum);
        }
        
        /**
         * Reads a file containing team and monster information, and processes its content to create a list of teams.
         * The file should follow a specific format, where the first line contains a mapping of monster names to unique keys,
         * and subsequent lines contain team details including monsters and win/loss counts.
         *
         * @param chosenFile    The file to be read and processed. Must be readable and follow the expected format.
         * @param prioritizeSpd A flag indicating how the file should be processed. If true, each Team will have a unique Monster dynamically created for said Team.
         *                      If false, there will be one instance per Monster and the Teams will share that one instance.
         * @return A list of teams constructed based on the file content, or null if an error occurs.
         */
        public static ArrayList<Team> readFile(File chosenFile, boolean prioritizeSpd)
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
                HashMap<Integer, Monster> library = new HashMap<>();
                line = read.nextLine();
                for (String s : line.split(","))
                {
                    lastI++;
                    String name = s.split(":")[0];
                    int key = Integer.parseInt(s.split(":")[1]);
                    Monster m = MONSTERS.createNewMonFromName(name, false);
                    if (m == null)
                    {
                        throw new InvalidClassException("Monster " + name + " not found");
                    }
                    library.put(key, Util.MONSTERS.createNewMonFromName(name, false));
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
                        String name = library.get(Integer.parseInt(list[i])).getName(false, false);
                        if (name == null)
                        {
                            throw new InvalidClassException("Unknown monster");
                        }
                        teamMonsters.add((prioritizeSpd) ? MONSTERS.createNewMonFromName(name, false) : library.get(Integer.parseInt(list[i])));
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
                CONSOLE_INTERFACE.OUTPUT.displayErrorMessage(e, line, lineNum, lastI);
                return null;
            }
        }
    }
    
    /**
     * The EFFECTS subclass provides utilities for managing and displaying information
     * about buffs and debuffs along with their descriptions. These effects are
     * read from pre-defined resources and printed with proper formatting.
     */
    public static class EFFECTS
    {
        /**
         * Prints all buffs and debuffs and their descriptions
         */
        public static void printStatDescriptions()
        {
            try
            {
                //Print the beginning message
                System.out.printf("%sBuffs:%s%s%sDebuffs:%s%n%n", ConsoleColor.BLUE, ConsoleColor.RESET, statSpacing("Buffs:".length()), ConsoleColor.RED, ConsoleColor.RESET);
                //Read from the buff and debuff keys
                Scanner readBuffs = new Scanner(Objects.requireNonNull(Buff.class.getResourceAsStream("Buff key.csv")));
                Scanner readDebuffs = new Scanner(Objects.requireNonNull(Debuff.class.getResourceAsStream("Debuff key.csv")));
                String buffName;
                String buffDescription;
                String debuffName;
                String debuffDescription;
                while (readBuffs.hasNextLine() || readDebuffs.hasNextLine())
                {
                    //Get the next buff to print
                    String[] buff = readBuffs.nextLine().split(",");
                    for (int i = 0; i < buff.length; i++)
                    {
                        buff[i] = buff[i].replaceAll(";", ",");
                    }
                    
                    try
                    {
                        //Get the next debuff to print
                        String[] debuff = readDebuffs.nextLine().split(",");
                        for (int i = 0; i < debuff.length; i++)
                        {
                            debuff[i] = debuff[i].replaceAll(";", ",");
                        }
                        //Get the name and description of the buff and debuff
                        buffName = buff[1];
                        int buffNameHolder = buffName.length();
                        buffDescription = buff[3];
                        debuffName = debuff[1];
                        debuffDescription = debuff[3];
                        int lastLineBreak = 0;
                        
                        //Print something else if there is no description
                        if (debuffDescription.equals("null"))
                        {
                            throw new NoSuchElementException();
                        }
                        
                        //Print part of the buff description on the next line if it is too long (More than 95 characters)
                        if (buffDescription.length() > 95)
                        {
                            StringBuilder s = new StringBuilder();
                            int count = 0;
                            boolean breakLine = false;
                            for (int i = 0; i < buffDescription.length(); i++)
                            {
                                //New line every 70ish characters
                                if (i != 0 && i % 70 == 0)
                                {
                                    breakLine = true;
                                }
                                //Break the line only on whitespace
                                if (breakLine && buffDescription.charAt(i) == ' ')
                                {
                                    //Indent the next line
                                    s.append(" ".repeat(Math.max(0, buffName.length() + 1)));
                                    
                                    breakLine = false;
                                    lastLineBreak = count;
                                }
                                count++;
                            }
                            //Print each buff and debuff description. (1 each per line)
                            //Format:
                            //<buff name>: <buff description>    <debuff name>: <debuff description>
                            System.out.printf("""
                                            %s%s:%s %s%s%s%s:%s %s%s
                                            %s%s%s
                                            
                                            """, ConsoleColor.BLUE, buffName, ConsoleColor.CYAN, buffDescription.substring(0, lastLineBreak), ConsoleColor.RED,
                                    statSpacing(buffName.length() + 2 + buffDescription.substring(0, lastLineBreak).length()), debuffName, ConsoleColor.YELLOW, debuffDescription, ConsoleColor.CYAN, s, buffDescription.substring(lastLineBreak), ConsoleColor.RESET);
                        }
                        //Print the effect descriptions normally
                        else
                        {
                            System.out.printf("%s%s:%s %s%s%s%s:%s %s%s%n%n", ConsoleColor.BLUE, buffName, ConsoleColor.CYAN, buffDescription,
                                    ConsoleColor.RED, statSpacing(buffNameHolder + 2 + buffDescription.substring(lastLineBreak).length()), debuffName,
                                    ConsoleColor.YELLOW, debuffDescription, ConsoleColor.RESET);
                        }
                    }
                    catch (NoSuchElementException e)
                    {
                        buffName = buff[1];
                        buffDescription = buff[3];
                        //Don't print the effect if it has no description
                        if (!buffDescription.equals("null"))
                        {
                            System.out.printf("%s%s:%s %s%s%n%n", ConsoleColor.BLUE, buffName, ConsoleColor.CYAN, buffDescription, ConsoleColor.RESET);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Formats the spacing needed to print the stats correctly
         *
         * @param length The length of the String already printed
         * @return The correct number of spaces to print
         */
        private static String statSpacing(int length)
        {
            
            //The number of spaces is equal to 100 minus the length of the String
            return " ".repeat(Math.max(0, 100 - length));
        }
    }
    
    /**
     * The FILTER_AND_SORT subclass provides methods for filtering and sorting Monsters and Teams.
     */
    public static class FILTER_AND_SORT
    {
        /**
         * Allows the user to whitelist or blacklist Monsters
         *
         * @param mons           The original set of Monsters
         * @param limitSelection True if method should check if user selected a valid number of Monsters, false otherwise
         * @return The new list of Monsters
         */
        public static ArrayList<Monster> filterMonsters(ArrayList<Monster> mons, boolean limitSelection)
        {
            while (true)
            {
                //Ask the user if they want to whitelist, blacklist, or do neither
                System.out.println("Would you like to whitelist or blacklist any monsters? (\"w\" for whitelist, \"b\" for blacklist, \"n\" for no)");
                String response = scan.nextLine();
                switch (response.toLowerCase())
                {
                    //Whitelist
                    case "w" ->
                    {
                        String monName = "";
                        ArrayList<Monster> whitelistedMonsters = new ArrayList<>();
                        while (true)
                        {
                            //Get Monster name
                            while (!MONSTERS.stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                            {
                                //Print current whitelisted Monsters
                                System.out.println("Type the next monster to whitelist or \"exit\" to exit");
                                System.out.print("Current Monsters: ");
                                whitelistedMonsters.forEach(m -> System.out.printf("%s\t\t", m.getName(true, false)));
                                System.out.println();
                                //Get the next Monster
                                monName = scan.nextLine();
                            }
                            //Exit filtering
                            if (monName.equals("exit"))
                            {
                                //Check there are enough Monsters
                                if (whitelistedMonsters.size() < 5 && limitSelection)
                                {
                                    monName = "";
                                    System.out.println("Error, must include at least 5 monsters");
                                    continue;
                                }
                                return whitelistedMonsters;
                            }
                            //Move Monster to whitelist
                            moveToFilteredList(mons, monName, whitelistedMonsters);
                            monName = "";
                        }
                    }
                    //Blacklist
                    case "b" ->
                    {
                        String monName = "";
                        ArrayList<Monster> blacklistedMonsters = new ArrayList<>();
                        while (true)
                        {
                            //Get Monster name
                            while (!MONSTERS.stringIsMonsterName(monName, mons) && !monName.equals("exit"))
                            {
                                //Print current blacklisted Monsters
                                System.out.println("Type the next monster to blacklist or \"exit\" to exit");
                                System.out.print("Current Monsters: ");
                                blacklistedMonsters.forEach(m -> System.out.printf("%s\t\t", m.getName(true, false)));
                                System.out.println();
                                //Get the next Monster
                                monName = scan.nextLine();
                            }
                            //Exit filtering
                            if (monName.equals("exit"))
                            {
                                return mons;
                            }
                            //Check there are enough Monsters left to remove another one
                            if (mons.size() == 5 && limitSelection)
                            {
                                monName = "";
                                System.out.println("Error, must include at least 5 monsters");
                                continue;
                            }
                            //Move Monster to blacklist
                            moveToFilteredList(mons, monName, blacklistedMonsters);
                            monName = "";
                        }
                    }
                    //No filter
                    case "n" ->
                    {
                        return mons;
                    }
                }
            }
        }
        
        /**
         * Filters the teams by Monsters (original list is unchanged)
         *
         * @param teams The teams to filter
         * @return The filtered teams
         */
        public static ArrayList<Team> filterTeams(final ArrayList<Team> teams)
        {
            //Get filtered Monsters
            ArrayList<Monster> filteredMonsters = filterMonsters(Monster.getMonstersFromDatabase(), false);
            Team tempFilteredTeam = new Team("Filtered", filteredMonsters);
            //Create a temporary list so the original list is unchanged
            ArrayList<Team> temp = new ArrayList<>(teams);
            
            //Filter teams
            outer:
            for (int i = temp.size() - 1; i >= 0; i--)
            {
                Team t = temp.get(i);
                //Remove the team if it has a Monster that is not included in the filter
                for (Monster teamMonster : t.getMonsters())
                {
                    if (!tempFilteredTeam.hasInstanceOf(teamMonster))
                    {
                        temp.remove(t);
                        continue outer;
                    }
                }
            }
            return temp;
        }
        
        /**
         * Creates a new Monster from the given name, adds it to the filtered list, and removes it from the unfiltered list
         *
         * @param unfilteredMonsters The unfiltered monsters
         * @param monName            The name of the Monster
         * @param filteredMonsters   The filtered monsters
         */
        private static void moveToFilteredList(ArrayList<Monster> unfilteredMonsters, String monName, ArrayList<Monster> filteredMonsters)
        {
            //Create Monster from name
            Monster mon;
            try
            {
                mon = MONSTERS.createNewMonFromName(monName, true);
            }
            catch (Exception e)
            {
                System.out.println("Unable to find Monster");
                return;
            }
            
            //Move Monster from the unfiltered list to the filtered one
            for (int i = 0; i < unfilteredMonsters.size(); i++)
            {
                if (unfilteredMonsters.get(i).getName(false, false).equals(mon.getName(false, false)))
                {
                    filteredMonsters.add(unfilteredMonsters.remove(i));
                    break;
                }
            }
        }
        
        /**
         * Sorts the teams using merge sort.
         *
         * @param teams      The teams to sort
         * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
         * @param sortOption The values the program should sort the teams by. ("wins" for wins "losses" for losses and "ratio" for win/loss ratio)
         */
        public static void sortTeams(ArrayList<Team> teams, boolean highToLow, String sortOption)
        {
            System.out.println("Sorting teams...");
            ArrayList<Team> temp = sortHelper(teams, highToLow, sortOption, teams.size());
            teams.clear();
            teams.addAll(temp);
            System.out.println("Done");
        }
        
        /**
         * Helper method for sorting a list of Team objects based on the specified options.
         *
         * @param teams      The list of Team objects to be sorted.
         * @param highToLow  A boolean indicating whether the sorting should be in descending (true) or ascending (false) order.
         * @param sortOption The sorting criteria to be applied.
         * @param totalSize  The total size of the original list, used for calculating and displaying progress.
         * @return A sorted ArrayList of Team objects based on the specified options.
         */
        private static ArrayList<Team> sortHelper(ArrayList<Team> teams, boolean highToLow, String sortOption, int totalSize)
        {
            if (teams.isEmpty() || teams.size() == 1)
            {
                return teams;
            }
            
            int middle = teams.size() / 2;
            ArrayList<Team> left = new ArrayList<>(teams.subList(0, middle));
            ArrayList<Team> right = new ArrayList<>(teams.subList(middle, teams.size()));
            
            return merge(sortHelper(left, highToLow, sortOption, totalSize), sortHelper(right, highToLow, sortOption, totalSize), highToLow, sortOption, totalSize);
        }
        
        /**
         * Merges two sorted lists of teams into a single sorted list based on the specified sorting criteria.
         *
         * @param left       The left ArrayList of teams to merge, pre-sorted based on the sorting criteria
         * @param right      The right ArrayList of teams to merge, pre-sorted based on the sorting criteria
         * @param highToLow  A boolean flag indicating whether sorting should be in descending order (true) or ascending order (false)
         * @param sortOption The sorting criteria to be applied.
         * @param totalSize  The total size of all teams being merged from both lists, used for progress calculation
         * @return An ArrayList of teams containing all elements from the input lists merged according to the specified order
         */
        private static ArrayList<Team> merge(ArrayList<Team> left, ArrayList<Team> right, boolean highToLow, String sortOption, int totalSize)
        {
            ArrayList<Team> mergedTeams = new ArrayList<>();
            
            //Compare values in each list
            for (; !left.isEmpty() && !right.isEmpty(); mergedTeams.add((mergeBool(highToLow, sortOption, left.getFirst(), right.getFirst())) ? left.removeFirst() : right.removeFirst()))
            {
            }
            
            //Add stragglers
            for (boolean b = !left.isEmpty(); !((b) ? left : right).isEmpty(); mergedTeams.add((b) ? left.removeFirst() : right.removeFirst()))
            {
            }
            
            //Print progress
            System.out.printf("Progress: %.4f%%\r", (mergedTeams.size() * 1.0 / totalSize) * 100);
            
            return mergedTeams;
        }
        
        /**
         * Gives the boolean required for the sorting algorithm.
         *
         * @param highToLow  True if the returned value should be sorted highest to lowest, false otherwise
         * @param sortOption This is the value the program will use to sort the teams.
         * @param t1         The first team for order checking
         * @param t2         The second team for order checking
         * @return The result of the boolean (true for the first team, false for the second team)
         */
        private static boolean mergeBool(boolean highToLow, String sortOption, Team t1, Team t2)
        {
            if (t1 == null || t2 == null)
            {
                return false;
            }
            
            double t1Ratio = 1.0 * t1.getWins() / t1.getLosses();
            double t2Ratio = 1.0 * t2.getWins() / t2.getLosses();
            
            return (highToLow) ? switch (sortOption)
            {
                case "wins" -> t1.getWins() > t2.getWins(); //Wins high to low
                case "losses" -> t1.getLosses() > t2.getLosses(); //Losses high to low
                default -> t1Ratio > t2Ratio; //Ratio high to low
            } : switch (sortOption)
            {
                case "wins" -> t1.getWins() < t2.getWins(); //Wins low to high
                case "losses" -> t1.getLosses() < t2.getLosses(); //Losses low to high
                default -> t1Ratio < t2Ratio; //Ratio low to high
            };
        }
        
        /**
         * Calculates each Monster's average place and sorts them from lowest to highest
         *
         * @param teams The teams to look through
         * @return An ordered list of Monsters
         */
        public static ArrayList<Pair<Monster, Integer>> sortMonsByPlace(final ArrayList<Team> teams)
        {
            HashMap<String, Pair<Integer, Integer>> map = new HashMap<>(); //Monster name, sum, number of appearances
            //For each team, add the index to each Monster's respective sum
            for (int i = 0; i < teams.size(); i++)
            {
                Team team = teams.get(i);
                for (Monster monster : team.getMonsters())
                {
                    //Increase the Monster's sum and # of appearances if it is already in the HashMap
                    if (map.containsKey(monster.getName(false, false)))
                    {
                        Pair<Integer, Integer> temp = map.get(monster.getName(false, false));
                        temp.setFirst(temp.getFirst() + i);
                        temp.setSecond(temp.getSecond() + 1);
                        map.put(monster.getName(false, false), temp);
                    }
                    else //Put the Monster into the HashMap with new values
                    {
                        map.put(monster.getName(false, false), new Pair<>(i, 1));
                    }
                }
            }
            
            //Pair each Monster with its avg place
            ArrayList<Pair<Monster, Integer>> returnVal = new ArrayList<>();
            try
            {
                map.forEach((name, pair) -> returnVal.add(new Pair<>(MONSTERS.createNewMonFromName(name, true), (pair.getFirst() / pair.getSecond()))));
            }
            catch (Exception e)
            {
                System.err.println("Unable to sort Monsters");
                return null;
            }
            
            //Sort the final list according to each Monster's average place
            for (int i = 0; i < returnVal.size(); i++)
            {
                Pair<Monster, Integer> pair = returnVal.get(i);
                int j = i - 1;
                while (j >= 0 && pair.getSecond() < returnVal.get(j).getSecond())
                {
                    returnVal.set(j + 1, returnVal.get(j));
                    j--;
                }
                returnVal.set(j + 1, pair);
            }
            return returnVal;
        }
    }
    
    /**
     * The STATISTICS subclass provides methods for statistical calculations, such as generating combinations,
     * computing simulations, and counting pre-completed simulations.
     */
    public static class STATISTICS
    {
        /**
         * Generates all possible Team combinations
         *
         * @param monsters A list of Monsters to generate combinations from
         * @param r        The length of each combination
         * @param saveTime True if the program should prioritize time over space, false otherwise
         * @return A list containing lists of Monsters to set up the Teams
         * @author ChatGPT
         */
        public static ArrayList<ArrayList<Monster>> generateCombinations(ArrayList<Monster> monsters, int r, boolean saveTime)
        {
            ArrayList<ArrayList<Monster>> result = new ArrayList<>();
            ArrayList<Monster> currentCombination = new ArrayList<>();
            //Initialize the list
            for (int i = 0; i < r; i++)
            {
                currentCombination.add(null);
            }
            System.out.print("Progress: 0%\r");
            generateCombinationsUtil(monsters, r, 0, 0, currentCombination, result, saveTime);
            System.out.println("\n");
            return result;
        }
        
        /**
         * A helper method to generate each combination. This method should only be called from {@link #generateCombinations(ArrayList, int, boolean)}
         *
         * @param monsters           A list of Monsters to generate the combinations from
         * @param r                  The length of each combination
         * @param index              The place in the list Monsters to start. This should be zero on the first call
         * @param depth              The current place in the current combination to add the next Monster. This should be zero on the first call
         * @param currentCombination The current combination being built. This should be a new ArrayList with a size of r on the first call
         * @param result             All combinations built already. This should be a new ArrayList on the first call.
         * @param saveTime           True if the program should prioritize time over space, false otherwise
         * @author ChatGPT
         */
        private static void generateCombinationsUtil(ArrayList<Monster> monsters, int r, int index, int depth, ArrayList<Monster> currentCombination, ArrayList<ArrayList<Monster>> result, boolean saveTime)
        {
            //Add the current combination to the result if it is complete
            if (depth == r)
            {
                if (saveTime)
                {
                    ArrayList<Monster> a = new ArrayList<>();
                    //Create new instances of each Monster to save time later
                    currentCombination.forEach(m -> {
                        try
                        {
                            a.add(Util.MONSTERS.createNewMonFromMon(m));
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    });
                    
                    result.add(a);
                }
                else
                {
                    //Add the same instance of each Monster to save memory
                    result.add(new ArrayList<>(currentCombination));
                }
                System.out.printf("Progress: %.1f%%\r", 100.0 * result.size() / nCr(monsters.size(), r));
                return;
            }
            
            for (int i = index; i < monsters.size(); i++)
            {
                //Add the Monster to the current combination and go to the place
                currentCombination.set(depth, monsters.get(i));
                generateCombinationsUtil(monsters, r, i + 1, depth + 1, currentCombination, result, saveTime);
            }
        }
        
        /**
         * Calculates the number of combinations (nCr) for choosing {@code r} items from a set of {@code n} items.
         *
         * @param n The total number of items in the set.
         * @param r The number of items to choose from the set.
         * @return The number of valid combinations (nCr), or 0 if {@code r > n}.
         * @author JetBrains AI Assistant
         */
        private static long nCr(int n, int r)
        {
            //Make sure there are valid combinations
            if (r > n)
            {
                return 0;
            }
            
            //Take advantage of symmetry: C(n, r) = C(n, n-r)
            r = Math.min(r, n - r);
            
            //Calculate combination count
            long result = 1;
            for (int i = 0; i < r; i++)
            {
                result *= (n - i);
                result /= (i + 1);
            }
            return result;
        }
        
        /**
         * Calculates the total number of simulations that can be run given the provided number of combinations
         *
         * @param numOfCombos The number of combinations being tested
         * @return The total number of different simulations that can be run (equivalent to numOfCombos!)
         */
        public static long totalNumOfSims(int numOfCombos)
        {
            long result = 0;
            try //to add recursively
            {
                for (int i = numOfCombos; i > 0; i--)
                {
                    result += numOfCombos;
                }
                return result;
            }
            catch (Exception e) //Too many recursive calls
            {
                //Flag an error
                Auto_Play.setSimsCalculationError(true);
                return result;
            }
        }
        
        /**
         * Calculates the number of completed simulations from a previous instance of Auto_Play
         *
         * @param i    The last i value from the previous instance
         * @param j    The last j value from the previous instance
         * @param size The size of the library from the previous instance
         * @return The number of simulations from the previous Auto_Play instance
         */
        public static int calculateNumOfPreCompletedSims(int i, int j, int size)
        {
            int total = 0;
            for (int k = 0; k < i; k++)
            {
                total += size - (k + 1);
            }
            total += j - (i + 1);
            return total;
        }
    }
    
    /**
     * The CHOOSERS class contains nested enums and classes that encapsulate
     * the logic for determining the flow of a turn in the game, whether it's automated or manually handled.
     */
    public static class CHOOSERS
    {
        /**
         * Defines possible results or outcomes of a turn taken in the game.
         * This enumeration is used to manage the flow of turns and determine
         * the resulting state after a Monster performs its actions.
         * <p>
         * Enum Constants:
         * - CONTINUE: Indicates that the game should continue the gameplay loop to repeat the current turn.
         * This could apply when the current Monster causes an error during its turn
         * <p>
         * - BREAK: Indicates that the game is interrupted or ends, typically
         * when a condition such as the death of a team is met. This result
         * halts further action in the game's turn loop.
         * <p>
         * - NORMAL: Represents the successful completion of a standard turn
         * where no interruptions or special conditions are triggered
         * and the game can proceed normally.
         */
        public enum TURN_RESULT
        {
            /**
             * Represents the state in which the game continues the gameplay loop, repeating the current turn.
             * This may occur when the current Monster's action results in an error or requires the turn to be reattempted.
             */
            CONTINUE,
            
            /**
             * Indicates that the game is interrupted or ends, typically when a condition such as
             * the death of a team is met. This result halts further action in the game's turn loop.
             */
            BREAK,
            
            /**
             * Represents the successful completion of a standard turn where no interruptions
             * or special conditions are triggered, allowing the game to proceed normally.
             */
            NORMAL
        }
        
        /**
         * The AUTO subclass provides functionality for algorithmic gameplay decisions during auto play.
         * It includes logic for determining the next turn based on attack bars, enemy and ally hp, etc.; applying turns;
         * targeting decisions, and handling game flow.
         */
        public static class AUTO
        {
            /**
             * Executes the next turn in the game, handling the logic for incrementing attack bars,
             * determining the next Monster to act, applying effects, and managing special conditions
             * like stuns, provokes, and team death. The method also ensures the game flow loops or ends
             * based on the game state.
             *
             * @param game The current game instance where the turn will be executed.
             * @return A {@link TURN_RESULT} indicating the outcome of the turn. Possible values include:
             * - CONTINUE: The game should repeat the current loop.
             * - BREAK: The game ends due to a final game condition (e.g., all Monsters on a team are defeated).
             * - NORMAL: The turn completed successfully without interruptions or special conditions.
             */
            public static TURN_RESULT nextTurn(Game game)
            {
                //Increment attack bars until at least one is full
                while (!game.hasFullAtkBar())
                {
                    game.increaseAtkBar();
                }
                
                //Get the next Monster
                Team highestAtkBar = game.getTeamWithHighestAtkBar();
                Team other = game.getOtherTeam();
                Monster next = highestAtkBar.monsterWithHighestFullAtkBar();
                
                //Activate before turn passives
                game.activateBeforeTurnPassives(next);
                
                //Apply before turn buffs and debuffs
                game.applyEffects(next);
                
                //If the monster's Hp falls below zero before turn (like from DoT)
                if (next.getCurrentHp() <= 0)
                {
                    next.kill();
                }
                
                //Check if stunned
                if (next.isStunned())
                {
                    next.decreaseStatCooldowns();
                    next.setAtkBar(0);
                    return CHOOSERS.TURN_RESULT.CONTINUE;
                }
                
                //Check if dead again
                if (next.getCurrentHp() <= 0)
                {
                    next.kill();
                    if (next.isDead())
                    {
                        next.setAtkBar(-999);
                        return CHOOSERS.TURN_RESULT.CONTINUE;
                    }
                }
                
                //Ends game if a team is dead
                if (game.endGame())
                {
                    return CHOOSERS.TURN_RESULT.BREAK;
                }
                
                //Check for Provoke
                Provoke p = next.getProvoke();
                if (p != null)
                {
                    Monster caster = p.getCaster();
                    next.nextTurn(caster, 1);
                    return CHOOSERS.TURN_RESULT.CONTINUE;
                }
                
                //Checks if any Monster on the other team has Threat
                boolean threat = other.monHasThreat();
                
                //Get ability number
                int abilityNum = next.chooseAbilityNum(next, game.getTeamWithHighestAtkBar(), next.getAbilities(), true);
                
                //Get target and apply nextTurn
                if (threat && !next.getAbility(abilityNum).targetsEnemy())
                {
                    Monster target = other.getMonWithThreat();
                    next.nextTurn(target, abilityNum);
                }
                chooseTargetAndApplyNextTurn(next, abilityNum, (next.getAbility(abilityNum).targetsEnemy() ? other : highestAtkBar), true);
                return CHOOSERS.TURN_RESULT.NORMAL;
            }
            
            /**
             * A recursive algorithm to choose the best target to attack/heal and apply the {@link Monster#nextTurn(Monster, int)} function.
             * <pre>
             *     The significance when choosing the target is as follows:
             *     1. low health targets
             *     2. Advantageous attributes
             *     3. Neutral attributes
             *     4. Disadvantageous attributes
             *     5. Monsters with a low number of buffs that decrease damage taken
             *     6. Monsters with a high number of debuffs that increase damage
             * </pre>
             *
             * @param next             The acting Monster
             * @param abilityNum       The ability number to use
             * @param potentialTargets A temporary team of Monsters that contain the potential targets
             * @param firstCall        True if this call is the first time it is called (From outside the method)
             */
            public static void chooseTargetAndApplyNextTurn(Monster next, int abilityNum, Team potentialTargets, boolean firstCall)
            {
                //Remove all dead monsters on first call
                if (firstCall)
                {
                    ArrayList<Monster> modifiedTeam = new ArrayList<>();
                    for (Monster mon : potentialTargets.getMonsters())
                    {
                        if (!mon.isDead())
                        {
                            modifiedTeam.add(mon);
                        }
                    }
                    chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
                    return;
                }
                //Do nothing if no potential targets found
                if (potentialTargets.size() == 0)
                {
                    return;
                }
                Team allyTeam = Monster.getGame().getTeamFromMon(next);
                Ability chosenAbility = next.getAbility(abilityNum);
                
                //Ability is a support ability
                if (chosenAbility instanceof Heal_Ability)
                {
                    //Target the Monster on the allied team with the lowest HP ratio
                    Monster target = allyTeam.getLowestHpMon();
                    verifyTargetAndApplyTurn(next, chosenAbility, target, abilityNum, potentialTargets);
                    return;
                }
                
                //Ability targets self
                if (chosenAbility.targetsSelf())
                {
                    next.nextTurn(next, abilityNum);
                    return;
                }
                
                //Ability is an attack ability
                //Get best attack choice and try to attack
                verifyTargetAndApplyTurn(next, chosenAbility, getBestMonToAttack(next, potentialTargets.getMonsters(), chosenAbility.getNumOfBeneficialEffectsToRemove()), abilityNum, potentialTargets);
            }
            
            /**
             * Verifies the target and attempts to complete its turn
             *
             * @param next             The acting Monster
             * @param chosenAbility    The chosen ability
             * @param target           The target Monster
             * @param abilityNum       The ability number
             * @param potentialTargets The potential targets
             */
            private static void verifyTargetAndApplyTurn(Monster next, Ability chosenAbility, Monster target, int abilityNum, Team potentialTargets)
            {
                //Check if the target is valid
                if (!next.targetIsValid(target, chosenAbility.targetsEnemy()))
                {
                    removeInvalidTarget(next, target, abilityNum, potentialTargets);
                    return;
                }
                //Try to complete turn
                if (!next.nextTurn(target, abilityNum))
                {
                    removeInvalidTarget(next, target, abilityNum, potentialTargets);
                }
            }
            
            /**
             * Removes an invalid target and attempts to find a new one
             *
             * @param next             The acting Monster
             * @param target           The target to remove
             * @param abilityNum       The chosen ability number
             * @param potentialTargets The potential targets
             */
            private static void removeInvalidTarget(Monster next, Monster target, int abilityNum, Team potentialTargets)
            {
                ArrayList<Monster> modifiedTeam = new ArrayList<>();
                //Remove invalid target
                for (Monster potentialTarget : potentialTargets.getMonsters())
                {
                    if (!potentialTarget.equals(target))
                    {
                        modifiedTeam.add(potentialTarget);
                    }
                }
                //Attempt to find new target
                chooseTargetAndApplyNextTurn(next, abilityNum, new Team("Modified", modifiedTeam), false);
            }
            
            /**
             * A method to find the best Monster to attack
             *
             * @param next                       The attacking Monster
             * @param monsters                   The Monsters to search through
             * @param numOfBuffsCanAbilityRemove Whether the chosen ability can remove buffs
             * @return The best Monster to attack
             */
            public static Monster getBestMonToAttack(Monster next, ArrayList<Monster> monsters, int numOfBuffsCanAbilityRemove)
            {
                Monster target = null;
                //Make sure there is at least one Monster alive
                for (Monster mon : monsters)
                {
                    if (!mon.isDead())
                    {
                        target = mon;
                        break;
                    }
                }
                //Do nothing if no alive targets
                if (target == null)
                {
                    return null;
                }
                
                //Calculate each Monster's "score" and find the highest
                double highestPoints = -1;
                double currentPoints;
                for (Monster mon : monsters)
                {
                    //Set to a predetermined non zero value
                    currentPoints = 2;
                    if (mon.isDead())
                    {
                        continue;
                    }
                    
                    ArrayList<Buff> targetBuffs = mon.getAppliedBuffs();
                    //Decrease score for certain buffs
                    for (Buff buff : targetBuffs)
                    {
                        currentPoints *= switch (buff.getBuffEffect())
                        {
                            case BuffEffect.REFLECT -> 0.9;
                            case BuffEffect.SHIELD -> 0.946;
                            case BuffEffect.IMMUNITY -> 0.82;
                            case BuffEffect.COUNTER -> 0.916;
                            case BuffEffect.CRIT_RESIST_UP -> 0.83;
                            case BuffEffect.DEF_UP -> 0.947;
                            case BuffEffect.ENDURE -> 0.983;
                            case BuffEffect.SOUL_PROTECTION -> 0.74;
                            case BuffEffect.DEFEND -> 0.69;
                            case BuffEffect.INVINCIBILITY -> 0.65;
                            default -> 1;
                        };
                    }
                    //Increase the score depending on how many buffs the ability can remove
                    currentPoints *= (0.19 * numOfBuffsCanAbilityRemove + 1);
                    ArrayList<Debuff> targetDebuffs = mon.getAppliedDebuffs();
                    //Increase score for certain debuffs
                    for (Debuff debuff : targetDebuffs)
                    {
                        switch (debuff.getDebuffEffect())
                        {
                            case DebuffEffect.DEC_DEF -> currentPoints *= 1.2;
                            case DebuffEffect.BRAND -> currentPoints *= 1.15;
                        }
                    }
                    
                    //Alter score for elemental relationship
                    currentPoints *= switch (next.getElement().relationWith(mon.getElement()))
                    {
                        case ConsoleColor.GREEN_BACKGROUND -> 1.3;
                        case ConsoleColor.RED_BACKGROUND -> 0.7;
                        default -> 1.16; //Neutral
                    };
                    
                    //Alter score for HP ratio
                    double weight = target.getHpRatio() / 100 * 1.31;
                    currentPoints /= weight;
                    
                    //Set a new target if it has a higher score
                    if (currentPoints > highestPoints)
                    {
                        target = mon;
                        highestPoints = currentPoints;
                    }
                    //If the scores are the same, choose the one with a lower HP ratio
                    else if (currentPoints == highestPoints)
                    {
                        target = (mon.getHpRatio() < target.getHpRatio()) ? mon : target;
                    }
                }
                return target;
            }
        }
        
        /**
         * The MANUAL subclass provides methods used to manage the manual selection
         * of turns for Monsters in the game. This involves handling user input, determining
         * which Monster acts next, and applying turns
         */
        public static class MANUAL
        {
            /**
             * Executes the next turn in the game loop, handling all mechanics such as
             * attack bar increments, buff and debuff applications, Monster actions,
             * and game state checks. Determines the flow of the turn based on the
             * state of the game and Monster involved.
             *
             * @param game the Game instance managing the teams, Monsters, and game mechanics.
             *             It contains the current state of the game and facilitates the turn processing.
             * @return a TURN_RESULT value indicating the outcome of the turn. Possible return values are:
             * CONTINUE if the game should repeat the current loop,
             * BREAK if the game has ended or is interrupted,
             * NORMAL if the turn concludes successfully under standard conditions.
             */
            public static TURN_RESULT nextTurn(Game game)
            {
                boolean monChosen = false;
                Monster next = null;
                while (true)
                {
                    //Increment attack bars until at least one is full
                    while (!game.hasFullAtkBar())
                    {
                        game.increaseAtkBar();
                    }
                    
                    //Print game and find who is next
                    System.out.printf("%n%n%s%n%n%n", game);
                    
                    //Delay output
                    pause(1000);
                    
                    //Get acting Monster
                    if (!monChosen)
                    {
                        next = game.getNextMonster();
                    }
                    //Print acting Monster
                    System.out.println(next);
                    //Delay output
                    pause(1000);
                    
                    //Activate before turn passives
                    game.activateBeforeTurnPassives(next);
                    
                    //Apply buffs and debuffs before the Monster's turn starts
                    if (!monChosen)
                    {
                        game.applyEffects(next);
                    }
                    
                    //If the monster's Hp falls below zero before turn
                    if (next.getCurrentHp() <= 0)
                    {
                        next.kill();
                    }
                    
                    //Print next mon
                    System.out.println(next.shortToString(true));
                    
                    //Check if the Monster is stunned
                    if (next.isStunned())
                    {
                        next.decreaseStatCooldowns();
                        next.setAtkBar(0);
                        return TURN_RESULT.CONTINUE;
                    }
                    
                    //Go to next monster if current is dead
                    if (next.isDead())
                    {
                        next.kill();
                        if (next.isDead())
                        {
                            return TURN_RESULT.CONTINUE;
                        }
                    }
                    
                    //Ends game if a team is dead
                    if (game.endGame())
                    {
                        return TURN_RESULT.BREAK;
                    }
                    //Delay output
                    pause(300);
                    
                    //Check for Provoke
                    Provoke p = next.getProvoke();
                    if (p != null)
                    {
                        printfWithColor("Provoked!%n", ConsoleColor.YELLOW);
                        Monster caster = p.getCaster();
                        next.nextTurn(caster, 1);
                        return TURN_RESULT.CONTINUE;
                    }
                    
                    //Checks if any Monster on the other team has Threat
                    boolean threat = game.getOtherTeam().monHasThreat();
                    
                    //Get ability number/print buff and debuff descriptions
                    monChosen = true;
                    int abilityNum = CONSOLE_INTERFACE.INPUT.getAbilityNum(next);
                    
                    //Get target num
                    Monster targetMon = CONSOLE_INTERFACE.INPUT.getTarget(next, abilityNum, threat);
                    //Re-choose ability if no target selected
                    if (targetMon == null)
                    {
                        continue;
                    }
                    
                    //Start the Monster's turn
                    MONSTERS.applyNextTurn(next, targetMon, abilityNum);
                    //Delay output
                    pause(800);
                    
                    return TURN_RESULT.NORMAL;
                }
            }
        }
    }
}