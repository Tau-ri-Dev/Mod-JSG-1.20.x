package dev.tauri.jsg.common.command.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.tauri.jsg.api.config.JSGConfig;
import dev.tauri.jsg.api.config.ingame.option.StargateConfigOptions;
import dev.tauri.jsg.api.stargate.type.StargateType;
import dev.tauri.jsg.api.stargate.type.StargateTypes;
import dev.tauri.jsg.api.util.IStargateGenerator;
import dev.tauri.jsg.common.worldgen.generator.StargateGenerator;
import dev.tauri.jsg.core.common.command.JSGAbstractCommand;
import dev.tauri.jsg.core.common.command.JSGCommand;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class CommandBuildGate extends JSGAbstractCommand {
    public CommandBuildGate() {
        super(JSGCommand.JSG_COMMAND_BASE);
    }

    @Override
    public List<String> getAliases() {
        return List.of("billgates");
    }

    @Override
    public String getName() {
        return "buildgate";
    }

    @Override
    public String getGeneralUsage() {
        return "buildgate <MILKYWAY|PEGASUS|UNIVERSE|TOLLAN|MOVIE|ORLIN|?>";
    }

    @Override
    public String getDescription() {
        return "Builds gate at place you are looking at.";
    }

    @Override
    @ParametersAreNonnullByDefault
    public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
        return command.then(Commands.argument("type", ResourceLocationArgument.id())
                .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                    for (var type : StargateType.values()) {
                        if (Objects.equals(builder.getRemainingLowerCase(), "") || type.getId().toString().startsWith(builder.getRemainingLowerCase()))
                            builder.suggest(type.getId().toString());
                    }
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    var type = ResourceLocationArgument.getId(ctx, "type");
                    var typeEnum = StargateType.valueOf(type);
                    if (typeEnum == null) {
                        baseCommand.sendErrorMess(ctx.getSource(), "Specify correct gate type!");
                        return 0;
                    }

                    var sender = ctx.getSource().getPlayer();
                    if (sender == null) {
                        baseCommand.sendErrorMess(ctx.getSource(), "This command can only be run by a player!");
                        return 0;
                    }

                    var pos = BlockPosHelper.getPos(sender.position());
                    var target = RayTraceHelper.rayTracePos(sender, 10);
                    if (target != null) {
                        if (typeEnum == StargateTypes.ORLIN.get())
                            pos = target.above();
                        else
                            pos = target.below();
                    }
                    var genCfg = new IStargateGenerator.PlacementConfig();

                    var direction = sender.getDirection().getOpposite();

                    var item = sender.getMainHandItem();
                    genCfg.overlay = BiomeOverlayInstance.getBiomeOverlayByItem(item);

                    var world = sender.level();

                    genCfg.gateBasePos = pos;
                    genCfg.gateFacing = direction;
                    genCfg.world = world;

                    genCfg.gateType = typeEnum;
                    genCfg.stargateConfig = (config) -> {
                        config.getOption(StargateConfigOptions.Classic.MAX_CAPACITORS).ifPresent(option -> option.parseAndSetValue(genCfg.capacitors.size()));
                        return config;
                    };

                    genCfg.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_MW);
                    genCfg.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_PEG);
                    genCfg.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_UNI);
                    genCfg.upgrades.add(IStargateGenerator.StargateUpgradesEnum.UPGRADE_IRIS_CREATIVE);
                    genCfg.upgrades.add(IStargateGenerator.StargateUpgradesEnum.GLYPH_CRYSTAL_STARGATE);

                    var generator = new StargateGenerator();
                    generator.setStargateEnergyInternalSmart(genCfg, JSGConfig.Stargate.stargateEnergyStorage.get());
                    var result = generator.generateStargate(genCfg);
                    if (result == null) {
                        baseCommand.sendErrorMess(ctx.getSource(), "Error while generating stargate!");
                        return 0;
                    }
                    baseCommand.sendSuccessMess(ctx.getSource(), "Stargate generated successfully!");
                    return 1;
                })
        ).executes(ctx -> {
            baseCommand.sendErrorMess(ctx.getSource(), "You need to specify gate type!");
            return 0;
        });
    }
}
