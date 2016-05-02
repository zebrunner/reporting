'use strict';

angular.module('pubnub.angular.service', []).factory('PubNub', [
  '$rootScope', function($rootScope) {
    var c, k, _i, _len, _ref;
    c = {
      '_instance': null,
      '_channels': [],
      '_presence': {},
      'jsapi': {}
    };
    _ref = ['map', 'each'];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      k = _ref[_i];
      if ((typeof PUBNUB !== "undefined" && PUBNUB !== null ? PUBNUB[k] : void 0) instanceof Function) {
        (function(kk) {
          return c[kk] = function() {
            var _ref1;
            return (_ref1 = c['_instance']) != null ? _ref1[kk].apply(c['_instance'], arguments) : void 0;
          };
        })(k);
      }
    }
    for (k in PUBNUB) {
      if ((typeof PUBNUB !== "undefined" && PUBNUB !== null ? PUBNUB[k] : void 0) instanceof Function) {
        (function(kk) {
          return c['jsapi'][kk] = function() {
            var _ref1;
            return (_ref1 = c['_instance']) != null ? _ref1[kk].apply(c['_instance'], arguments) : void 0;
          };
        })(k);
      }
    }
    c.initialized = function() {
      return !!c['_instance'];
    };
    c.init = function() {
      c['_instance'] = PUBNUB.init.apply(PUBNUB, arguments);
      c['_channels'] = [];
      c['_presence'] = {};
      c['_presData'] = {};
      return c['_instance'];
    };
    c.destroy = function() {
      c['_instance'] = null;
      c['_channels'] = null;
      c['_presence'] = null;
      return c['_presData'] = null;
    };
    c._ngFireMessages = function(realChannel) {
      return function(messages, t1, t2) {
        return c.each(messages[0], function(message) {
          return $rootScope.$broadcast("pn-message:" + realChannel, {
            message: message,
            channel: realChannel
          });
        });
      };
    };
    c._ngInstallHandlers = function(args) {
      var oldmessage, oldpresence;
      oldmessage = args.message;
      args.message = function() {
        $rootScope.$broadcast(c.ngMsgEv(args.channel), {
          message: arguments[0],
          env: arguments[1],
          channel: args.channel
        });
        if (oldmessage) {
          return oldmessage(arguments);
        }
      };
      oldpresence = args.presence;
      args.presence = function() {
        var channel, cpos, event, _base, _base1;
        event = arguments[0];
        channel = args.channel;
        if (event.uuids) {
          c.each(event.uuids, function(uuid) {
            var state, _base, _base1;
            state = uuid.state ? uuid.state : null;
            uuid = uuid.uuid ? uuid.uuid : uuid;
            (_base = c['_presence'])[channel] || (_base[channel] = []);
            if (c['_presence'][channel].indexOf(uuid) < 0) {
              c['_presence'][channel].push(uuid);
            }
            (_base1 = c['_presData'])[channel] || (_base1[channel] = {});
            if (state) {
              return c['_presData'][channel][uuid] = state;
            }
          });
        } else {
          if (event.uuid && event.action) {
            (_base = c['_presence'])[channel] || (_base[channel] = []);
            (_base1 = c['_presData'])[channel] || (_base1[channel] = {});
            if (event.action === 'leave') {
              cpos = c['_presence'][channel].indexOf(event.uuid);
              if (cpos !== -1) {
                c['_presence'][channel].splice(cpos, 1);
              }
              delete c['_presData'][channel][event.uuid];
            } else {
              if (c['_presence'][channel].indexOf(event.uuid) < 0) {
                c['_presence'][channel].push(event.uuid);
              }
              if (event.data) {
                c['_presData'][channel][event.uuid] = event.data;
              }
            }
          }
        }
        $rootScope.$broadcast(c.ngPrsEv(args.channel), {
          event: event,
          message: arguments[1],
          channel: channel
        });
        if (oldpresence) {
          return oldpresence(arguments);
        }
      };
      return args;
    };
    c.ngListChannels = function() {
      return c['_channels'].slice(0);
    };
    c.ngListPresence = function(channel) {
      var _ref1;
      return (_ref1 = c['_presence'][channel]) != null ? _ref1.slice(0) : void 0;
    };
    c.ngPresenceData = function(channel) {
      return c['_presData'][channel] || {};
    };
    c.ngSubscribe = function(args) {
      var _base, _name;
      if (c['_channels'].indexOf(args.channel) < 0) {
        c['_channels'].push(args.channel);
      }
      (_base = c['_presence'])[_name = args.channel] || (_base[_name] = []);
      args = c._ngInstallHandlers(args);
      return c.jsapi.subscribe(args);
    };
    c.ngUnsubscribe = function(args) {
      var cpos;
      cpos = c['_channels'].indexOf(args.channel);
      if (cpos !== -1) {
        c['_channels'].splice(cpos, 1);
      }
      c['_presence'][args.channel] = null;
      delete $rootScope.$$listeners[c.ngMsgEv(args.channel)];
      delete $rootScope.$$listeners[c.ngPrsEv(args.channel)];
      return c.jsapi.unsubscribe(args);
    };
    c.ngPublish = function() {
      return c['_instance']['publish'].apply(c['_instance'], arguments);
    };
    c.ngHistory = function(args) {
      args.callback = c._ngFireMessages(args.channel);
      return c.jsapi.history(args);
    };
    c.ngHereNow = function(args) {
      args = c._ngInstallHandlers(args);
      args.state = true;
      args.callback = args.presence;
      delete args.presence;
      delete args.message;
      return c.jsapi.here_now(args);
    };
    c.ngWhereNow = function(args) {
      return c.jsapi.where_now(args);
    };
    c.ngState = function(args) {
      return c.jsapi.state(args);
    };
    c.ngMsgEv = function(channel) {
      return "pn-message:" + channel;
    };
    c.ngPrsEv = function(channel) {
      return "pn-presence:" + channel;
    };
    c.ngAuth = function() {
      return c['_instance']['auth'].apply(c['_instance'], arguments);
    };
    c.ngAudit = function() {
      return c['_instance']['audit'].apply(c['_instance'], arguments);
    };
    c.ngGrant = function() {
      return c['_instance']['grant'].apply(c['_instance'], arguments);
    };
    return c;
  }
]);