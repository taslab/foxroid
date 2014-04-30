var element;
var touch;
var ox;
var oy;

// Supporting mouse or touch events depending on the delivery context
var isTouch = 'ontouchstart' in window;
var touchstart = isTouch ? 'touchstart' : 'mousedown';
var touchmove = isTouch ? 'touchmove' : 'mousemove';
var touchend = isTouch ? 'touchend' : 'mouseup';

//pc or fxos
function etype(e){
    return isTouch ? e.touches[0] : e;
}


//
function getScrollPosition(document_obj){
    return{
        x:document_obj.body.scrollLeft || document_obj.documentElement.scrollLeft,
        y:document_obj.body.scrollTop  || document_obj.documentElement.scrollTop
    };
}

function addXY(area) {
    element = area;

    //Original position
    ox = parseInt(element.style.left);
    oy = parseInt(element.style.top);

    document.addEventListener(touchstart, start,true);
}

function removeXY() {
    document.removeEventListener(touchstart, start,true);
}

function start(e) {
    touch = etype(e);
    var elm = document.getElementById('body');
    elm.style.overflow = 'hidden'; 

    //The difference between the mouse point and the point of the upper left
    this.ex = touch.clientX - parseInt(element.style.left); 
    this.ey = touch.clientY - parseInt(element.style.top);
    document.addEventListener(touchmove, drag, true);
    document.addEventListener(touchend, end, true);
}

function drag(e) {
    touch = etype(e);

    var scroll_pos = getScrollPosition(document);
    dx = touch.clientX - parseInt(element.offsetWidth) / 2;
    dy = (touch.clientY +  scroll_pos.y) - parseInt(element.offsetHeight) / 2;

    element.style.top = dy + 'px';
    element.style.left =  dx + 'px';
    element.style.position = 'absolute';
    element.style.zIndex = 1200;
}

function end(e) {
    touch = etype(e);
    e.preventDefault();

    var dA = document.getElementById('dropArea');
    this.dAy =  dA.getBoundingClientRect().left;
    this.dAx = dA.getBoundingClientRect().top;
    if (dropArea.clientWidth <= touch.clientX && touch.clientX <= this.dAy + dropArea.clientWidth && this.dAx <= touch.clientY && touch.clientY <= this.dAx + dropArea.clientHeight) {
        alert(touch.target.id);
    }
    element.style.left = ox + 'px';
    element.style.top = oy + 'px';
    element.style.position = 'relative';
    element.style.zIndex = 800;
    var elm = document.getElementById('body');
    elm.style.overflow = 'auto'; 
    document.removeEventListener(touchmove, drag,true);
    document.removeEventListener(touchend, end,true);
}

