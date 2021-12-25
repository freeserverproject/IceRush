package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.consts.IceRushKart;

import java.util.*;

public class IceRushKartHandler {

    private List<IceRushKart> iceRushKarts;

    public IceRushKartHandler() {
        this.iceRushKarts = new ArrayList<>();
    }

    public List<IceRushKart> getIceRushKarts() {
        return iceRushKarts;
    }

    public IceRushKart getIceRushKart(UUID uuid) {
        for (IceRushKart iceRushKart : this.iceRushKarts) {
            if (iceRushKart.getKartUUID().equals(uuid)) {
                return iceRushKart;
            }
        }
        return null;
    }

    public IceRushKart getRandomIceRushKart(IceRushKart ignoreIceRushKart) {
        List<IceRushKart> iceRushKarts = new ArrayList<>(this.iceRushKarts);
        iceRushKarts.remove(ignoreIceRushKart);
        if (iceRushKarts.isEmpty()) return null;
        Collections.shuffle(iceRushKarts);
        return iceRushKarts.get(0);
    }

}
