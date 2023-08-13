package com.mmf.plantpal.models;

public class Plant extends Item {

    private String species;
    private String careInstructions;
    private String growHabit;

    public Plant() {
    }


    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getCareInstructions() {
        return careInstructions;
    }

    public void setCareInstructions(String careInstructions) {
        this.careInstructions = careInstructions;
    }

    public String getGrowHabit() {
        return growHabit;
    }

    public void setGrowHabit(String growHabit) {
        this.growHabit = growHabit;
    }
}

