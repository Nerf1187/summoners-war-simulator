/**
 * Inputs for attack abilities
 * @type String
 */
const attackInputs = `
        <!-- Ability name -->
        <label for="{name}-name">Ability Name</label>
        <input type="text" id="{name}-name" style="width: 100px">
        
        <!-- Damage multiplier -->
        <label for="{name}-multiplier">Damage Multiplier</label>
        <input type="number" id="{name}-multiplier">
        
        <!-- Other-stat multiplier -->
        <label for="{name}-other-stat_dmg">Damage Increases According to Other Stat?</label>
        <input type="checkbox" id="{name}-other-stat_dmg" onchange="{
            // Show selector if checkbox is checked, hide it if not
            let div = document.getElementById('{name}-dmg-stat-div');
            div.style.display = (this.checked) ? 'inline-block' : 'none';
        }">
        
        <!-- Other-stat multiplier selector -->
        <div id="{name}-dmg-stat-div" style="display: none; margin-right: 18px">
            <!-- Stat selector -->
            <label for="{name}-dmg-stat">Which Stat?</label>
            <select id="{name}-dmg-stat">
                <option value="MaxHp">HP</option>
                <option value="Def">Defense</option>
                <option value="Spd">Speed</option>
            </select>
            
            <!-- Multiplier amount -->
            <label for="{name}-dmg-stat-amount">Amount?</label>
            <input type="number" id="{name}-dmg-stat-amount" style="margin-right: 0">%
        </div>
        
        <!-- Skill up damage -->
        <label for="{name}-skill-up-dmg">Skill Up Damage Increase</label>
        <input type="number" id="{name}-skill-up-dmg" style="margin-right: 0">
        <p style="display: inline-block; margin-right: 10px" class="percent" id="attack-skill-percent">%</p>
        
        <!-- Healing amount -->
        <label for="{name}-healing-percent">Healing Percent</label>
        <input type="number" id="{name}-healing-percent">
        
        <!-- Number of activations (hits) -->
        <label for="{name}-number-of-activations">Number of Activations</label>
        <input type="number" id="{name}-number-of-activations" min="1">
        
        <br>
        <!-- Ability description -->
        <div class="ability-description">
            <label for="{name}-description" style="margin-right: 5px;">Description</label>
            <textarea id="{name}-description" style="width: 900px; height: 80px;"></textarea>
        </div>
        <br>
        
        <!-- Ability cooldown -->
        <label for="{name}-cooldown">Cooldown</label>
        <input type="number" id="{name}-cooldown" min="0">
        
        <!-- Ability ignores defense checkbox -->
        <label for="{name}-ignores-defense">Ignores Defense?</label>
        <input type="checkbox" id="{name}-ignores-defense">
        
        <!-- Ability ignores damage reduction checkbox -->
        <label for="{name}-ignores-dmg-reduction">Ignores Dmg Reduction?</label>
        <input type="checkbox" id="{name}-ignores-dmg-reduction">
        
        <!-- Ability hits team checkbox -->
        <label for="{name}-hits-team">Hits Team?</label>
        <input type="checkbox" id="{name}-hits-team">
`;

/**
 * Inputs for heal abilities
 * @type String
 */
const healInputs = `
        <!-- Ability name -->
        <label for="{name}-name">Ability Name</label>
        <input type="text" id="{name}-name" style="width: 100px;">
        
        <!-- Ability healing amount -->
        <label for="{name}-healing-percent">Healing Percent</label>
        <input type="number" id="{name}-healing-percent">
        
        <!-- Ability skill up amount-->
        <label for="{name}-skill-up-healing">Skill Up Healing Increase</label>
        <input type="number" id="{name}-skill-up-healing" style="margin-right: 0">
        <p style="display: inline-block; margin-right: 10px" class="percent" id="{name}-skill-percent">%</p>
        
        <!-- Ability number of activations -->
        <label for="{name}-number-of-activations">Number of Activations</label>
        <input type="number" id="{name}-number-of-activations">
        
        <br>
        <!-- Ability description -->
        <div class="ability-description">
            <label for="{name}-description" style="margin-right: 5px;">Description</label>
            <textarea id="{name}-description" style="width: 900px; height: 80px;"></textarea>
        </div>
        <br>
        
        <!--Ability cooldown-->
        <label for="{name}-cooldown">Cooldown</label>
        <input type="number" id="{name}-cooldown">
        
        <!-- Ability targets team checkbox-->
        <label for="{name}-targets-team">Hits Team?</label>
        <input type="checkbox" id="{name}-targets-team">
`;

/**
 * Inputs for passive abilities
 * @type String
 */
const passiveInputs = `
        <!-- Ability name -->
        <label for="{name}-name">Ability Name</label>
        <input type="text" id="{name}-name" style="width: 100px;">
        <br>
        
        <!-- Ability Description -->
        <div class="ability-description">
            <label for="{name}-description" style="margin-right: 5px;">Description</label>
            <textarea id="{name}-description" style="width: 900px; height: 80px;"></textarea>
        </div>
`;

/**
 * Inputs for leader skills
 * @type String
 * @private
 */
