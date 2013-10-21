Ext.define('PM.view.Probe', {
	extend: 'Ext.grid.Panel',
	title: 'Probes',
	forceFit:true,
	height:'100%',
	iconCls:'probe',
    columns: [
        { header: 'Probe ID',  dataIndex: 'probeID', width: 3 },
        { header: 'Interval',  dataIndex: 'interval', width:1 },
        { header: 'Time Unit', dataIndex: 'timeunit', width:2},
        { header: 'Running', dataIndex: 'running',width:1,
        	renderer: function(value){
    	        if (value == true) {
    	            text = 'Yes';
    	        } else {
    	        	text = 'No';
    	        }
    	        return text;
    	    }}
    ],
    tbar: Ext.create('PM.view.ProbeToolbar'),
    store : Ext.create('PM.store.Probe', {
		machineId: this.machineId,
		proxy:{
			type:'ajax',
			url:'probe'
		},
		listeners : {
			beforeload: function(store){
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