package uk.ac.standrews.cs.cs3099.risk.game;

public class TradeCardsMove extends Move {
	private Card[] cards;

	public TradeCardsMove(int playerId, int ackId)
	{
		super(playerId, ackId);
	}

	public TradeCardsMove(int playerId, int ackId, Card[] cards)
	{
		this(playerId, ackId);
		this.cards = cards;
	}

	/**
	 * @return Array of {@link Card} instances. Should be length 3, all unique and all of same type to be valid.
	 */
	public Card[] getCards()
	{
		return cards;
	}

	@Override
	public MoveType getType()
	{
		return MoveType.TRADE_IN_CARDS;
	}
}