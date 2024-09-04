package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

public class Kumar extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Kumar()
    {
        super("Kumar" + count, FIRE, 13_005, 681, 593, 101, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Kumar1.csv", this));
        setAbilities();
        count++;
    }
    
    public Kumar(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.REMOVE_BENEFICIAL_EFFECT, 0, 1, Debuff.OBLIVION, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(75, 50);
        abilities.add(new Attack_Ability("Crushing Blow (1)", 1.3 * (1.1 + (0.18 * getMaxHp()) / getAtk()), 0, 1, "With an attack that always lands as a Critical Hit, removes 1 beneficial effect from the enemy with a 75% chance and grants Oblivion" +
                " for 2 turns with a 50% chance. The damage increases according to your MAX HP.", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Buff> ability2Buffs = abilityBuffs(Buff.CLEANSE, 0);
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        abilities.add(new Heal_Ability("Meditate (2)", 0.3, 1, "Removes harmful effects granted on yourself and the ally target you selected, and recovers the HP of yourself and the ally by 30% of your MAX HP through meditation.", ability2Buffs,
                ability2BuffChances, 3, false));
        
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(Debuff.SILENCE, 2, 0);
        ArrayList<Integer> ability3DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Trick of Fire (3)", 1.2 * ((0.29 * getMaxHp()) / getAtk()), 0, 1, "Attacks all enemies with a spell that summons the power of fire to Silence them for 2 turns with a 75% chance and removes the harmful " +
                "effects granted on all allies. The damage is proportionate to your MAX HP.", ability3Debuffs, ability3DebuffChances, 4, false, false, true));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.33, Monster.ALL));
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = getCritRate();
        if (abilityNum == 1)
        {
            setCritRate(999_999);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            setCritRate(critRate);
            return false;
        }
        
        switch (abilityNum)
        {
            case 2:
            {
                heal(this, abilities.get(1));
            }
            case 3:
            {
                applyToTeam(game.getNextMonsTeam(), Monster::cleanse);
            }
        }
        setCritRate(critRate);
        super.afterTurnProtocol((abilityNum == 3) ? game.getOtherTeam() : target, abilityNum != 2);
        return true;
    }
}