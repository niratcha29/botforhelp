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

import com.iphayao.linebot.model.Holiday;
import com.iphayao.repository.Holiday_Repo;;;

@Service
public class HolidayService {
	private static final DateFormat dateNowHoliday = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	private Holiday_Repo holiday;
	
	ModelMapper modelMapper;

	
	
	public String getAllHoliday() {
		modelMapper = new ModelMapper();
		Stack<String> holi_list = new Stack<>();
		ArrayList<Map<String, Object>> holiday_all = holiday.holidayList();
		holiday_all.forEach(record -> {
			Holiday holi = new Holiday();
			modelMapper.map(record, holi);
			holi_list.push("\n" + "➤ " + holi.getDate_holiday() + "  " + holi.getName_holiday());
		});

		String Holiday_In_Year = holi_list.toString();
		Holiday_In_Year = Holiday_In_Year.replace("[", "");
		Holiday_In_Year = Holiday_In_Year.replace("]", "");
		Holiday_In_Year = Holiday_In_Year.replace(",", "");
		
		return Holiday_In_Year;
	}
	
	public String getHolidaySoon(){   
		modelMapper = new ModelMapper();
		Date nowDate = new Date();
		Stack<String> holi_list = new Stack<>();
		ArrayList<Map<String, Object>> holiday_all = holiday.Holiday_Soon();
		holiday_all.forEach(record -> {
			Holiday holi = new Holiday();
			modelMapper.map(record, holi);
			holi_list.push("\n" + holi.getDate_holiday() + "   " + holi.getName_holiday());

		});
		String day1 = holiday_all.get(0).toString();
		String day2 = holiday_all.get(1).toString();
		String day3 = holiday_all.get(2).toString();
		day1 = day1.replace("2019-01-01", "01/01/2019");
		day1 = day1.replace("2019-02-05", "05/02/2019");
		day1 = day1.replace("2019-02-19", "19/02/2019");
		day1 = day1.replace("2019-04-08", "08/04/2019");
		day1 = day1.replace("2019-04-15", "15/04/2019");
		day1 = day1.replace("2019-04-16", "16/04/2019");
		day1 = day1.replace("2019-05-01", "01/05/2019");
		day1 = day1.replace("2019-07-20", "20/07/2019");
		day1 = day1.replace("2019-07-16", "16/07/2019");
		day1 = day1.replace("2019-07-29", "29/07/2019");
		day1 = day1.replace("2019-08-12", "12/08/2019");
		day1 = day1.replace("2019-10-14", "14/10/2019");
		day1 = day1.replace("2019-10-23", "23/10/2019");
		day1 = day1.replace("2019-12-5", "05/12/2019");
		day1 = day1.replace("2019-12-10", "10/12/2019");
		day1 = day1.replace("2019-12-31", "31/12/2019");
		// -------------------------------------------------
		day2 = day2.replace("2019-01-01", "01/01/2019");
		day2 = day2.replace("2019-02-05", "05/02/2019");
		day2 = day2.replace("2019-02-19", "19/02/2019");
		day2 = day2.replace("2019-02-08", "08/02/2019");
		day2 = day2.replace("2019-04-15", "15/04/2019");
		day2 = day2.replace("2019-04-16", "16/04/2019");
		day2 = day2.replace("2019-05-01", "01/05/2019");
		day2 = day2.replace("2019-05-20", "20/05/2019");
		day2 = day2.replace("2019-07-20", "20/07/2019");
		day2 = day2.replace("2019-07-16", "16/07/2019");
		day2 = day2.replace("2019-07-29", "29/07/2019");
		day2 = day2.replace("2019-08-12", "12/08/2019");
		day2 = day2.replace("2019-10-14", "14/10/2019");
		day2 = day2.replace("2019-10-23", "23/10/2019");
		day2 = day2.replace("2019-12-5", "05/12/2019");
		day2 = day2.replace("2019-12-10", "10/12/2019");
		day2 = day2.replace("2019-12-31", "31/12/2019");
		// -------------------------------------------------
		day3 = day3.replace("2019-01-01", "01/01/2019");
		day3 = day3.replace("2019-02-05", "05/02/2019");
		day3 = day3.replace("2019-02-19", "19/02/2019");
		day3 = day3.replace("2019-02-08", "08/02/2019");
		day3 = day3.replace("2019-04-15", "15/04/2019");
		day3 = day3.replace("2019-04-16", "16/04/2019");
		day3 = day3.replace("2019-05-01", "01/05/2019");
		day3 = day3.replace("2019-05-20", "20/05/2019");
		day3 = day3.replace("2019-07-20", "20/07/2019");
		day3 = day3.replace("2019-07-16", "16/07/2019");
		day3 = day3.replace("2019-07-29", "29/07/2019");
		day3 = day3.replace("2019-08-12", "12/08/2019");
		day3 = day3.replace("2019-10-14", "14/10/2019");
		day3 = day3.replace("2019-10-23", "23/10/2019");
		day3 = day3.replace("2019-12-5", "05/12/2019");
		day3 = day3.replace("2019-12-10", "10/12/2019");
		day3 = day3.replace("2019-12-31", "31/12/2019");
		// -------------------------------------------------
		day1 = day1.replace("{", "");
		day1 = day1.replace("}", "");
		day1 = day1.replace("to_date=", "");
		day1 = day1.replace("name_holiday=", "");
		day1 = day1.replace("=", "");
		day1 = day1.replace(",", " ");
		day2 = day2.replace("{", "");
		day2 = day2.replace("}", "");
		day2 = day2.replace("to_date=", "");
		day2 = day2.replace("name_holiday=", " ");
		day2 = day2.replace("=", "");
		day2 = day2.replace(",", " ");
		day3 = day3.replace("{", "");
		day3 = day3.replace("}", "");
		day3 = day3.replace("to_date=", "");
		day3 = day3.replace("name_holiday=", " ");
		day3 = day3.replace("=", "");
		day3 = day3.replace(",", " ");
		
		String holidaySoon = "วันที่ปัจจุบัน คือ  " + " " + dateNowHoliday.format(nowDate)
		+ "\n" + "\n" + "วันหยุดที่จะถึงเร็วๆนี้ ได้เเก่ " + "\n" + "➤ " + day1 + "\n" + "➤ "
		+ day2 + "\n" + "➤ " + day3;
		return holidaySoon;
	}
	
	
	
	
	
	
}
