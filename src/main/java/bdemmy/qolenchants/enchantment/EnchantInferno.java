package bdemmy.qolenchants.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantInferno extends Enchantment {

    protected EnchantInferno(Rarity enchantment$Weight_1, EnchantmentTarget enchantmentTarget_1, EquipmentSlot[] equipmentSlots_1) {
        super(enchantment$Weight_1, enchantmentTarget_1, equipmentSlots_1);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack itemStack_1) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack_1) > 0) {
            return false;
        }

        return super.isAcceptableItem(itemStack_1);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }

    @Override
    public int getMinPower(int int_1) {
        return 22;
    }

    @Override
    public int getMaxPower(int level) {
        return 61;
    }
}