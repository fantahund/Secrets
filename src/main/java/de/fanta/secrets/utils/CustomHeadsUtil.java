package de.fanta.secrets.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.fanta.secrets.Secrets;
import de.iani.cubesideutils.bukkit.items.CustomHeads;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public enum CustomHeadsUtil {
    RAINBOW_X("4d37c12c-eb19-4499-8c62-33d84c4d9761", "Rainbow X", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVkNWM3NWY2Njc1ZWRjMjkyZWEzNzg0NjA3Nzk3MGQyMjZmYmQ1MjRlN2ZkNjgwOGYzYTQ3ODFhNTQ5YjA4YyJ9fX0="),

    RAINBOW_ARROW_UP("daf047f5-cf80-4fdf-bbc8-d7b676c97fcf", "Rainbow Arrow Up", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNkYjhmNDM2NTZjMDZjNGU4NjgzZTJlNjM0MWI0NDc5ZjE1N2Y0ODA4MmZlYTRhZmYwOWIzN2NhM2M2OTk1YiJ9fX0="),
    RAINBOW_ARROW_DOWN("2b529a28-1db9-4450-a3eb-6c9897417bbb", "Rainbow Arrow Down", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlMWU3MzBjNzcyNzljOGUyZTE1ZDhiMjcxYTExN2U1ZTJjYTkzZDI1YzhiZTNhMDBjYzkyYTAwY2MwYmI4NSJ9fX0="),
    RAINBOW_ARROW_LEFT("375056b3-1bbb-47c3-8f49-1284b46176f7", "Rainbow Arrow Left", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjOTZhNWMzZDEzYzMxOTkxODNlMWJjN2YwODZmNTRjYTJhNjUyNzEyNjMwM2FjOGUyNWQ2M2UxNmI2NGNjZiJ9fX0="),
    RAINBOW_ARROW_RIGHT("aaea5de0-a21a-4d07-bdb3-060b47085107", "Rainbow Arrow Right", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzMzYWU4ZGU3ZWQwNzllMzhkMmM4MmRkNDJiNzRjZmNiZDk0YjM0ODAzNDhkYmI1ZWNkOTNkYThiODEwMTVlMyJ9fX0="),
    RAINBOW_ARROW_BACKWARD_II("3b0bfc9d-474f-45c2-88e5-faee505e0885", "Rainbow Backward II", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZjZTQ3NWE0Mzg0ZTA5ZTMyY2Q4ZTkxZDA5Yzc5NDdhZGY3ODI3MzA3ZmRhZGRiYWYyOTk4NTE0OTQ4ZmI2ZSJ9fX0="),
    RAINBOW_ARROW_FORWARD_II("a090e767-c207-4f3b-a207-9671389c120f", "Rainbow Forward II", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyMGYwZDRiMmUwMTBlODAxNTNhNjhiNDU1MDFkMDU0YzQzYmIyNDhmNDRiYjgzNzM2NDBlMzIzNTY3OWFjMyJ9fX0="),
    RAINBOW_BLANK("fb0cf49a-a3fd-4fd9-be63-0833bab9566e", "Rainbow Blank", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzJiNjczMjE5OWZhZjFiMjQ0NzcyNWU1NjgyOTA5MGZiY2ViNmMyYjUxNDk1Mzg2MmZmMDNjMTZiNTNmMzU5OSJ9fX0="),
    ;

    private final ItemStack head;
    CustomHeadsUtil(String ownerUUIDString, String ownerName, String texturesProperty) {
        head = getCustomTextureHead(UUID.fromString(ownerUUIDString), ownerName, texturesProperty);
    }

    public ItemStack getHead() {
        return new ItemStack(head);
    }

    public ItemStack getHead(String displayName) {
        return getHead(displayName, (String[]) null);
    }

    public ItemStack getHead(String displayName, String... lore) {
        ItemStack stack = getHead();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null && lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getCustomTextureHead(UUID uuid, String name, String value) {
        if (!Secrets.getPlugin().isPaperServer()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(uuid, "");
            profile.getProperties().put("textures", new Property("textures", value));
            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            head.setItemMeta(meta);
            return head;
        } else {
            return CustomHeads.createHead(uuid, name, value);
        }
    }
}
