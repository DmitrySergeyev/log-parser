function toISOStringWithhoutChangingDate (date) {
	return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString();
}

function showReport1 (dateFrom, dateTo) {
	
	$.post("http://localhost:8080/api/v1/reports/1", {
		from : dateFrom,
		to : dateTo
	}, 
	function(data, status) {

		var table = $('#table1').DataTable();
		table.clear();
		
		var dataSet = [];

		data.forEach(function(element) {
			dataSet.push([ element['countryName'], element['queryCount'] ]);
		});

		table.rows.add(dataSet);
		table.draw();
		
	});
}

$(document).ready(function() {

	var myDatepicker = $('#datepicker1').datepicker({
		range: true,
		toggleSelected: false,
		timepicker: true,
		timeFormat: 'hh:ii',
		multipleDatesSeparator: " - ",
		onHide: function (inst, animationCompleted) {
			dates = inst.selectedDates;
			
			if(dates.length == 2) {
				dateFrom = toISOStringWithhoutChangingDate(dates[0]);
				dateTo = toISOStringWithhoutChangingDate(dates[1]);
				showReport1(dateFrom, dateTo);
			}
		}
	}).data('datepicker');
	
	$('#table1').DataTable({
		columns : [ { title : "Country"	}, { title : "Query number"	}, ],
		order: [[1, 'desc']]
	});
});