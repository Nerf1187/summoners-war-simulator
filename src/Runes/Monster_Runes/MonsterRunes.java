package Runes.Monster_Runes;

import Monsters.*;
import Runes.*;
import java.io.*;
import java.util.*;

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
    
    /**
     * Loads a rune set from memory
     *
     * @param f        The file to load from
     * @param setToMon The Monster to set the Runes to
     * @return The ArrayList of Runes
     */
    public static ArrayList<Rune> getRunesFromFile(File f, Monster setToMon)
    {
        try
        {
            Scanner read = new Scanner(f);
            ArrayList<Rune> runes = new ArrayList<>();
            int place = 1;
            
            //Read each line
            while (read.hasNextLine())
            {
                String[] runeInfo = read.nextLine().split(",");
                //Get rune info
                int type = Integer.parseInt(runeInfo[0]);
                int mainAttNum = Integer.parseInt(runeInfo[1]);
                int mainAttAmount = Integer.parseInt(runeInfo[2]);
                MainAttribute mainAttribute = new MainAttribute(mainAttNum, mainAttAmount);
                ArrayList<SubAttribute> subs = new ArrayList<>();
                //Get each sub-attribute
                for (int i = 3; i < runeInfo.length; i += 2)
                {
                    int subNum = Integer.parseInt(runeInfo[i]);
                    int subAmount = Integer.parseInt(runeInfo[i + 1]);
                    subs.add(new SubAttribute(subNum, subAmount));
                }
                runes.add(new Rune(type, mainAttribute, place, subs, setToMon));
                place++;
            }
            read.close();
            
            return runes;
        }
        catch (FileNotFoundException e)
        {
            System.err.printf("Rune file not found for %s%n", setToMon.getName(true, false));
            return null;
        }
    }
    
    /**
     * Loads a rune set from memory.
     *
     * @param fileName The name of the file to load from. Should contain only the name, not the path
     * @param setToMon The Monster to set the Runes to
     * @return The ArrayList of Runes
     */
    public static ArrayList<Rune> getRunesFromFile(String fileName, Monster setToMon)
    {
        //Open the file and read the contents
        return getRunesFromFile(new File(path + "/" + fileName), setToMon);
    }
}