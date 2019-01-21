package dsergeyev.projects.weblogparser.models;

public class CountDayHourDto implements Comparable<CountDayHourDto>  {
	
	private int day;
	private int hour;
	private int queryCount;

	public int getDay() {
		return day;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getQueryCount() {
		return queryCount;
	}
	
	public CountDayHourDto(int day, int hour, int queryCount) {
		super();
		this.day = day;
		this.hour = hour;
		this.queryCount = queryCount;
	}

	@Override
	public int compareTo(CountDayHourDto o) {
		if (this.day < o.getDay()) {
			return -1;
		} else {
			if (this.day == o.getDay()) {
				return (this.hour < o.getDay()) ? -1 : ((this.hour == o.getDay()) ? 0 : 1);
			} else {
				return 1;
			}
		}
	}
}
