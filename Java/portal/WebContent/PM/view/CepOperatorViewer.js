Ext.define('PM.view.CepOperatorViewer', {
	extend: 'Ext.grid.Panel',
	title: 'Complex Event Processing Operator Definitions',
	iconCls: 'cep-edit',
	store: global.cepDefStore,
	forceFit:true,
	height:'100%',
    columns: [
        { header: 'Name', dataIndex: 'name', flex: 1},
        { header: 'Description',  dataIndex: 'description' ,flex:2},
        { header: 'Full Class Name',  dataIndex: 'className' ,flex: 2},
        { header: 'Author',  dataIndex: 'author' , flex:1}
    ],
    listeners:{
    	select: function(rowModel, record) {
    		log(record);
    		this.nextSibling().store.loadRawData(record.raw.paramDefinitions);
    	}
    },
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