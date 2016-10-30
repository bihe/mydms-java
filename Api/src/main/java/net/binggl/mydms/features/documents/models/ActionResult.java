package net.binggl.mydms.features.documents.models;

public enum ActionResult {
	None(0, "None"),
	Created(1, "Saved"),
	Updated(2, "Found"),
	Deleted(3, "Deleted"),
	Error(99, "Error");
		
	private Integer id;
    private String name;
 
    private ActionResult(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
