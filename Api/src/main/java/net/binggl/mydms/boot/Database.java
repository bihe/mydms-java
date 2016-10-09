package net.binggl.mydms.boot;

import net.binggl.mydms.Example.Foo;
import net.binggl.mydms.Example.Person;
import net.binggl.mydms.tags.Tag;

public final class Database {

	public static Class<?>[] MappedEntities = new Class<?>[]{
		Person.class,
		Foo.class,
		Tag.class
	};
}
