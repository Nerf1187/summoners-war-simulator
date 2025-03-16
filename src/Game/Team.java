package Game;

import Errors.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * This class is used to create and manage Teams
 *
 * @author Anthony (Tony) Youssef
 */
public class Team
{
    private final String name;
    private final ArrayList<Monster> monsters;
    private int wins = 0;
    private int losses = 0;
    
    /**
     * Creates a new Team object
     *
     * @param name The name of the Team
     * @param mons The list of Monster on the Team
     */
    public Team(String name, ArrayList<Monster> mons)
    {
        this.name = name;
        this.monsters = mons;
    }
    
    /**
     * Increases the attack bar by the default amount for each Monster on the Team. This is the same as calling {@link Monster#increaseAtkBar()} on each Monster
     */
    public void increaseAtkBar()
    {
        for (Monster m : monsters)
        {
            m.increaseAtkBar();
        }
    }
    
    /**
     * Checks if there are any Monster still alive
     *
     * @return True if all Monster are dead, false otherwise
     */
    public boolean deadTeam()
    {
        //Check if every Monster on the team is dead
        for (Monster m : monsters)
        {
            if (!m.isDead())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @return True if there is at least one Monster with a full attack bar, false otherwise
     */
    public boolean hasFullAtkBar()
    {
        //Check if any Monster on the team has a full attack bar
        for (Monster m : monsters)
        {
            if (m.hasFullAtkBar())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds the Monster with a full attack bar, if there are multiple Monsters, chooses the one with the highest value
     *
     * @return The Monster with the highest full attack bar
     */
    public Monster MonsterWithHighestFullAtkBar()
    {
        double highest = 0;
        Monster highestMon = null;
        //Search through each Monster on the team
        for (Monster m : monsters)
        {
            //Only check if the attack bar is full
            if (m.getAtkBar() >= Monster.MAX_ATK_BAR_VALUE && m.getAtkBar() > highest)
            {
                highest = m.getAtkBar();
                highestMon = m;
            }
        }
        return highestMon;
    }
    
    /**
     * Finds the highest attack bar value on the Team
     *
     * @return The highest attack bar value
     */
    public double getHighestFullAtkBar()
    {
        double highest = 0;
        for (Monster m : monsters)
        {
            //Only check if the attack bar is full
            if (m.getAtkBar() >= Monster.MAX_ATK_BAR_VALUE && m.getAtkBar() > highest)
            {
                highest = m.getAtkBar();
            }
        }
        return highest;
    }
    
    /**
     * Gets the Monsters on the Team
     *
     * @return The Monsters on the Team
     */
    public ArrayList<Monster> getMonsters()
    {
        return monsters;
    }
    
    /**
     * Formats the Team into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return print(-1, -1);
    }
    
    /**
     * Formats the Team into a readable String with elemental relationships included on each Monster
     *
     * @param elementNum The element number to compare each Monster to
     * @param enemyTeam  Whether this Team is the enemy Team or friendly Team (1 for true, 0 for false, -1 for no elemental relationships)
     * @return The formatted String
     */
    public String print(int elementNum, int enemyTeam)
    {
        //Initialize lists
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> hp = new ArrayList<>(), atkBar = new ArrayList<>();
        ArrayList<ArrayList<Buff>> buffs = new ArrayList<>();
        ArrayList<ArrayList<Debuff>> debuffs = new ArrayList<>();
        ArrayList<ArrayList<Stat>> otherEffects = new ArrayList<>();
        
        for (Monster mon : monsters)
        {
            if (mon.isDead())
            {
                //Add dead String
                names.add(ConsoleColors.BLACK + ConsoleColors.WHITE_BACKGROUND + mon.getName(false, true) + ConsoleColors.RESET);
                hp.add(null);
                atkBar.add(null);
                buffs.add(null);
                debuffs.add(null);
                otherEffects.add(null);
            }
            else
            {
                //Add elemental relationships
                if (enemyTeam != -1)
                {
                    names.add(ConsoleColors.BLACK_BOLD + ((enemyTeam == 1) ? elementalRelationship(elementNum, mon.getElement()) :
                            ConsoleColors.GREEN_BACKGROUND) + mon.getName(false, true) + ConsoleColors.RESET);
                }
                else //Do not add elemental relationships
                {
                    names.add(mon.getName(true, true));
                }
                
                //Add HP and attack bar ratios
                hp.add(mon.getHpRatio());
                String atkBarPercent = mon.getAtkBar() / 10 + "";
                if (atkBarPercent.length() > 4 && !atkBarPercent.equals("100.0"))
                {
                    atkBarPercent = atkBarPercent.substring(0, 4);
                }
                atkBar.add(Double.parseDouble(atkBarPercent));
                //Add stats
                buffs.add(mon.getAppliedBuffs());
                debuffs.add(mon.getAppliedDebuffs());
                otherEffects.add(mon.getOtherStats());
            }
        }
        
        //Return formatted String
        return formatLines(names, hp, atkBar, buffs, debuffs, otherEffects);
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
    private String formatLines(ArrayList<String> names, ArrayList<Double> hp, ArrayList<Double> atkBar, ArrayList<ArrayList<Buff>> buffs, ArrayList<ArrayList<Debuff>> debuffs, ArrayList<ArrayList<Stat>> otherEffects)
    {
        //Initialize lines
        ArrayList<String> firstLineInfo = new ArrayList<>();
        ArrayList<String> secondLineInfo = new ArrayList<>();
        ArrayList<String> thirdLineInfo = new ArrayList<>();
        ArrayList<String> fourthLineInfo = new ArrayList<>();
        
        //Add each Monster's basic info
        for (int i = 0; i < names.size(); i++)
        {
            if (names.get(i).contains(ConsoleColors.BLACK)) //Add dead Monster
            {
                firstLineInfo.add(names.get(i));
            }
            else //Add living Monster
            {
                firstLineInfo.add("%s (%sHp = %s%%%s, %sAttack Bar = %s%%%s)".formatted(names.get(i), ConsoleColors.GREEN, hp.get(i), ConsoleColors.RESET, ConsoleColors.CYAN, atkBar.get(i), ConsoleColors.RESET));
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
        return toStringSpacing(firstLineInfo, secondLineInfo, thirdLineInfo, fourthLineInfo) + ConsoleColors.RESET;
    }
    
    /**
     * Gets the number of Monsters on the Team
     *
     * @return The number of Monsters on the Team
     */
    public int size()
    {
        return monsters.size();
    }
    
    /**
     * Finds the Monster at the given index of the Team
     *
     * @param index The index to return. If the index is less than 0, returns the first element. If the index is greater the size of the team, returns the last element
     * @return The Monster at the given index
     */
    public Monster get(int index)
    {
        if (index < 0)
        {
            return monsters.getFirst();
        }
        if (index >= monsters.size())
        {
            return monsters.getLast();
        }
        return monsters.get(index);
    }
    
    /**
     * Gets the name of the Team
     *
     * @return The name of the Team
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Finds the correct elemental relationship between the given elements. (Assumes Monsters are from different Teams)
     *
     * @param e1 The first element
     * @param e2 the second element
     * @return The color of the relationship (green background for advantageous; yellow background for neutral; red background for disadvantageous)
     */
    public static String elementalRelationship(int e1, int e2)
    {
        //Advantageous
        if ((e1 == Monster.FIRE && e2 == Monster.WIND) || (e1 == Monster.WATER && e2 == Monster.FIRE) || (e1 == Monster.WIND && e2 == Monster.WATER) || (e1 == Monster.LIGHT && e2 == Monster.DARK) || (e1 == Monster.DARK && e2 == Monster.LIGHT))
        {
            return ConsoleColors.GREEN_BACKGROUND;
        }
        //Neutral
        if ((e1 == e2) || (e1 == Monster.LIGHT) || (e1 == Monster.DARK) || (e2 == Monster.LIGHT) || (e2 == Monster.DARK))
        {
            return ConsoleColors.YELLOW_BACKGROUND;
        }
        //Disadvantageous
        return ConsoleColors.RED_BACKGROUND;
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
    private String toStringSpacing(ArrayList<String> line1, ArrayList<String> line2, ArrayList<String> line3, ArrayList<String> line4)
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
     * A method to properly format each line in {@link Team#toStringSpacing(ArrayList, ArrayList, ArrayList, ArrayList)}
     *
     * @param lines Each line to format (Should be a length of 4)
     * @return The properly formatted String
     */
    private static String formatString(ArrayList<ArrayList<String>> lines)
    {
        String s = "";
        //Line 1 - Monster info
        for (int i = 0; i < lines.get(0).size(); i++)
        {
            s += lines.getFirst().get(i);
        }
        s += "\n";
        
        //Line 2 - Buffs
        for (int i = 0; i < lines.get(0).size(); i++)
        {
            s += ConsoleColors.BLUE + lines.get(1).get(i);
        }
        s += "\n";
        
        //Line 3 - Debuffs
        for (int i = 0; i < lines.get(0).size(); i++)
        {
            s += ConsoleColors.RED + lines.get(2).get(i);
        }
        s += "\n";
        
        //Line 4 - Other effects
        for (int i = 0; i < lines.get(0).size(); i++)
        {
            s += ConsoleColors.PURPLE + lines.get(3).get(i);
        }
        return s;
    }
    
    /**
     * If the given String contains a color, finds the length without the color, otherwise returns the length of the String
     *
     * @param string The String to find the length of
     * @return The length of the String without colors
     */
    private int lengthWithoutColors(String string)
    {
        int length = string.length();
        if (string.contains(ConsoleColors.GREEN))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.CYAN))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.BLUE_BOLD_BRIGHT))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.RED_BOLD_BRIGHT))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.YELLOW_BOLD_BRIGHT))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.WHITE_BOLD_BRIGHT))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.PURPLE_BOLD_BRIGHT))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.BLACK))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.WHITE_BACKGROUND))
        {
            length -= 5;
        }
        if (string.contains(ConsoleColors.BLACK_BOLD))
        {
            length -= 7;
        }
        if (string.contains(ConsoleColors.GREEN_BACKGROUND))
        {
            length -= 5;
        }
        if (string.contains(ConsoleColors.YELLOW_BACKGROUND))
        {
            length -= 5;
        }
        if (string.contains(ConsoleColors.RED_BACKGROUND))
        {
            length -= 5;
        }
        
        for (int i = 0; i < string.length() - 4; i++)
        {
            if (string.startsWith(ConsoleColors.RESET, i))
            {
                length -= 4;
            }
        }
        
        return length;
    }
    
    /**
     * Formats a String for a single Monster from the team
     *
     * @param mon  The Monster to print
     * @param self True if the method must return a String representing a Monster targeting itself, false otherwise
     * @return The formatted String
     */
    public String getSingleMonFromTeam(Monster mon, boolean self)
    {
        int count = monsters.indexOf(mon);
        
        if (self)
        {
            return "%s%s%s%s (%d)%s".formatted(ConsoleColors.GREEN_BACKGROUND, ConsoleColors.BLACK_BOLD, mon.shortToString(false), ConsoleColors.PURPLE, count, ConsoleColors.RESET);
        }
        
        return "%s%s (%d)%s".formatted(mon.shortToString(true), ConsoleColors.PURPLE, count, ConsoleColors.RESET);
    }
    
    /**
     * Finds all viable Monster targets and returns their indexes in the Team
     *
     * @param oneMon Whether only one index is to be returned or not
     * @param args   The Monster which the method will return the index of if <code>oneMon</code> is true.
     * @return A list of viable indexes that can be targeted
     */
    public ArrayList<Integer> viableNums(boolean oneMon, Monster... args)
    {
        ArrayList<Integer> returnValues = new ArrayList<>();
        if (oneMon)
        {
            //Make sure only one Monster is passed
            if (args.length != 1)
            {
                throw new InvalidArgumentLength("Length of varArgs must be 1 to print one Monster");
            }
            //Get the index of the requested Monster
            returnValues.add(monsters.indexOf(args[0]));
            return returnValues;
        }
        //Add all living Monsters
        for (int i = 0; i < monsters.size(); i++)
        {
            if (!monsters.get(i).isDead())
            {
                returnValues.add(i);
            }
        }
        return returnValues;
    }
    
    /**
     * @return True if there is a Monster with a Threat buff, false otherwise
     */
    public boolean monHasThreat()
    {
        //Search through each Monster for one with Threat
        for (Monster mon : monsters)
        {
            if (mon.hasThreat())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds the Monster with a Threat buff on the Team
     *
     * @return The Monster with a Threat buff. If no such Monster exists, returns null
     */
    public Monster getMonWithThreat()
    {
        //Searches through each Monster for one with Threat
        for (Monster mon : monsters)
        {
            if (mon.hasThreat())
            {
                return mon;
            }
        }
        return null;
    }
    
    /**
     * Searches the Team for the provided Monster.
     *
     * @param monster The Monster to search for. Must be the same object, not just an instance of what to look for
     * @return True if the Team contains the provided Monster, false otherwise
     */
    public boolean contains(Monster monster)
    {
        return monsters.contains(monster);
    }
    
    /**
     * Searches the Team for an instance of the provided Monster
     *
     * @param monster The Monster instance to search for
     * @return True if the Team contains at least one instance of the provided Monster, false otherwise
     */
    public boolean hasInstanceOf(Monster monster)
    {
        for (Monster mon : monsters)
        {
            //Compare classes
            if (mon.getClass() == monster.getClass())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Searches through the provided String for any numbers that are not 0. (ignores decimals)
     *
     * @param num The String to search through
     * @return True if the provided String has a number other than 0, false otherwise.
     * @throws ConflictingArguments If the String cannot be parsed to a valid double.
     */
    public static boolean stringHasNumOtherThanZero(String num)
    {
        try
        {
            //Make sure the String is a valid double
            Double.parseDouble(num);
        }
        catch (NumberFormatException e)
        {
            //String is not a valid double
            throw new ConflictingArguments("Please enter a valid double");
        }
        //Look through the String for any chars that are not "0" or "."
        for (char c : num.toCharArray())
        {
            if (c == '0' || c == '.')
            {
                continue;
            }
            return true;
        }
        return false;
    }
    
    /**
     * @return A random (living) Monster from the Team.
     */
    public Monster getRandomMon()
    {
        //Checks if all Monsters are dead
        boolean allDead = true;
        for (Monster monster : monsters)
        {
            if (!monster.isDead())
            {
                allDead = false;
            }
        }
        //Return nothing if there are no living Monsters
        if (allDead)
        {
            return null;
        }
        //Find a random living Monster and return it
        Monster returnMon;
        do
        {
            returnMon = monsters.get(new Random().nextInt(monsters.size()));
        }
        while (returnMon.isDead());
        return returnMon;
    }
    
    /**
     * @return The (living) Monster with the lowest amount of HP
     */
    public Monster getLowestHpMon()
    {
        //Initialize lowest variables
        Monster lowest = null;
        int lowestAmount = 0;
        for (Monster monster : monsters)
        {
            if (!monster.isDead())
            {
                lowest = monster;
                lowestAmount = lowest.getCurrentHp();
                break;
            }
        }
        
        //Find the Monster with the lowest HP
        for (Monster monster : monsters)
        {
            if (monster.isDead())
            {
                continue;
            }
            if (monster.getCurrentHp() < lowestAmount && !monster.isDead())
            {
                lowest = monster;
                lowestAmount = monster.getCurrentHp();
            }
        }
        return lowest;
    }
    
    /**
     * Resets every Monster on the team by creating new instances of the same class. Does not set team-based rune effects
     * @return True if every Monster on the team was successfully reset, false otherwise
     */
    public boolean newInstances()
    {
        try
        {
            monsters.replaceAll(Monster::createNewMonFromMon);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * Resets each Monster on the team
     */
    public void resetTeam()
    {
        monsters.forEach(Monster::reset);
    }
    
    
    /**
     * Compares this with the provided Team
     *
     * @param other The Team to compare
     * @return True if <code>other.name.equals(this.name)</code>, false otherwise
     */
    public boolean equals(Team other)
    {
        return name.equals(other.name);
    }
    
    /**
     * Counts the number of Monsters on the Team that are not dead
     *
     * @return The number of Monsters on the Team that are not dead
     */
    public int numOfLivingMons()
    {
        return monsters.stream().mapToInt(mon -> !mon.isDead() ? 1 : 0).sum();
    }
    
    /**
     * Checks whether the given list has the given Monster name (case-insensitive)
     *
     * @param name       The name to search for
     * @param pickedMons The List to search in
     * @return True if at least one Monster's name in the List equals the given String, false otherwise
     */
    protected static boolean teamHasMon(String name, ArrayList<Monster> pickedMons)
    {
        //Search the list for the Monster
        for (Monster mon : pickedMons)
        {
            if (mon.getName(false, false).equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the number of wins for the Team
     *
     * @return The number of wins for this Team
     */
    public int getWins()
    {
        return wins;
    }
    
    /**
     * Sets the number of wins to given number
     *
     * @param wins The new number of wins
     */
    public void setWins(int wins)
    {
        this.wins = wins;
    }
    
    /**
     * Increases the number of wins by one
     */
    public void increaseWins()
    {
        wins++;
    }
    
    /**
     * Gets the number of losses for the Team
     *
     * @return The number of losses for this Team
     */
    public int getLosses()
    {
        return losses;
    }
    
    /**
     * Sets the number of losses for the Team
     *
     * @param losses The new number of losses
     */
    public void setLosses(int losses)
    {
        this.losses = losses;
    }
    
    /**
     * Increases the number of losses by one
     */
    public void increaseLosses()
    {
        losses++;
    }
    
    /**
     * Calculates any damage reduction for the attack
     *
     * @param attackedTeam The team containing the Monster being attacked
     * @param dmg          The initial damage to be dealt
     * @param target       The Monster being attacked
     * @return The new damage to be dealt
     */
    public double dmgReduction(Team attackedTeam, double dmg, Monster target)
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
     * Counts the number of dead Monsters on the Team
     *
     * @return The number of Monsters who are dead
     */
    public int numDead()
    {
        return monsters.stream().mapToInt(monster -> (monster.isDead()) ? 1 : 0).sum();
    }
    
    /**
     * Searches through the Monsters on the team and finds the (living) Monster whose HP ratio is the lowest
     *
     * @return The Monster with the lowest HP ratio greater than 0.
     */
    public Monster getMonsterWithLowestHpRatio()
    {
        //Initialize variables
        double lowestRatio = monsters.getFirst().getCurrentHp();
        ArrayList<Monster> lowestRatioMons = new ArrayList<>(); //Contains the Monsters with the same lowest HP ratio
        lowestRatioMons.add(monsters.getFirst());
        for (Monster monster : monsters)
        {
            //Clear list if the current ratio is lower than the saved one
            if (monster.getHpRatio() < lowestRatio && !monster.isDead())
            {
                lowestRatioMons.clear();
                lowestRatioMons.add(monster);
                lowestRatio = monster.getHpRatio();
            }
            //Add Monster to the list
            if (monster.getHpRatio() == lowestRatio)
            {
                lowestRatioMons.add(monster);
            }
        }
        //Get a random Monster from the list
        return lowestRatioMons.get(new Random().nextInt(lowestRatioMons.size()));
    }
}