package Effects.Debuffs;

import Util.Util.STRINGS;

public enum DebuffEffect
{
    NULL(-1),
    GLANCING_HIT_UP(0),
    DEC_ATK(1),
    DEC_DEF(2),
    DEC_ATK_SPD(3),
    BLOCK_BENEFICIAL_EFFECTS(4),
    BOMB(5),
    PROVOKE(6),
    SLEEP(7),
    CONTINUOUS_DMG(8),
    FREEZE(9),
    STUN(10),
    UNRECOVERABLE(11),
    SILENCE(12),
    BRAND(13),
    OBLIVION(14),
    DEC_ATK_BAR(15),
    REMOVE_BENEFICIAL_EFFECT(16),
    STRIP(17),
    SEAL(18),
    SHORTEN_BUFFS(19);
    
    private final int num;
    
    DebuffEffect(int num)
    {
        this.num = num;
    }
    
    public int getNum()
    {
        return num;
    }
    
    public String toString()
    {
        return STRINGS.toTitleCase(super.toString());
    }
    
    public static DebuffEffect numToDebuff(int num)
    {
        for (DebuffEffect debuff : values())
        {
            if (debuff.getNum() == num)
            {
                return debuff;
            }
        }
        return NULL;
    }
}