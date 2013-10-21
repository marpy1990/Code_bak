Ext.define('PM.view.EventTypeViewer', {
	extend: 'Ext.panel.Panel',
	preventHeader: true,
	layout:'column',
	iconCls:'event-viewer',
	items: [
	    {
	    	xtype: 'grid',
	    	columnWidth: .7,
	    	title: 'Event Types',
	    	store: global.eventTypeStore,
	    	forceFit:true,
	    	height:'100%',
	    	iconCls: 'et-man',
	        columns: [
	            { header: 'Name', dataIndex: 'id'},
	            { header: 'Description',  dataIndex: 'description' }
	        ],
	        tbar: {
	    		xtype: 'toolbar',
	    		items: [{
	        		xtype: 'button',
	        		itemId: 'add',
	        		iconCls: 'et-add',
	        		text: 'Add'
	        	}, {
	        		xtype: 'button',
	        		disabled: true,
	        		itemId: 'edit',
	        		iconCls: 'et-edit',
	        		text: 'Edit'
	        	}, {
	        		xtype: 'button',
	        		disabled: true,
	        		itemId: 'delete',
	        		iconCls: 'et-delete',
	        		text: 'Delete'
	        	}]
	    	},
	    	listeners : {
	    		select: function(rowModel, record) {
	        		this.nextSibling().setSource(record.raw.attributes);
	        	},
	        	selectionchange: function(selModel, selections) {
	        		this.down('#delete').setDisabled(selections.length === 0);
	        		this.down('#edit').setDisabled(selections.length === 0);
	        	}
	        }
	    },{
	    	xtype: 'propertygrid',
	    	columnWidth: .3,
	    	title: 'Attributes',
	    	height:'100%',
	    	source:{},
	    	tbar: {
	    		xtype: 'toolbar',
	    		items: [{
	        		xtype: 'button',
	        		itemId: 'add',
	        		iconCls: 'et-add',
	        		text: 'Add'
	        	}, {
	        		xtype: 'button',
	        		disabled: true,
	        		itemId: 'edit',
	        		iconCls: 'et-edit',
	        		text: 'Edit'
	        	}, {
	        		xtype: 'button',
	        		disabled: true,
	        		itemId: 'delete',
	        		iconCls: 'et-delete',
	        		text: 'Delete'
	        	}]
	    	},
	    	listeners : {
	        	selectionchange: function(selModel, selections) {
	        		this.down('#delete').setDisabled(selections.length === 0);
	        		this.down('#edit').setDisabled(selections.length === 0);
	        	}
	        }
	    }
	]
});
