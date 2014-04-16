'use strict';

let FAVICON_GETTER = 'http://favicon.st-hatena.com/?url={0}';
let FAVICON_FIREFOX = 'http://www.mozilla.jp/static/images/firefox/favicon-32.ico';
let FAVICON_ANDROID = 'http://developer.android.com/favicon.ico';

// entity
function Item() {
}
Item.valueOf = data => ({
  url: data.url,
  title: data.description,
  site: data.name,
  icon : !StringUtils.isEmpty(data.icon) ? data.icon: FAVICON_GETTER.format(data.url),
  category : {
    icon: data.category == 1 ? FAVICON_FIREFOX: data.category == 2 ? FAVICON_ANDROID: null,
    name: data.category == 1 ? 'Firefox': data.category == 2 ? 'Android': null
  }
});

// connect between view and viewmodel.
let
viewmodel = {
  lblEmpty : ko.observable(),
  lblAppendPage : ko.observable(),

  tips : ko.observableArray(),

  attachItemEvent : function(element, index, data) {
    $(element).filter('li').bind('click', function(event) {
      self.port.emit('onItemClicked', data.url);
    });
  },

  swapData : function(newData) {
    this.tips.removeAll();
    let array = [];
    for (let i in newData) {
      array.push(Item.valueOf(newData[i]));
    }
    ko.utils.arrayPushAll(this.tips, array);
  }
};
ko.applyBindings(viewmodel);

self.port.on('show', function(pageInfo) {
  //FIXME require('sdk/l10n').getみたいのがcontentScript内ではできない？のでport経由でローカライズ
  self.port.once('onGetProperties', function(values) {
    viewmodel.lblEmpty(values.LABEL_EMPTY_MESSAGE);
    viewmodel.lblAppendPage(values.LABEL_APPEND_EXTARNAL_WEBSITE);
  });
  self.port.emit('getProperties', ['LABEL_EMPTY_MESSAGE', 'LABEL_APPEND_EXTARNAL_WEBSITE']);

  if (pageInfo != null) {
    viewmodel.swapData(pageInfo.links);
  }
});

self.port.on('hide', function() {
  viewmodel.swapData(null);
});


$('#appendPage').bind('click', function(event) {
  self.port.emit('prepareAppendPagePanel');
});