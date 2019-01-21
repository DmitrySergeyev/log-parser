package dsergeyev.projects.weblogparser.services.db;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Repository;

import dsergeyev.projects.weblogparser.models.CategoryDto;
import dsergeyev.projects.weblogparser.models.CategoryIdCountDto;
import dsergeyev.projects.weblogparser.models.CountCountryDto;
import dsergeyev.projects.weblogparser.models.CountDayHourDto;
import dsergeyev.projects.weblogparser.models.CountHourDto;
import dsergeyev.projects.weblogparser.models.UsersCountPurchaseDto;

@Repository
public interface ReportRepository {
	public Set<CountCountryDto> getReport1(LocalDateTime from, LocalDateTime to) throws SQLException;
	public Set<CountCountryDto> getReport2(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException;
	public Set<CountHourDto> getReport3(int categoryId, LocalDate from, LocalDate to) throws SQLException;
	public Set<CountDayHourDto> getReport4(LocalDateTime from, LocalDateTime to) throws SQLException;
	public Set<CategoryIdCountDto> getReport5(int categoryId, LocalDateTime from, LocalDateTime to) throws SQLException;
	public int getReport6(LocalDateTime from, LocalDateTime to) throws SQLException;
	public UsersCountPurchaseDto getReport7(LocalDateTime from, LocalDateTime to) throws SQLException;
	public Set<CategoryDto> getCategories() throws SQLException;
}
