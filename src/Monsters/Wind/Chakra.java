package Monsters.Wind;

import Abilities.*;
import Effects.Debuffs.*;
import Effects.*;
import Game.*;
import Monsters.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Indra
 */
public class Chakra extends Monster
{
    private static int count = 1;
    
    private boolean thundererActive = false;
    private final int normalSpd;
    private Ability ability1OtherHits;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Chakra()
    {
        this("Chakra1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Chakra(String runeFileName)
    {
        super("Chakra" + count, Element.WIND, 10_215, 593, 867, 106, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        normalSpd = getSpd();
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = new ArrayList<>();
        ability1Debuffs.add(new DecAtkBar(30));
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(65);
        Ability a1 = new Attack_Ability("Thunder Shot (1)", 3.6 * 1.2, 0, 1, "Attacks the enemy and decreases its Attack Bar by 30% with a 65% chance. When used during the Thunderer state, you attack additionally with lower damage to enemies of " +
                                                                              "the same attribute as the target.", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.DEC_ATK.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(80);
        Ability a2 = new Attack_Ability("Wild Bolt (2)", 4.4 * 1.25, 0, 1, "Attacks all enemies and decreases their Attack Power for 2 turns with a 80% chance. When used during the Thunderer state, leaves a Branding effect for 2 turns with a 70% " +
                                                                            "chance.", ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        Ability a3 = new Ability("The Wind Thunderer (3)", 0, 0, 1, "Falls under the Thunderer state that enhances your skills for 3 turns and instantly gains another turn. While under the Thunderer state, your Attack Speed increases by 100%. " +
                                                                     "Once the Thunderer state ends, you become stunned for 1 turn. In addition, decreases the skill cooldown of [The Wind Thunderer] by 2 turns each when you are not under the Thunderer state.", 4, false, false, false, true, false, false);
        
        Ability a4 = new Passive("Might · Hurricane", "Deals additional damage that's proportionate to your Attack Speed when you attack the enemy and removes 1 beneficial effect from the enemy with a slower Attack Speed than yours. If the damage" +
                                                       " dealt on your turn during the Thunderer state is 15% or above of the target's MAX HP, increases the duration of the Thunderer state for 1 turn.");
        
        super.setAbilities(a1, a2, a3, a4);
        
        //Thunderer secondary hits
        ability1OtherHits = new Attack_Ability("", 0.178, 0, 1, "", ability1Debuffs, ability1DebuffChances, 0, false, false, false);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //@Passive
        //Increase damage based on speed
        if (passiveCanActivate())
        {
            abilities.getFirst().setDmgMultiplier(1.2 * (((thundererActive) ? 2.23 : 3.6) + (getSpd() * 47.0) / getAtk()));
            abilities.get(1).setDmgMultiplier(1.25 * (((thundererActive) ? 2.62 : 4.4) + (getSpd() * 47.0) / getAtk()));
        }
        else //Normal damage multiplier
        {
            abilities.getFirst().setDmgMultiplier(((thundererActive) ? 2.23 : 3.6) * 1.2);
            abilities.get(1).setDmgMultiplier(((thundererActive) ? 2.62 : 4.4) * 1.25);
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        //List to save targets
        ArrayList<Monster> targets = new ArrayList<>();
        if (thundererActive)
        {
            switch (abilityNum)
            {
                //Attack enemies of the same element as the target
                case 1 -> applyToTeam(game.getOtherTeam(), m -> {
                    if (m.getElement() == target.getElement() && m != target)
                    {
                        attack(m, ability1OtherHits);
                        targets.add(m);
                    }
                });
                case 2 -> target.addAppliedDebuff(DebuffEffect.BRAND, 70, 2, this);
            }
            //Increase time remaining on Thunderer if the damage dealt was greater than 15% of the target's health
            if (!this.isDead())
            {
                applyToTeam(game.getOtherTeam(), m -> {
                    if (m.getDmgTakenThisTurn() >= m.getMaxHp() * 0.15)
                    {
                        this.getThunderer().setNumTurns(this.getThunderer().getNumTurns() + 1);
                    }
                });
            }
        }
        
        //Add Thunderer effect
        if (abilityNum == 3)
        {
            //Remove Thunderer if it is already applied
            if (thundererActive)
            {
                this.removeThunderer(false);
            }
            //Re-add the effect
            Effect t = new Effect(4);
            t.setEffect(OtherEffect.THUNDERER);
            this.addOtherEffect(t);
            this.setSpd(normalSpd * 2);
            thundererActive = true;
            //Gain another turn
            this.setAtkBar(999_999);
        }
        
        //Remove a debuff if the target's speed is lower than Chakra's
        applyToTeam(game.getOtherTeam(), m -> {
            if (m.getSpd() < this.getSpd())
            {
                if (resistanceCheck(m))
                {
                    target.removeRandomBuff();
                }
                else if (isPrint())
                {
                    System.out.println("Resisted!");
                }
            }
        });
        
        //Make sure the Thunderer effect still has turns remaining and remove the effect if not
        if (thundererActive)
        {
            this.decreaseThundererTurns();
        }
        else //Decrease ability cooldown if not in the Thunderer state
        {
            abilities.get(2).decCooldown();
            abilities.get(2).decCooldown();
        }
        
        super.afterTurnProtocol((abilityNum == 2) ? game.getOtherTeam() : (targets.isEmpty()) ? target : new Team("", targets), abilityNum != 4);
        return true;
    }
    
    /**
     * @return The Thunderer effect if it exists, null otherwise
     */
    private Effect getThunderer()
    {
        if (thundererActive)
        {
            for (Effect effect : this.getOtherEffects())
            {
                if (effect.getEffect() == OtherEffect.THUNDERER)
                {
                    return effect;
                }
            }
        }
        System.out.println("hi");
        return null;
    }
    
    /**
     * Removes the Thunderer effect and resets the stats if the number of turns remaining is less than or equal to 0. Does nothing otherwise
     */
    private void removeThunderer(boolean stun)
    {
        if (!thundererActive)
        {
            return;
        }
        Effect t = this.getThunderer();
        if (stun)
        {
            this.addGuaranteedAppliedDebuff(DebuffEffect.STUN, 2, this);
        }
        this.removeOtherEffect(t);
        this.setSpd(normalSpd);
        thundererActive = false;
    }
    
    private void decreaseThundererTurns()
    {
        Effect t = this.getThunderer();
        if (t != null)
        {
            t.decreaseTurn();
            if (t.getNumTurns() <= 0)
            {
                this.removeThunderer(true);
            }
        }
    }
    
    public void reset()
    {
        //Remove the Thunderer effect if it is applied
        this.removeThunderer(false);
        super.reset();
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead())
        {
            this.removeThunderer(false);
        }
    }
    
    public Monster copy()
    {
        Chakra save = (Chakra) super.copy();
        save.thundererActive = this.thundererActive;
        return save;
    }
    
    public void paste(Monster save)
    {
        if (!(save instanceof Chakra))
        {
            return;
        }
        
        super.paste(save);
        this.thundererActive = ((Chakra) save).thundererActive;
    }
}
