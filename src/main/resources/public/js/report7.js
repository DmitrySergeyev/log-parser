var chartColors = [ 
	'rgb(75, 192, 192)', 
	'rgb(255, 159, 64)', 
	'rgb(255, 99, 132)',
	'rgb(255, 205, 86)', 
	'rgb(54, 162, 235)',
	'rgb(153, 102, 255)',
	'rgb(231,233,237)'];

var ctx = document.getElementById("chart7").getContext("2d");

var config = {
	type : 'doughnut',
	data : {
		datasets : [ {
			backgroundColor : "rgba(2,117,216,1)",
			borderColor : "rgba(2,117,216,1)",
			fill : false,
		} ]
	},
	options: {
		display: true
	}
};

var myChart = new Chart(ctx, config);

var myDatepicker = $('#datepicker7').datepicker({
	range : true,
	toggleSelected : false,
	timepicker : true,
	timeFormat : 'hh:ii',
	multipleDatesSeparator : " - ",
	onHide : function(inst, animationCompleted) {
		dates = inst.selectedDates;
		console.log(dates);
		if (dates.length == 2) {
			console.log("11");
			dateFrom = toISOStringWithhoutChangingDate(dates[0]);
			dateTo = toISOStringWithhoutChangingDate(dates[1]);
			showReport7(dateFrom, dateTo);
		}
	}
}).data('datepicker');

function toISOStringWithhoutChangingDate(date) {
	return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
			.toISOString();
}

function dateToYMD(date) {
	var d = date.getDate();
	var m = date.getMonth() + 1; // Month from 0 to 11
	var y = date.getFullYear();
	return '' + y + '-' + (m <= 9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
}

function showReport7(date1, date2) {

	$.post("http://localhost:8080/api/v1/reports/7", {
		from : date1,
		to : date2
	}, function(data, status) {
		myData = data['usersCountPurchase'];
		console.log(myData);

		config.data.datasets.splice(0, 1);
		config.data.labels.splice(0, 1);

		var colorNames = Object.keys(window.chartColors);
		var colorName = colorNames[config.data.datasets.length % colorNames.length];
	    var newColor = window.chartColors[colorName];
	      
		var newLables = [];
		var newDataset = {
			label : date1 + " - " + date2,
			backgroundColor : chartColors,
			borderColorchartColors: chartColors,
			data : []
		};

		myData.forEach(function(element) {
			newDataset.data.push(element['usersCount']);
			newLables.push(element['purchaseCount'] + " purchase (" + element['usersCount'] + ")");
		});

		console.info(newDataset);
		console.info(newLables);
		config.data.labels = newLables;
		config.data.datasets.push(newDataset);
		config.options.text = date1 + " - " + date2;

		window.colorIndex = 0;
		myChart.update();
	});

}