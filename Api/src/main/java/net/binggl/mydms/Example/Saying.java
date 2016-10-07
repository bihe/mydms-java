package net.binggl.mydms.Example;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Saying {
    private long id;

    private List<Person> persons;
    private List<Foo> foos;
    
    @Length(max = 3)
    private String content;

    public Saying() {
        // Jackson deserialization
    }

    public Saying(long id, String content, List<Person> persons, List<Foo> foos) {
        this.id = id;
        this.content = content;
        this.persons = persons;
        this.foos = foos;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
    
    @JsonProperty
    public List<Person> getPersons() {
        return persons;
    }
    
    @JsonProperty
    public List<Foo> getFoos() {
        return foos;
    }
}