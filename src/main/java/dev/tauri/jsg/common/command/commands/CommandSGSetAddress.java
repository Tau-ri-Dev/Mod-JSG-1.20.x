package dev.tauri.jsg.common.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.tauri.jsg.api.registry.JSGSymbolUsages;
import dev.tauri.jsg.api.stargate.network.address.StargateAddressDynamic;
import dev.tauri.jsg.core.common.command.JSGCommand;
import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUtil;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class CommandSGSetAddress extends AbstractStargateCommand {
    public CommandSGSetAddress() {
        super(JSGCommand.JSG_COMMAND_BASE);
    }

    @Override
    public String getName() {
        return "sgsetaddress";
    }

    @Override
    public String getGeneralUsage() {
        return "sgsetaddress [x y z] <map=> *glyphs";
    }

    @Override
    public String getDescription() {
        return "Sets the address of the specified gate.";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> getMainCommandPart(boolean pos) {
        return Commands.argument("map", StringArgumentType.word())
                .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                    for (var type : SymbolType.values(JSGSymbolUsages.STARGATES.get())) {
                        if (Objects.equals(builder.getRemainingLowerCase(), "") || type.getId().toString().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            builder.suggest(type.getId().toString());
                    }
                    return builder.buildFuture();
                }).then(getGlyphArguments(1, ctx ->
                        setAddress(
                                pos ? BlockPosArgument.getLoadedBlockPos(ctx, "position") : null,
                                StringArgumentType.getString(ctx, "map"),
                                List.of(
                                        StringArgumentType.getString(ctx, "glyph1"),
                                        StringArgumentType.getString(ctx, "glyph2"),
                                        StringArgumentType.getString(ctx, "glyph3"),
                                        StringArgumentType.getString(ctx, "glyph4"),
                                        StringArgumentType.getString(ctx, "glyph5"),
                                        StringArgumentType.getString(ctx, "glyph6"),
                                        StringArgumentType.getString(ctx, "glyph7"),
                                        StringArgumentType.getString(ctx, "glyph8")
                                ),
                                ctx
                        )
                ));
    }

    private SuggestionProvider<CommandSourceStack> getSymbolSuggestion() {
        return (CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
            try {
                var symbolTypeString = StringArgumentType.getString(context, "map");
                if (symbolTypeString == null) return builder.buildFuture();
                var symbolType = SymbolType.byId(JSGMapping.rl(symbolTypeString.toLowerCase()));
                if (symbolType == null) return builder.buildFuture();

                StargateAddressDynamic address = new StargateAddressDynamic(symbolType);
                try {
                    for (int i = 1; i <= 8; i++) {
                        var s = StringArgumentType.getString(context, "glyph" + i);
                        if (s == null) break;
                        var symbol = SymbolUtil.getSymbolFromNameIndex(symbolType, s.replaceAll("_", " "));
                        if (symbol == null || !symbol.isValidForAddress()) {
                            continue;
                        }
                        address.addSymbol(symbol);
                    }
                } catch (Exception ignored) {
                }


                for (var symbol : symbolType.getValues()) {
                    if (!symbol.isValidForAddress() || address.contains(symbol)) continue;
                    var name = symbol.getEnglishName().replaceAll(" ", "_");
                    if (Objects.equals(builder.getRemainingLowerCase(), "") || name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                        builder.suggest(name);
                }
                return builder.buildFuture();
            } catch (Exception ignored) {
                return builder.buildFuture();
            }
        };
    }

    private ArgumentBuilder<CommandSourceStack, ?> getGlyphArguments(int glyph, Command<CommandSourceStack> cmd) {
        var last = Commands.argument("glyph" + glyph, StringArgumentType.word()).suggests(getSymbolSuggestion());
        if (glyph >= 8) return last.executes(cmd);
        var next = getGlyphArguments(glyph + 1, cmd);
        last.then(next);
        return last;
    }


    private int setAddress(@Nullable BlockPos position, String map, List<String> glyphs, @NotNull CommandContext<CommandSourceStack> ctx) {
        var stargate = getStargate(position, ctx);
        if (stargate == null) return 0;

        if (map == null || glyphs == null) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.noaddressspace");
            return 0;
        }

        var symbolType = SymbolType.byId(JSGMapping.rl(map.toLowerCase()));
        if (symbolType == null) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.noaddressspace");
            return 0;
        }

        StargateAddressDynamic address = new StargateAddressDynamic(symbolType);
        var i = 0;
        for (var s : glyphs) {
            i++;
            if (i > 8)
                break;
            var symbol = SymbolUtil.getSymbolFromNameIndex(symbolType, s.replaceAll("_", " "));
            if (symbol == null || !symbol.isValidForAddress()) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.wrongsymbol", i);
                return 0;
            }
            if (address.contains(symbol)) {
                baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.duplicatesymbol", i);
                return 0;
            }
            address.addSymbol(symbol);
        }
        if (i < 8 || address.getSize() < 8) {
            baseCommand.sendErrorMess(ctx.getSource(), "commands.sgsetaddress.wrongaddress");
            return 0;
        }

        stargate.setGateAddress(symbolType, address.toImmutable());
        baseCommand.sendSuccessMess(ctx.getSource(), "commands.sgsetaddress.success", BlockHelper.blockPosToBetterString(position), address.toString());
        return 1;
    }
}
