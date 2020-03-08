package com.iphayao.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Stack;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iphayao.linebot.model.Food;
import com.iphayao.linebot.model.Holiday;
import com.iphayao.repository.Foods_Repo;;

@Service
public class FoodsService {
	
	@Autowired
	private Foods_Repo foods;
	
	ModelMapper modelMapper;

	
	public String ListAllFoods(){
		modelMapper = new ModelMapper();
		Stack<String> holi_list = new Stack<>();
		ArrayList<Map<String, Object>> ListFoodsAll = foods.foodsList();
		ListFoodsAll.forEach(record -> {
			Food menus = new Food();
			modelMapper.map(record, menus);
			holi_list.push("\n" + menus.getFood_id() + "  " + menus.getFood_name());
		});
		String foodsList ="รายการอาหารค่ะ"+"\n"+ holi_list.toString();
		foodsList = foodsList.replace("[", "");
		foodsList = foodsList.replace("]", "");
		foodsList = foodsList.replace(",", "");
		return foodsList;
		
		
	}
	
	
	
	
	
	
	
	
}
