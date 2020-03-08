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
public class LogRepo {
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


	public void saveLog( String logcase,  String userid) {

		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			stb = new StringBuilder();
			stb.append( "INSERT INTO public.log_support(log_desc, log_date, user_line_id)VALUES(:logcase, now(), :userid)");
			parameters.addValue("logcase", logcase);
			parameters.addValue("userid", userid);
			jdbcTemplate.update(stb.toString(), parameters);
		} catch (EmptyResultDataAccessException ex) {
			log.error("Msg :: {}, Trace :: {}", ex.getMessage(), ex.getStackTrace());
		}
	}
	
}
