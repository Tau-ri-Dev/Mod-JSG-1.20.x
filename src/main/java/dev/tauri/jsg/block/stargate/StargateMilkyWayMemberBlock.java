package dev.tauri.jsg.block.stargate;

import dev.tauri.jsg.api.block.stargate.IStargateChevronBlock;
import dev.tauri.jsg.api.block.stargate.IStargateRingBlock;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.registry.JSGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class StargateMilkyWayMemberBlock extends StargateClassicMemberBlock {

    public static class StargateMilkyWayChevronBlock extends StargateMilkyWayMemberBlock implements IStargateChevronBlock {
        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_MILKYWAY_CHEVRON_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }

    public static class StargateMilkyWayRingBlock extends StargateMilkyWayMemberBlock implements IStargateRingBlock {
        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_MILKYWAY_RING_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }
}
