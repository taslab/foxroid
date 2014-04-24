window.addEventListener("load", function() {

  var music = navigator.getDeviceStorage('music');
  if(music){
    var req = music.enumerate();
    req.onsuccess = function(e){
      var file = e.target.result;
      document.getElementById("title").innerHTML =  file.name;
      var audio = new Audio (window.URL.createObjectURL (file));
      audio.play();
    }
    req.onerror = function(e){
      console.log("error");
    }
  }
});



