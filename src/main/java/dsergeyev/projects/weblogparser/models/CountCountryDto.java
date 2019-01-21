package dsergeyev.projects.weblogparser.models;

public class CountCountryDto implements Comparable<CountCountryDto> {
	
	private int queryCount;
	private String countryName;
	
	public int getQueryCount() {
		return queryCount;
	}

	public String getCountryName() {
		return countryName;
	}

	public CountCountryDto(int queryCount, String countryName) {
		super();
		this.queryCount = queryCount;
		this.countryName = countryName;
	}

	@Override
	public int compareTo(CountCountryDto o) {
		return Integer.compare(o.getQueryCount(), this.queryCount);
	}
}
