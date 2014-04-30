function findSDcard() {
    var sdcard = navigator.getDeviceStorage("sdcard");
    var request = sdcard.available();
    request.onsuccess = function() {
        // The result is a string
        if (this.result == "available") {
            var p = document.createElement("p");
            p.innerHTML = "The SDCard on your device is available";
            document.getElementById("result").appendChild(p);
        } else if (this.result == "unavailable") {
            var p = document.createElement("p");
            p.innerHTML = "The SDcard on your device is not available";
            document.getElementById("result").appendChild(p);
        } else {
            var p = document.createElement("p");
            p.innerHTML = "The SDCard on your device is shared and thus not availables";
            document.getElementById("result").appendChild(p);
        }
        var free = sdcard.freeSpace();
        free.onsuccess = function() {
            var size = this.result / 1048576;
            var p = document.createElement("p");
            p.innerHTML = "SDCard free space is " + size.toFixed(2);
            document.getElementById("result").appendChild(p);
        }
    }
    request.onerror = function() {
        var p = document.createElement("p");
        p.innerHTML = "Unable to get the space used by the SDCard: " + this.error;
        document.getElementById("result").appendChild(p);
    }
}
window.onload = findSDcard();