const leaderInputs = `
        <!-- Stat selector-->
        <label for="{name}-stat">Stat</label>
        <select id="{name}-stat">
            <option value="DEF">DEF</option>
            <option value="ATK">ATK</option>
            <option value="SPD">SPD</option>
            <option value="HP">HP</option>
            <option value="RES">RES</option>
        </select>
        
        <!-- Stat amount -->
        <label for="{name}-amount">Amount</label>
        <input type="number" id="{name}-amount" min="0" max="1" style="margin-right: 0">
        <p style="display: inline-block; margin-right: 10px;" class="percent" id="leader-stat-percent">%</p>
        
        <!-- Affected element selector-->
        <label for="{name}-element">Element Affected?</label>
        <select id="{name}-element">
            <option value="WATER">WATER</option>
            <option value="FIRE">FIRE</option>
            <option value="WIND">WIND</option>
            <option value="LIGHT">LIGHT</option>
            <option value="DARK">DARK</option>
            <option value="ALL">ALL</option>
        </select>
`;

/**
 * Inputs for parent Ability
 * @type String
 * @private
 */
const parentInputs = `
        <!-- Ability name -->
        <label for="{name}-name">Ability Name</label>
        <input type="text" id="{name}-name" style="width: 100px;">
        
        <!-- Ability damage multiplier-->
        <label for="{name}-multiplier">Damage Multiplier</label>
        <input type="number" id="{name}-multiplier">
        
        <!-- Ability healing amount-->
        <label for="{name}-healing-percent">Healing Percent</label>
        <input type="number" id="{name}-healing-percent">
        
        <!-- Ability number of activations -->
        <label for="{name}-number-of-activations">Number of Activations</label>
        <input type="number" id="{name}-number-of-activations">
        
        <br>
        <!-- Ability description -->
        <div class="ability-description">
            <label for="{name}-description" style="margin-right: 5px;">Description</label>
            <textarea id="{name}-description" style="width: 900px; height: 80px;"></textarea>
        </div>
        <br>
        
        <!-- Ability cooldown -->
        <label for="{name}-cooldown">Cooldown</label>
        <input type="number" id="{name}-cooldown">
        
        <!-- Ability targets enemy checkbox -->
        <label for="{name}-targets-enemy">Targets Enemy?</label>
        <input type="checkbox" id="{name}-targets-enemy">
        
        <!-- Ability is passive checkbox -->
        <label for="{name}-is-passive">Passive?</label>
        <input type="checkbox" id="{name}-is-passive">
        
        <!--Ability ignores defense checkbox -->
        <label for="{name}-ignores-defense">Ignores Defense?</label>
        <input type="checkbox" id="{name}-ignores-defense">
        
        <!-- Ability targets self checkbox -->
        <label for="{name}-targets-self">Targets Self?</label>
        <input type="checkbox" id="{name}-targets-self">
        
        <!-- Ability ignores damage reduction checkbox-->
        <label for="{name}-ignores-dmg-reduction">Ignores Dmg Reduction?</label>
        <input type="checkbox" id="{name}-ignores-dmg-reduction">
        
        <!-- Ability hits team checkbox-->
        <label for="{name}-hits-team">Hits Team?</label>
        <input type="checkbox" id="{name}-hits-team">
`;

/**
 * Contains all buffs formatted in individual HTML <option> tags
 * @type String
 */
let buffOptions = "";

/**
 * Contains all debuffs formatted in individual HTML <option> tags
 * @type String
 */
let debuffOptions = "";

/**
 * Create HTML logic
 */
