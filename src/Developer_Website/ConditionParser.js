/**
 * This function formats all information needed for applying an effect. This will take every condition into account and return the appropriate statements.
 *
 * @param conditionDiv {HTMLDivElement} The div element containing the condition information
 * @param {Object} effect An object containing information about the effect to apply
 * @param {string} effect.effect The type of effect to apply (e.g. Shield, Provoke)
 * @param {string|null} effect.chance The chance of successfully applying the effect
 * @param {number|null} effect.amount The magnitude of the effect (e.g. shield health)
 * @param {number|null} effect.turns The number of turns the effect lasts
 * @param {boolean|null} effect.immunityCheck Whether to bypass immunity checks
 *
 * @returns {{beforeTurnStatements: string[], afterTurnStatements: string[], ifStatement: string, applyEffectStatement: string, canOverwriteForTeamAttack: boolean}} An object containing 5 arguments<br><br>
 * - beforeTurnStatements: An array containing Strings that should be placed before the turn begins<br>
 * - afterTurnStatements: An array containing String that should be placed after the turn ends but before the conditions<br>
 * - ifStatement: The if statement itself. This is formatted as <code>if (\*conditions\*)</code>. Note this does not include the braces (<code>{}</code>)<br>
 * - applyEffectStatement: The statement for applying the effect itself. This should be placed after <code>ifStatement</code>. REMEMBER TO ADD BRACES (<code>{}</code>)<br>
 * - canOverwriteForTeamAttack: This marks whether the information returned by this function can be changed for team targets. If false, the information
 * should not be changed. If true, then all occurrences of <code>'target'</code> can be changed to <code>'m'</code> to be placed inside a for loop.
 */
