package dsergeyev.projects.weblogparser.models;

public class CountHourDto implements Comparable<CountHourDto> {

	private int hour;
	private int queryCount;

	public int getHour() {
		return hour;
	}
	
	public int getQueryCount() {
		return queryCount;
	}
	
	public CountHourDto(int hour, int queryCount) {
		super();
		this.hour = hour;
		this.queryCount = queryCount;
	}

	@Override
	public int compareTo(CountHourDto o) {
		return Integer.compare(this.hour, o.getHour());
	}
}
