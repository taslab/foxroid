/**
 *
 */

const API_BASE_URI = 'http://api.foxroid.com/';
const API_LINK_URI = API_BASE_URI + 'link';
const FAVICON_URL = 'http://favicon.st-hatena.com/?url={0}';
const TAGS = ['FirefoxOS', 'Android'];

const views = {

    initialize: function() {
        this.src = {
            url: {
                input: $('#src_url'),
                validation: $('#src_url_validation'),
                insertor: $('#src_url_insert_currentpage')
            },
            pagename: $('#src_pagename'),
            tags: $('#src_tags'),
            tagSelector: $('#src_tags_selector')
        };
        this.src.url.input.on('dragover', event => event.originalEvent.preventDefault());
        this.src.url.input.on('drop', event => this._onDrop(event.originalEvent, this.src));
        this.src.url.input.on('blur', event => this._onBlur(event.originalEvent, this.src));
        this.src.url.input.on('focus', event => this._resetSendBtnStatus());
        this.src.pagename.on('focus', event => this._resetSendBtnStatus());
        $.each(TAGS, (i, value) => this.src.tags.append($('<option/>').html(value).val(value))); //create options.
        this.src.tags.on('change', event => this._switchTagSelector(event, this.src.tagSelector));
        this.src.tags.val(TAGS[1]).trigger('change');
        this.src.tags.on('focus', event => this._resetSendBtnStatus());
        this.src.url.insertor.on('click', event => this._setCurrentUrl(this.src));

        this.dest = {
            url: {
                input: $('#dest_url'),
                validation: $('#dest_url_validation'),
                insertor: $('#dest_url_insert_currentpage')
            },
            pagename: $('#dest_pagename'),
            tags: $('#dest_tags'),
            tagSelector: $('#dest_tags_selector')
        };
        this.dest.url.input.on('dragover', event => event.originalEvent.preventDefault());
        this.dest.url.input.on('drop', event => this._onDrop(event.originalEvent, this.dest));
        this.dest.url.input.on('blur', event => this._onBlur(event.originalEvent, this.dest));
        this.dest.url.input.on('focus', event => this._resetSendBtnStatus());
        this.dest.pagename.on('focus', event => this._resetSendBtnStatus());
        $.each(TAGS, (i, value) => this.dest.tags.append($('<option/>').html(value).val(value))); //create options.
        this.dest.tags.on('change', event => this._switchTagSelector(event, this.dest.tagSelector));
        this.dest.tags.val(TAGS[0]).trigger('change');
        this.dest.tags.on('focus', event => this._resetSendBtnStatus());
        this.dest.url.insertor.on('click', event => this._setCurrentUrl(this.dest));

        this.sendBtn = document.getElementById('btn_send');
        Widget.bind(this.sendBtn).as(StatusButton, 'idle');
        this.sendBtn.addEventListener('click', event => this._performCreate());

    },

    _getInputValues: function() {
        return {
            "source": {
                "url": this.src.url.input.val(),
                "name": "", //TODO next version.
                "icon": "", //TODO next version #### this.src.url.input.css('background-image'),
                "description": this.src.pagename.val(),
                "tags": [
                    this.src.tags.val()
                ]
            },
            "destination": {
                "url": this.dest.url.input.val(),
                "name": "", //TODO next version.
                "icon": "", //TODO next version #### this.dest.url.input.css('background-image'), //TODO changeable next version.
                "description": this.dest.pagename.val(),
                "tags": [
                    this.dest.tags.val()
                ]
            },
            "mutualed": true //TODO changeable next vesion
        };
    },

    _onBlur: function(event, view) {
        this._performSearch(view);
    },

    _onDrop: function(event, view) {
        event.preventDefault();
        var url = event.dataTransfer.getData("text/plain");
        $(event.target).val(url);
        this._performSearch(view);
    },

    _performSearch: function(view) {

        var url = view.url.input.val();

        // If the same as the previous word, don't search.
        if (view.prevUrl == url) {
            return;
        }
        view.prevUrl = url;

        if (!this._validation(view)) {
            return;
        }

        // clear input status
        view.url.validation.text('');

        this._nofityLoading(view, true);

        searchExternalSiteInfo(url, result => {
            this._nofityLoading(view, false);

            view.url.input.val(result.url);
            view.pagename.val(result.title);

            view.url.input.css('background-image', 'url(' + result.icon + ')');

        });
    },

    _performCreate: function() {
        //FIXME Order to validate the src and dest, not to decision logic like 'validation (src) && validation (dest)'.
        var valid = true;
        if (!this._validation(this.src)) {
            valid = false;
        }
        if (!this._validation(this.dest)) {
            valid = false;
        }

        if (valid) {

            this.sendBtn.disabled = true;
            this.sendBtn.status('progress');

            var listener = (message) => {
                window.removeEventListener('message', listener, false);
                if ( !! message.data) {
                    this.sendBtn.status('success');
                } else {
                    this.sendBtn.status('failed');
                    this.sendBtn.disabled = false;
                }
            }
            window.addEventListener('message', listener, false);
            window.parent.postMessage({
                'type': 'createLink',
                'reason': 'created relation',
                'data': this._getInputValues()
            }, '*');
        }
    },

    _setCurrentUrl: function(view) {
        var listener = (message) => {
            window.removeEventListener('message', listener, false);
            view.url.input.val(message.data);
            this._performSearch(view);
        }
        window.addEventListener('message', listener, false);
        window.parent.postMessage({
            'type': 'getCurrentUrl',
        }, '*');
    },

    _resetSendBtnStatus: function(event) {
        this.sendBtn.disabled = false;
        this.sendBtn.status('idle');
    },

    _validation: function(view) {

        /* validation URL */
        var url = view.url.input.val();
        if (StringUtils.isEmpty(url)) {
            view.url.validation.text('URL is required.');
            return false;
        }
        if (!StringUtils.isURL(url)) {
            view.url.validation.text('invalid URL format.');
            return false;
        }

        return true;
    },

    _nofityLoading: function(view, shown) {
        if (shown) {
            view.url.input.css('background-image', 'url("./resources/ajax-loader.gif")');
        } else {
            view.url.input.css('background-image', 'url("")');
        }
    },

    _switchTagSelector: function(event, dom) {
        var value = event.target.value;
        var resource = '';
        switch (value) {
            case 'Android':
                resource = './resources/ic_tag_android.png';
                break;
            default:
                resource = './resources/ic_tag_firefoxos.png';
                break;
        }
        dom.css('background-image', 'url({0})'.format(resource));
    }

};

function searchExternalSiteInfo(url, oncomplete) {

    var result = {
        url: url,
        title: null,
        icon: null
    };

    $.ajax({
        url: url,
        type: 'GET',
        success: data => {
            var div = document.createElement('div');
            $(div).html(data.responseText);
            result.title = $('title', $(div)).text();
            result.icon = FAVICON_URL.format(url);
            div = null;
        },
        complete: () => oncomplete.apply(oncomplete, [result]),

    });
}



$(document).ready(function() {
    views.initialize();
});
