package run.tere.plugin.icerush.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.Inventory;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;

public class IceRushCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!command.getName().equalsIgnoreCase("icerush")) return false;
        if (!sender.isOp()) {
            ChatUtil.sendMessage(sender, "§cあなたは必要な権限を持っていません!");
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage("§f======== [§bIceRush§f] ========");
            sender.sendMessage("§a/icerush§f: このヘルプを表示");
            sender.sendMessage("§a/icerush start§f: ゲームを開始");
            sender.sendMessage("§a/icerush get <dash/jump/itemblock/checkpoint>§f: 各種アイテムを取得する");
            sender.sendMessage("§a/icerush addkart§f: カートを追加する");
        } else {
            switch (args[0]) {
                case "start":

                    ChatUtil.sendMessage(sender, "§aIceRushを開始しました!");
                    break;
                case "get":
                    if (!(sender instanceof Player player)) {
                        ChatUtil.sendMessage(sender, "§cプレイヤーのみ実行できます!");
                        return false;
                    }
                    Inventory inventory = player.getInventory();
                    switch (args[1]) {
                        case "dash":
                            inventory.addItem(ObjectUtil.getDashboard());
                            ChatUtil.sendMessage(sender, "§aDashを取得しました!");
                            break;
                        case "jump":
                            inventory.addItem(ObjectUtil.getJumpboard(args[2]));
                            ChatUtil.sendMessage(sender, "§aJumpを取得しました!");
                            break;
                        case "itemblock":
                            inventory.addItem(ObjectUtil.getItemBlock());
                            ChatUtil.sendMessage(sender, "§aItemBlockを取得しました!");
                            break;
                        case "checkpoint":
                            inventory.addItem(ObjectUtil.getCheckpoint());
                            ChatUtil.sendMessage(sender, "§aCheckpointを取得しました!");
                            break;
                        default:
                            ChatUtil.sendMessage(sender, "§cそのアイテムは存在しません!");
                            return false;
                    }
                    break;
                    case "addkart":
                        if (!(sender instanceof Player player)) {
                            ChatUtil.sendMessage(sender, "§cプレイヤーのみ実行できます!");
                            return false;
                        }
                        Entity vehicle = player.getVehicle();
                        if (vehicle == null) {
                            ChatUtil.sendMessage(sender, "§cカートに乗っていません!");
                            return false;
                        }
                        IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getIceRushKarts().add(new IceRushKart(vehicle.getUniqueId()));
                        ChatUtil.sendMessage(sender, "§aカートを追加しました!");
                        break;
                default:
                    ChatUtil.sendMessage(sender, "§cそのコマンドは存在しません!");
                    return false;
            }
        }
        return true;
    }

}
