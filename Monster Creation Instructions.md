This file is only needed if you intend to create a new Monster.

1. Go to the [Developer Website](src/Developer%20Website/index.html), enter all the necessary information including stats and abilities. Submit the form when you're done to format everything


2. Create a file in the correct element directory (e.g. Water, Fire, Dark, etc.) in the [Monsters](src/Monsters) folder with the correct name


3. Copy the result from the website and paste it in the new file.


4. Check the lines marked by a TODO comment and add anything else needed.


5. Create any other methods as needed (ex. Miho has an attacked() and afterHitProtocol() function for her passive).
   If the Monster creates or uses any effects that are not buffs or debuffs, create a reset() method that handles how to dispose of them when resetting the Monster.


6. Go to [Runes](src/GUI/Runes.java) and follow the instructions to create your rune set(s)


7. Add the Monster's name and element to the [database](src/Monsters/Monster%20database.csv), and it will automatically be added to the program


8. You can use the [Main](src/Game/Main.java) class to make sure your runes are correct and to test your new Monster.
   (You can adjust stats in the class if you want, such as the speed stat if you want them to always go first).