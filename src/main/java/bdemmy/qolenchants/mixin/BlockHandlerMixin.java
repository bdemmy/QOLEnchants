package bdemmy.qolenchants.mixin;

import bdemmy.qolenchants.ModQOLEnchants;
import bdemmy.qolenchants.util.RecipeUtils;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(AbstractBlock.class)
public abstract class BlockHandlerMixin {
    private static final int   VEIN_MINE_ORE_MAX_BLOCKS        = 24;
    private static final int   VEIN_MINE_LOG_MAX_BLOCKS        = 128;
    private static final float VEIN_MINE_DURABILITY_MULTIPLIER = 1.5f;

    // Helper random
    private static Random rand = new Random();

    @Inject(locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "RETURN", ordinal = 1), method = "Lnet/minecraft/block/AbstractBlock;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;")
    private void droppedStacksHandler(BlockState state, LootContext.Builder contextBuilder, CallbackInfoReturnable<List> cir, Identifier identifier, LootContext lootContext, ServerWorld serverWorld, LootTable lootTable) {
        // Get the mining player, if they exist.
        // If they don't, then no player broke this block and we don't want to continue.
        Entity player = contextBuilder.getNullable(LootContextParameters.THIS_ENTITY);
        if (player == null) {
            return;
        }

        // Self instance
        Block selfBlock = (Block) (Object) this;

        // Setup loot shit
        ServerWorld world = contextBuilder.getWorld();
        BlockPos pos = new BlockPos(contextBuilder.get(LootContextParameters.ORIGIN));

        // Make sure the held tool is effective
        Item heldItemType = contextBuilder.get(LootContextParameters.TOOL).getItem();

        // Doesn't apply, just return
        if (isLog(selfBlock) && !(heldItemType instanceof AxeItem)) {
            return;
        } else if (isOre(selfBlock) && !(heldItemType instanceof PickaxeItem)) {
            return;
        }

        // Check to see if we have our custom enchants.
        boolean doInferno  = EnchantmentHelper.getLevel(Registry.ENCHANTMENT.get(new Identifier(ModQOLEnchants.MOD_ID, "enchant_inferno")), contextBuilder.get(LootContextParameters.TOOL)) > 0;
        boolean doVeinMine = EnchantmentHelper.getLevel(Registry.ENCHANTMENT.get(new Identifier(ModQOLEnchants.MOD_ID, "enchant_veinmine")), contextBuilder.get(LootContextParameters.TOOL)) > 0;

        // We don't need to modify anything.
        // Save cycles and return.
        if (!doInferno && !doVeinMine) {
            return;
        }

        // Make our output list that we will use
        List<ItemStack> outLootList = new ArrayList<>();

        // Vein miner enchantment
        if ((doVeinMine && (isOre(selfBlock) || isLog(selfBlock))) && !player.isSneaking()) {
            int veinSize = veinMine(world, pos, selfBlock, doInferno);
            for (int i = 0; i < veinSize; i++) {
                outLootList.addAll(lootTable.generateLoot(lootContext));
            }

            // Damage our item, no free damage here
            for (ItemStack handItem : player.getItemsHand()) {
                if (handItem.getItem().equals(contextBuilder.get(LootContextParameters.TOOL).getItem())) {
                    int damageToApply = (int) (VEIN_MINE_DURABILITY_MULTIPLIER * (veinSize - 1));
                    handItem.damage(damageToApply, new Random(), (ServerPlayerEntity) player);
                }
            }

            int totalXp = 0;
            for (int i = 0; i < veinSize; i++) {
                totalXp += getExperience(rand, selfBlock);
            }

            dropExperience(world, pos, totalXp);

        } else {
            outLootList.addAll(lootTable.generateLoot(lootContext));
        }

        // Inferno enchantment
        // Loop through all of the current drops, and if they have a furnace recipe then smelt.
        if (doInferno) {
            for (int i = 0; i < outLootList.size(); i++) {
                ItemStack furnaceOutput = RecipeUtils.getFurnaceRecipe(outLootList.get(i), world);
                if (furnaceOutput != ItemStack.EMPTY) {
                    furnaceOutput.setCount(getQuantityWithFortune(contextBuilder));
                    outLootList.set(i, furnaceOutput.copy());
                }
            }

            spawnInfernoParticles(world, pos);
        }

        cir.setReturnValue(outLootList);
    }

    private void spawnInfernoParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 3; i++) {
            world.spawnParticles(ParticleTypes.FLAME, pos.getX() + rand.nextFloat() - 0.5f, pos.getY() + rand.nextFloat() - 0.5f, pos.getZ() + rand.nextFloat() - 0.5f, 1, rand.nextFloat() / 8, rand.nextFloat() / 8, rand.nextFloat() / 8, 0.03f);
        }
    }

    private void dropExperience(ServerWorld world, BlockPos pos, int totalXp) {
        while (totalXp > 0) {
            int orbXp = ExperienceOrbEntity.roundToOrbSize(totalXp);
            totalXp -= orbXp;
            world.spawnEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, orbXp));
        }
    }

    private int getExperience(Random rand, Block block) {
        if (block == Blocks.COAL_ORE) {
            return MathHelper.nextInt(rand, 0, 2);
        } else if (block == Blocks.DIAMOND_ORE) {
            return MathHelper.nextInt(rand, 3, 7);
        } else if (block == Blocks.EMERALD_ORE) {
            return MathHelper.nextInt(rand, 3, 7);
        } else if (block == Blocks.LAPIS_ORE) {
            return MathHelper.nextInt(rand, 2, 5);
        } else {
            return block == Blocks.NETHER_QUARTZ_ORE ? MathHelper.nextInt(rand, 2, 5) : 0;
        }
    }

    private int getQuantityWithFortune(LootContext.Builder context) {
        LootContext lootContext = context.parameter(LootContextParameters.BLOCK_STATE, Blocks.DIAMOND_ORE.getDefaultState()).luck(getFortuneFromLootContextBuilder(context)).build(LootContextTypes.BLOCK);
        LootTable lootSupplier = context.getWorld().getServer().getLootManager().getTable(Blocks.DIAMOND_ORE.getLootTableId());
        return lootSupplier.generateLoot(lootContext).get(0).getCount();
    }

    private int getFortuneFromLootContextBuilder(LootContext.Builder lootContext$builder) {
        return EnchantmentHelper.getLevel(Enchantments.FORTUNE, lootContext$builder.get(LootContextParameters.TOOL));
    }

    private int veinMine(ServerWorld world, BlockPos pos, Block block, boolean doInferno) {
        int maxCount = isOre(block) ? VEIN_MINE_ORE_MAX_BLOCKS : VEIN_MINE_LOG_MAX_BLOCKS;

        // Get the adjacent blocks
        Set<BlockPos> positions = traverseAdjacent(world, pos, block, maxCount);

        // Break every found block
        for (BlockPos bPos : positions) {
            if (bPos != pos) {
                world.breakBlock(bPos, false);

                if (doInferno) {
                    spawnInfernoParticles(world, bPos);
                }
            }
        }

        // Return how many blocks we broke
        return positions.size();
    }

    private Set<BlockPos> traverseAdjacent(ServerWorld world, BlockPos pos, Block block, int count) {
        // We need a set of the blocks that we find
        // And a deque for queueing blocks to traverse
        Set<BlockPos> found = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        // Start with our initial pos
        queue.add(pos);

        // Keep popping off of the front of the queue and checking its neighbors
        while (!queue.isEmpty()) {
            BlockPos temp = queue.removeFirst();

            found.add(temp);

            if (found.size() >= count) {
                break;
            }

            // TODO: Needs better refactoring
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == y && y == z && z == 0) {
                            continue;
                        }

                        BlockPos curPos = new BlockPos(temp.getX() + x, temp.getY() + y, temp.getZ() + z);

                        if (world.getBlockState(curPos).getBlock().equals(block)) {
                            if (!found.contains(curPos) && !queue.contains(curPos)) {
                                queue.add(curPos);
                            }
                        }
                    }
                }
            }
        }

        return found;
    }

    private boolean isOre(Block block) {
        return block instanceof OreBlock || block instanceof RedstoneOreBlock;
    }

    private boolean isLog(Block block) {
        return block.equals(Blocks.ACACIA_LOG) || block.equals(Blocks.BIRCH_LOG) || block.equals(Blocks.DARK_OAK_LOG) || block.equals(Blocks.JUNGLE_LOG)
                || block.equals(Blocks.OAK_LOG) || block.equals(Blocks.SPRUCE_LOG);
    }
}
