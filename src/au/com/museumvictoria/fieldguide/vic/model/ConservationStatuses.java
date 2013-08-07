package au.com.museumvictoria.fieldguide.vic.model;

public class ConservationStatuses {
	private String authority;
	private String status;
	
	public ConservationStatuses() {}
	public ConservationStatuses(String authority, String status) {
		this.authority = authority;
		this.status = status;
	}
	
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ConservationStatuses [authority=" + authority + ", status="
				+ status + "]";
	}
}
