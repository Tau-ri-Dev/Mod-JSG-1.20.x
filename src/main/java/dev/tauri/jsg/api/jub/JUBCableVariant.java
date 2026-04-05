package dev.tauri.jsg.api.jub;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum JUBCableVariant implements StringRepresentable {
    CENTER(),
    SOUTH(Direction.SOUTH),
    NORTH(Direction.NORTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST),
    DOWN(Direction.DOWN),
    UP(Direction.UP),

    SN(Direction.SOUTH, Direction.NORTH),
    SW(Direction.SOUTH, Direction.WEST),
    SE(Direction.SOUTH, Direction.EAST),
    SD(Direction.SOUTH, Direction.DOWN),
    SU(Direction.SOUTH, Direction.UP),
    NW(Direction.NORTH, Direction.WEST),
    NE(Direction.NORTH, Direction.EAST),
    ND(Direction.NORTH, Direction.DOWN),
    NU(Direction.NORTH, Direction.UP),
    WE(Direction.WEST, Direction.EAST),
    WD(Direction.WEST, Direction.DOWN),
    WU(Direction.WEST, Direction.UP),
    ED(Direction.EAST, Direction.DOWN),
    EU(Direction.EAST, Direction.UP),
    DU(Direction.DOWN, Direction.UP),

    NWS(Direction.NORTH, Direction.WEST, Direction.SOUTH),
    NES(Direction.NORTH, Direction.EAST, Direction.SOUTH),
    NDS(Direction.NORTH, Direction.DOWN, Direction.SOUTH),
    NUS(Direction.NORTH, Direction.UP, Direction.SOUTH),
    WES(Direction.WEST, Direction.EAST, Direction.SOUTH),
    WDS(Direction.WEST, Direction.DOWN, Direction.SOUTH),
    WUS(Direction.WEST, Direction.UP, Direction.SOUTH),
    EDS(Direction.EAST, Direction.DOWN, Direction.SOUTH),
    EUS(Direction.EAST, Direction.UP, Direction.SOUTH),
    DUS(Direction.DOWN, Direction.UP, Direction.SOUTH),
    WEN(Direction.WEST, Direction.EAST, Direction.NORTH),
    WDN(Direction.WEST, Direction.DOWN, Direction.NORTH),
    WUN(Direction.WEST, Direction.UP, Direction.NORTH),
    EDN(Direction.EAST, Direction.DOWN, Direction.NORTH),
    EUN(Direction.EAST, Direction.UP, Direction.NORTH),
    DUN(Direction.DOWN, Direction.UP, Direction.NORTH),
    EDW(Direction.EAST, Direction.DOWN, Direction.WEST),
    EUW(Direction.EAST, Direction.UP, Direction.WEST),
    DUW(Direction.DOWN, Direction.UP, Direction.WEST),
    DUE(Direction.DOWN, Direction.UP, Direction.EAST),

    WENS(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH),
    WDNS(Direction.WEST, Direction.DOWN, Direction.NORTH, Direction.SOUTH),
    WUNS(Direction.WEST, Direction.UP, Direction.NORTH, Direction.SOUTH),
    EDNS(Direction.EAST, Direction.DOWN, Direction.NORTH, Direction.SOUTH),
    EUNS(Direction.EAST, Direction.UP, Direction.NORTH, Direction.SOUTH),
    DUNS(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH),
    EDWS(Direction.EAST, Direction.DOWN, Direction.WEST, Direction.SOUTH),
    EUWS(Direction.EAST, Direction.UP, Direction.WEST, Direction.SOUTH),
    DUWS(Direction.DOWN, Direction.UP, Direction.WEST, Direction.SOUTH),
    DUES(Direction.DOWN, Direction.UP, Direction.EAST, Direction.SOUTH),
    EDWN(Direction.EAST, Direction.DOWN, Direction.WEST, Direction.NORTH),
    EUWN(Direction.EAST, Direction.UP, Direction.WEST, Direction.NORTH),
    DUWN(Direction.DOWN, Direction.UP, Direction.WEST, Direction.NORTH),
    DUEN(Direction.DOWN, Direction.UP, Direction.EAST, Direction.NORTH),
    DUEW(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST),

    EDWNS(Direction.EAST, Direction.DOWN, Direction.WEST, Direction.NORTH, Direction.SOUTH),
    EUWNS(Direction.EAST, Direction.UP, Direction.WEST, Direction.NORTH, Direction.SOUTH),
    DUWNS(Direction.DOWN, Direction.UP, Direction.WEST, Direction.NORTH, Direction.SOUTH),
    DUENS(Direction.DOWN, Direction.UP, Direction.EAST, Direction.NORTH, Direction.SOUTH),
    DUEWS(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.SOUTH),
    DUEWN(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH),

    ALL(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP);

    private final List<Direction> connections;

    JUBCableVariant(Direction... connectedTo) {
        connections = List.of(connectedTo);
    }

    public static JUBCableVariant fromDirections(List<Direction> connections) {
        var finalConnections = new ArrayList<>(new HashSet<>(connections));
        if (finalConnections.isEmpty()) return CENTER;
        for (var variant : JUBCableVariant.values()) {
            if (finalConnections.size() != variant.connections.size()) continue;
            if (finalConnections.containsAll(variant.connections)) {
                return variant;
            }
        }
        return JUBCableVariant.CENTER;
    }

    public List<Direction> getConnections() {
        return new ArrayList<>(this.connections);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }
}
