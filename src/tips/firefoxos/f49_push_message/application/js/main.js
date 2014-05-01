window.addEventListener("load", function() {


  var button = document.getElementById("btn");
  button.onclick = function() {
    if (navigator.push) {
      var req = navigator.push.register();

      req.onsuccess = function(e) {
        var endpoint = req.result;
        document.getElementById("msg").innerHTML = "New endpoint: " + endpoint;
        sendXHR('PUT', endpoint, "version=" + new Date().getTime());
      };

      req.onerror = function(e) {
        console.error("Error getting a new endpoint: " + JSON.stringify(e));
      };
    }
  };

  if (window.navigator.mozSetMessageHandler) {
    window.navigator.mozSetMessageHandler('push', function(e) {
      console.log('Push Handler: ' + e.version);
      var icon = document.location.protocol + '//' + document.location.host + '/img/icon/app-icon64.png'
      var notification = navigator.mozNotification.createNotification("Push Notification", e.version, icon);
      notification.show();

    });
  }

  function sendXHR(aType, aURL, aData) {
    var xhr = new XMLHttpRequest({
      mozSystem: true
    });
    xhr.open(aType, aURL);
    xhr.responseType = "json";
    xhr.overrideMimeType("application/json");
    xhr.onload = function(evt) {
      console.log("send XHR success");
    };
    xhr.onerror = function(evt) {
      console.log("send XHR failed");
    };
    xhr.send(aData);
  }

});