window.onload = () => {
    //Get all buffs and debuffs from files
    getAllBuffs();
    getAllDebuffs();
    
    //Create the input form
    const form = document.getElementById("form");
    const formElements = form.elements;
    
    //Add ability button
    const addAbilityButton = document.getElementById("add-ability");
    
    //Ability containers
    let abilityDivs = document.getElementById("abilities");
    
    //Ability information
    let abilityTypes = [];
    let abilityTargets = [];
    
    let trueOldIndex = -1;
    
    //Add ability logic
    addAbilityButton.addEventListener("click", () => {
        //Create new div
        let newDiv = document.createElement("div");
        let divNum = abilityDivs.children.length + 1 || 1;
        let divName = "ability" + divNum;
        newDiv.id = divName;
        newDiv.className = "ability";
        newDiv.innerHTML = `
                <!-- Display ability number -->
                <h2 id="${divName}-header"><span class="handle">&equiv;</span>Ability ${divNum}</h2>

                <!-- Ability type -->
                <label for="${divName}-type">Ability Type</label>
                <!-- Change inputs to ability type -->
                <select id="${divName}-type" onchange="{
                    let dynamicDivName = 'ability' + this.id.charAt(7);
                    let inputs = document.getElementById(dynamicDivName + '-inputs');
                    document.getElementById(dynamicDivName + '-buffs').style.removeProperty('display');
                    document.getElementById(dynamicDivName + '-debuffs').style.removeProperty('display');
                    switch (this.value)
                    {
                        case 'Attack':
                            inputs.innerHTML = attackInputs.formatUnicorn({name: dynamicDivName});
                            break;
                        case 'Heal':
                            inputs.innerHTML = healInputs.formatUnicorn({name: dynamicDivName});
                            break;
                        case 'Passive':
                            inputs.innerHTML = passiveInputs.formatUnicorn({name: dynamicDivName});
                            document.getElementById(dynamicDivName + '-buffs').style.display = 'none';
                            document.getElementById(dynamicDivName + '-debuffs').style.display = 'none';
                            break;
                        case 'Leader':
                            inputs.innerHTML = leaderInputs.formatUnicorn({name: dynamicDivName});
                            document.getElementById(dynamicDivName + '-buffs').style.display = 'none';
                            document.getElementById(dynamicDivName + '-debuffs').style.display = 'none';
                            break;
                        case 'Parent':
                            inputs.innerHTML = parentInputs.formatUnicorn({name: dynamicDivName});
                    }
                }">
                    <option value="Attack">Attack</option>
                    <option value="Heal">Heal</option>
                    <option value="Passive">Passive</option>
                    <option value="Leader">Leader</option>
                    <option value="Parent">Parent</option>
                </select>
                
                <!-- Buffs -->
                <div id="ability${divNum}-buffs" class="section">
                    <h3>Buffs</h3>
                    <div id="ability${divNum}-buffs-container"></div>
                    <!-- Buff adder -->
                    <button type="button" id="ability${divNum}-add-buffs" class="buff-button buff" onclick="{
                        let buffs = this.previousElementSibling;
                        let newBuffLength = buffs.children.length + 1;
                        let div = document.createElement('div');
                        div.className = 'effect';
                        
                        //Create a dynamic div name in case the div number has changed
                        let dynamicDivName = 'ability' + this.id.charAt(7);
                        
                        // Add buff logic
                        div.innerHTML = \`
                            <!-- Display buff number -->
                            <h4 id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-header'>Buff \` + newBuffLength + \`</h4>
                        
                            <!-- Buff type selector -->
                            <label for='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-selector'></label>
                            <select id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-selector'>\` + buffOptions + \`</select>
                            
                            <!-- Extra inputs go here if needed -->
                            <div id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-extras' style='display: none;'>
                                    <label for='\` + dynamicDivName + \`-buff-amount'>Amount</label>
                                    <input type='number' id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-amount' style='margin-right: 0'>
                                    <p style='margin-right: 10px; display: inline-block' class='percent' id ='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-percent'>%</p>
                            </div>
                            
                            <!-- Turns applied -->
                            <label for='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-turns'>Turns</label>
                            <input type='number' id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-turns' required='' value=''>
                        
                            <!-- Chance applied -->
                            <label for='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-chance'>Chance</label>
                            <input type='number' id='\` + dynamicDivName + \`-buff\` + newBuffLength + \`-chance' required='' min='0' max='100' style='margin-right: 1px;'>%
                            
                            <!-- Remove buff -->
                            <button class='debuff-button' onclick='{this.parentElement.parentElement.renameAllChildrenEffects(\` + newBuffLength + \`); this.parentElement.remove();}' style='margin-left: 10px;'>Remove buff</button>
                        \`;
                        buffs.appendChild(div);
                        
                        //Add extra inputs for certain buffs
                        let buffSelector = document.getElementById(dynamicDivName + '-buff' + newBuffLength + '-selector')
                        buffSelector.onchange = () => {
                            let element = document.getElementById(buffSelector.id.substring(0, 8) + '-buff' + buffSelector.id.charAt(13) + '-extras');
                            if (element)
                            {
                                switch (buffSelector.value)
                                {
                                    case 'Shield':
                                    case 'Inc Atk Bar':
                                    case 'Remove Debuff':
                                    case 'Buff Steal':
                                        element.style.display = 'inline-block';
                                        break;
                                    default:
                                        element.style.display = 'none';
                                }
                                
                                switch (buffSelector.value)
                                {
                                    case 'Shield':
                                    case 'Remove Debuff':
                                    case 'Buff Steal':
                                        element.lastElementChild.innerHTML = '';
                                        break;
                                    default:
                                        element.lastElementChild.innerHTML = '%';
                                }
                                
                                switch (buffSelector.value)
                                {
                                    case 'Inc Atk Bar':
                                    case 'Buff Steal':
                                    case 'Remove Debuff':
                                    case 'Cleanse':
                                        element.nextElementSibling.style.display = 'none';
                                        element.nextElementSibling.nextElementSibling.style.display = 'none';
                                        break;
                                    default:
                                        //4, 5
                                        element.nextElementSibling.style.display = 'inline-block';
                                        element.nextElementSibling.nextElementSibling.style.display = 'inline-block';
                                        break;
                                }
                            }
                        }
                    }">Add buff</button>
                </div>
                <br>
                
                <!-- Debuffs -->
                <div id="ability${divNum}-debuffs" class="section">
                    <h3>Debuffs</h3>
                    <div id="ability${divNum}-debuffs-container"></div>
                    <button type="button" id="ability${divNum}-add-debuff" class="debuff-button debuff" onclick="{
                        let debuffs = this.previousElementSibling;
                        let newDebuffLength = debuffs.children.length + 1;
                        let div = document.createElement('div');
                        div.className = 'effect';
                        let dynamicDivName = 'ability' + this.id.charAt(7);
                        
                        // Add debuff logic
                        div.innerHTML = \`
                            <!-- Display debuff number -->
                            <h4 id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-header'>Debuff \` + newDebuffLength + \`</h4>
                        
                            <!-- Debuff type selector -->
                            <label for='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-selector'></label>
                            <select id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-selector'>\` + debuffOptions + \`</select>
                        
                            <!-- Extra inputs go here if needed -->
                            <div id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-extras' style='display: none;'>
                                <label for='\` + dynamicDivName + \`-debuff-amount'>Amount</label>
                                <input type='number' id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-amount' style='margin-right: 0'>
                                <p style='margin-right: 10px; display: inline-block' class='percent' id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-percent'>%</p>
                            </div>
                        
                            <!-- Turns applied -->
                            <label for='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-turns'>Turns</label>
                            <input type='number' id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-turns' required=''>
                            
                            <!-- Goes through immunity checkbox -->
                            <label for='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-through-immunity'>Goes through Immunity?</label>
                            <input type='checkbox' id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-through-immunity' required=''>
                        
                            <!-- Chance applied -->
                            <label for='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-chance'>Chance</label>
                            <input type='number' id='\` + dynamicDivName + \`-debuff\` + newDebuffLength + \`-chance' required='' min='0' max='100' style='margin-right: 0'>%
                            
                            <!-- Remove debuff -->
                            <button class='debuff-button' onclick='{this.parentElement.parentElement.renameAllChildrenEffects(\` + newDebuffLength + \`); this.parentElement.remove();}' style='margin-left: 10px;'>Remove debuff</button>
                        \`;
                        debuffs.appendChild(div);
                        
                        //Extra inputs for certain debuffs
                        //TODO Add logic for SHORTEN_BUFF
                        let debuffSelector = document.getElementById(dynamicDivName + '-debuff' + newDebuffLength + '-selector')
                        debuffSelector.onchange = () => {
                            let element = document.getElementById(debuffSelector.id.substring(0, 8) + '-debuff' + debuffSelector.id.charAt(15) + '-extras');
                            if (element)
                            {
                                element.style.display = (debuffSelector.value === 'Dec Atk Bar') ? 'inline-block' : 'none';
                                element.children[1].value = (debuffSelector.value === 'Shorten Buff') ? -1 : '';
                            }
                        };
                    }">Add Debuff</button>
                    <br><br>
                </div>
                
                <!-- Default inputs (attack) -->
                <div id="${divName}-inputs">
                    <h3>Ability information</h3>
                    ${attackInputs.formatUnicorn({name: divName})}
                </div>
                
                <br>
                
                <!-- Button to remove the ability -->
                <button type="button" class="debuff-button" onclick="{
                    this.parentElement.parentElement.renameAllChildrenEffects(parseInt(this.parentElement.id.charAt(7)), true);
                    this.parentElement.remove();
                }">Remove Ability</button>
                `;
        
        abilityDivs.appendChild(newDiv);
        
        new Sortable(abilityDivs, {
            animation:  150,
            handle:     ".handle",
            ghostClass: "dragging",
            onEnd:      function () {
                trueOldIndex = -1;
            },
            onChange:   function (event) {
                if (trueOldIndex === -1)
                {
                    trueOldIndex = event.oldDraggableIndex;
                }
                
                console.log(trueOldIndex);
                console.log(event.item);
                
                if (trueOldIndex < event.newDraggableIndex)
                {
                    event.item.renameAllChildrenEffects(trueOldIndex + 1, true, false, true);
                    event.from.childNodes[trueOldIndex + 1].renameAllChildrenEffects(trueOldIndex + 1, true, true, true);
                }
                else
                {
                    event.item.renameAllChildrenEffects(trueOldIndex + 1, true, true, true);
                    event.from.childNodes[trueOldIndex + 1].renameAllChildrenEffects(trueOldIndex + 1, true, false, true);
                }
                
                trueOldIndex = event.newDraggableIndex;
            }
        });
    });
    
    /**
     * Submit code to be formatted
     */
    document.getElementById("submit").addEventListener("click", (e) => {
        // Prevent page from reloading
        e.preventDefault();
        //Format code
        let code = formatCode();
        //Put output into textarea
        let codeOutput = document.getElementById("code-output");
        codeOutput.innerHTML = "";
        codeOutput.innerHTML = code;
    });
    
    /**
     * Copy code output to clipboard
     */
    document.getElementById("copy-button").addEventListener("click", () => {
        let copyOutput = document.getElementById("copy-output");
        let codeOutput = document.getElementById("code-output");
        navigator.clipboard.writeText(codeOutput.innerHTML.replaceAll("&lt;", "<").replaceAll("&gt;", ">")).then(() => copyOutput.innerHTML = "Copied to clipboard!");
    });
    
    /**
     * Format inputs into Java code
     * @return {String} The formatted code
     */
    function formatCode()
    {
        //Monster name
        let name = get("name").toTitleCase().replaceAll(" ", "_");
        
        //Initial constructor function
        let statement = "super(\"{name}\" + count, Element.{element}, {hp}, {def}, {attack}, {spd}, {critRate}, {critDmg}, {res}, {acc});".formatUnicorn({
            name:     name.toTitleCase(),
            element:  get("element").toUpperCase(),
            hp:       parseInt(get("hp")).formatNumbers(),
            def:      parseInt(get("defense")).formatNumbers(),
            attack:   parseInt(get("attack")).formatNumbers(),
            spd:      get("speed"),
            critRate: get("crit-rate"),
            critDmg:  get("crit-dmg"),
            res:      get("resistance"),
            acc:      get("accuracy"),
        });
        
        //Format code and return the result
        return `
        package Monsters.${get("element").toTitleCase()};
        
        import Abilities.*;
        import Monsters.*;
        import Effects.Buffs.*;
        import Effects.Debuffs.*;
        import Effects.*;
        import Util.Util.*;
        import java.util.*;
        
        public class ${name} extends Monster
        {
            private static int count = 1;
            
            /**
            * Creates the Monster with the default rune set
            */
            public ${name}()
            {
                this("${name}1.csv");
            }
            
            /**
            * Creates the Monster with the given rune file
            *
            * @param runeFileName The name of the rune file to use
            */
            public ${name}(String runeFileName)
            {
                ${statement}
                super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
                setAbilities();
                count++;
            }
            
            private void setAbilities()
            {
                ${/*Format each ability*/ formatAllAbilitiesCode()}
            }
            
            public boolean nextTurn(Monster target, int abilityNum)
            {
                //TODO Add prechecks and modifications
                //This is also where effects that are applied before the attack should be added
                //Activate passive abilities here or after the if condition
            
                boolean b = super.nextTurn(target, abilityNum);
                if (!b)
                {
                    return false;
                }
                
                //TODO Add conditions
                
                //TODO Check this line
                //The result of the (possible) ternary operator should be the target monster or team
                ${formatAfterTurnCode()}
                return true;
            }
            
            //TODO Add any extra functions
        }`;
    }
    
    /**
     * Gets the input value with the given name
     * @param {String} elementName The name of the element to retrieve
     * @return {boolean | String | Number | null} The element value, or null if the element does not exist
     */
    function get(elementName)
    {
        try
        {
            let item = formElements.namedItem(elementName);
            return item.type !== "checkbox" ? item.value : item.checked;
        }
        catch (e)
        {
            return null;
        }
    }
    
    /**
     * Formats all submitted abilities
     * @return {String} The formatted ability code
     */
    function formatAllAbilitiesCode()
    {
        //The ability number
        let count = 1;
        //The ability input number
        let abilityStringNum = 1;
        let code = "";
        
        //Keep trying to format abilities until there are none left
        while (true)
        {
            if (get(`ability${count}-type`))
            {
                if (count !== 1)
                {
                    code += "\t\t";
                }
                
                //Format current ability
                code += formatAbilityCode(count, abilityStringNum) + "\n\n";
            }
            else
            {
                //Break the loop when there are no more abilities
                break;
            }
            //Increase if the ability can be actively used (Not a passive or leader)
            abilityStringNum += (get(`ability${count}-type`) !== "Passive" && get(`ability${count}-type`) !== "Leader");
            count++;
        }
        
        if (code)
        {
            code += "\t\t";
        }
        
        //Add final line;
        let A_s = "";
        for (let i = 1; i < count; i++)
        {
            A_s += "a" + (i) + ", ";
        }
        
        code += `super.setAbilities(${A_s.substring(0, A_s.length - 2)});`;
        
        return code;
    }
    
    /**
     * Formats an individual ability
     * @param {Number} abilityNum The ability number
     * @param {Number} stringNum The ability input number
     * @returns {String} The formatted ability code
     */
    function formatAbilityCode(abilityNum, stringNum)
    {
        //Get ability name
        let abilityName = `ability${abilityNum}`;
        let code = "";
        //Get ability type
        let type = get(`${abilityName}-type`);
        //Add type to global array
        abilityTypes.push(type);
        
        //Format all effects
        let hasDebuffs = undefined;
        let hasBuffs = undefined;
        if (type !== "Passive" && type !== "Leader")
        {
            //Format debuffs
            hasDebuffs = formatDebuffs(abilityNum);
            code += hasDebuffs;
            if (hasDebuffs)
            {
                code += "\n\t\t";
            }
            
            //Format buffs
            hasBuffs = formatBuffs(abilityNum);
            code += hasBuffs;
            if (hasBuffs)
            {
                code += "\n\t\t";
            }
        }
        
        //Add start ability code
        if (type === "Passive")
        {
            code += "\t\t//@Passive:Creation\n\t\t";
        }
        code += "Ability a" + abilityNum + " = new ";
        //code += "abilities.add(new ";
        
        //Start of ability constructor
        switch (type)
        {
            case "Attack":
            case "Heal":
                code += type + "_Ability(\"{name} ({num})\", ".formatUnicorn({
                    name: get(`${abilityName}-name`),
                    num:  stringNum
                });
                break;
            case "Passive":
                code += `Passive("${get(`${abilityName}-name`)}", `;
                break;
            case "Leader":
                code += "Leader_Skill(";
                break;
            case "Parent":
                code += `Ability("${get(abilityName + "-name")} (${stringNum})", `;
                break;
        }
        
        //Add other stat damage multiplier if there is one
        if (type === "Attack" || type === "Parent")
        {
            if (!get(`${abilityName}-other-stat_dmg`))
            {
                code += "{multiplier} * {skillUpDmg}, ".formatUnicorn({
                    multiplier: get(`${abilityName}-multiplier`),
                    skillUpDmg: (get(`${abilityName}-skill-up-dmg`).includes(".")) ? get(`${abilityName}-skill-up-dmg`) : "1." + get(`${abilityName}-skill-up-dmg`)
                });
            }
            else
            {
                code += "{skillUpDmg} * ({multiplier} + (get{stat}() * {amount}) / getAtk())".formatUnicorn({
                    skillUpDmg: (get(`${abilityName}-skill-up-dmg`).includes(".")) ? get(`${abilityName}-skill-up-dmg`) : "1." + get(`${abilityName}-skill-up-dmg`),
                    multiplier: get(`${abilityName}-multiplier`),
                    stat:       get(`${abilityName}-dmg-stat`),
                    amount:     (get(`${abilityName}-dmg-stat-amount`).includes(".")) ? get(`${abilityName}-dmg-stat-amount`) : get(`${abilityName}-dmg-stat-amount`) / 100,
                });
            }
        }
        
        //Add more parameters
        if (type !== "Passive" && type !== "Leader")
        {
            code += "{healAmount}{skillUpHealing}, {activations}, ".formatUnicorn({
                healAmount:     get(`${abilityName}-healing-percent`),
                skillUpHealing: (type !== "Heal") ? "" : " * " + ((get(`${abilityName}-skill-up-healing`).includes(".")) ? get(`${abilityName}-skill-up-healing`) : "1." + get(`${abilityName}-skill-up-healing`)),
                activations:    get(`${abilityName}-number-of-activations`),
            });
        }
        
        //Add more parameters
        if (type !== "Leader")
        {
            code += "\"{description}\"".formatUnicorn({description: get(`${abilityName}-description`)});
            if (type !== "Passive")
            {
                code += ", ";
            }
        }
        
        switch (type)
        {
                //Add buffs then debuffs if they exist
            case "Heal":
                if (hasBuffs)
                {
                    code += "{name}Buffs, {name}BuffChances, ".formatUnicorn({name: abilityName});
                }
                if (hasDebuffs)
                {
                    code += "{name}Debuffs, {name}DebuffChances, ".formatUnicorn({name: abilityName});
                }
                break;
                //Add debuffs then buffs if they exist
            case "Attack":
            case "Parent":
                if (hasDebuffs)
                {
                    code += "{name}Debuffs, {name}DebuffChances, ".formatUnicorn({name: abilityName});
                }
                if (hasBuffs)
                {
                    code += "{name}Buffs, {name}BuffChances, ".formatUnicorn({name: abilityName});
                }
                break;
        }
        
        //Add more parameters
        if (type !== "Passive" && type !== "Leader")
        {
            code += "{cooldown}, ".formatUnicorn({cooldown: get(`${abilityName}-cooldown`)});
        }
        
        //Check if the ability targets the team and push the result to global array
        abilityTargets.push(get(`${abilityName}-hits-team`));
        //Add more parameters
        switch (type)
        {
            case "Attack":
                code += "{ignoresDefense}, {ignoresDmgReduction},{ignore} {hitsTeam}".formatUnicorn({
                    ignoresDefense:      get(`${abilityName}-ignores-defense`),
                    ignoresDmgReduction: get(`${abilityName}-ignores-dmg-reduction`),
                    hitsTeam:            get(`${abilityName}-hits-team`),
                    ignore:              (hasBuffs && !hasDebuffs) ? " 0," : ""
                });
                break;
            case "Heal":
                code += "{targetsTeam}".formatUnicorn({targetsTeam: get(`${abilityName}-targets-team`)});
                break;
            case "Parent":
                code += "{targetsEnemy}, {isPassive}, {ignoresDefense}, {targetsSelf}, {ignoresDmgReduction}, {targetsTeam}{ignore}".formatUnicorn({
                    targetsEnemy:        get(`${abilityName}-targets-enemy`),
                    isPassive:           get(`${abilityName}-is-passive`),
                    ignoresDefense:      get(`${abilityName}-ignores-defense`),
                    targetsSelf:         get(`${abilityName}-targets-self`),
                    ignoresDmgReduction: get(`${abilityName}-ignores-dmg-reduction`),
                    targetsTeam:         get(`${abilityName}-hits-team`),
                    ignore:              (hasBuffs && !hasDebuffs) ? ", 0" : ""
                });
                break;
            case "Leader":
                code += "RuneAttribute.{stat}, {amount}, Element.{element}".formatUnicorn({
                    stat:    get(`${abilityName}-stat`),
                    amount:  get(`${abilityName}-amount`),
                    element: get(`${abilityName}-element`),
                });
        }
        
        //End of line
        code += ");";
        
        //Return full code
        return code;
    }
    
    /**
     * Formats the debuffs for the ability
     * @param {Number} abilityNum The ability number
     * @returns {String} The formatted debuffs
     */
    function formatDebuffs(abilityNum)
    {
        //Initialize variables
        let abilityName = `ability${abilityNum}`;
        let line1Code = "";
        let line2Code = "";
        let extraLines = [];
        let count = 1;
        //Add debuffs until there are no more left
        while (true)
        {
            //Check if there are debuffs left
            if (get(`${abilityName}-debuff${count}-selector`))
            {
                //Add comma to line 1 if it is created
                if (line1Code)
                {
                    line1Code += ", ";
                }
                else //Create line 1
                {
                    line1Code = `ArrayList<Debuff> ${abilityName}Debuffs = abilityDebuffs(`;
                }
                
                if (!get(`${abilityName}-debuff${count}-amount`))
                {
                    //Write debuff info to line 1
                    line1Code += "DebuffEffect.{debuff}.getNum(), {turns}, {throughImmunity}".formatUnicorn({
                        debuff:          get(`${abilityName}-debuff${count}-selector`).toUpperCase().replaceAll(" ", "_"),
                        turns:           get(`${abilityName}-debuff${count}-turns`),
                        throughImmunity: get(`${abilityName}-debuff${count}-through-immunity`) + 0,
                    });
                }
                else
                {
                    //Special debuffs
                    extraLines.push(`${abilityName}Debuffs.add(new {debuff}({amount}{turns}));`.formatUnicorn({
                        debuff: get(`${abilityName}-debuff${count}-selector`).replaceAll(" ", ""),
                        amount: (get(`${abilityName}-debuff${count}-amount`) === "-1") ? "" : get(`${abilityName}-debuff${count}-amount`),
                        turns:  (get(`${abilityName}-debuff${count}-turns`)) ?
                                ((get(`${abilityName}-debuff${count}-amount`) === "-1") ? "" : ", ")
                                        + get(`${abilityName}-debuff${count}-turns`) : ""
                    }));
                }
                
                //Add comma to line 2 if it is created
                if (line2Code)
                {
                    line2Code += ", ";
                }
                else //Create line 2
                {
                    line2Code = `\t\tArrayList<Integer> ${abilityName}DebuffChances = MONSTERS.abilityChances(`;
                }
                
                //Write debuff info to line 2
                line2Code += get(`${abilityName}-debuff${count}-chance`);
            }
            else
            {
                //Break loop if no more debuffs
                break;
            }
            count++;
        }
        //Return formatted lines
        return formatEffectLines(line1Code, line2Code, extraLines);
    }
    
    /**
     * Formats the buffs for an ability
     * @param abilityNum {Number} The ability number
     * @returns {String} The formatted buffs
     */
    function formatBuffs(abilityNum)
    {
        //Initialize variables
        let abilityName = `ability${abilityNum}`;
        let line1Code = "";
        let line2Code = "";
        let extraLines = [];
        let count = 1;
        //Add buffs until there are none left
        while (true)
        {
            //Check if there are buffs left
            if (get(`${abilityName}-buff${count}-selector`))
            {
                //Add comma to line 1 if it is created
                if (line1Code)
                {
                    line1Code += ", ";
                }
                else //Create line 1
                {
                    line1Code = `ArrayList<Buff> ${abilityName}Buffs = MONSTERS.abilityBuffs(`;
                }
                
                if (!get(`${abilityName}-buff${count}-amount`))
                {
                    //Write buff info to line 1
                    line1Code += "BuffEffect.{buff}.getNum(), {turns}".formatUnicorn({
                        buff:            get(`${abilityName}-buff${count}-selector`).toUpperCase().replaceAll(" ", "_"),
                        turns:           get(`${abilityName}-buff${count}-turns`),
                        throughImmunity: (get(`${abilityName}-buff${count}-through-immunity`) + 0)
                    });
                }
                else
                {
                    //Special buffs
                    extraLines.push(`${abilityName}Buffs.add(new {buff}({amount}{turns}));`.formatUnicorn({
                        buff:   get(`${abilityName}-buff${count}-selector`).replaceAll(" ", ""),
                        amount: get(`${abilityName}-buff${count}-amount`),
                        turns:  (get(`${abilityName}-buff${count}-turns`)) ? ", " + get(`${abilityName}-buff${count}-turns`) : ""
                    }));
                }
                
                //Add comma to line 2 if it is created
                if (line2Code)
                {
                    line2Code += ", ";
                }
                else //Create line 2
                {
                    line2Code = `\t\tArrayList<Integer> ${abilityName}BuffChances = MONSTERS.abilityChances(`;
                }
                
                //Write buff info to line 2
                line2Code += get(`${abilityName}-buff${count}-chance`);
            }
            else
            {
                //Break loop if no more debuffs
                break;
            }
            count++;
        }
        //Return formatted lines
        return formatEffectLines(line1Code, line2Code, extraLines);
    }
    
    /**
     * Formats the final lines for the buffs and debuffs
     * @param {string} line1 The first line of code
     * @param {string} line2 The second line of code
     * @param {string[]} extraLines Any extra lines of code
     * @returns {string} The formatted lines as a single string
     */
    function formatEffectLines(line1, line2, extraLines)
    {
        //End of lines
        if (line1)
        {
            line1 += ");";
            line2 += ");";
        }
        
        //Return formatted debuffs
        if (line1 && line2)
        {
            let finalString = line1 + "\n";
            //Add extra lines
            extraLines?.forEach(line => finalString += "\t\t" + line + "\n");
            return finalString + line2;
        }
        //Return empty string if no debuffs
        return "";
    }
    
    /**
     * Formats the afterTurnProtocol() function call
     * @returns {String} The formatted function call
     */
    function formatAfterTurnCode()
    {
        //Ignore all abilities that are not attack or heal abilities
        for (let i = 0; i < abilityTypes.length; i++)
        {
            const type = abilityTypes[i];
            if (type !== "Attack" && type !== "Heal")
            {
                abilityTypes.splice(i, 1);
            }
        }
        
        abilityTypes.filter(type => type === "Attack" || type === "Heal");
        //1 attack ability, may or may not target team
        if (abilityTypes.count("Attack") === 1)
        {
            let index = abilityTypes.indexOf("Attack");
            return "super.afterTurnProtocol({target}, abilityNum == {index});".formatUnicorn({
                target: (abilityTypes[index] === "true") ? "game.getOtherTeam()" : "target",
                index:  index + 1
            });
        }
        
        //All abilities are attack abilities, may or may not target team
        if (abilityTypes.count("Attack") === abilityTypes.length)
        {
            //One ability targets team
            if (abilityTargets.count(true) === 1)
            {
                return "super.afterTurnProtocol({target}, true);".formatUnicorn({
                    target: `(abilityNum == ${abilityTargets.indexOf(true) + 1}) ? game.getOtherTeam() : target`
                });
            }
            
            //2 abilities target team
            else if (abilityTargets.count(true) === 2)
            {
                return "super.afterTurnProtocol({target}, true);".formatUnicorn({
                    target: `(abilityNum == ${abilityTargets.indexOf(false) + 1}) ? target : game.getOtherTeam()`
                });
            }
            
            //No abilities target team
            return "super.afterTurnProtocol(target, true);";
        }
        
        //More than 1 attack ability, but not all. May or may not target team
        return "super.afterTurnProtocol({target}, abilityNum != {index});".formatUnicorn({
            target: (abilityTargets.count(true) > 0) ? `(abilityNum == ${abilityTargets.indexOf(true) + 1}) ? game.getOtherTeam() : target` : "target",
            index:  abilityTypes.indexOf("Heal") + 1
        });
    }
};

