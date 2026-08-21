package io.redspace.simpleblood.registry;

import io.redspace.simpleblood.IronsSimpleBloodMod;
import io.redspace.simpleblood.data.BleedStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, IronsSimpleBloodMod.MODID);

    public static final Supplier<AttachmentType<BleedStack>> BLEED_STACK = ATTACHMENT_TYPES.register(
            "bleed_stack",
            () -> AttachmentType.builder(BleedStack::new).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
