var i = 0;
var pg1;
var pg2;
var per1;
var per2;
var timer;

function timerInit() {
  timer = setTimeout('timerInit()', 1000);
  if (i <= 100) {
    pg1.setAttribute('value', i);
    pg2.setAttribute('value', i);
    per1.innerHTML = i + '%';
    per2.innerHTML = i + '%';
    i++;
  } else {
    clearTimeout(timer);
  }
}

window.addEventListener('load', function() {
  per1 = document.getElementById('percent1');
  per2 = document.getElementById('percent2');
  pg1 = document.getElementById('progress1');
  pg2 = document.getElementById('progress2');

  timerInit();
});
