package bdemmy.qolenchants.mixin;

import bdemmy.qolenchants.ModQOLEnchants;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class JumpHandlerMixin {
    // Our mixin to modify the vertical jump velocity.
    @Inject(method = "getJumpVelocity()F", at = @At(value = "RETURN"), cancellable = true)
    protected void getJumpVelocity(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;

            for (ItemStack stack : playerEntity.getArmorItems()) {
                if (stack.equals(ItemStack.EMPTY)) {
                    continue;
                }

                // If we can find an item equipped with the springheel enchantment, adjust jump velocity
                if (EnchantmentHelper.getLevel(Registry.ENCHANTMENT.get(new Identifier(ModQOLEnchants.MOD_ID, "enchant_springheel")), stack) > 0) {
                    cir.setReturnValue(cir.getReturnValueF() * 1.34f);
                    return;
                }
            }
        }
    }

    // Modify the call to handleFallDamage, to reduce the fall damage threshold by two blocks.
    @ModifyArg(method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;computeFallDamage(FF)I"),
            index = 0)
    private float adjustFallHeight(float fallDistance) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;

            // If we can find an item equipped with the springheel enchantment, adjust fall damage
            for (ItemStack stack : playerEntity.getArmorItems()) {
                if (stack.equals(ItemStack.EMPTY)) {
                    continue;
                }

                if (EnchantmentHelper.getLevel(Registry.ENCHANTMENT.get(new Identifier(ModQOLEnchants.MOD_ID, "enchant_springheel")), stack) > 0) {
                    return Math.max(fallDistance - 2.f, 1.f);
                }
            }
        }

        return fallDistance;
    }
}
