package dev.tauri.jsg.api.client.screen.util;

public class DHDScreenHelper {
    public int crystalTexX;
    public int crystalTexY;
    public int titleTexX;
    public int titleTexY;

    public DHDScreenHelper(int crystalTexX, int crystalTexY, int titleTexX, int titleTexY) {
        this.crystalTexX = crystalTexX;
        this.crystalTexY = crystalTexY;
        this.titleTexX = titleTexX;
        this.titleTexY = titleTexY;
    }

    public static DHDScreenHelper getMilkyWay() {
        return new DHDScreenHelper(176, 0, 177, 87);
    }

    public static DHDScreenHelper getPegasus() {
        return new DHDScreenHelper(201, 0, 177, 96);
    }
}
