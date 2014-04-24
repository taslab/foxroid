window.addEventListener("load", function() {

  var audio = new Audio();
  var music = navigator.getDeviceStorage('music');
  if (music) {
    var req = music.enumerate();
    req.onsuccess = function(e) {
      audio.pause();
      audio = new Audio(window.URL.createObjectURL(e.target.result));
      audio.play();
      document.getElementById("sound").innerHTML = e.target.result.name;
      audio.onended = function() {
        e.target.
        continue ();
      }
      audio.onerror = function() {
        e.target.
        continue ();
      }
    }
  }

  var toggle = true;
  document.getElementById("play").onclick = function() {
    if (toggle) {
      document.getElementById("play").textContent = "pause";
      audio.play();
    } else {
      document.getElementById("play").textContent = "play";
      audio.pause();
    }
    toggle = !toggle;
  }

});