var twitter = {
  consumerKey: 'pJZKHkmWjbBGbxp2m6EqtiM9n',
  consumerSecret: 'fL4Jxijza0Ldb2fWCbYTmBrZUyiA2vM1arZTd8LWwbwnfun7Xo',


  errorfunc: function() {
    console.log('error');
  },

  //---------------------
  getRequestToken: function(callback) {
    var accessor = {
      consumerSecret: this.consumerSecret,
      tokenSecret: ''
    };

    var message = {
      method: 'GET',
      action: 'https://api.twitter.com/oauth/request_token',
      parameters: {
        oauth_version: '1.0',
        oauth_signature_method: 'HMAC-SHA1',
        oauth_consumer_key: this.consumerKey,
      }
    };

    OAuth.setTimestampAndNonce(message);
    OAuth.SignatureMethod.sign(message, accessor);
    var target = OAuth.addToURL(message.action, message.parameters);

    this.getserverdata(target, function(data) {
      var ret = splitdata(data);
      callback(ret);
    });
  },

  //---------------------
  getAccessToken: function(callback) {
    var accessor = {
      consumerSecret: this.consumerSecret,
      tokenSecret: localStorage.getItem('request_token_secret')
    };

    var message = {
      method: 'GET',
      action: 'https://api.twitter.com/oauth/access_token',
      parameters: {
        oauth_version: '1.0',
        oauth_signature_method: 'HMAC-SHA1',
        oauth_consumer_key: this.consumerKey,
        oauth_token: localStorage.getItem('request_token'),
        oauth_verifier: localStorage.getItem('oauth_verifier')
      }
    };

    OAuth.setTimestampAndNonce(message);
    OAuth.SignatureMethod.sign(message, accessor);
    var target = OAuth.addToURL(message.action, message.parameters);

    this.getserverdata(target, function(data) {
      var ret = splitdata(data);
      callback(ret);
    });
  },

  //---------------------
  getTimeLine: function(callback) {
    var accessor = {
      consumerSecret: this.consumerSecret,
      tokenSecret: localStorage.getItem('access_token_secret')
    };

    var message = {
      method: 'GET',
      action: 'https://api.twitter.com/1.1/statuses/home_timeline.json',
      parameters: {
        oauth_version: '1.0',
        oauth_signature_method: 'HMAC-SHA1',
        oauth_consumer_key: this.consumerKey,
        oauth_token: localStorage.getItem('access_token'),
        count: '50'
      }
    };

    OAuth.setTimestampAndNonce(message);
    OAuth.SignatureMethod.sign(message, accessor);
    var target = OAuth.addToURL(message.action, message.parameters);

    this.getserverdata(target, function(data) {
      callback(data);
    });
  },

  //---------------------
  getserverdata: function(target, callback) {
    var xhr = new XMLHttpRequest({
      mozSystem: true
    });
    xhr.open('GET', target, true);

    xhr.onreadystatechange = function() {
      if ((xhr.readyState === 4 && xhr.status === 200) || (xhr.readyState === 4 && xhr.status === 0)) {
        callback(xhr.responseText);
      } else if (xhr.statusText !== 'OK') {
        errorfunc();
      }
    };
    xhr.onerror = function() {
      errorfunc();
    };
    xhr.send(null);
  }
};

function splitdata(data) {
  var a = [];
  var dat = data.split('&');
  for (var i = 0; i < dat.length; i++) {
    var d = dat[i].split('=');
    a[d[0]] = d[1];
    a.length++;
  }
  return a;
}

function update(data) {
  var dat;
  for (var i = 0; i < data.length; i++) {
    if (typeof(dat) === 'undefined') dat = '<p>' + data[i].user.name + ' : ' + data[i].text + '</p>';
    else dat += '<p>' + data[i].user.name + ' : ' + data[i].text + '</p>';
  }
  document.getElementById('msg').innerHTML = dat;
}

window.addEventListener('load', function() {

  var param = window.location.search.substring(1, window.location.search.length);
  var dat = splitdata(param);

  if (dat.length !== 0) {
    disptimeline();
  }

  document.getElementById('btn').onclick = function() {

    twitter.getRequestToken(function(res) {
      localStorage.setItem('request_token', res['oauth_token']);
      localStorage.setItem('request_token_secret', res['oauth_token_secret']);
      window.location.href = 'https://api.twitter.com/oauth/authorize?oauth_token=' + res['oauth_token'];
    });
  };

  function disptimeline() {

    localStorage.setItem('oauth_verifier', dat['oauth_verifier']);
    twitter.getAccessToken(function(res) {
      localStorage.setItem('access_token', res['oauth_token']);
      localStorage.setItem('access_token_secret', res['oauth_token_secret']);
      twitter.getTimeLine(function(data) {

        var timeline = JSON.parse(data);
        update(timeline);
      });
    });
  }

});