function createIfStatements(conditionDiv, effect)
{
    //Initialize variables
    let beforeTurnStatements = [];
    let afterTurnStatements = [];
    let ifStatement = "if (";
    let canOverwriteForTeamAttack = false;
    
    //For each condition, format the appropriate statement(s)
    for (let condition of conditionDiv.children)
    {
        let baseId = condition.id;
        let b = get(baseId + "-selector");
        
        switch (b)
        {
            case "enemyKilled":
                beforeTurnStatements.push(`Team other = game.getOtherTeam();
                int numDeadBefore = other.numDead();`);
                afterTurnStatements.push("int numDeadAfter = other.numDead();");
                ifStatement += "numDeadAfter > numDeadBefore";
                break;
            case "targetWasCrit":
                ifStatement += "target.wasCrit()";
                canOverwriteForTeamAttack = true;
                break;
            case "targetNotCrit":
                ifStatement += "!target.wasCrit()";
                canOverwriteForTeamAttack = true;
                break;
            case "buffRemoved":
                beforeTurnStatements.push("int numBuffsBefore = target.getAppliedBuffs().size();");
                afterTurnStatements.push("int numBuffsAfter = target.getAppliedBuffs().size();");
                ifStatement += "numBuffsAfter < numBuffsBefore";
                canOverwriteForTeamAttack = true;
                break;
            case "debuffRemoved":
                beforeTurnStatements.push("int numDebuffsBefore = target.getAppliedDebuffs().size();");
                afterTurnStatements.push("int numDebuffsAfter = target.getAppliedDebuffs().size();");
                ifStatement += "numDebuffsAfter < numDebuffsBefore";
                canOverwriteForTeamAttack = true;
                break;
            case "buffApplied":
                if (!buffArray.includes(get(baseId + "-selector-extra-inputs-effect")))
                {
                    beforeTurnStatements.push("int numBuffsBefore = target.getAppliedBuffs().size();");
                    afterTurnStatements.push("int numBuffsAfter = target.getAppliedBuffs().size();");
                    ifStatement += "numBuffsAfter > numBuffsBefore";
                }
                else
                {
                    ifStatement += "target.containsBuff(BuffEffect." + get(baseId + "-selector-extra-inputs-effect").toEnumCase() + ")";
                }
                canOverwriteForTeamAttack = true;
                break;
            case "debuffApplied":
                if (!debuffArray.includes(get(baseId + "-selector-extra-inputs-effect")))
                {
                    beforeTurnStatements.push("int numDebuffsBefore = target.getAppliedDebuffs().size();");
                    afterTurnStatements.push("int numDebuffsAfter = target.getAppliedDebuffs().size();");
                    ifStatement += "numDebuffsAfter > numDebuffsBefore";
                }
                else
                {
                    ifStatement += "target.containsDebuff(DebuffEffect." + get(baseId + "-selector-extra-inputs-effect").toEnumCase() + ")";
                }
                break;
            case "targetAttackBarReduced":
                beforeTurnStatements.push("double atkBarBefore = target.getAtkBar();");
                afterTurnStatements.push("double atkBarAfter = target.getAtkBar();");
                ifStatement += "atkBarBefore > arkBarAfter";
                canOverwriteForTeamAttack = true;
                break;
            case "targetIsSelf":
                ifStatement += "target.equals(this)";
                canOverwriteForTeamAttack = true;
                break;
            case "targetIsNotSelf":
                ifStatement += "!target.equals(this);";
                canOverwriteForTeamAttack = true;
                break;
            case "selfHasEffect":
                let selfEffect = get(baseId + "-selector-extra-inputs-effect");
                if (buffArray.includes(selfEffect))
                {
                    ifStatement += `this.containsBuff(BuffEffect.${selfEffect.toEnumCase()})`;
                }
                else if (debuffArray.includes(selfEffect))
                {
                    ifStatement += `this.containsDebuff(DebuffEffect.${selfEffect.toEnumCase()})`;
                }
                else
                {
                    switch (selfEffect)
                    {
                        case "Any Buff":
                            ifStatement += "this.getAppliedBuffs().size() > 0";
                            break;
                        case "Any Debuff":
                            ifStatement += "this.getAppliedDebuffs().size() > 0";
                            break;
                        default:
                            ifStatement += "(this.getAppliedBuffs().size() > 0 || this.getAppliedDebuffs() > 0)";
                    }
                }
                break;
            case "targetHasEffect":
                let targetEffect = get(baseId + "-selector-extra-inputs-effect");
                if (buffArray.includes(targetEffect))
                {
                    ifStatement += `target.containsBuff(BuffEffect.${targetEffect.toEnumCase()})`;
                }
                else if (debuffArray.includes(targetEffect))
                {
                    ifStatement += `target.containsDebuff(DebuffEffect.${targetEffect.toEnumCase()})`;
                }
                else
                {
                    switch (targetEffect)
                    {
                        case "Any Buff":
                            ifStatement += "target.getAppliedBuffs().size() > 0";
                            break;
                        case "Any Debuff":
                            ifStatement += "target.getAppliedDebuffs().size() > 0";
                            break;
                        default:
                            ifStatement += "(target.getAppliedBuffs().size() > 0 || target.getAppliedDebuffs() > 0)";
                    }
                }
                canOverwriteForTeamAttack = true;
                break;
            case "selfNotHasEffect":
                let selfNotEffect = get(baseId + "-selector-extra-inputs-effect");
                if (buffArray.includes(selfNotEffect))
                {
                    ifStatement += `!this.containsBuff(BuffEffect.${selfNotEffect.toEnumCase()})`;
                }
                else if (debuffArray.includes(selfNotEffect))
                {
                    ifStatement += `!this.containsDebuff(DebuffEffect.${selfNotEffect.toEnumCase()})`;
                }
                else
                {
                    switch (selfNotEffect)
                    {
                        case "Any Buff":
                            ifStatement += "this.getAppliedBuffs().size() == 0";
                            break;
                        case "Any Debuff":
                            ifStatement += "this.getAppliedDebuffs().size() == 0";
                            break;
                        default:
                            ifStatement += "(this.getAppliedBuffs().size() == 0 && this.getAppliedDebuffs() == 0)";
                    }
                }
                break;
            case "targetNotHasEffect":
                let targetNotEffect = get(baseId + "-selector-extra-inputs-effect");
                if (buffArray.includes(targetNotEffect))
                {
                    ifStatement += `!target.containsBuff(BuffEffect.${targetNotEffect.toEnumCase()})`;
                }
                else if (debuffArray.includes(targetNotEffect))
                {
                    ifStatement += `!target.containsDebuff(DebuffEffect.${targetNotEffect.toEnumCase()})`;
                }
                else
                {
                    switch (targetNotEffect)
                    {
                        case "Any Buff":
                            ifStatement += "target.getAppliedBuffs().size() == 0";
                            break;
                        case "Any Debuff":
                            ifStatement += "target.getAppliedDebuffs().size() == 0";
                            break;
                        default:
                            ifStatement += "(target.getAppliedBuffs().size() == 0 && target.getAppliedDebuffs() == 0)";
                    }
                }
                canOverwriteForTeamAttack = true;
                break;
            case "targetUnderHealthRatio":
                let underRatio = get(baseId + "-selector-extra-inputs-amount");
                ifStatement += `target.getHpRatio() < ${underRatio}`;
                canOverwriteForTeamAttack = true;
                break;
            case "targetOverHealthRatio":
                let overRatio = get(baseId + "-selector-extra-inputs-amount");
                ifStatement += `target.getHpRatio() > ${overRatio}`;
                canOverwriteForTeamAttack = true;
                break;
            case "damageToTargetUnderHealthRatio":
                let takenUnder = get(baseId + "-selector-extra-inputs-amount");
                ifStatement += `target.getDmgTakenThisTurn() < target.getMaxHp() * ${takenUnder / 100}`;
                canOverwriteForTeamAttack = true;
                break;
            case "damageToTargetOverHealthRatio":
                let takenOver = get(baseId + "-selector-extra-inputs-amount");
                ifStatement += `target.getDmgTakenThisTurn() > target.getMaxHp() * ${takenOver / 100}`;
                canOverwriteForTeamAttack = true;
                break;
            case "targetStatUnderSelfStat":
                let targetUnder = get(baseId + "-selector-extra-inputs-target-stat");
                let selfUnder = get(baseId + "-selector-extra-inputs-self-stat");
                
                switch (targetUnder)
                {
                    case "ATK":
                    case "DEF":
                    case "SPD":
                    case "MAX_HP":
                    case "CURRENT_HP":
                    case "CRIT_RATE":
                    case "CRIT_DMG":
                        targetUnder = `target.get${targetUnder.replaceAll("_", " ").toTitleCase().replaceAll(" ", "")}()`;
                        break;
                    case "RES":
                        targetUnder = "target.getResistance()";
                        break;
                    case "ACC":
                        targetUnder = "target.getAccuracy()";
                        break;
                }
                
                switch (selfUnder)
                {
                    case "ATK":
                    case "DEF":
                    case "SPD":
                    case "MAX_HP":
                    case "CURRENT_HP":
                    case "CRIT_RATE":
                    case "CRIT_DMG":
                        selfUnder = `this.get${selfUnder.replaceAll("_", " ").toTitleCase().replaceAll(" ", "")}()`;
                        break;
                    case "RES":
                        selfUnder = "this.getResistance()";
                        break;
                    case "ACC":
                        selfUnder = "this.getAccuracy()";
                        break;
                }
                
                ifStatement += `${targetUnder} < ${selfUnder}`;
                canOverwriteForTeamAttack = true;
                break;
            case "targetStatOverSelfStat":
                let targetOver = get(baseId + "-selector-extra-inputs-target-stat");
                let selfOver = get(baseId + "-selector-extra-inputs-self-stat");
                
                switch (targetOver)
                {
                    case "ATK":
                    case "DEF":
                    case "SPD":
                    case "MAX_HP":
                    case "CURRENT_HP":
                    case "CRIT_RATE":
                    case "CRIT_DMG":
                        targetOver = `target.get${targetOver.replaceAll("_", " ").toTitleCase().replaceAll(" ", "")}()`;
                        break;
                    case "RES":
                        targetOver = "target.getResistance()";
                        break;
                    case "ACC":
                        targetOver = "target.getAccuracy()";
                        break;
                }
                
                switch (selfOver)
                {
                    case "ATK":
                    case "DEF":
                    case "SPD":
                    case "MAX_HP":
                    case "CURRENT_HP":
                    case "CRIT_RATE":
                    case "CRIT_DMG":
                        selfOver = `this.get${selfOver.replaceAll("_", " ").toTitleCase().replaceAll(" ", "")}()`;
                        break;
                    case "RES":
                        selfOver = "this.getResistance()";
                        break;
                    case "ACC":
                        selfOver = "this.getAccuracy()";
                        break;
                }
                
                ifStatement += `${targetOver} > ${selfOver}`;
                canOverwriteForTeamAttack = true;
                break;
        }
        
        let andor = get(baseId + "-andor");
        
        if (andor)
        {
            ifStatement += ((andor === "and") ? " && " : " || ");
        }
    }
    
    //Add the last parenthesis.
    ifStatement += ")";
    
    return {
        beforeTurnStatements:      beforeTurnStatements,
        afterTurnStatements:       afterTurnStatements,
        ifStatement:               ifStatement,
        applyEffectStatement:      createApplyEffectStatement(effect),
        canOverwriteForTeamAttack: canOverwriteForTeamAttack
    };
}

