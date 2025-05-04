package Game;

import Errors.*;
import Monsters.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import Util.Util.*;
import java.util.*;

/**
 * This class is used to create and manage Teams
 *
 * @author Anthony (Tony) Youssef
 */
public class Team implements Iterable<Monster>
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
    
    @Override
    public Iterator<Monster> iterator()
    {
        return monsters.iterator();
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
    public Monster monsterWithHighestFullAtkBar()
    {
        double highest = 0;
        Monster highestMon = null;
        //Search through each Monster on the team
        for (Monster m : monsters)
        {
            //Do nothing if the Monster is dead
            if (m.isDead())
            {
                continue;
            }
            
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
        return print(null, -1);
    }
    
    /**
     * Formats the Team into a readable String with elemental relationships included on each Monster
     *
     * @param element The element number to compare each Monster to
     * @param enemyTeam  Whether this Team is the enemy Team or friendly Team (1 for true, 0 for false, -1 for no elemental relationships)
     * @return The formatted String
     */
    public String print(Element element, int enemyTeam)
    {
        //Initialize lists
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> hp = new ArrayList<>(), atkBar = new ArrayList<>();
        ArrayList<ArrayList<Buff>> buffs = new ArrayList<>();
        ArrayList<ArrayList<Debuff>> debuffs = new ArrayList<>();
        ArrayList<ArrayList<Effect>> otherEffects = new ArrayList<>();
        
        for (Monster mon : monsters)
        {
            if (mon.isDead())
            {
                //Add dead String
                names.add("" + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + mon.getName(false, true) + ConsoleColor.RESET);
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
                    names.add("" + ConsoleColor.BLACK_BOLD + ((enemyTeam == 1) ? element.relationWith(mon.getElement()) :
                            ConsoleColor.GREEN_BACKGROUND) + mon.getName(false, true) + ConsoleColor.RESET);
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
                //Add effects
                buffs.add(mon.getAppliedBuffs());
                debuffs.add(mon.getAppliedDebuffs());
                otherEffects.add(mon.getOtherEffects());
            }
        }
        
        //Return formatted String
        return CONSOLE_INTERFACE.OUTPUT.formatLines(names, hp, atkBar, buffs, debuffs, otherEffects);
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
            return "%s%s%s%s (%d)%s".formatted(ConsoleColor.GREEN_BACKGROUND, ConsoleColor.BLACK_BOLD, mon.shortToString(false), ConsoleColor.PURPLE, count, ConsoleColor.RESET);
        }
        
        return "%s%s (%d)%s".formatted(mon.shortToString(true), ConsoleColor.PURPLE, count, ConsoleColor.RESET);
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
            monsters.replaceAll(MONSTERS::createNewMonFromMon);
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