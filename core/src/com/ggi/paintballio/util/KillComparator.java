package com.ggi.paintballio.util;

import java.util.Comparator;

import com.ggi.paintballio.network.User;

public class KillComparator implements Comparator<User>{



	@Override
	public int compare(User o1, User o2) {
		return o2.kills-o1.kills;
	}

}
