var musicdata = {
  playdata: 0,
  index: 0,

  init: function() {
    var music = navigator.getDeviceStorage('music');
    if (!music) return;

    this.playdata = Array();
    this.index = 0;

    var req = music.enumerate();
    req.addEventListener('success', evt => {
      this.playdata.push(evt.target.result);
      evt.target.continue();
    });
  },

  getsrc: function() {
    return this.playdata[this.index];
  },
  getmeta: function() {
    return this.playdata[this.index].name;
  },
  next: function() {
    this.index++;
    if (this.index >= this.playdata.length - 1) this.index = 0;
  },
  previous: function() {
    this.index--;
    if (this.index < 0) this.index = this.playdata.length - 1;
  },
};


var player = {
  dir: true,
  playStatus: 'PLAYSTATUS_STOP',

  get audio() {
    return document.getElementById('player-audio');
  },
  init: function() {
    this.playStatus = 'PLAYSTATUS_STOP';

    this.audio.addEventListener('ended', () => {
      console.log('onend');
      this.next();
    });
    this.audio.addEventListener('error', () => {
      console.log('onerror');
      if (this.dir) this.next();
      else this.previous();
    });
  },
  play: function() {
    this.audio.mozAudioChannelType = 'content';
    this.audio.src = window.URL.createObjectURL(musicdata.getsrc());
    this.playStatus = 'PLAYSTATUS_PLAYING';
    this.audio.play();
    this.updateRemoteMetadata(musicdata.getmeta());
    this.updateRemotePlayStatus('PLAYING');
    document.getElementById('msg').innerHTML = musicdata.getmeta();
  },
  playpause: function() {
    if (this.playStatus == 'PLAYSTATUS_STOP') {
      this.play();
    } else if (this.playStatus == 'PLAYSTATUS_PAUSE') {
      this.playStatus = 'PLAYSTATUS_PLAYING';
      this.audio.play();
      this.updateRemotePlayStatus('PLAYING');
    } else if (this.playStatus == 'PLAYSTATUS_PLAYING') {
      this.playStatus = 'PLAYSTATUS_PAUSE';
      this.audio.pause();
      this.updateRemotePlayStatus('PAUSE');
    }
  },
  next: function() {
    this.dir = true;
    musicdata.next();
    this.play();
  },
  previous: function() {
    this.dir = false;
    musicdata.previous();
    this.play();
  },
  updateRemoteMetadata: function(msg) {
    var notifyMetadata = {
      title: msg,
      artist: msg,
      album: msg
    };
    MusicComms.notifyMetadataChanged(notifyMetadata);
  },
  updateRemotePlayStatus: function(msg) {
    var info = {
      playStatus: msg,
    };
    MusicComms.notifyStatusChanged(info);
  }

};

var MusicComms = {
  enabled: false,

  commands: {
    playpause: function() {
      player.playpause();
    },
    next: function() {
      player.next();
    },
    previous: function() {
      player.previous();
    }
  },
  init: function() {
    this.mrc = new MediaRemoteControls();

    for (var command in this.commands) {
      this.mrc.addCommandListener(command, this.commands[command].bind(this));
    }

    this.mrc.start();

    this.mrc.notifyAppInfo({
      origin: window.location.origin,
      icon: window.location.origin + '/img/icon/app-icon64.png'
    });

    this.enabled = true;
  },
  notifyMetadataChanged: function(metadata) {
    if (this.enabled)
      this.mrc.notifyMetadataChanged(metadata);
  },
  notifyStatusChanged: function(info) {
    if (this.enabled)
      this.mrc.notifyStatusChanged(info);
  },
};

window.addEventListener('load', function() {

  MusicComms.init();
  player.init();
  musicdata.init();

  document.getElementById('btn2').addEventListener('click', function() {
    player.previous();
  });
  document.getElementById('btn3').addEventListener('click', function() {
    player.next();
  });
  document.getElementById('btn').addEventListener('click', function() {
    player.playpause();
  });


});