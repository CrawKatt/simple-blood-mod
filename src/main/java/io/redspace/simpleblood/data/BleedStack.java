package io.redspace.simpleblood.data;

import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.WeakHashMap;

public class BleedStack {
    private static final Map<Entity, BleedStack> PENDING = new WeakHashMap<>();

    private float totalDamage;

    public static BleedStack get(Entity entity) {
        BleedStack stack = PENDING.get(entity);
        if (stack == null) {
            stack = new BleedStack();
            PENDING.put(entity, stack);
        }
        return stack;
    }

    public static boolean has(Entity entity) {
        return PENDING.containsKey(entity);
    }

    public static BleedStack remove(Entity entity) {
        return PENDING.remove(entity);
    }

    public void add(float damage) {
        totalDamage += damage;
    }

    public float totalDamage() {
        return totalDamage;
    }
}
