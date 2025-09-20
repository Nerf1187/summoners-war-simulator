package Effects;

import Util.Util.STRINGS;

public enum OtherEffect
{
    NULL(-1),
    BERSERK(0),
    TOTEM(1),
    MAGIC_SPHERE(2),
    MACARON_SHIELD(3),
    THUNDERER(4),
    RIDER(5);
    
    private final int num;
    
    OtherEffect(int num)
    {
        this.num = num;
    }
    
    public String toString()
    {
        return STRINGS.toTitleCase(super.toString());
    }
}