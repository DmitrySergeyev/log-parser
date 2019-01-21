package dsergeyev.projects.weblogparser.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;

import dsergeyev.projects.weblogparser.Application;
import dsergeyev.projects.weblogparser.services.db.DefaultParserRepository;

import java.util.regex.Matcher;

@Service
public class Parser {

	private static final String DOMAIN = "all_to_the_bottom.com";
	private static final String IP_ADDRESS_PATTERN_STR = "^(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\."
			+ "(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\." + "(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\."
			+ "(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
	private static final Pattern ipPattrern = Pattern.compile(IP_ADDRESS_PATTERN_STR);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Value(value="${resources.GeoLite2.path}")
	private String ipDatebaseFilePath;	
	@Value(value="${resources.log.path}")
	private String logFilePath;
	@Value(value="${spring.datasource.url}")
	private String dbURL;
	@Value(value="${spring.datasource.username}")
	private String dbLoggin;
	@Value(value="${spring.datasource.password}")
	private String dbPassword;
	@Value(value="${spring.datasource.rebuild}")
	private boolean rebuildDb;
	
	private Matcher matcher;
	private Logger logger = LoggerFactory.getLogger(Application.class);
	
	private DefaultParserRepository parserRepository;
	private DatabaseReader ipDatebaseReader;
	
	private int totalIpNumber = 0;
	private int recognizedIpNumber;
	private int unrecognizedIpNumber = 0;
	
	private int totalIpQueryNumber = 0;
	private int recognisedIpQueryNumber = 0;
	private int unrecognizedIpQueryNumber = 0;
	
	private HashMap<String, Integer> countryIdIpCache = new HashMap<String, Integer>();
	private HashMap<String, Integer> countryNameIdCache = new HashMap<String, Integer>();
	private HashMap<String, Integer> categoryNameIdCache = new HashMap<String, Integer>();
	private HashMap<GoodDto, Integer> goodDtoIdCahe = new HashMap<GoodDto, Integer>();
	private HashMap<Integer, Integer> goodIdStoreIdCahe = new HashMap<Integer, Integer>();
	
	// Returns country ID for specified IP or NULL if IP is not found in IP database
	private Integer getCountryId(String ip) throws SQLException {

		Integer id = null;
		
		if (this.countryIdIpCache.containsKey(ip)) {
			id = this.countryIdIpCache.get(ip);
			if (id == null)
				this.unrecognizedIpQueryNumber++;
			else 
				this.recognisedIpQueryNumber++;
		} else {
			try {
				CountryResponse cr = ipDatebaseReader.country(InetAddress.getByName(ip));
				String countryName = cr.getCountry().getName();

				if (countryName == null)
					throw new GeoIp2Exception(ip + " was not found in IP database");

				
				if (this.countryNameIdCache.containsKey(countryName)) {
					id = this.countryNameIdCache.get(countryName);
				} else {
					id = this.parserRepository.saveCountryAndGetId(countryName);
					this.countryNameIdCache.put(countryName, id);
				}

				this.countryIdIpCache.put(ip, id);
				this.recognizedIpNumber++;
				this.recognisedIpQueryNumber++;
			} catch (UnknownHostException e) {
				this.logger.info("UnknownHostException occurred during getting information from IP datebase (GeoLite2 Country) for ip:" +  ip);
				e.printStackTrace();
			} catch (IOException e) {
				this.logger.info("IOException occurred during getting information from IP datebase (GeoLite2 Country) for ip:" +  ip);
				e.printStackTrace();
			} catch (GeoIp2Exception e) {
				this.countryIdIpCache.put(ip, null);
				this.unrecognizedIpNumber++;
				this.unrecognizedIpQueryNumber++;
				this.logger.info(ip + " was not found in IP database");
			}
			
			this.totalIpNumber++;
		}
		
		this.totalIpQueryNumber++;
		return id;
	}
	
	// Returns category ID by category name
	private int getCategory(String name) throws SQLException {
		int categoryId;
		
		if (this.categoryNameIdCache.containsKey(name)) {
			categoryId = this.categoryNameIdCache.get(name);
		} else {
			categoryId = this.parserRepository.saveCategoryAndGetId(name);
			this.categoryNameIdCache.put(name, categoryId);
		}
		
		return categoryId;
	}
	
	// Returns good ID by it name and name of it category specified in GoodDto class instance 
	private int getGoodId(GoodDto goodDto) throws SQLException {
		int goodId;
		
		if(this.goodDtoIdCahe.containsKey(goodDto)) {
			goodId = this.goodDtoIdCahe.get(goodDto);
		} else {
			goodId = this.parserRepository.saveGoodAndGetId(goodDto.getName(), this.getCategory(goodDto.getCategory()));
			this.goodDtoIdCahe.put(goodDto, goodId);
		}
		
		return goodId;
	}
	
