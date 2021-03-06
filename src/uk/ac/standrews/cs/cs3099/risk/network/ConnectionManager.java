package uk.ac.standrews.cs.cs3099.risk.network;

import uk.ac.standrews.cs.cs3099.risk.commands.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionManager {
	private final boolean isServer;
	private final ArrayList<PlayerSocket> playerSockets = new ArrayList<PlayerSocket>();

	private NetworkedGame game;
	private HostServer hostServer;

	/**
	 * Create a new server instance, listening on the specified port.
	 *
	 * @param port The port to listen for incoming connections.
	 */
	public ConnectionManager(NetworkedGame game, int port) throws IOException
	{
		this.game = game;
		this.isServer = true;
		ServerSocket socket = new ServerSocket(port);
		hostServer = new HostServer(this, socket);
		new Thread(hostServer).start();
	}

	/**
	 * Create a new host instance, connecting to the specified host:port.
	 *
	 * @param hostname The hostname of the server to connect to.
	 * @param port     The port to connect on.
	 */
	public ConnectionManager(NetworkedGame game, String hostname, int port) throws IOException
	{
		this.game = game;
		this.isServer = false;
		Socket socket = new Socket(hostname, port);

		PlayerSocket playerSocket = new PlayerSocket(game, socket);
		playerSockets.add(playerSocket);
		new Thread(playerSocket).start();
	}

	/**
	 * Send the specified command to all connected clients (either a single host for forwarding, or all clients).
	 *
	 * @param command The command to send.
	 */
	public void sendCommand(Command command)
	{
		System.out.println("Sending command: " + command.toJSON());

		for (PlayerSocket playerSocket : playerSockets) {
			if (playerSocket.getPlayerId() != command.getPlayerId()) {
				playerSocket.sendCommand(command);
			}
		}

		if (command.getAckId() > -1) {
			game.addAcknowledgement(command);
		}
	}

	/**
	 * Called by the {@link HostServer} instance when a new client connects to this host.
	 *
	 * @param socket The client socket for the newly connected client.
	 */
	protected void clientConnected(Socket socket)
	{
		try {
			PlayerSocket playerSocket = new PlayerSocket(game, socket);
			playerSockets.add(playerSocket);
			new Thread(playerSocket).start();
		} catch (IOException e) {
			System.err.println("Failed to process new client socket.");
			e.printStackTrace();
		}
	}

	/**
	 * @return true if acting as a host, false if not
	 */
	public boolean isServer()
	{
		return isServer;
	}

	/**
	 * Returns a socket for a specified player
	 * @param playerID - the id of the player who's socket you want
	 * @return the PlayerSocket of that player
	 */
	public PlayerSocket getSocketById(int playerID) {
		for(PlayerSocket socket: playerSockets){
			if (socket.getPlayerId()==playerID){
				return socket;
			}
		}
		return null;
	}

	/**
	 * Removes the specified player socket from the list of players sockets
	 * @param socket the socket to remove
	 */
	public void removePlayerSocket(PlayerSocket socket){
		playerSockets.remove(socket);
	}

	/**
	 * @return the list of player sockets
	 */
	public ArrayList<PlayerSocket> getPlayerSockets(){
		return playerSockets;
	}

	/**
	 * @return the host server
	 */
	public HostServer getHostServer(){
		return hostServer;
	}

}

