package dev.tauri.jsg.integration.create;

import dev.tauri.jsg.JSG;
import dev.tauri.jsg.common.blockentity.stargate.StargateAbstractBaseBE;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PonderScenes implements PonderPlugin {
    public PonderScenes() {
        PonderIndex.addPlugin(this);
    }

    @Override
    public String getModId() {
        return JSG.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(JSGMapping.rl(JSG.MOD_ID, "stargate_milkyway_base_block"), JSGMapping.rl(JSG.MOD_ID, "gate"), PonderScenes::stargateMerging);
    }

    public static void stargateMerging(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("jsg.stargate", "This is a template");
        scene.configureBasePlate(0, 0, 11);
        scene.setSceneOffsetY(-1);
        scene.scaleSceneView(.95f);
        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(util.select().position(5, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().layersFrom(1), Direction.DOWN);
        scene.idle(20);
        scene.world().modifyBlockEntity(new BlockPos(5, 1, 3), StargateAbstractBaseBE.class, (stargate) -> {
            stargate.getMergeHelper().updateMemberStateAndCheck(true);
        });
        scene.markAsFinished();
    }
}
