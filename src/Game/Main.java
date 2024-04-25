package Game;

import Monsters.Dark.*;
import Monsters.Fire.*;
import Monsters.*;
import Monsters.Water.*;
import Monsters.Wind.*;
import Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import java.util.*;

/**
 * This class is a manual battle simulation. You can either play with preset Teams or pick Teams in the program (pick 5, ban 1)
 */
public class Main
{
    //Scanner
    public static final Scanner scan = new Scanner(System.in);
    
    //Boolean to test if nextMon is stunned
    protected static boolean stunned = false;
    
    //Boolean to make sure next monster isn't changed when changing abilities
    private static boolean monChosen = false;
    
    private static Monster next = null;
    
    /**
     * Runs this program
     */
    void main()
    {
        Monster.setDatabase();
        //Create team ArrayLists
        ArrayList<Monster> mons1;
        ArrayList<Monster> mons2;
        
        ArrayList<ArrayList<Monster>> teams = selectTeams();
        mons1 = teams.get(0);
        mons2 = teams.get(1);
        
        //Set teams
        Team team1 = new Team("Team 1", mons1);
        Team team2 = new Team("Team 2", mons2);
        
        setRuneEffectsAndNames(team1, team2);
        pickLeaders(team1, team2);
        
        //Create game
        Game game = new Game(team1, team2);
        
        //Play game
        while (!game.endGame())
        {
            //Increment attack bars until at least one is full
            if (!game.hasFullAtkBar())
            {
                game.increaseAtkBar();
                continue;
            }
            
            //Print game and find who is next
            System.out.println("\n\n" + game + "\n\n");
            pause(1000);
            if (!monChosen)
            {
                Team teamWithHighestAtkBar = game.getTeamWithHighestAtkBar();
                next = teamWithHighestAtkBar.MonsterWithHighestFullAtkBar();
            }
            System.out.println(next);
            pause(1000);
            
            //Activate before turn passives
            game.activateBeforeTurnPassives(next);
            
            if (!monChosen)
            {
                game.applyStats(next);
            }
            
            //If the monster's Hp falls below zero before turn
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
            }
            
            //Print next mon
            System.out.println(next.shortToString(true));
            
            //Check if stunned
            if (stunned)
            {
                stunned = false;
                next.decreaseStatCooldowns();
                next.setAtkBar(0);
                continue;
            }
            
            //Go to next monster if current is dead
            if (next.getCurrentHp() <= 0)
            {
                next.kill();
                monChosen = false;
                if (next.isDead())
                {
                    continue;
                }
            }
            
            //Ends game if a team is dead
            if (game.endGame())
            {
                break;
            }
            pause(300);
            
            //Check for Provoke
            Provoke p = next.getProvoke();
            if (p != null)
            {
                System.out.println(ConsoleColors.YELLOW + "Provoked!" + ConsoleColors.RESET);
                Monster caster = p.getCaster();
                next.nextTurn(caster, 1);
                continue;
            }
            
            //Checks if any Monster on the other team has Threat
            boolean threat = game.getOtherTeam().monHasThreat();
            
            //Get ability number/print buffs and debuffs descriptions
            monChosen = true;
            int abilityNum = Game.getAbilityNum(next);
            
            //get target num/re-choose ability if wanted
            Monster targetMon = game.getTarget(next, abilityNum, threat);
            if (targetMon == null)
            {
                continue;
            }
            
            //Apply nextTurn()
            //If something went wrong, does process again
            Game.applyNextTurn(next, targetMon, abilityNum);
            monChosen = false;
            pause(800);
        }
        
