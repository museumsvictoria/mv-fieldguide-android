package au.com.museumvictoria.fieldguide.vic.model;

public class Images {
	private String filename;
	private String imageDescription;
	private String credit;
	
	public Images() { }

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	@Override
	public String toString() {
		return "Images [filename=" + filename + ", imageDescription="
				+ imageDescription + ", credit=" + credit + "]";
	}
	
}
