window.addEventListener("load", function() {

  var dom = {};
  var ids = ['msg', 'btn'];
  ids.forEach(function createElementRef(name) {
    dom[name] = document.getElementById(name);
  });

  var WifiManager = navigator.mozWifiManager;
  if (!WifiManager) {
    console.log("testwifi NotSupport");
    return;
  }

  WifiManager.onstatuschange = function(event) {
    dom.msg.textContent = event.status;
    if (event.status === 'connected') {
      dom.msg.textContent += ':' + WifiManager.connection.network.ssid;
    }
  };

  var toggle = true;
  dom.btn.addEventListener('click', function() {
    toggle = toggle ? false : true;
    var lock = navigator.mozSettings.createLock();
    var result = lock.set({
      'wifi.enabled': toggle
    });
  });

});