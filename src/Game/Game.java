package Game;

import Errors.*;
import Monsters.Fire.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * This class contains all the information for the game that is used by {@link Main}, {@link Auto_Play} and {@link Test_One_Team}.
 *
 * @author Anthony (Tony) Youssef
 */
public class Game
{
    private static boolean canCounter;
    private static final Scanner scan = new Scanner(System.in);
    private final Team t1, t2;
    
    //Team with the Monster with the highest atk bar : other team
    private Team teamWithHighestAtkBar = new Team("", new ArrayList<>()), other = new Team("", new ArrayList<>());
    
    /**
     * Creates a new {@link Game} object
     *
     * @param t1 The first Team in the game
     * @param t2 The second Team in the game
     */
    public Game(Team t1, Team t2)
    {
        Monster.setGame(this);
        if (t1.size() != t2.size())
        {
            throw new ConflictingArguments("Teams must have the same number of mons.");
        }
        
        this.t1 = t1;
        this.t2 = t2;
    }
    
    /**
     * @return true if a counter is possible in this Game, false otherwise.
     */
    public static boolean canCounter()
    {
        return canCounter;
    }
    
    /**
     * Manually sets the possibility of a counter
     *
     * @param canCounter The value to set to
     */
    public static void setCanCounter(boolean canCounter)
    {
        Game.canCounter = canCounter;
    }
    
    /**
     * Increases the attack bar for every Monster on each Team. This is the same as calling {@link Monster#increaseAtkBar()} for every Monster in the
     * Game.
     */
    public void increaseAtkBar()
    {
        t1.increaseAtkBar();
        t2.increaseAtkBar();
    }
    
    /**
     * @return true if at least one Monster in the Game has a full attack bar, false otherwise.
     */
    public boolean hasFullAtkBar()
    {
        return t1.hasFullAtkBar() || t2.hasFullAtkBar();
    }
    
    /**
     * @return true if a Team has no alive Monsters remaining, false otherwise
     */
    public boolean endGame()
    {
        return t1.deadTeam() || t2.deadTeam();
    }
    
    /**
     * Finds the {@link Team} with a Monster with a full attack bar. If both have a Monster with a full attack bar chooses the one with a higher value. If
     * they
     * are the same, chooses a Team at random.
     *
     * @return the Team with the highest attack bar
     */
    public Team getTeamWithHighestAtkBar()
    {
        if (t1.getHighestFullAtkBar() > t2.getHighestFullAtkBar())
        {
            teamWithHighestAtkBar = t1;
            other = t2;
            return t1;
        }
        if (t2.getHighestFullAtkBar() > t1.getHighestFullAtkBar())
        {
            teamWithHighestAtkBar = t2;
            other = t1;
            return t2;
        }
        if (new Random().nextInt(101) <= 50)
        {
            teamWithHighestAtkBar = t1;
            other = t2;
            return t1;
        }
        teamWithHighestAtkBar = t2;
        other = t1;
        return t2;
    }
    
    /**
     * Finds the Team that still has Monsters remaining. If both Teams have Monsters returns an empty Team
     *
     * @return a blank Team or the Team with Monsters still remaining
     */
    public Team getWinningTeam()
    {
        if (!endGame())
        {
            return new Team("None", new ArrayList<>());
        }
        if (t1.deadTeam())
        {
            return t2;
        }
        return t1;
    }
    
    /**
     * Finds the team that has no Monsters remaining. Returns an empty Team if both Teams still have Monsters
     *
     * @return A blank Team or the Team with no Monsters left
     */
    public Team getLosingTeam()
    {
        if (!endGame())
        {
            return new Team("None", new ArrayList<>());
        }
        if (t1.deadTeam())
        {
            return t1;
        }
        return t2;
    }
    
    /**
     * Formats the Game into a readable String
     *
     * @return the formatted String
     */
    public String toString()
    {
        String s = "";
        s += "Team 1:\n";
        s += t1;
        s += "\n\nTeam 2:\n";
        s += t2;
        return s;
    }
    
    /**
     * Calculates how much damage each continuous damage Debuff does as a percent of health
     *
     * @return the percent of health to damage
     */
    public double continuousDmgAmount()
    {
        double returnAmount = 0.05;
        for (int i = 0; i < t1.size(); i++)
        {
            //@Passive (Sath)
            if ((t1.get(i) instanceof Sath && !t1.get(i).isDead() && !t1.get(i).containsDebuff(Debuff.OBLIVION)) ||
                    (t2.get(i) instanceof Sath && !t2.get(i).isDead() && !t2.get(i).containsDebuff(Debuff.OBLIVION)))
            {
                returnAmount *= 2.0;
                break;
            }
        }
        
        return returnAmount;
    }
    
    /**
     * Resets the Game. Same as creating a new Game with the same Teams.
     */
    public void reset()
    {
        t1.reset();
        t2.reset();
    }
    
    /**
     * Activates every Monster's passive ability that activates before a turn if they have one. Also checks each Monster for Provoke and removes the
     * debuff if the caster is dead
     *
     * @param next The Monster whose turn it is
     */
    public void activateBeforeTurnPassives(Monster next)
    {
        Team nextTeam = teamWithHighestAtkBar;
        for (int i = 0; i < nextTeam.size() + other.size(); i++)
        {
            Monster m = (i < nextTeam.size()) ? nextTeam.get(i) : other.get(i - nextTeam.size());
            m.beforeTurnProtocol(next, m.equals(next), other.contains(m), m.containsDebuff(Debuff.OBLIVION));
            if (m.containsDebuff(Debuff.PROVOKE))
            {
                if (m.getProvoke().getCaster().isDead())
                {
                    m.removeDebuff(Debuff.PROVOKE);
                }
            }
        }
    }
    
