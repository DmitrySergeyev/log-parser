package dsergeyev.projects.weblogparser.services.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsergeyev.projects.weblogparser.Application;
import dsergeyev.projects.weblogparser.models.CategoryDto;
import dsergeyev.projects.weblogparser.models.CategoryIdCountDto;
import dsergeyev.projects.weblogparser.models.CountCountryDto;
import dsergeyev.projects.weblogparser.models.CountDayHourDto;
import dsergeyev.projects.weblogparser.models.CountHourDto;
import dsergeyev.projects.weblogparser.models.UserCountPurchase;
import dsergeyev.projects.weblogparser.models.UsersCountPurchaseDto;

public class DefaultReportRepository implements ReportRepository {

	private Logger logger = LoggerFactory.getLogger(Application.class);
	private Connection connection;
	
	public DefaultReportRepository (Connection connection) {
		try {
			this.connection = connection;
			this.createPreparedStatements();
		} catch (SQLException e) {
			logger.error("IOException occurred during setting connection to log database");
			e.printStackTrace();
		}
	}
	
	private PreparedStatement getReport1Statement;
	private PreparedStatement getReport2Statement;
	private PreparedStatement getReport3Statement;
	private PreparedStatement getReport4Statement;
	private PreparedStatement getReport5Statement;
	private PreparedStatement getReport6Statement;
	private PreparedStatement getReport7Statement;
	private PreparedStatement getCategoryStatement;
	
	private DateTimeFormatter sqlDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private void createPreparedStatements() throws SQLException {
	
			this.getReport1Statement = this.connection.prepareStatement(
					"SELECT COUNT(country_id) as count_county, country_id, country.name from (" + 
					"	SELECT country_id, date FROM (" + 
					"		(SELECT country_id, date FROM logger_schema.good_query)" + 
					"		UNION ALL" + 
					"		(SELECT country_id, date FROM logger_schema.category_query) " + 
					"		UNION ALL" + 
					"		(SELECT country_id, date FROM logger_schema.domain_query)" + 
					"		UNION ALL" + 
					"		(SELECT country_id, date FROM logger_schema.cart_item)" + 
					"		UNION ALL" + 
					"		(SELECT country_id, date FROM logger_schema.payment_query)" + 
					"		UNION ALL" + 
					"		(SELECT country_id, date FROM logger_schema.success_payment_query)" + 
					"	) as t " + 
					"    WHERE country_id IS NOT NULL AND date BETWEEN ? AND ?) as histogram " + 
					"INNER JOIN country AS country ON country.id = histogram.country_id " + 
					"GROUP BY country_id " +
					"ORDER BY count_county desc"); 
			
			this.getReport2Statement = this.connection.prepareStatement(
					"SELECT COUNT(country_id) as count_county, country_id, country.name FROM " + 
					"	(SELECT country_id FROM logger_schema.good_query WHERE " + 
					"		good_id IN (SELECT id FROM logger_schema.good WHERE category_id = ?) AND " + 
					"		date BETWEEN ? AND ?) as histogram " + 
					"INNER JOIN country AS country ON country.id = histogram.country_id " + 
					"GROUP BY country_id " + 
					"ORDER BY count_county desc"
			);
			
			this.getReport3Statement = this.connection.prepareStatement( 
					"SELECT hour(date), COUNT(*) FROM category_query WHERE " + 
					"	category_id = ? AND " + 
					"	date BETWEEN ? AND ? " + 
					"GROUP BY hour(date)"
			);
		
			this.getReport4Statement = this.connection.prepareStatement(
					"SELECT day(date), hour(date), count(date) FROM " + 
					"    (SELECT date FROM (" + 
					"		(SELECT date FROM logger_schema.good_query)" + 
					"		UNION ALL " + 
					"		(SELECT date FROM logger_schema.category_query) " + 
					"		UNION ALL " + 
					"		(SELECT date FROM logger_schema.domain_query) " + 
					"		UNION ALL " + 
					"		(SELECT date FROM logger_schema.cart_item) " + 
					"		UNION ALL " + 
					"		(SELECT date FROM logger_schema.payment_query) " + 
					"		UNION ALL " + 
					"		(SELECT date FROM logger_schema.success_payment_query) " + 
					"	) as t " + 
					"    WHERE date BETWEEN ? AND ?) as querys_date " + 
					"GROUP BY day(date), hour(date)"
			);
			
			this.getReport5Statement = this.connection.prepareStatement(
					"SELECT * FROM " + 
					"	(SELECT id, name, cnt FROM  " + 
					"		(SELECT category_id, Count(category_id) as cnt FROM " + 
					"			(SELECT good_id FROM cart_item WHERE cart_id IN " + 
					"				(SELECT cart_id FROM success_payment_query WHERE " + 
					"					date BETWEEN ? AND ?)) as paid_goods " + 
					"		INNER JOIN good AS good ON good.id = paid_goods.good_id " + 
					"		group by (category_id)) as paid_goods_with_category " + 
					"	INNER JOIN category AS category ON category.id = paid_goods_with_category.category_id) as result " + 
					"WHERE id <> ? " + 
					"ORDER by cnt desc"
			);
			
			this.getReport6Statement = this.connection.prepareStatement(
					"SELECT  count(distinct cart_id) FROM cart_item WHERE " + 
					"	date BETWEEN ? AND ?  AND " + 
					"    cart_id NOT IN  " + 
					"		(SELECT cart_id FROM success_payment_query WHERE " + 
					"			date BETWEEN ? AND ?)"
			);

			
			this.getReport7Statement = this.connection.prepareStatement(
					"SELECT cnt, COUNT(cnt) FROM " + 
					"	(SELECT user_id, COUNT(user_id) as cnt FROM payment_query  WHERE cart_id IN " + 
					"		(SELECT cart_id FROM success_payment_query WHERE " + 
					"			date BETWEEN ? AND ?) " + 
					"	group by (user_id)) as user_counts_purchase " + 
					"group by cnt"
			);
			
			this.getCategoryStatement = this.connection.prepareStatement(
					"SELECT id, name FROM logger_schema.category");
	}
	
