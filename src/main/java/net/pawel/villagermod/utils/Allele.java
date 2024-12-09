package net.pawel.villagermod.utils;

import net.pawel.villagermod.enums.TraitType;

public class Allele {
    private final char first;
    private final char second;

    public Allele(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean isDominant() {
        return first == 'A' || second == 'A';
    }

    public String getTrait(TraitType type) {
        return isDominant() ? type.getDominantTrait() : type.getRecessiveTrait();
    }

    public static Allele random() {
        return new Allele(randomAllele(), randomAllele());
    }

    private static char randomAllele() {
        return Math.random() < 0.5 ? 'A' : 'a';
    }

    public static Allele inherit(Allele parent1, Allele parent2) {
        char fromParent1 = Math.random() < 0.5 ? parent1.first : parent1.second;
        char fromParent2 = Math.random() < 0.5 ? parent2.first : parent2.second;
        return new Allele(fromParent1, fromParent2);
    }

    @Override
    public String toString() {
        return "" + first + second;
    }
}

