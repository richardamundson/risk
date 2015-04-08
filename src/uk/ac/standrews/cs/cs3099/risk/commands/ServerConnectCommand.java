package uk.ac.standrews.cs.cs3099.risk.commands;

/**
 * Move to represent the server_connect command. Used only for the internal frontend server.
 */
public class ServerConnectCommand extends Command {
	private ServerConnectPayload payload;

	public ServerConnectCommand(int playerId)
	{
		super(playerId);
	}

	public String getHostname()
	{
		return this.payload.hostname;
	}

	public int getPort()
	{
		return this.payload.port;
	}

	@Override
	public CommandType getType()
	{
		return CommandType.SERVER_CONNECT;
	}

	private class ServerConnectPayload {
		String hostname;
		int port;
	}
}
