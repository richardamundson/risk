import Model = require('./model');
import Collection = require('./collection');
import Player = require('./player');
import Messages = require('./messages');
import Map = require('./map');

var defaultMapJson = require('../../default-map.json');

var HOST : string = 'ws://localhost:7574';

class Game extends Model {
	private socket : WebSocket;

	playerList : Collection<Player>;
	self : Player;
	map : Map;

	constructor(options?) {
		super(options);
		this.playerList = new Collection<Player>();
		this.map = new Map();
		this.map.fromJSON(defaultMapJson);
	}

	connect(host : string, port : number) : Promise<Messages.Message> {
		return new Promise<Messages.Message>((resolve, reject) => {
			// Connect to Game server and attempt to join a game.
			this.socket = new WebSocket(HOST);
			this.socket.onmessage = this.messageReceived.bind(this);
			this.socket.onopen = () => {
				this.sendMessage({
					command: 'server_connect',
					payload: {
						hostname: host,
						port: port
					}
				});
			};

			this.once('acceptJoinGame', (message : Messages.Message) => {
				this.off('rejectJoinGame');
				this.trigger('connected');
				resolve(message);
			});

			this.once('rejectJoinGame', (message : Messages.Message) => {
				this.off('acceptJoinGame');
				reject(message);
			});
		});
	}

	showToast(message : string) {
		this.trigger('toast', message);
	}

	updateArmyCounts() : void {
		// Initialise array of army/territory counts to 0.
		var armyCounts = this.playerList.map(function(player) {
			return 0;
		});

		var territoryCounts = armyCounts.slice(0);

		// Sum up counts from every territory.
		this.map.territories.forEach(function(territory) {
			var player = territory.getOwner();

			if (!player) {
				return;
			}

			territoryCounts[player.id]++;
			armyCounts[player.id] += territory.getArmies();
		});

		// Update counts on each player.
		this.playerList.forEach(function(player) {
			player.setArmies(armyCounts[player.id]);
			player.setTerritories(territoryCounts[player.id]);
		});
	}

	private messageReceived(event : MessageEvent) {
		var message : Messages.Message = JSON.parse(event.data);

		switch (message.command) {
			case 'accept_join_game':
				this.acceptJoinGameMessageReceived(<Messages.AcceptJoinGameMessage>message);
				break;

			case 'reject_join_game':
				this.rejectJoinGameMessageReceived(<Messages.RejectJoinGameMessage>message);
				break;

			case 'players_joined':
				this.playersJoinedMessageReceived(<Messages.PlayersJoinedMessage>message);
				break;

			case 'ping':
				this.pingMessageReceived(<Messages.PingMessage>message);
				break;

			case 'initialise_game':
				this.initialiseGameMessageReceived(<Messages.InitialiseGameMessage>message);
				break;

			case 'setup':
				this.setupMessageReceived(<Messages.SetupMessage>message);
				break;
		}
	}

	private sendMessage(json : Messages.Message) {
		this.socket.send(JSON.stringify(json));
	}

	private acceptJoinGameMessageReceived(message : Messages.AcceptJoinGameMessage) {
		this.self = new Player({
			player_id: message.payload.player_id,
			name: 'TODO Implement player names'
		});

		this.playerList.add(this.self);
		this.trigger('acceptJoinGame', message);
	}

	private rejectJoinGameMessageReceived(message : Messages.RejectJoinGameMessage) {
		this.trigger('rejectJoinGame', message);
	}

	private playersJoinedMessageReceived(message : Messages.PlayersJoinedMessage) {
		message.payload.forEach((playerInfo) => {
			var player = new Player({
				player_id: playerInfo[0],
				name: playerInfo[1]
			});

			this.playerList.add(player);
		}, this);
	}

	private pingMessageReceived(message : Messages.PingMessage) {
		// Ignore pings from non-player hosts.
		if (message.player_id === null) {
			return;
		}

		var player : Player = this.playerList.get(message.player_id);
		player.isActive = true;
	}

	private initialiseGameMessageReceived(message : Messages.InitialiseGameMessage) {
		this.set({
			version: message.payload.version,
			features: message.payload.supported_features
		});

		this.trigger('gameStart');
	}

	private setupMessageReceived(message : Messages.SetupMessage) {
		var player = this.playerList.get(message.player_id);
		var territory = this.map.territories.get(message.payload);

		// Tell the user what happened.
		var toast = player.name + ' claimed ' + territory.getName();

		if (territory.getOwner()) {
			toast = player.name + ' reinforced ' + territory.getName();
		}

		this.showToast(toast);

		this.handleSetupMessage(message);
	}

	public handleSetupMessage(message : Messages.SetupMessage) {
		var player = this.playerList.get(message.player_id);
		var territory = this.map.territories.get(message.payload);

		// Claim/reinforce the specified territory.
		territory.setOwner(player);
		territory.addArmies(1);

		this.updateArmyCounts();

		// Trigger change to update the map view.
		this.trigger('change:map');
	}

	private rollResultMessageReceived(message : Messages.RollResultMessage) {

	}
}

export = Game;
