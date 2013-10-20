Ext.define('PM.view.EventTable', {
	extend: 'Ext.grid.Panel',
	title: 'Event Table',
	store: global.eventStore,
	forceFit:true,
	height:'100%',
    columns: [
        { header: 'ID', dataIndex: 'id', flex: 1},
        { header: 'Event Type',  dataIndex: 'type' ,flex: 2},
        { header: 'Source ID', dataIndex: 'source', flex: 3},
        { header: 'Time', dataIndex: 'time', flex: 5}
    ],
    bbar: Ext.create('Ext.toolbar.Paging', {
        store: global.eventStore,
        pageSize: 50,
        displayInfo: true
    }),
    listeners:{
    	select: function(rowModel, record) {
    		this.nextSibling().setSource(record.raw.detail);
    	}
    },
    initComponent: function() {
//    	this.store.mid = this.machineId;
    	this.callParent(arguments); 
    }
});