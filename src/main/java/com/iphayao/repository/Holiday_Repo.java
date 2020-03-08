package com.iphayao.repository;

import java.util.ArrayList;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Data
public class Holiday_Repo {

	public class Model {

		public String profileCode;
		public String profileDesc;
		public Boolean active;

	}
	@Autowired
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate = null;
	private StringBuilder stb = null;

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
		// List<Map<String, Object>> result = null;
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

}
