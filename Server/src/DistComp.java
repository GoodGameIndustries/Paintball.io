import java.util.Comparator;

import com.ggi.paintballio.network.User;

public class DistComp implements Comparator<User> {

	private User u;
	
	public DistComp(User u){
		this.u=u;
	}
	
	@Override
	public int compare(User o1, User o2) {
		if(Math.abs(Math.pow(o1.x-u.x, 2)+Math.pow(o1.y-u.y, 2)) < Math.abs(Math.pow(o2.x-u.x, 2)+Math.pow(o2.y-u.y, 2))){
			return -1;
		}
		else{
			return 1;
		}
	}
	

}
