
package Monsters.Wind;

import Abilities.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Effects.*;
import Game.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

public class Savannah extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Savannah()
    {
        this("Savannah1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Savannah(String runeFileName)
    {
        super("Savannah" + count, Element.WIND, 10_215, 593, 517, 107, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_ATK_SPD.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Spear of Protector (1)", 2.0 * 1.20, 0, 1, "Attacks the enemy to decrease the Attack Speed for 2 turns with a 50% chance. The faster your Attack Speed compared to the enemy's, the greater damage you can inflict.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        Ability a2 = new Attack_Ability("Critical Link (2)", 1.20 * ((getSpd() + 120.0) / 100), 0, 2, "The faster your Attack Speed, the greater the damage becomes. Attacks the enemy with the beast 2 times. The beast's attack " +
                                                                                                      "changes all beneficial effects granted on the target to Continuous Damage for 1 turn, and the rider's attack inflicts damage that increases by 15% for each harmful effect granted on the target.", 3, false, false, false);
        
        a2.addBeneficialEffectRemoversOverride(DebuffEffect.STRIP);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_SPD_UP.getNum(), 2);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100);
        Ability a3 = new Attack_Ability("Gigantic Roar (3)", 4.5 * 1.15, 0, 1, "The rider increases the Attack Speed of all allies for 2 turns, and the beast attacks all enemies. Weakens the enemy's Defense for 2 turns if the enemy's Defense is lower than your Attack Power, and decreases the enemy's Attack Bar by 75% if the enemy's Attack Speed is slower than yours.", ability3Buffs, ability3BuffChances, 5, false, false, true, 0);
        
        //@Passive:Creation
        Ability a4 = new Passive("Rider", "Dismounts the beast to battle when you're inflicted with damage that may cause you to die.");
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Increase ability 1 damage multiplier according to the relative speed difference
        if (abilityNum == 1)
        {
            double relSpd = 1.0 * this.getSpd() / target.getSpd();
            
            abilities.getFirst().setDmgMultiplier(1.2 * (2.0 * relSpd));
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        super.afterTurnProtocol((abilityNum == 3) ? game.getOtherTeam() : target, true);
        return true;
    }
    
    public void attackerBeforeHitProtocol(Monster target, int abilityNum, int count)
    {
        super.attackerBeforeHitProtocol(target, abilityNum, count);
        
        abilities.get(1).setDmgMultiplier((((this.containsOtherEffect(OtherEffect.RIDER) || count == 2) && !target.getAppliedDebuffs().isEmpty()) ? (1.15 * target.getAppliedDebuffs().size()) : 1) * 1.20 * ((getSpd() + 120.0) / 100));
    }
    
    public void attackerAfterHitProtocol(Monster target, int abilityNum, int count)
    {
        super.attackerAfterHitProtocol(target, abilityNum, count);
        
        if (abilityNum == 2 && count == 1 && !this.containsOtherEffect(OtherEffect.RIDER))
        {
            int num = target.strip(this);
            for (int i = 0; i < num; i++)
            {
                target.addGuaranteedAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 1, this);
            }
            
            abilities.get(1).setDmgMultiplier(Math.max(1.15 * target.getAppliedDebuffs().size(), 1) * 1.20 * ((getSpd() + 120.0) / 100.0));
        }
        
        if (abilityNum == 3)
        {
            if (target.getDef() < this.getAtk())
            {
                target.addAppliedDebuff(DebuffEffect.DEC_DEF, 100, 2, this);
            }
            
            if (target.getSpd() < this.getSpd())
            {
                target.addAppliedDebuff(new DecAtkBar(75), 100, this);
            }
        }
    }
    
    public void kill()
    {
        super.kill();
        
        if (this.isDead() && !this.containsOtherEffect(OtherEffect.RIDER))
        {
            //Symbolize the rider passive
            this.addOtherEffect(new Effect(OtherEffect.RIDER));
            
            //Re-set stats for the rider
            this.setMaxHp(this.getMaxHp() / 2.0);
            this.setHpRatio(100);
            this.setSpd(this.getSpd() * 0.85);
            
            //Alter abilities
            this.abilities.get(1).setDescription("The faster your Attack Speed, the greater the damage becomes. Attacks the enemy to inflict damage that increases by 15% for each harmful effect granted on the target.");
            this.abilities.get(1).setNumOfActivations(1);
            this.abilities.remove(2);
            this.abilities.getLast().setDescription("MAX HP will be decreased by 50% and Attack Speed by 15% when you dismount the beast. The inflicted damage on the enemy will be increased by 100%.");
            
            //Re-set death state and name
            this.setDead(false);
            this.setName(this.getName(false, true).replaceAll("Savannah", "Savannah [Rider]"));
            
            //Print the passive activation
            if (isPrint())
            {
                //@Passive:Activation
                System.out.printf("%s Rider (Passive)\n\n", this.getName(true, true));
            }
        }
    }
    
    public double dmgIncProtocol(double num)
    {
        return this.containsOtherEffect(OtherEffect.RIDER) ? num * 2 : num;
    }
    
    public void reset()
    {
        this.setAbilities();
        
        if (this.removeAllOf(OtherEffect.RIDER) > 0)
        {
            this.setMaxHp(this.getMaxHp() * 2.0);
            this.setSpd(this.getSpd() / 0.85);
        }
        
        super.reset();
    }
    
    public String getName(boolean withElement, boolean withNumber)
    {
        return (withNumber) ? super.getName(withElement, true) : this.getElement() + "Savannah" + ConsoleColor.RESET;
    }
}