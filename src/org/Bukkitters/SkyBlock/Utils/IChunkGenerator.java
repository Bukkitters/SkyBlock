package org.Bukkitters.SkyBlock.Utils;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class IChunkGenerator extends ChunkGenerator {

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz, BiomeGrid biome) {
		ChunkData data = createChunkData(world);
		biome.setBiome(chunkx, 0, chunkz, Biome.THE_VOID);
		return data;
	}

}
