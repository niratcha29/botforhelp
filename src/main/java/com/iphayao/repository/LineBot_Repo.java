//package com.iphayao.repository;
//
//import java.sql.Array;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.Statement;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
////import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import com.iphayao.linebot.model.UserLog;
//import com.linecorp.bot.model.message.flex.component.Text;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Repository
//@Data
//public class LineBot_Repo {
//
//	public class Model {
//
//		public String profileCode;
//		public String profileDesc;
//		public Boolean active;
//		private String createdProgram;
//		private String updatedProgram;
//	}
//	@Autowired
//	private DataSource dataSource;
//	private NamedParameterJdbcTemplate jdbcTemplate = null;
//	private StringBuilder stb = null;
//	
//	public int register(UserLog userLog) {
//		int aaa = 0;
//		try {
//			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//			stb = new StringBuilder();
//			stb.append(" UPDATE employee SET emp_emp_line_id = :lineid");
//			stb.append(" WHERE emp_emp_code = :empcode ");
//
//			MapSqlParameterSource parameters = new MapSqlParameterSource();
//			parameters.addValue("empcode", userLog.getEmpCode());
//			parameters.addValue("lineid", userLog.getUserID());
//
//			aaa = jdbcTemplate.update(stb.toString(), parameters);
//			return aaa;
//			// (stb.toString(), parameters,
//			// new BeanPropertyRowMapper<Entity>(Entity.class));
//		} catch (EmptyResultDataAccessException ex) {
//			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
//		}
//		return aaa;
//	}
//	
//	
//	public String findEmp(String empCode) {
//		ArrayList<Map<String, Object>> result = null;
//		// List<Map<String, Object>> result = null;
//		try {
//			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//			stb = new StringBuilder();
//			stb.append(" SELECT emp_emp_name FROM employee ");
//			stb.append(" WHERE emp_emp_code = :empcode ");
//			MapSqlParameterSource parameters = new MapSqlParameterSource();
//			parameters.addValue("empcode", empCode);
//			result = (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(stb.toString(), parameters);
//			if (result.size() == 0) {
//				return null;
//			}
//		} catch (EmptyResultDataAccessException ex) {
//			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
//		}
//		return (String) result.get(0).get("emp_emp_name");
//	}
//
//	
//	
//}
