package enterprises.iwakura.finallyusefulstonecutter;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;

@Mod(FinallyUsefulStonecutterMod.MODID)
public class FinallyUsefulStonecutterMod {

    public static final String MODID = "finally_useful_stonecutter";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FinallyUsefulStonecutterMod() {
        LOGGER.info("Improving stonecutter since... today I guess!");
        LOGGER.info("Made by Iwakura Enterprises");
    }
}
