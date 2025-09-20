let buffArray = [];
let debuffArray = [];

setBuffArray();
setDebuffArray();

const buffRemoved = {
    "inputType": "select",
    "options":   ["Split_General", "Any Buff", "Split_Buffs"].concat(buffArray),
    "label":     "Effect: "
};

const debuffRemoved = {
    "inputType": "select",
    "options":   ["Split_General", "Any Debuff", "Split_Debuffs"].concat(debuffArray),
    "label":     "Effect: "
};

const buffApplied = buffRemoved;

const debuffApplied = debuffRemoved;

const selfHasEffect = {
    "inputType": "select",
    "options":   buffArray.concat(debuffArray),
    "label":     "Effect: "
};

const targetHasEffect = selfHasEffect;

const selfNotHasEffect = selfHasEffect;

const targetNotHasEffect = selfHasEffect;

const targetUnderHealthRatio = {
    "inputType": "number",
    "min":       "0",
    "max":       "100",
    "label":     "Amount: "
};

const targetOverHealthRatio = targetUnderHealthRatio;

const damageToTargetUnderHealthRatio = targetUnderHealthRatio;

const damageToTargetOverHealthRatio = damageToTargetUnderHealthRatio;

const targetStatUnderSelfStat = {
    "inputType": ["select", "select"],
    "options":   [allStats(), allStats()],
    "label":     ["Target Stat: ", "Self Stat: "]
};

const targetStatOverSelfStat = targetStatUnderSelfStat;

/**
 * Fetches a CSV file containing buff data from the specified path, processes its content,
 * and populates the `buffArray` with specific values extracted from the file.
 * After populating `buffArray`, it triggers the `setSelfHasEffect` function for further processing.
 */
function setBuffArray()
{
    fetch("../Effects/Buffs/Buff key.csv").then((res) => res.blob()).then((res) => {
        res.text().then((res) => res.split("\n").forEach((buff) => {
            buffArray.push(buff.split(",")[1]);
        })).then(() => setSelfHasEffect());
    });
}

/**
 * Fetches debuff data from a CSV file, processes the data, and populates a global array with debuff values.
 * Subsequent to populating the array, triggers the `setSelfHasEffect` method.
 */
function setDebuffArray()
{
    fetch("../Effects/Debuffs/Debuff key.csv").then((res) => res.blob()).then((res) => {
        res.text().then((res) => res.split("\n").forEach((debuff) => {
            debuffArray.push(debuff.split(",")[1]);
        })).then(() => setSelfHasEffect());
    });
}

/**
 * Updates the options for `selfHasEffect`, `buffRemoved`, and `debuffRemoved` by
 * combining predefined arrays and other relevant effect arrays such as `buffArray`
 * and `debuffArray`.
 */
function setSelfHasEffect()
{
    selfHasEffect.options = ["Split_General", "Any", "Any Buff", "Any Debuff", "Split_Buffs"].concat(buffArray.concat(["Split_Debuffs"].concat(debuffArray)));
    buffRemoved.options = ["Split_General", "Any Buff", "Split_Buffs"].concat(buffArray);
    debuffRemoved.options = ["Split_General", "Any Debuff", "Split_Debuffs"].concat(debuffArray);
}

/**
 * Retrieves a list of all available stats.
 *
 * @returns {string[]} An array of stat names including DEF, ATK, SPD, MAX_HP, CURRENT_HP, CRIT_RATE, CRIT_DMG, RES, and ACC.
 */
function allStats()
{
    return ["DEF", "ATK", "SPD", "MAX_HP", "CURRENT_HP", "CRIT_RATE", "CRIT_DMG", "RES", "ACC"];
}

/**
 * Creates a container with additional input elements based on the given conditions and input types.
 *
 * @param {Object} conditionObj - An object containing configuration for the inputs.
 *                                Properties may include `inputType` (string or array of strings defining input types),
 *                                `min`, `max`, `label`, and `options` (arrays specifying corresponding attributes for each input).
 * @param {string} baseName - A base name used to generate the id for the container element.
 *
 * @returns {HTMLDivElement} A div element containing the generated input elements with the specified configurations.
 */
function createExtraInputs(conditionObj, baseName)
{
    //Create the div
    let div = document.createElement("div");
    
    //Set the properties
    div.id = `${baseName}-extra-inputs`;
    div.classList.add("extra-inputs");
    
    let inputTypes = conditionObj.inputType;
    
    //Format the inputs
    if (typeof (inputTypes) === "string")
    {
        div = createExtraInput(div, inputTypes, conditionObj.min, conditionObj.max, conditionObj.label, conditionObj.options);
    }
    else if (typeof (inputTypes) === "object")
    {
        for (let i = 0; i < inputTypes.length; i++)
        {
            div = createExtraInput(div, inputTypes[i], conditionObj.min?.[i], conditionObj.max?.[i], conditionObj.label?.[i], conditionObj.options?.[i]);
        }
    }
    
    return div;
}

/**
 * Creates an additional input element and attaches it to the specified container element with an associated label.
 *
 * @param {HTMLElement} div The HTML container element to which the input and label will be appended.
 * @param {string} inputType The type of input element to create (e.g., "number", "select").
 * @param {number} min The minimum value for the input element if the type is "number".
 * @param {number} max The maximum value for the input element if the type is "number".
 * @param {string} label The text for the input's associated label.
 * @param {[string]} options An array of options for the input if the type is "select". Each option can include "Split_\<name\>" for grouping.
 *
 * @returns {HTMLElement} The container element with the newly appended input and label.
 */
function createExtraInput(div, inputType, min, max, label, options)
{
    let input = document.createElement((inputType === "number") ? "input" : inputType);
    
    input.id = `${div.id}-${label.toLowerCase().replaceAll(" ", "-").substring(0, label.length - 2)}`;
    
    if (inputType === "number")
    {
        input.type = "number";
    }
    
    switch (inputType)
    {
        case "number":
            input.min = min;
            input.max = max;
            break;
        case "select":
            let optionsString = "";
            for (let option of options)
            {
                //Check if the current option is the title of a group
                if (option.includes("Split") && !optionsString.isEmpty())
                {
                    optionsString += `</optgroup>`;
                }
                if (option.includes("Split"))
                {
                    optionsString += `<optgroup label='${option.substring(option.indexOf("Split") + "Split_".length)}'>`;
                    continue;
                }
                optionsString += `<option value='${option}'>${option}</option>`;
            }
            input.innerHTML = optionsString;
            break;
    }
    //Create the label
    let lbl = document.createElement("label");
    lbl.for = input.id;
    lbl.innerHTML = label;
    
    div.appendChild(lbl);
    div.appendChild(input);
    
    return div;
}