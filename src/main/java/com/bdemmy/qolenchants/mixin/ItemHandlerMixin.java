package com.bdemmy.qolenchants.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemHandlerMixin {
    @Inject(at = @At("HEAD"), cancellable = true, method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;")
    private void itemUseHandler(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user.isSneaking() && !stack.isEmpty() && stack.isDamaged()) {
            if (EnchantmentHelper.getLevel(Enchantments.MENDING, stack) != 0) {
                int i = Math.min(xpToDurability(user.totalExperience), stack.getDamage());
                user.addExperience(durabilityToXp(i) * -1);
                stack.setDamage(stack.getDamage() - i);
                cir.setReturnValue(TypedActionResult.fail(stack));
            }
        }
    }

    private int durabilityToXp(int durability) {
        return durability / 2;
    }

    private int xpToDurability(int xp) {
        return xp * 2;
    }
}
