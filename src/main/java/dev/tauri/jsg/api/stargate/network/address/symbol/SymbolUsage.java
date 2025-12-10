package dev.tauri.jsg.api.stargate.network.address.symbol;

public record SymbolUsage(String id) {
    public static final SymbolUsage STARGATES = new SymbolUsage("stargates");

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SymbolUsage otherSU)) return false;
        return this.id.equalsIgnoreCase(otherSU.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
