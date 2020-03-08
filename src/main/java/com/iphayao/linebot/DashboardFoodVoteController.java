package com.iphayao.linebot;

import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iphayao.repository.Holiday_Repo;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@Controller
public class DashboardFoodVoteController {
	
	@Autowired
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate = null;
	private StringBuilder stb = null;

	@GetMapping("/dashboardFoodVote")
	public ArrayList<Map<String, Object>> findEmp(String empCode) {
		System.out.println("Stay in ChartController");
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append("select food_id,food_food_name,count(food_id)  from employee_vote inner join foods on employee_vote.food_id = foods.food_food_id group by food_id,foods.food_food_name order by count desc ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("empcode", empCode);
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
			if (result.size() == 0) {
				return null;
			}
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return result;
	}

}