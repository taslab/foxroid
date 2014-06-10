window.addEventListener('load', function() {

  var mode = 'MODE_SERVER';
  var service = server;

  var dom = {};
  var ids = ['input_s', 'input_d', 'ipadr', 'adr', 'btn'];
  ids.forEach(function createElementRef(name) {
    dom[name] = document.getElementById(name);
  });

  dom.input_s.onchange = function() {
    mode = 'MODE_SERVER';
    service = server;
    dom.ipadr.disabled = true;
  };
  dom.input_d.onchange = function() {
    mode = 'MODE_CLIENT';
    service = client;
    dom.ipadr.disabled = false;
  };

  stat.init();
  sencer.init();
  dom.adr.innerHTML = 'CurrentIP:' + getIPAddress();

  var toggle = true;
  dom.btn.onclick = function() {
    if (toggle) {
      service.start();
      stat.put('Service Start');
      dom.btn.textContent = 'STOP';
    } else {
      service.stop();
      stat.put('Service STOP');
      dom.btn.textContent = 'START';
    }
    toggle = !toggle;
  };

  function getIPAddress() {
    var WifiManager = navigator.mozWifiManager;
    if (!WifiManager) {
      console.log('wifi NotSupport');
      return 0;
    }
    return WifiManager.connectionInformation.ipAddress;
  }
});

//---------------------
var stat = {
  message: 0,
  init: function() {
    this.message = document.getElementById('status');
    this.put('Service STOP');
  },
  put: function(msg) {
    this.message.innerHTML = msg;
  }
};

//---------------------
var sencer = {
  light: 0,

  init: function() {
    window.addEventListener('devicelight', (e) => {
      this.light = e.value;
      var msg = document.getElementById('sensor');
      msg.innerHTML = 'light: ' + e.value;
    });
  },
  getCurrentValue: function() {
    return this.light;
  }
};
//---------------------
var server = {
  svrsoc: 0,
  start: function() {
    var tcpsocket = navigator.mozTCPSocket;
    this.svrsoc = tcpsocket.listen(8080);
    document.bgColor = '#FFFFFF';
    this.svrsoc.onconnect = function(e) {
      stat.put('connect');
      e.ondata = function(c) {
        console.log(c.data);
        e.send('light:' + sencer.getCurrentValue());
        e.close();
      };
      e.onclose = function() {
        stat.put('disconnect');
      };
    };

    this.svrsoc.onerror = function() {
      console.log('sv_onerror');
    };
  },

  stop: function() {
    if(!!this.svrsoc) this.svrsoc.close();
  }
};
//---------------------
var client = {
  con: 0,
  start: function() {
    var tcpsocket = navigator.mozTCPSocket;
    setInterval(function() {
      var ipaddress = document.getElementById('ipadr').value;
      this.con = tcpsocket.open(ipaddress, 8080);
      con.onopen = function() {
        console.log('socket open');
        this.con.send('client to server message');
      };
      con.ondata = function(e) {
        console.log(e.data);
        var msg = document.getElementById('recieve');
        msg.innerHTML = e.data;
        var res = e.data.split(':');
        var value = Number(res[1]);
        if (value > 10) document.bgColor = '#FF0000';
        else document.bgColor = '#00FF00';
      };
    }, 5000);
  },

  stop: function() {
    if(!!this.con) this.con.close();
  }
};