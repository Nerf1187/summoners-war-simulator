/**
 * Creates and returns a string representing a conditional div element with specified attributes.
 *
 * @param {number} effectLength - The effect length used to define the condition element.
 * @param {Object} dynamicDivName - The dynamic HTML element name providing a base id for naming.
 * @param {boolean} isBuff - Indicates whether the condition pertains to a buff (true) or a debuff (false).
 *
 * @returns {string} A string containing the HTML structure for the conditional div element.
 */
function createConditionalDivString(effectLength, dynamicDivName, isBuff)
{
    //Add each possible condition to the selector
    let conditions = "";
    for (let condition in effectConditions)
    {
        conditions += "<option value=" + condition + ">" + condition + "</option>";
    }
    
    let baseName = dynamicDivName.id + (isBuff ? "-buff" : "-debuff") + effectLength + "-condition";
    return `
        <div id='` + baseName + `1' class='condition'>
            <label for='` + baseName + `1'>Condition: </label>
            <select id='` + baseName + `1-selector' onchange='addExtraInputs(this.value, this)'>
                '` + conditions + `'
            </select>
            <button class="buff-button" onclick="this.style.display = \'none\'; addCondition(this, \'` + baseName + `\');">Add Condition</button>
        </div>`;
}

/**
 * Adds a new condition dynamically to the parent element of the specified button.
 * This method appends a new condition input structure and allows chaining of conditions.
 *
 * @param {HTMLElement} button - The button element that triggered the addition of a new condition.
 * @param {string} baseName - The base name used to generate unique identifiers for new elements.
 *
 * @returns {boolean} Returns true upon successful addition of the condition.
 */
function addCondition(button, baseName)
{
    //Get the parent conditions div
    let conditionDiv = button.parentElement.parentElement;
    let newConditionLength = conditionDiv.children.length + 1;
    
    //Create the and-or selector
    let andOrSelector = document.createElement("select");
    andOrSelector.id = baseName + (newConditionLength - 1) + "-andor";
    andOrSelector.innerHTML = "<option value=\"and\">AND</option><option value=\"or\">OR</option>";
    button.parentElement.appendChild(andOrSelector);
    
    //Save the current values of each element
    let currentValues = getAllInputVals(conditionDiv);
    
    //Add each possible condition to the selector
    let conditions = "";
    for (let condition in effectConditions)
    {
        conditions += "<option value=" + condition + ">" + condition + "</option>";
    }
    
    let html = `
        <div id='{baseName}{newConditionLength}' class='condition'>
            <label for='`.formatUnicorn({
        baseName:           baseName,
        newConditionLength: newConditionLength
    }) + baseName + newConditionLength + `'>Condition: </label>
            <select id='` + baseName + newConditionLength + `-selector' onchange='addExtraInputs(this.value, this)'>
                '` + conditions + `'
            </select>
            <button class="buff-button" onclick="this.style.display = \'none\'; addCondition(this, \'` + baseName + `\');">Add condition</button>
        </div>`;
    
    conditionDiv.innerHTML += html;
    
    //Re-set each element's value to what it was before altering the HTML
    for (let [key, val] of Object.entries(currentValues))
    {
        document.getElementById(key).value = val;
    }
    
    return true;
}

/**
 * Dynamically adds extra input elements based on a given condition and its associated selector.
 * If no relevant condition is found, it removes the existing extra input elements if they are not buttons.
 *
 * @param {string} condition The name of the condition used to determine what extra inputs to add.
 * @param {HTMLElement} conditionSelector The DOM element representing the selector associated with the condition.
 */
function addExtraInputs(condition, conditionSelector)
{
    let obj = effectConditions[condition];
    
    //Create new inputs only if the condition needs them
    if (obj)
    {
        //Create the elements if needed
        let div = createExtraInputs(obj, conditionSelector.id);
        
        //Remove the existing elements if they are not buttons
        if (conditionSelector.nextElementSibling?.tagName !== "BUTTON")
        {
            conditionSelector.nextElementSibling.remove();
        }
        
        //Insert the new inputs
        conditionSelector.insertAdjacentElement("afterend", div);
    }
    else //Remove any extra inputs if they are not needed
    {
        if (conditionSelector.nextElementSibling?.tagName !== "BUTTON")
        {
            conditionSelector.nextElementSibling.remove();
        }
    }
}

/**
 * Recursively retrieves the values of all input and select elements within a given div element and its child div elements.
 *
 * @param {HTMLElement} div The parent div element to be scanned for input and select elements.
 *
 * @returns {Object} An object containing the IDs of input and select elements as keys and their corresponding values.
 */
function getAllInputVals(div)
{
    let currentValues = {};
    for (let child of div.children)
    {
        switch (child.tagName)
        {
            case "INPUT":
            case "SELECT":
                currentValues[child.id] = child.value;
                break;
            case "DIV":
                for (let [key, val] of Object.entries(getAllInputVals(child)))
                {
                    currentValues[key] = val;
                }
        }
        
        if (child.tagName === "INPUT" || child.tagName === "SELECT")
        {
            currentValues[child.id] = child.value;
        }
    }
    return currentValues;
}