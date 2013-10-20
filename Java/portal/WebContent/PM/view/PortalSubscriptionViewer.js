Ext.define('PM.view.PortalSubscriptionViewer', {
	extend: 'Ext.grid.Panel',
	title: 'The Portal\'s Subscriptions',
	iconCls: 'sub-man',
	store: global.portalSubStore,
	forceFit:true,
	height:'100%',
    columns: [
//        { header: 'ID', dataIndex: 'id', width: 1},
//        { header: 'Subscription Key', dataIndex: 'key', width: 2},
        { header: 'Event Type Name', dataIndex: 'eventTypeName', width: 4},
        { header: 'Filter',  dataIndex: 'filter' , width:3, 
        	renderer: function(value) {
        		if (value=="") {
        			return "(All)";
        		} else return value;
        	}}
    ],
    tbar: {
    	xtype: 'toolbar',
    	items: [{
    		xtype: 'button',
    		itemId: 'add',
    		iconCls: 'sub-add',
    		text: 'Add',
    		handler : function() {
    			var form = Ext.create('PM.view.SubscriptionForm', {
	        		type: 'add'
	        	});
    			Ext.create('Ext.window.Window', {
    	        	title: 'Create Subscription...',
    	        	height:300,
    	        	width:400,
    	        	layout:'fit',
    	        	modal: true,
    	        	items: [form]
    	        }).show();
    			log(form.type);
    		}
    	}, {
    		xtype: 'button',
    		disabled: true,
    		itemId: 'edit',
    		iconCls: 'sub-edit',
    		text: 'Edit', 
    		handler : function() {
    			var selection = this.up('grid').getSelectionModel().getSelection()[0];
    			var form = Ext.create('PM.view.SubscriptionForm', {
	        		type: 'edit'
	        	});
    			
    			form.loadRecord(selection);
    			var window = Ext.create('Ext.window.Window', {
    	        	title: 'Edit Subscription...',
    	        	height:300,
    	        	width:400,
    	        	layout:'fit',
    	        	modal: true,
    	        	items: [form], 
    	        }).show();
    		}
    	}, {
    		xtype: 'button',
    		disabled: true,
    		itemId: 'delete',
    		iconCls: 'sub-delete',
    		text: 'Delete',
    		handler: function() {
    			var selection = this.up('grid').getSelectionModel().getSelection()[0];
    			log(selection);
                if (selection) {
                	Ext.Ajax.request({
                        url: 'subman?type=delete',
                        params: {
                        	id: selection.data.id
                        },
                        success: function(response){
                        	global.portalSubStore.remove(selection);
                        }
                    });
                }
    		}
    	}]
    },
    listeners : {
    	selectionchange: function(selModel, selections) {
    		this.down('#delete').setDisabled(selections.length === 0);
    		this.down('#edit').setDisabled(selections.length === 0);
    	}
    }
});