	// Returns good ID by store good ID and query IP address, like in example below
	// 9085 shop_api      | 2018-08-06 01:05:02 [9KYCRHK2] INFO: 119.40.25.73 https://all_to_the_bottom.com/frozen_fish/pike/
	// ...
	// ...
	// 9089 shop_api      | 2018-08-06 01:07:57 [F1YFP6M7] INFO: 119.40.25.73 https://all_to_the_bottom.com/cart?goods_id=12&amount=2&cart_id=9079
	private int getGoodId(String ip, int storeGoodId) throws SQLException {
		int goodId;
		
		if(this.goodIdStoreIdCahe.containsKey(storeGoodId)) {
			goodId = this.goodIdStoreIdCahe.get(storeGoodId); 
		} else {
			goodId = this.parserRepository.setGoodStoreIdAndGetId(ip, storeGoodId);
			this.goodIdStoreIdCahe.put(storeGoodId, goodId);
		}
		
		return goodId;
	}
	
	@PostConstruct
	public void parse() {
		if (rebuildDb) {
			try (Scanner scanner = new Scanner(new File(logFilePath))) {
				logger.info("Log file detected and opened");
				try (Connection connection = DriverManager.getConnection(dbURL, dbLoggin, dbPassword)) {
					logger.info("Log database connected");
					try {
						this.ipDatebaseReader = new DatabaseReader.Builder(new File(ipDatebaseFilePath)).build();
						this.logger.info("IP datebase 'GeoLite2 Country' connected");
					} catch (IOException e1) {
						this.logger.error("IOException occurred during opening IP datebase (GeoLite2 Country) file: "
								+ ipDatebaseFilePath);
						e1.printStackTrace();
					}
					parserRepository = new DefaultParserRepository(connection);

					this.logger.info("Dropping all old tables of used scheme: ");
					this.parserRepository.dropSchema();
					this.logger.info("Creating new tables: ");
					this.parserRepository.createNewSchema();

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						this.logger.info(line);

						LocalDateTime ldt = LocalDateTime.parse(line.substring(16, 35), dateTimeFormatter);
						matcher = ipPattrern.matcher(line.substring(53));

						if (matcher.find()) {
							String ip = matcher.group(0);
							String[] subDomans = line.substring(line.indexOf(DOMAIN)).split("/");

							switch (subDomans.length) {
							case 1:
								// 1 - domain browsing
								// https://all_to_the_bottom.com/
								this.parserRepository.saveDomainQuery(ldt, ip, this.getCountryId(ip));
								break;

							case 2:
								// 2 - cart_item
								// https://all_to_the_bottom.com/cart?goods_id=12&amount=2&cart_id=9079
								if (subDomans[1].startsWith("cart?")) {
									String[] params = subDomans[1].substring(5).split("&");
									int storeGoodId = Integer.parseInt(params[0].substring(9));

									this.parserRepository.saveCartItem(Integer.parseInt(params[1].substring(7)),
											this.getGoodId(ip, storeGoodId), Integer.parseInt(params[2].substring(8)),
											ldt, ip, this.getCountryId(ip));
								} else {
									// 3 - payment of the cart
									// https://all_to_the_bottom.com/pay?user_id=84125685042&cart_id=9079
									if (subDomans[1].startsWith("pay?")) {
										String[] params = subDomans[1].substring(5).split("&");
										this.parserRepository.savePaymentQuery(params[0].substring(7),
												Integer.parseInt(params[1].substring(8)), ldt, ip,
												this.getCountryId(ip));
									} else {
										// 4 - success payment of the cart
										// https://all_to_the_bottom.com/success_pay_9079
										if (subDomans[1].startsWith("success_pay_")) {
											this.parserRepository.saveSuccessPaymentQuery(
													Integer.parseInt(subDomans[1].substring(12)), ldt, ip,
													this.getCountryId(ip));
										} else {
											// 5 - category browsing
											// https://all_to_the_bottom.com/canned_food/
											this.parserRepository.saveCategoryQuery(this.getCategory(subDomans[1]), ldt,
													ip, this.getCountryId(ip));
										}
									}
								}
								break;

							case 3:
								// 6 - good browsing
								// https://all_to_the_bottom.com/semi_manufactures/salmon_cutlet/
								this.parserRepository.saveGoodQuery(
										this.getGoodId(new GoodDto(subDomans[2], (subDomans[1]))), ldt, ip,
										this.getCountryId(ip));
								break;
							}
						}
					}

					this.logger.info("");
					this.logger.info("The total number of unique IP: " + this.totalIpNumber);
					this.logger.info(" - recognised   - : " + this.recognizedIpNumber);
					this.logger.info(" - unrecognised - : " + this.unrecognizedIpNumber);
					this.logger.info("");
					this.logger.info("Total number of queryies: " + this.totalIpQueryNumber);
					this.logger.info(" - from recognised IP  - : " + this.recognisedIpQueryNumber);
					this.logger.info(" - from unrecognised   - : " + this.unrecognizedIpQueryNumber);
					this.logger.info("");
				} catch (SQLException e1) {
					this.logger.error("IOException occurred during setting connection to log database");
					e1.printStackTrace();
				}

			} catch (FileNotFoundException e2) {
				this.logger.error("Log file not found: " + e2.getMessage());
				e2.printStackTrace();
			}
		}
	}
}
