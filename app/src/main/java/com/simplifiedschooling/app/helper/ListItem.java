package com.simplifiedschooling.app.helper;

public class ListItem {

	String image;
	String title;
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public ListItem(String image, String title, boolean selected) {
		super();
		this.image = image;
		this.title = title;
		this.selected = selected;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
