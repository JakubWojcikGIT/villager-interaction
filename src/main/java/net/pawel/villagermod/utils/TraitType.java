package net.pawel.villagermod.utils;

public enum TraitType {
    AGGRESSION("Aggressive", "Peaceful"),
    AGILITY("Tanky", "Agile");

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

