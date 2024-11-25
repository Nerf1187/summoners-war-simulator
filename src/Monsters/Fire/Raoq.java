package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * Fire Inugami (2A)
 */
public class Raoq extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Raoq()
    {
        super("Raoq" + count, FIRE, 9_720, 582, 801, 108, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Raoq1.csv", this));
        setAbilities();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Raoq(String runeFileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.DEC_DEF, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(80);
        abilities.add(new Attack_Ability("Scratch (1)", 4.5 * 1.2, 0, 1, "Attacks with giant claws to weaken the enemy's defense for 2 turns with a 70% " +
                                                                         "chance and attacks consecutively with a 30% chance.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        abilities.add(new Attack_Ability("Team Up (2)", 4.5 * 1.2, 0, 1, "Teams up with two other allies to attack an enemy.", 3, false, false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Annihilate", "If you kill the enemy, you will get an extra turn instantly and your skill cooldown time will decrease by 1 turn."));
        
        super.setAbilities(abilities);
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
                if (tempTeam.numOfLivingMons() <= 1)
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
            while ((attackingMon1 instanceof Raoq && tempTeam.numOfLivingMons() > 1) || attackingMon1.isStunned());
            
            //Get random second Monster and make sure it is not the same as the first and not Raoq
            Monster attackingMon2 = null;
            tempMonsters = new ArrayList<>(friendlyTeam.getMonsters());
            tempTeam = new Team("Temp", tempMonsters);
            do
            {
                if (attackingMon1 == null || tempTeam.numOfLivingMons() <= 2)
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
            while ((attackingMon2 instanceof Raoq && tempTeam.numOfLivingMons() > 2) || attackingMon2.equals(attackingMon1) || attackingMon2.isStunned());
            
            //Have the two random Monsters attack the target if they exist
            boolean canCounter = Game.canCounter();
            if (attackingMon1 != null)
            {
                Game.setCanCounter(false);
                Monster save = attackingMon1.copy();
                attackingMon1.attack(target, attackingMon1.getAbility(1));
                attackingMon1.paste(save);
            }
            if (attackingMon2 != null)
            {
                Game.setCanCounter(false);
                Monster save = attackingMon2.copy();
                attackingMon2.attack(target, attackingMon2.getAbility(1));
                attackingMon2.paste(save);
            }
            Game.setCanCounter(canCounter);
        }
        
        //Gain an extra turn if the Monster is dead and reduce the cooldown for ability 2
        if (target.isDead())
        {
            if (isPrint())
            {
                System.out.printf("%sExtra turn!%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
            }
            setAtkBar(2_000);
            abilities.get(2).decCooldown();
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
}