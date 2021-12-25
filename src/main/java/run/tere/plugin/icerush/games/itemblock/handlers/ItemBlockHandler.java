package run.tere.plugin.icerush.games.itemblock.handlers;

import run.tere.plugin.icerush.games.itemblock.impl.AttackerItemBlock;
import run.tere.plugin.icerush.games.itemblock.impl.DashItemBlock;
import run.tere.plugin.icerush.games.itemblock.impl.PlayerSwapperItemBlock;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBlockHandler {

    private List<ItemBlock> itemBlocks;

    public ItemBlockHandler() {
        this.itemBlocks = new ArrayList<>();
        init();
    }

    private void init() {
        itemBlocks.add(new AttackerItemBlock());
        itemBlocks.add(new DashItemBlock());
        itemBlocks.add(new PlayerSwapperItemBlock());
    }

    public List<ItemBlock> getItemBlocks() {
        return itemBlocks;
    }

    public ItemBlock getCopiedRandomItemBlock() {
        List<ItemBlock> copiedItemBlocks = new ArrayList<>(this.itemBlocks);
        Collections.shuffle(copiedItemBlocks);
        return copiedItemBlocks.get(0);
    }

    public ItemBlock getItemBlock(String name) {
        for (ItemBlock itemBlock : itemBlocks) {
            if (itemBlock.getName().equalsIgnoreCase(name)) {
                return itemBlock;
            }
        }
        return null;
    }

}
