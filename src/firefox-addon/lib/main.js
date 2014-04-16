'use strict';

// load addon-modules
const widgets = require("sdk/widget");
const tabs = require("sdk/tabs");
const data = require('sdk/self').data;
const _ = require('sdk/l10n').get;
const ui = require('sdk/ui');
const { Toolbar } = require("sdk/ui/toolbar");
const { Frame } = require("sdk/ui/frame");

// load user-modules
const dao = require('dao');

/**
 * 
 */
exports.main = function() {
  dao.refresh();
  
  // information related to active tab.
  let pageInfo;
  
  // monitor url changes in the active tab.
  let onPageLoaded = function(tab) {
    pageInfo = undefined;
    linkPanel.hide();
    
    if (tab.url !== undefined) {
      dao.find(tab.url, function(success) {
        widget.iconEnabled(success && success.links);
        pageInfo = success;
      });
    }
    
  }
  tabs.on('load', onPageLoaded);
  tabs.on('activate', onPageLoaded);
  
  
  // Setting of the panel to be displayed when pressed the widget.
  let linkPanel = require('sdk/panel').Panel({
    contentURL: data.url('links.html'),
    contentScriptFile: [
      data.url('jslib/jquery-1.11.0.min.js'),
      data.url('jslib/knockout-3.1.0.js'),
      data.url('jslib/tasjs-commons.min.0.0.1.js'),
      data.url('js/links.js')
    ],
    contentScriptWhen: "ready",
    onShow: function() {
      this.port.emit('show', pageInfo);
    },
    onHide: function() {
      this.port.emit('hide');
    }
  });
  linkPanel.port.on('getProperties', function(keys) {
    let result = {};
    for each (let key in keys) {
      result[key] = _(key);
    }
    this.emit('onGetProperties', result);
  });
  linkPanel.port.on('onItemClicked', function(url){
    tabs.open(url);
    linkPanel.hide();
  });
  linkPanel.port.on('prepareAppendPagePanel', function() {
    linkPanel.hide();
    

    let { emit } = require("sdk/event/core");
    emit(toolbar, 'hide');
  });
  
  
  let widget = ui.ActionButton({
    id: 'widget',
    label: _('WIDGET_LABEL'),
    icon: {
      '16': './resources/icon_disabled16.png',
      '32': './resources/icon_disabled32.png'
    },
    onClick: function(state) {
      linkPanel.show({position: this});
    }
  });
  widget.iconEnabled = function(enabled) {
    this.icon = enabled ? {
      '16': './resources/icon16.png',
      '32': './resources/icon32.png'
    }: {
      '16': './resources/icon_disabled16.png',
      '32': './resources/icon_disabled32.png'
    }
  };
  
  
  let toolbar = Toolbar({
    title : _('WIDGET_LABEL'),
    items : [new Frame({url : "./setting.html"})],
    hidden : false,
    onAttach: function(e) {
    	console.log('##########################', 'attach');
    },
    onDetach: function(e) {
    	console.log('##########################', 'detach');
    },
    onShow: function(e) {
      console.log('##########################', 'show');
    },
    onHide : function(e) {
      console.log('##########################', 'hide');
      console.log('aaaa;;;;;', toolbar);
    }
  });
  console.log('##########################', toolbar.id);
  
}
