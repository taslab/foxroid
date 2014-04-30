window.addEventListener("load", function() {

  var dom = {};
  var ids = ['button', 'msg', 'log'];
  ids.forEach(function createElementRef(name) {
    dom[name] = document.getElementById(name);
  });

  dom.button.onclick = function() {

    var bluetooth = window.navigator.mozBluetooth;
    if (!bluetooth) {
      console.log("not support mozBluetooth");
      window.alert("not support mozBluetooth");
    }

    if (!bluetooth.enabled) {
      console.log("mozBluetooth disable");
      window.alert("mozBluetooth disable");
      return;
    }
    console.log("mozBluetooth OK");

    var defaultadapter;
    var discoverTimeoutTime = 60000;
    var discoverTimeout = null;
    var array = [];

    var BluetoothAdapter = bluetooth.getDefaultAdapter();
    dom.msg.textContent = '';

    BluetoothAdapter.onsuccess = function() {

      defaultadapter = BluetoothAdapter.result;
      var startDiscoveryflag;
      var req = defaultadapter.startDiscovery();
      req.onsuccess = function() {
        startDiscoveryflag = true;
        printLog("startDiscovery");
        discoverTimeout = setTimeout(stopDiscovery, discoverTimeoutTime);
      };
      req.onerror = function() {
        startDiscoveryflag = false;
        printLog("startDiscovery fail");
      };

      var ul = document.createElement('ul');

      defaultadapter.ondevicefound = function(evt) {
        var device = evt.device;

        function cksamedevice() {
          for (var i = 0; i < array.length; i++) {
            if (array[i].name === device.name) return false;
          }
          return true;
        }

        if (cksamedevice()) {
          array.push(device);
          var li = document.createElement('li');
          li.innerHTML = device.name;
          li.addEventListener("click", function() {
            stopDiscovery(device);
          });
          ul.appendChild(li);
          dom.msg.appendChild(ul);
        }
      };

      function Pair(device) {
        console.log("pairing:" + device.name);
        printLog("pairing:" + device.name);
        var req = defaultadapter.pair(device.address);
        req.onsuccess = function() {
          printLog("paired:" + device.name);
        };
        req.onerror = function(error) {
          printLog("paired:" + req.error.name);
        };
      }

      function stopDiscovery(device) {
        if (!startDiscoveryflag) return;
        startDiscoveryflag = false;
        var req = defaultadapter.stopDiscovery();
        req.onsuccess = function() {
          printLog("stopDiscovery");
          Pair(device);
        };
        req.onerror = function() {
          printLog("stopDiscovery fail");
        };
        clearTimeout(discoverTimeout);
      }
    };
    BluetoothAdapter.onerror = function() {
      console.log("mozBluetooth fail");
    };

    function printLog(log) {
      dom.log.innerHTML = log;
    }
  };

});