    /**
     * Applies buffs and debuffs that activate before the Monsters turn.
     *
     * @param next The Monster whose turn it is
     */
    public void applyStats(Monster next)
    {
        boolean dmgTaken = false;
        //Apply debuffs before turn
        for (Debuff debuff : next.getAppliedDebuffs())
        {
            switch (debuff.getDebuffNum())
            {
                //Bomb
                case Debuff.BOMB ->
                {
                    if (debuff.getNumTurns() == 1)
                    {
                        next.setCurrentHp((int) (next.getCurrentHp() - next.getMaxHp() * 0.4));
                        Main.printBombExplodeEffect(next);
                        if (next.getCurrentHp() == 0)
                        {
                            next.setDead(true);
                        }
                        Main.stunned = true;
                        dmgTaken = true;
                    }
                }
                //Sleep
                case Debuff.SLEEP ->
                {
                    Main.printSleepEffect();
                    Main.stunned = true;
                }
                //DOT
                case Debuff.CONTINUOUS_DMG ->
                {
                    next.setCurrentHp((int) (next.getCurrentHp() - next.getMaxHp() * (continuousDmgAmount())));
                    dmgTaken = true;
                    if (Monster.isPrint())
                    {
                        System.out.println("DOT Applied, you took " + (int) (next.getMaxHp() * (continuousDmgAmount())));
                    }
                }
                //Freeze
                case Debuff.FREEZE ->
                {
                    Main.printFreezeEffect();
                    Main.stunned = true;
                }
                //Stun
                case Debuff.STUN ->
                {
                    Main.printStunEffect();
                    Main.stunned = true;
                }
            }
        }
        
        if (dmgTaken)
        {
            next.removeDebuff(Debuff.SLEEP);
        }
        
        //Apply buffs before turn
        for (Buff buff : next.getAppliedBuffs())
        {
            if (buff.getBuffNum() == Buff.RECOVERY && !next.containsDebuff(Debuff.UNRECOVERABLE))
            {
                next.setCurrentHp(Math.min(next.getMaxHp(), (int) (next.getCurrentHp() + next.getMaxHp() * 0.15)));
                if (Monster.isPrint())
                {
                    System.out.println("Continuous healing applied, you healed " + (int) (next.getMaxHp() * 0.15) + " HP.");
                }
            }
        }
    }
    
    /**
     * Gets the ability number from the user
     *
     * @param next The Monster whose turn it is
     * @return the valid number the user selects
     */
    public static int getAbilityNum(Monster next)
    {
        int abilityNum;
        do
        {
            System.out.println("Type the ability number you want to use (e.g. 1,2...) or type \"stats\" to see stat descriptions");
            try
            {
                abilityNum = scan.nextInt();
                if (next.abilityIsPassive(abilityNum))
                {
                    System.out.println("Oops! Ability is passive! (You can not use this ability, it is automatically applied)\n");
                    abilityNum = -1;
                }
            }
            catch (InputMismatchException e)
            {
                String response = scan.nextLine();
                abilityNum = -1;
                if (response.equals("stats"))
                {
                    Stats.Stat.printStatDescriptions();
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                abilityNum = -1;
            }
        }
        while (!next.getViableAbilityNumbers().contains(abilityNum));
        return abilityNum;
    }
    
    /**
     * Gets the target the user wants to attack/heal
     *
     * @param next       The Monster whose turn it is.
     * @param abilityNum The ability number the user has chosen
     * @param threat     Whether a Monster has the Threat buff
     * @return the target Monster
     */
    public Monster getTarget(Monster next, int abilityNum, boolean threat)
    {
        //get target num/re-choose ability if wanted
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
            //Enemy does not have any Threat buffs and ability targets enemy
            else if (next.getAbility(abilityNum).targetsEnemy())
            {
                System.out.print(other.print(next.getElement(), 1) + "\n");
            }
            //Ability targets self
            else if (next.getAbility(abilityNum).targetsSelf())
            {
                System.out.println(teamWithHighestAtkBar.getSingleMonFromTeam(next, true));
                monster = next;
            }
            //Ability targets friendly Team
            else
            {
                System.out.println(teamWithHighestAtkBar.print(next.getElement(), 0) + "\n");
            }
            try
            {
                target = scan.nextInt();
            }
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
     * @return the team whose mons turn it is
     */
    public Team getNextMonsTeam()
    {
        return teamWithHighestAtkBar;
    }
    
    /**
     * @return the team who does not have their mon's turn
     */
    public Team getOtherTeam()
    {
        return other;
    }
    
    /**
     * Finds the Team that contains the given Monster
     *
     * @param monster The Monster to search for
     * @return the Team with the given Monster
     */
    public Team getTeamFromMon(Monster monster)
    {
        return (teamWithHighestAtkBar.contains(monster)) ? teamWithHighestAtkBar : other;
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
        //Apply nextTurn()
        //If something went wrong, does process again
        if (!next.nextTurn(targetMon, abilityNum))
        {
            System.out.println("Uh oh! Something in the attack went wrong! (Check your ability cooldown and make sure you're not targeting a dead " +
                    "monster!)");
        }
    }
}