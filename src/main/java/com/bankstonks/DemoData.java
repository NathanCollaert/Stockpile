package com.bankstonks;

import java.util.Arrays;
import java.util.List;

/**
 * A built-in sample portfolio of iconic "buy once" end-game items, used only when Demo mode is
 * enabled. Buy prices and dates are real historical values from the OSRS Wiki price API (the same
 * data GE trackers use). Demo mode renders from this list and never reads or writes your real
 * tracked data.
 */
final class DemoData
{
	private DemoData()
	{
	}

	/** One sample holding: an item bought once at a real past price/date. */
	static final class Entry
	{
		final int itemId;    // item shown (icon + name)
		final int priceId;   // item used for the live GE price (see charged/uncharged note below)
		final long spent;    // gp paid (quantity 1, so this is the unit buy price)
		final long epochMs;  // when it was bought

		Entry(int itemId, long spent, long epochMs)
		{
			this(itemId, itemId, spent, epochMs);
		}

		/**
		 * Charged Scythe/Shadow are not tradeable, so they have no live price. We show the charged
		 * item (it looks complete) but value it at its tradeable uncharged variant's price.
		 */
		Entry(int itemId, int priceId, long spent, long epochMs)
		{
			this.itemId = itemId;
			this.priceId = priceId;
			this.spent = spent;
			this.epochMs = epochMs;
		}
	}

	static final List<Entry> ITEMS = Arrays.asList(
		new Entry(20997, 1_587_159_226L, 1_757_894_400_000L),        // Twisted bow, 2025-09-15
		new Entry(22325, 22486, 1_500_977_607L, 1_759_276_800_000L), // Scythe of vitur (charged look, uncharged price), 2025-10-01
		new Entry(27275, 27277, 901_982_059L, 1_755_734_400_000L),   // Tumeken's shadow (charged look, uncharged price), 2025-08-21
		new Entry(12817, 538_328_661L, 1_762_819_200_000L),   // Elysian spirit shield, 2025-11-11
		new Entry(26382, 229_346_511L, 1_757_030_400_000L),   // Torva full helm, 2025-09-05
		new Entry(26384, 232_739_950L, 1_757_030_400_000L),   // Torva platebody, 2025-09-05
		new Entry(26386, 178_225_361L, 1_757_030_400_000L),   // Torva platelegs, 2025-09-05
		new Entry(21021, 140_736_518L, 1_761_004_800_000L),   // Ancestral robe top, 2025-10-21
		new Entry(21024, 109_006_157L, 1_761_004_800_000L),   // Ancestral robe bottom, 2025-10-21
		new Entry(13239, 24_118_510L, 1_764_633_600_000L),    // Primordial boots, 2025-12-02
		new Entry(22324, 30_587_614L, 1_761_955_200_000L),    // Ghrazi rapier, 2025-11-01
		new Entry(27238, 80_652_284L, 1_759_708_800_000L),    // Masori body (f), 2025-10-06
		new Entry(27690, 31_311_908L, 1_765_843_200_000L),    // Voidwaker, 2025-12-16
		new Entry(13652, 51_513_805L, 1_768_089_600_000L)     // Dragon claws, 2026-01-11
	);
}
