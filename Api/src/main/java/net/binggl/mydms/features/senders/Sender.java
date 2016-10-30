package net.binggl.mydms.features.senders;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.binggl.mydms.features.shared.NamedItem;

@Entity
@Table(name = "SENDERS")
public class Sender implements NamedItem {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	public Sender() {
		super();
	}

	public Sender(String name) {
		super();
		this.name = name;
	}

	public Sender(long id, String name) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Sender)) {
			return false;
		}

		final Sender that = (Sender) o;

		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
