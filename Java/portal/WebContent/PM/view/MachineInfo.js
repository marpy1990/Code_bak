Ext.define('PM.view.MachineInfo', {
	extend: 'Ext.grid.Panel',
	title: 'Overview',
	iconCls:'overview',
	forceFit: true,
    columns:[{
    	text:'Attribute',
    	dataIndex:'key',
    	width: 1
    },{
    	text:'Value',
    	dataIndex:'value',
    	width: 3
    }],
    
	initComponent: function() {
		this.store = Ext.create('PM.store.MachineInfo', {
			proxy:{
				type:'ajax',
				url:'machineInfo'
			},
			fields:['key', 'value', 'type'],
			groupField: 'type'
		});
		var groupingFeature = Ext.create('Ext.grid.feature.Grouping',{
	        groupHeaderTpl: 'Machine Information: {name}'
	    });
		Ext.apply(this, {
			features: [groupingFeature]
		});
		this.callParent(arguments); 
	}
});
