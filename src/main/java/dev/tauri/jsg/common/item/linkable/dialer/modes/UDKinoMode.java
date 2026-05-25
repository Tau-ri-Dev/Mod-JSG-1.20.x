package dev.tauri.jsg.common.item.linkable.dialer.modes;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.client.renderer.item.dialer.IUniverseDialerScreen;
import dev.tauri.jsg.client.renderer.item.dialer.screen.UDKinoScreen;
import dev.tauri.jsg.common.item.linkable.dialer.UniverseDialerMode;
import dev.tauri.jsg.common.registry.JSGEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UDKinoMode extends UniverseDialerMode {
    public static final String C_LINKED_KINO = "kino";

    public UDKinoMode() {
        super(JSG.rl("kino"), "item.jsg.universe_dialer.mode_kino");
    }

    private final UDKinoScreen screen = new UDKinoScreen();

    @Override
    public IUniverseDialerScreen getScreen() {
        return screen;
    }

    @Override
    public boolean onUse(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, boolean shift) {
        var ray = rayTraceKino(player, 5);
        if (ray != null) {
            compound.putInt(C_LINKED_KINO, ray.getEntity().getId());
            return true;
        }
        return super.onUse(compound, stack, world, player, hand, shift);
    }

    // on server
    @Override
    public void keyPressed(CompoundTag compound, ItemStack stack, Level world, Player player, InteractionHand hand, char keyCode, boolean backspace, boolean shift, boolean alt, boolean ctrl) {
        super.keyPressed(compound, stack, world, player, hand, keyCode, backspace, shift, alt, ctrl);
        // TODO(Mine): handle Kino control
    }

    @Nullable
    public static EntityHitResult rayTraceKino(@Nullable Entity e, double reach) {
        if (e == null) {
            return null;
        } else {
            var vec3 = e.getEyePosition(1);
            var vec31 = e.getViewVector(1);
            var vec32 = vec3.add(vec31.x * reach, vec31.y * reach, vec31.z * reach);
            var aabb = e.getBoundingBox().expandTowards(vec31.scale(reach)).inflate(1.0D, 1.0D, 1.0D);
            return ProjectileUtil.getEntityHitResult(e, vec3, vec32, aabb, (eTest) -> eTest.getType() == JSGEntities.KINO.get(), reach);
        }
    }
}
