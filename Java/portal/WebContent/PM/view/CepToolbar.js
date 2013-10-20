Ext.define('PM.view.CepToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	items: [{
		xtype: 'button',
		text:'Add', 
		itemId: 'add',
		iconCls:'cep-add',
		handler: function() {
			var form = Ext.create('PM.view.CepConfigForm', {
				type: 'add'
			});
	        Ext.create('Ext.window.Window', {
	        	title: 'New Operator Instance',
	        	height:600,
	        	width:800,
	        	layout:'fit',
	        	modal: true,
	        	items: [form]
	        }).show();
	    }
	},{
		xtype: 'button',
		itemId: 'edit',
		iconCls:'cep-edit',
		text:'Edit',
		disabled:true,
		handler: function() {
			var form = Ext.create('PM.view.CepConfigForm', {
				type: 'edit'
			});
	        Ext.create('Ext.window.Window', {
	        	title: 'Edit Operator Instance',
	        	height:600,
	        	width:800,
	        	layout:'fit',
	        	modal: true,
	        	items: [form]
	        }).show();
	    }
	},{
		xtype:'button',
		itemId: 'delete',
		iconCls:'cep-delete',
		text:'Delete',
		disabled:true,
		handler: function() {
			var selection = this.up('grid').getSelectionModel().getSelection()[0];
            if (selection) {
            	Ext.Ajax.request({
                    url: 'cep',
                    params: {
                    	type: 'delete',
                    	mid: global.activeNodeId,
                    	id: selection.data.id
                    },
                    success: function(response){
                    	cepView.down("#cepTable").store.load();
                    	global.treeStore.load();
                    }
                });
            }
		}
	},{
		xtype:'button',
		itemId: 'start',
		iconCls:'cep-start',
		text:'Start',
		disabled:true,
		handler: function() {
			var selection = this.up('grid').getSelectionModel().getSelection()[0];
            if (selection) {
            	Ext.Ajax.request({
                    url: 'cep',
                    params: {
                    	type: 'start',
                    	mid: global.activeNodeId,
                    	id: selection.data.id
                    },
                    success: function(response){
                    	cepView.down("#cepTable").store.load();
                    }
                });
            }
		}
	},{
		text:'Stop',
		itemId: 'stop',
		iconCls:'cep-stop',
		disabled:true,
		handler: function() {
			var selection = this.up('grid').getSelectionModel().getSelection()[0];
            if (selection) {
            	Ext.Ajax.request({
                    url: 'cep',
                    params: {
                    	type: 'stop',
                    	mid: global.activeNodeId,
                    	id: selection.data.id
                    },
                    success: function(response){
                    	cepView.down("#cepTable").store.load();
                    }
                });
            }
		}
	}],
	enableStart: function() {
		this.down('#edit').enable();
		this.down('#delete').enable();
		this.down('#start').enable();
		this.down('#stop').disable();
	},
	enableStop: function() {
		this.down('#edit').enable();
		this.down('#delete').enable();
		this.down('#start').disable();
		this.down('#stop').enable();
	},
	disable: function() {
		this.down('#edit').disable();
		this.down('#delete').disable();
		this.down('#start').disable();
		this.down('#stop').disable();
	}
});