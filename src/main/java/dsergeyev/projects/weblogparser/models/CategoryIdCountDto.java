package dsergeyev.projects.weblogparser.models;

public class CategoryIdCountDto implements Comparable<CategoryIdCountDto> {

	private int id;
	private String name;
	private int count;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getCount() {
		return count;
	}
	
	public CategoryIdCountDto(int id, String name, int count) {
		super();
		this.id = id;
		this.name = name;
		this.count = count;
	}
	
	@Override
	public int compareTo(CategoryIdCountDto o) {
		return Integer.compare(o.getCount(), this.count);
	}
}
