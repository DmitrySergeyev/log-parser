var ctx = document.getElementById("chart3").getContext("2d");

var config = {
	type : 'bar',
	data : {
		labels : [ "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00",
				"7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00",
				"14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00",
				"21:00", "22:00", "23:00" ],
		datasets : [ {
			backgroundColor : "rgba(2,117,216,1)",
			borderColor : "rgba(2,117,216,1)",
			fill : false,
		} ]
	},
	options : {
		responsive : true,
		title : {
			display : true,
			text : 'Category views distribution during the day(-s)'
		},
		tooltips : {
			mode : 'index',
			intersect : false,
		},
		hover : {
			mode : 'nearest',
			intersect : true
		},
		scales : {
			xAxes : [ {
				display : true,
				scaleLabel : {
					display : true,
					labelString : 'Hours'
				}
			} ],
			yAxes : [ {
				display : true,
				scaleLabel : {
					display : true,
					labelString : 'Number of views'
				}
			} ]
		}
	}
};

var myChart = new Chart(ctx, config);

var myDatepicker = $('#datepicker3').datepicker({
	range : true,
	toggleSelected : false,
	timepicker : false,
	multipleDatesSeparator : " - ",
	onHide : function(inst, animationCompleted) {
		showReport3();
	}
}).data('datepicker');

$('#select3').on('change', function() {
	showReport3();
});


function dateToYMD(date) {
	var d = date.getDate();
	var m = date.getMonth() + 1; // Month from 0 to 11
	var y = date.getFullYear();
	return '' + y + '-' + (m <= 9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
}

function showReport3() {

	var myDatepickerDates = myDatepicker.selectedDates;
		
	if (myDatepickerDates.length == 2) {

		var date1 = dateToYMD(myDatepickerDates[0]);
		var date2 = dateToYMD(myDatepickerDates[1]);
		
		$.post("http://localhost:8080/api/v1/reports/3", 
		{
			from : date1,
			to : date2,
			categoryId : $('#select3').val()
		}, 
		function(data, status) 
		{
			var dataSet = [];

			config.data.datasets.splice(0, 1);

			var newDataset = {
				label : date1 + " - " + date2,
				backgroundColor : "rgba(2,117,216,1)",
				borderColor : "rgba(2,117,216,1)",
				data : [],
				fill : false
			};

			data.forEach(function(element) {
				newDataset.data.push(element['queryCount']);
			});

			config.data.datasets.push(newDataset);

			myChart.update();
		});
	}
}

$(document).ready(
		function() {
			$.get("http://localhost:8080/api/v1/categories", 
				{},
				function(data, status) {
					var select = $('#select3');
					data.forEach(function(element) {
						select.append($("<option></option>").attr("value",
								element['id']).text(element['name']));
				});

		});
});