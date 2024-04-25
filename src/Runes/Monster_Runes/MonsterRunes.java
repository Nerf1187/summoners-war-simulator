package Runes.Monster_Runes;

import Monsters.*;
import Runes.*;
import java.io.*;
import java.util.*;

/**
 * @author Anthony (Tony) Youssef
 * The parent class for all Monster rune classes. This class should never be directly called or initialized, only inherited
 */
public class MonsterRunes
{
    /**
     * Loads a rune set from memory
     *
     * @param f        The file to load from
     * @param setToMon The Monster to set the Runes to
     * @return The ArrayList of Runes
     */
    public static ArrayList<Rune> getRunesFromFile(File f, Monster setToMon)
    {
        ArrayList<Rune> runes = new ArrayList<>();
        try
        {
            Scanner read = new Scanner(f);
            int place = 1;
            while (read.hasNextLine())
            {
                String[] runeInfo = read.nextLine().split(",");
                int type = Integer.parseInt(runeInfo[0]);
                int mainAttNum = Integer.parseInt(runeInfo[1]);
                int mainAttAmount = Integer.parseInt(runeInfo[2]);
                MainAttribute mainAttribute = new MainAttribute(mainAttNum, mainAttAmount);
                ArrayList<SubAttribute> subs = new ArrayList<>();
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
        }
        catch (FileNotFoundException e)
        {
            System.err.println("File not found for " + setToMon.getName(true, false));
            System.exit(1);
        }
        
        return runes;
    }
    
    /**
     * Loads a rune set from memory. This is the same as calling getRunesFromFile(new File("src/Runes/Monster_runes/" + fileName);
     *
     * @param fileName The name of the file to load from. Should contain only the name, not the path
     * @param setToMon The Monster to set the Runes to
     * @return The ArrayList of Runes
     */
    public static ArrayList<Rune> getRunesFromFile(String fileName, Monster setToMon)
    {
        File f = new File("src/Runes/Monster_Runes/" + fileName);
        return getRunesFromFile(f, setToMon);
    }
}
