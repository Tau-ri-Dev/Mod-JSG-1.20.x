package dev.tauri.jsg.api.client.screen.util;

import net.minecraft.client.renderer.Rect2i;

public class JSGRect2i extends Rect2i {
    public JSGRect2i(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public JSGRect2i offset(int x, int y){
        return new JSGRect2i(this.getX() + x, this.getY() + y, this.getWidth(), this.getHeight());
    }

    public Rect2i toNormal(){
        return new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
}
