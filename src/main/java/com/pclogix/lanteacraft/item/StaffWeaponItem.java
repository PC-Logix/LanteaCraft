package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.entity.StaffBlastEntity;
import com.pclogix.lanteacraft.enchantment.ExplodingEnchantmentHandler;
import com.pclogix.lanteacraft.registry.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class StaffWeaponItem extends Item {
    private static final float SHOT_SPEED = 2.5F;
    private static final double MELEE_DAMAGE_MODIFIER = 5.0D;
    private static final double ATTACK_SPEED_MODIFIER = -3.0D;

    public StaffWeaponItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation id = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        return id != null && (id.equals(ResourceLocation.withDefaultNamespace("power"))
                || id.equals(ResourceLocation.withDefaultNamespace("flame"))
                || id.equals(ResourceLocation.withDefaultNamespace("unbreaking"))
                || id.equals(ResourceLocation.withDefaultNamespace("mending"))
                || id.equals(ExplodingEnchantmentHandler.EXPLODING.location()));
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, MELEE_DAMAGE_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.STAFF_WEAPON_FIRE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!level.isClientSide) {
            StaffBlastEntity blast = new StaffBlastEntity(level, player);
            blast.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOT_SPEED, 0.0F);
            level.addFreshEntity(blast);
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
        }

        player.getCooldowns().addCooldown(this, 12);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
