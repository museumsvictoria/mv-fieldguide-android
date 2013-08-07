package au.com.museumvictoria.fieldguide.vic.model;

public class Group {
	private String label;
	private int order; 
	private String standardImage;
	private String highlightedImage;
	public Group() { }
	public Group(String label, int order) {
		this.label = label;
		this.order = order;
		this.standardImage = "";
		this.highlightedImage = "";
	}
	public Group(String label, int order, String standardImage, String highlightedImage) {
		this.label = label;
		this.order = order;
		this.standardImage = standardImage;
		this.highlightedImage = highlightedImage;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getStandardImage() {
		return standardImage;
	}
	public void setStandardImage(String standardImage) {
		this.standardImage = standardImage;
	}
	public String getHighlightedImage() {
		return highlightedImage;
	}
	public void setHighlightedImage(String highlightedImage) {
		this.highlightedImage = highlightedImage;
	}
	
}
