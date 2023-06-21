package headDepartmentUI;

import java.io.Serializable;

import javafx.scene.control.CheckBox;

public class statisticTable implements Serializable {

	private static final long serialVersionUID = 3L;
	private String conductId;
	private String testName;
	private CheckBox box;
	
	public statisticTable(String testID,String testName) {
		super();
		this.conductId = testID;
		this.testName = testName;
		this.box = new CheckBox();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getConductId() {
		return conductId;
	}
	
	public String getTestName() {
		return testName;
	}

	public CheckBox getBox()
	{
		return box;
	}
	
	public void setBox(CheckBox box)
	{
		this.box=box;
	}

}
