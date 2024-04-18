package soleimanihossein.keycloak.protocol.groupmapper.entity;

/**
 * @author soleimaniHossein
 */
public class CustomGroup {

	private String name, id;

	// Constructor
	public CustomGroup() {
	}

	public CustomGroup(String id, String name) {
		this.id = id;
		this.name = name;
	}

	// Getters
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	// Setter
	public void setName(String name) {
		this.name = name;

	}

	public void setId(String id) {
		this.id = id;

	}

}
