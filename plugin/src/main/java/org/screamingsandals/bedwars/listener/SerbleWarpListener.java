package org.screamingsandals.bedwars.listener;

import net.serble.serblenetworkplugin.API.Schemas.WarpEvent;
import net.serble.serblenetworkplugin.API.Schemas.WarpEventListener;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;

public class SerbleWarpListener implements WarpEventListener {

    @Override
    public boolean onWarpEvent(WarpEvent warpEvent) {
        if (!Main.isPlayerInGame(warpEvent.getPartyLeader())) {
            return false;
        }

        Game game = Main.getPlayerGameProfile(warpEvent.getPartyLeader()).getGame();

        if (Main.isPlayerInGame(warpEvent.getTarget())) {
            Main.getPlayerGameProfile(warpEvent.getTarget()).changeGame(game, false);
        } else {
            game.joinToGame(warpEvent.getTarget());
        }
        return true;
    }

}
