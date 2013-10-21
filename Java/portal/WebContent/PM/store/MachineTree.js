Ext.define('PM.store.MachineTree', {
	extend: 'Ext.data.TreeStore',
	fields: ['text','id', 'type','iid','mid'],
    proxy: {
        type: 'ajax',
        url: 'network',
    },
    root: {
    	text: 'Predictive Monitoring System',
        id: 'startpage',
        expanded: true, 
        iconCls: 'system',
        type: 'function'
    }
});