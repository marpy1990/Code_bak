Ext.define('PM.view.ProbeToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	defaults:{
		xtype: 'button'
	},
	items: [{
		iconCls:'probe-add',
		itemId:'add',
		text:'Add'
	},{
		iconCls:'probe-edit',
		itemId:'edit',
		text:'Edit',
		disabled:true,
	},{
		iconCls:'probe-delete',
		itemId:'delete',
		text:'Delete',
		disabled:true
	},{
		iconCls:'probe-start',
		itemId:'start',
		text:'Start',
		disabled:true
	},{
		iconCls:'probe-stop',
		itemId: 'stop',
		text:'Stop',
		disabled:true
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