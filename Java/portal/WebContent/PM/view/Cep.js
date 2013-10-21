Ext.define('PM.view.Cep', {
	extend: 'Ext.grid.Panel',
	title: 'Complex Event Processing Rules',
	forceFit:true,
	height:'100%',
	iconCls:'cep',
	columns : [ {
		header : 'ID',
		dataIndex : 'id',
		width:3
	}, {
		header : 'Complex Event Type',
		dataIndex : 'complexEventType',
		width:3
	}, {
		header : 'Cep Rule Name',
		dataIndex : 'ruleName',
		width:3
	}, {
		header : 'Running',
		dataIndex : 'running',
		width:1,
		renderer: function(value){
	        if (value == true) {
	            text = 'Yes';
	        } else {
	        	text = 'No';
	        }
	        return text;
	    }
	}],
	tbar: Ext.create('PM.view.CepToolbar'),
	store : Ext.create('PM.store.Cep', {
		proxy: {
			type: 'ajax',
			url:'/cep',
		},
		autoLoad: false,
		listeners : {
			beforeload: function(store) {
				Ext.apply(store.proxy.extraParams, {
					mid: global.activeNodeId
				});
			}
		}
	}),
	listeners: {
		select: function(rowModel, record) {
			if (record.data.running) {
				this.down('toolbar').enableStop();
			} else {
				this.down('toolbar').enableStart();
			}
    	}
	}
});