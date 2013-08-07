package au.com.museumvictoria.fieldguide.vic.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Species {
	private String identifier;
	private String label;
	private String sublabel;
	private String searchText;
	private String squareThumbnail;
	private String group;
	private String subgroup;
	private String template;
	private Detail details;
	private ArrayList<Images> images;
	private ArrayList<Audio> audio;
	
	public Species() { }

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSublabel() {
		return sublabel;
	}

	public void setSublabel(String sublabel) {
		this.sublabel = sublabel;
	}

	public String getSearchText() {
		
		if (searchText == null) {
			searchText = ""; 
			if (details != null) {
				if (details.getOtherNames() != null && !details.getOtherNames().equals("null")) {
					searchText += " " + details.getOtherNames().replaceAll(",", " "); 
				}
			}
			searchText += (group.replaceAll(" and ", " ").replaceAll(" allies ", " ").replaceAll(",", "")).trim(); 
			searchText += " " + subgroup + " " + label + " " + sublabel;
		}
		
		return searchText.trim().toLowerCase();
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSquareThumbnail() {
		return squareThumbnail;
	}

	public void setSquareThumbnail(String squareThumbnail) {
		this.squareThumbnail = squareThumbnail;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSubgroup() {
		if (subgroup == null) {
			subgroup = ""; 
		}
		return subgroup;
	}

	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Detail getDetails() {
		return details;
	}

	public void setDetails(Detail details) {
		this.details = details;
	}

	public ArrayList<Images> getImages() {
		return images;
	}

	public void setImages(ArrayList<Images> images) {
		this.images = images;
	}

	public ArrayList<Audio> getAudio() {
		return audio;
	}

	public void setAudio(ArrayList<Audio> audio) {
		this.audio = audio;
	}

	@Override
	public String toString() {
		return "Species [identifier=" + identifier + ", label=" + label
				+ ", sublabel=" + sublabel + ", searchText=" + searchText
				+ ", squareThumbnail=" + squareThumbnail + ", group=" + group
				+ ", subgroup=" + subgroup + ", template=" + template
				+ ", details=" + details + ", images=" + images + ", audio="
				+ audio + "]";
	}

	
}
