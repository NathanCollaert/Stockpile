package com.bankstonks;

import com.bankstonks.model.SlotState;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.events.GrandExchangeOfferChanged;

/**
 * Watches Grand Exchange offers and records the cost basis of every <b>buy</b> that fills.
 *
 * <p>A single buy is reported incrementally across many {@code GrandExchangeOfferChanged}
 * events. We diff each event against the last-seen state of that slot to extract only the
 * newly-filled quantity/spend. Because the previous state is persisted, offers that the
 * client re-broadcasts on login produce a zero delta and are not double-counted.</p>
 *
 * <p>{@code offer.getSpent() / offer.getQuantitySold()} gives the true average price paid,
 * including partial fills at different prices, so we accumulate spend directly.</p>
 */
@Slf4j
@Singleton
public class GrandExchangeBuyTracker
{
	private final PortfolioManager manager;

	/**
	 * True while the client is replaying the batch of existing GE offers that fires on login/hop.
	 * During this window an offer we have no baseline for was bought in a previous session (and
	 * recorded then), so we adopt it as the baseline instead of recording it again.
	 */
	private volatile boolean loginSync;

	@Inject
	public GrandExchangeBuyTracker(PortfolioManager manager)
	{
		this.manager = manager;
	}

	/** Marks the start of the login/hop offer replay (set true), or its end (set false). */
	public void setLoginSync(boolean syncing)
	{
		this.loginSync = syncing;
	}

	/** @return true if a buy was recorded (so the caller can refresh the UI). */
	public boolean handle(GrandExchangeOfferChanged event)
	{
		if (!manager.hasAccount())
		{
			return false;
		}

		final int slot = event.getSlot();
		final GrandExchangeOffer offer = event.getOffer();
		final GrandExchangeOfferState state = offer.getState();

		if (state == GrandExchangeOfferState.EMPTY)
		{
			// On login the client resets every slot to EMPTY before replaying the real offers.
			// Clearing here would drop the baseline (and the offer's lot identity), so a later
			// fill would start a new lot. Skip clearing during the login/hop replay window.
			if (!loginSync)
			{
				manager.clearSlot(slot);
				manager.save();
			}
			return false;
		}

		if (!isBuy(state))
		{
			// Sell offers do not affect cost basis; forget the slot so a later buy in the
			// same slot starts from a clean delta (but not during the login replay, as above).
			if (!loginSync)
			{
				manager.clearSlot(slot);
				manager.save();
			}
			return false;
		}

		final int itemId = offer.getItemId();
		final int quantitySold = offer.getQuantitySold();
		final long spent = offer.getSpent();

		SlotState prev = manager.getSlot(slot);
		boolean continuing = prev != null && prev.getItemId() == itemId && quantitySold >= prev.getQuantitySold();
		int prevQty = continuing ? prev.getQuantitySold() : 0;
		long prevSpent = continuing ? prev.getSpent() : 0;

		int deltaQty = quantitySold - prevQty;
		long deltaSpent = spent - prevSpent;

		// Partial fills of the same offer accumulate into one lot; a new offer gets a fresh id.
		long offerId = continuing ? prev.getLotId() : 0;

		boolean recorded = false;
		if (deltaQty > 0 && deltaSpent > 0)
		{
			if (loginSync && !continuing)
			{
				// Login/hop replay of an offer we have no baseline for: it was bought (and
				// recorded) in a prior session. Adopt it as the baseline without recording, so the
				// same buy is never counted twice. A later real fill diffs against this baseline.
				log.debug("login sync: adopting baseline item={} qty={} spent={} (not recorded)", itemId, quantitySold, spent);
			}
			else
			{
				if (offerId == 0)
				{
					offerId = manager.nextOfferId();
				}
				manager.recordOfferFill(itemId, deltaQty, deltaSpent, System.currentTimeMillis(), offerId);
				recorded = true;
				log.debug("recorded buy fill: item={} qty={} spent={} offer={}", itemId, deltaQty, deltaSpent, offerId);
			}
		}

		manager.setSlot(slot, new SlotState(itemId, quantitySold, spent, offerId));
		manager.save();
		return recorded;
	}

	private static boolean isBuy(GrandExchangeOfferState state)
	{
		return state == GrandExchangeOfferState.BUYING
			|| state == GrandExchangeOfferState.BOUGHT
			|| state == GrandExchangeOfferState.CANCELLED_BUY;
	}
}
