package dsergeyev.projects.weblogparser.services.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dsergeyev.projects.weblogparser.Application;

//@Component
public class DefaultParserRepository implements ParserRepository {

	private static final String CREATE_SCHEMA_FILE_PATH = 
			"/home/dmitry/git/log-parser/src/main/java/dsergeyev/projects/weblogparser/services/db/scripts/CreateSchema.sql";
	private static final String DROP_SCHEMA_FILE_PATH = 
			"/home/dmitry/git/log-parser/src/main/java/dsergeyev/projects/weblogparser/services/db/scripts/DropSchema.sql";
	
	private Logger logger = LoggerFactory.getLogger(Application.class);
	private Connection connection;
	
	private PreparedStatement saveCountryStatement;
	private PreparedStatement saveDomainQueryStatement;
	private PreparedStatement saveCategoryStatement;
	private PreparedStatement saveGoodStatement;
	private PreparedStatement saveGoodQueryStatement;
	private PreparedStatement saveCategoryQueryStatement;
	private PreparedStatement saveCartItemStatement;
	private PreparedStatement savePaymentStatement;
	private PreparedStatement saveConformationPaymantStatement;
	private PreparedStatement setGoodStoreIdStatement;
	private PreparedStatement getGoodIdByGoodStoreId;
	
	private DateTimeFormatter sqlformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	// Reads all SQL commands from the file and execute it in batch mode
	private void execute (String fileScritpPath) {
		StringBuilder bathcSQL = new StringBuilder();
		try (Scanner scanner = new Scanner(new File(fileScritpPath))) {
			try (Statement statement = this.connection.createStatement()) {
				StringBuilder sb = new StringBuilder("\n");
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (!line.isEmpty()) {
						sb.append(line);
						if (sb.charAt(sb.length() - 1) == ';') {
							String query = sb.toString();
							bathcSQL.append(query);
							statement.addBatch(query);
							sb.setLength(1);
						} else {
							sb.append("\n");
						}
					}
				}
				this.logger.info(bathcSQL.toString());
				statement.executeBatch();
			} catch (SQLException e) {
				logger.error("Database access error occurs during executing SQL script file");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			logger.error("SQL script file '" + fileScritpPath + "' was not found");
			e.printStackTrace();
		}
	}

	private void createPreparedStatements() throws SQLException {

		this.saveCountryStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.country (name) VALUES (?)", 
				Statement.RETURN_GENERATED_KEYS);

		this.saveDomainQueryStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.domain_query (date, ip, country_id) VALUES (?, ?, ?)");
		
