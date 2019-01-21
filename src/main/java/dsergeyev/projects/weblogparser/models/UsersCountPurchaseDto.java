package dsergeyev.projects.weblogparser.models;

import java.util.TreeSet;

public class UsersCountPurchaseDto {

	int totalPurchase;
	private TreeSet<UserCountPurchase> UsersCountPurchase;
	
	public int getTotalPurchase() {
		return totalPurchase;
	}
	public TreeSet<UserCountPurchase> getUsersCountPurchase() {
		return UsersCountPurchase;
	}
	
	public UsersCountPurchaseDto(int totalPurchase, TreeSet<UserCountPurchase> usersCountPurchase) {
		super();
		this.totalPurchase = totalPurchase;
		UsersCountPurchase = usersCountPurchase;
	}
}
