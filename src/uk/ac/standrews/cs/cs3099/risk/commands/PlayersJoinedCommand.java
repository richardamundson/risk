package uk.ac.standrews.cs.cs3099.risk.commands;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PlayersJoinedCommand extends Command {
	private String command = "players_joined";
	private PlayersNames[] payload;

	public PlayersJoinedCommand(PlayersNames[] playerNames)
	{
		super(-1);
		this.payload = playerNames;
	}

	/**
	 * @return 2D array of player ID/name pairs.
	 */
	public PlayersNames[] getPlayerNames()
	{
		return payload;
	}

	@Override
	public CommandType getType()
	{
		return CommandType.PLAYERS_JOINED;
	}

	public static class PlayersNames {
		private int playerId;
		private String playerName;
		private String publicKey;

		public PlayersNames(int playerId, String playerName, String publicKey)
		{
			this.playerId = playerId;
			this.playerName = playerName;
			this.publicKey = publicKey;
		}

		public int getPlayerId()
		{
			return playerId;
		}

		public String getPlayerName()
		{
			return playerName;
		}

		public String getPublicKey()
		{
			return publicKey;
		}
	}

	public static class PlayersNamesDeserializer implements JsonDeserializer<PlayersNames> {
		@Override
		public PlayersNames deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			JsonArray jsonArray = json.getAsJsonArray();
			int playerId = jsonArray.get(0).getAsInt();
			String playerName = jsonArray.get(1).getAsString();
			String publicKey = jsonArray.get(2).getAsString();
			return new PlayersNames(playerId, playerName, publicKey);
		}
	}
}
