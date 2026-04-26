package dev.tauri.jsg.common.block.jub;

import dev.tauri.jsg.common.capability.JSGCapabilities;
import dev.tauri.jsg.core.common.block.TickableBEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class JUBDeviceBlock extends TickableBEBlock {
    public JUBDeviceBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            var deviceBE = level.getBlockEntity(pos);
            if (deviceBE != null) {
                var deviceCapOpt = deviceBE.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve();
                deviceCapOpt.ifPresent(deviceCap -> player.sendSystemMessage(Component.literal(deviceCap.getBus().uuid.toString())));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        level.updateNeighbourForOutputSignal(blockPos, this);
    }


    @Override
    public void onNeighborChange(BlockState state, LevelReader reader, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, reader, pos, neighbor);

        if (!(reader instanceof Level level)) return;
        if (level.isClientSide()) return;
        var deviceBE = level.getBlockEntity(pos);
        if (deviceBE == null) return;

        var deviceCapOpt = deviceBE.getCapability(JSGCapabilities.JUST_UNIVERSAL_BUS).resolve();
        if (deviceCapOpt.isEmpty()) return;
        var deviceCap = deviceCapOpt.get();

        // update network
        deviceCap.updateBusDevices(level, neighbor);
    }
}