		this.saveCategoryStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.category (name) VALUES (?)", 
				Statement.RETURN_GENERATED_KEYS);

		this.saveGoodStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.good (name, category_id) VALUES (?, ?)",
				Statement.RETURN_GENERATED_KEYS);

		this.saveCategoryQueryStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.category_query (category_id, date, ip, country_id) VALUES (?, ?, ?, ?)");
		
		this.saveGoodQueryStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.good_query (good_id, date, ip, country_id) VALUES (?, ?, ?, ?)");
	
		this.saveCartItemStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.cart_item (amount, good_id, cart_id, date, ip, country_id) VALUES (?, ?, ?, ?, ?, ?)");

		this.savePaymentStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.payment_query (user_id, cart_id, date, ip, country_id) VALUES (?, ?, ?, ?, ?)");
		
		this.saveConformationPaymantStatement = this.connection.prepareStatement(
				"INSERT INTO logger_schema.success_payment_query (cart_id, date, ip, country_id) VALUES (?, ?, ?, ?)");
		
		this.setGoodStoreIdStatement = this.connection.prepareStatement(
				"UPDATE logger_schema.good SET store_id = ? WHERE id = (" + 
						"SELECT good_id FROM logger_schema.good_query WHERE ip = ? " +
						"ORDER BY date desc LIMIT 1)");
		
		this.getGoodIdByGoodStoreId = this.connection.prepareStatement(
				"SELECT id FROM logger_schema.good WHERE store_id = ?");
	}

	public DefaultParserRepository(Connection connection) throws SQLException {
		this.connection = connection;
		this.createPreparedStatements();
	}

	public void dropSchema() {
		this.execute(DROP_SCHEMA_FILE_PATH);
	}

	public void createNewSchema() {
		this.execute(CREATE_SCHEMA_FILE_PATH);
	}

	private PreparedStatement setPreparedStatementCountry (PreparedStatement prs, int index, Integer countryId) throws SQLException {
		if (countryId != null)
			prs.setInt(index, countryId);
		else
			prs.setNull(index, java.sql.Types.INTEGER);
		
		return prs;
	}
	
	public int saveCountryAndGetId(String name) throws SQLException {
		this.saveCountryStatement.setString(1, name);
		this.saveCountryStatement.executeUpdate();
		ResultSet generatedKeys = this.saveCountryStatement.getGeneratedKeys();
		generatedKeys.next();
		return generatedKeys.getInt(1);
	}
	
	public void saveDomainQuery(LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.saveDomainQueryStatement.setTimestamp(1, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.saveDomainQueryStatement.setString(2, ip);
		this.setPreparedStatementCountry(this.saveDomainQueryStatement, 3, countryId).executeUpdate();
	}

	public int saveCategoryAndGetId(String name) throws SQLException {
		this.saveCategoryStatement.setString(1, name);
		this.saveCategoryStatement.executeUpdate();
		ResultSet generatedKeys = this.saveCategoryStatement.getGeneratedKeys();
		generatedKeys.next();
		return generatedKeys.getInt(1);
	}

	public int saveGoodAndGetId(String name, int categoryId) throws SQLException {
		this.saveGoodStatement.setString(1, name);
		this.saveGoodStatement.setInt(2, categoryId);
		this.saveGoodStatement.executeUpdate();
		ResultSet generatedKeys = this.saveGoodStatement.getGeneratedKeys();
		generatedKeys.next();
		return generatedKeys.getInt(1);
	}
	
	public void saveCategoryQuery (int categoryId, LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.saveCategoryQueryStatement.setInt(1, categoryId);
		this.saveCategoryQueryStatement.setTimestamp(2, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.saveCategoryQueryStatement.setString(3, ip);
		this.setPreparedStatementCountry(this.saveCategoryQueryStatement, 4, countryId).executeUpdate();
	}
	
	public void saveGoodQuery (int goodId, LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.saveGoodQueryStatement.setInt(1, goodId);
		this.saveGoodQueryStatement.setTimestamp(2, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.saveGoodQueryStatement.setString(3, ip);
		this.setPreparedStatementCountry(this.saveGoodQueryStatement, 4, countryId).executeUpdate();
	}

	public void saveCartItem(int amount, int goodId, int cartId, LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.saveCartItemStatement.setInt(1, amount);
		this.saveCartItemStatement.setInt(2, goodId);
		this.saveCartItemStatement.setInt(3, cartId);
		this.saveCartItemStatement.setTimestamp(4, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.saveCartItemStatement.setString(5, ip);
		this.setPreparedStatementCountry(this.saveCartItemStatement, 6, countryId).executeUpdate();
	}
	
	public void savePaymentQuery(String userId, int cartId, LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.savePaymentStatement.setString(1, userId);
		this.savePaymentStatement.setInt(2, cartId);
		this.savePaymentStatement.setTimestamp(3, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.savePaymentStatement.setString(4, ip);
		this.setPreparedStatementCountry(this.savePaymentStatement, 5, countryId).executeUpdate();
	}
	
	public void saveSuccessPaymentQuery(int cartId, LocalDateTime ldt, String ip, Integer countryId) throws SQLException {
		this.saveConformationPaymantStatement.setInt(1, cartId);
		this.saveConformationPaymantStatement.setTimestamp(2, Timestamp.valueOf(ldt.format(sqlformatter)));
		this.saveConformationPaymantStatement.setString(3, ip);
		this.setPreparedStatementCountry(this.saveConformationPaymantStatement, 4, countryId).executeUpdate();
	}

	public int setGoodStoreIdAndGetId(String ip, int storeGoodId) throws SQLException {
		this.setGoodStoreIdStatement.setInt(1, storeGoodId);
		this.setGoodStoreIdStatement.setString(2, ip);
		this.setGoodStoreIdStatement.executeUpdate();
		this.getGoodIdByGoodStoreId.setInt(1, storeGoodId);
		ResultSet result = this.getGoodIdByGoodStoreId.executeQuery();
		result.next();
		int goodId = result.getInt(1);
		
		return goodId;
	}
}