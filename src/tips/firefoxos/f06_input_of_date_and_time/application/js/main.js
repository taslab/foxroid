document.addEventListener('DOMComponentsLoaded', function(){

  var calendar = document.getElementById('cal1');
  var singledate = document.getElementById('singledate');

  //calendar setting
  calendar.chosen = new Date();
  calendar.view = new Date();
  calendar.multiple = false;
  calendar.disableUIToggle = true;
  calendar.span = 1;
  calendar.firstWeekdayNum = 0;
  calendar.labels = {
      prev: "<<",
      next: ">>",
      months: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
      weekdays: ["日", "月", "火", "水", "木", "金", "土"]
  };

  calendar.addEventListener('datetap', function(event){
    singledate.value = event.detail.iso;
  });

});