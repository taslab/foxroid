var firstView, secondView;

function checkPermission() {
  // Adding the permission directly within the manifest.webapp
  // then Notification.permission is "granted"
  if (Notification && Notification.permission !== "granted") {
    Notification.requestPermission(function(status) {
      if (Notification.permission !== status) {
        Notification.permission = status;
      }
    });
  }
}

function startNotify() {
  if (Notification && Notification.permission === "granted") {
    var n = new Notification("Hi!");
    n.addEventListener("click", function() {
      // Clicked when the notification is closed
      n.close();
      showSecond();
    });
  } else if (Notification && Notification.permission !== "denied") {
    Notification.requestPermission(function(status) {
      if (Notification.permission !== status) {
        Notification.permission = status;
      }

      if (status === "granted") {
        var n = new Notification("Hi!");
        n.addEventListener("click", function() {
          n.close();
          showSecond();
        });
      } else {
        alert("Hi!");
      }
    });
  } else {
    alert("Hi!");
  }
}

window.onload = function() {
  firstView = document.getElementById("first");
  secondView = document.getElementById("second");
  document.getElementById("back-to-first").addEventListener("click", showFirst);
  document.getElementById("start-notification").addEventListener("click", startNotify);
  checkPermission();
}

function showSecond() {
  firstView.classList.add("slide", "out");
  firstView.classList.remove("activate");
  firstView.addEventListener("animationend", on_webkitAnimationEnd, false);
  secondView.classList.add("slide", "in", "activate");
  secondView.classList.remove("hidden");
  secondView.addEventListener("animationend", on_webkitToAnimationEnd, false);
}

function on_webkitAnimationEnd(e) {
  firstView.classList.add("hidden");
  firstView.classList.remove("slide", "out");
  firstView.removeEventListener("animationend", on_webkitAnimationEnd, false);
}

function on_webkitToAnimationEnd(e) {
  secondView.classList.remove("hidden", "slide", "in");
  secondView.removeEventListener("animationend", on_webkitToAnimationEnd, false);
}

function showFirst() {
  secondView.classList.add("slide", "reverse", "out");
  secondView.classList.remove("activate");
  secondView.addEventListener("animationend", on_webkitReverseAnimationEnd, false);
  firstView.classList.add("slide", "reverse", "in", "activate");
  firstView.classList.remove("hidden");
  firstView.addEventListener("animationend", on_webkitReverseToAnimationEnd, false);
}

function on_webkitReverseAnimationEnd(e) {
  secondView.classList.add("hidden");
  secondView.classList.remove("slide", "reverse", "out");
  secondView.removeEventListener("animationend", on_webkitReverseAnimationEnd, false);
}

function on_webkitReverseToAnimationEnd(e) {
  firstView.classList.remove("hidden", "slide", "reverse", "in");
  firstView.removeEventListener("animationend", on_webkitReverseToAnimationEnd, false);
}