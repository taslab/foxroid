window.addEventListener('load', function() {

  var dom = {};
  var ids = ['btn', 'txt1', 'ck1'];
  ids.forEach(function createElementRef(name) {
    dom[name] = document.getElementById(name);
  });

  dom.btn.addEventListener('click', function() {
    localStorage.clear();
    dom.txt1.value = '';
    dom.ck1.checked = false;
  });

  var txt1 = dom.txt1;
  if (localStorage.getItem(txt1.id)) {
    txt1.value = localStorage.getItem(txt1.id);
    console.log(txt1.value);
  }
  txt1.addEventListener('keypress', function(e) {
    localStorage.setItem(txt1.id, txt1.value + String.fromCharCode(e.charCode));
  });

  var ck1 = dom.ck1;
  if (localStorage.getItem(ck1.id)) {
    var v = localStorage.getItem(ck1.id);
    ck1.checked = ((v == 1) ? true : false);
  }
  ck1.addEventListener('click', function() {
    var v = ck1.checked ? 1 : 0;
    localStorage.setItem(ck1.id, v);
  });

});