package Game;

import Monsters.*;
import java.util.*;

import static Game.Auto_Play.generateCombinations;
import static Game.Main.getRuneSetNum;
import static Game.Main.printMonsToPick;

/**
 * This class is used to test one Team against all others
 *
 * @author Anthony (Tony) Youssef
 */
public class Test_One_Team
{
    private static final Scanner scan = new Scanner(System.in);
    private static int mainTeamWins = 0, numOfBattles = 0;
    
    /**
     * Runs this program
     */
    
    public static void main(String[] args)
    {
        Monster.setPrint(false);
        Team mainTeam = setTeam();
        ArrayList<Monster> allMons = new ArrayList<>();
        for (String name : Monster.monsterNamesDatabase.keySet())
        {
            String element = Monster.monsterNamesDatabase.get(name);
            name = name.replaceAll(" ", "_");
            try
            {
                Class<?> c = Class.forName("Monsters." + element + "." + name);
                allMons.add((Monster) c.getDeclaredConstructor().newInstance());
            }
            catch (Throwable e)
            {
                throw new RuntimeException(e);
            }
        }
        ArrayList<ArrayList<Monster>> allTeams = generateCombinations(allMons, 4);
        for (ArrayList<Monster> enemyMons : allTeams)
        {
            Team enemyTeam = new Team("Team 2", enemyMons);
            mainTeam.reset();
            Main.setRuneEffectsAndNames(mainTeam, enemyTeam);
            Team winningTeam = Auto_Play.battle(mainTeam, enemyTeam, 0);
            numOfBattles++;
            if (winningTeam != null && winningTeam == mainTeam)
            {
                mainTeamWins++;
            }
        }
        
        System.out.println("Number of wins: " + mainTeamWins);
        System.out.println("Number of battles: " + numOfBattles);
    }
    
    /**
     * Sets the Team to test.
     *
     * @return The main Team to test
     */
    private static Team setTeam()
    {
        ArrayList<Monster> monstersPicked = new ArrayList<>();
        String inputMon;
        while (monstersPicked.size() < 4)
        {
            do
            {
                printMonsToPick(monstersPicked);
                System.out.print("\nCurrent team: ");
                for (Monster mon : monstersPicked)
                {
                    System.out.print(mon.getName(true, false) + ", ");
                }
                System.out.println("\nChoose your monster");
                inputMon = scan.nextLine();
            }
            while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, monstersPicked));
            //Get class
            int runeSetNum = getRuneSetNum();
            
            try
            {
                inputMon = inputMon.replaceAll(" ", "_");
                inputMon = Monster.toProperName(inputMon);
                String temp = inputMon.replaceAll("_", " ");
                String element = Monster.monsterNamesDatabase.get(temp);
                String name = "Monsters." + element + "." + inputMon;
                Class<?> c = Class.forName(name);
                Monster monToAdd;
                if (runeSetNum == -1)
                {
                    monToAdd = (Monster) c.getDeclaredConstructor().newInstance();
                }
                else
                {
                    try
                    {
                        monToAdd = (Monster) c.getDeclaredConstructor(String.class).newInstance(inputMon + runeSetNum + ".csv");
                    }
                    catch (NoSuchMethodException e)
                    {
                        monToAdd = (Monster) c.getDeclaredConstructor(Class.class).newInstance(Class.forName(String.format("Runes.Monster_Runes.%sRunes%d",
                                name.substring(10 + element.length()), runeSetNum)));
                    }
                }
                monstersPicked.add(monToAdd);
                if (runeSetNum != -1)
                {
                    scan.nextLine();
                }
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("Oops! Rune set not found");
                scan.nextLine();
                System.out.println();
            }
            catch (Exception ignored)
            {
            }
        }
        return new Team("Team 1", monstersPicked);
    }
}