/**
 * Formats a string representation of the statement that applies a specified effect to a target.
 *
 * @param {Object} effectInfo - An object containing information about the effect to be applied.
 * @param {string} effectInfo.effect - The type of effect to apply (e.g., "Shield", "Provoke", etc.).
 * @param {number|null} effectInfo.chance - The chance of successfully applying the effect.
 * @param {number|null} effectInfo.amount - The magnitude of the effect (e.g., the shield health, attack bar modification, etc.).
 * @param {number|null} effectInfo.turns - The number of turns the effect lasts.
 * @param {boolean|null} effectInfo.immunityCheck - Whether to bypass immunity when applying the effect.
 *
 * @return {string} The generated string representation of the statement to apply the effect. Returns an error message if the effect type is unknown.
 */
function createApplyEffectStatement(effectInfo)
{
    //Initialize variables
    let effect = effectInfo["effect"];
    let chance = effectInfo["chance"];
    let amount = effectInfo["amount"];
    let turns = effectInfo["turns"];
    let immunityCheck = effectInfo["immunityCheck"];
    
    if (buffArray.includes(effect))
    {
        switch (effect)
        {
            case "Shield":
                return `target.addAppliedBuff(new Shield(${amount}, ${turns}), ${chance}, this);`;
            case "Buff Steal":
            case "Remove Debuff":
                let returnVal = "";
                for (let i = 0; i < amount; i++)
                {
                    returnVal += `target.addAppliedBuff(BuffEffect.${effect.toEnumCase()}, ${chance}, this);`;
                }
                return returnVal;
            case "Increase Atk Bar":
                return `target.addAppliedBuff(new IncAtkBar(${amount}), ${chance}, this);`; //TODO Include other buffs here
            case "Cleanse":
                return `target.addAppliedBuff(BuffEffect.CLEANSE, 0, this);`;
            case "Defend":
            case "Threat":
                return `target.addAppliedBuff(new ${effect}(${turns}, this), turns, this);`;
            default:
                return `target.addAppliedBuff(BuffEffect.${effect.toEnumCase()}, ${chance}, this);`;
        }
    }
    else if (debuffArray.includes(effect))
    {
        let immunity = immunityCheck ? "Guaranteed" : "";
        switch (effect)
        {
            case "Dec Atk Bar":
                return `target.add${immunity}AppliedDebuff(new DecAtkBar(${amount}), ${chance}, this);`;
            case "Provoke":
                return `target.add${immunity}AppliedDebuff(new Provoke(${turns}, this), ${chance}, this);`;
            case "Shorten Buff":
                return `target.add${immunity}AppliedDebuff(new ShortenBuff(${turns}), ${chance}, 0, this);`;
            default:
                return `target.add${immunity}AppliedDebuff(DebuffEffect.${effect.toEnumCase()}, ${chance}, ${turns}, this);`;
        }
    }
    else
    {
        console.log("%cERROR: Unknown effect", "color: red; font-weight: bold;");
        return "";
    }
}