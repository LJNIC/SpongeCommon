package org.spongepowered.common.world.gen.populators;

import static com.google.common.base.Preconditions.checkArgument;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.PopulatorType;
import org.spongepowered.api.world.gen.PopulatorTypes;
import org.spongepowered.api.world.gen.populator.ChorusFlower;

import java.util.Random;

public class ChorusFlowerPopulator implements ChorusFlower {

    private NoiseGeneratorSimplex noise;
    private long lastSeed = -1;
    private int exclusion = 64;

    @Override
    public PopulatorType getType() {
        return PopulatorTypes.CHORUS_FLOWER;
    }

    @Override
    public int getExclusionRadius() {
        return this.exclusion;
    }

    @Override
    public void setExclusionRadius(int radius) {
        checkArgument(radius >= 0, "Exclusion radius must be positive or zero");
        this.exclusion = radius;
    }

    @Override
    public void populate(Chunk chunk, Random rand) {
        if (this.noise == null || chunk.getWorld().getProperties().getSeed() != this.lastSeed) {
            this.lastSeed = chunk.getWorld().getProperties().getSeed();
            this.noise = new NoiseGeneratorSimplex(new Random(this.lastSeed));
        }

        World worldObj = (World) chunk.getWorld();
        Vector3i min = chunk.getBlockMin();
        BlockPos chunkPos = new BlockPos(min.getX(), min.getY(), min.getZ());
        int chunkX = min.getX() / 16;
        int chunkZ = min.getZ() / 16;
        if ((long) chunkX * (long) chunkX + (long) chunkZ * (long) chunkZ > this.exclusion * this.exclusion) {
            if (this.func_185960_a(chunkX, chunkZ, 1, 1) > 40.0F) {
                int count = rand.nextInt(5);

                for (int n = 0; n < count; ++n) {
                    int x = rand.nextInt(16) + 8;
                    int y = rand.nextInt(16) + 8;
                    int z = worldObj.getHeight(chunkPos.add(x, 0, y)).getY();

                    if (z > 0) {
                        if (worldObj.isAirBlock(chunkPos.add(x, z, y))
                                && worldObj.getBlockState(chunkPos.add(x, z - 1, y)).getBlock() == Blocks.end_stone) {
                            BlockChorusFlower.func_185603_a(worldObj, chunkPos.add(x, z, y), rand, 8);
                        }
                    }
                }
            }
        }
    }

    private float func_185960_a(int x, int z, int p_185960_3_, int p_185960_4_) {
        float f = x * 2 + p_185960_3_;
        float f1 = z * 2 + p_185960_4_;
        float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

        if (f2 > 80.0F) {
            f2 = 80.0F;
        }

        if (f2 < -100.0F) {
            f2 = -100.0F;
        }

        for (int i = -12; i <= 12; ++i) {
            for (int j = -12; j <= 12; ++j) {
                long k = x + i;
                long l = z + j;

                if (k * k + l * l > 4096L && this.noise.func_151605_a(k, l) < -0.8999999761581421D) {
                    float f3 = (MathHelper.abs(k) * 3439.0F + MathHelper.abs(l) * 147.0F) % 13.0F + 9.0F;
                    f = p_185960_3_ - i * 2;
                    f1 = p_185960_4_ - j * 2;
                    float f4 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * f3;

                    if (f4 > 80.0F) {
                        f4 = 80.0F;
                    }

                    if (f4 < -100.0F) {
                        f4 = -100.0F;
                    }

                    if (f4 > f2) {
                        f2 = f4;
                    }
                }
            }
        }

        return f2;
    }
}
