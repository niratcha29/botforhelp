package com.iphayao.linebot.model;



import lombok.Data;

@Data
public class Food {
	private String food_name;
	private String food_id;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
	}

	public String getFood_name() {
		return food_name;
	}

	public void setFood_name(String food_name) {
		this.food_name = food_name;
	}

	public String getFood_id() {
		return food_id;
	}

	public void setFood_id(String food_id) {
		this.food_id = food_id;
	}

}