        //Print which team won
        System.out.printf("%s wins!", ConsoleColors.BLUE_BOLD_BRIGHT + game.getWinningTeam().getName());
    }
    
    /**
     * Prints the stun effect to the console
     */
    protected static void printStunEffect()
    {
        if (Monster.isPrint())
        {
            System.out.println("Stunned!");
            pause(200);
        }
    }
    
    /**
     * Prints the sleep effect to the console
     */
    protected static void printSleepEffect()
    {
        if (Monster.isPrint())
        {
            System.out.println("Zzzzz... Slept!");
            pause(200);
        }
    }
    
    /**
     * Prints the freeze effect to the console
     */
    protected static void printFreezeEffect()
    {
        if (Monster.isPrint())
        {
            System.out.println(ConsoleColors.CYAN_BACKGROUND_BRIGHT + ConsoleColors.BLACK_BOLD + "Brrr... Frozen!" + ConsoleColors.RESET);
            pause(200);
        }
    }
    
    /**
     * Prints the bomb explosion effect
     *
     * @param target The Monster with the bomb
     */
    protected static void printBombExplodeEffect(Monster target)
    {
        
        if (Monster.isPrint())
        {
            System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + ConsoleColors.BLACK_BOLD + "BOOM!" + ConsoleColors.RESET + " Bomb exploded! You " +
                    "took " + (target.getMaxHp() * 0.4) + " damage! You have " + Math.max(target.getCurrentHp(), 0) + " health left!");
        }
        
        printStunEffect();
    }
    
    /**
     * Pauses the program for a given time
     *
     * @param time The amount of time (in milliseconds) to pause
     */
    public static void pause(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException ignored)
        {
        }
    }
    
    /**
     * Converts the given String to a Monster using the name
     *
     * @param name The name of the Monster
     * @param mons the List of Monsters to search in
     * @return the Monster whose name equals the given String
     */
    private static Monster getMonFromName(String name, ArrayList<Monster> mons)
    {
        for (Monster mon : mons)
        {
            if (mon.getName(false, false).equalsIgnoreCase(name))
            {
                return mon;
            }
        }
        return new Monster();
    }
    
    /**
     * Sorts the given HashMap keys in alphabetical order
     *
     * @param map the HashMap to sort
     */
    public static void sort(HashMap<String, String> map)
    {
        ArrayList<String> list = new ArrayList<>(map.keySet());
        for (int i = 1; i < list.size(); i++)
        {
            int count = i;
            String temp = list.get(count);
            while (count >= 1 && list.get(count).compareTo(list.get(i - 1)) < 0)
            {
                list.set(count, list.get(i - 1));
                count--;
            }
            list.set(count, temp);
        }
        HashMap<String, String> tempMap = new HashMap<>();
        for (String s : list)
        {
            tempMap.put(s, map.get(s));
        }
        map = tempMap;
    }
    
    /**
     * Adds the elemental color to the given String
     *
     * @param name    The name of the Monster
     * @param element The element of the Monster
     * @return the name of the Monster with the appropriate elemental color
     */
    private static String nameWithElement(String name, String element)
    {
        String s;
        switch (element.toLowerCase())
        {
            case "fire" -> s = ConsoleColors.RED_BOLD_BRIGHT;
            case "water" -> s = ConsoleColors.BLUE_BOLD_BRIGHT;
            case "wind" -> s = ConsoleColors.YELLOW_BOLD_BRIGHT;
            case "light" -> s = ConsoleColors.WHITE_BOLD_BRIGHT;
            case "dark" -> s = ConsoleColors.PURPLE_BOLD_BRIGHT;
            default ->
            {
                return null;
            }
        }
        return s + name + ConsoleColors.RESET;
    }
    
    /**
     * Prints the Monsters that can still be picked for the Team
     *
     * @param monsPicked The Monsters that can not be picked
     */
    protected static void printMonsToPick(ArrayList<Monster> monsPicked)
    {
        ArrayList<String> toPrint = new ArrayList<>();
        ArrayList<String> fire = new ArrayList<>();
        ArrayList<String> water = new ArrayList<>();
        ArrayList<String> wind = new ArrayList<>();
        ArrayList<String> light = new ArrayList<>();
        ArrayList<String> dark = new ArrayList<>();
        try
        {
            Scanner read = new Scanner(Objects.requireNonNull(Monster.class.getResourceAsStream("Monster database.csv")));
            while (read.hasNextLine())
            {
                String[] monAndElement = read.nextLine().split(",");
                String name = nameWithElement(monAndElement[0], monAndElement[1]);
                if (getMonFromName(monAndElement[0], monsPicked).equals(new Monster()))
                {
                    switch (monAndElement[1].toLowerCase())
                    {
                        case "fire" -> fire.add(name);
                        case "water" -> water.add(name);
                        case "wind" -> wind.add(name);
                        case "light" -> light.add(name);
                        case "dark" -> dark.add(name);
                    }
                }
            }
            Collections.sort(water);
            Collections.sort(fire);
            Collections.sort(wind);
            Collections.sort(light);
            Collections.sort(dark);
            toPrint.addAll(water);
            toPrint.addAll(fire);
            toPrint.addAll(wind);
            toPrint.addAll(light);
            toPrint.addAll(dark);
            for (String s : toPrint)
            {
                System.out.print(s + "      ");
            }
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Allows the user to either play with preset Teams or pick the Monsters for each Team
     *
     * @return The two Teams chosen by the user
     */
    public static ArrayList<ArrayList<Monster>> selectTeams()
    {
        ArrayList<ArrayList<Monster>> teams = new ArrayList<>();
        
        //Create team ArrayLists
        ArrayList<Monster> mons1 = new ArrayList<>();
        ArrayList<Monster> mons2 = new ArrayList<>();
        
        //Skips monster selection and starts game with pre-built Teams if user answers "y"
        String test;
        if (!Monster.monsterNamesDatabase.isEmpty())
        {
            System.out.println("Preset teams? (Type \"y\" for yes)");
            test = scan.nextLine();
        }
        else
        {
            test = "y";
        }
        if (test.equalsIgnoreCase("y"))
        {
            mons1.add(new Riley());
            mons1.add(new Rasheed());
            mons1.add(new Kaki());
            mons1.add(new Dominic());
            
            mons2.add(new Ariel());
            mons2.add(new Aegir());
            mons2.add(new Riley());
            mons2.add(new Rasheed());
        }
        
        //4v4
        //Choose monsters 5 monsters for each team then ban 1
        if (!test.equals("y"))
        {
            //Pick mons
            while (mons1.size() < 5 || mons2.size() < 5)
            {
                String inputMon;
                //Team 1 pick
                if (mons1.size() < 5 && mons1.size() == mons2.size())
                {
                    do
                    {
                        printMonsToPick(mons1);
                        System.out.print("\nTeam 1 current team: ");
                        for (Monster mon : mons1)
                        {
                            System.out.print(mon.getName(true, false) + ", ");
                        }
                        System.out.println("\nTeam 1 choose your monster (type \"inspect\" to inspect a monster)");
                        inputMon = scan.nextLine();
                        
                        //inspect monster
                        if (inputMon.equals("inspect"))
                        {
                            Monster.inspect();
                        }
                    }
                    while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, mons1));
                    //Get class
                    if (!addMonToTeam(mons1, inputMon))
                    {
                        continue;
                    }
                }
                
                
                //Team 2 pick
                if (mons2.size() < 5 && mons1.size() == mons2.size() + 1)
                {
                    do
                    {
                        printMonsToPick(mons2);
                        System.out.print("\nTeam 2 current team: ");
                        for (Monster mon : mons2)
                        {
                            System.out.print(mon.getName(true, false) + ", ");
                        }
                        System.out.println("\nTeam 2 choose your monster (type \"inspect\" to inspect a monster)");
                        inputMon = scan.nextLine();
                        //Inspect
                        if (inputMon.equals("inspect"))
                        {
                            Monster.inspect();
                        }
                    }
                    while (!Monster.stringIsMonsterName(inputMon) || Team.teamHasMon(inputMon, mons2));
                }
            }
            
            //Ban one mon from other team
            String banName;
            do
            {
                System.out.println("Team 2 choose one monster from Team 1 to ban.");
                for (Monster mon : mons1)
                {
                    System.out.print(mon.getName(true, false) + ", ");
                }
                System.out.println();
                banName = scan.nextLine();
                //Inspect
                if (banName.equals("inspect"))
                {
                    Monster.inspect();
                }
            }
            while (!Monster.stringIsMonsterName(banName, mons1));
            mons1.remove(getMonFromName(banName, mons1));
            
            do
            {
                System.out.println("Team 1 choose one monster from Team 2 to ban.");
                for (Monster mon : mons2)
                {
                    System.out.print(mon.getName(true, false) + ", ");
                }
                System.out.println();
                banName = scan.nextLine();
                //Inspect
                if (banName.equals("inspect"))
                {
                    Monster.inspect();
                }
            }
            while (!Monster.stringIsMonsterName(banName, mons2));
            mons2.remove(getMonFromName(banName, mons2));
        }
        
        teams.add(mons1);
        teams.add(mons2);
        return teams;
    }
    
    private static boolean addMonToTeam(ArrayList<Monster> teamToAdd, String inputMon)
    {
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
            teamToAdd.add(monToAdd);
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
            return false;
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        System.out.println();
        return true;
    }
    
    /**
     * Activates the team based rune effects (e.g. fight, shield) and sets the names for each Monster
     *
     * @param team1 The first Team to apply
     * @param team2 The second Team to apply
     */
    public static void setRuneEffectsAndNames(Team team1, Team team2)
    {
        //Implement ally rune effects
        for (Monster mon : team1.getMonsters())
        {
            for (Monster mon1 : team1.getMonsters())
            {
                mon1.addAppliedBuff(new Rune_shield((int) (mon.getMaxHp() * (0.15 * mon.numOfSets(Rune.SHIELD))), 3), mon);
                mon1.setAtk((int) (mon1.getAtk() + mon1.getBaseAtk() * (0.08 * mon.numOfSets(Rune.FIGHT))));
                mon1.setDef((int) (mon1.getDef() + mon1.getBaseDef() * (0.08 * mon.numOfSets(Rune.DETERMINATION))));
                mon1.setMaxHp((int) (mon1.getMaxHp() + mon1.getBaseMaxHp() * (0.08 * mon.numOfSets(Rune.ENHANCE))));
                mon1.setAccuracy((mon1.getAccuracy() + (10 * mon.numOfSets(Rune.ACCURACY))));
                mon1.setResistance(mon1.getResistance() + (10 * mon.numOfSets(Rune.TOLERANCE)));
                mon1.setCurrentHp(mon1.getMaxHp());
            }
        }
        for (Monster mon : team2.getMonsters())
        {
            for (Monster mon1 : team2.getMonsters())
            {
                mon1.addAppliedBuff(new Rune_shield((int) (mon.getMaxHp() * (0.15 * mon.numOfSets(Rune.SHIELD))), 3), mon);
                mon1.setAtk((int) (mon1.getAtk() + mon1.getBaseAtk() * (0.08 * mon.numOfSets(Rune.FIGHT))));
                mon1.setDef((int) (mon1.getDef() + mon1.getBaseDef() * (0.08 * mon.numOfSets(Rune.DETERMINATION))));
                mon1.setMaxHp((int) (mon1.getMaxHp() + mon1.getBaseMaxHp() * (0.08 * mon.numOfSets(Rune.ENHANCE))));
                mon1.setAccuracy((mon1.getAccuracy() + (10 * mon.numOfSets(Rune.ACCURACY))));
                mon1.setResistance(mon1.getResistance() + (10 * mon.numOfSets(Rune.TOLERANCE)));
                mon1.setCurrentHp(mon1.getMaxHp());
            }
        }
        
        //Set names for each team
        for (Monster mon : team1.getMonsters())
        {
            mon.setName(mon.getName(false, false) + "(1)");
        }
        for (Monster mon : team2.getMonsters())
        {
            mon.setName(mon.getName(false, false) + "(2)");
        }
    }
    
    /**
     * Gets the rune set number to apply to the Monster
     *
     * @return the rune set number to apply
     */
    public static int getRuneSetNum()
    {
        int runeSetNum = 0;
        do
        {
            System.out.println("Type rune set number or \"d\" for default");
            try
            {
                runeSetNum = Math.max((scan.nextInt()), 0);
            }
            catch (InputMismatchException e)
            {
                if (scan.nextLine().equals("d"))
                {
                    runeSetNum = -1;
                }
            }
        }
        while (runeSetNum == 0);
        return runeSetNum;
    }
    
    /**
     * Allows the user to pick leaders for each Team if a leader is available
     *
     * @param t1 The first Team
     * @param t2 The second Team
     */
    public static void pickLeaders(Team t1, Team t2)
    {
        ArrayList<Monster> mons1 = new ArrayList<>();
        ArrayList<Monster> mons2 = new ArrayList<>();
        for (Monster mon : t1.getMonsters())
        {
            if (mon.hasLeaderSkill())
            {
                mons1.add(mon);
            }
        }
        for (Monster mon : t2.getMonsters())
        {
            if (mon.hasLeaderSkill())
            {
                mons2.add(mon);
            }
        }
        Team pickLeader1 = new Team("1", mons1);
        Team pickLeader2 = new Team("2", mons2);
        
        getLeader(pickLeader1, t1);
        getLeader(pickLeader2, t2);
    }
    
    /**
     * Prompt the user to select a leader Monster for the Team if possible, prints a message otherwise.
     *
     * @param potentialLeaders An ArrayList of potential leaders.
     * @param team             The Team to apply the leader skill to.
     */
    private static void getLeader(Team potentialLeaders, Team team)
    {
        if (!potentialLeaders.getMonsters().isEmpty())
        {
            String inputMon;
            do
            {
                System.out.println("Team " + potentialLeaders.getName() + " pick a leader by typing their name (Type \"inspect\" to inspect a Monster)");
                System.out.println(potentialLeaders);
                inputMon = scan.nextLine();
                if (inputMon.equals("inspect"))
                {
                    Monster.inspect();
                }
            }
            while (!Monster.stringIsMonsterName(inputMon, potentialLeaders.getMonsters()));
            Monster leader = getMonFromName(inputMon, potentialLeaders.getMonsters());
            leader.applyLeaderSkill(team);
        }
        else
        {
            System.out.println("No leader skill available.");
        }
    }
}