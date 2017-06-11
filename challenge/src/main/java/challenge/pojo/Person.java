package challenge.pojo;

public class Person {
    
    private long id;
    private String handle;
    private String name;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getHandle() {
        return handle;
    }
    public void setHandle(String handle) {
        this.handle = handle;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object that) {
    	return (this == that) ? true : (this.id == ((Person) that).id);
    }
}