var script = document.createElement('script');
script.src = "https://www.youtube.com/iframe_api";

var firstScript = document.getElementsByTagName('script')[0];
firstScript.parentNode.insertBefore(script, firstScript);

var player;
function onYouTubeIframeAPIReady() {
  player = new YT.Player('player', {
    width: '320',
    height: '240',
    videoId: 'DjjkAYI5kNM',
    events: {
      'onReady': onPlayerReady,
      'onStateChange': onPlayerStateChange
    }
  });
}

function onPlayerReady(event) {
  event.target.playVideo();
}

var done = false;
function onPlayerStateChange(event) {
  if (event.data == YT.PlayerState.PLAYING && !done) {
    setTimeout(stopVideo, 6000);
    done = true;
  }
}
function stopVideo() {
  player.stopVideo();
}
