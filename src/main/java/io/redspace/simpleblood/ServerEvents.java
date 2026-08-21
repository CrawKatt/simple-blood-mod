package io.redspace.simpleblood;

import io.redspace.simpleblood.client.particles.BloodParticleOptions;
import io.redspace.simpleblood.data.BloodConfig;
import io.redspace.simpleblood.data.BleedStack;
import io.redspace.simpleblood.registry.AttachmentRegistry;
import io.redspace.simpleblood.registry.BloodRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void handleBleed(EntityTickEvent.Post event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide() || !entity.hasData(AttachmentRegistry.BLEED_STACK)) {
            return;
        }

        BleedStack pending = entity.removeData(AttachmentRegistry.BLEED_STACK);
        if (pending == null) {
            return;
        }

        BloodConfig config = bloodConfigFor(entity);
        if (config == null) {
            return;
        }

        spawnBleed(entity, config, pending.totalDamage());
    }

    @SubscribeEvent
    public static void trackBleedingDamage(LivingDamageEvent.Pre event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide() || bloodConfigFor(entity) == null) {
            return;
        }
event.getContainer().setPostAttackInvulnerabilityTicks(0);
        float damage = event.getContainer().getNewDamage();
        if (damage <= 0 || damage == Float.MAX_VALUE) {
            return;
        }

        entity.getData(AttachmentRegistry.BLEED_STACK).add(damage);
    }

    private static void spawnBleed(Entity entity, BloodConfig config, float damage) {
        if (damage <= config.minDamage()) {
            return;
        }

        damage = Math.min(damage, 2000);
        var level = entity.level();
        AABB aabb = entity.isMultipartEntity() ? entity.getParts()[entity.getRandom().nextInt(entity.getParts().length)].getBoundingBox() : entity.getBoundingBox();
        Vec3 vec = aabb.getCenter();
        int count = (int) (damage / config.minDamage())
                + level.random.nextIntBetweenInclusive(0, (int) (2 * (damage - config.minDamage()) / config.maxDamage()));
        double speed = config.scaledBaseSpeed() + count * config.scaledSpeedPerParticle();
        double bbShove = Math.max(aabb.getXsize() * 0.5 - 0.5, 0);
        double scale = (aabb.getXsize() + 2) / 3f;
        spawnParticles(
                level,
                new BloodParticleOptions(config.color(), (float) scale, config.isGraphic()),
                vec.x,
                vec.y + aabb.getYsize() * 0.5,
                vec.z,
                count,
                0.05 + bbShove,
                0.1,
                0.05 + bbShove,
                speed,
                true
        );
    }

    @Nullable
    private static BloodConfig bloodConfigFor(Entity entity) {
        for (BloodConfig config : BloodRegistry.getAll()) {
            if (entity.getType().is(config.entityTag())) {
                return config;
            }
        }
        return null;
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> ((ServerLevel) level).sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
    }
}
