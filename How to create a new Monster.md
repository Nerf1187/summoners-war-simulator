This file is only needed if you intend to create a new Monster.

1. Create a new class named the Monster's name, replacing spaces with underscores as needed.
   Have it extend the Monster parent class and make sure it is in the Monsters/(element) package.


2. Create two constructors, one which takes no arguments (this is the default constructor) and one which takes a
   String as an argument (this is to apply a rune set that is not the default).


3. Create the Abilities (Not the specific things they do, just the general like damage and effects) and put them in an ArrayList<Ability>.
   There are helper methods in the Monster parent class to help create the ArrayLists.
   Once all abilities are created call the parent setAbilities() method using the ArrayList<Ability> as an argument.


4. Look at the Monster child classes already created for help formatting the constructors.


5. Create a nextTurn() method that overrides the parent class.
   This method should now do anything needed before the actual turn starts then call the super method.
   If the parent returns false then the child method should immediately end, returning false.
   Otherwise, now is when you add the specific things the abilities do, such as conditional debuffs/buffs. At the end of the method, call the afterTurnProtocol() parent 
   function and return true.


6. Create any other methods as needed (ex. Miho has an attacked() and afterHitProtocol() function for her passive).
   If the Monster creates or uses any effects that are not buffs or debuffs, create a reset() method that handles how to dispose of them when resetting the Monster.


7. Go to [Runes](src/GUI/Runes.java) and follow the instructions to create your rune set(s)


8. Add the Monster's name and element to the [database](src/Monsters/Monster%20database.csv), and it will automatically be added to the program


9. You can use the [Main](src/Game/Main.java) class to make sure your runes are correct and to test your new Monster.
   (You can adjust stats in the class if you want, such as the speed stat if you want them to always go first).