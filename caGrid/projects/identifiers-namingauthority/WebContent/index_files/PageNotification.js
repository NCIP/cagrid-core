
// Provide a default path to dwr.engine
if (dwr == null) var dwr = {};
if (dwr.engine == null) dwr.engine = {};
if (DWREngine == null) var DWREngine = dwr.engine;

if (PageNotification == null) var PageNotification = {};
PageNotification._path = '/dwr';
PageNotification.setPermissionManager = function(p0, callback) {
  dwr.engine._execute(PageNotification._path, 'PageNotification', 'setPermissionManager', p0, callback);
}
PageNotification.setPageManager = function(p0, callback) {
  dwr.engine._execute(PageNotification._path, 'PageNotification', 'setPageManager', p0, callback);
}
PageNotification.setNotificationManager = function(p0, callback) {
  dwr.engine._execute(PageNotification._path, 'PageNotification', 'setNotificationManager', p0, callback);
}
PageNotification.startWatching = function(p0, callback) {
  dwr.engine._execute(PageNotification._path, 'PageNotification', 'startWatching', p0, callback);
}
PageNotification.stopWatching = function(p0, callback) {
  dwr.engine._execute(PageNotification._path, 'PageNotification', 'stopWatching', p0, callback);
}
