package Monsters;

import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import Errors.*;
import Game.*;
import Monsters.Fire.*;
import Runes.*;
import Util.Util.*;
import java.math.*;
import java.util.*;

import static Effects.Buffs.BuffEffect.*;
import static Effects.Debuffs.DebuffEffect.*;
import static Runes.RuneType.*;
import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

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
    public static final HashMap<String, String> MONSTER_NAMES_DATABASE = new HashMap<>();
    
    /**
     * Denotes the value for an attack bar to be full
     */
    public static final int MAX_ATK_BAR_VALUE = 1_000;
    private static boolean print = true;
    private boolean deathMsgPrinted = false;
    
    //Constant attributes
    private final int MAX_BOMBS = 3, MAX_DOT = 8, MAX_BUFFS = 8;
    //Constant path to this class
    public static final String path = Monster.class.getResource("Monster.class").getPath()
                                              .substring(0, Monster.class.getResource("Monster.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36)
                                              .replaceAll("%20", " ")
                                              .replaceAll("file:", "") + "/src/Monsters";
    
    //Monster stats
    private int currentHp, maxHp, destroyedHp = 0, def, atk, spd, critRate, critDmg, resistance, accuracy;
    private final Element element;
    protected static Game game = null;
    
    //Used when applying runes
    private double tempMaxHp, tempDef, tempAtk;
    
    //Used to apply buffs/debuffs
    private int extraAtk = 0, extraDef = 0, extraCritRate = 0, shield = 0;
    protected int extraSpd = 0;
    
    private String name;
    //Monster's base stats (unchanging)
    private final int baseMaxHp, baseAtk, baseDef, baseSpd, baseCritRate, baseCritDmg, baseRes, baseAcc;
    
    //Various variables for turns
    private int extraGlancingRate, lessAtk, lessDef;
    protected int lessAtkSpd;
    private int abilityGlancingRateChange;
    private int numOfViolentRuneProcs = 0;
    private double dmgDealtThisTurn, dmgDealtThisHit, dmgTakenThisTurn, dmgTakenThisHit;
    private double atkBar = 0;
    private boolean dead = false, crit = false, glancing = false, wasCrit = false, wasGlanced = false, singleTargetAttack = false, shouldCounter = false, isCopy = false;
    
    //Applied Effects
    private ArrayList<Buff> appliedBuffs = new ArrayList<>();
    private ArrayList<Debuff> appliedDebuffs = new ArrayList<>();
    private ArrayList<Effect> otherEffects = new ArrayList<>();
    
    //Abilities
    protected ArrayList<Ability> abilities;
    
    //Runes
    private ArrayList<Rune> runes = new ArrayList<>();
    
    //Initialize the database when creating the class
    static
    {
        setDatabase();
    }
    
    /**
     * Creates a new Monster
     *
     * @param name       The name of the Monster
     * @param element    The element of the Monster. Use <code>Monster.{ELEMENT}</code> Ex. <code>Monster.FIRE</code>
     * @param hp         The base health of the Monster
     * @param def        The base defense of the Monster
     * @param attack     The base attack power of the Monster
     * @param speed      The base attack speed of the Monster
     * @param critRate   The base crit hit rate of the Monster (as a percent)
     * @param critDmg    The base crit damage amount of the Monster (as a percent)
     * @param resistance The base resistance of the Monster (as a percent)
     * @param accuracy   The base accuracy of the Monster (as a percent)
     */
    public Monster(String name, Element element, int hp, int def, int attack, int speed, int critRate, int critDmg, int resistance, int accuracy)
    {
        //Make sure no Monster's speed is changed for testing
        //Comment this check if you are testing and increase the speed of a Monster
        final int maxSpeed = 350;
        if (speed > 350)
        {
            System.out.printf("Base speed must be less than %d: %s%n", maxSpeed, name);
            throw new IllegalArgumentException("Base Speed must be less than %d: %s".formatted(maxSpeed, name));
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
    public Monster(Element element)
    {
        this("", element, 0, 0, 0, 0, 15, 50, 15, 0);
    }
    
    /**
     * Creates a blank Monster
     */
    public Monster()
    {
        this(Element.FIRE);
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
     * @deprecated To be removed in v1.1.7. Please use {@link Monster#setAbilities(Ability...)}
     */
    public void setAbilities(ArrayList<Ability> abilities)
    {
        this.abilities = abilities;
    }
    
    /**
     * Sets the abilities for this object.
     *
     * @param abilities an array of Ability objects to be assigned. These abilities
     *                  will replace any pre-existing abilities for this object.
     */
    public void setAbilities(Ability... abilities)
    {
        this.abilities = new ArrayList<>(Arrays.asList(abilities));
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
            StringBuilder returnName = new StringBuilder();
            //Remove the ID number and return the result
            for (char c : name.toCharArray())
            {
                if (!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' || c == '(' || c == ')'))
                {
                    returnName.append(c);
                }
            }
            return returnName.toString();
        }
        //Get the element color
        ConsoleColor elementColor = element.getFontColor();
        //Return the name with the element followed by the ID number
        if (withNumber)
        {
            return elementColor + name + ConsoleColor.RESET;
        }
        StringBuilder returnName = new StringBuilder();
        //Remove the ID number from the name
        for (char c : name.toCharArray())
        {
            if ((c < '0' || c > '9') && c != '(' && c != ')')
            {
                returnName.append(c);
            }
        }
        //Return the name with the element
        return "" + elementColor + returnName + ConsoleColor.RESET;
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
    public Element getElement()
    {
        return element;
    }
    
    /**
     * Changes the Monster's current health. The updated health will be in the range <code>[0, maxHp - destroyedHp]</code>
     *
     * @param currentHp The Monster's new health
     */
    public void setCurrentHp(int currentHp)
    {
        this.currentHp = Math.max(Math.min(currentHp, maxHp - destroyedHp), 0);
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
     * @return All Effects (not buff or debuffs) on the Monster
     */
    public ArrayList<Effect> getOtherEffects()
    {
        return otherEffects;
    }
    
    /**
     * Set the attack bar to a given amount
     *
     * @param num The amount to set the attack bar to
     */
    public void setAtkBar(double num)
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
            if (this.containsBuff(BuffEffect.ENDURE))
            {
                this.currentHp = 1;
            }
            //Check for Soul Protection buff
            if (this.containsBuff(BuffEffect.SOUL_PROTECTION))
            {
                //Revive Monster with 30% HP
                currentHp = (int) (this.maxHp * 0.3);
                removeBuff(BuffEffect.SOUL_PROTECTION);
            }
        }
        return this.currentHp <= 0;
    }
    
    /**
     * Sets the hp ratio for an entity as a percentage value and updates the current health points accordingly.
     * The hpRatio is clamped to a value between 0 and 100 before being applied.
     *
     * @param hpRatio the health percentage to set, clamped to the range [0, 100]
     */
    public void setHpRatio(double hpRatio)
    {
        hpRatio = Math.max(0, Math.min(100, hpRatio));
        this.currentHp = (int) Math.ceil((hpRatio / 100.0) * maxHp);
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
     * Checks if the Monster was hit by a crit from the most recent hit on this turn
     *
     * @return True if the Monster was hit with a crit from the most recent attack this turn, false otherwise
     */
    public boolean wasCrit()
    {
        return wasCrit;
    }
    
    /**
     * Checks if the Monster was hit by a glancing hit from the most recent hit on this turn
     *
     * @return True if the Monster was hit with a glancing hit from the most recent attack this turn, false otherwise
     */
    public boolean wasGlanced()
    {
        return wasGlanced;
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
     * Resets all extra effects
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
     * Clears then sets {@link Monster#MONSTER_NAMES_DATABASE} according to the Monsters in "Monster database.csv"
     */
    private static void setDatabase()
    {
        //Clear the map
        MONSTER_NAMES_DATABASE.clear();
        
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
            MONSTER_NAMES_DATABASE.put(monAndElement[0], monAndElement[1]);
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
        ArrayList<Monster> allMons = new ArrayList<>();
        
        //Add a new instance of each Monster in the database
        try
        {
            MONSTER_NAMES_DATABASE.forEach((name, _) -> allMons.add(MONSTERS.createNewMonFromName(name, true)));
        }
        catch (Exception e)
        {
            System.err.println("Error getting database Monster instances");
            throw e;
        }
        
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
    public int getBuffIndex(BuffEffect num)
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
    public int getDebuffIndex(DebuffEffect debuff)
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
            if (ability.isViableAbility(this.containsDebuff(SILENCE)))
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
        StringBuilder s = new StringBuilder();
        //Get the Monster's name with its ID and element
        s.append("%s:\n".formatted(getName(true, true)));
        //Add each ability except for leader skills
        for (Ability ability : abilities)
        {
            if (ability instanceof Leader_Skill)
            {
                continue;
            }
            s.append("\t %s\n\n".formatted(ability.toString(this.containsDebuff(SILENCE), this.containsDebuff(OBLIVION))));
        }
        return s.toString();
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
                \t\tOther Effects: %s%s""".formatted(
                name, ConsoleColor.GREEN, healthPercent.substring(0, healthPercent.indexOf(".") + 2), ConsoleColor.CYAN, atkBarPercent.substring(0, atkBarPercent.indexOf(".") + 2), ConsoleColor.BLUE,
                appliedBuffs, ConsoleColor.RED,
                appliedDebuffs, ConsoleColor.PURPLE,
                otherEffects, ConsoleColor.RESET);
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
     * @param effect The buff to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedBuff(BuffEffect effect, int turns, Monster caster)
    {
        //Increase the number of turns by one if this is the caster
        if (caster.equals(this) && turns > 0)
        {
            turns++;
        }
        //Make sure the buff is not a shield
        if (effect == BuffEffect.SHIELD)
        {
            throw new RuntimeException("Please use the other addAppliedBuff method for shields");
        }
        
        //Check if the Monster already has the buff
        if (this.containsBuff(effect))
        {
            switch (effect)
            {
                //Continuous healing can stack with itself, no other buff can.
                case RECOVERY ->
                {
                    if (this.appliedBuffs.size() <= MAX_BUFFS)
                    {
                        appliedBuffs.add(new Buff(effect, turns));
                    }
                }
                case THREAT ->
                {
                    //Apply the new Threat if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(new Buff(effect, 1))).getNumTurns() < turns)
                    {
                        removeBuff(effect);
                        appliedBuffs.add(new Threat(turns));
                    }
                }
                case DEFEND ->
                {
                    //Apply the new Defend if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(new Buff(effect, 1))).getNumTurns() < turns)
                    {
                        removeBuff(effect);
                        appliedBuffs.add(new Defend(turns, caster));
                    }
                }
                default ->
                {
                    //Apply the new buff if and only if its turns active is longer than the one already applied
                    if (appliedBuffs.get(getBuffIndex(effect)).getNumTurns() < turns)
                    {
                        removeBuff(effect);
                        appliedBuffs.add(new Buff(effect, turns));
                    }
                }
            }
        }
        else //Newly applied buff
        {
            switch (effect)
            {
                //Special cases
                case THREAT -> appliedBuffs.add(new Threat(turns));
                case DEFEND -> appliedBuffs.add(new Defend(turns, caster));
                default -> appliedBuffs.add(new Buff(effect, turns));
            }
        }
    }
    
    /**
     * Adds a new buff to the Monster
     *
     * @param effect The buff to add
     * @param chance The chance it will apply
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedBuff(BuffEffect effect, double chance, int turns, Monster caster)
    {
        int random = new Random().nextInt(101);
        //Apply the buff with chance%
        if (random <= chance)
        {
            addAppliedBuff(effect, turns, caster);
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
            default -> addAppliedBuff(buff.getBuffEffect(), buff.getNumTurns(), caster);
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
     * @param effect The debuff to add
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    private void addAppliedDebuff(DebuffEffect effect, int turns, Monster caster)
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
        if (this.containsDebuff(effect))
        {
            //Bomb and DOT can stack
            //Provoke cannot be overridden
            if (effect != BOMB && effect != CONTINUOUS_DMG && effect != PROVOKE)
            {
                //Add the buff its turns are more than the one already applied
                if (appliedDebuffs.get(getDebuffIndex(effect)).getNumTurns() < turns)
                {
                    this.removeDebuff(effect);
                    appliedDebuffs.add(new Debuff(effect, turns, 0));
                }
            }
            else
            {
                if (effect == BOMB)
                {
                    //Check for the number of bomb stacks
                    if (countDebuff(BOMB) < MAX_BOMBS)
                    {
                        appliedDebuffs.add(new Debuff(BOMB, turns, 0));
                    }
                }
                else
                {
                    //Check for the number of DOT stacks
                    if (countDebuff(CONTINUOUS_DMG) < MAX_DOT)
                    {
                        appliedDebuffs.add(new Debuff(CONTINUOUS_DMG, turns, 0));
                    }
                }
            }
        }
        //Add new Provoke
        else if (effect == PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        //Add the debuff
        else
        {
            appliedDebuffs.add(new Debuff(effect, turns, 0));
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
        if (this.containsBuff(IMMUNITY) && !debuff.goesThroughImmunity())
        {
            return;
        }
        DebuffEffect effect = debuff.getDebuffEffect();
        int turns = debuff.getNumTurns();
        if (this.containsDebuff(effect))
        {
            //Bomb and DOT can stack, provoke cannot be overridden
            if (effect != BOMB && effect != CONTINUOUS_DMG && effect != PROVOKE)
            {
                //Add the debuff if it has more turns than the one already applied
                if (appliedDebuffs.get(getDebuffIndex((effect))).getNumTurns() < turns)
                {
                    removeDebuff(effect);
                    appliedDebuffs.add(debuff);
                }
            }
            else
            {
                if (effect == BOMB)
                {
                    //Check for the number of bomb stacks
                    if (countDebuff(BOMB) < MAX_BOMBS)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
                else
                {
                    //Check for the number of DOT stacks
                    if (countDebuff(CONTINUOUS_DMG) < MAX_DOT)
                    {
                        appliedDebuffs.add(debuff);
                    }
                }
            }
        }
        //Add a new Provoke
        else if (effect == PROVOKE)
        {
            appliedDebuffs.add(new Provoke(turns, caster));
        }
        else //Add the new debuff
        {
            appliedDebuffs.add(debuff);
        }
    }
    
    /**
     * Adds a new effect to the Monster that ignores resistance (Still checks for immunity buff)
     *
     * @param effect The effect to add
     * @param turns  The number of turns to apply the effect
     * @param caster The Monster who gave it
     */
    public void addGuaranteedAppliedDebuff(DebuffEffect effect, int turns, Monster caster)
    {
        addGuaranteedAppliedDebuff(new Debuff(effect, turns, 0), caster);
    }
    
    /**
     * Adds a new debuff to the Monster (Assumes debuff does not go through immunity
     *
     * @param effect The debuff to add
     * @param chance The chance the debuff will be applied (0-100)
     * @param turns  The number of turns it is active
     * @param caster The Monster who gave it
     */
    public void addAppliedDebuff(DebuffEffect effect, double chance, int turns, Monster caster)
    {
        //Check for immunity buff
        if (this.containsBuff(IMMUNITY))
        {
            if (print)
            {
                printfWithColor("Immunity!", ConsoleColor.GREEN);
            }
            return;
        }
        //Resistance check
        int random = new Random().nextInt(101);
        if (random <= chance)
        {
            addAppliedDebuff(effect, turns, caster);
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
        if (debuff instanceof DecAtkBar || debuff instanceof ShortenBuff)
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
        addAppliedDebuff(debuff.getDebuffEffect(), debuff.getNumTurns(), caster);
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
        if (this.containsBuff(IMMUNITY) && !debuff.goesThroughImmunity())
        {
            if (print)
            {
                printfWithColor("Immunity!", ConsoleColor.GREEN);
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
     * Removes all instances of a effect
     *
     * @param effect The number of the effect to remove
     */
    public void removeDebuff(DebuffEffect effect)
    {
        removeDebuff(new Debuff(effect));
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
     * @param num The buff to remove
     */
    public void removeBuff(BuffEffect num)
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
     * Counts the number of instances of the given effect are on the Monster
     *
     * @param effect The effect to count
     * @return The number of instances of the given effect
     */
    public int countBuff(BuffEffect effect)
    {
        return countBuff(new Buff(effect));
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
     * Counts the number of instances of the given effect are on the Monster
     *
     * @param effect The effect to count
     * @return The number of instances of the given effect
     */
    public int countDebuff(DebuffEffect effect)
    {
        return countDebuff(new Debuff(effect));
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
        int critRate = (target.containsBuff(CRIT_RESIST_UP)) ? (int) (this.critRate * 0.7) : this.critRate;
        ConsoleColor elementRelation = element.relationWith(target.getElement());
        
        //Change crit rate based on the elemental relationship
        int critChanceChange = 0;
        int critResist = (target.containsBuff(CRIT_RESIST_UP)) ? -50 : 0;
        int glancingChanceChange = 0;
        if (elementRelation.equals(ConsoleColor.GREEN_BACKGROUND))
        {
            critChanceChange = 15;
        }
        else if (elementRelation.equals(ConsoleColor.RED_BACKGROUND))
        {
            critChanceChange = -15;
            glancingChanceChange = -50;
        }
        
        //Glancing
        if (new Random().nextInt(100) < (extraGlancingRate + glancingChanceChange + abilityGlancingRateChange))
        {
            //Current dmg * 0.7
            dmg *= 0.7;
            glancing = true;
        }
        //Crit
        else if (new Random().nextInt(100) < (critRate + critChanceChange + extraCritRate + critResist))
        {
            //Current dmg * 1.critDmg
            dmg *= (1 + ((this.critDmg) / 100.0));
            crit = true;
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
        //1,000 / (1,140 + 3.5d)
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
            removeBuff(BuffEffect.SHIELD);
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
        if (print && num > 0)
        {
            System.out.printf("%s's Attack bar increased by %d percent%n", this.getName(true, true), num);
        }
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
    
    public boolean turnIsValid(Monster target, int abilityNum)
    {
        Ability a = abilities.get(abilityNum - 1);
        return a.getTurnsRemaining() <= 0 && targetIsValid(target, a.targetsEnemy());
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
                    attack(target, a, false, false);
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
        if (this.containsBuff(CLEANSE))
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
        
        //Apply effects
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
        String ignoreDef = (ability.ignoresDefense()) ? STRINGS.formatWithColor("Ignore Defense! ", ConsoleColor.RED) : " ";
        
        //Brand
        if (target.containsDebuff(BRAND))
        {
            finalDmg *= 1.25;
        }
        
        //Invincibility
        if (!ability.ignoresDmgReduction() && target.containsBuff(INVINCIBILITY))
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
        if (target.containsBuff(REFLECT))
        {
            dealDmg(finalDmg * 0.3, false);
            if (print)
            {
                printfWithColor("%d damage reflected.", ConsoleColor.PURPLE, (int) (finalDmg * 0.3));
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
                printfWithColor("Defend!", ConsoleColor.BLUE);
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
        target.removeDebuff(SLEEP);
        tempTarget.removeDebuff(SLEEP);
        
        //Apply damage reduction
        if (!ability.ignoresDmgReduction())
        {
            finalDmg = TEAMS.dmgReduction(game.getOtherTeam(), finalDmg, target);
        }
        
        //Activate damage increasing passive if there is one
        finalDmg = dmgIncProtocol(finalDmg);
        
        //Round the damage up
        finalDmg = Math.ceil(finalDmg);
        
        //Deal damage to target
        tempTarget.dealDmg(finalDmg, ability.ignoresDmgReduction());
        dmgDealtThisTurn += finalDmg;
        dmgDealtThisHit = finalDmg;
        target.dmgTakenThisHit = finalDmg;
        target.dmgTakenThisTurn += finalDmg;
        
        //Print crit or glancing hit messages
        if (crit)
        {
            if (print)
            {
                printfWithColor("Critical hit! ", ConsoleColor.GREEN);
            }
        }
        if (glancing)
        {
            if (print)
            {
                printfWithColor("Glancing hit! ", ConsoleColor.YELLOW);
            }
        }
        
        //Print damage dealt
        if (print)
        {
            printfWithColor("%s%s dealt %,d damage to %s.\n", ConsoleColor.PURPLE, ignoreDef, getName(true, true),
                    (int) finalDmg, tempTarget.getName(true, true));
        }
        
        target.wasCrit = crit;
        target.wasGlanced = glancing;
        
        crit = false;
        glancing = false;
        
        //After hit passive
        this.selfAfterHitProtocol(target, this.abilities.indexOf(ability) + 1, count);
        
        //Vampire Buff
        if (containsBuff(BuffEffect.VAMPIRE) && !containsDebuff(UNRECOVERABLE))
        {
            this.setCurrentHp((int) (currentHp + (0.2 * finalDmg)));
        }
        
        //Apply buffs to self if Monster does not have the beneficial effect block debuff
        if (!this.containsDebuff(BLOCK_BENEFICIAL_EFFECTS))
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                this.addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
            }
        }
        
        target.shouldCounter = target.containsBuff(COUNTER);
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
            DebuffEffect debuffEffect = debuffs.get(i).getDebuffEffect();
            int chance = debuffsChance.get(i) + increasedChance;
            //Stripping is not affected by glancing hits
            if (glancing && (debuffEffect == STRIP || debuffEffect == REMOVE_BENEFICIAL_EFFECT || debuffEffect == SHORTEN_BUFFS))
            {
                chance -= increasedChance;
            }
            target.addAppliedDebuff(debuffs.get(i), chance, this);
        }
        if (print)
        {
            System.out.println("\n");
        }
        
        //Seal Rune
        if (this.numOfSets(RuneType.SEAL) > 2 && !this.containsDebuff(DebuffEffect.SEAL))
        {
            double chance = 25;
            if (glancing)
            {
                chance /= 2;
            }
            target.addAppliedDebuff(DebuffEffect.SEAL, chance * this.numOfSets(RuneType.SEAL), 1, this);
        }
        
        //Decrease Atk Bar
        if (target.hasDecAtkBar())
        {
            DecAtkBar dec = target.getDecAtkBar();
            target.decreaseAtkBarByPercent(dec.getAmount());
            target.removeDebuff(dec);
            if (print)
            {
                printfWithColor("Decreased %s's Attack bar by %d%%!%n", ConsoleColor.RED, target.getName(true, true), dec.getAmount());
            }
        }
        
        //Buff steal
        if (this.containsBuff(BUFF_STEAL))
        {
            this.stealBuff(target);
        }
        
        //Remove beneficial effect
        if (target.containsDebuff(REMOVE_BENEFICIAL_EFFECT))
        {
            target.removeRandomBuff();
            target.removeDebuff(REMOVE_BENEFICIAL_EFFECT);
        }
        
        //Strip
        if (target.containsDebuff(STRIP))
        {
            this.removeBuff(THREAT);
            while (!this.appliedBuffs.isEmpty())
            {
                this.decreaseStatCooldowns();
            }
        }
        
        //Shorten beneficial effect
        if (target.containsDebuff(SHORTEN_BUFFS))
        {
            ShortenBuff sb = (ShortenBuff) target.getAppliedDebuffs().get(target.getDebuffIndex(SHORTEN_BUFFS));
            for (Buff buff : target.getAppliedBuffs())
            {
                buff.decreaseTurn(sb.getAmount());
            }
        }
        
        //Remove harmful effect
        if (this.containsBuff(REMOVE_DEBUFF))
        {
            this.removeRandomDebuff();
            this.removeBuff(REMOVE_DEBUFF);
        }
        
        //Target after hit passive
        target.targetAfterHitProtocol(this);
        if (!tempTarget.equals(target))
        {
            tempTarget.targetAfterHitProtocol(this);
        }
        
        //Vampire Rune
        if (!this.containsDebuff(UNRECOVERABLE) && !this.containsDebuff(DebuffEffect.SEAL))
        {
            this.setCurrentHp((int) Math.ceil((currentHp + (finalDmg * 0.35 * numOfSets(RuneType.VAMPIRE)))));
        }
        
        //Destroy Rune
        if (this.numOfSets(DESTROY) > 0 && !this.containsDebuff(DebuffEffect.SEAL))
        {
            //Max 4% of target's max HP per Destroy set
            int percentToDestroy = 4 * numOfSets(DESTROY);
            
            //30% of damage dealt
            double amountToDestroy = 0.3 * finalDmg;
            if (amountToDestroy > maxHp * (percentToDestroy / 100.0))
            {
                amountToDestroy = maxHp * (percentToDestroy / 100.0);
            }
            this.destroyHp((int) Math.ceil(amountToDestroy));
        }
        
        //Nemesis Rune
        if (tempTarget.numOfSets(NEMESIS) > 0 && !tempTarget.containsDebuff(DebuffEffect.SEAL))
        {
            double dmgPercent = finalDmg / target.getMaxHp();
            double atkBarPercentIncrease = 0.04 * tempTarget.numOfSets(NEMESIS) * dmgPercent / 0.07;
            tempTarget.increaseAtkBarByPercent((int) Math.ceil(atkBarPercentIncrease));
        }
        
        //Despair Rune
        if (this.numOfSets(DESPAIR) > 0 && !this.containsDebuff(DebuffEffect.SEAL))
        {
            double chance = 25.0;
            if (glancing)
            {
                chance /= 2;
            }
            target.addAppliedDebuff(STUN, chance, 1, this);
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
    public void attack(Monster target, Ability ability, boolean isCounter, boolean ignoreTargetDeathState)
    {
        //Make sure this is not dead
        if (this.isDead())
        {
            this.kill();
            return;
        }
        
        if (!ignoreTargetDeathState && target.isDead())
        {
            return;
        }
        
        attack(target, ability, isCounter, 1);
        
        //Heal self if the ability heals based off damage done
        heal(this, dmgDealtThisHit * ability.getHealingPercent() / 100.0);
    }
    
    /**
     * The Monster's basic attack command. This method assumes the attack is not a counter and will execute all functions that are necessary during the Monster's turn
     *
     * @param target  The target Monster to attack
     * @param ability The ability to attack with
     */
    public void attack(Monster target, Ability ability)
    {
        attack(target, ability, false, false);
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
                this.attack(monster, ability);
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
            if (buff.getBuffEffect() == REMOVE_DEBUFF)
            {
                //Remove a random debuff
                target.removeRandomDebuff();
                target.removeBuff(REMOVE_DEBUFF);
            }
            //Cleanse the target
            if (buff.getBuffEffect() == CLEANSE)
            {
                target.cleanse();
                target.removeBuff(CLEANSE);
            }
        }
        
        //Heal the target if they do not have unrecoverable
        heal(target, ability.getHealingPercent() * target.getMaxHp());
        
        //Apply buffs to the target if they do not have Beneficial Effect Blocker
        if (!target.containsDebuff(BLOCK_BENEFICIAL_EFFECTS))
        {
            ArrayList<Buff> buffs = ability.getBuffs();
            ArrayList<Integer> buffsChance = ability.getBuffsChance();
            for (int i = 0; i < buffs.size(); i++)
            {
                if (buffs.get(i).getBuffEffect() != CLEANSE && buffs.get(i).getBuffEffect() != REMOVE_DEBUFF)
                {
                    target.addAppliedBuff(buffs.get(i), buffsChance.get(i), this);
                }
            }
        }
        
        //Extend Beneficial Effects
        if (target.containsBuff(EXTEND_BUFF))
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
            target.removeBuff(EXTEND_BUFF);
        }
        
        //Shorten Harmful effect
        if (target.containsBuff(SHORTEN_DEBUFF))
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
            target.removeBuff(SHORTEN_DEBUFF);
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
     * Heals the specified Monster for a given amount of health. The healing process
     * accounts for conditions such as whether the target is dead or has the unrecoverable
     * debuff. If the target is affected by the unrecoverable debuff, the method will output
     * a warning message (if printing is enabled) and terminate without applying healing.
     *
     * @param target The Monster instance to heal. Healing will not be applied if the target
     *               is dead or has the unrecoverable debuff.
     * @param amount The amount of health to restore to the target. This value is rounded up
     *               to the nearest integer and added to the target's current health, without
     *               exceeding their maximum health minus destroyed health.
     */
    protected void heal(Monster target, double amount)
    {
        //Make sure the target can be healed
        if (target.isDead() || target.containsDebuff(UNRECOVERABLE))
        {
            if (print && target.containsDebuff(UNRECOVERABLE))
            {
                printfWithColor("Unrecoverable!\n", ConsoleColor.RED);
            }
            return;
        }
        
        int intAmount = (int) Math.ceil(amount);
        
        //Heal while making sure hp does not exceed max amount
        target.currentHp += intAmount;
        target.currentHp = Math.min(target.currentHp, target.maxHp - target.destroyedHp);
        
        if (print && intAmount > 0)
        {
            printfWithColor("Healed %s for %,d health\n", ConsoleColor.GREEN, target.getName(true, true), intAmount);
        }
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
        target.dmgTakenThisTurn = 0;
        target.dmgTakenThisHit = 0;
        
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
        
        //Remove any other effects with no turns left
        for (int i = target.otherEffects.size() - 1; i >= 0; i--)
        {
            Effect effect = target.otherEffects.get(i);
            if (effect.getNumTurns() <= 0)
            {
                target.removeOtherEffect(effect);
            }
        }
        
        //Check if the target or self are dead
        if (this.isDead())
        {
            this.kill();
        }
        if (target.isDead())
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
        if (this.isDead())
        {
            this.kill();
        }
        
        //Do after turn function for each Monster
        for (Monster target : targetMons)
        {
            if (target.isDead())
            {
                target.kill();
            }
            if (attack)
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
        target.dmgTakenThisTurn = 0;
        target.dmgTakenThisHit = 0;
        
        if (this.isDead())
        {
            this.kill();
        }
        if (target.isDead())
        {
            target.kill();
        }
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
            this.decreaseStatCooldowns();
            if (!isStunned())
            {
                this.decreaseAbilityCooldowns();
            }
        }
        
        //Reset damage dealt this turn
        dmgDealtThisTurn = 0;
        dmgDealtThisHit = 0;
        
        //Violent Rune
        boolean vioProcced = false;
        if (numOfSets(VIOLENT) > 0 && Game.canCounter() && !containsDebuff(DebuffEffect.SEAL))
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
                    printfWithColor("Extra Turn!%n", ConsoleColor.GREEN);
                }
            }
            else //Reset vio procs
            {
                numOfViolentRuneProcs = 0;
            }
        }
        Game.setCanCounter(true);
        //Increase every Monster's attack bar if Violent rune was not procced and the turn was not a counter
        if (!isCounter && !vioProcced && !this.isCopy)
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
        if (stolen.getBuffEffect() != DEFEND && stolen.getBuffEffect() != BuffEffect.NULL)
        {
            addAppliedBuff(stolen.getBuffEffect(), stolen.getNumTurns(), this);
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
            switch (buff.getBuffEffect())
            {
                case ATK_UP -> extraAtk = (int) (atk * 0.5);
                case DEF_UP -> extraDef = (int) (def * 0.7);
                case CRIT_RATE_UP -> extraCritRate = (int) (critRate * 0.3);
                case ATK_SPD_UP -> extraSpd = (int) (spd * 0.3);
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
            switch (debuff.getDebuffEffect())
            {
                case GLANCING_HIT_UP -> extraGlancingRate = 50;
                case DEC_ATK -> lessAtk = (int) (atk * 0.5);
                case DEC_DEF -> lessDef = (int) (def * 0.7);
                case DEC_ATK_SPD -> lessAtkSpd = (int) (spd * 0.3);
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
            
            //Reset extra effect values if the buff has no turns left
            if (buff.getNumTurns() <= 0)
            {
                switch (buff.getBuffEffect())
                {
                    case ATK_UP -> extraAtk = 0;
                    case DEF_UP -> extraDef = 0;
                    case CRIT_RATE_UP -> extraCritRate = 0;
                    case ATK_SPD_UP -> extraSpd = 0;
                    case SHIELD -> shield = 0;
                }
                appliedBuffs.remove(i);
            }
        }
        
        for (int i = appliedDebuffs.size() - 1; i >= 0; i--)
        {
            Debuff debuff = appliedDebuffs.get(i);
            //Decrease the debuffs turns left
            debuff.decreaseTurn();
            //Remove extra effect values if the debuff has no turns left
            if (debuff.getNumTurns() <= 0)
            {
                switch (debuff.getDebuffEffect())
                {
                    case GLANCING_HIT_UP -> extraGlancingRate = 0;
                    case DEC_ATK -> lessAtk = 0;
                    case DEC_DEF -> lessDef = 0;
                    case DEC_ATK_SPD -> lessAtkSpd = 0;
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
    public boolean containsDebuff(DebuffEffect num)
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
    public boolean containsBuff(BuffEffect num)
    {
        return containsBuff(new Buff(num));
    }
    
    /**
     * Checks if the Monster has the given Effect (Not buff or debuff)
     *
     * @param effect The Effect to look for
     * @return True if the Monster has the provided Effect
     */
    public boolean containsOtherEffect(Effect effect)
    {
        for (Effect s : otherEffects)
        {
            if (s.equals(effect))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the Monster has the Effect (Not buff or debuff) associated with the given number
     *
     * @param effect The Effect number to look for
     * @return True if the Monster has the Effect associated with the given number
     */
    public boolean containsOtherEffect(OtherEffect effect)
    {
        Effect s = new Effect(1);
        s.setEffect(effect);
        return containsOtherEffect(s);
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
            return new Buff(BuffEffect.NULL, 0);
        }
        //Get a random buff
        int rand = new Random().nextInt(appliedBuffs.size());
        Buff buff = this.appliedBuffs.remove(rand);
        //Reset extra effect values if needed
        switch (buff.getBuffEffect())
        {
            case ATK_UP -> extraAtk = 0;
            case DEF_UP -> extraDef = 0;
            case CRIT_RATE_UP -> extraCritRate = 0;
            case ATK_SPD_UP -> extraSpd = 0;
            case SHIELD -> shield = 0;
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
            return new Debuff(DebuffEffect.NULL, 0, 0);
        }
        //Get a random debuff
        int rand = new Random().nextInt(appliedDebuffs.size());
        Debuff debuff = appliedDebuffs.remove(rand);
        //Reset extra effect values if needed
        switch (debuff.getDebuffEffect())
        {
            case GLANCING_HIT_UP -> extraGlancingRate = 0;
            case DEC_ATK -> lessAtk = 0;
            case DEC_DEF -> lessDef = 0;
            case DEC_ATK_SPD -> lessAtkSpd = 0;
        }
        return debuff;
    }
    
    /**
     * Adds a Effect (not buff or debuff) to the Monster
     *
     * @param effect The Effect to add
     */
    public void addOtherEffect(Effect effect)
    {
        otherEffects.add(effect);
    }
    
    /**
     * Removes all instances of the provided Effect
     *
     * @param effect The Effect to look for
     */
    public void removeOtherEffect(Effect effect)
    {
        for (int i = otherEffects.size() - 1; i >= 0; i--)
        {
            if (otherEffects.get(i).equals(effect))
            {
                otherEffects.remove(i);
            }
        }
    }
    
    /**
     * Removes the effect from the Monster
     *
     * @param num The effect number to remove
     */
    public void removeOtherEffect(OtherEffect num)
    {
        Effect s = new Effect(1);
        s.setEffect(num);
        removeOtherEffect(s);
    }
    
    /**
     * Checks if the Monster is stunned on this turn. (If the Monster is slept, frozen, or stunned)
     *
     * @return True if the Monster is stunned, false otherwise
     */
    public boolean isStunned()
    {
        return containsDebuff(SLEEP) || containsDebuff(FREEZE) || containsDebuff(STUN);
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
     * Removes all buffs on the Monster
     *
     * @return The number of buffs removed
     */
    public int strip()
    {
        int size = appliedBuffs.size();
        this.removeBuff(THREAT);
        while (!this.appliedBuffs.isEmpty())
        {
            this.decreaseStatCooldowns();
        }
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
        if (containsBuff(BuffEffect.ENDURE))
        {
            currentHp = 1;
        }
        //Soul Protection
        else if (containsBuff(SOUL_PROTECTION))
        {
            currentHp = (int) (maxHp * 0.3);
            removeBuff(new Buff(SOUL_PROTECTION, 1));
        }
        else //Kill the Monster
        {
            currentHp = 0;
            //Remove all buffs and debuffs
            if (this.containsBuff(THREAT))
            {
                this.removeBuff(THREAT);
            }
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
            if (print && !this.deathMsgPrinted)
            {
                printfWithColor("%s died!\n", ConsoleColor.RED_BOLD_BRIGHT, this.getName(true, true));
                this.deathMsgPrinted = true;
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
     * Formats a set of numbers into an ArrayList of Debuffs
     *
     * @param args The numbers to format. Format: Debuff number, number of turns, whether it goes through immunity (0 for false, 1 for true), repeat as needed
     * @return The ArrayList of Debuffs specified by the varargs
     */
    public ArrayList<Debuff> abilityDebuffs(int... args)
    {
        return MONSTERS.abilityDebuffs(this, args);
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
        if (shouldCounter && !isDead() && Game.canCounter() && !isStunned())
        {
            this.shouldCounter = false;
            Game.setCanCounter(false);
            if (print)
            {
                System.out.println("Counter!");
            }
            this.counter(attacker);
        }
        
        //Revenge Rune
        if (numOfSets(REVENGE) > 0 && Game.canCounter() && !isStunned() && !containsDebuff(DebuffEffect.SEAL))
        {
            Game.setCanCounter(false);
            int random = new Random().nextInt(101);
            if (random <= 15 * numOfSets(REVENGE))
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
        
        //Save the Monster's current state and ability numbers
        Monster copy = this.copy();
        double tempAtkBar = copy.getAtkBar();
        
        //Counter the attacker
        copy.nextTurn(attacker, 1);
        
        //Reset all effect and ability numbers
        this.setAtkBar(tempAtkBar + copy.getAtkBar());
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
     * Prints the Monsters stats followed by its abilities and runes
     */
    public void printWithDetails()
    {
        //Print stats and effects
        if (print)
        {
            System.out.printf("%s: %sHp: %,d %s+%,d; %sAttack: %,d %s+%,d; %sDefense: %,d %s+%,d; %sSpeed: %d %s+%d; %sCrit rate: %d%%; %sCrit damage: %d%%; %sResistance: %d%%; Accuracy: %s%d%%%s\n\n", getName(true, false), ConsoleColor.GREEN,
                    baseMaxHp, ConsoleColor.GREEN_BOLD_BRIGHT, maxHp - baseMaxHp, ConsoleColor.RED, baseAtk,
                    ConsoleColor.RED_BOLD_BRIGHT, atk - baseAtk, ConsoleColor.YELLOW, baseDef,
                    ConsoleColor.YELLOW_BOLD_BRIGHT, def - baseDef, ConsoleColor.CYAN, baseSpd, ConsoleColor.CYAN_BOLD_BRIGHT,
                    spd - baseSpd, ConsoleColor.BLUE, critRate, ConsoleColor.PURPLE, critDmg, ConsoleColor.RESET, Math.min(resistance, 100),
                    (accuracy >= 100) ? ConsoleColor.RED : "", Math.min(accuracy, 100), ConsoleColor.RESET);
        }
        //Print each ability
        for (Ability ability : abilities)
        {
            if (print)
            {
                System.out.printf("\t %s\n\n%n", ability.toString(containsDebuff(SILENCE), containsDebuff(OBLIVION)));
            }
        }
        
        //Print each rune type
        if (runes != null)
        {
            if (print)
            {
                printfWithColor("Rune sets:%n", ConsoleColor.PURPLE);
            }
            ArrayList<RuneType> runeTypes = new ArrayList<>();
            //Get the rune types
            for (Rune rune : runes)
            {
                runeTypes.add(rune.getType());
            }
            ArrayList<String> types = new ArrayList<>();
            while (!runeTypes.isEmpty())
            {
                //Count the number of each set
                RuneType currentType = runeTypes.getLast();
                for (int i = runeTypes.size() - 1; i >= 0; i--)
                {
                    if (runeTypes.get(i) == currentType)
                    {
                        runeTypes.remove(runeTypes.get(i));
                    }
                }
                if (numOfSets(currentType) == 0)
                {
                    continue;
                }
                //Format the number of sets
                types.add("%s x%d".formatted(currentType, numOfSets(currentType)));
            }
            Collections.reverse(types);
            
            //Print the rune sets
            for (String string : types)
            {
                if (print)
                {
                    printfWithColor("\t%s\t", ConsoleColor.PURPLE, string);
                }
            }
        }
        
        if (print)
        {
            System.out.printf("%s\n\n", ConsoleColor.RESET);
        }
    }
    
    /**
     * Applies all stat rune effects that are not team-based
     */
    private void applyRuneSetEffectsForBeginningOfGame()
    {
        //Apply set effects
        tempMaxHp = applyEffect(ENERGY, tempMaxHp, baseMaxHp, 0.15);
        tempAtk = applyEffect(FATAL, tempAtk, baseAtk, 0.35);
        spd = (int) Math.ceil(applyEffect(SWIFT, spd, baseSpd, 0.25));
        tempDef = applyEffect(GUARD, tempDef, baseDef, 0.15);
        critRate += (12 * numOfSets(BLADE));
        accuracy += (20 * numOfSets(FOCUS));
        resistance += (20 * numOfSets(RuneType.ENDURE));
        critDmg += (40 * numOfSets(RAGE));
        
        //Add immunity for each set of Will
        if (numOfSets(WILL) > 0)
        {
            this.addAppliedBuff(IMMUNITY, numOfSets(WILL), this);
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
    private double applyEffect(RuneType setNum, double tempStat, double baseStat, double amt)
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
    private int countNumOfEffectRunes(RuneType type)
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
    public int numOfSets(RuneType type)
    {
        //Fatal, Swift, Vampire, Despair, Violent, and Rage runes need 4 to count as a set
        if (countNumOfEffectRunes(type) >= 4 && (type == FATAL || type == SWIFT || type == RuneType.VAMPIRE || type == DESPAIR || type == VIOLENT || type == RAGE))
        {
            return 1;
        }
        //Artifacts only need 1 to count as a set
        if (type == ELEMENT_ARTIFACT || type == TYPE_ARTIFACT)
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
     * Activates target's passive abilities that are triggered after a Monster is hit
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
        for (int i = this.otherEffects.size() - 1; i >= 0; i--)
        {
            Effect effect = this.otherEffects.get(i);
            if (effect.getNumTurns() <= 0)
            {
                this.removeOtherEffect(effect);
            }
        }
    }
    
    /**
     * Activates attacker's passive abilities that are triggered after a Monster attacks a target
     *
     * @param target     The target Monster of the attack
     * @param abilityNum The chosen ability number
     */
    public void selfAfterHitProtocol(Monster target, int abilityNum, int count)
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
        removeBuff(THREAT);
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
        dmgDealtThisHit = 0;
        dmgTakenThisTurn = 0;
        dmgTakenThisHit = 0;
        crit = false;
        wasCrit = false;
        glancing = false;
        
        //Apply immunity for each Will set
        if (this.numOfSets(WILL) > 0)
        {
            this.addAppliedBuff(IMMUNITY, numOfSets(WILL), new Monster());
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
        return !this.containsDebuff(UNRECOVERABLE) && !this.isDead();
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
            if ((t1.get(i) instanceof Sath && !t1.get(i).isDead() && t1.get(i).passiveCanActivate()) ||
                (t2.get(i) instanceof Sath && !t2.get(i).isDead() && t2.get(i).passiveCanActivate()))
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
        return (!this.containsDebuff(OBLIVION));
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
        Monster save = MONSTERS.createNewMonFromMon(this);
        
        save.name = this.name;
        
        save.currentHp = this.currentHp;
        save.destroyedHp = this.destroyedHp;
        
        save.extraAtk = this.extraAtk;
        save.lessAtk = this.lessAtk;
        
        save.extraDef = this.extraDef;
        save.lessDef = this.lessDef;
        
        save.extraSpd = this.extraSpd;
        save.lessAtkSpd = this.lessAtkSpd;
        
        save.extraCritRate = this.extraCritRate;
        save.extraGlancingRate = this.extraGlancingRate;
        
        save.shield = this.shield;
        
        save.numOfViolentRuneProcs = this.numOfViolentRuneProcs;
        
        save.atkBar = this.atkBar;
        
        save.dead = this.dead;
        
        ArrayList<Buff> buffs = new ArrayList<>();
        this.getAppliedBuffs().forEach(b -> buffs.add(new Buff(b.getBuffEffect(), b.getNumTurns())));
        
        ArrayList<Debuff> debuffs = new ArrayList<>();
        this.getAppliedDebuffs().forEach(d -> {
            debuffs.add(new Debuff(d.getDebuffEffect(), d.getNumTurns(), 0));
            debuffs.getLast().setCaster(d.getCaster());
        });
        
        ArrayList<Effect> otherEffects = new ArrayList<>();
        this.otherEffects.forEach(s -> {
            otherEffects.add(new Effect(s.getNumTurns()));
            otherEffects.getLast().setEffect(s.getEffect());
        });
        
        for (int i = 0; i < this.abilities.size(); i++)
        {
            save.abilities.get(i).setToNumTurns(this.abilities.get(i).getTurnsRemaining());
        }
        
        save.appliedBuffs = buffs;
        save.appliedDebuffs = debuffs;
        save.otherEffects = otherEffects;
        
        save.isCopy = true;
        
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
        
        //HP
        this.currentHp = save.currentHp;
        this.destroyedHp = save.destroyedHp;
        
        //Attack
        this.extraAtk = save.extraAtk;
        this.lessAtk = save.lessAtk;
        
        //Defense
        this.extraDef = save.extraDef;
        this.lessDef = save.lessDef;
        
        //Speed
        this.extraSpd = save.extraSpd;
        this.lessAtkSpd = save.lessAtkSpd;
        
        //Crit/Glancing rate
        this.extraCritRate = save.extraCritRate;
        this.extraGlancingRate = save.extraGlancingRate;
        
        //Shield
        this.shield = save.shield;
        
        //Vio procs
        this.numOfViolentRuneProcs = save.numOfViolentRuneProcs;
        
        //Attack bar
        this.atkBar = save.atkBar;
        
        //Dead state
        this.dead = save.dead;
        
        //Effects
        this.appliedBuffs = save.appliedBuffs;
        this.appliedDebuffs = save.appliedDebuffs;
        this.otherEffects = save.otherEffects;
        
        //Abilities
        this.abilities = save.abilities;
    }
}