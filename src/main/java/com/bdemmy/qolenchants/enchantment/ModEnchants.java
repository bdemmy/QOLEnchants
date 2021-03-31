package com.bdemmy.qolenchants.enchantment;

import com.bdemmy.qolenchants.ModQOLEnchants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEnchants {
    public static final Enchantment enchantment_inferno = new EnchantInferno(Enchantment.Rarity.RARE,
            EnchantmentTarget.DIGGER,
            new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});

    public static final Enchantment enchantment_veinmine = new EnchantVeinmine(Enchantment.Rarity.RARE,
            EnchantmentTarget.DIGGER,
            new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});

    public static final Enchantment enchantment_springheel = new EnchantSpringheel(Enchantment.Rarity.RARE,
            EnchantmentTarget.ARMOR_LEGS,
            new EquipmentSlot[]{EquipmentSlot.LEGS});

    public static void init() {
        Registry.register(Registry.ENCHANTMENT, new Identifier(ModQOLEnchants.MOD_ID, "enchant_inferno"), enchantment_inferno);
        Registry.register(Registry.ENCHANTMENT, new Identifier(ModQOLEnchants.MOD_ID, "enchant_veinmine"), enchantment_veinmine);
        Registry.register(Registry.ENCHANTMENT, new Identifier(ModQOLEnchants.MOD_ID, "enchant_springheel"), enchantment_springheel);
    }
}
