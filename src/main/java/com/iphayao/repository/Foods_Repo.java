package com.iphayao.repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iphayao.linebot.model.UserLog;
import com.linecorp.bot.model.message.flex.component.Text;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Data
public class Foods_Repo {
	public class Model {
		public String profileCode;
		public String profileDesc;
		public Boolean active;
		private String createdProgram;
		private String updatedProgram;
	}
	@Autowired
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate = null;
	private StringBuilder stb = null;

	public ArrayList<Map<String, Object>> foodsList() {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append("select food_food_id,food_food_name from foods order by food_food_id ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return result;
	}
	public String CountVote(UserLog userLog) {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			Calendar c = Calendar.getInstance();
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(
					" select count(*) from employee_vote where emp_emp_id =:empcode  and week_of_year=:week_of_year    and  year_vote=:year_Vote ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			int weekOfYearInteger = c.get(Calendar.WEEK_OF_YEAR);
			int yearInteger = c.get(Calendar.YEAR);
			parameters.addValue("empcode", userLog.getEmpCode());
			parameters.addValue("week_of_year", weekOfYearInteger);
			parameters.addValue("year_Vote", yearInteger);
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
			String count = result.toString();
			userLog.setCountVote(count);
			String change1 = userLog.getCountVote();
			String change2 = change1.replace("[{count=", "");
			String change3 = change2.replace("}]", "");
			int countVote = Integer.parseInt(change3);
			userLog.setCountVout_CheckPossilibity(countVote);
			if (result.size() == 0) {
				return null;
			}
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return (String) result.get(0).get("emp_emp_id");
	}
	public int register(UserLog userLog) {
		int aaa = 0;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(" UPDATE employee SET emp_emp_line_id = :lineid");
			stb.append(" WHERE emp_emp_code = :empcode ");

			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("empcode", userLog.getEmpCode());
			parameters.addValue("lineid", userLog.getUserID());
			aaa = jdbcTemplate.update(stb.toString(), parameters);
			return aaa;
			// (stb.toString(), parameters,
			// new BeanPropertyRowMapper<Entity>(Entity.class));
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return aaa;
	}
	public int saveFood(UserLog string) {
		Calendar c = Calendar.getInstance();
		int aaa = 0;
		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			int weekOfYearInteger = c.get(Calendar.WEEK_OF_YEAR);
			int yearInteger = c.get(Calendar.YEAR);
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			stb.append( "insert into employee_vote (emp_emp_id,food_id,date_vote,week_of_year,year_vote)  values  (:employeeCode,:foodIdVote,:dateNow,:week_of_year,:year)");
			String employeeCode = string.getEmpCode();
			String FoodsIdVote = string.getFoodId();
			parameters.addValue("employeeCode", employeeCode);
			parameters.addValue("foodIdVote", FoodsIdVote);
			parameters.addValue("dateNow", dateFormat.format(date));
			parameters.addValue("week_of_year", weekOfYearInteger);
			parameters.addValue("year", yearInteger);
			parameters.addValue("dateNow", dateFormat.format(date));
			aaa = jdbcTemplate.update(stb.toString(), parameters);
			return aaa;
			// (stb.toString(), parameters,
			// new BeanPropertyRowMapper<Entity>(Entity.class));
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return aaa;
	}
	public String findFoods(String foodId) {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(" select  food_food_name from foods ");
			stb.append(" WHERE food_food_id = :foodCode ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("foodCode", foodId);
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
			if (result.size() == 0) {
				return null;
			}
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return (String) result.get(0).get("food_food_name");
	}
	public String findEmp(String empCode) {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(" SELECT emp_emp_name FROM employee ");
			stb.append(" WHERE emp_emp_code = :empcode ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("empcode", empCode);
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
			if (result.size() == 0) {
				return null;
			}
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return (String) result.get(0).get("emp_emp_name");
	}
	public ArrayList<Map<String, Object>> holidayList() {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(" select * from holiday order by number_event ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return result;
	}
	public ArrayList<Map<String, Object>> Holiday_Soon() {
		ArrayList<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(
					" select to_date(date_holiday, 'dd/mm/yyyy'),name_holiday from holiday where to_date(date_holiday, 'dd/mm/yyyy') between now() and to_date('31/12/2019', 'dd/mm/yyyy') order by to_date  limit 3 ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return result;
	}
	public ArrayList<Map<String, Object>> list() {
		ArrayList<Map<String, Object>> result = null;
		// List<Map<String, Object>> result = null;
		try {
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append(" SELECT message FROM log_chat ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
		return result;
	}
}
