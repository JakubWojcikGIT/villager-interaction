package net.pawel.villagermod.utils;

import net.pawel.villagermod.enums.TraitType;

import java.util.EnumMap;
import java.util.Map;

public class VillagerTraits {
    private final Map<TraitType, Allele> traits = new EnumMap<>(TraitType.class);

    public VillagerTraits() {
        for (TraitType type : TraitType.values()) {
            traits.put(type, Allele.random());
        }
    }

    public VillagerTraits(VillagerTraits parent1, VillagerTraits parent2) {
        for (TraitType type : TraitType.values()) {
            Allele allele1 = parent1.getAllele(type);
            Allele allele2 = parent2.getAllele(type);
            traits.put(type, Allele.inherit(allele1, allele2));
        }
    }

    public Allele getAllele(TraitType type) {
        return traits.get(type);
    }

    public String describeTrait(TraitType type) {
        return this.traits.get(type).getTrait(type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Traits:\n");
        for (TraitType type : TraitType.values()) {
            sb.append(type.name())
                    .append(": ")
                    .append(describeTrait(type))
                    .append(" (")
                    .append(getAllele(type))
                    .append(")\n");
        }
        return sb.toString();
    }

    public Map<TraitType, Allele> getTraits() {
        return traits;
    }
}

