'use strict';

// load addon-modules
const tabs = require("sdk/tabs");
const data = require('sdk/self').data;
const _ = require('sdk/l10n').get;
const ui = require('sdk/ui');
const {
    Toolbar
} = require("sdk/ui/toolbar");
const {
    Frame
} = require("sdk/ui/frame");

// load user-modules
const dao = require('dao');
const api = require('foxroid-api');

/**
 *
 */
exports.main = function() {

    // information related to active tab.
    let pageInfo;

    function refleshTabMeta(tab) {
        pageInfo = undefined;
        linkPanel.hide();

        if (tab.url !== undefined) {
            dao.find(tab.url, function(success) {
                actionBtn.iconEnabled(success && success.links && success.links.length > 0);
                pageInfo = success;
            });
        }
    }
    // monitor url changes in the active tab.
    tabs.on('load', refleshTabMeta);
    tabs.on('activate', refleshTabMeta);

    function sync() {
        api.sync(function() {
            refleshTabMeta(tabs.activeTab);
        }, function(e) {
            console.error(e)
        });
    }

    dao.initialize(function() {
        sync();
    });



    // Setting of the actionbutton.
    let actionBtn = ui.ActionButton({
        id: 'actionBtn',
        label: _('WIDGET_LABEL'),
        icon: {
            '16': './resources/icon_disabled16.png',
            '32': './resources/icon_disabled32.png'
        },
        onClick: function(state) {
            linkPanel.show({
                position: this
            });
        }
    });
    actionBtn.iconEnabled = function(enabled) {
        this.icon = enabled ? {
            '16': './resources/icon16.png',
            '32': './resources/icon32.png'
        } : {
            '16': './resources/icon_disabled16.png',
            '32': './resources/icon_disabled32.png'
        }
    };

    // Setting of the panel to be displayed when pressed the actionbutton.
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
        for each(let key in keys) {
            result[key] = _(key);
        }
        this.emit('onGetProperties', result);
    });
    linkPanel.port.on('onItemClicked', function(url) {
        tabs.open(url);
        linkPanel.hide();
    });
    linkPanel.port.on('sync', function() {
        linkPanel.hide();
        sync();
    });

    // Setting of the toolbar for create relation bettween page and page.
    let toolbarFrame = new Frame({
        url: "./toolbar.html",
        onMessage: function(e) {
            if (e.data.type == 'refleshData') {
                sync();
            } else if (e.data.type == 'getCurrentUrl') {
                this.postMessage(tabs.activeTab.url, this.url);
            } else if (e.data.type == 'createLink') {
                api.create(e.data.data, () => {
                    refleshTabMeta(tabs.activeTab);
                    this.postMessage(true, this.url);
                }, () => this.postMessage(false, this.url));
            }
        }
    });
    let toolbar = Toolbar({
        title: _('WIDGET_LABEL'),
        items: [toolbarFrame],
        hidden: true
    });

}
