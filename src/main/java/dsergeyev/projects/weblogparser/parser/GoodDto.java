package dsergeyev.projects.weblogparser.parser;

public class GoodDto {

	private String goodName;
	private String categorName;
	
	public String getName() {
		return goodName;
	}

	public String getCategory() {
		return categorName;
	}

	public GoodDto(String goodName, String categoryName) {
		super();
		this.goodName = goodName;
		this.categorName = categoryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categorName == null) ? 0 : categorName.hashCode());
		result = prime * result + ((goodName == null) ? 0 : goodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoodDto other = (GoodDto) obj;
		if (categorName == null) {
			if (other.categorName != null)
				return false;
		} else if (!categorName.equals(other.categorName))
			return false;
		if (goodName == null) {
			if (other.goodName != null)
				return false;
		} else if (!goodName.equals(other.goodName))
			return false;
		return true;
	}
}
