package dsergeyev.projects.weblogparser.models;

public class CategoryDto implements Comparable<CategoryDto> {

	private int id;
	private String name;

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public CategoryDto(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public int compareTo(CategoryDto o) {
		return Integer.compare(this.id, o.getId());
	}
}

