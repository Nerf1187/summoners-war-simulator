package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Fire Beast Monk
 */
public class Kumar extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Kumar()
    {
        this("Kumar1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Kumar(String runeFileName)
    {
        super("Kumar" + count, Element.FIRE, 13_005, 681, 593, 101, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.REMOVE_BENEFICIAL_EFFECT.getNum(), 0, 1, DebuffEffect.OBLIVION.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(75, 50);
        Ability a1 = new Attack_Ability("Crushing Blow (1)", 1.3 * (1.1 + (0.18 * getMaxHp()) / getAtk()), 0, 1, "With an attack that always lands as a Critical Hit, removes 1 beneficial effect from the enemy with a 75% chance and grants Oblivion" +
                                                                                                                  " for 2 turns with a 50% chance. The damage increases according to your MAX HP.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Heal_Ability("Meditate (2)", 0.3, 1, "Removes harmful effects granted on yourself and the ally target you selected, and recovers the HP of yourself and the ally by 30% of your MAX HP through meditation." , 3, false);
        
        ArrayList<Debuff> ability3Debuffs = abilityDebuffs(DebuffEffect.SILENCE.getNum(), 2, 0);
        ArrayList<Integer> ability3DebuffChances = MONSTERS.abilityChances(100);
        Ability a3 = new Attack_Ability("Trick of Fire (3)", 1.2 * ((0.29 * getMaxHp()) / getAtk()), 0, 1, "Attacks all enemies with a spell that summons the power of fire to Silence them for 2 turns with a 75% chance and removes the harmful " +
                                                                                                            "effects granted on all allies. The damage is proportionate to your MAX HP.", ability3Debuffs, ability3DebuffChances, 4, false, false, true);
        Ability a4 = new Leader_Skill(RuneAttribute.HP, 0.33, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        int critRate = getCritRate();
        //Force ability 1 to crit
        if (abilityNum == 1)
        {
            setCritRate(999_999);
        }
        else if (abilityNum == 2)
        {
            if (!this.isDead() && targetIsValid(target, false) && this.abilityIsValid(2))
            {
                target.cleanse();
                this.cleanse();
            }
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            setCritRate(critRate);
            return false;
        }
        
        switch (abilityNum)
        {
            //Heal self as well
            case 2 -> heal(this, abilities.get(1));
            //Cleanse all allied Monsters
            case 3 ->
            {
                applyToTeam( game.getNextMonsTeam(), Monster::cleanse);
            }
        }
        
        //Reset crit rate
        setCritRate(critRate);
        super.afterTurnProtocol((abilityNum == 3) ? game.getOtherTeam() : target, abilityNum != 2);
        return true;
    }
}
