package com.pclogix.lanteacraft.mixin;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockEditScreen.class)
public abstract class StructureBlockEditScreenMixin extends Screen {
    @Shadow
    private EditBox nameEdit;

    private StructureBlockEditScreenMixin() {
        super(Component.empty());
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void lanteacraft$addExpeditionTemplatePicker(CallbackInfo ci) {
        List<String> templates = lanteacraft$expeditionTemplates();
        if (templates.isEmpty()) {
            return;
        }

        Button picker = Button.builder(Component.literal("Pick"), button -> {
                    String value = lanteacraft$nextTemplate(templates, this.nameEdit.getValue());
                    this.nameEdit.setValue(value);
                    button.setTooltip(Tooltip.create(Component.literal(value)));
                    this.setFocused(this.nameEdit);
                })
                .bounds(this.width / 2 + 154, 40, 50, 20)
                .tooltip(Tooltip.create(Component.literal("Cycle expedition structures")))
                .build();
        this.addRenderableWidget(picker);
    }

    private static List<String> lanteacraft$expeditionTemplates() {
        return ImmutableList.of(
                "lanteacraft:expedition/combat_room_1",
                "lanteacraft:expedition/combat_room_2",
                "lanteacraft:expedition/combat_room_3",
                "lanteacraft:expedition/combat_room_4",
                "lanteacraft:expedition/gate_room",
                "lanteacraft:expedition/hall_1",
                "lanteacraft:expedition/hall_2",
                "lanteacraft:expedition/hall_3",
                "lanteacraft:expedition/hall_left_1",
                "lanteacraft:expedition/hall_right_1",
                "lanteacraft:expedition/hall_short_1",
                "lanteacraft:expedition/hall_tall_1",
                "lanteacraft:expedition/hall_wide_1",
                "lanteacraft:expedition/intersection_cross",
                "lanteacraft:expedition/reward_room",
                "lanteacraft:expedition/reward_room_2",
                "lanteacraft:expedition/reward_room_3",
                "lanteacraft:expedition/reward_vault_1",
                "lanteacraft:expedition/room_combat_arena_1",
                "lanteacraft:expedition/room_combat_arena_2",
                "lanteacraft:expedition/room_final_corridor_1",
                "lanteacraft:expedition/room_medium_1",
                "lanteacraft:expedition/room_octagonish_1",
                "lanteacraft:expedition/room_reward_antechamber_1",
                "lanteacraft:expedition/room_reward_antechamber_2",
                "lanteacraft:expedition/room_small_1",
                "lanteacraft:expedition/room_tall_1",
                "lanteacraft:expedition/room_wide_1",
                "lanteacraft:expedition/terminator_cap_hall_in",
                "lanteacraft:expedition/terminator_cap_room_in");
    }

    private static String lanteacraft$nextTemplate(List<String> templates, String current) {
        int index = templates.indexOf(current);
        return templates.get((index + 1) % templates.size());
    }
}
