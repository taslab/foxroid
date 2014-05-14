'use strict';

const prefs = require('sdk/simple-prefs').prefs;
const Request = require("sdk/request").Request;
// load user-modules
const dao = require('dao');


const API_BASE_URI = 'http://127.0.0.1:9000/';
const API_PAGES_TEMPLATE = API_BASE_URI + 'pages?updated={0}&ignorePendingLink=true';
const API_LINK_URI = API_BASE_URI + 'link';

if (!String.prototype.format) {
    String.prototype.format = function(arg) {
        let rep_fn = undefined;
        if (typeof arg == "object") {
            rep_fn = function(m, k) {
                return arg[k];
            };
        } else {
            let args = arguments;
            rep_fn = function(m, k) {
                return args[parseInt(k)];
            };
        }
        return this.replace(/\{(\w+)\}/g, rep_fn);
    };
}

function sync(onsuccess, onfailed) {
    try {
        let latest = !! prefs.LATEST_SYNCHRONIZED ? prefs.LATEST_SYNCHRONIZED: 0;
        let api = API_PAGES_TEMPLATE.format(latest);

        Request({
            url: api,
            onComplete: response => {
                let json = response.json;
                try {
                    if (json.data) {
                        for (let i in json.data) {
                            let item = json.data[i];
                            if (item.status !== 'DISABLED') {
                                dao.insertOrUpdate(item);
                            } else {
                                dao.remove(item.id);
                            }
                        }
                    }
                    prefs.LATEST_SYNCHRONIZED = '' + json.created;
                    if ( !! onsuccess) {
                        onsuccess.apply(this);
                    }
                } catch (e) {
                    if ( !! onfailed) {
                        onfailed.apply(this, [e]);
                    }
                }
            }
        }).get();
    } catch (e) {
        onfailed.apply(this);
    }
};

exports.sync = sync;

exports.create = function(data, onsuccess, onfailed) {
    Request({
        url: API_LINK_URI,
        content: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json',
        onComplete: response => {
            if (response.status === 201) {
                sync(onsuccess, onfailed);
            } else {
                if ( !! onfailed) {
                    onfailed.apply(this);
                }
            }
        }
    }).post();
};
