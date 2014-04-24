window.addEventListener('load', function() {

  if (String.prototype.format === undefined) {
    String.prototype.format = function(arg) {
      var tmp = this;
      // Object
      if (typeof arg === "object") {
        for (var elem in arg) {
          tmp = tmp.replace("{" + elem + "}", arg[elem]);
        }
        // String
      } else {
        for (var i = 0; i < arguments.length; i++) {
          tmp = tmp.replace("{" + i + "}", arguments[i]);
        }
      }
      return tmp;
    };
  }

  if (!navigator.geolocation) {
    console.log('geolocation NotSupport');
    return;
  }

  locate();

  function locate() {
    navigator.geolocation.getCurrentPosition(successCallback, errorCallback);
    setTimeout(locate, 10 * 60 * 1000);
  }

  function successCallback(possion) {
    var str = "{0} lon:{1} lat:{2}".format(gettime(), possion.coords.longitude, possion.coords.latitude);
    document.getElementById('msg').textContent = str;
    writefile(str);
  }

  function errorCallback(error) {
    document.getElementById('msg').textContent = "{0} : {1}".format(gettime(), error.message);
    writefile(gettime() + error.message);
  }

  function gettime() {
    var d = new Date();
    var time = "{0}:{1}:{2} ".format(d.getHours(), d.getMinutes(), d.getSeconds());
    return time;
  }

  function writefile(text) {
    var sdcard = navigator.getDeviceStorage('sdcard');
    var filename = 'myFile.txt';

    var request = sdcard.get(filename);
    request.onsuccess = function(e) {
      var reader = new FileReader();
      reader.readAsText(e.target.result);
      reader.onloadend = function(loadE) {
        var buf = loadE.target.result + '\n' + text;
        var ret = sdcard.delete(filename);
        ret.onsuccess = function() {
          newfile(buf);
        };
      };
    };

    request.onerror = function() {
      newfile(text);
    };

    function newfile(t) {
      var file = new Blob([t], {
        type: 'text/plain'
      });
      var request = sdcard.addNamed(file, filename);
      request.onerror = function() {
        console.warn('Unable to write the file: ' + this.error);
      };
    }

  }

});
