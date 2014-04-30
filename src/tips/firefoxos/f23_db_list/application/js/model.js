function insertData() {
    // Enter the set value in the config(Optional because it is automatically generated)
    localforage.config({
        name: "Rank",
        storeName: "items",
        version: "1.0"
    });
    // I want to insert into the DB array data
    localforage.setItem("rank", ranks, function(value) {
        console.log("DBに入っているデータ数:" + value.length);
        loadData();
    });
}

function loadData() {
    localforage.getItem("rank", function(value) {
        var ul = document.createElement("ul");
        document.getElementById("result").appendChild(ul);
        for (var i = 0; i < value.length; i++) {
            var li = document.createElement("li");
            ul.appendChild(li);
            var p = document.createElement("p");
            p.innerHTML = "<font size=\"5\" color=\"#e4006f\">" + value[i].rank + "<br>" + value[i].title + "</font><br>" + value[i].artist + "<br>" + value[i].text;
            li.appendChild(p);
        };
    });
}

window.onload = insertData();