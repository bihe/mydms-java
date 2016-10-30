package net.binggl.mydms.features.shared.store;

public class OrderBy {
	private String field;
	private SortOrder sort;

	public OrderBy(String field, SortOrder sort) {
		super();
		this.field = field;
		this.sort = sort;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public SortOrder getSort() {
		return sort;
	}

	public void setSort(SortOrder sort) {
		this.sort = sort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
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
		OrderBy other = (OrderBy) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (sort != other.sort)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderBy [field=" + field + ", sort=" + sort + "]";
	}
}
