package Game;

import Errors.*;
import Monsters.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

import static Game.Main.scan;

/**
 * This class contains all the information for the game that is used by {@link Main}, {@link Auto_Play} and {@link Test_One_Team}.
 *
 * @author Anthony (Tony) Youssef
 */
public class Game
{
    private static boolean canCounter;
    private final Team t1, t2;
    
    //Team with the Monster with the highest attack bar | The other team
    private Team teamWithHighestAtkBar = new Team("", new ArrayList<>()), other = new Team("", new ArrayList<>());
    
    /**
     * Creates a new Game object
     *
     * @param t1 The first Team in the game
     * @param t2 The second Team in the game
     */
    public Game(Team t1, Team t2)
    {
        //Set the game for the Monsters
        Monster.setGame(this);
        
        //Check that the teams have the same number of Monsters
        if (t1.size() != t2.size())
        {
            throw new ConflictingArguments("Teams must have the same number of mons.");
        }
        
        //Set teams
        this.t1 = t1;
        this.t2 = t2;
    }
    
    /**
     * @return True if a counter is possible in this Game, false otherwise.
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
     * Increases the attack bar for every Monster on each Team. This is the same as calling {@link Monster#increaseAtkBar()} for every Monster in the Game
     */
    public void increaseAtkBar()
    {
        t1.increaseAtkBar();
        t2.increaseAtkBar();
    }
    
    /**
     * @return True if at least one Monster in the Game has a full attack bar, false otherwise.
     */
    public boolean hasFullAtkBar()
    {
        return t1.hasFullAtkBar() || t2.hasFullAtkBar();
    }
    
    /**
     * @return True if a Team has no living Monsters remaining, false otherwise
     */
    public boolean endGame()
    {
        return t1.deadTeam() || t2.deadTeam();
    }
    
