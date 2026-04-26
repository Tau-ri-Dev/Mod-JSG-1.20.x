package dev.tauri.jsg.common.block.stargate;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.api.block.stargate.IStargateChevronBlock;
import dev.tauri.jsg.api.block.stargate.IStargateRingBlock;
import dev.tauri.jsg.common.registry.JSGBlockEntities;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public abstract class StargateMovieMemberBlock extends StargateClassicMemberBlock {

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @javax.annotation.Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockGetter, components, tooltipFlag);
        components.add(JSG.getInProgress());
    }

    public static class StargateMovieChevronBlock extends StargateMovieMemberBlock implements IStargateChevronBlock {
        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_MOVIE_CHEVRON_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }

    public static class StargateMovieRingBlock extends StargateMovieMemberBlock implements IStargateRingBlock {
        @Nullable
        @Override
        @ParametersAreNonnullByDefault
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return JSGBlockEntities.STARGATE_MOVIE_RING_BE.get().create(pos, state);
        }

        @Nullable
        @Override
        public RegistryObject<CreativeModeTab> getTab() {
            return CoreTabs.TAB_TRANSPORTATION;
        }
    }
}
