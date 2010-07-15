if (typeof jQuery != "undefined") {
    
    var AJS = (function () {
        var bindings = {click: {}},
            initFunctions = [],
            included = [],
            isInitialised = false;

        var res = {

            params: {},
            /**
            * Returns an HTMLElement reference.
            * @method $
            * @param {String | HTMLElement |Array} el Accepts a string to use as an ID for getting a DOM reference, an actual DOM reference, or an Array of IDs and/or HTMLElements.
            * @return {HTMLElement | Array} A DOM reference to an HTML element or an array of HTMLElements.
            */
            $: jQuery,

            log: function(obj) {
                if (typeof console != "undefined" && console.log)
                    console.log(obj);
            },

            /**
             * Prevent further handling of an event. Returns false, which you should use as the return value of your event handler:
             *     return AJS.stopEvent(e);
             * @param e jQuery event
             */
            stopEvent: function(e) {
                e.stopPropagation();
                return false; // required for JWebUnit pop-up links to work properly
            },
            include: function (url) {
                if (!this.contains(included, url)) {
                    included.push(url);
                    var s = document.createElement("script");
                    s.src = url;
                    this.$("body").append(s);
                }
            },
            /**
            * Shortcut function to toggle class name of an element.
            * @method toggleClassName
            * @param {String | HTMLElement} element The HTMLElement or an ID to toggle class name on.
            * @param {String} className The class name to remove or add.
            */
            toggleClassName: function (element, className) {
                if (!(element = this.$(element))) {
                    return;
                }
                element.toggleClass(className);
            },
            /**
             * Shortcut function adds or removes "hidden" classname to an element based on a passed boolean.
             * @method setVisible
             * @param {String | HTMLElement} element The HTMLElement or an ID to show or hide.
             * @param {boolean} show true to show, false to hide
             */
            setVisible: function (element, show) {
                if (!(element = this.$(element))) {
                    return;
                }
                var $ = this.$; // aliased for use inside function below
                $(element).each(function () {
                    var isHidden = $(this).hasClass("hidden");
                    if (isHidden && show) {
                        $(this).removeClass("hidden");
                    }
                    else if (!isHidden && !show) {
                        $(this).addClass("hidden");
                    }
                });
            },
            /**
             * Shortcut function adds or removes "current" classname to an element based on a passed boolean.
             * @param {String | HTMLElement} element The HTMLElement or an ID to show or hide.
             * @param {boolean} show true to add "current" class, false to remove
             */
            setCurrent: function (element, current) {
                if (!(element = this.$(element))) {
                    return;
                }
                if (current)
                    element.addClass("current");
                else
                    element.removeClass("current");
            },
            /**
             * Shortcut function to see if passed element is currently visible on screen.
             * @method isVisible
             * @param {String | HTMLElement} element The HTMLElement or an jQuery selector to check.
             */
            isVisible: function (element) {
                return !this.$(element).hasClass("hidden");
            },
            /**
            * Runs functions from list (@see toInit) and attach binded funtions (@see bind)
            * @method init
            */
            init: function () {
                var ajs = this;
                this.$(".parameters input").each(function () {
                    var value = this.value,
                        id = this.title || this.id;
                    if (ajs.$(this).hasClass("list")) {
                        if (ajs.params[id]) {
                            ajs.params[id].push(value);
                        } else {
                            ajs.params[id] = [value];
                        }
                    } else {
                        ajs.params[id] = (value.match(/^(tru|fals)e$/i) ? value.toLowerCase() == "true" : value);
                    }
                });
                isInitialised = true;
                AJS.initFunctions = initFunctions;
                for (var i = 0, ii = initFunctions.length; i < ii; i++) {
                    if (typeof initFunctions[i] == "function") {
                        initFunctions[i](AJS.$);
                    }
                }
            },
            /**
            * Adds functions to the list of methods to be run on initialisation. Wraps
            * error handling around the provided function so its failure won't prevent
            * other init functions running.
            * @method toInit
            * @param {Function} func Function to be call on initialisation.
            * @return AJS object.
            */
            toInit: function (func) {
                var ajs = this;
                this.$(function () {
                    try {
                        func.apply(this, arguments);
                    } catch(ex) {
                        ajs.log("Failed to run init function: " + ex);
                    }
                });
                return this;
            },
            
            /**
            * DEPRECATED instead use AJS.$(element).bind();
            * Binds given function to some object or set of objects as event handlers by class name or id.
            * @method bind
            * @param {String} reference Element or name of the element class. Put "#" in the beginning od the string to use it as id.
            * @param {String} handlerName (optional) Name of the event i.e. "click", "mouseover", etc.
            * @param {Function} func Function to be attached.
            * @return AJS object.
            */
            bind: function () {},
            
            /**
            * Finds the index of an element in the array.
            * @method indexOf
            * @param item Array element which will be searched.
            * @param fromIndex (optional) the index from which the item will be searched. Negative values will search from the
            * end of the array.
            * @return a zero based index of the element.
            */
            indexOf: function (array, item, fromIndex) {
                var length = array.length;
                if (fromIndex == null) {
                  fromIndex = 0;
                } else {
                    if (fromIndex < 0) {
                      fromIndex = Math.max(0, length + fromIndex);
                    }
                }
                for (var i = fromIndex; i < length; i++) {
                  if (array[i] === item) return i;
                }
                return -1;
            },
            /**
            * Looks for an element inside the array.
            * @method contains
            * @param item Array element which will be searched.
            * @return {Boolean} Is element in array.
            */
            contains: function (array, item) {
                return this.indexOf(array, item) > -1;
            },
            /**
            * Replaces tokens in a string with arguments, similar to Java's MessageFormat.
            * Tokens are in the form {0}, {1}, {2}, etc.
            * @method format
            * @param message the message to replace tokens in
            * @param arg (optional) replacement value for token {0}, with subsequent arguments being {1}, etc.
            * @return {String} the message with the tokens replaced
            * @usage AJS.format("This is a {0} test", "simple");
            */
            format: function (message) {
                var args = arguments;
                return message.replace(/\{(\d+)\}/g, function (str, i) {
                    var replacement = args[parseInt(i, 10) + 1];
                    return replacement != null ? replacement : str;
                });
            },
            /**
            * Includes firebug lite for debugging in IE. Especially in IE.
            * @method firebug
            * @usage Type in addressbar "javascript:alert(AJS.firebug());"
            */
            firebug: function () {
                var script = this.$(document.createElement("script"));
                script.attr("src", "http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js");
                this.$("head").append(script);
                (function () {
                    if (window.firebug) {
                        firebug.init();
                    } else {
                        setTimeout(arguments.callee, 0);
                    }
                })();
            },
            /**
            * Compare two strings in alphanumeric way
            * @method alphanum
            * @param {String} a first string to compare
            * @param {String} b second string to compare
            * @return {Number(-1|0|1)} -1 if a < b, 0 if a = b, 1 if a > b
            * @usage a.sort(AJS.alphanum)
            */
            alphanum: function (a, b) {
                var chunks = /(\d+|\D+)/g,
                    am = a.match(chunks),
                    bm = b.match(chunks),
                    len = Math.max(am.length, bm.length);
                for (var i = 0; i < len; i++) {
                    if (i == am.length) {
                        return -1;
                    }
                    if (i == bm.length) {
                        return 1;
                    }
                    var ad = parseInt(am[i], 10),
                        bd = parseInt(bm[i], 10);
                    if (ad == am[i] && bd == bm[i] && ad != bd) {
                        return (ad - bd) / Math.abs(ad - bd);
                    }
                    if ((ad != am[i] || bd != bm[i]) && am[i] != bm[i]) {
                        return am[i] < bm[i] ? -1 : 1;
                    }
                }
                return 0;
            },
            dim: function () {
                if (AJS.dim.dim) {
                    AJS.dim.dim.remove();
                    AJS.dim.dim = null;
                } else {
                    AJS.dim.dim = AJS("div").css({
                        width: "100%",
                        height: AJS.$(document).height(),
                        background: "#000",
                        opacity: .5,
                        position: "absolute",
                        top: 0,
                        left: 0
                    });
                    AJS.$("body").append(AJS.dim.dim);
                }
            },
            popup: function (width, height) {
                var shadow = AJS.$('<div class="shadow"><div class="tl"></div><div class="tr"></div><div class="l"></div><div class="r"></div><div class="bl"></div><div class="br"></div><div class="b"></div></div>');
                var popup = AJS("div").css({
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    margin: "-" + Math.round(height / 2) + "px 0 0 -" + Math.round(width / 2) + "px",
                    width: width + "px",
                    height: height + "px",
                    background: "#fff",
                    zIndex: 3000
                });
                AJS.$("body").append(shadow);
                AJS.$(".shadow").css({
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    margin: "-" + Math.round(height / 2) + "px 0 0 -" + Math.round(width / 2 + 16) + "px",
                    width: width + 32 + "px",
                    height: height + 29 + "px",
                    zIndex: 2999
                });
                AJS.$("body").append(popup);
                AJS.dim();
                
                return popup;
            },
            onTextResize: function (f) {
                if (typeof f == "function") {
                    if (AJS.onTextResize["on-text-resize"]) {
                        AJS.onTextResize["on-text-resize"].push(function (emsize) {
                            f(emsize);
                        });
                    } else {
                        var em = AJS("div");
                        em.css({
                            width: "1em",
                            height: "1em",
                            position: "absolute",
                            top: "-9999em",
                            left: "-9999em"
                        });
                        this.$("body").append(em);
                        em.size = em.width();
                        setInterval(function () {
                            if (em.size != em.width()) {
                                em.size = em.width();
                                for (var i = 0, ii = AJS.onTextResize["on-text-resize"].length; i < ii; i++) {
                                    AJS.onTextResize["on-text-resize"][i](em.size);
                                };
                            }
                        }, 0);
                        AJS.onTextResize.em = em;
                        AJS.onTextResize["on-text-resize"] = [function (emsize) {
                            f(emsize);
                        }];
                    }
                }
            },
            unbindTextResize: function (f) {
                for (var i = 0, ii = AJS.onTextResize["on-text-resize"].length; i < ii; i++) {
                    if (AJS.onTextResize["on-text-resize"][i] == f) {
                        return AJS.onTextResize["on-text-resize"].splice(i, 1);
                    }
                };
            },
            escape: function (string) {
                return escape(string).replace(/%u\w{4}/gi, function (w) {
                    return unescape(w);
                });
            }
        };
        if (typeof AJS != "undefined") {
            for (var i in AJS) {
                res[i] = AJS[i];
            }
        }
        /**
        * Creates DOM object
        * @method AJS
        * @param {String} element tag name
        * @return {jQuery object}
        * @usage var a = AJS("div");
        */
        var result = function () {
            var res = null;
            if (arguments.length && typeof arguments[0] == "string") {
                res = arguments.callee.$(document.createElement(arguments[0]));
                if (arguments.length == 2) {
                    res.html(arguments[1]);
                }
            }
            return res;
        };
        for (var i in res) {
            result[i] = res[i];
        }
        return result;
    })();

    AJS.$(function () {AJS.init();});
}

if (typeof console == "undefined") {
    console = {
        messages: [],
        log: function (text) {
            this.messages.push(text);
        },
        show: function () {
            alert(this.messages.join("\n"));
            this.messages = [];
        }
    };
}
else {
    // Firebug console - show not required to do anything.
    console.show = function(){};
}