//If the website was opened through the WebsiteRunner, send a request to quit
window.onbeforeunload = () => fetch("/quit").then();
window.onunload = () => fetch("/quit").then();
window.onclose = () => fetch("/quit").then();

/**
 * Formats the string similar to Java's printf. Use the syntax shown below.
 * ```
 * "Hello I am {name}".formatUnicorn({
 *      name: "foo"
 *  }); //Produces "Hello I am foo"
 *
 *  ```
 * @param {{}} arguments The values for the placeholders
 * @author StackOverflow
 */
String.prototype.formatUnicorn = function () {
    let e = this.toString();
    if (!arguments.length)
    {
        return e;
    }
    let t = typeof arguments[0], n = "string" === t || "number" === t ? Array.prototype.slice.call(arguments) : arguments[0];
    for (let o in n)
    {
        e = e.replace(new RegExp("\\{" + o + "\\}", "gi"), n[o]);
    }
    return e;
};

/**
 * Converts the string to title case
 */
String.prototype.toTitleCase = function () {
    return this.replace(/\w\S*/g, text => text.charAt(0).toUpperCase() + text.substring(1).toLowerCase());
};

/**
 * Checks if the string is empty
 * @returns {boolean} True if the String contains nothing or only spaces, false otherwise
 */
String.prototype.isEmpty = function () {
    return this.toString().replaceAll(" ", "").length === 0;
};

