package dsergeyev.projects.weblogparser.models;

public class UserCountPurchase implements Comparable<UserCountPurchase> {

	int purchaseCount;
	int usersCount;
	
	public int getPurchaseCount() {
		return purchaseCount;
	}
	public int getUsersCount() {
		return usersCount;
	}
	
	public UserCountPurchase(int purchaseCount, int usersCount) {
		super();
		this.purchaseCount = purchaseCount;
		this.usersCount = usersCount;
	}
	
	@Override
	public int compareTo(UserCountPurchase o) {
		return Integer.compare(this.purchaseCount, o.getPurchaseCount());
	}
}
