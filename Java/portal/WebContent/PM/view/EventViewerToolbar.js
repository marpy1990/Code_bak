Ext.define('PM.view.EventViewerToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	items:['Filter','-','Type:', 
   Ext.create('PM.view.EventTypeSelector', {
	   listeners: {
		   change: function(combo, newValue) {
	    		var eventTable = this.up('panel').down('#eventTable');
	    		eventTable.store.filter('type', newValue);
	    	}
	   }
   }),
	       'Source:',
	{
		xtype:'combobox',
		valueField:'id',
		queryMode: 'local',
	    displayField: 'id',
	    store: global.machineIdStore,
	    listeners : {
	    	change: function(combo, newValue) {
	    		var eventTable = this.up('panel').down('#eventTable');
	    		eventTable.store.filter('source', newValue);
	    	}
	    }
	}]
});