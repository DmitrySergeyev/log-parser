package dsergeyev.projects.weblogparser.controllers;

import org.springframework.web.bind.annotation.RestController;

import dsergeyev.projects.weblogparser.services.ReportCreatorService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class LoggerController {

	// I used numbers of reports from the task in controller mapping to simplify it checking and 
	// avoiding very long report names (at least it seems logical for me in this case)
	private static final String API = "/api/v1";
	private static final String REPORTS_DOMAIN = API + "/reports";
	private static final String REPORT_1 = REPORTS_DOMAIN + "/1";
	private static final String REPORT_2 = REPORTS_DOMAIN + "/2";
	private static final String REPORT_3 = REPORTS_DOMAIN + "/3";
	private static final String REPORT_4 = REPORTS_DOMAIN + "/4";
	private static final String REPORT_5 = REPORTS_DOMAIN + "/5";
	private static final String REPORT_6 = REPORTS_DOMAIN + "/6";
	private static final String REPORT_7 = REPORTS_DOMAIN + "/7";
	private static final String CATEGORIES = API + "/categories";
	
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 1000;
	
	private ReportCreatorService reportCreatorService = new ReportCreatorService();

	// 1) Посетители из какой страны совершают больше всего действий на сайте?
	// Каждой строчке соответствует один запрос к серверу. 
	// Для выбранного диапазона времени (дата + время в формате yyyy-mm-ddTdd:hh:ss) 
	// возвращается список названий стран и соответствующие им количества запросов. 
	// Список отсортирован по уменьшению количества запросов, соответственно первый элемент 
	// содержит название страны с наибольшим числом запросов. 
	 @PostMapping(value = REPORT_1)
    public ResponseEntity<?> getReport1(
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
		try {
			return new ResponseEntity<>(this.reportCreatorService.getReport1(from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    // 2) Посетители из какой страны чаще всего интересуются товарами из определенных категорий?
	// Для указанной категории выбираются все категории товара. Для всех товаров с учетом указанного диапазона 
	// времени (дата + время в формате yyyy-mm-ddTdd:hh:ss) выбираются все просмотры товара, которые группируются 
	// по стране откуда был сделан запрос. Результат возвращается в виде списка стран и соответствующего им числа 
	// запросов, с сортировкой по уменьшению количества последних. Самый первый элемент – страна откуда было получено
	// больше всего запросов на просмотр товаров заданной категории. 
    @PostMapping(value = REPORT_2)
    public ResponseEntity<?>  getReport2(
    		@RequestParam(name = "categoryId")int categoryId,
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
		try {
			return new ResponseEntity<>(this.reportCreatorService.getReport2(categoryId, from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    // 3) В какое время суток чаще всего просматривают определенную категорию товаров?
    // 	Для указанного диапазона дат (yyyy-mm-dd) выбираются все просмотры указанной категории и строится почасовая
    // гистограмма просмотров. При выборе нескольких дат просмотры, сгруппированные по часам суток, складываются для 
    // всех дней. Возвращается количество просмотров, отнесенное к суткам в часах. Список отсортирован по часам (от 0 
    // до 23), всего 24 значения). 
    @PostMapping(value = REPORT_3)
    public ResponseEntity<?>  getReport3(
    		@RequestParam(name = "categoryId")int categoryId,
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
		try {
			return new ResponseEntity<>(this.reportCreatorService.getReport3(categoryId, from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    // 4) Какая нагрузка (число запросов) на сайт за астрономический час?
    // Для указанного диапазона времени (дата + время в формате yyyy-mm-ddTdd:hh:ss) считает количество запросов и 
    // группирует их по времени (по целым значениям часа). Возвращает список в формате: день, час дня, количество 
    // запросов. Список отсортирован по увеличению номера дня и номера часа внутри одного дня. Количество возвращаемых
    // элементов равно количеству целых часов, покрывающих заданные период времени. 
    @PostMapping(value = REPORT_4)
    public ResponseEntity<?>  getReport4(
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
    	try {
			return new ResponseEntity<>(this.reportCreatorService.getReport4(from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    // 5) Товары из какой категории чаще всего покупают совместно с товаром из заданной категории?
	// Для выбранной категории товара выбираются все позиции. Для указанного диапазона времени (дата + время в формате 
    // yyyy-mm-ddTdd:hh:ss) выбираем все успешно оплаченные корзины содержащие интересующие нас позиции. Полученный 
    // результаты группируем по категориям, исключая исходную. Возвращается ответ в формате наименования категории 
    // и количества запросов. Сортировка списка по убыванию количества последних. 
    @PostMapping(value = REPORT_5)
    public ResponseEntity<?>  getReport5(
    		@RequestParam(name = "categoryId")int categoryId,
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
    	try {
			return new ResponseEntity<>(this.reportCreatorService.getReport5(categoryId, from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    // 6) Сколько брошенных (не оплаченных) корзин имеется за определенный период?
    // Для указанного диапазона времени (дата + время в формате yyyy-mm-ddTdd:hh:ss) выбираем все корзины которые 
    // были созданы или обновлены (в которые добавлялся товар). Исключаем те, что были оплачены за указанный период.
    // Получаем количество неоплаченных корзин - одно число. 
    @PostMapping(value = REPORT_6)
    public ResponseEntity<?>  getReport6(
		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
			try {
				return new ResponseEntity<>(this.reportCreatorService.getReport6(from, to), HttpStatus.OK);
			} catch (SQLException e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
    }
    
    // 7) Какое количество пользователей совершали повторные покупки за определенный период?
	// Для указанного периода времени (дата + время в формате yyyy-mm-ddTdd:hh:ss) находим все оплаченные корзины, 
    // группируем по user id. Возвращается DTO с указанием общего количества оплаченных корзин и списком, 
    // состоящим из набора пар: количество покупок – количество пользователей, совершивших указанное количество покупок,
    // отсортированный по увеличению числа покупок. Первый элемент списка, наиболее вероятно, содержит значение
    // количества покупок равное 1. Сумма количества пользователей из всех остальных элементов является количество 
    // пользователей, которые совершили 2 и более покупки за указанный период.
    @PostMapping(value = REPORT_7)
    public ResponseEntity<?>  getReport7(
    		@RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from, 
    		@RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
    		@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
    	try {
			return new ResponseEntity<>(this.reportCreatorService.getReport7(from, to), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @GetMapping(value=CATEGORIES)
    public ResponseEntity<?>  getCategories() {
    	try {
			return new ResponseEntity<>(this.reportCreatorService.getCategories(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
        
}
