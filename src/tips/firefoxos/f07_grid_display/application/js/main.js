window.addEventListener('load', function(){

  var elmGrid = document.getElementsByClassName('grid');
  var elmGridWidth = elmGrid[0].clientWidth;
  for (var i = 0; i < elmGrid.length; i++) {
    elmGrid[i].style.height = elmGridWidth + 'px';
  }

});