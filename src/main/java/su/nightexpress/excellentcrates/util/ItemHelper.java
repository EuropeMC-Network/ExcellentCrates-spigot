package su.nightexpress.excellentcrates.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.item.ItemBridge;
import su.nightexpress.nightcore.integration.item.bukkit.BukkitItem;
import su.nightexpress.nightcore.integration.item.codec.AdaptedItemCodec;
import su.nightexpress.nightcore.integration.item.dummy.DummyItem;
import su.nightexpress.nightcore.util.ItemTag;
import su.nightexpress.nightcore.util.Version;

import java.util.Optional;

public class ItemHelper {

    @NotNull
    public static AdaptedItem readOrPlaceholder(@NotNull FileConfig config, @NotNull String path) {
        return read(config, path).orElse(vanilla(CrateUtils.getQuestionStack()));
    }

    @NotNull
    public static Optional<AdaptedItem> read(@NotNull FileConfig config, @NotNull String path) {
        String oldType = config.getString(path + ".Type");
        if (oldType != null) {
            if (oldType.equalsIgnoreCase("vanilla")) {
                String itemTag = config.getString(path + ".ItemTag");
                if (itemTag != null) {
                    ItemTag tag = new ItemTag(itemTag, Version.MC_1_21.getDataVersion());
                    config.set(path + ".Tag", tag);
                    config.remove(path + ".ItemTag");
                }

                if (config.contains(path + ".Tag")) {
                    ItemTag tag = ItemTag.read(config, path + ".Tag");
                    config.remove(path + ".Tag");
                    write(config, path, new BukkitItem(tag));
                }
            }
            else if (oldType.equalsIgnoreCase("custom")) {
                String handlerName = config.getString(path + ".Handler", "null");
                String itemId = config.getString(path + ".ItemId", "null");
                int amount = config.getInt(path + ".Amount");

                config.remove(path + ".Handler");
                config.remove(path + ".ItemId");
                config.remove(path + ".Amount");

                config.set(path + ".Provider", handlerName);
                config.set(path + ".Data.ID", itemId);
                config.set(path + ".Data.Amount", amount);
            }

            config.remove(path + ".Type");
        }

        if (!config.contains(path + ".Provider")) return Optional.empty();

        AdaptedItem item = AdaptedItemCodec.read(config, path);
        return item instanceof DummyItem ? Optional.empty() : Optional.of(item);
    }

    public static void write(@NotNull FileConfig config, @NotNull String path, @NotNull AdaptedItem item) {
        AdaptedItemCodec.write(config, path, item);
    }

    @NotNull
    public static ItemStack toItemStack(@NotNull AdaptedItem item) {
        return item.itemStack().orElse(CrateUtils.getQuestionStack());
    }

    public static boolean isCustom(@NotNull ItemStack itemStack) {
        return !ItemBridge.get().isBukkitOnly(itemStack);
    }

    public static boolean isVanilla(@NotNull AdaptedItem item) {
        return item instanceof BukkitItem;
    }

    @NotNull
    public static AdaptedItem vanilla(@NotNull ItemStack itemStack) {
        return new BukkitItem(ItemTag.of(itemStack));
    }

    @NotNull
    public static AdaptedItem adapt(@NotNull ItemStack itemStack) {
        return ItemBridge.get().adapt(itemStack).orElseGet(() -> vanilla(itemStack));
    }

    @NotNull
    public static AdaptedItem adapt(@NotNull ItemStack itemStack, boolean allowCustoms) {
        return allowCustoms ? adapt(itemStack) : vanilla(itemStack);
    }
}
