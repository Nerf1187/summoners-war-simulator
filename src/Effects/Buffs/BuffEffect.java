package Effects.Buffs;

import Util.Util.STRINGS;

public enum BuffEffect
{
    NULL(-1),
    ATK_UP(0),
    DEF_UP(1),
    CRIT_RATE_UP(2),
    CRIT_RESIST_UP(3),
    ATK_SPD_UP(4),
    RECOVERY(5),
    COUNTER(6),
    IMMUNITY(7),
    INVINCIBILITY(8),
    REFLECT(9),
    SHIELD(10),
    ENDURE(11),
    DEFEND(12),
    SOUL_PROTECTION(13),
    RUNE_SHIELD(14),
    THREAT(15),
    INCREASE_ATK_BAR(16),
    CLEANSE(17),
    BUFF_STEAL(18),
    VAMPIRE(19),
    REMOVE_DEBUFF(20),
    EXTEND_BUFF(21),
    SHORTEN_DEBUFF(22);
    
    private final int num;
    
    BuffEffect(int num)
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
    
    public static BuffEffect numToBuff(int num)
    {
        for (BuffEffect buff : values())
        {
            if (buff.getNum() == num)
            {
                return buff;
            }
        }
        return NULL;
    }
}