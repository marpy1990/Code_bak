Ext.define('PM.view.ProbeDefViewer', {
	extend: 'Ext.grid.Panel',
	title: 'Probe Definition',
	iconCls: 'probe-edit',
	store: global.probeDefStore,
	forceFit:true,
	height:'100%',
    columns: [
        { header: 'ID', dataIndex: 'id', width: 1},
        { header: 'Name', dataIndex: 'name', width: 2},
        { header: 'Description',  dataIndex: 'description' ,width: 4},
        { header: 'Full Class Name',  dataIndex: 'className' ,width: 4},
        { header: 'Author',  dataIndex: 'author' , width:2}
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
    	selectionchange: function(selModel, selections) {
    		this.down('#delete').setDisabled(selections.length === 0);
    		this.down('#edit').setDisabled(selections.length === 0);
    	}
    }
});