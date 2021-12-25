package run.tere.plugin.icerush.games.itemblock.interfaces;

import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import run.tere.plugin.icerush.games.consts.IceRushKart;

public interface ItemBlock {

    String getName();
    ItemStack getItemStack();
    void use(Vehicle vehicle, IceRushKart iceRushKart);

}
