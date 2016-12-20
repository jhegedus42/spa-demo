
// rebeam: Improves click/tap performance, see
//   https://github.com/callemall/material-ui#react-tap-event-plugin
//   http://stackoverflow.com/a/34015469/988941
var injectTapEventPlugin = require('react-tap-event-plugin');
injectTapEventPlugin();

// rebeam: Original index.js bundle did not include material, but for now
// it's easier to just require immediately rather than loading async. However
// we could load async if we start up a simple UI without this.
window.mui          = require("material-ui");
window.mui.Styles   = require("material-ui/styles");
window.mui.SvgIcons = require('material-ui/svg-icons/index');

// rebeam: react-sortable-hoc
window.Sortable = require('react-sortable-hoc');
window.SortableContainer = window.Sortable.SortableContainer;
window.SortableElement = window.Sortable.SortableElement;
window.SortableHandle = window.Sortable.SortableHandle;
