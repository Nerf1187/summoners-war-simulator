package Runes.Monster_Runes;

/**
 * An interface to read rune files and convert them into runes
 *
 * @author Anthony (Tony) Youssef
 */
public class MonsterRunes
{
    public static final String path = MonsterRunes.class.getResource("MonsterRunes.class").getPath()
                                              .substring(0, MonsterRunes.class.getResource("MonsterRunes.class").getPath().indexOf("Summoners%20War%20Battle%20Simulator") + 36)
                                              .replaceAll("%20", " ")
                                              .replaceAll("file:", "") + "/src/Runes/Monster_Runes";
}