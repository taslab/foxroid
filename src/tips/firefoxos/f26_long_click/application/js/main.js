const interval = 500;

function touchStart(aEvent){
    var target = aEvent.target;
    timer = setTimeout(function() {
        document.querySelector('#action-menu').className = 'fade-in';
        document.querySelector('#btn-cancel').addEventListener ('touchstart', function () {
            document.querySelector('#action-menu').className = 'fade-out';
        });
    }, interval);
}

//Cancel the timer, press and hold
function touchMove(){clearFunction();}
function touchEnd(){clearFunction();}
function onTouchCancel(){clearFunction();}
function clearFunction(){clearTimeout(timer);}


window.onload = function(){
    var container = document.getElementById("list-group");
    container.addEventListener('touchstart', touchStart, false);
    document.addEventListener('touchmove', touchMove, false);
    document.addEventListener('touchend', touchEnd, false);
    addEventListener('touchcancel', onTouchCancel, false);
}