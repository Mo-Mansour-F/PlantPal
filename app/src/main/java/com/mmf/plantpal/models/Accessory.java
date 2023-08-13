package com.mmf.plantpal.models;

public class Accessory extends Item {
    private String usageInstructions;
    private String type;

    public Accessory() {
    }

    public String getUsageInstructions() {
        return usageInstructions;
    }

    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
