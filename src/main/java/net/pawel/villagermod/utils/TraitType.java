package net.pawel.villagermod.utils;

public enum TraitType {
    AGGRESSION("Aggressive", "Peaceful"), // Aggressive: więcej Attack Damage, mniej Knockback Resistance; Peaceful: większa Vision Range, szybsza regeneracja zdrowia.
    AGILITY("Tanky", "Agile"), // Tanky: więcej HP i Knockback Resistance, mniejsza Movement Speed; Agile: większa Movement Speed, mniej HP.
    SOCIABILITY("Pack Member", "Lone Wolf"), // Lone Wolf: buffy solo, debuffy w grupie; Pack Member: buffy w grupie, debuffy solo
    COURAGE("Brave", "Cowardly"), // Brave: większa Knockback Resistance, odporność na strach; Cowardly: szybsza Movement Speed, próba ucieczki?
    INTELLIGENCE("Simple-Minded", "Clever"), // Simple-Minded: nie mam pomysłu xD; Clever: lekka regeneracja zdrowia w walce
    CURIOSITY("Curious", "None"), // Curious: większa Vision Range; None: brak efektu.
    STRENGTH("None", "Strong"), // Strong: więcej Attack Damage; None: brak efektu.
    LEADERSHIP("None", "Leader"), // Leader: bonusy do Max Health dla siebie i Attack Damage dla grupy w zasięgu; None: brak efektu.
    SPEED("Heavy", "Swift"), // Swift: większa Movement Speed, mniejsza Knockback Resistance; Heavy: większa Knockback Resistance, mniejsza Movement Speed.
    NIGHT_VISION("None", "Night Owl"); // Night Owl: większa Vision Range w nocy; None: brak efektu.





    private final String dominantTrait;
    private final String recessiveTrait;

    TraitType(String dominantTrait, String recessiveTrait) {
        this.dominantTrait = dominantTrait;
        this.recessiveTrait = recessiveTrait;
    }

    public String getDominantTrait() {
        return dominantTrait;
    }

    public String getRecessiveTrait() {
        return recessiveTrait;
    }
}

