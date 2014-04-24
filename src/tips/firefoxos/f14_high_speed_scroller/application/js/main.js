window.onload = function() {

    var overlay = document.querySelector('nav[data-type="scrollbar"] p');
    var jumper = document.querySelector('nav[data-type="scrollbar"] ol');
    var scrollable = document.querySelector('#groups-container');

    var scrollToCb = function scrollCb(domTarget, group) {
        if (domTarget.offsetTop > 0) {
            scrollable.scrollTop = domTarget.offsetTop;
        }
    };

    var params = {
        overlay: overlay,
        jumper: jumper,
        groupSelector: '#group-',
        scrollToCb: scrollToCb
    };

    utils.alphaScroll.init(params);
};