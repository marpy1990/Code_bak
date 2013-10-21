Ext.define('PM.store.MachineInfo', {
	extend: 'Ext.data.Store', 
	fields:['key', 'value'],
	listeners : {
		beforeload: function(store){
			Ext.apply(store.proxy.extraParams, {
				mid: global.activeNodeId
			});
		}
	}
});