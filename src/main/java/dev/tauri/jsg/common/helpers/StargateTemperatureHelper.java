package dev.tauri.jsg.common.helpers;

import com.google.common.collect.ImmutableList;
import dev.tauri.jsg.common.block.stargate.StargateAbstractBaseBlock;
import dev.tauri.jsg.common.block.stargate.StargateAbstractMemberBlock;
import dev.tauri.jsg.common.blockentity.stargate.StargateClassicBaseBE;
import dev.tauri.jsg.core.common.helper.TemperatureHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * All temperatures are returned as degrees Celsius
 */
public class StargateTemperatureHelper {
    public static double getTemperatureAroundGate(StargateClassicBaseBE<?> baseBE) {
        var level = baseBE.getLevel();
        if (level == null) return 0;
        double suma = 0;
        int count = 0;
        for (var member : baseBE.getMergeHelper().getBlocks().keySet()) {
            for (var direction : Direction.values()) {
                var posOffset = member.offset(direction.getNormal());
                var state = level.getBlockState(posOffset);
                var temp = getBlockTemperature(state, level, posOffset);
                if (temp == null) continue;
                suma += temp;
                count++;
            }
        }
        if (count == 0) return 20;
        return suma / count;
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public static Double getBlockTemperature(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof StargateAbstractBaseBlock || state.getBlock() instanceof StargateAbstractMemberBlock)
            return null;
        if (dev.tauri.jsg.core.common.registry.helper.FluidHelper.isLiquidBlock(state)) {
            Fluid fluid = state.getFluidState().getType();
            if (state.getBlock() instanceof LiquidBlock liquid) {
                fluid = liquid.getFluid();
            }
            if (fluid != Fluids.EMPTY) {
                return TemperatureHelper.asKelvins(fluid.getFluidType().getTemperature()).toCelsius();
            }
        }
        return (double) ((getTemperature(state, level, level.getBiome(pos).get(), pos) - 0.4f) * 25f);
    }


    /**
     * Proudly stolen from Mojang's Biome class as they are stupid and have useful methods marked as private :|
     */
    public static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));

    public static float getTemperature(BlockState state, Level level, Biome biome, BlockPos pos) {
        if (!state.canBeReplaced()) {
            if (state.is(BlockTags.SNOW)) return (-5f) / 25f + 0.4f;
            if (state.is(BlockTags.ICE)) return (-7f) / 25f + 0.4f;
            if (state.getBlock() == Blocks.MAGMA_BLOCK) return (60f) / 25f + 0.4f;
            return ((((-11f + state.getLightBlock(level, pos) + level.getBrightness(LightLayer.BLOCK, pos)) + 36f) / (20f + 36f)) * 2.0f);
        }

        float modifiedTemp = biome.getModifiedClimateSettings().temperatureModifier().modifyTemperature(pos, biome.getBaseTemperature());
        if (pos.getY() > 80) {
            float heightCoefficient = (float) (TEMPERATURE_NOISE.getValue(((float) pos.getX() / 8.0F), ((float) pos.getZ() / 8.0F), false) * 8.0D);
            return modifiedTemp - (heightCoefficient + (float) pos.getY() - 80.0F) * 0.05F / 40.0F;
        }
        return modifiedTemp;
    }
}