/**
 * Formats the number to have an underscore every three digits
 * @returns {string} A string containing the formatted number
 */
Number.prototype.formatNumbers = function () {
    let s = (this + "").split(".")[0];
    //Check for decimals
    let decimals = (this + "").split(".")[1];
    let count = s.length - 1;
    let newString = "";
    for (let i = 0; i < s.length; i++)
    {
        newString += s.charAt(i);
        if (count % 3 === 0 && count !== 0)
        {
            newString += ",";
        }
        count--;
    }
    return (newString + ((decimals) ? "." + decimals : "")).replaceAll(",", "_");
};

/**
 * Parses an integer given a String.
 * @param {string} str The String to parse
 * @returns {Number | NaN} The parsed number. If the String contains any characters that are not numeric or contains a dash that is not the first
 * character, return NaN
 */
parseInt = function (str) {
    const _9 = "9".charCodeAt(0);
    const _0 = "0".charCodeAt(0);
    let i = 0;
    for (let char of str)
    {
        if ((i++ !== 0 && char === "-") && (char.charCodeAt(0) > _9 || char.charCodeAt(0) < _0))
        {
            return NaN;
        }
    }
    return Number.parseInt(str, 10);
};

/**
 * Count the number of a certain item in the array
 * @param item {any} The item to count
 * @returns {number} The number of times the item appears in the array
 */
