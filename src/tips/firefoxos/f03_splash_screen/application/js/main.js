var end = false;

check();
main();

function check() {
    if (!end) {
        setTimeout('check()', 100);
    } else {
        //fade animation
        $('#Splash').addClass('fadeOut');
        //fade animation
        $('#Splash').on('animationend', function(){
            $('#Splash').css('display', 'none');
            $('#Splash').unbind('animationend');
        });
    }
}

function main() {
    setTimeout(function(){
        end = true;
    }, 1000);
}