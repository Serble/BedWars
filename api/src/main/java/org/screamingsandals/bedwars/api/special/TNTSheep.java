package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 *
 */
public interface TNTSheep<G extends Game, P extends BWPlayer, T extends Team, E extends Wrapper, L extends Wrapper, LE extends Wrapper> extends SpecialItem<G, P, T> {
	/**
	 * @return
	 */
	LE getEntity();

	/**
	 * @return
	 */
	L getInitialLocation();
	
	/**
	 * @return
	 */
	E getTnt();
	
	/**
	 * @return
	 */
	double getSpeed();
	
	/**
	 * @return
	 */
	double getFollowRange();
}