Array.prototype.count = function (item) {
    return this.filter(element => element === item).length;
};

/**
 * Read all buffs from file
 */
function getAllBuffs()
{
    fetch("../Effects/Buffs/Buff key.csv").then((res) => res.blob()).then((res) => {
        res.text().then((res) => res.split("\n").forEach(buff => {
            if (buff.split(",")[1] !== "Rune Shield")
            {
                buffOptions += "<option value=\"{buff}\">{buff}</option>\n".formatUnicorn({buff: buff.split(",")[1]});
            }
        }));
    });
}

/**
 * Read all debuffs from file
 */
function getAllDebuffs()
{
    fetch("../Effects/Debuffs/Debuff key.csv").then((res) => res.blob()).then((res) => {
        res.text().then((res) => res.split("\n").forEach(buff => {
            debuffOptions += "<option value=\"{debuff}\">{debuff}</option>\n".formatUnicorn({debuff: buff.split(",")[1]});
        }));
    });
}

/**
 * Renames all children of the HTMLElement to decrease or increase the effect number by one
 * @param oldNum {Number} The effect number that was removed
 * @param decAbility {boolean} True if the function should focus on renaming ability numbers, false if it should focus on renaming effect numbers.
 * @param decNum {boolean} True if the function should decrease the numbers, false if it should increase the numbers.
 * @param renameSelf {boolean} True if the function should rename the element itself, false otherwise
 * @precondition `this` must be a div
 */
