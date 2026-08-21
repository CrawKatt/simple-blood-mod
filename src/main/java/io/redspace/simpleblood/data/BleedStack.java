package io.redspace.simpleblood.data;

public class BleedStack {
    private float totalDamage;

    public void add(float damage) {
        totalDamage += damage;
    }

    public float totalDamage() {
        return totalDamage;
    }
}
