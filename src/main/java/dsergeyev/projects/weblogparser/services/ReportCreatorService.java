package dsergeyev.projects.weblogparser.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsergeyev.projects.weblogparser.Application;
import dsergeyev.projects.weblogparser.models.CategoryDto;
import dsergeyev.projects.weblogparser.models.CategoryIdCountDto;
import dsergeyev.projects.weblogparser.models.CountCountryDto;
import dsergeyev.projects.weblogparser.models.CountDayHourDto;
import dsergeyev.projects.weblogparser.models.CountHourDto;
import dsergeyev.projects.weblogparser.models.UsersCountPurchaseDto;
import dsergeyev.projects.weblogparser.services.db.DefaultReportRepository;


public class ReportCreatorService {

	private Connection connection;
	private DefaultReportRepository reportRepository;
	private Logger logger = LoggerFactory.getLogger(Application.class);
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/logger_schema";
	private static final String DB_LOGIN = "dmitry";
	private static final String DB_PASSWORD = "12341234";

	
	public ReportCreatorService () {
		try {
			this.connection = DriverManager.getConnection(DB_URL, DB_LOGIN, DB_PASSWORD);
			this.reportRepository = new DefaultReportRepository(connection);
		}
		catch (SQLException e) {
			logger.error("IOException occurred during setting connection to log database");
			e.printStackTrace();
		}
	}
	
	public Set<CountCountryDto> getReport1(LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport1(from, to);
	}

	public Set<CountCountryDto> getReport2(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport2(categoryId, from, to);
	}

	public Set<CountHourDto> getReport3(int categoryId, LocalDate from, LocalDate to) throws SQLException {
		return this.reportRepository.getReport3(categoryId, from, to);
	}

	public Set<CountDayHourDto> getReport4(LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport4(from, to);
	}
	
	public Set<CategoryIdCountDto> getReport5(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport5(categoryId, from, to);
	}
	
	public int getReport6(LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport6(from, to);
	}

	public UsersCountPurchaseDto getReport7(LocalDateTime from, LocalDateTime to) throws SQLException {
		return this.reportRepository.getReport7(from, to);

	}

	public Set<CategoryDto> getCategories() throws SQLException {
		return this.reportRepository.getCategories();
	}
}
