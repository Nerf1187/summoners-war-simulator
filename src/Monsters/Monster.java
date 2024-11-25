package Monsters;

import Abilities.*;
import Errors.*;
import Game.*;
import Monsters.Fire.*;
import Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.math.*;
import java.util.*;

import static Game.Main.scan;

/**
 * The parent class for all Monsters. This class contains all methods used by Monsters.
 *
 * @author Anthony (Tony) Youssef
 */
public class Monster
{
    /**
     * Contains the name and element of every monster
     */
    public static HashMap<String, String> monsterNamesDatabase = new HashMap<>();
    
    /**
     * Denotes the value for an attack bar to be full
     */
    public static final int MAX_ATK_BAR_VALUE = 1_000;
    private static boolean print = true;
    
    //Global elements
    public static final int FIRE = 0, WATER = 1, WIND = 2, LIGHT = 3, DARK = 4, ALL = 5;
    private final int MAX_BOMBS = 3, MAX_DOT = 8;
    private String name;
    
    //Monster stats
    private int currentHp, maxHp, destroyedHp = 0, def, atk, spd, critRate, critDmg, resistance, accuracy;
    private final int element;
    protected static Game game = null;
    
    //Used when applying runes
    private double tempMaxHp, tempDef, tempAtk;
    
    //Used to apply buffs/debuffs
    private int extraAtk = 0, extraDef = 0, extraCritRate = 0, shield = 0;
    protected int extraSpd = 0;
    
    //Monster's base stats (unchanging)
    private final int baseMaxHp, baseAtk, baseDef, baseSpd, baseCritRate, baseCritDmg, baseRes, baseAcc;
    private int extraGlancingRate, lessAtk, lessDef;
    protected int lessAtkSpd;
    private int abilityGlancingRateChange;
    private int numOfViolentRuneProcs = 0;
    private double dmgDealtThisTurn, dmgTakenThisTurn;
    private double atkBar = 0;
    private boolean dead = false, crit = false, glancing = false, wasCrit = false, singleTargetAttack = false;
    private ArrayList<Buff> appliedBuffs = new ArrayList<>();
    private ArrayList<Debuff> appliedDebuffs = new ArrayList<>();
    private ArrayList<Stat> otherStats = new ArrayList<>();
    private ArrayList<Ability> abilities;
    private ArrayList<Rune> runes = new ArrayList<>();
    
    /**
     * Creates a new Monster
     *
     * @param name       The name of the Monster
     * @param element    The element of the Monster. FIRE = 0, WATER = 1. WIND = 2, LIGHT = 3, DARK = 4
     * @param hp         The base health of the Monster
     * @param def        The base defense of the Monster
     * @param attack     The base attack power of the Monster
     * @param speed      The base attack speed of the Monster
     * @param critRate   The base crit hit rate of the Monster (as a percent)
     * @param critDmg    The base crit damage amount of the Monster (as a percent)
     * @param resistance The base resistance of the Monster (as a percent)
     * @param accuracy   The base accuracy of the Monster (as a percent)
     */
    public Monster(String name, int element, int hp, int def, int attack, int speed, int critRate, int critDmg, int resistance, int accuracy)
    {
        //Make sure no Monster's speed is changed for testing
        if (speed > 400)
        {
            System.out.printf("Speed must be less than 500: %s%n", name);
            throw new RuntimeException("Speed must be less than 500: %s".formatted(name));
        }
        
        //Monster info
        this.name = name;
        this.element = element;
        
        //Base stats
        baseAtk = attack;
        baseDef = def;
        baseSpd = speed;
        baseCritRate = critRate;
        baseCritDmg = critDmg;
        baseRes = resistance;
        baseAcc = accuracy;
        baseMaxHp = hp;
        
        //Temporary values for calculating rune effects
        tempMaxHp = hp;
        tempAtk = attack;
        tempDef = def;
        
        //Final stats
        this.maxHp = hp;
        currentHp = hp;
        this.def = def;
        this.atk = attack;
        this.spd = speed;
        this.critRate = critRate;
        this.critDmg = critDmg;
        this.resistance = resistance;
        this.accuracy = accuracy;
    }
    
    /**
     * Creates a new blank Monster with the specified element
     *
     * @param element The element of the Monster
     */
    public Monster(int element)
    {
        this("", element, 0, 0, 0, 0, 15, 50, 15, 0);
    }
    
    /**
     * Creates a blank Monster
     */
    public Monster()
    {
        this(0);
    }
    
    /**
     * Checks whether the program is currently set to print to console
     *
     * @return True if printing to the console is activated
     */
    public static boolean isPrint()
    {
        return print;
    }
    
    /**
     * Sets whether printing the console is activated (on by default
     *
     * @param print True for on, false for off
     */
    public static void setPrint(boolean print)
    {
        Monster.print = print;
    }
    
    /**
     * Sets the abilities for the Monsters
     *
     * @param abilities The abilities to set
     */
    public void setAbilities(ArrayList<Ability> abilities)
    {
        this.abilities = abilities;
    }
    
    /**
     * Gets the Monster's abilities
     *
     * @return The Monster's abilities
     */
    public ArrayList<Ability> getAbilities()
    {
        return abilities;
    }
    
    /**
     * Sets the rune set to the provided List
     *
     * @param runes The Monster's rune set
     */
    public void setRunes(ArrayList<Rune> runes)
    {
        //Create temporary values for rounding
        tempMaxHp = baseMaxHp;
        tempDef = baseDef;
        tempAtk = baseAtk;
        spd = baseSpd;
        critRate = baseCritRate;
        critDmg = baseCritDmg;
        resistance = baseRes;
        accuracy = baseAcc;
        
        this.runes = runes;
        //Apply each rune
        for (Rune rune : runes)
        {
            rune.apply();
        }
        
        //Round each stat up
        tempMaxHp = Math.ceil(tempMaxHp);
        tempDef = Math.ceil(tempDef);
        tempAtk = Math.ceil(tempAtk);
        
        //Apply rune set effects
        applyRuneSetEffectsForBeginningOfGame();
        
        //Round stats up
        maxHp = (int) Math.ceil(tempMaxHp);
        def = (int) Math.ceil(tempDef);
        atk = (int) Math.ceil(tempAtk);
        currentHp = maxHp;
    }
    
    /**
     * Gets this Monster's runes
     *
     * @return This Monster's runes
     */
    public ArrayList<Rune> getRunes()
    {
        return runes;
    }
    
