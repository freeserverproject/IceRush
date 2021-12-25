package run.tere.plugin.icerush.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.Inventory;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.Course;
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
            sender.sendMessage("§a/icerush§f: このヘルプを表示する");
            sender.sendMessage("§a/icerush start§f: ゲームを開始する");
            sender.sendMessage("§a/icerush result§f: 結果を表示");
            sender.sendMessage("§a/icerush reset§f: ゲームをリセットする");
            sender.sendMessage("§a/icerush get <dash/jump/itemblock/checkpoint/returncp>§f: 各種アイテムを取得する");
            sender.sendMessage("§a/icerush addkart§f: カートを追加する");
            sender.sendMessage("§a/icerush course set§f: コースをセットする");
            sender.sendMessage("§a/icerush course add§f: 現在のワールドをコースとして追加する");
            sender.sendMessage("§a/icerush course remove§f: 現在のワールドをコースから削除する");
            sender.sendMessage("§a/icerush course lap [value]§f: 現在のワールドの周回回数を変更する");
            sender.sendMessage("§a/icerush course bgm [FileName]§f: 現在のワールドのコースのBGMを変更する");
            sender.sendMessage("§a/icerush course maxcp [value]§f: 現在のワールドのコースの最大チェックポイント数を変更する");
            sender.sendMessage("§a/icerush course list§f: コースのリストを表示する");
        } else {
            switch (args[0]) {
                case "start":
                    IceRush.getPlugin().getGameHandler().prepareGame();
                    ChatUtil.sendMessage(sender, "§aIceRushを開始しました!");
                    break;
                case "result":
                    IceRush.getPlugin().getGameHandler().showResult();
                    break;
                case "reset":
                    IceRush.getPlugin().getGameHandler().resetGame();
                    ChatUtil.sendMessage(sender, "§aIceRushをリセットしました!");
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
                        case "returncp":
                            inventory.addItem(ObjectUtil.getReturnCheckpoint());
                            ChatUtil.sendMessage(sender, "§aReturnCheckpointを取得しました!");
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
                case "course":
                    if (args.length == 1) {
                        ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                        return false;
                    }
                    if (!(sender instanceof Player player)) {
                        ChatUtil.sendMessage(sender, "§cプレイヤーのみ実行できます!");
                        return false;
                    }
                    World world = player.getWorld();
                    switch (args[1]) {
                        case "set":
                            if (args.length != 2) {
                                ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                                return false;
                            }
                            IceRush.getPlugin().getGameHandler().setNowCourseUUID(world.getUID());
                            ChatUtil.sendMessage(sender, "§aコースを設定しました!");
                            break;
                        case "add":
                            if (IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(world.getUID()) != null) {
                                ChatUtil.sendMessage(sender, "§cそのワールドはすでにコースに追加されています!");
                                return false;
                            }
                            IceRush.getPlugin().getGameHandler().getCourseHandler().addCourse(new Course(world.getUID()));
                            ChatUtil.sendMessage(sender, "§aコースを追加しました!");
                            break;
                        case "remove":
                            Course course = IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(world.getUID());
                            if (course == null) {
                                ChatUtil.sendMessage(sender, "§cそのワールドはコースに追加されていません!");
                                return false;
                            }
                            IceRush.getPlugin().getGameHandler().getCourseHandler().removeCourse(course);
                            ChatUtil.sendMessage(sender, "§aコースを削除しました!");
                            break;
                        case "list":
                            ChatUtil.sendMessage(sender, "§aコース一覧:");
                            for (Course allCourse : IceRush.getPlugin().getGameHandler().getCourseHandler().getCourseList()) {
                                World allWorld = Bukkit.getWorld(allCourse.getWorldUUID());
                                if (allWorld == null) continue;
                                ChatUtil.sendMessage(sender, "§a" + allWorld.getName());
                            }
                            break;
                        case "lap":
                            if (args.length != 3) {
                                ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                                return false;
                            }
                            Course lapWorldCourse = IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(world.getUID());
                            lapWorldCourse.setLapSize(Integer.parseInt(args[2]));
                            ChatUtil.sendMessage(sender, "§aラップサイズを設定しました! " + args[2]);
                            break;
                        case "bgm":
                            if (args.length != 3) {
                                ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                                return false;
                            }
                            Course bgmWorldCourse = IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(world.getUID());
                            bgmWorldCourse.setBGM(args[2]);
                            ChatUtil.sendMessage(sender, "§aBGMを設定しました! " + args[2]);
                            break;
                        case "maxcp":
                            if (args.length != 3) {
                                ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                                return false;
                            }
                            Course maxcpWorldCourse = IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(world.getUID());
                            maxcpWorldCourse.setMaxCheckpointSize(Integer.parseInt(args[2]));
                            ChatUtil.sendMessage(sender, "§aチェックポイントの最大数を設定しました! " + args[2]);
                            break;
                        default:
                            ChatUtil.sendMessage(sender, "§cコマンドが不正です!");
                            return false;
                    }
                    break;
                default:
                    ChatUtil.sendMessage(sender, "§cそのコマンドは存在しません!");
                    return false;
            }
        }
        return true;
    }

}
