package lc.common.configuration.model;

public class ModelDataNode extends ModelConfigNode {
	
	private String data;

	public ModelDataNode() {
		super();
	}

	public ModelDataNode(String name) {
		super(name);
	}

	public ModelDataNode(String name, ModelConfigNode parent) {
		super(name, parent);
	}

	public ModelDataNode(String name, String comment) {
		super(name, comment);
	}

	public ModelDataNode(String name, String comment, ModelConfigNode parent) {
		super(name, comment, parent);
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	

}