    /**
     * Finds the Team with a Monster with a full attack bar. If both have a Monster with a full attack bar, chooses the one with a higher raw value. If they are the same, chooses a Team at random.
     *
     * @return The Team with the highest attack bar
     */
    public Team getTeamWithHighestAtkBar()
    {
        //Team 1 has a higher full attack bar
        if (t1.getHighestFullAtkBar() > t2.getHighestFullAtkBar())
        {
            teamWithHighestAtkBar = t1;
            other = t2;
            return t1;
        }
        //Team 2 has a higher full attack bar
        if (t2.getHighestFullAtkBar() > t1.getHighestFullAtkBar())
        {
            teamWithHighestAtkBar = t2;
            other = t1;
            return t2;
        }
        //Both teams have equal highest full attack bars
        //Randomly choose the team
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
     * Finds the Team that still has living Monsters remaining. If both Teams have living Monsters, returns an empty Team
     *
     * @return A blank Team or the Team with Monsters still remaining
     */
    public Team getWinningTeam()
    {
        //Return a blank team if the game has not ended
        if (!endGame())
        {
            return new Team("None", new ArrayList<>());
        }
        //Team 2 won
        if (t1.deadTeam())
        {
            return t2;
        }
        //Team 1 won
        return t1;
    }
    
    /**
     * Finds the team that has no living Monsters remaining. Returns an empty Team if both Teams still have living Monsters
     *
     * @return A blank Team or the Team with no Monsters left
     */
    public Team getLosingTeam()
    {
        //Return a blank team if the game has not ended
        if (!endGame())
        {
            return new Team("None", new ArrayList<>());
        }
        //Team 1 lost
        if (t1.deadTeam())
        {
            return t1;
        }
        //Team 2 lost
        return t2;
    }
    
    /**
     * Formats the Game into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        //Returns team 1 followed by team 2
        String s = "";
        s += "Team 1:\n";
        s += t1;
        s += "\n\nTeam 2:\n";
        s += t2;
        return s;
    }
    
    /**
     * Resets the Game. Same as creating a new Game with the same Teams.
     */
    public void reset()
    {
        if (t1.newInstances() && t2.newInstances())
        {
            return;
        }
        System.err.println("Error resetting game");
        System.exit(1);
    }
    
    /**
     * Activates every Monster's passive ability that activates before a turn if they have one. Also checks each Monster for Provoke and removes the
     * debuff if the caster is dead
     *
     * @param next The Monster whose turn it is
     */
    public void activateBeforeTurnPassives(Monster next)
    {
        //Activate passives for the acting team, then the other team
        for (int i = 0; i < teamWithHighestAtkBar.size() + other.size(); i++)
        {
            Monster m = (i < teamWithHighestAtkBar.size()) ? teamWithHighestAtkBar.get(i) : other.get(i - teamWithHighestAtkBar.size());
            //Do anything needed for the Monster before its turn
            m.beforeTurnProtocol(next, m.equals(next), other.contains(m), m.containsDebuff(Debuff.OBLIVION));
            //Remove Provoke if the caster is dead
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
        Monster stunner = null;
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
                        //Detonate bomb
                        next.setCurrentHp((int) (next.getCurrentHp() - next.getMaxHp() * 0.4));
                        Main.printBombExplodeEffect(next);
                        stunner = debuff.getCaster();
                        dmgTaken = true;
                    }
                }
                //Sleep
                case Debuff.SLEEP -> Main.printSleepEffect();
                //DOT
                case Debuff.CONTINUOUS_DMG ->
                {
                    next.applyContinuousDmg();
                    dmgTaken = true;
                }
                //Freeze
                case Debuff.FREEZE -> Main.printFreezeEffect();
                //Stun
                case Debuff.STUN -> Main.printStunEffect();
            }
        }
        
        //Check if the Monster is dead. Do this AFTER loop to not break program
        if (next.getCurrentHp() <= 0)
        {
            next.kill();
        }
        
        //Remove sleep if any damage was taken. Do this AFTER loop to not break program
        if (dmgTaken)
        {
            next.removeDebuff(Debuff.SLEEP);
        }
        
        //Try to stun Monster if a bomb exploded. Do this AFTER loop to not break program
        if (stunner != null)
        {
            next.addAppliedDebuff(Debuff.STUN, 100, 1, stunner);
        }
        
        //Apply buffs before turn
        for (Buff buff : next.getAppliedBuffs())
        {
            //Recovery
            if (buff.getBuffNum() == Buff.RECOVERY && !next.containsDebuff(Debuff.UNRECOVERABLE))
            {
                next.setCurrentHp(Math.min(next.getMaxHp(), (int) (next.getCurrentHp() + next.getMaxHp() * 0.15)));
                if (Monster.isPrint())
                {
                    System.out.printf("Continuous healing applied, you healed %,d HP.%n", (int) (next.getMaxHp() * 0.15));
                }
            }
        }
    }
    
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
            System.out.println("Type the ability number you want to use (e.g. 1,2...) or type \"stats\" to see stat descriptions");
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
                if (response.equals("stats"))
                {
                    Stats.Stat.printStatDescriptions();
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
    public Monster getTarget(Monster next, int abilityNum, boolean threat)
    {
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
     * Gets the acting team
     *
     * @return The acting team
     */
    public Team getNextMonsTeam()
    {
        return teamWithHighestAtkBar;
    }
    
    /**
     * @return The non-acting team
     */
    public Team getOtherTeam()
    {
        return other;
    }
    
    /**
     * Finds the Team that contains the given Monster
     *
     * @param monster The Monster to search for
     * @return The Team with the given Monster
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
        //Try to perform the Monster's turn
        //If something went wrong, does process again
        if (!next.nextTurn(targetMon, abilityNum))
        {
            System.out.println("Uh oh! Something in the turn went wrong! (Check your ability number and cooldown cooldown and make sure you're not targeting a dead monster!)");
        }
    }
}