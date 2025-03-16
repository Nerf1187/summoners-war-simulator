package Monsters.Dark;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Dark Ezio
 */
public class Evan extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    private final Attack_Ability passiveAttack = new Attack_Ability("", 2.4 * 1.2, 0, 1, "", 0, false, false, false);
    
    /**
     * Creates the Monster with the default rune set
     */
    public Evan()
    {
        this("Evan1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Evan(String runeFileName)
    {
        super("Evan" + count, DARK, 9_390, 538, 812, 98, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Commander Strike (1)", 3.8 * 1.3, 0, 1, "Attacks the enemy to inflict damage that ignores all damage reduction effects.", 0, false, true, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.DEC_DEF, 1, 0, Debuff.STUN, 1, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100, 70);
        abilities.add(new Attack_Ability("Steel Swordsmanship (2)", 3.2 * 1.2, 0, 2, "Attacks the enemy 2 times to decrease its Defense with a 100% chance and stuns for 1 turn with a 70% chance.", ability2Debuffs, ability2DebuffChances, 3, false,
                false, false));
        
        //@Passive:Creation
        abilities.add(new Passive("Meticulous Attack", "Attacks additionally with an attack that cannot be counterattacked when you attack the enemy on your turn. The additional attack always lands as a Critical Hit, and the damage is increased by" +
                                                       " 100% if you attack a Light attribute Monster."));
        
        abilities.add(new Leader_Skill(Stat.ACC, 0.48, ALL));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (passiveAttack.getDmgMultiplier() != 2.4 * 1.2)
        {
            throw new IllegalArgumentException("Dmg multiplier is incorrect.\nExpected: %f\nActual%f".formatted(2.4 * 1.2, passiveAttack.getDmgMultiplier()));
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (passiveCanActivate())
        {
            //Save crit rate for passive
            int critRate = getCritRate();
            setCritRate(9_999);
            if (target.getElement() == LIGHT)
            {
                passiveAttack.setDmgMultiplier(passiveAttack.getDmgMultiplier() * 2);
            }
            //Passive attack
            attack(target, passiveAttack);
            //Reset crit rat
            setCritRate(critRate);
            //Reset passive damage multiplier
            if (target.getElement() == LIGHT)
            {
                passiveAttack.setDmgMultiplier(passiveAttack.getDmgMultiplier() / 2);
            }
        }
        
        super.afterTurnProtocol(target, true);
        return true;
    }
}