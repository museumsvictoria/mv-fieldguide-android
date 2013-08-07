package au.com.museumvictoria.fieldguide.vic.model;

public class Audio {

	private String filename;
	private String audioDescription;
	private String credit;
	
	public Audio() { }

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getAudioDescription() {
		return audioDescription;
	}

	public void setAudioDescription(String audioDescription) {
		this.audioDescription = audioDescription;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	@Override
	public String toString() {
		return "Audio [filename=" + filename + ", audioDescription="
				+ audioDescription + ", credit=" + credit + "]";
	}

	
	
}
