Ext.define('PM.view.MachineDetail', {
	extend: 'Ext.tab.Panel',
	region: 'center',
	border:0,
	tabBar: {
		height:30,
		defaults:{
			height:28
		},
	},
	plain:true,
	initComponent: function() {
		var mid = this.machineId;
		
		Ext.apply(this, {
			items:[Ext.create('PM.view.MachineInfo',{
				machineId:mid
			}),
			Ext.create('PM.view.CepTab', {
				machineId:mid
			}),
			Ext.create('PM.view.Probe', {
				machineId:mid
			})]
		});
		this.callParent(arguments); 
	}
});