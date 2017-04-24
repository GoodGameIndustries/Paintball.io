import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.ggi.paintballio.network.Die;
import com.ggi.paintballio.network.Login;
import com.ggi.paintballio.network.Network;
import com.ggi.paintballio.network.NewBullet;
import com.ggi.paintballio.network.PlayerUpdate;
import com.ggi.paintballio.network.User;

public class Bot implements Runnable{
	
	private PBServer s;
	
	private Client client;
	
	private User user;
	
	private int[][] map;
	
	private ArrayList<User> players = new ArrayList<User>();
	private ArrayList<NewBullet> bullets = new ArrayList<NewBullet>();
	private ArrayList<Point2D> path = new ArrayList<Point2D>();
	
	private boolean render = false;
	
	private boolean dead = false;
	
	private Point2D moveTo = null;
	private Point2D redSpawn;
	private Point2D blueSpawn;
	
	private long lastRender = System.currentTimeMillis();
	
	private ArrayList<Point2D> lastCase = new ArrayList<Point2D>();
	
	public Bot(PBServer s){
		this.s=s;
		createClient();
		map = testmap();
	}

	@Override
	public void run() {
		while(true){
			if(s.redTeam.size()+s.blueTeam.size() < 6){
				Login l = new Login();
				l.user="Bot";
				l.version= s.version;
				send(l);
			}
			while(user==null){System.out.print("");}
			spawn();
			while(!dead){
				System.out.print("");
				if(System.currentTimeMillis()-lastRender>200){
				render = true;
				if(moveTo == null){
					//System.out.println("genpath");
					while(path.size()==0){genPath();}
					//System.out.println("egenpath");
					moveTo = path.get(0);
					path.remove(0);
				}
				//System.out.println("move");
				user.angle=getAngle(moveTo,new Point2D(user.x,user.y));
				user.x=moveTo.x;
				user.y=moveTo.y;
				moveTo = null;
				User u = visibleEnemy();
				if(u!=null){
					//shoot
				}
				
				sendUpdate();
				//if()
				//System.out.println("Update");
				render = false;
				lastRender = System.currentTimeMillis();
				}
			}
		}
		
	}
	
