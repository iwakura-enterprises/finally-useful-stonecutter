package enterprises.iwakura.finallyusefulstonecutter;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FinallyUsefulStonecutterMod.MODID)
public class FinallyUsefulStonecutterMod {

    public static final String MODID = "finally_useful_stonecutter";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FinallyUsefulStonecutterMod() {
        LOGGER.info("Improving stonecutter since... two days ago I guess!");
        LOGGER.info("Made by Iwakura Enterprises");
    }
}
