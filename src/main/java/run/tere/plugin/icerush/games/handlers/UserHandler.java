package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.consts.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserHandler {

    private List<User> users;

    public UserHandler() {
        this.users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(UUID playerUUID) {
        for (User user : users) {
            if (user.getPlayerUUID().equals(playerUUID)) {
                return user;
            }
        }
        User user = new User(playerUUID);
        users.add(user);
        return user;
    }

    public int getGoaledSize() {
        int size = 0;
        for (User user : users) {
            if (user.isGoal()) {
                size++;
            }
        }
        return size;
    }

}
