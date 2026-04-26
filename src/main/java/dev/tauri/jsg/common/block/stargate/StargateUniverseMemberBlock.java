package dev.tauri.jsg.common.block.stargate;

import dev.tauri.jsg.api.block.stargate.IStargateChevronBlock;
import dev.tauri.jsg.api.block.stargate.IStargateRingBlock;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class StargateUniverseMemberBlock extends StargateClassicMemberBlock {

    public static class StargateUniverseChevronBlock extends StargateUniverseMemberBlock implements IStargateChevronBlock {

        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_UNIVERSE_CHEVRON_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }

    public static class StargateUniverseRingBlock extends StargateUniverseMemberBlock implements IStargateRingBlock {
        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_UNIVERSE_RING_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }
}