HTMLElement.prototype.renameAllChildrenEffects = function (oldNum, decAbility = false, decNum = true, renameSelf = false) {
    for (let element of this.children)
    {
        //If element is a div, rename the children
        if (element.tagName.toLowerCase() === "div")
        {
            element.renameAllChildrenEffects(oldNum, decAbility || (element.id.includes("header") && element.id.includes("buff")), decNum);
        }
        //Do nothing if element is a label
        if (element.tagName.toLowerCase() === "label" || !element.id)
        {
            continue;
        }
        renameElement(element);
    }
    
    if (renameSelf)
    {
        renameElement(this);
    }
    
    function renameElement(element, oNum = oldNum, dAbility = decAbility, dNum = decNum)
    {
        let newId = element.id;
        
        //Get the index of the number to change
        let index = (!dAbility) ? newId.indexOf("buff") + "buff".length : "ability".length;
        let num = -1;
        let i;
        
        //Get the old number (max 3 digits)
        for (i = 1; i <= 3; i++)
        {
            //Try parsing the number
            let temp = parseInt(newId.substring(index, index + i));
            if (isNaN(temp))
            {
                i--;
                break;
            }
            num = temp;
        }
        
        //Do nothing if the current number is already correct
        if ((num < oNum && dNum) || (num > oNum && !dNum))
        {
            return;
        }
        
        //Create the new ID
        newId = newId.substring(0, index) + ((dNum) ? num - 1 : num + 1) + newId.substring(index + i);
        //Reset the elements ID
        element.id = newId;
        if (element.tagName.toLowerCase().includes("h"))
        {
             element.innerHTML = ((element.innerHTML.includes("span")) ?
                                 element.innerHTML.substring(0, element.innerHTML.indexOf("</span>") + "</span>".length) : "") +
                    newId.substring((element.tagName.includes("4")) ?
                                    "ability_-".length : 0, ((element.id.indexOf("buff") + 1) || index - 3) + 3).toTitleCase()
                    + " " + newId.charAt((element.id.includes("buff") && element.id.includes("header")) ?
                                         newId.indexOf("buff") + 4 : index);
        }
    }
};