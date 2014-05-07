/**
 * This source code is a collection of classes for accessing the resource files.
 */
'use strict';

const {
    indexedDB, IDBKeyRange
} = require('sdk/indexed-db');

// For beta
const DB_VERSION = "1";


const IDBTransaction = { //FIXME ff25?で廃止されてる
    READ_ONLY: 'readonly' /*transaction default is readonly. 0 in Chrome*/ ,
    READ_WRITE: 'readwrite' /* 1 in Chrome*/ ,
    VERSION_CHANGE: 'versionchange' /* 2 in Chrome*/
};

const DDL = {
    NAME: 'foxroid.db',
    Table: {
        PAGES: 'pages'
    },
    Column: {
        URL: 'url',
        NAME: 'name',
        ICON: 'icon',
        CATEGORY: 'category',
        STATUS: 'status'
    },
    STATUS: {
        UNCOMMIT: -1,
        PENDING: 0,
        ACCEPTED: 1
    }
};

let database = {
    onerror: function(e) {
        console.log(e.value);
    }
};

/**
 *
 * @param {Object} version
 * @param {Object} entries
 */

function open(version, onsuccess) {

    let request = indexedDB.open(DDL.NAME, version);
    request.onupgradeneeded = function(e) {

        let db = e.target.result;

        e.target.transaction.onerror = database.onerror;

        if (db.objectStoreNames.contains(DDL.Table.PAGES)) {
            db.deleteObjectStore(DDL.Table.PAGES);
        }

        // Create table of pages.
        let objectStore = db.createObjectStore(DDL.Table.PAGES, {
            keyPath: DDL.Column.URL
        });
        // // Store values in the newly created objectStore.
        // objectStore.transaction.oncomplete = function(event) {
        //     let transaction = db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
        //     let pageStore = transaction.objectStore(DDL.Table.PAGES);
        //     //XXX initial data add.
        // }

    }
    request.onsuccess = function(e) {
        database.db = e.target.result;
        console.log('##### database accessable');
        onsuccess.apply(this);
    }
    request.onerror = database.onerror;
}


/**
 * @param {String} url
 * @param {Function} onComplete
 * @param {Function} onError
 */

function findByUrl(url, onComplete, onError) {
    let objectStore = database.db.transaction([DDL.Table.PAGES]).objectStore(DDL.Table.PAGES);
    let request = objectStore.get(url);
    request.onsuccess = function(event) {
        let links = [];
        try {
            links = this.result;
        } catch (e) {
            // nothing
        }
        onComplete.apply(this, [links]);
    }
}

exports.initialize = function(listener) {
    open(DB_VERSION, listener);
}

exports.insertOrUpdate = function(item, onsuccess, onfailed) {

    let transaction = database.db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
    transaction.oncomplete = onsuccess;
    transaction.onerror = onfailed;
    let objectStore = transaction.objectStore(DDL.Table.PAGES);
    let request = objectStore.get(item.url);
    request.onsuccess = function() {
        if ( !! request.result) {
            objectStore.put(item);
        } else {
            objectStore.add(item);
        }
    }
}

exports.remove = function(url, onsuccess) {
    let transaction = database.db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
    transaction.oncomplete = onsuccess;
    transaction.onerror = onfailed;
    let objectStore = transaction.objectStore(DDL.Table.PAGES);
    let request = objectStore.delete(item.url);
}

exports.find = function(url, onsuccess, onfailed) {
    try {
        return findByUrl(url, onsuccess, onfailed);
    } catch (e) {
        return undefined;
    }
}
