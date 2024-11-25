package Monsters.Water;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import java.util.*;

/**
 * Water Howl (2A)
 */
public class Lulu_And_Friends extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Lulu_And_Friends()
    {
        super("Lulu and Friends" + count, WATER, 10_050, 714, 648, 99, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Lulu_And_Friends1.csv", this));
        setAbilities();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Lulu_And_Friends(String runeFileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Attack! (1)", 4.4 * 1.1, 0, 1, "Attacks the enemy target and recovers the HP of the " +
                                                                         "ally with the lowest HP ratio by 15%.", 0, false, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.CLEANSE, 0, Buff.IMMUNITY, 1);
        ArrayList<Integer> ability2BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Heal! (2)", 0.25 * 1.25, 1, "Removes all harmful effects on the target ally and grants Immunity" +
                                                                    " for 1 turn. In addition, recovers both your HP and the target's HP by 25% each.", ability2Buffs, ability2BuffChances, 3, false));
        
        abilities.add(new Heal_Ability("Remove! Heal! (3)", 0.25 * 1.2, 1, "Removes all harmful effects of all allies and recovers the" +
                                                                           " HP of all allies by 25% each. Grants Immunity on the allies who had no harmful effects for 2 turns.", 4, true));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        Team team = game.getNextMonsTeam();
        switch (abilityNum)
        {
            //Heal the ally with the lowest HP ratio
            case 1 -> heal(team.getMonsterWithLowestHpRatio(), new Heal_Ability("", 1.15 * 0.15, 1, "", 0, false));
            //Heal self
            case 2 -> heal(this, new Heal_Ability("", 0.25 * 1.25, 1, "", 0, false));
            //Apply immunity if the target has no debuffs
            case 3 -> applyToTeam(team, m -> {
                if (m.cleanse() == 0)
                {
                    m.addAppliedBuff(Buff.IMMUNITY, 2, this);
                }
            });
        }
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}