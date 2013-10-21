Ext.define('PM.store.MachineId', {
	extend : 'Ext.data.Store',
	fields:['id'],
	proxy: {
		type:'ajax',
		url:'/network?node=machineCombo',
		reader: {
			type: 'json'
		}
	},
	autoLoad: true
});
