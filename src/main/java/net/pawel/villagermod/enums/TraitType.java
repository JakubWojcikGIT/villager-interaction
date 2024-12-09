package net.pawel.villagermod.enums;

public enum TraitType {
    AGGRESSION("Aggressive", "Peaceful"),
    AGILITY("Tanky", "Agile"),
    SOCIABILITY("Pack Member", "Lone Wolf"),
    COURAGE("Brave", "Cowardly"),
    INTELLIGENCE("Simple-Minded", "Clever"),
    CURIOSITY("Curious", "None"),
    STRENGTH("None", "Strong"),
    LEADERSHIP("None", "Leader"),
    SPEED("Heavy", "Swift"),
    NIGHT_VISION("None", "Night Owl");

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

