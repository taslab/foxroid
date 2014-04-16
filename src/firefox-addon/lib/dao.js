/**
* This source code is a collection of classes for accessing the resource files.
*/
'use strict';

const Request = require('sdk/request').Request;
const { indexedDB, IDBKeyRange } = require('sdk/indexed-db');

// const API = 'http://localhost:8080';
const API = 'https://foxroid-gae.appspot.com/';


const IDBTransaction = {//FIXME ff25?で廃止されてる
  READ_ONLY: 'readonly'/*transaction default is readonly. 0 in Chrome*/,
  READ_WRITE: 'readwrite'/* 1 in Chrome*/,
  VERSION_CHANGE: 'versionchange'/* 2 in Chrome*/
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
function open(version, entries) {
  let pages = entries;
  
  let request = indexedDB.open(DDL.NAME, version);
  request.onupgradeneeded = function(e) {
    
    let db = e.target.result;
    
    e.target.transaction.onerror = database.onerror;

    if (db.objectStoreNames.contains(DDL.Table.PAGES)) {
      db.deleteObjectStore(DDL.Table.PAGES);
    }

    // Create table of pages.
    let objectStore = db.createObjectStore(DDL.Table.PAGES, {keyPath: DDL.Column.URL});
    // Store values in the newly created objectStore.
    objectStore.transaction.oncomplete = function(event) {
      let transaction = db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
      let pageStore = transaction.objectStore(DDL.Table.PAGES);
      for (let i in pages) {
        pageStore.add(pages[i]);
      }
    }

  }
  request.onsuccess = function(e) {
    database.db = e.target.result;
  }
  request.onerror = database.onerror;
}

/**
 * 
 * @param {Object} page
 * @param {Function} onComplete
 * @param {Function} onError
 */
function addPage(page, onComplete, onError) {
  let transaction = database.db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
  transaction.oncomplete = onComplete;
  transaction.onerror = onError;
  
  let objectStore = transaction.objectStore(DDL.Table.PAGES);
  objectStore.add(page);
}

/**
 * 
 * @param {Object} pages
 * @param {Function} onComplete
 * @param {Function} onError
 */
function addPages(pages, /*options*/ onComplete, onError) {
  let transaction = database.db.transaction([DDL.Table.PAGES], IDBTransaction.READ_WRITE);
  transaction.oncomplete = onComplete;
  transaction.onerror = onError;
  
  let objectStore = transaction.objectStore(DDL.Table.PAGES);
  for (let i in pages) {
    objectStore.add(pages[i]);
  }
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
    onComplete.apply(this, [this.result]);
  }
}

exports.refresh = function() {

  let onComplete = (response) => {
    try {
      let json = response.json;
      if (json.code == 200) {
        open(json.version, json.entries);
      }
    } catch (e) {
      console.error(e);
    }
  };
  
  /** for debug */
  let response = {
    json: JSON.parse(require('sdk/self').data.load('resources/mapping.json'))
  };
  onComplete(response);
  
  // Request({
    // url : API,
    // onComplete : onComplete
  // }).get();

}

exports.find = function(url, onComplete, onError) {
  try {
    return findByUrl(url, onComplete, onError);
  } catch (e) {
    return undefined;
  }
}

