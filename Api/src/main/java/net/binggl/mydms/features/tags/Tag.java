package net.binggl.mydms.features.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.binggl.mydms.features.documents.models.DocumentsTags;
import net.binggl.mydms.features.shared.models.NamedItem;

@Entity
@Table(name = "TAGS")
public class Tag implements NamedItem {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.tag", cascade=CascadeType.REMOVE, orphanRemoval=true)
	private List<DocumentsTags> documents = new ArrayList<DocumentsTags>();
			
	public Tag() {
		super();
	}

	public Tag(String name) {
		super();
		this.name = name;
	}

	public Tag(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonIgnore	
	public List<DocumentsTags> getDocuments() {
		return documents;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Tag)) {
			return false;
		}

		final Tag that = (Tag) o;

		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
