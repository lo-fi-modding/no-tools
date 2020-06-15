package lofimodding.notools;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NoToolsMod.MOD_ID)
public class NoToolsMod {
  public static final String MOD_ID = "no-tools";
  public static final Logger LOGGER = LogManager.getLogger();

  public static final Tag<Item> DISABLED_TOOLS = new ItemTags.Wrapper(loc("disabled_tools"));

  public NoToolsMod() {
    Config.registerConfig();

    final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(this::onConfigReload);
    modBus.addListener(this::gatherData);
    modBus.addListener(this::clientSetup);

    final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    forgeBus.addListener(this::onBreakSpeed);
    forgeBus.addListener(this::onUseHoe);
    forgeBus.addListener(this::onAttackEntity);
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(MOD_ID, path);
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    LOGGER.info("Loading client-only features...");
    MinecraftForge.EVENT_BUS.addListener(this::onTooltip);
  }

  private void onConfigReload(final ModConfig.Reloading event) {
    LOGGER.info("Reloading config {}", event.getConfig().getFileName());
  }

  private void onBreakSpeed(final PlayerEvent.BreakSpeed event) {
    final Item held = event.getPlayer().getHeldItemMainhand().getItem();

    if(Config.ENABLED.get() && Config.DISABLE_USE.get() && DISABLED_TOOLS.contains(held)) {
      event.setCanceled(true);
    }
  }

  private void onUseHoe(final UseHoeEvent event) {
    final Item held = event.getContext().getItem().getItem();

    if(Config.ENABLED.get() && Config.DISABLE_USE.get() && DISABLED_TOOLS.contains(held)) {
      event.setCanceled(true);
    }
  }

  private void onAttackEntity(final AttackEntityEvent event) {
    final Item held = event.getPlayer().getHeldItemMainhand().getItem();

    if(Config.ENABLED.get() && Config.DISABLE_USE.get() && DISABLED_TOOLS.contains(held)) {
      event.setCanceled(true);
    }
  }

  private void onTooltip(final ItemTooltipEvent event) {
    final Item held = event.getItemStack().getItem();

    if(Config.ENABLED.get() && Config.DISABLE_USE.get() && DISABLED_TOOLS.contains(held)) {
      event.getToolTip().add(new TranslationTextComponent("no-tools.disable_use"));
    }
  }

  private void gatherData(final GatherDataEvent event) {
    final DataGenerator gen = event.getGenerator();

    if(event.includeClient()) {
      gen.addProvider(new EnglishLang(gen));
    }

    if(event.includeServer()) {
      gen.addProvider(new ItemTagProvider(gen));
    }
  }

  public static class EnglishLang extends LanguageProvider {
    public EnglishLang(final DataGenerator gen) {
      super(gen, MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
      this.add("no-tools.disable_use", "This tool has been disabled by No Tools!");
    }
  }

  public static class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(final DataGenerator gen) {
      super(gen);
    }

    @Override
    protected void registerTags() {
      this.getBuilder(DISABLED_TOOLS)
        .add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE)
        .add(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL)
        .add(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE)
        .add(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE)
        .add(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD);
    }
  }
}
