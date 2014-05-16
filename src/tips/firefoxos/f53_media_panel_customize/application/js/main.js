window.addEventListener('load', function(){

  // ff rew skiptime
  const skipvalue = 10;

  var audioBlock = document.getElementById('audioBlock');
  var playbtn = document.getElementById('play');
  var rewbtn = document.getElementById('rew');
  var ffbtn = document.getElementById('ff');
  var startTime = document.getElementById('startTime');
  var endTime = document.getElementById('endTime');
  var seekbar = document.getElementById('seekbar');
  var handle = document.getElementById('handle');

  var eventType = utils.seekbars.isTouch();
  var musicData = [];
  var musicInit = false;

  var musicdb = new MediaDB(
    'music',
    function metadataParserWrapper(file, onsuccess, onerror) {
      LazyLoader.load('js/thirdparty/metadata_scripts.js', function() {
        parseAudioMetadata(file, onsuccess, onerror);
      });
    }, {
      indexes: [
        'metadata.album',
        'metadata.artist',
        'metadata.title',
        'metadata.rated',
        'metadata.played',
        'date'
      ],
      batchSize: 1,
      autoscan: true,
      version: 2
    }
  );

  musicdb.onscanend = function(event) {
    musicdb.enumerateAll('metadata.title', null, null, function(back) {
      function play(filename){
        musicdb.getFile(filename, function(file){
          musicData.push(file);
          var url = URL.createObjectURL(musicData[0]);
          audioBlock.removeAttribute('src');
          audioBlock.load();
          audioBlock.mozAudioChannelType = 'content';
          audioBlock.src = url;
          audioBlock.load();
          playbtn.className = 'media-icon media-play';
        });
      }

      for (var obj in back) {
        if(back[obj].type == "audio/mpeg"){
          play(back[obj].name);
          musicInit = true;
          break;
        }
      }
    });
  };

  //Decomposition of time
  function formatTime(time) {
    var minutes = Math.floor(time / 60) % 60;
    var seconds = Math.floor(time % 60);

    return (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds);
  }

  function getTime(time){
    return (time / audioBlock.duration);
  }

  //Time of 1%
  function getPlayTime(per) {
    music = audioBlock.duration / 100;
    time = per * music;
    return time;
  }

  //audioBlock
  audioBlock.addEventListener('loadedmetadata', function(){
    if(audioBlock.duration){
        endTime.textContent = formatTime(audioBlock.duration);
    } else {
        endTime.textContent = '--:--';
    }
  });

  audioBlock.addEventListener('timeupdate', function() {
    utils.seekbars.set(getTime(audioBlock.currentTime));

    //set starttime endtime
    startTime.textContent = formatTime(audioBlock.currentTime);
    endTime.textContent = formatTime(audioBlock.duration - audioBlock.currentTime);
  });

  audioBlock.addEventListener('ended', function() {

    playbtn.className = 'media-icon media-play';
    seekbar.querySelector('progress').setAttribute('value', 0);
    handle.style.transform = 'translateX(0px)';

    //set starttime endtime
    startTime.textContent = '00:00';
    endTime.textContent = formatTime(audioBlock.duration - audioBlock.currentTime);
  });


  //seekbar
  seekbar.addEventListener(eventType.touchstart, function() {
    audioBlock.pause();
  });

  seekbar.addEventListener(eventType.touchend, function() {
    utils.seekbars.getDragValue();
    per = seekbar.querySelector('progress').value * 100;
    audioBlock.currentTime = getPlayTime(per);
    if (this.isPlay) {
      playbtn.className = 'media-icon media-stop';
      audioBlock.play();
    }
  });


  //handle
  handle.addEventListener(eventType.touchstart, function() {
    window.removeEventListener(eventType.touchstart, arguments.callee);
    audioBlock.pause();

    this.addEventListener(eventType.touchend, function() {
      window.removeEventListener(eventType.touchend, arguments.callee);
      var seekValue = utils.seekbars.getSeek() * 100;
      audioBlock.currentTime = getPlayTime(seekValue);
    });
  });

  //play button
  playbtn.addEventListener(eventType.touchstart, function(){
    if(musicInit) {
      if(audioBlock.paused){
        playbtn.className = 'media-icon media-stop';
        audioBlock.play();
        seekbar.isPlay = true;
      } else {
        playbtn.className = 'media-icon media-play';
        audioBlock.pause();
        seekbar.isPlay = false;
      }
    }
  });

  //rewind button
  rewbtn.addEventListener(eventType.touchstart, function(){
    audioBlock.currentTime -= skipvalue;
  });

  //fastFwd button
  ffbtn.addEventListener('click', function(){
    audioBlock.currentTime += skipvalue;
  });

});