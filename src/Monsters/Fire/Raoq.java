package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

/**
 * Fire Inugami (2A)
 */
public class Raoq extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Raoq()
    {
        this("Raoq1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Raoq(String runeFileName)
    {
        super("Raoq" + count, Element.FIRE, 9_720, 582, 801, 108, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_DEF.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(80);
        Ability a1 = new Attack_Ability("Scratch (1)", 4.5 * 1.2, 0, 1, "Attacks with giant claws to weaken the enemy's defense for 2 turns with a 70% " +
                                                                         "chance and attacks consecutively with a 30% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Team Up (2)", 4.5 * 1.2, 0, 1, "Teams up with two other allies to attack an enemy.", 3, false, false, false);
        
        //@Passive:Creation
        Ability a3 = new Passive("Annihilate", "If you kill the enemy, you will get an extra turn instantly and your skill cooldown time will decrease by 1 turn.");
        
        super.setAbilities(a1, a2, a3);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        
        if (!b)
        {
            return false;
        }
        
        // Repeat ability 1 with a 30% chance until it fails
        if (abilityNum == 1)
        {
            do
            {
                attack(target, abilities.getFirst());
            }
            while ((new Random().nextInt(101) < 30) && !target.isDead());
        }
        
        //Select random Monsters to attack with
        if (abilityNum == 2)
        {
            final Team friendlyTeam = game.getNextMonsTeam();
            Monster attackingMon1 = null;
            ArrayList<Monster> tempMonsters = new ArrayList<>(friendlyTeam.getMonsters());
            Team tempTeam = new Team("Temp", tempMonsters);
            
            //Get random first Monster and make sure it is not Raoq
            do
            {
                if (tempTeam.numOfLivingMons() < 1)
                {
                    break;
                }
                attackingMon1 = tempTeam.getRandomMon();
                if (attackingMon1 == null)
                {
                    break;
                }
                tempTeam.getMonsters().remove(attackingMon1);
            }
            while ((attackingMon1 instanceof Raoq && tempTeam.numOfLivingMons() >= 1) || attackingMon1.isStunned());
            
            //Get random second Monster and make sure it is not the same as the first and not Raoq
            Monster attackingMon2 = null;
            tempMonsters = new ArrayList<>(friendlyTeam.getMonsters());
            tempTeam = new Team("Temp", tempMonsters);
            do
            {
                if (attackingMon1 == null || tempTeam.numOfLivingMons() < 2)
                {
                    break;
                }
                attackingMon2 = tempTeam.getRandomMon();
                if (attackingMon2 == null)
                {
                    break;
                }
                tempTeam.getMonsters().remove(attackingMon2);
            }
            while ((attackingMon2 instanceof Raoq && tempTeam.numOfLivingMons() >= 2) || attackingMon2.equals(attackingMon1) || attackingMon2.isStunned());
            
            //Have the two random Monsters attack the target if they exist
            boolean canCounter = Game.canCounter();
            if (attackingMon1 != null)
            {
                Game.setCanCounter(false);
                Monster save = attackingMon1.copy();
                double tempAtkBar = save.getAtkBar();
                save.nextTurn(target, 1);
                attackingMon1.setAtkBar(tempAtkBar + save.getAtkBar());
            }
            if (attackingMon2 != null)
            {
                Game.setCanCounter(false);
                Monster save = attackingMon2.copy();
                double tempAtkBar = save.getAtkBar();
                save.nextTurn(target, 1);
                attackingMon2.setAtkBar(tempAtkBar + save.getAtkBar());
            }
            Game.setCanCounter(canCounter);
        }
        
        //Gain an extra turn if the Monster is dead and reduce the cooldown for ability 2
        if (target.isDead())
        {
            if (isPrint())
            {
                printfWithColor("Extra turn!", ConsoleColor.GREEN);
            }
            setAtkBar(2_000);
            abilities.get(2).decCooldown();
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
}
