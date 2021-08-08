package bdemmy.qolenchants;

import bdemmy.qolenchants.enchantment.ModEnchants;
import net.fabricmc.api.ModInitializer;

public class ModQOLEnchants implements ModInitializer {
    public static final String MOD_ID = "qolenchants";

    @Override
    public void onInitialize() {
        ModEnchants.init();
    }
}
