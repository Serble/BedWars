package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;

public class RejoinCommand extends BaseCommand {
    public RejoinCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "rejoin", BedWarsPermission.REJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        // TODO: Use Wrapper (bedwars changes needed)
                        var player = commandContext.getSender().as(Player.class);
                        if (Main.isPlayerInGame(player)) {
                            player.sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix().asComponent()));
                            return;
                        }

                        String name = null;
                        if (Main.isPlayerGameProfileRegistered(player)) {
                            name = Main.getPlayerGameProfile(player).getLatestGameName();
                        }
                        if (name == null) {
                            player.sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix().asComponent()));
                        } else {
                            GameManager.getInstance().getGame(name)
                                    .ifPresentOrElse(
                                            game -> game.joinToGame(player),
                                            () -> player.sendMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_ERRORS_GAME_IS_GONE).defaultPrefix().asComponent()))
                                    );
                        }
                    })
        );
    }
}