	private User visibleEnemy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	private void spawn() {
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[i].length; j++){
				if(map[i][j]==2){blueSpawn = new Point2D(j,map.length-i);}
				if(map[i][j]==3){redSpawn = new Point2D(j,map.length-i);}
			}
		}
		
		if(user.team==0){
			user.x=blueSpawn.x+.5f;
			user.y=blueSpawn.y+.5f;
			user.servX=blueSpawn.x+.5f;
			user.servY=blueSpawn.y+.5f;
			//hasSpawned=true;
		}
		else if(user.team==1){
			user.x=redSpawn.x+.5f;
			user.y=redSpawn.y+.5f;
			user.servX=redSpawn.x+.5f;
			user.servY=redSpawn.y+.5f;
			//hasSpawned=true;
		}
		sendUpdate();
		
		
	}

	private void sendUpdate() {
		PlayerUpdate up = new PlayerUpdate();
		up.angle = user.angle;
		up.hand=user.hand;
		up.kills=user.kills;
		up.name=user.name;
		up.playerID=user.playerID;
		up.team=user.team;
		up.x=user.x+.5f;
		up.y=user.y+.5f;
		send(up);
		
	}

	private void genPath() {
		try{
			
		switch((int)(Math.random()*8)){
		case 0: path.add(new Point2D(Math.round(user.x+1),Math.round(user.y)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 1: path.add(new Point2D(Math.round(user.x-1),Math.round(user.y)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 2: path.add(new Point2D(Math.round(user.x),Math.round(user.y+1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 3: path.add(new Point2D(Math.round(user.x),Math.round(user.y-1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 4: path.add(new Point2D(Math.round(user.x+1),Math.round(user.y+1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 5: path.add(new Point2D(Math.round(user.x-1),Math.round(user.y-1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 6: path.add(new Point2D(Math.round(user.x+1),Math.round(user.y-1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		case 7: path.add(new Point2D(Math.round(user.x-1),Math.round(user.y+1)));
		if(map[(int) (map.length-path.get(0).y)][(int) path.get(0).x]!= 1){break;}
		path.remove(0);
		}
			if(!lastCase.contains(path.get(0))){
				lastCase.add(path.get(0));
				if(lastCase.size()>3){lastCase.remove(0);}
			}
			else{
				path.remove(0);
			}
		}catch(Exception e){
			
		}
	}

	public void createClient() {
		client = new Client(4096,256);
		client.addListener(new ThreadedListener(new Listener() {
			public void received(Connection connection, Object object) {
				//System.out.println("object received");
				if(object instanceof User){
					System.out.println("User received");
					User o = (User) object;
					user = o;
					//map = user.map;
				}
				
				else if(object instanceof PlayerUpdate){
					while(render){};
					PlayerUpdate o = (PlayerUpdate) object;
					if(user!=null && user.playerID!=o.playerID){
					User u = findPlayer(o.playerID);
					if(u.x == -1 || u.y == -1){
						u.x=o.x;
						u.y=o.y;
					}
					u.angle=o.angle;
					u.hand=o.hand;
					u.kills=o.kills;
					u.name=o.name;
					u.playerID=o.playerID;
					u.team=o.team;
					u.lastX=u.x;
					u.lastY=u.y;
					//u.x=o.x;
					//u.y=o.y;
					u.servX=o.x;
					u.servY=o.y;
					u.rCount=0;
					}
				}
				
				else if(object instanceof NewBullet){
					while(render){};
					NewBullet o = (NewBullet) object;
					if(user!=null&&o.shootID!=user.playerID){
						User u = findPlayer(o.shootID);
						//u.angle=new Vector2(o.xA-u.x,o.yA-u.y).angle();
						u.angle=getAngle(new Point2D(o.xA,o.yA), new Point2D(u.x,u.y));
						switch(u.hand){
						case 0:
						o.x=(float) (u.x+1/3f*Math.sin(Math.toRadians(u.angle)));
						o.y=(float) (u.y-1/3f*Math.cos(Math.toRadians(u.angle)));
							break;
						case 1:
						o.x=(float) (u.x-1/3f*Math.sin(Math.toRadians(u.angle)));
						o.y=(float) (u.y+1/3f*Math.cos(Math.toRadians(u.angle)));
							break;
						}
					bullets.add(o);
					}
				}
				else if(object instanceof Die){
					Die o = (Die)object;
					if(o.playerID!=user.playerID){
					User dying = findPlayer(o.playerID);
					players.remove(dying);
					if(o.killerID!=user.playerID&&o.killerID>=0){
					User killing = findPlayer(o.killerID);
					killing.kills++;}
					else if(o.killerID>=0){
						user.kills++;
					}
					}
				}
				
			}
		}));
		client.start();
		Network.register(client);
	}
	
	protected User findPlayer(int playerID) {
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).playerID==playerID){
				return players.get(i);
			}
		}
		User u = new User();
		players.add(u);
		
		return u;
	}
	
	public float getAngle(Point2D target, Point2D me) {
	    float angle = (float) Math.toDegrees(Math.atan2(target.y - me.y, target.x - me.x));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
	}

	public void send(Object o){
		if(!client.isConnected()){
			try {
				client.connect(5000, "localhost",30000,31111);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		client.sendTCP(o);
	}
	
	private int[][] testmap(){
		int map[][] = {
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,},
				{1,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,1,},
				{1,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,},
				{1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,},
				{1,0,2,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,3,0,1,},
				{1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,},
				{1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1,0,0,0,0,0,0,0,1,},
				{1,0,0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,1,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,0,0,1,},
				{1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,},
		};
		
		return map;
	}
}
