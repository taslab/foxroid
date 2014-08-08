window.addEventListener('load', function(){

  document.getElementById('btn').onclick = function(){

    var manager = navigator.mozApps.mgmt;
    var applist = manager.getAll();
    var installed = false;

    applist.onsuccess = function(){

      for(var i=0;i<applist.result.length;i++){
        var app = applist.result[i];
        if(app.manifestURL === 'https://marketplace.firefox.com/app/fa85e65f-14ac-4736-89da-7fc27ac8862c/manifest.webapp'){

          var a = app.launch();
          a.onsuccess = function(){
            console.log('Exec success');
          };
          a.onerror = function(){
            console.log('Exec error');
          };
          installed = true;
          break;
        }
      }

      if (!installed) {
        new MozActivity({
          name: 'view',
          data: {
            type: 'url',
            url: 'https://marketplace.firefox.com/app/notesplus'
          }
        });
      }
    };

    applist.onerror = function(){
      console.log('error');
    };

  };

});