	@Override
	public Set<CountCountryDto> getReport1(LocalDateTime from, LocalDateTime to) throws SQLException {
		TreeSet<CountCountryDto> result = new TreeSet<CountCountryDto>();
		this.getReport1Statement.setTimestamp(1, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport1Statement.setTimestamp(2, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		ResultSet resultSet = this.getReport1Statement.executeQuery();
		while(resultSet.next()) {
			result.add(new CountCountryDto(resultSet.getInt(1), resultSet.getString(3)));
		}
		
		return result;
	}
	
	@Override
	public Set<CountCountryDto> getReport2(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException {
		TreeSet<CountCountryDto> result = new TreeSet<CountCountryDto>();
		this.getReport2Statement.setInt(1, categoryId);
		this.getReport2Statement.setTimestamp(2, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport2Statement.setTimestamp(3, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		ResultSet resultSet = this.getReport2Statement.executeQuery();
		while(resultSet.next()) {
			result.add(new CountCountryDto(resultSet.getInt(1), resultSet.getString(3)));
		}
		
		return result;
	}

	@Override
	public Set<CountHourDto> getReport3(int categoryId, LocalDate from, LocalDate to) throws SQLException {
		TreeSet<CountHourDto> result = new TreeSet<CountHourDto>();
		this.getReport3Statement.setInt(1, categoryId);
		this.getReport3Statement.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
		this.getReport3Statement.setTimestamp(3, Timestamp.valueOf(to.plus(1, ChronoUnit.DAYS).atStartOfDay()));
		ResultSet resultSet = this.getReport3Statement.executeQuery();
		while(resultSet.next()) {
			result.add(new CountHourDto(resultSet.getInt(1), resultSet.getInt(2)));
		}
		
		return result;
	}

	@Override
	public Set<CountDayHourDto> getReport4(LocalDateTime from, LocalDateTime to) throws SQLException {
		TreeSet<CountDayHourDto> result = new TreeSet<CountDayHourDto>();
		this.getReport4Statement.setTimestamp(1, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport4Statement.setTimestamp(2, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		ResultSet resultSet = this.getReport4Statement.executeQuery();
		while(resultSet.next()) {
			result.add(new CountDayHourDto(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3)));
		}
		
		return result;
	}
	
	@Override
	public Set<CategoryIdCountDto> getReport5(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException {
		TreeSet<CategoryIdCountDto> result = new TreeSet<CategoryIdCountDto>();
		this.getReport5Statement.setTimestamp(1, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport5Statement.setTimestamp(2, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		this.getReport5Statement.setInt(3, categoryId);
		ResultSet resultSet = this.getReport5Statement.executeQuery();
		while(resultSet.next()) {
			result.add(new CategoryIdCountDto(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
		}
		
		return result;
	}
	
	@Override
	public int getReport6(LocalDateTime from, LocalDateTime to) throws SQLException {
		this.getReport6Statement.setTimestamp(1, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport6Statement.setTimestamp(2, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		this.getReport6Statement.setTimestamp(3, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport6Statement.setTimestamp(4, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		ResultSet resultSet = this.getReport6Statement.executeQuery();
		resultSet.next();
		return resultSet.getInt(1);
	}
	
	@Override
	public UsersCountPurchaseDto getReport7(LocalDateTime from, LocalDateTime to) throws SQLException {
		TreeSet<UserCountPurchase> buf = new TreeSet<UserCountPurchase>();
		int totalPurchase = 0;
		this.getReport7Statement.setTimestamp(1, Timestamp.valueOf(from.format(sqlDateTimeFormatter)));
		this.getReport7Statement.setTimestamp(2, Timestamp.valueOf(to.format(sqlDateTimeFormatter)));
		ResultSet resultSet = this.getReport7Statement.executeQuery();
		while(resultSet.next()) {
			int usersCount = resultSet.getInt(2);
			totalPurchase += usersCount;
			buf.add(new UserCountPurchase(resultSet.getInt(1), usersCount));
		}
		
		return new UsersCountPurchaseDto(totalPurchase, buf);
	}
	
	@Override
	public Set<CategoryDto> getCategories() throws SQLException  {
		TreeSet<CategoryDto> result = new TreeSet<CategoryDto>();
		ResultSet resultSet = this.getCategoryStatement.executeQuery();
		while(resultSet.next()) {
			result.add(new CategoryDto(resultSet.getInt(1), resultSet.getString(2)));
		}
		
		return result;
	}
}