const INTERVAL = 5000;

var intervalid = 0;
addEventListener('message', function(e) {

  if (e.data.stop) {
    clearInterval(intervalid);
    return;
  } else if (intervalid !== 0) {
    clearInterval(intervalid);
  }
  intervalid = setInterval(function() {
    getserver(e.data.name);
  }, INTERVAL);
});

function getserver(servername) {
  var xhr = new XMLHttpRequest({
    mozSystem: true
  });
  xhr.open('GET', servername, true);
  xhr.onreadystatechange = function() {
    if ((xhr.readyState == 4) && (xhr.status == 200)) {
      postMessage(xhr.responseText);
    }
  };
  xhr.send(null);
}