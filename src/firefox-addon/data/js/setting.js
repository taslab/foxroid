$(document).ready(function() {
  const animDuration = 200;
  const faviconURLTemplate = 'http://favicon.st-hatena.com/?url={0}';

  /*
   * difine of view
   */
  const views = {
    contents : $('#__foxroid_setting_contents'),
    origin : {
      url : $('#__foxroid_setting_origin_url'),
      title : $('#__foxroid_setting_origin_title'),
      icon : $('#__foxroid_setting_origin_icon_img')
    },
    page : {
      url : $('#__foxroid_setting_page_url'),
      title : $('#__foxroid_setting_page_title'),
      icon : $('#__foxroid_setting_page_icon_img')
    },
    send : $('#__foxroid_setting_send'),
    notification : {
      loading : $('#__foxroid_setting_notify_loading'),
      send : $('#__foxroid_setting_notify_send'),
      sended : $('#__foxroid_setting_notify_sended')
    }
  };

  /*
  * attach view event/status
  */
  // @formatter:off
  views.origin.url.on('dragover', event => event.originalEvent.preventDefault());
  views.origin.url.on('drop',event => onDrop(event.originalEvent, views.origin));
  views.origin.url.on('blur',event => onBlur(event.originalEvent, views.origin));
  views.page.url.on('dragover', event => event.originalEvent.preventDefault());
  views.page.url.on('drop',event => onDrop(event.originalEvent, views.page));
  views.page.url.on('blur',event => onBlur(event.originalEvent, views.page));
  views.send.on('click', event=> onSendClick(event.originalEvent));
  // @formatter:on

  /**
   *
   * @param {Object} event
   * @param {Object} view
   */
  function onBlur(event, view) {
    var url = $(event.target).val();

    if (StringUtils.isEmpty(url)) {
      return;
    }

    nofityLoading(true);

    // @formatter:off
    __search(url, result => __bindView(view, result));
    // @formatter:on
  }

  /**
   *
   * @param {Object} event
   * @param {Object} view
   */
  function onDrop(event, view) {
    event.preventDefault();

    nofityLoading(true);

    var url = event.dataTransfer.getData("text/plain");
    // @formatter:off
    __search(url, result => __bindView(view, result));
    // @formatter:on
  }

  function __bindView(view, data) {
    view.url.val(data.url);
    view.title.val(data.title);
    view.icon.attr('src', data.icon);

    nofityLoading(false);
  }

  /**
   *
   * @param {Object} shown
   */
  function nofityLoading(shown) {
    __switchDisplay(views.contents, !shown);
    __switchDisplay(views.notification.loading, shown);
  }

  function __switchDisplay($area, shown) {
    if (shown) {
      $area.fadeIn(animDuration);
    } else {
      $area.fadeOut(animDuration);
    }
  }

  function onSendClick(event) {
    notifySend(true);
    setTimeout(function onComplete() {
      //TODO
      __switchDisplay(views.notification.send, false);
      __switchDisplay(views.notification.sended, true);
      setTimeout(function() {
        __switchDisplay(views.notification.sended, false);
        __switchDisplay(views.contents, true);
      }, 1000);
    }, 3000);
  }

  /**
   *
   * @param {Object} shown
   */
  function notifySend(shown) {
    __switchDisplay(views.contents, !shown);
    __switchDisplay(views.notification.send, shown);
  }

  /*
   * Logic
   */

  function __search(url, oncomplete) {

    //TODO 前回と同じURLの場合はじいた方がいい

    var result = {
      url : url,
      title : null,
      icon : null
    };

    $.ajax({
      url : url,
      type : 'GET',
      success : function(data) {
        var div = document.createElement('div');
        $(div).html(data.responseText);
        result.title = $('title', $(div)).text();
        result.icon = faviconURLTemplate.format(url);
        div = null;

        oncomplete.apply(oncomplete, [result]);
      },
      error : function() {
        oncomplete.apply(oncomplete, [result]);
      }
    });
  }

});
