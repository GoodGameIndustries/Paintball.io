import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;
import com.ggi.paintballio.network.Die;
import com.ggi.paintballio.network.Login;
import com.ggi.paintballio.network.Network;
import com.ggi.paintballio.network.NewBullet;
import com.ggi.paintballio.network.PlayerUpdate;
import com.ggi.paintballio.network.ServerConn;
import com.ggi.paintballio.network.User;

public class PBServer {
	
	public Server server;
	
	private Client client;
	
	public ArrayList<Integer> blueTeam = new ArrayList<Integer>();
	public ArrayList<Integer> redTeam = new ArrayList<Integer>();
	
	private int[][] map;
	
	private int mapID;
	
	private ArrayList<PlayerUpdate> updates = new ArrayList<PlayerUpdate>();
	private ArrayList<Bot> bots = new ArrayList<Bot>();
	
	private long startTime = System.currentTimeMillis();
	private long sendTime = System.currentTimeMillis();
	
	private boolean debug = false;
	
	public String version = "1.0.0";
	
	private String dns = "";
	
	public PBServer(){
		mapID = (int) (Math.random()*3);
		//mapID = 1;
		if(!debug){
		try {
			dns = readFile("config",Charset.defaultCharset());
			dns.replaceAll("\n", "");
			dns = dns.substring(0, dns.length()-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		
		System.out.println("Starting server...");
		System.out.println(dns);
		System.out.println(dns.length());
		//genMap();
		
		runServer();
		if(!debug){
		createClient();}
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("Thead running");
			while(true){
				//System.out.println("Checking for updates");
				if(System.currentTimeMillis()-sendTime>10000){
				connect();
				ServerConn  s = new ServerConn();
				s.dns=dns;
				s.connections=blueTeam.size()+redTeam.size();
				if(!debug){client.sendTCP(s);}
				sendTime = System.currentTimeMillis();
				}
				if(System.currentTimeMillis()-startTime>21600000){System.exit(0);}
				System.out.print("");
				if(updates.size()>0){
					System.out.println("Updates available");
					if(updates.get(0)!=null){server.sendToAllTCP(updates.get(0));}
					updates.remove(0);
					System.out.println("Update sent");
				}
			}
				
			}
			
		});
		t.start();
		//genBots();
	}
	
	private void genBots() {
		for(int i = 0; i < 1; i++){
			Bot b = new Bot(this);
			bots.add(b);
			Thread t = new Thread(b);
			t.start();
		}
		
	}

	private void runServer(){
		server = new Server(8192,512);
		server.start();
		try {
			server.bind(30000,31111);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		Network.register(server);
		server.addListener(new ThreadedListener(new Listener(){
			public void connected(Connection connection){
				System.out.println("New connection! " + connection.getID()+ " " + connection.getRemoteAddressTCP());
			}
			
			public void disconnected(Connection connection){
				Die d = new Die();
				d.killerID=-1;
				d.playerID=connection.getID();
				if(blueTeam.remove((Integer)(connection.getID()))){d.playerTeam=0;}
				if(redTeam.remove((Integer)(connection.getID()))){d.playerTeam=1;}
				server.sendToAllTCP(d);
				
				if(blueTeam.size()+redTeam.size()>server.getConnections().length){
					blueTeam.clear();
					redTeam.clear();
				}
			}
			
			public void received(Connection connection, Object object){
				if(object instanceof Login){
					System.out.println("Login received");
					Login o = (Login) object;
					User u = new User();
					u.playerID=connection.getID();
					u.name=o.user;
					u.map=mapID;
					//u.map=map;
					if(redTeam.size()<blueTeam.size()){
						u.team=1;
						redTeam.add(connection.getID());
					}else{u.team=0;blueTeam.add(connection.getID());}
					connection.sendTCP(u);
				}
				else if(object instanceof PlayerUpdate){
					PlayerUpdate o = (PlayerUpdate) object;
					addUpdate(o);
					
					if(o.team==0&&!blueTeam.contains(o.playerID)){blueTeam.add(o.playerID);}
					if(o.team==1&&!redTeam.contains(o.playerID)){redTeam.add(o.playerID);}
				
				}
				else if(object instanceof NewBullet){
					NewBullet o = (NewBullet) object;
					server.sendToAllTCP(o);
				
				}
				else if(object instanceof Die){
					Die o = (Die) object;
					if(o.playerTeam==0){blueTeam.remove((Integer)(o.playerID));}
					else{redTeam.remove((Integer)(o.playerID));}
					server.sendToAllTCP(o);
				
				}
			}
		}));
	}
	
	protected void addUpdate(PlayerUpdate o) {
		for(int i = 0; i < updates.size(); i++){
			if(updates.get(i).playerID==o.playerID){
				updates.set(i, o);
				System.out.println("Update refreshed");
				return;
			}
		}
		updates.add(o);
		System.out.println("Update added");
		
	}

	public static void main(String[] args){
		new PBServer();
	}
	
	public void createClient() {
		client = new Client();
		client.addListener(new ThreadedListener(new Listener() {
			public void received(Connection connection, Object object) {
				
			}
		}));
		client.start();
		Network.register(client);
	}
	
	public void connect(){
		if(client!=null&&!client.isConnected()){
			try {
				client.connect(5000,debug?"localhost":"ec2-54-71-24-203.us-west-2.compute.amazonaws.com",30001,31112);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
}
