window.addEventListener('load', function() {
    var body = document.body;
    var element = document.getElementById('list-group');
    var dropArea = document.getElementById('dropArea');
    var dropAreaY =  dropArea.getBoundingClientRect().top;
    var dropAreaX = dropArea.getBoundingClientRect().left;

    var scrollYvalue;
    var options = {
        holdEvents: true
    };
    var GD = new GestureDetector(element, options);
    GD.startDetecting();

    element.addEventListener('holdmove', event => {
      body.style.overflow = 'hidden';
      event.target.style.position = 'absolute';
      event.target.style.zIndex = 999;
  
      scrollYvalue = document.documentElement.scrollTop || document.body.scrollTop;
  
      event.target.style.top = event.detail.position.clientY + scrollYvalue - (event.target.clientHeight / 2) + 'px';
      event.target.style.left = event.detail.position.clientX - (event.target.clientWidth / 2) + 'px';

    });

    element.addEventListener('holdend', event => {
        body.style.overflow = 'auto';

        if (event.detail.end.clientX >= dropAreaX && event.detail.end.clientX <= dropAreaX + dropArea.clientWidth && event.detail.end.clientY >= dropAreaY && event.detail.end.clientY <= dropAreaY + dropArea.clientHeight) {
            alert(event.target.textContent);
        }

        event.target.style.position = 'static';
    });

});