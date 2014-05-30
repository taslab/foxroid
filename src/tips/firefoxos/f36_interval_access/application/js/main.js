window.addEventListener('load', function() {

  var worker = new Worker('js/worker.js');
  worker.onmessage = function(e) {
    document.getElementById('msg').innerHTML = e.data;
  };

  document.getElementById('btn').onclick = function() {
    var servername = document.getElementById('name').value;
    worker.postMessage( {'name': servername} );
  };
  document.getElementById('stop').onclick = function() {
    worker.postMessage( {'stop': true } );
  };

});
