window.onload = function() {

  document.getElementById("openBrowser").onclick = function() {
    var address = document.getElementById("url").value;
    new MozActivity({
      name: "view",
      data: { type: "url", url: address }
    });
  };

};