    /**
     * Change the Monster's name
     *
     * @param name The new name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Gives the name of the Monster with its associated number and element if desired
     *
     * @param withElement True if the elemental color is included
     * @param withNumber  True if the Monster's number is included
     * @return The Monster's name with its element and color if wanted
     */
    public String getName(boolean withElement, boolean withNumber)
    {
        if (!withElement)
        {
            if (withNumber)
            {
                //Return the name followed by ID number
                return name;
            }
            String returnName = "";
            //Remove the ID number and return the result
            for (char c : name.toCharArray())
            {
                if (!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '(' || c == ')'))
                {
                    returnName += c;
                }
            }
            return returnName;
        }
        //Get the element color
        String elementColor = switch (element)
        {
            case FIRE -> ConsoleColors.RED_BOLD_BRIGHT;
            case WATER -> ConsoleColors.BLUE_BOLD_BRIGHT;
            case WIND -> ConsoleColors.YELLOW_BOLD_BRIGHT;
            case LIGHT -> ConsoleColors.WHITE_BOLD_BRIGHT;
            case DARK -> ConsoleColors.PURPLE_BOLD_BRIGHT;
            default -> "";
        };
        //Return the name with the element followed by the ID number
        if (withNumber)
        {
            return elementColor + name + ConsoleColors.RESET;
        }
        String returnName = "";
        //Remove the ID number from the name
        for (char c : name.toCharArray())
        {
            if (!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '(' || c == ')'))
            {
                returnName += c;
            }
        }
        //Return the name with the element
        return elementColor + returnName + ConsoleColors.RESET;
    }
    
    /**
     * Change the Monster's resistance
     *
     * @param resistance The new resistance
     */
    public void setResistance(double resistance)
    {
        this.resistance = (int) Math.ceil(resistance);
    }
    
    /**
     * Gets the Monster's current resistance
     *
     * @return The Monster's current resistance
     */
    public int getResistance()
    {
        return resistance;
    }
    
    /**
     * Change the Monster's accuracy
     *
     * @param accuracy The new accuracy
     */
    public void setAccuracy(double accuracy)
    {
        this.accuracy = (int) Math.ceil(accuracy);
    }
    
    /**
     * Gets the Monster's current accuracy
     *
     * @return The Monster's current accuracy
     */
    public int getAccuracy()
    {
        return accuracy;
    }
    
    /**
     * Sets the Monster's attack power
     *
     * @param atk The Monster's new attack power
     */
    public void setAtk(double atk)
    {
        this.atk = (int) Math.ceil(atk);
    }
    
    /**
     * @return The Monster's current attack stat (Does not include buffs/debuffs)
     */
    public int getAtk()
    {
        return atk;
    }
    
    /**
     * Gets the Monster's base attack
     *
     * @return The Monster's base attack
     */
    public int getBaseAtk()
    {
        return baseAtk;
    }
    
    /**
     * Change the Monster's defense
     *
     * @param def The Monster's new defense
     */
    public void setDef(double def)
    {
        this.def = (int) Math.ceil(def);
    }
    
    /**
     * @return The Monster's current defense stat (Does not include buffs/debuffs)
     */
    public int getDef()
    {
        return def;
    }
    
    /**
     * Gets the Monster's base defense
     *
     * @return The Monster's base defense
     */
    public int getBaseDef()
    {
        return baseDef;
    }
    
    /**
     * Set the Monster's temporary max health
     *
     * @param hp The Monster's new temporary max health
     */
    public void setTempMaxHp(double hp)
    {
        tempMaxHp = hp;
    }
    
    /**
     * Gets the Monster's temporary max health
     *
     * @return The Monster's temporary max health
     */
    public double getTempMaxHp()
    {
        return tempMaxHp;
    }
    
    /**
     * Set the Monster's temporary attack
     *
     * @param tempAtk The Monster's new temporary attack
     */
    public void setTempAtk(double tempAtk)
    {
        this.tempAtk = tempAtk;
    }
    
    /**
     * Gets the Monster's temporary attack
     *
     * @return The Monster's temporary attack
     */
    public double getTempAtk()
    {
        return tempAtk;
    }
    
    /**
     * Set the Monster's temporary defense
     *
     * @param tempDef The Monster's new defense
     */
    public void setTempDef(double tempDef)
    {
        this.tempDef = tempDef;
    }
    
    /**
     * Gets the Monster's temporary defense
     *
     * @return The Monster's temporary defense
     */
    public double getTempDef()
    {
        return tempDef;
    }
    
    /**
     * Gets the Monster's element
     *
     * @return The Monster's element
     */
    public int getElement()
    {
        return element;
    }
    
    /**
     * Change the Monster's current health
     *
     * @param currentHp The Monster's new health
     */
    public void setCurrentHp(int currentHp)
    {
        this.currentHp = Math.min(currentHp, maxHp - destroyedHp);
    }
    
    /**
     * Gets the Monster's current health
     *
     * @return The Monster's current health
     */
    public int getCurrentHp()
    {
        return currentHp;
    }
    
    /**
     * Sets the Monster's max Hp
     *
     * @param maxHp The Monster's new max Hp
     */
    public void setMaxHp(double maxHp)
    {
        this.maxHp = (int) Math.ceil(maxHp);
    }
    
    /**
     * Gets the Monster's maximum health
     *
     * @return The Monster's maximum health
     */
    public int getMaxHp()
    {
        return maxHp;
    }
    
    /**
     * Gets the Monster's base maximum health
     *
     * @return The Monster's base maximum health
     */
    public int getBaseMaxHp()
    {
        return baseMaxHp;
    }
    
    /**
     * Sets the Monster's attack speed
     *
     * @param spd The Monster's new attack speed
     */
    public void setSpd(double spd)
    {
        this.spd = (int) Math.ceil(spd);
    }
    
    /**
     * Gets the Monster's current speed stat
     *
     * @return The Monster's current speed stat
     */
    public int getSpd()
    {
        return spd;
    }
    
    /**
     * Gets the Monster's base speed
     *
     * @return The Monster's base speed
     */
    public int getBaseSpd()
    {
        return baseSpd;
    }
    
    /**
     * Changes the Monster's critical hit rate
     *
     * @param critRate The Monster's new critical hit rate
     */
    public void setCritRate(double critRate)
    {
        this.critRate = (int) Math.ceil(critRate);
    }
    
    /**
     * Gets the Monster's current critical hit rate
     *
     * @return The Monster's current critical hit rate
     */
    public int getCritRate()
    {
        return critRate;
    }
    
    /**
     * Changes the Monster's critical damage
     *
     * @param critDmg The Monster's new critical damage
     */
    public void setCritDmg(double critDmg)
    {
        this.critDmg = (int) Math.ceil(critDmg);
    }
    
    /**
     * Gets the Monster's current critical damage
     *
     * @return The Monster's current critical damage
     */
    public int getCritDmg()
    {
        return critDmg;
    }
    
    /**
     * Sets the altered glancing rate as determined by an ability
     *
     * @param abilityGlancingRateChange The new value
     */
    public void setAbilityGlancingRateChange(int abilityGlancingRateChange)
    {
        this.abilityGlancingRateChange = abilityGlancingRateChange;
    }
    
    /**
     * Sets the current Game in use
     *
     * @param g The new Game
     */
    public static void setGame(Game g)
    {
        game = g;
    }
    
    /**
     * Gets the current game in use
     *
     * @return The current game in use
     */
    public static Game getGame()
    {
        return game;
    }
    
    /**
     * Gets all current buffs on the Monster
     *
     * @return All current buffs on the Monster
     */
    public ArrayList<Buff> getAppliedBuffs()
    {
        return appliedBuffs;
    }
    
    /**
     * Gets all current debuffs on the Monster
     *
     * @return All current debuffs on the Monster
     */
    public ArrayList<Debuff> getAppliedDebuffs()
    {
        return appliedDebuffs;
    }
    
    /**
     * @return All Stats (not buff or debuffs) on the Monster
     */
    public ArrayList<Stat> getOtherStats()
    {
        return otherStats;
    }
    
    /**
     * Set the attack bar to a given amount
     *
     * @param num The amount to set the attack bar to
     */
    public void setAtkBar(int num)
    {
        atkBar = Math.max(num, 0);
    }
    
    /**
     * Gets the current attack bar value
     *
     * @return The current attack bar value
     */
    public double getAtkBar()
    {
        return atkBar;
    }
    
    /**
     * Marks the Monster as alive (false) or dead (true)
     *
     * @param isDead The new dead state of the Monster
     */
    public void setDead(boolean isDead)
    {
        dead = isDead;
    }
    
    /**
     * Checks if the Monster is dead
     *
     * @return True if the Monster has zero health, false otherwise
     */
    public boolean isDead()
    {
        if (this.currentHp <= 0)
        {
            //Check for Endure buff
            if (this.containsBuff(Buff.ENDURE))
            {
                this.currentHp = 1;
            }
            //Check for Soul Protection buff
            if (this.containsBuff(Buff.SOUL_PROTECTION))
            {
                //Revive Monster with 30% HP
                currentHp = (int) (this.maxHp * 0.3);
                removeBuff(Buff.SOUL_PROTECTION);
            }
        }
        return this.currentHp <= 0;
    }
    
    /**
     * Resets all extra stat effects
     */
    private void resetStatEffects()
    {
        extraAtk = 0;
        lessAtk = 0;
        extraDef = 0;
        lessDef = 0;
        extraCritRate = 0;
        extraGlancingRate = 0;
        extraSpd = 0;
        lessAtkSpd = 0;
    }
    
    /**
     * Sets {@link Monster#monsterNamesDatabase}
     */
    public static void setDatabase()
    {
        //Read from the database file
        Scanner read = new Scanner(Objects.requireNonNull(Monster.class.getResourceAsStream("Monster database.csv")));
        while (read.hasNextLine())
        {
            String line = read.nextLine();
            //Skip empty lines
            if (line.isEmpty())
            {
                continue;
            }
            //Split the line between the Monster's name and element
            String[] monAndElement = line.split(",");
            //Add the info to the database variable
            monsterNamesDatabase.put(monAndElement[0], monAndElement[1]);
        }
        //Close the reader
        read.close();
    }
    
    /**
     * Gets an ArrayList containing a new instance of every Monster
     *
     * @return An ArrayList containing a new instance of every Monster
     */
    public static ArrayList<Monster> getMonstersFromDatabase()
    {
        //Set the database if it is empty
        if (monsterNamesDatabase.isEmpty())
        {
            setDatabase();
        }
        ArrayList<Monster> allMons = new ArrayList<>();
        
        //Add a new instance of each Monster in the database
        monsterNamesDatabase.forEach((name, _) -> allMons.add(createNewMonFromName(name)));
        
        return allMons;
    }
    
    /**
     * Gets the Monster's support abilities that apply multiple buffs
     *
     * @return The Monster's support abilities that apply multiple buffs
     */
    public ArrayList<Ability> getSupportAbilitiesWithMultipleBuffs()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            //Check if the ability applies more than 2 buffs
            if (ability.getBuffs().size() >= 2)
            {
                returnList.add(ability);
            }
        }
        return returnList;
    }
    
    /**
     * Gets the Monster's attack abilities
     *
     * @return The Monster's attack abilities
     */
    public ArrayList<Ability> getAttackAbilities()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        for (Ability ability : abilities)
        {
            //Check if the ability targets the enemy
            if (ability.targetsEnemy())
            {
                returnList.add(ability);
            }
        }
        return returnList;
    }
    
    /**
     * Gets how much damage this Monster has dealt this turn
     *
     * @return How much damage this Monster has dealt this turn
     */
    public double getDmgDealtThisTurn()
    {
        return dmgDealtThisTurn;
    }
    
    /**
     * Gets how much damage this Monster has taken this turn
     *
     * @return How much damage this Monster has taken this turn
     */
    public double getDmgTakenThisTurn()
    {
        return dmgTakenThisTurn;
    }
    
    /**
     * Calculates the current health ratio of the Monster
     *
     * @return The ratio of current health to max health of the Monster (0-100)
     */
    public double getHpRatio()
    {
        //Get the HP ratio
        double ratio = 100.0 * currentHp / maxHp;
        //Round the number and return the result
        return switch (ratio)
        {
            //Do nothing if the ratio is 0 or 100
            case 0.0, 100.0 -> ratio;
            //Round to 2 decimal places if the number is between 10 and 100
            case double i when i >= 10.0 -> BigDecimal.valueOf(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue();
            //Round to 3 decimal places if the number is between 0 and 10
            default -> BigDecimal.valueOf(ratio).setScale(3, RoundingMode.HALF_UP).doubleValue();
        };
    }
    
    /**
     * Finds the index of the given buff
     *
     * @param buff The buff to find
     * @return The index of the provided buff
     */
    public int getBuffIndex(Buff buff)
    {
        //Search through the array and return the index of the requested buff
        for (int i = 0; i < appliedBuffs.size(); i++)
        {
            if (appliedBuffs.get(i).equals(buff))
            {
                return i;
            }
        }
        //Return -1 if not found
        return -1;
    }
    
    /**
     * Finds the index of the buff with the given number
     *
     * @param num The buff number to search for
     * @return The index of the buff with the given number
     */
    public int getBuffIndex(int num)
    {
        return this.getBuffIndex(new Buff(num));
    }
    
    /**
     * Finds the index of the given debuff
     *
     * @param debuff The debuff to find
     * @return The index of the provided debuff. -1 if it is not found
     */
    public int getDebuffIndex(Debuff debuff)
    {
        //Search through the array and return the index of the requested buff
        for (int i = 0; i < appliedDebuffs.size(); i++)
        {
            if (appliedDebuffs.get(i).equals(debuff))
            {
                return i;
            }
        }
        //Return -1 if not found
        return -1;
    }
    
    /**
     * Returns the index of the debuff with the given debuff number
     *
     * @param debuff The number of the debuff to search for
     * @return The index of the debuff with the given debuff number. -1 if it is not found
     */
    public int getDebuffIndex(int debuff)
    {
        return getDebuffIndex(new Debuff(debuff, 0, 0));
    }
    
    /**
     * Returns the Ability associated with the given number if there is one
     *
     * @param num The Ability's number (The element number NOT the index number)
     * @return The Ability associated with the given number if there is one, returns null otherwise
     */
    public Ability getAbility(int num)
    {
        //Make sure the input number is within the range
        if (num > abilities.size() || num < 1)
        {
            return null;
        }
        //Return the ability
        return abilities.get(num - 1);
    }
    
    /**
     * Gets each ability that can be used this turn
     *
     * @return A list of viable ability numbers
     */
    public ArrayList<Integer> getViableAbilityNumbers()
    {
        ArrayList<Integer> returnArray = new ArrayList<>();
        //Search through each ability for ones that can be used this turn
        for (int i = 0; i < abilities.size(); i++)
        {
            Ability ability = abilities.get(i);
            if (ability.isViableAbility(this.containsDebuff(Debuff.SILENCE)))
            {
                returnArray.add(i + 1);
            }
        }
        //Return the list of numbers whose corresponding ability can be used
        return returnArray;
    }
    
    /**
     * Gets the Monster's Team support abilities if it has one
     *
     * @return The Monster's Team support abilities if it has one
     */
    public ArrayList<Heal_Ability> getTeamSupportAbilities()
    {
        ArrayList<Heal_Ability> returnList = new ArrayList<>();
        //Search through the abilities for instances of Heal Abilities
        for (Ability ability : abilities)
        {
            if (ability instanceof Heal_Ability ha)
            {
                //Add the Heal Ability
                returnList.add(ha);
            }
        }
        //Return the list of abilities
        return returnList;
    }
    
    /**
     * Gets the Monster's abilities that target itself
     *
     * @return The Monster's abilities that target itself
     */
    public ArrayList<Ability> getSelfSupportAbilities()
    {
        ArrayList<Ability> returnList = new ArrayList<>();
        //Search through the array for abilities that target this
        for (Ability ability : abilities)
        {
            if (ability.targetsSelf() && !(ability instanceof Passive))
            {
                returnList.add(ability);
            }
        }
        //Return the list of abilities
        return returnList;
    }
    
    /**
     * @return The Defend buff on the Monster if there is one, returns <code>null</code> otherwise
     */
    private Defend getDefend()
    {
        //Search through the array for a Defend buff
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Defend d)
            {
                return (d);
            }
        }
        return null;
    }
    
    /**
     * @return The Shield buff on the Monster if there is one, otherwise returns a new shield with zero health and zero turns remaining
     */
    private Shield getShield()
    {
        //Search through the array for a Shield buff
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Shield s)
            {
                return s;
            }
        }
        //Return a blank Shield if one was not found
        return new Shield(0, 0);
    }
    
    /**
     * @return The DecAtkBar on the Monster if it has one, otherwise returns a new DecAtkBar with a value of zero
     */
    private DecAtkBar getDecAtkBar()
    {
        //Search through the array for a DecAtkBar debuff
        for (Debuff d : appliedDebuffs)
        {
            if (d instanceof DecAtkBar dec)
            {
                return (dec);
            }
        }
        //return a blank DecAtkBar if there is one was not found
        return new DecAtkBar(0);
    }
    
    /**
     * @return The Provoke debuff on a Monster if there is one, returns <code>null</code> otherwise
     */
    public Provoke getProvoke()
    {
        //Search through the array for a Provoke Debuff
        for (Debuff debuff : appliedDebuffs)
        {
            if (debuff instanceof Provoke p)
            {
                return (p);
            }
        }
        return null;
    }
    
    /**
     * Returns the Monster's name and abilities in a readable form
     *
     * @return The formatted String
     */
    @Override
    public String toString()
    {
        String s = "";
        //Get the Monster's name with its ID and element
        s += "%s:\n".formatted(getName(true, true));
        //Add each ability except for leader skills
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill)
            {
                continue;
            }
            s += "\t %s\n\n".formatted(ability.toString(containsDebuff(Debuff.SILENCE), containsDebuff(Debuff.OBLIVION)));
        }
        return s;
    }
    
    /**
     * Formats the Monster into a shorter readable String.
     * <pre>
     * Format:
     * name (&lt;HP&gt;, &lt;Attack Bar&gt;)
     *      Buffs: [&lt;Current buffs&gt;]
     *      Debuffs: [&lt;Current debuffs&gt;]
     *      Other Effects: [&lt;Any other effects&gt;] (&lt;Place in team&gt;) </pre>
     *
     * @param withElement True if the method should return the Monster's name with its element, false otherwise
     * @return The shorter formatted String
     */
    public String shortToString(boolean withElement)
    {
        //Get the Monster's name
        String name = withElement ? getName(true, true) : this.name;
        //Get the Monster's HP and attack bar ratios
        String healthPercent = getHpRatio() + "";
        String atkBarPercent = 100.0 * atkBar / MAX_ATK_BAR_VALUE + "";
        
        //Format the String and return the result
        return """
                %s%s (Hp = %s%%%s, Attack Bar = %s%%)%s
                \t\tBuffs: %s%s
                \t\tDebuffs: %s%s
                \t\tOther Effects: %s%s""".formatted(name, ConsoleColors.GREEN, healthPercent.substring(0, healthPercent.indexOf(".") + 2), ConsoleColors.CYAN, atkBarPercent.substring(0, atkBarPercent.indexOf(".") + 2), ConsoleColors.BLUE, appliedBuffs, ConsoleColors.RED, appliedDebuffs, ConsoleColors.PURPLE, otherStats, ConsoleColors.RESET);
    }
    
    /**
     * Destroy an amount of HP
     *
     * @param amount The new amount to destroy
     */
    public void destroyHp(int amount)
    {
        //Max destroyed HP is 60% of max HP
        destroyedHp = (destroyedHp + amount > maxHp * 0.6) ? (int) (maxHp * 0.6) : destroyedHp + amount;
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param num    The buff number to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(int num, int turns, Monster caster)
    {
        //Increase the number of turns by one if this is the caster
        if (caster.equals(this) && turns > 0)
        {
            turns++;
        }
        //Make sure the buff is not a shield
        if (num == Buff.SHIELD)
        {
            throw new RuntimeException("Please use the other addAppliedBuff method for shields");
        }
        
        //Check if the Monster already has the buff
        if (this.containsBuff(num))
        {
            switch (num)
            {
                //Continuous healing can stack with itself, no other buff can.
                case Buff.RECOVERY -> appliedBuffs.add(new Buff(num, turns));
                case Buff.THREAT ->
                {
                    //Apply the new Threat if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(new Buff(num, 1))).getNumTurns() < turns)
                    {
                        removeBuff(num);
                        appliedBuffs.add(new Threat(turns));
                    }
                }
                case Buff.DEFEND ->
                {
                    //Apply the new Defend if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(new Buff(num, 1))).getNumTurns() < turns)
                    {
                        removeBuff(num);
                        appliedBuffs.add(new Defend(turns, caster));
                    }
                }
                default ->
                {
                    //Apply the new buff if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(num)).getNumTurns() < turns)
                    {
                        removeBuff(num);
                        appliedBuffs.add(new Buff(num, turns));
                    }
                }
            }
        }
        else //Newly applied buff
        {
            switch (num)
            {
                //Special cases
                case Buff.THREAT -> appliedBuffs.add(new Threat(turns));
                case Buff.DEFEND -> appliedBuffs.add(new Defend(turns, caster));
                default -> appliedBuffs.add(new Buff(num, turns));
            }
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param num    The buff number to add
     * @param chance The chance it will apply
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedBuff(int num, double chance, int turns, Monster caster)
    {
        int random = new Random().nextInt(101);
        //Apply the buff with chance%
        if (random <= chance)
        {
            addAppliedBuff(num, turns, caster);
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param buff   The buff to add
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(Buff buff, Monster caster)
    {
        switch (buff)
        {
            //Check if the buff is a shield
            case Shield possibleShield ->
            {
                //Do nothing if the shield has no health
                if (possibleShield.getAmount() == 0)
                {
                    return;
                }
                if (hasShield())
                {
                    //Apply the new shield if it has more health than the current one
                    if (getShield().getAmount() > possibleShield.getAmount() || getShield().getAmount() == possibleShield.getAmount() && getShield().getNumTurns() > possibleShield.getNumTurns())
                    {
                        return;
                    }
                    else //Remove the old shield
                    {
                        appliedBuffs.remove(getShield());
                        shield = 0;
                    }
                }
                appliedBuffs.add(possibleShield);
            }
            case IncAtkBar amount ->
            {
                //Increase the attack bar
                this.increaseAtkBarByPercent(amount.getAmount());
                this.removeBuff(amount);
            }
            case null ->
            {
            }
            //Apply the new buff
            default -> addAppliedBuff(buff.getBuffNum(), buff.getNumTurns(), caster);
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param buff   The buff to add
     * @param chance The chance it will apply
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(Buff buff, double chance, Monster caster)
    {
        int random = new Random().nextInt(101);
        //Apply the buff with chance%
        if (random <= chance)
        {
            addAppliedBuff(buff, caster);
        }
    }
    
    /**
     * Adds a new debuff to the Monster (Assumes the debuff does not go through immunity)
     *
     * @param num    The debuff number to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedDebuff(int num, int turns, Monster caster)
    {
        //Resistance check
        if (caster.resistanceCheck(this))
        {
            if (print)
            {
                System.out.println("Resisted!");
            }
            return;
        }
        //Check if the Monster already has the debuff
        if (this.containsDebuff(num))
        {
            //Bomb and DOT can stack
            //Provoke cannot be overridden
            if (num != Debuff.BOMB && num != Debuff.CONTINUOUS_DMG && num != Debuff.PROVOKE)
            {
                //Add the buff its turns are more than the one already applied
                if (appliedDebuffs.get(getDebuffIndex(num)).getNumTurns() < turns)
                {
                    this.removeDebuff(num);
                    appliedDebuffs.add(new Debuff(num, turns, 0));
                }
            }
            else
            {
                if (num == Debuff.BOMB)
                {
                    //Check for the number of bomb stacks
                    if (countDebuff(Debuff.BOMB) < MAX_BOMBS)
                    {
                        appliedDebuffs.add(new Debuff(Debuff.BOMB, turns, 0));
                    }
                }
                else
                {
                    //Check for the number of DOT stacks
                    if (countDebuff(Debuff.CONTINUOUS_DMG) < MAX_DOT)
                    {
                        appliedDebuffs.add(new Debuff(Debuff.CONTINUOUS_DMG, turns, 0));
                    }
                }
            }
        }
        //Add new Provoke
        else if (num == Debuff.PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        //Add the debuff
        else
        {
            appliedDebuffs.add(new Debuff(num, turns, 0));
        }
    }
    
    /**
     * Adds a new debuff to the Monster that ignores resistance (Still checks for immunity buff)
     *
     * @param debuff The debuff to add
     * @param caster The Monster who gave it
     */
    public void addGuaranteedAppliedDebuff(Debuff debuff, Monster caster)
    {
        //Add the caster to the debuff
        debuff.setCaster(caster);
        //Do nothing if this has immunity and the debuff does not go through immunity
        if (this.containsBuff(Buff.IMMUNITY) && !debuff.goesThroughImmunity())
        {
            return;
        }
        int num = debuff.getDebuffNum();
        int turns = debuff.getNumTurns();
        if (this.containsDebuff(num))
        {
            //Bomb and DOT can stack, provoke cannot be overridden
            if (num != Debuff.BOMB && num != Debuff.CONTINUOUS_DMG && num != Debuff.PROVOKE)
            {
                //Add the debuff if it has more turns than the one already applied
                if (appliedDebuffs.get(getDebuffIndex((num))).getNumTurns() < turns)
                {
                    removeDebuff(num);
                    appliedDebuffs.add(debuff);
                }
            }
            else
            {
                if (num == Debuff.BOMB)
                {
                    //Check for the number of bomb stacks
                    if (countDebuff(Debuff.BOMB) < MAX_BOMBS)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
                else
                {
                    //Check for the number of DOT stacks
                    if (countDebuff(Debuff.CONTINUOUS_DMG) < MAX_DOT)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
            }
        }
        //Add a new Provoke
        else if (num == Debuff.PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        else //Add the new debuff
        {
            appliedDebuffs.add(debuff);
        }
    }
    
    /**
     * Adds a new debuff to the Monster that ignores resistance (Still checks for immunity buff)
     *
     * @param debuff The number of the debuff to add
     * @param turns  The number of turns to apply the debuff
     * @param caster The Monster who gave it
     */
    public void addGuaranteedAppliedDebuff(int debuff, int turns, Monster caster)
    {
        addGuaranteedAppliedDebuff(new Debuff(debuff, turns, 0), caster);
    }
    
    /**
     * Adds a new debuff to the Monster (Assumes debuff does not go through immunity
     *
     * @param num    The debuff number to add
     * @param chance The chance the debuff will be applied (0-100)
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(int num, double chance, int turns, Monster caster)
    {
        //Check for immunity buff
        if (this.containsBuff(Buff.IMMUNITY))
        {
            if (print)
            {
                System.out.printf("%sImmunity!%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
            return;
        }
        //Resistance check
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedDebuff(num, turns, caster);
        }
    }
    
    /**
     * Adds a new debuff to the Monster
     *
     * @param debuff The debuff to add
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(Debuff debuff, Monster caster)
    {
        //Check for DecAtkBar and Shorten_Debuff
        if (debuff instanceof DecAtkBar || debuff instanceof Shorten_Buff)
        {
            //Resistance check
            int resRate = new Random().nextInt(101);
            if (resRate <= Math.max(15, Math.min(resistance, 100) - Math.min(accuracy, 100)))
            {
                if (print)
                {
                    System.out.println("Resisted!");
                }
                return;
            }
            
            //Add the debuff
            appliedDebuffs.add(debuff);
            return;
        }
        addAppliedDebuff(debuff.getDebuffNum(), debuff.getNumTurns(), caster);
    }
    
    /**
     * Adds a new debuff to the Monster
     *
     * @param debuff The debuff to add
     * @param chance The chance it will apply (0-100)
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(Debuff debuff, double chance, Monster caster)
    {
        //Immunity check
        if (this.containsBuff(Buff.IMMUNITY) && !debuff.goesThroughImmunity())
        {
            if (print)
            {
                System.out.printf("%sImmunity!%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
            return;
        }
        //Apply the debuff with chance%
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedDebuff(debuff, caster);
        }
    }
    
    /**
     * Removes all instances of a debuff
     *
     * @param debuff The debuff to remove
     */
    public void removeDebuff(Debuff debuff)
    {
        //Search through the array for all instances of the debuff
        for (int i = appliedDebuffs.size() - 1; i >= 0; i--)
        {
            if (appliedDebuffs.get(i).equals(debuff))
            {
                appliedDebuffs.remove(i);
            }
        }
    }
    
    /**
     * Removes all instances of a debuff
     *
     * @param debuff The number of the debuff to remove
     */
    public void removeDebuff(int debuff)
    {
        removeDebuff(new Debuff(debuff));
    }
    
    /**
     * Removes all instances of a buff
     *
     * @param buff The buff to remove
     */
    public void removeBuff(Buff buff)
    {
        //Search through the array for all instances of the buff
        for (int i = appliedBuffs.size() - 1; i >= 0; i--)
        {
            if (appliedBuffs.get(i).equals(buff))
            {
                appliedBuffs.remove(i);
            }
        }
    }
    
    /**
     * Removes all instances of a buff
     *
     * @param num The buff number to remove
     */
    public void removeBuff(int num)
    {
        removeBuff(new Buff(num));
    }
    
    /**
     * Counts the number of instances of the given buff are on the Monster
     *
     * @param buff The buff to count
     * @return The number of instances of the given buff
     */
    public int countBuff(Buff buff)
    {
        int count = 0;
        //Search through the array for all instances of the buff
        for (Buff b : appliedBuffs)
        {
            if (b.equals(buff))
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of instances of the given buff are on the Monster
     *
     * @param buff The number of the buff to count
     * @return The number of instances of the given buff
     */
    public int countBuff(int buff)
    {
        return countBuff(new Buff(buff));
    }
    
    /**
     * Counts the number of instances of the given debuff are on the Monster
     *
     * @param debuff The debuff to count
     * @return The number of instances of the given debuff
     */
    public int countDebuff(Debuff debuff)
    {
        int count = 0;
        //Search through the array for all instances of the debuff
        for (Debuff d : appliedDebuffs)
        {
            if (d.equals(debuff))
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of instances of the given debuff are on the Monster
     *
     * @param debuff The number of the debuff to count
     * @return The number of instances of the given debuff
     */
    public int countDebuff(int debuff)
    {
        return countDebuff(new Debuff(debuff));
    }
    
    /**
     * Calculates the base damage for the Monster to deal on this turn
     *
     * @param multiplier The ability multiplier
     * @param target     The Monster being attacked
     * @return The base damage to deal for this turn
     */
    public int calculateBaseDamage(double multiplier, Monster target)
    {
        //Attack * Multiplier
        double dmg = ((atk + extraAtk - lessAtk) * multiplier);
        
        //Calculate crit rate
        int critRate = (target.containsBuff(Buff.CRIT_RESIST_UP)) ? (int) (this.critRate * 0.7) : this.critRate;
        String elementRelation = Team.elementalRelationship(element, target.element);
        //Change crit rate based on the elemental relationship
        int critChanceChange = 0;
        if (elementRelation.equals(ConsoleColors.GREEN_BACKGROUND))
        {
            critChanceChange = 15;
        }
        else if (elementRelation.equals(ConsoleColors.RED_BACKGROUND))
        {
            critChanceChange = -20;
        }
        //Crit
        if (new Random().nextInt(101) <= (critRate + extraCritRate - extraGlancingRate + critChanceChange))
        {
            //Current dmg * 1.critDmg
            dmg *= (1 + ((this.critDmg) / 100.0));
            crit = true;
        }
        //Glancing
        else if (new Random().nextInt(101) <= (30 + extraGlancingRate - critChanceChange + abilityGlancingRateChange))
        {
            //Current dmg * 0.7
            dmg *= 0.7;
            glancing = true;
        }
        //Random damage within range of calculation
        return Math.max(1, new Random().nextInt((int) dmg - 100, (int) dmg + 100));
    }
    
    /**
     * Calculates the base damage reduction for the Monster
     *
     * @return The base damage reduction
     */
    public double calculateBaseDmgReduction()
    {
        //1,000 / (1140 +3.5d)
        return (1_000 / (1_140 + 3.5 * (def + extraDef - lessDef)));
    }
    
    /**
     * Deals an amount of damage to the Monster
     *
     * @param dmg                 The amount of damage to deal
     * @param ignoresDmgReduction True if the damage should ignore damage reduction effects (such as shields)
     */
    public void dealDmg(double dmg, boolean ignoresDmgReduction)
    {
        //Deal damage to the shield
        if (!ignoresDmgReduction)
        {
            shield -= (int) Math.ceil(dmg);
            getShield().decreaseShieldHealth((int) Math.ceil(dmg));
        }
        //Deal damage directly to the HP
        else
        {
            currentHp -= (int) Math.ceil(dmg);
        }
        //Remove the shield if it has no health left and apply the leftover damage to the HP
        if (shield < 0)
        {
            removeBuff(Buff.SHIELD);
            currentHp += shield;
            shield = 0;
        }
    }
    
    /**
     * Increases the attack bar by the default amount (7% of the Monster's attack speed) if the Monster is alive, does nothing otherwise
     */
    public void increaseAtkBar()
    {
        //Do nothing if the Monster is dead
        if (dead)
        {
            atkBar = 0;
            return;
        }
        //Increase the attack bar by 7% of the speed
        atkBar += (spd + extraSpd - lessAtkSpd) * 0.07;
    }
    
    /**
     * Checks if the Monster has a full attack bar
     *
     * @return True if the Monster has a full attack bar, false otherwise
     */
    public boolean hasFullAtkBar()
    {
        return atkBar >= MAX_ATK_BAR_VALUE;
    }
    
    /**
     * Increases the attack bar by a given amount
     *
     * @param num The amount to increase the attack bar
     */
    public void increaseAtkBar(double num)
    {
        atkBar += Math.max(num, 0);
    }
    
    /**
     * Increases the attack bar by a given amount
     *
     * @param num The percent to increase the attack bar (0-100)
     */
    public void increaseAtkBarByPercent(int num)
    {
        //Prevent the attack bar from going negative
        atkBar += (int) Math.max(Math.ceil(MAX_ATK_BAR_VALUE * (num / 100.0)), 0);
    }
    
    /**
     * Decreases the attack bar by a given amount
     *
     * @param num The percent to decrease the attack bar (0-100)
     */
    public void decreaseAtkBarByPercent(int num)
    {
        increaseAtkBarByPercent(-num);
    }
    
    /**
     * Checks if the given ability number is valid to use
     *
     * @param abilityNum The ability number to check
     * @return True if the ability associated with the given number can be used on this turn, false otherwise
     */
    public boolean abilityIsValid(int abilityNum)
    {
        abilityNum--;
        return abilityNum >= 0 && abilityNum < abilities.size() && !abilities.get(abilityNum).isPassive() && !(abilities.get(abilityNum) instanceof Leader_Skill) && abilities.get(abilityNum).getTurnsRemaining() <= 0;
    }
    
    /**
     * Checks if the given ability is valid to use
     *
     * @param ability The ability to check
     * @return True if the given ability can be used on this turn, false otherwise
     */
    public boolean abilityIsValid(Ability ability)
    {
        return !ability.isPassive() && !(ability instanceof Leader_Skill) && ability.getTurnsRemaining() <= 0;
    }
    
    /**
     * Checks if the given target can be attacked/healed
     *
     * @param target        The targeted Monster to check
     * @param enemyIsTarget True if target and acting Monster are on opposite teams, false otherwise
     * @return True if the given target can be attacked/healed
     */
    public boolean targetIsValid(Monster target, boolean enemyIsTarget)
    {
        //Provoke check
        Provoke p = this.getProvoke();
        if (p != null)
        {
            return p.getCaster().equals(target);
        }
        //Check if the Monster is dead
        if (target.isDead())
        {
            return false;
        }
        //Check the other team for any Threat buffs
        for (Monster other : game.getOtherTeam().getMonsters())
        {
            if (other.hasThreat() && enemyIsTarget && target.equals(other))
            {
                return true;
            }
        }
        return true;
    }
    
    /**
     * Starts the Monster's turn
     *
     * @param target     The targeted Monster
     * @param abilityNum The ability number to use
     * @return True if the turn completed successfully, false otherwise
     */
    public boolean nextTurn(Monster target, int abilityNum)
    {
        abilityNum--;
        //Check if the Monster is dead
        if (this.isDead())
        {
            return false;
        }
        
        
        //Get chosen ability
        Ability a = abilities.get(abilityNum);
        
        //Make sure the ability and targets are valid
        if (!targetIsValid(target, a.targetsEnemy()) || !abilityIsValid(a))
        {
            return false;
        }
        
        resetStatEffects();
        
        //Make sure the ability's cooldown is over
        if (a.getTurnsRemaining() == 0)
        {
            //Reset the attack bar
            atkBar = 0;
            if ((a.targetsEnemy()))
            {
                if (a.targetsAllTeam()) //Attack the entire team
                {
                    attackTeam(game.getOtherTeam(), a);
                }
                else //Attack a single target
                {
                    attack(target, a, false);
                }
            }
            else //Heal Ability
            {
                if (a.targetsAllTeam()) //Heal the entire team
                {
                    healTeam(game.getNextMonsTeam(), a);
                }
                else //Heal a single target
                {
                    heal(target, a);
                }
            }
            //Set the ability to its max cooldown
            a.setToMaxCooldown();
        }
        else
        {
            //Print an error message
            if (print)
            {
                System.out.printf("Ability on cooldown (%d turns remaining)\n", a.getTurnsRemaining());
            }
            return false;
        }
        
        //Cleanse
        if (this.containsBuff(Buff.CLEANSE))
        {
            cleanse();
        }
        
        //Apply shield
        if (this.hasShield() && shield == 0)
        {
            shield = getShield().getAmount();
        }
        
        return true;
    }
    
    /**
     * The Monster's basic attack command. This method will execute all functions that are needed during the Monster's turn
     *
     * @param target    The target Monster to attack
     * @param ability   The ability to attack with
     * @param isCounter True if this attack is a counter, false otherwise
     * @param count     The number of times this method has been called this turn
     */
    protected void attack(Monster target, Ability ability, boolean isCounter, int count)
    {
        this.singleTargetAttack = true;
        if (print)
        {
            System.out.println("\n");
        }
        
        //Make sure booleans do not conflict with each other
        if (Game.canCounter() && isCounter)
        {
            throw new ConflictingArguments("%s: canCounter and isCounter can not both be true".formatted(getName(true, true)));
        }
        
        //Apply stat effects
        applyBeginningOfTurnBuffs();
        applyBeginningOfTurnDebuffs();
        
        //Mak sure the Monster is not stunned
        if (isStunned())
        {
            return;
        }
        
        //Calculate base damage
        double finalDmg = calculateBaseDamage(ability.getDmgMultiplier(), target);
        
        //Multiply the base damage by the base damage reduction
        finalDmg *= (ability.ignoresDefense()) ? new Monster(target.element).calculateBaseDmgReduction() : target.calculateBaseDmgReduction();
        
        String ignoreDef = (ability.ignoresDefense()) ? "%sIgnore Defense! %s".formatted(ConsoleColors.RED, ConsoleColors.RESET) : " ";
        
        //Brand
        if (target.containsDebuff(Debuff.BRAND))
        {
            finalDmg *= 1.25;
        }
        
        //Invincibility
        if (!ability.ignoresDmgReduction() && target.containsBuff(Buff.INVINCIBILITY))
        {
            finalDmg = 0;
            if (print)
            {
                System.out.printf("%s has invincibility!\n", target.getName(true, true));
            }
        }
        
        //Reduce damage if the attack is a counter
        if (isCounter)
        {
            finalDmg *= 0.7;
        }
        
        //Reflect damage if the target has the Reflect buff
        if (target.containsBuff(Buff.REFLECT))
        {
            dealDmg(finalDmg * 0.3, false);
            if (print)
            {
                System.out.printf("%s%d damage reflected.%s", ConsoleColors.PURPLE, (int) (finalDmg * 0.3), ConsoleColors.RESET);
            }
            finalDmg *= 0.7;
        }
        
        //Defend buff
        boolean defend = false;
        Monster tempTarget = target;
        if (!ability.ignoresDmgReduction() && target.hasDefend())
        {
            if (print)
            {
                System.out.printf("%sDefend! %s%n", ConsoleColors.BLUE, ConsoleColors.RESET);
            }
            Defend def = target.getDefend();
            if (def != null)
            {
                //Change target and reduce damage
                target = def.getCaster();
                finalDmg /= 2;
                defend = true;
                tempTarget = target;
            }
        }
        
        //Reduce damage if target has Threat
        if (target.hasThreat())
        {
            finalDmg *= 0.85;
        }
        
        //Remove sleep if needed
        target.removeDebuff(Debuff.SLEEP);
        tempTarget.removeDebuff(Debuff.SLEEP);
        
        //Apply damage reduction
        if (!ability.ignoresDmgReduction())
        {
            finalDmg = game.getOtherTeam().dmgReduction(game.getOtherTeam(), finalDmg, target);
        }
        
        //Activate damage increasing passive if there is one
        finalDmg = dmgIncProtocol(finalDmg);
        
        //Deal damage to target
        tempTarget.dealDmg(finalDmg, ability.ignoresDmgReduction());
        dmgDealtThisTurn += finalDmg;
        target.dmgTakenThisTurn += finalDmg;
        
        //Print crit or glancing hit messages
        if (crit)
        {
            if (print)
            {
                System.out.printf("%sCritical hit! %s", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
        }
        if (glancing)
        {
            if (print)
            {
                System.out.printf("%sGlancing hit! %s", ConsoleColors.YELLOW, ConsoleColors.RESET);
            }
        }
        
        //Print damage dealt
        if (print)
        {
            System.out.printf("%s%s%s dealt %,d damage to %s.\n%s", ignoreDef, getName(true, true), ConsoleColors.PURPLE,
                    (int) finalDmg, tempTarget.getName(true, true), ConsoleColors.RESET);
        }
        
        target.wasCrit = crit;
        
        crit = false;
        glancing = false;
        
        //After hit passive
        this.selfAfterHitProtocol(target, this.abilities.indexOf(ability) + 1);
        
        //Vampire Buff
        if (containsBuff(Buff.VAMPIRE) && !containsDebuff(Debuff.UNRECOVERABLE))
        {
            this.setCurrentHp((int) (currentHp + (0.2 * finalDmg)));
        }
        
        //Apply buffs to self if Monster does not have the beneficial effect block debuff
        if (!this.containsDebuff(Debuff.BLOCK_BENEFICIAL_EFFECTS))
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                this.addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
            }
        }
        
        //Apply debuffs to target if Monster does not have immunity and the attack was not a glancing hit
        int increasedChance = 0;
        if (crit)
        {
            increasedChance = 20;
        }
        else if (glancing)
        {
            increasedChance = -999_999;
        }
        ArrayList<Debuff> debuffs = ability.getDebuffs();
        ArrayList<Integer> debuffsChance = ability.getDebuffsChance();
        for (int i = 0; i < debuffs.size(); i++)
        {
            target.addAppliedDebuff(debuffs.get(i), debuffsChance.get(i) + increasedChance, this);
        }
        if (print)
        {
            System.out.println("\n");
        }
        
        //Seal Rune
        if (this.numOfSets(Rune.SEAL) > 2 && !this.containsDebuff(Debuff.SEAL))
        {
            double chance = 25;
            if (glancing)
            {
                chance /= 2;
            }
            target.addAppliedDebuff(Debuff.SEAL, chance * this.numOfSets(Rune.SEAL), 1, this);
        }
        
        //Decrease Atk Bar
        if (target.hasDecAtkBar())
        {
            DecAtkBar dec = target.getDecAtkBar();
            target.decreaseAtkBarByPercent(dec.getAmount());
            target.removeDebuff(dec);
            if (print)
            {
                System.out.printf("%sDecreased %s's Attack bar by %d%%!%s%n", ConsoleColors.RED, target.getName(true, true), dec.getAmount(), ConsoleColors.RESET);
            }
        }
        
        //Buff steal
        if (this.containsBuff(Buff.BUFF_STEAL))
        {
            this.stealBuff(target);
        }
        
        //Remove beneficial effect
        if (target.containsDebuff(Debuff.REMOVE_BENEFICIAL_EFFECT))
        {
            target.removeRandomBuff();
            target.removeDebuff(Debuff.REMOVE_BENEFICIAL_EFFECT);
        }
        
        //Strip
        if (target.containsDebuff(Debuff.STRIP))
        {
            this.removeBuff(Buff.THREAT);
            while (!this.appliedBuffs.isEmpty())
            {
                this.decreaseStatCooldowns();
            }
        }
        
        //Shorten beneficial effect
        if (target.containsDebuff(Debuff.SHORTEN_BUFFS))
        {
            Shorten_Buff sb = (Shorten_Buff) target.getAppliedDebuffs().get(target.getDebuffIndex(Debuff.SHORTEN_BUFFS));
            for (Buff buff : target.getAppliedBuffs())
            {
                buff.decreaseTurn(sb.getAmount());
            }
        }
        
        //Remove harmful effect
        if (this.containsBuff(Buff.REMOVE_DEBUFF))
        {
            this.removeRandomDebuff();
            this.removeBuff(Buff.REMOVE_DEBUFF);
        }
        
        //Target after hit passive
        target.targetAfterHitProtocol(this);
        if (!tempTarget.equals(target))
        {
            tempTarget.targetAfterHitProtocol(this);
        }
        
        //Check if the target is now dead
        if (tempTarget.getCurrentHp() <= 0)
        {
            tempTarget.kill();
            return;
        }
        
        //Vampire Rune
        if (!containsDebuff(Debuff.UNRECOVERABLE) && !containsDebuff(Debuff.SEAL))
        {
            this.setCurrentHp((int) Math.ceil((currentHp + (finalDmg * 0.35 * numOfSets(Rune.VAMPIRE)))));
        }
        
        //Destroy Rune
        if (numOfSets(Rune.DESTROY) > 0 && !containsDebuff(Debuff.SEAL))
        {
            //Max 4% of target's max HP per Destroy set
            int percentToDestroy = 4 * numOfSets(Rune.DESTROY);
            
            //30% of damage dealt
            double amountToDestroy = 0.3 * finalDmg;
            if (amountToDestroy > maxHp * (percentToDestroy / 100.0))
            {
                amountToDestroy = maxHp * (percentToDestroy / 100.0);
            }
            this.destroyHp((int) Math.ceil(amountToDestroy));
        }
        
        //Nemesis Rune
        if (tempTarget.numOfSets(Rune.NEMESIS) > 0 && !tempTarget.containsDebuff(Debuff.SEAL))
        {
            double dmgPercent = finalDmg / target.getMaxHp();
            double atkBarPercentIncrease = 0.04 * numOfSets(Rune.NEMESIS) * dmgPercent / 0.07;
            this.increaseAtkBarByPercent((int) Math.ceil(atkBarPercentIncrease));
        }
        
        //Despair Rune
        if (numOfSets(Rune.DESPAIR) > 0 && !containsDebuff(Debuff.SEAL))
        {
            double chance = 25.0;
            if (glancing)
            {
                chance /= 2;
            }
            target.addAppliedDebuff(Debuff.STUN, chance, 1, this);
        }
        
        //Attack again if the ability is a multi-hit
        if (count < ability.getNumOfActivations())
        {
            this.attack(target, ability, isCounter, count + 1);
            return;
        }
        
        //Activate after turn functions if the attack was a counter
        if (isCounter)
        {
            this.afterTurnProtocol(target, true, true);
        }
        
        //Counter if the target had the Defend buff
        if (defend && !isCounter)
        {
            tempTarget.counter(this);
        }
    }
    
    /**
     * The Monster's basic attack command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target    The target Monster to attack
     * @param ability   The ability to attack with
     * @param isCounter True if this attack is a counter, false otherwise
     */
    public void attack(Monster target, Ability ability, boolean isCounter)
    {
        //Make sure the Monster is not dead
        if (currentHp <= 0)
        {
            this.kill();
            return;
        }
        
        attack(target, ability, isCounter, 1);
        
        //Heal self if the ability heals based off damage done
        setCurrentHp(currentHp + (int) (dmgDealtThisTurn * ability.getHealingPercent()));
    }
    
    /**
     * The Monster's basic attack command. This method assumes the attack is not a counter and will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to attack
     * @param ability The ability to attack with
     */
    public void attack(Monster target, Ability ability)
    {
        attack(target, ability, false);
    }
    
    /**
     * Attacks the entire team
     *
     * @param target  The team to attack
     * @param ability The ability to attack with
     */
    public void attackTeam(Team target, Ability ability)
    {
        this.singleTargetAttack = false;
        //Attack each Monster
        for (Monster monster : target.getMonsters())
        {
            if (!monster.isDead())
            {
                this.attack(monster, ability, false);
            }
        }
    }
    
    /**
     * The Monster's basic heal command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to heal
     * @param ability The ability to heal with
     * @param count   The number of times this method has been called this turn
     */
    private void heal(Monster target, Ability ability, int count)
    {
        //Check if the target is dead
        if (target.isDead())
        {
            return;
        }
        
        for (Buff buff : ability.getBuffs())
        {
            if (buff.getBuffNum() == Buff.REMOVE_DEBUFF)
            {
                //Remove a random debuff
                target.removeRandomDebuff();
                target.removeBuff(Buff.REMOVE_DEBUFF);
            }
            //Cleanse the target
            if (buff.getBuffNum() == Buff.CLEANSE)
            {
                target.cleanse();
                target.removeBuff(Buff.CLEANSE);
            }
        }
        
        //Heal the target if they do not have unrecoverable
        double healAmount = 0;
        if (!target.containsDebuff(Debuff.UNRECOVERABLE))
        {
            healAmount = ability.getHealingPercent() * target.maxHp;
            target.currentHp += (int) Math.ceil(healAmount);
            if (target.currentHp > target.maxHp)
            {
                target.currentHp = target.maxHp;
            }
        }
        
        //Print heal message
        if (print && healAmount > 0)
        {
            System.out.printf("%sHealed %s%s for %d health.%s\n", ConsoleColors.GREEN, target.getName(true, true), ConsoleColors.GREEN, (int) healAmount, ConsoleColors.RESET);
        }
        
        
        //Apply buffs to the target if they do not have Beneficial Effect Blocker
        if (!target.containsDebuff(Debuff.BLOCK_BENEFICIAL_EFFECTS))
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                if (buffs.get(i).getBuffNum() != Buff.CLEANSE && buffs.get(i).getBuffNum() != Buff.REMOVE_DEBUFF)
                {
                    target.addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
                }
            }
        }
        
        //Extend Beneficial Effects
        if (target.containsBuff(Buff.EXTEND_BUFF))
        {
            ArrayList<Buff> buffs = target.getAppliedBuffs();
            for (int i = buffs.size() - 1; i >= 0; i--)
            {
                Buff buff = buffs.get(i);
                //Remove the buff it has no turns left
                if (buff.getNumTurns() <= 0)
                {
                    target.removeBuff(buff);
                    continue;
                }
                
                //Increase the number of turns left by 1
                buff.setNumTurns(buff.getNumTurns() + 1);
            }
            target.removeBuff(Buff.EXTEND_BUFF);
        }
        
        //Shorten Harmful effect
        if (target.containsBuff(Buff.SHORTEN_DEBUFF))
        {
            ArrayList<Debuff> debuffs = target.getAppliedDebuffs();
            for (int i = debuffs.size() - 1; i >= 0; i--)
            {
                Debuff debuff = debuffs.get(i);
                //Decrease the number of turns left by 1
                debuff.setNumTurns(debuff.getNumTurns() - 1);
                
                //Remove the debuff if it has no turns left
                if (debuff.getNumTurns() <= 0)
                {
                    target.removeDebuff(debuff);
                }
            }
            target.removeBuff(Buff.SHORTEN_DEBUFF);
        }
        
        //Heal again if the ability heals multiple times
        if (count < ability.getNumOfActivations())
        {
            heal(target, ability, count + 1);
        }
    }
    
    /**
     * The Monster's basic heal command. This method will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to heal
     * @param ability The ability to heal with
     */
    public void heal(Monster target, Ability ability)
    {
        heal(target, ability, 1);
    }
    
    /**
     * Heals the entire team
     *
     * @param target  The team to heal
     * @param ability The ability to use
     */
    public void healTeam(Team target, Ability ability)
    {
        //Heal each Monster
        for (Monster monster : target.getMonsters())
        {
            if (!monster.isDead())
            {
                heal(monster, ability);
            }
        }
    }
    
    /**
     * Applies a function to every alive Monster on a team
     *
     * @param team The team to apply to
     * @param func The function to apply
     */
    public void applyToTeam(Team team, Function func)
    {
        //Apply the function to the team
        for (Monster monster : team.getMonsters())
        {
            if (!monster.isDead())
            {
                func.apply(monster);
            }
        }
    }
    
    /**
     * This method will activate all after-attack commands, including counters and rune effects
     *
     * @param target The target that was attacked
     */
    private void afterAttackProtocol(Monster target)
    {
        //Target's attacked passive
        target.attacked(this);
        
        //Remove any buffs with no turns left
        for (int i = target.appliedBuffs.size() - 1; i >= 0; i--)
        {
            Buff buff = target.appliedBuffs.get(i);
            if (buff.getNumTurns() <= 0)
            {
                target.removeBuff(buff);
            }
        }
        
        //Remove any debuffs with no turns left
        for (int i = target.appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = target.appliedDebuffs.get(i);
            if (debuff.getNumTurns() <= 0)
            {
                target.removeDebuff(debuff);
            }
        }
        
        //Remove any other stats with no turns left
        for (int i = target.otherStats.size() - 1; i >= 0; i--)
        {
            Stat stat = target.otherStats.get(i);
            if (stat.getNumTurns() <= 0)
            {
                target.removeOtherStat(stat);
            }
        }
        
        //Check if the target or self are dead
        if (this.currentHp <= 0)
        {
            this.kill();
        }
        if (target.currentHp <= 0)
        {
            target.kill();
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param targetMons The Monster(s) that were targeted
     * @param attack     True if this turn was an attack turn, false otherwise
     */
    private void afterTurnProtocol(ArrayList<Monster> targetMons, boolean attack)
    {
        if (attack)
        {
            //Do after turn function for each Monster
            for (Monster target : targetMons)
            {
                afterAttackProtocol(target);
            }
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param target The Monster that was targeted
     * @param attack True if this turn was an attack turn, false otherwise
     */
    private void afterTurnProtocol(Monster target, boolean attack)
    {
        if (attack)
        {
            afterAttackProtocol(target);
        }
    }
    
    /**
     * This method will activate all after-turn commands, including counters and rune effects
     *
     * @param o         The Monster(s) that were targeted. This object should be an ArrayList<Monster>, a Team, or a Monster
     * @param isCounter True if this turn was a counter, false otherwise
     * @param attack    True if this turn was an attack turn, false otherwise
     * @throws ConflictingArguments If Object o is not an <code>ArrayList&lt;Monster&gt;</code>, a Team, or a Monster
     */
    public void afterTurnProtocol(Object o, boolean isCounter, boolean attack)
    {
        switch (o)
        {
            case Team t -> afterTurnProtocol(t.getMonsters(), attack);
            case ArrayList<?> a -> afterTurnProtocol((ArrayList<Monster>) a, attack);
            case Monster m -> afterTurnProtocol(m, attack);
            default -> throw new ConflictingArguments("Object must be an ArrayList, a Team, or a Monster");
        }
        
        //Decrease effect and ability cooldowns
        if (!isCounter)
        {
            decreaseStatCooldowns();
            if (!isStunned())
            {
                decreaseAbilityCooldowns();
            }
        }
        
        //Reset damage dealt this turn
        dmgDealtThisTurn = 0;
        
        //Violent Rune
        boolean vioProcced = false;
        if (numOfSets(Rune.VIOLENT) > 0 && Game.canCounter() && !containsDebuff(Debuff.SEAL))
        {
            //Prevent counters
            Game.setCanCounter(false);
            int random = new Random().nextInt(101);
            //Base
            double extraTurnChance = 22;
            //Decrease proc chance for each time the rune was activated
            for (int i = 0; i < numOfViolentRuneProcs; i++)
            {
                extraTurnChance *= 0.55;
            }
            if (random <= Math.ceil(extraTurnChance))
            {
                //Give the Monster an extra turn
                this.atkBar = 99_000;
                vioProcced = true;
                numOfViolentRuneProcs++;
                //Print a message for the extra turn
                if (print)
                {
                    System.out.printf("%sExtra Turn!%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
                }
            }
            else //Reset vio procs
            {
                numOfViolentRuneProcs = 0;
            }
        }
        Game.setCanCounter(true);
        //Increase every Monster's attack bar if Violent rune was not procced and the turn was not a counter
        if (!isCounter && !vioProcced)
        {
            game.increaseAtkBar();
        }
    }
    
    /**
     * This method assumes this turn was not a counter and will activate all after-turn commands, including counters and rune effects
     *
     * @param o      The Monster(s) that were targeted. This object should be an ArrayList<Monster>, a Team, or a Monster
     * @param attack True if this turn was an attack turn, false otherwise
     * @throws ConflictingArguments If Object o is not an ArrayList, a Team, or a Monster
     */
    public void afterTurnProtocol(Object o, boolean attack)
    {
        afterTurnProtocol(o, false, attack);
    }
    
    /**
     * Attempts to steal a random buff from the target Monster and applies it to this
     *
     * @param target The target Monster
     */
    protected void stealBuff(Monster target)
    {
        //Steal a random buff
        Buff stolen = target.removeRandomBuff();
        
        //Make sure the stolen buff isn't Defend or Null
        //Defend buffs can't be stolen
        if (stolen.getBuffNum() != Buff.DEFEND && stolen.getBuffNum() != Buff.NULL)
        {
            addAppliedBuff(stolen.getBuffNum(), stolen.getNumTurns(), this);
        }
    }
    
    /**
     * Attempts to steal all buffs from the target and apply them to this
     *
     * @param target The target to try and steal from
     */
    protected void stealAllBuffs(Monster target)
    {
        //Steal all buffs on the target Monster
        for (int j = target.getAppliedBuffs().size() - 1; j >= 0; j--)
        {
            stealBuff(target);
        }
    }
    
    /**
     * Applies all buffs on the Monster that are relevant to the turn
     */
    private void applyBeginningOfTurnBuffs()
    {
        //Look for attack, defense, crit rate, or speed up buffs
        for (Buff buff : appliedBuffs)
        {
            switch (buff.getBuffNum())
            {
                case Buff.ATK_UP -> extraAtk = (int) (atk * 0.5);
                case Buff.DEF_UP -> extraDef = (int) (def * 0.7);
                case Buff.CRIT_RATE_UP -> extraCritRate = (int) (critRate * 0.3);
                case Buff.ATK_SPD_UP -> extraSpd = (int) (spd * 0.3);
            }
        }
    }
    
    /**
     * Applies all debuffs on the Monster that are relevant to the turn
     */
    private void applyBeginningOfTurnDebuffs()
    {
        //Look for attack, defense, or attack speed down, and for glancing rate up
        for (Debuff debuff : appliedDebuffs)
        {
            switch (debuff.getDebuffNum())
            {
                case Debuff.GLANCING_HIT_UP -> extraGlancingRate = 50;
                case Debuff.DEC_ATK -> lessAtk = (int) (atk * 0.5);
                case Debuff.DEC_DEF -> lessDef = (int) (def * 0.7);
                case Debuff.DEC_ATK_SPD -> lessAtkSpd = (int) (spd * 0.3);
            }
        }
    }
    
    /**
     * Decreases the time remaining on all buffs and debuffs by one turn each (Except Threat). Removes any buffs and debuffs with zero turns remaining
     */
    public void decreaseStatCooldowns()
    {
        for (int i = appliedBuffs.size() - 1; i >= 0; i--)
        {
            Buff buff = appliedBuffs.get(i);
            //Decrease the turn if it's not a Threat
            buff.decreaseTurn();
            
            //Reset extra stat values if the buff has no turns left
            if (buff.getNumTurns() <= 0)
            {
                switch (buff.getBuffNum())
                {
                    case Buff.ATK_UP -> extraAtk = 0;
                    case Buff.DEF_UP -> extraDef = 0;
                    case Buff.CRIT_RATE_UP -> extraCritRate = 0;
                    case Buff.ATK_SPD_UP -> extraSpd = 0;
                    case Buff.SHIELD -> shield = 0;
                }
                appliedBuffs.remove(i);
            }
        }
        
        for (int i = appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = appliedDebuffs.get(i);
            //Decrease the debuffs turns left
            debuff.decreaseTurn();
            //Remove extra stat values if the debuff has no turns left
            if (debuff.getNumTurns() <= 0)
            {
                switch (debuff.getDebuffNum())
                {
                    case Debuff.GLANCING_HIT_UP -> extraGlancingRate = 0;
                    case Debuff.DEC_ATK -> lessAtk = 0;
                    case Debuff.DEC_DEF -> lessDef = 0;
                    case Debuff.DEC_ATK_SPD -> lessAtkSpd = 0;
                }
                appliedDebuffs.remove(i);
            }
        }
    }
    
    /**
     * Decreases the cooldown for all abilities by one turn each if applicable
     */
    public void decreaseAbilityCooldowns()
    {
        for (Ability ability : abilities)
        {
            ability.decCooldown();
        }
    }
    
    /**
     * Checks if the Monster has the given Debuff
     *
     * @param debuff The debuff to look for
     * @return True if the Monster has the given Debuff, false otherwise
     */
    public boolean containsDebuff(Debuff debuff)
    {
        for (Debuff d : appliedDebuffs)
        {
            if (d.equals(debuff))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Debuff associated with the provided number
     *
     * @param num The Debuff number to look for
     * @return True if the Monster has the Debuff associated with the provided number, false otherwise
     */
    public boolean containsDebuff(int num)
    {
        return containsDebuff(new Debuff(num));
    }
    
    /**
     * Checks if the Monster has the given Buff
     *
     * @param buff The buff to look for
     * @return True if the Monster has the given Buff, false otherwise
     */
    public boolean containsBuff(Buff buff)
    {
        for (Buff b : appliedBuffs)
        {
            if (b.equals(buff))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Debuff associated with the provided number
     *
     * @param num The Debuff number to look for
     * @return True if the Monster has the Debuff associated with the provided number, false otherwise
     */
    public boolean containsBuff(int num)
    {
        return containsBuff(new Buff(num));
    }
    
    /**
     * Checks if the Monster has the given Stat (Not buff or debuff)
     *
     * @param stat The Stat to look for
     * @return True if the Monster has the provided Stat
     */
    public boolean containsOtherStat(Stat stat)
    {
        for (Stat s : otherStats)
        {
            if (s.equals(stat))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Stat (Not buff or debuff) associated with the given number
     *
     * @param stat The Stat number to look for
     * @return True if the Monster has the Stat associated with the given number
     */
    public boolean containsOtherStat(int stat)
    {
        Stat s = new Stat(1);
        s.setStatNum(stat);
        return containsOtherStat(s);
    }
    
    /**
     * Removes a random Buff if there is at least one Buff on the Monster, returns a NULL buff otherwise
     *
     * @return The buff that was removed
     */
    public Buff removeRandomBuff()
    {
        //Return a null buff if there are no buffs to remove
        if (this.appliedBuffs.isEmpty())
        {
            return new Buff(Buff.NULL, 0);
        }
        //Get a random buff
        int rand = new Random().nextInt(appliedBuffs.size());
        Buff buff = this.appliedBuffs.remove(rand);
        //Reset extra stat values if needed
        switch (buff.getBuffNum())
        {
            case Buff.ATK_UP -> extraAtk = 0;
            case Buff.DEF_UP -> extraDef = 0;
            case Buff.CRIT_RATE_UP -> extraCritRate = 0;
            case Buff.ATK_SPD_UP -> extraSpd = 0;
            case Buff.SHIELD -> shield = 0;
        }
        return buff;
    }
    
    /**
     * Removes a random Debuff if there is at least one Debuff on the Monster, does nothing otherwise
     *
     * @return The debuff that was removed
     */
    public Debuff removeRandomDebuff()
    {
        //Return a null buff if there are no debuffs to remove
        if (appliedDebuffs.isEmpty())
        {
            return new Debuff(Debuff.NULL, 0, 0);
        }
        //Get a random debuff
        int rand = new Random().nextInt(appliedDebuffs.size());
        Debuff debuff = appliedDebuffs.remove(rand);
        //Reset extra stat values if needed
        switch (debuff.getDebuffNum())
        {
            case Debuff.GLANCING_HIT_UP -> extraGlancingRate = 0;
            case Debuff.DEC_ATK -> lessAtk = 0;
            case Debuff.DEC_DEF -> lessDef = 0;
            case Debuff.DEC_ATK_SPD -> lessAtkSpd = 0;
        }
        return debuff;
    }
    
    /**
     * Adds a Stat (not buff or debuff) to the Monster
     *
     * @param stat The Stat to add
     */
    public void addOtherStat(Stat stat)
    {
        otherStats.add(stat);
    }
    
    /**
     * Removes all instances of the provided Stat
     *
     * @param stat The Stat to look for
     */
    public void removeOtherStat(Stat stat)
    {
        for (int i = otherStats.size() - 1; i >= 0; i--)
        {
            if (otherStats.get(i).equals(stat))
            {
                otherStats.remove(i);
            }
        }
    }
    
    /**
     * Removes the stat from the Monster
     *
     * @param num The stat number to remove
     */
    public void removeOtherStat(int num)
    {
        Stat s = new Stat(1);
        s.setStatNum(num);
        removeOtherStat(s);
    }
    
    /**
     * Checks if the Monster is stunned on this turn. (If the Monster is slept, frozen, or stunned)
     *
     * @return True if the Monster is stunned, false otherwise
     */
    public boolean isStunned()
    {
        return containsDebuff(Debuff.SLEEP) || containsDebuff(Debuff.FREEZE) || containsDebuff(Debuff.STUN);
    }
    
    /**
     * Removes all debuffs on the Monster
     *
     * @return The number of debuffs removed
     */
    public int cleanse()
    {
        int size = appliedDebuffs.size();
        appliedDebuffs.clear();
        return size;
    }
    
    /**
     * Compares two Monsters
     *
     * @param mon The Monster to compare
     * @return True if the two Monster's names are equal
     */
    public boolean equals(Monster mon)
    {
        return name.equals(mon.name);
    }
    
    /**
     * Checks if the Monster has no health left and does not have any Buffs that prevent death. If so, kills the Monster.
     */
    public void kill()
    {
        //Do nothing if the Monster still has health left
        if (currentHp > 0)
        {
            return;
        }
        
        //Endure
        if (containsBuff(Buff.ENDURE))
        {
            currentHp = 1;
        }
        //Soul Protection
        else if (containsBuff(Buff.SOUL_PROTECTION))
        {
            currentHp = (int) (maxHp * 0.3);
            removeBuff(new Buff(13, 1));
        }
        else //Kill the Monster
        {
            currentHp = 0;
            //Remove all buffs and debuffs
            while (!appliedBuffs.isEmpty() || !appliedDebuffs.isEmpty())
            {
                decreaseStatCooldowns();
            }
            //Reset attack bar
            atkBar = 0;
            dead = true;
            glancing = false;
            crit = false;
            //Print a kill message
            if (print)
            {
                System.out.printf("%s%s died!\n%s%n", ConsoleColors.RED_BOLD_BRIGHT, name, ConsoleColors.RESET);
            }
            
            //Remove any Provokes on the other team if the caster was self
            Team other = game.getOtherTeam();
            for (int i = 0; i < other.size(); i++)
            {
                Monster m = other.get(i);
                if (m.getProvoke() != null)
                {
                    Provoke p = m.getProvoke();
                    if (p.getCaster().equals(this))
                    {
                        m.removeDebuff(p);
                    }
                }
            }
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
            throw new BadArgumentLength("Bad argument length: %d".formatted(args.length));
        }
        
        //Format the args into buffs
        ArrayList<Buff> buffs = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2)
        {
            buffs.add(new Buff(args[i], args[i + 1]));
        }
        return buffs;
    }
    
    /**
     * Formats a set of numbers into an ArrayList of Debuffs
     *
     * @param args The numbers to format. Format: Debuff number, number of turns, whether it goes through immunity (0 for false, 1 for true), repeat as needed
     * @return The ArrayList of Debuffs specified by the varargs
     */
    public ArrayList<Debuff> abilityDebuffs(int... args)
    {
        //Make sure the argument length is valid
        if (args.length % 3 != 0)
        {
            throw new BadArgumentLength("Bad argument length: %d".formatted(args.length));
        }
        
        //Format the args into debuffs
        ArrayList<Debuff> debuffs = new ArrayList<>();
        for (int i = 0; i < args.length; i += 3)
        {
            Debuff d = new Debuff(args[i], args[i + 1], args[i + 2]);
            d.setCaster(this);
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
     * Checks if the Ability associated with the provided number is passive
     *
     * @param abilityNum The Ability number
     * @return True if the Ability associated with the provided number is passive, false otherwise
     */
    public boolean abilityIsPassive(int abilityNum)
    {
        return abilities.get(abilityNum - 1).isPassive();
    }
    
    /**
     * Activates passive abilities that are triggered when attacked (ex. Miho)
     *
     * @param attacker The attacking Monster
     */
    public void attacked(Monster attacker)
    {
        //Decrease the turns remaining on Threat if the attack was a single target attack
        if (this.hasThreat() && attacker.singleTargetAttack)
        {
            for (Buff b : appliedBuffs)
            {
                if (b instanceof Threat t)
                {
                    t.decreaseTurn(1);
                }
            }
        }
        
        //Counter
        if (containsBuff(Buff.COUNTER) && !isDead() && Game.canCounter() && !isStunned())
        {
            Game.setCanCounter(false);
            if (print)
            {
                System.out.println("Counter!");
            }
            this.counter(attacker);
        }
        
        //Revenge Rune
        if (numOfSets(Rune.REVENGE) > 0 && Game.canCounter() && !isStunned() && !containsDebuff(Debuff.SEAL))
        {
            Game.setCanCounter(false);
            int random = new Random().nextInt(101);
            if (random <= 15 * numOfSets(Rune.REVENGE))
            {
                if (print)
                {
                    System.out.println("Counter! (Revenge Rune)");
                }
                this.counter(attacker);
            }
        }
    }
    
    /**
     * Counters the attacking Monster
     *
     * @param attacker The Monster who attacked
     */
    private void counter(Monster attacker)
    {
        //Do nothing if the Monster can't counter
        if (!Game.canCounter())
        {
            return;
        }
        Game.setCanCounter(false);
        //Save the Monster's current stat and ability numbers
        ArrayList<Buff> targetBuffs = new ArrayList<>(appliedBuffs);
        ArrayList<Debuff> targetDebuffs = new ArrayList<>(appliedDebuffs);
        ArrayList<Stat> targetOtherEffects = new ArrayList<>(otherStats);
        ArrayList<Integer> targetAbilityCooldowns = new ArrayList<>();
        double currentAtkBar = atkBar;
        
        for (Ability ability : abilities)
        {
            targetAbilityCooldowns.add(ability.getTurnsRemaining());
        }
        
        //Counter the attacker
        this.nextTurn(attacker, 1);
        
        //Reset all stat and ability numbers
        appliedBuffs = targetBuffs;
        appliedDebuffs = targetDebuffs;
        otherStats = targetOtherEffects;
        ArrayList<Ability> abilityArrayList = abilities;
        for (int i = 0; i < abilityArrayList.size(); i++)
        {
            abilityArrayList.get(i).setToNumTurns(targetAbilityCooldowns.get(i));
        }
        
        for (Buff b : targetBuffs)
        {
            b.decreaseTurn(-1);
        }
        
        for (Debuff d : targetDebuffs)
        {
            d.decreaseTurn(-1);
        }
        
        for (Stat s : targetOtherEffects)
        {
            s.decreaseTurn(-1);
        }
        
        atkBar = currentAtkBar;
    }
    
    /**
     * Checks if the Monster has a Threat buff
     *
     * @return True if the Monster has a Threat buff, false otherwise
     */
    public boolean hasThreat()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Threat)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has a Defend buff
     *
     * @return True if the Monster has a Defend buff, false otherwise
     */
    private boolean hasDefend()
    {
        for (Buff b : appliedBuffs)
        {
            if (b instanceof Defend)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has a shield buff
     *
     * @return True if the Monster has a shield, false otherwise
     */
    private boolean hasShield()
    {
        for (Buff buff : appliedBuffs)
        {
            if (buff instanceof Shield)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has a DecAtkBar debuff
     *
     * @return True if the Monster has a DecAtkBar debuff, false otherwise
     */
    private boolean hasDecAtkBar()
    {
        for (Debuff d : appliedDebuffs)
        {
            if (d instanceof DecAtkBar)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the monster was hit by a crit this turn
     *
     * @return True if the Monster was hit with a crit this turn, false otherwise
     */
    public boolean wasCrit()
    {
        return wasCrit;
    }
    
    /**
     * Prints the Monsters stats followed by its abilities and runes
     */
    public void printWithDetails()
    {
        //Print stats and effects
        if (print)
        {
            System.out.printf("%s: %sHp: %,d %s+%,d; %sAttack: %,d %s+%,d; %sDefense: %,d %s+%,d; %sSpeed: %d %s+%d; %sCrit rate: %d%%; %sCrit damage: %d%%; %sResistance: %d%%; Accuracy: %s%d%%%s\n\n", getName(true, false), ConsoleColors.GREEN,
                    baseMaxHp, ConsoleColors.GREEN_BOLD_BRIGHT, maxHp - baseMaxHp, ConsoleColors.RED, baseAtk,
                    ConsoleColors.RED_BOLD_BRIGHT, atk - baseAtk, ConsoleColors.YELLOW, baseDef,
                    ConsoleColors.YELLOW_BOLD_BRIGHT, def - baseDef, ConsoleColors.CYAN, baseSpd, ConsoleColors.CYAN_BOLD_BRIGHT,
                    spd - baseSpd, ConsoleColors.BLUE, critRate, ConsoleColors.PURPLE, critDmg, ConsoleColors.RESET, Math.min(resistance, 100),
                    (accuracy >= 100) ? ConsoleColors.RED : "", Math.min(accuracy, 100), ConsoleColors.RESET);
        }
        //Print each ability
        for (Ability ability : abilities)
        {
            if (print)
            {
                System.out.printf("\t %s\n\n%n", ability.toString(containsDebuff(Debuff.SILENCE), containsDebuff(Debuff.OBLIVION)));
            }
        }
        
        //Print each rune type
        if (runes != null)
        {
            if (print)
            {
                System.out.printf("%sRune sets:%n", ConsoleColors.PURPLE_BOLD_BRIGHT);
            }
            ArrayList<Integer> runeTypes = new ArrayList<>();
            //Get the rune types
            for (Rune rune : runes)
            {
                runeTypes.add(rune.getType());
            }
            ArrayList<String> types = new ArrayList<>();
            while (!runeTypes.isEmpty())
            {
                //Count the number of each set
                int integer = runeTypes.getLast();
                for (int i = runeTypes.size() - 1; i >= 0; i--)
                {
                    if (runeTypes.get(i) == integer)
                    {
                        runeTypes.remove(runeTypes.get(i));
                    }
                }
                if (numOfSets(integer) == 0)
                {
                    continue;
                }
                //Format the number of sets
                types.add("%s x%d".formatted(Rune.numToType(integer), numOfSets(integer)));
            }
            Collections.reverse(types);
            
            //Print the rune sets
            for (String string : types)
            {
                if (print)
                {
                    System.out.printf("\t%s\t", string);
                }
            }
        }
        
        if (print)
        {
            System.out.printf("%s\n\n", ConsoleColors.RESET);
        }
    }
    
    /**
     * Applies all stat rune effects that are not team-based
     */
    private void applyRuneSetEffectsForBeginningOfGame()
    {
        //Apply set effects
        tempMaxHp = applyEffect(Rune.ENERGY, tempMaxHp, baseMaxHp, 0.15);
        tempAtk = applyEffect(Rune.FATAL, tempAtk, baseAtk, 0.35);
        spd = (int) Math.ceil(applyEffect(Rune.SWIFT, spd, baseSpd, 0.25));
        tempDef = applyEffect(Rune.GUARD, tempDef, baseDef, 0.15);
        critRate += (12 * numOfSets(Rune.BLADE));
        accuracy += (20 * numOfSets(Rune.FOCUS));
        resistance += (20 * numOfSets(Rune.ENDURE));
        critDmg += (40 * numOfSets(Rune.RAGE));
        
        //Add immunity for each set of Will
        if (numOfSets(Rune.WILL) > 0)
        {
            this.addAppliedBuff(Buff.IMMUNITY, numOfSets(Rune.WILL), this);
        }
    }
    
    /**
     * This method should only be used for effects that increase a stat multiplicatively (ex. Energy). Applies a rune's main effect.
     *
     * @param setNum   The Rune set number to count
     * @param tempStat The current temporary value of the affected stat
     * @param baseStat The base value of the affected stat
     * @param amt      The decimal to multiply the base stat by
     * @return The new value of the temporary stat variable
     */
    private double applyEffect(int setNum, double tempStat, double baseStat, double amt)
    {
        //Add the base stat multiplied by amt for each set of the rune
        //Math.ceil is calculated once for each set
        for (int i = 0; i < numOfSets(setNum); i++)
        {
            tempStat += Math.ceil(baseStat * amt);
        }
        return tempStat;
    }
    
    /**
     * Counts the number of runes for the given type number
     *
     * @param type The rune number to check
     * @return The number of runes for the number
     */
    private int countNumOfEffectRunes(int type)
    {
        //Do nothing if the runes haven't been set yet
        if (runes == null)
        {
            return 0;
        }
        int count = 0;
        
        //Count the number of the rune type
        for (Rune rune : runes)
        {
            count += (type == rune.getType()) ? 1 : 0;
        }
        return count;
    }
    
    /**
     * Counts the number of set effects for the type number
     *
     * @param type The rune number to check
     * @return The number of set effects for the number
     */
    public int numOfSets(int type)
    {
        //Fatal, Swift, Vampire, Despair, Violent, and Rage runes need 4 to count as a set
        if (countNumOfEffectRunes(type) >= 4 && (type == Rune.FATAL || type == Rune.SWIFT || type == Rune.VAMPIRE || type == Rune.DESPAIR || type == Rune.VIOLENT || type == Rune.RAGE))
        {
            return 1;
        }
        //Artifacts only need 1 to count as a set
        if (type == Rune.ELEMENTARTIFACT || type == Rune.TYPEARTIFACT)
        {
            return countNumOfEffectRunes(type);
        }
        
        //Every other rune needs 2 to count as a set
        return countNumOfEffectRunes(type) / 2;
    }
    
    /**
     * Activates all passives triggered before a Monster's turn
     *
     * @param nextMon     The Monster whose turn it is
     * @param self        True if this.equals(nextMon)
     * @param enemyTurn   True if nextMon is on the opposing Team
     * @param hasOblivion True if the Monster has the Oblivion debuff
     */
    public void beforeTurnProtocol(Monster nextMon, boolean self, boolean enemyTurn, boolean hasOblivion)
    {
        //Make sure the booleans are not colliding
        if (self && enemyTurn)
        {
            throw new ConflictingArguments("self and enemyTurn cannot both be true: " + getName(true, true));
        }
    }
    
    /**
     * Checks for passives that reduce the damage taken by the Monster and calculates the new damage if needed
     *
     * @param num  The current damage to take
     * @param self True if this is the current Monster, false otherwise
     * @return The updated damage to take
     */
    public double dmgReductionProtocol(double num, boolean self)
    {
        return num;
    }
    
    /**
     * Checks for passives the increase the damage given by the Monster and calculates the new damage if needed
     *
     * @param num The current damage to deal
     * @return The updated damage to deal
     */
    public double dmgIncProtocol(double num)
    {
        return num;
    }
    
    /**
     * Activates passive abilities that are triggered after a Monster is hit
     *
     * @param attacker The attacking Monster
     */
    public void targetAfterHitProtocol(Monster attacker)
    {
        //Remove any buffs with no turns remaining
        for (int i = this.appliedBuffs.size() - 1; i >= 0; i--)
        {
            Buff buff = this.appliedBuffs.get(i);
            if (buff.getNumTurns() <= 0)
            {
                this.removeBuff(buff);
            }
        }
        
        //Remove any debuffs with no turns remaining
        for (int i = this.appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = this.appliedDebuffs.get(i);
            if (debuff.getNumTurns() <= 0)
            {
                this.removeDebuff(debuff);
            }
        }
        
        //Remove any other effects with no turns remaining
        for (int i = this.otherStats.size() - 1; i >= 0; i--)
        {
            Stat stat = this.otherStats.get(i);
            if (stat.getNumTurns() <= 0)
            {
                this.removeOtherStat(stat);
            }
        }
    }
    
    /**
     * Activates passive abilities that are triggered after a Monster attacks a target
     *
     * @param target     The target Monster of the attack
     * @param abilityNum The chosen ability number
     */
    public void selfAfterHitProtocol(Monster target, int abilityNum)
    {
    }
    
    /**
     * Checks if the Monster has a support ability that targets multiple Monsters
     *
     * @return True if the Monster has a support ability that targets multiple Monsters, false otherwise
     */
    public boolean hasTeamSupportAbility()
    {
        //Check if there is an ability that targets allies and targets the entire team
        for (Ability ability : abilities)
        {
            if (!ability.targetsEnemy() && !ability.targetsSelf() && ability.targetsAllTeam())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has an Ability that targets itself
     *
     * @return True if the Monster has an Ability that targets itself, false otherwise
     */
    public boolean hasSelfSupportAbility()
    {
        //Check for an ability that targets self
        for (Ability ability : abilities)
        {
            if (ability.targetsSelf())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has a support ability that applies multiple buffs
     *
     * @return True if the Monster has a support ability that applies multiple buffs, false otherwise
     */
    public boolean hasSupportAbilityWithMultipleBuffs()
    {
        //Check for an ability that applies at least 2 buffs
        for (Ability ability : abilities)
        {
            if (ability.getBuffs().size() >= 2)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Resets the Monster to its state after applying runes
     */
    public void reset()
    {
        //Remove buffs and debuffs (other effects are removed in child classes
        removeBuff(Buff.THREAT);
        while (!appliedBuffs.isEmpty() || !appliedDebuffs.isEmpty())
        {
            decreaseStatCooldowns();
        }
        
        //Reset ability cooldowns
        for (Ability ability : abilities)
        {
            ability.setToNumTurns(0);
        }
        
        //Reset stats
        destroyedHp = 0;
        currentHp = maxHp;
        atkBar = 0;
        dead = false;
        abilityGlancingRateChange = 0;
        dmgDealtThisTurn = 0;
        dmgTakenThisTurn = 0;
        crit = false;
        wasCrit = false;
        glancing = false;
        
        //Apply immunity for each Will set
        if (this.numOfSets(Rune.WILL) > 0)
        {
            this.addAppliedBuff(Buff.IMMUNITY, numOfSets(Rune.WILL), new Monster());
        }
    }
    
    /**
     * Checks if the Monster has a leader skill
     *
     * @return True if the Monster has a leader skill, false otherwise
     */
    public boolean hasLeaderSkill()
    {
        //Check for a leader skill
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Applies the Monster's leader skill if it has one, does nothing otherwise
     *
     * @param applyToTeam The Team to apply the skill to
     */
    public void applyLeaderSkill(Team applyToTeam)
    {
        //Do nothing if the Monster has no leader skill to apply
        if (!this.hasLeaderSkill())
        {
            return;
        }
        
        //Apply the leader skill to the team
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill skill)
            {
                skill.apply(applyToTeam);
            }
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
        while (!stringIsMonsterName(inputInspect));
        
        //Get the rune set number
        int runeSetNum = Main.getRuneSetNum();
        
        //Try to create the Monster
        Monster m = createNewMonFromName(inputInspect, Math.abs(runeSetNum));
        
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
     * Searches the Monster database for a name that matches the provided String. This is the same as calling
     * {@link Monster#stringIsMonsterName(String, ArrayList)} with {@link Monster#monsterNamesDatabase}
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
        
        //Fill the database if it is empty
        if (monsterNamesDatabase.isEmpty())
        {
            setDatabase();
        }
        
        //Search the database for the given name
        for (String string : monsterNamesDatabase.keySet())
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
        String returnName = "";
        //Replace all spaces with underscores
        name = name.replaceAll(" ", "_");
        //Format the name similar to titlecase
        for (int i = 0; i < name.length(); i++)
        {
            String character = String.valueOf(name.charAt(i));
            //Capitalize the first letter
            if (i == 0)
            {
                returnName += character.toUpperCase();
            }
            //Capitalize letters after an underscore
            else if (name.charAt(i - 1) == '_')
            {
                returnName += character.toUpperCase();
            }
            else //Add the character to the formatted string
            {
                returnName += character;
            }
        }
        return returnName;
    }
    
    /**
     * A recursive algorithm to choose the best ability for the Monster to use.
     * Chooses a support Ability if there is a Monster on the same Team with low
     * health or the Ability applies multiple buffs.
     *
     * @param next       The acting Monster
     * @param actingTeam The Team with the acting Monster
     * @param abilities  The set of abilities that can be chosen
     * @param firstCall  True if this call is the first time it is called (from outside the method)
     * @return The ability's number on the monster
     */
    public int chooseAbilityNum(Monster next, Team actingTeam, ArrayList<Ability> abilities, boolean firstCall)
    {
        //Remove abilities that can't be called on this turn
        if (firstCall)
        {
            ArrayList<Integer> viableNums = next.getViableAbilityNumbers();
            ArrayList<Ability> modifiedAbilities = new ArrayList<>();
            for (int i = 0; i < abilities.size(); i++)
            {
                if (viableNums.contains(i))
                {
                    modifiedAbilities.add(abilities.get(i));
                }
            }
            return chooseAbilityNum(next, actingTeam, modifiedAbilities, false);
        }
        
        int numOfLowHealthTeammates = 0;
        boolean teammateWithLowHealth = false;
        boolean multipleTeammatesWithLowHealth;
        //Check for low health teammates
        for (Monster mon : actingTeam.getMonsters())
        {
            if (mon.getHpRatio() <= 50.0)
            {
                numOfLowHealthTeammates++;
                teammateWithLowHealth = true;
            }
        }
        multipleTeammatesWithLowHealth = (numOfLowHealthTeammates >= 2);
        
        //Choose a team support ability if an ally needs healing
        if (teammateWithLowHealth && next.hasTeamSupportAbility())
        {
            ArrayList<Heal_Ability> supportAbilities = new ArrayList<>();
            //Get the Monster's support abilities
            for (Ability ability : abilities)
            {
                if (ability instanceof Heal_Ability h && next.getTeamSupportAbilities().contains(h))
                {
                    supportAbilities.add(h);
                }
            }
            Collections.reverse(supportAbilities);
            for (int i = supportAbilities.size() - 1; i >= 0; i--)
            {
                Heal_Ability ability = supportAbilities.get(i);
                //Remove abilities on cooldown
                if (ability.getTurnsRemaining() > 0)
                {
                    supportAbilities.remove(ability);
                    continue;
                }
                //Use a single target support if possible and if only one ally needs healing
                if (ability.targetsAllTeam() && !multipleTeammatesWithLowHealth && supportAbilities.size() > 1)
                {
                    supportAbilities.remove(ability);
                }
            }
            Collections.reverse(supportAbilities);
            if (!supportAbilities.isEmpty())
            {
                //Attempt to use the last support ability in the list
                Ability a = supportAbilities.getLast();
                //Retry choosing if the ability is not usable
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : supportAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, actingTeam, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose a self-support ability
        if (next.hasSelfSupportAbility())
        {
            ArrayList<Ability> selfAbilities = new ArrayList<>();
            //Get abilities that target self
            for (Ability a : abilities)
            {
                if (next.getSelfSupportAbilities().contains(a))
                {
                    selfAbilities.add(a);
                }
            }
            
            //Remove unusable abilities
            for (int i = selfAbilities.size() - 1; i >= 0; i--)
            {
                Ability ability = selfAbilities.get(i);
                if (ability.getTurnsRemaining() > 0 || ability instanceof Passive)
                {
                    selfAbilities.remove(ability);
                }
            }
            
            if (!selfAbilities.isEmpty())
            {
                //Attempt to use the last ability in the list
                Ability a = selfAbilities.getLast();
                //Retry choosing if the ability can't be used
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : selfAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, actingTeam, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose a Heal ability with multiple buffs
        if (next.hasSupportAbilityWithMultipleBuffs())
        {
            ArrayList<Ability> supportAbilities = new ArrayList<>();
            //Get all Heal abilities with multiple buffs
            for (Ability a : abilities)
            {
                if (next.getSupportAbilitiesWithMultipleBuffs().contains(a))
                {
                    supportAbilities.add(a);
                }
            }
            
            //Remove unusable abilities
            for (int i = supportAbilities.size() - 1; i >= 0; i--)
            {
                Ability ability = supportAbilities.get(i);
                if (ability.getTurnsRemaining() > 0 || ability instanceof Passive)
                {
                    supportAbilities.remove(ability);
                }
            }
            
            if (!supportAbilities.isEmpty())
            {
                //Attempt to use the last ability in the list
                Ability a = supportAbilities.getLast();
                //Retry choosing if the ability can't be used
                if (!next.abilityIsValid(a))
                {
                    ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                    for (Ability ab : supportAbilities)
                    {
                        if (!ab.equals(a))
                        {
                            modifiedAbilities.add(ab);
                        }
                    }
                    return chooseAbilityNum(next, actingTeam, modifiedAbilities, false);
                }
                return next.getAbilities().indexOf(a) + 1;
            }
        }
        
        //Choose attack ability
        ArrayList<Ability> otherAbilities = new ArrayList<>();
        //Get all attack abilities
        for (Ability a : abilities)
        {
            if (next.getAttackAbilities().contains(a))
            {
                otherAbilities.add(a);
            }
        }
        
        //Remove unusable abilities
        for (int i = otherAbilities.size() - 1; i >= 0; i--)
        {
            Ability ability = otherAbilities.get(i);
            if (ability.getTurnsRemaining() > 0 || ability instanceof Heal_Ability || ability instanceof Passive)
            {
                otherAbilities.remove(ability);
            }
        }
        
        if (!otherAbilities.isEmpty())
        {
            //Attempt to use the last ability in the list
            Ability a = otherAbilities.getLast();
            //Retry choosing if the ability can't be used
            if (!next.abilityIsValid(a))
            {
                ArrayList<Ability> modifiedAbilities = new ArrayList<>();
                for (Ability ab : otherAbilities)
                {
                    if (!ab.equals(a))
                    {
                        modifiedAbilities.add(ab);
                    }
                }
                return chooseAbilityNum(next, actingTeam, modifiedAbilities, false);
            }
            return next.getAbilities().indexOf(a) + 1;
        }
        
        //Base case, returns the Monsters basic ability
        return 1;
    }
    
    /**
     * Determines if a monster can be healed
     *
     * @return True if the monster can be healed, false otherwise
     */
    public boolean canHeal()
    {
        return !this.containsDebuff(Debuff.UNRECOVERABLE) && !this.isDead();
    }
    
    /**
     * Creates a new Monster given another of the same class
     *
     * @param mon The Monster to create a new instance of
     * @return A new Monster of the same class
     */
    public static Monster createNewMonFromMon(Monster mon)
    {
        return createNewMonFromName(mon.getName(false, false));
    }
    
    /**
     * Creates a new Monster given its name. Uses the default rune set
     *
     * @param name The name of the Monster
     * @return A new Monster with the given name. Ex: inputting "Loren" will return a new Loren instance with rune set 1
     */
    public static Monster createNewMonFromName(String name)
    {
        return createNewMonFromName(name, 1);
    }
    
    /**
     * Creates a new Monster given its name.
     *
     * @param name       The name of the Monster
     * @param runeSetNum The rune set number to use
     * @return A new Monster with the given name. Ex: inputting ("Loren", 2) will return a new Loren instance with rune set 2
     */
    public static Monster createNewMonFromName(String name, int runeSetNum)
    {
        //Fill the database if it's empty
        if (monsterNamesDatabase.isEmpty())
        {
            setDatabase();
        }
        //Replace spaces with underscores
        name = name.replaceAll(" ", "_");
        //Convert the name into a readable format
        name = toProperName(name);
        
        //Create a temp name to get from the database
        String temp = name.replaceAll("_", " ");
        String element = monsterNamesDatabase.get(temp);
        
        //Get the Monster's element
        String className = "Monsters.%s.%s".formatted(element, name);
        try
        {
            //Get the Monster's class and try to create a new instance
            Class<?> c = Class.forName(className);
            Monster m = (Monster) c.getConstructor(String.class).newInstance("%s%d.csv".formatted(name, runeSetNum));
            //Return null if the Monster could not be created properly, otherwise return the Monster
            return m.getRunes() == null ? null : m;
        }
        catch (ClassNotFoundException e) //The Monster could not be created properly
        {
            System.err.println("Can not create Monster");
            scan.nextLine();
            System.out.println();
            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * Calculates how much damage each continuous damage Debuff does as a percentage of health
     *
     * @return The percentage of health to damage
     */
    public static double continuousDmgAmount()
    {
        //Base DOT damage is 5% of max HP
        double returnAmount = 0.05;
        Team t1 = game.getNextMonsTeam(), t2 = game.getOtherTeam();
        for (int i = 0; i < t1.size(); i++)
        {
            //@Passive (Sath)
            //Double the damage if there is a Sath passive active
            if ((t1.get(i) instanceof Sath && !t1.get(i).isDead() && !t1.get(i).passiveCanActivate()) ||
                (t2.get(i) instanceof Sath && !t2.get(i).isDead() && !t2.get(i).passiveCanActivate()))
            {
                returnAmount *= 2.0;
                break;
            }
        }
        
        return returnAmount;
    }
    
    /**
     * Applies continuous damage to the Monster
     */
    public void applyContinuousDmg()
    {
        this.setCurrentHp((int) Math.ceil((this.getCurrentHp() - (this.getMaxHp() * continuousDmgAmount()))));
        //Print DOT dmg message
        if (print)
        {
            System.out.printf("DOT Applied, %s took %,d%n", this.getName(true, true), (int) (this.getMaxHp() * (continuousDmgAmount())));
        }
    }
    
    /**
     * Checks if the Monster's passive ability can be triggered
     *
     * @return True if the passive can be triggered, false otherwise
     */
    public boolean passiveCanActivate()
    {
        return (!this.containsDebuff(Debuff.OBLIVION));
    }
    
    /**
     * Applies the resistance check using a randomly generated number. Uses the target's resistance and self's accuracy
     *
     * @param target The target Monster
     * @return True if the random the randomly generated number is lower than the target's resistance minus self's accuracy (Minimum 15%), false otherwise
     */
    protected boolean resistanceCheck(Monster target)
    {
        return new Random().nextInt(101) < Math.max(15, Math.min(target.resistance, 100) - Math.min(this.accuracy, 100));
    }
    
    /**
     * Copies the changeable attributes of a Monster into a new object
     *
     * @return A new Monster with the same attributes as the original
     */
    public Monster copy()
    {
        //TODO finish push pop and have Raoq use them for ability 2
        Monster save = createNewMonFromName(this.getName(false, false));
        
        save.currentHp = this.currentHp;
        save.destroyedHp = this.destroyedHp;
        save.extraAtk = this.extraAtk;
        save.lessAtk = this.lessAtk;
        save.lessDef = this.lessDef;
        save.lessAtkSpd = this.lessAtkSpd;
        save.extraDef = this.extraDef;
        save.extraCritRate = this.extraCritRate;
        save.extraGlancingRate = this.extraGlancingRate;
        save.extraSpd = this.extraSpd;
        save.shield = this.shield;
        save.numOfViolentRuneProcs = this.numOfViolentRuneProcs;
        save.atkBar = this.atkBar;
        save.dead = this.dead;
        
        ArrayList<Buff> buffs = new ArrayList<>();
        this.getAppliedBuffs().forEach(b -> buffs.add(new Buff(b.getBuffNum(), b.getNumTurns())));
        
        ArrayList<Debuff> debuffs = new ArrayList<>();
        this.getAppliedDebuffs().forEach(d -> {
            debuffs.add(new Debuff(d.getDebuffNum(), d.getNumTurns(), 0));
            debuffs.getLast().setCaster(d.getCaster());
        });
        
        ArrayList<Stat> otherEffects = new ArrayList<>();
        this.otherStats.forEach(s -> {
            otherEffects.add(new Stat(s.getNumTurns()));
            otherEffects.getLast().setStatNum(s.getStatNum());
        });
        
        for (int i = 0; i < this.abilities.size(); i++)
        {
            save.abilities.get(i).setToNumTurns(this.abilities.get(i).getTurnsRemaining());
        }
        
        save.appliedBuffs = buffs;
        save.appliedDebuffs = debuffs;
        save.otherStats = otherEffects;
        
        return save;
    }
    
    /**
     * Pastes a Monster's changeable attributes onto self
     *
     * @param save The Monster to paste the attributes from
     */
    public void paste(Monster save)
    {
        if (!save.getName(false, false).equals(this.getName(false, false)))
        {
            return;
        }
        
        this.currentHp = save.currentHp;
        this.destroyedHp = save.destroyedHp;
        this.extraAtk = save.extraAtk;
        this.lessAtk = save.lessAtk;
        this.lessDef = save.lessDef;
        this.lessAtkSpd = save.lessAtkSpd;
        this.extraDef = save.extraDef;
        this.extraCritRate = save.extraCritRate;
        this.extraGlancingRate = save.extraGlancingRate;
        this.extraSpd = save.extraSpd;
        this.shield = save.shield;
        this.numOfViolentRuneProcs = save.numOfViolentRuneProcs;
        this.atkBar = save.atkBar;
        this.dead = save.dead;
        
        this.appliedBuffs = save.appliedBuffs;
        this.appliedDebuffs = save.appliedDebuffs;
        this.otherStats = save.otherStats;
        this.abilities = save.abilities;
    }
}