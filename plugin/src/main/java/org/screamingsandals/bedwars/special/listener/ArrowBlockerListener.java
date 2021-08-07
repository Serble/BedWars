package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.ArrowBlocker;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.entity.DamageCause;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.event.player.SPlayerClickedBlockEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ArrowBlockerListener {
    private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";

    @OnEvent
    public void onArrowBlockerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onPlayerUseItem(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManager.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (event.getAction() == SPlayerClickedBlockEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerClickedBlockEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
                var stack = event.getItem().as(ItemStack.class);
                var unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, ARROW_BLOCKER_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, ArrowBlocker.class)) {
                        event.setCancelled(true);

                        int protectionTime = Integer.parseInt(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        var arrowBlocker = new ArrowBlocker(game, event.getPlayer().as(Player.class),
                                game.getTeamOfPlayer(event.getPlayer().as(Player.class)), stack, protectionTime);

                        if (arrowBlocker.isActivated()) {
                            player.sendMessage(Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ALREADY_ACTIVATED).prefixOrDefault(game.getCustomPrefixComponent()));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactory(delay, arrowBlocker, gPlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        arrowBlocker.activate();
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(gPlayer, ArrowBlocker.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onDamage(SEntityDamageEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof EntityHuman)) {
            return;
        }

        var player = ((EntityHuman) entity).asPlayer();

        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManager.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (gPlayer.isSpectator) {
            return;
        }

        var arrowBlocker = (ArrowBlocker) game.getFirstActivedSpecialItemOfPlayer(player.as(Player.class), ArrowBlocker.class);
        if (arrowBlocker != null && event.getDamageCause() == DamageCause.PROJECTILE) {
            event.setCancelled(true);
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return ARROW_BLOCKER_PREFIX
                + MiscUtils.getIntFromProperty(
                "protection-time", "specials.arrow-blocker.protection-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.arrow-blocker.delay", event);
    }
}
