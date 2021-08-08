package bdemmy.qolenchants.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class EnchantSpringheel extends Enchantment {

    protected EnchantSpringheel(Rarity enchantment$Weight_1, EnchantmentTarget enchantmentTarget_1, EquipmentSlot[] equipmentSlots_1) {
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
    public int getMinPower(int int_1) {
        return 16;
    }

    @Override
    public int getMaxPower(int level) {
        return 61;
    }
}