# Bank Stonks

*"I bought that ages ago… I wonder what it's worth now?"*

Bank Stonks is a RuneLite plugin that remembers what you paid for the items you buy on the
Grand Exchange and shows you, at a glance, how much **profit or loss** you're sitting on for
the ones you're still holding in your bank.

## The vision

This isn't a flipping tool and it isn't here to tell you when to sell. It exists for one
simple, satisfying reason: **curiosity**. You accumulate stuff over time, supplies you
stockpiled, gear you invested in, that pile of an item you were convinced would moon. And it
just sits in your bank. Bank Stonks quietly keeps score, so whenever you're curious you can see
how those old purchases have actually panned out.

## How it works

- Every time you **buy** something on the GE, Bank Stonks records the quantity and the *actual*
  average price you paid (partial fills included).
- Your **held quantity comes from your bank**, not from guessing. So selling, dropping,
  alching or trading is reflected automatically the next time your bank updates.
- Profit/loss for an item = **held quantity × (current wiki price − your average buy price)**.
- Held quantity is capped at what you actually *bought* (`min(bought, in bank)`), so free
  drops of an item you also bought never get counted as "profit".
- Charged/uncharged and dose variants are matched together, so buying an item uncharged,
  charging it, and banking the charged version still tracks correctly.

## Good to know

- Bank Stonks can only record buys **from the moment it's installed**; the game doesn't expose
  your old purchase history. Use **manual add** (with a Held-since date) to backfill older
  holdings.
- Category matching for charged/dose variants relies on RuneLite's item variation data, which
  covers the common cases but not every single item.