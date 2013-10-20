Ext.define('PM.view.NetworkViewer', {
	extend: 'Ext.grid.Panel',
	title: 'Network Overview',
	iconCls: 'network',
	store: global.networkStore,
	forceFit:true,
	height:'100%',
    columns: [
        { header: 'ID', dataIndex: 'id', width: 4},
        { header: 'Machine Name', dataIndex: 'machineName', width: 4},
        { header: 'Port', dataIndex: 'port', width: 1},
        { header: 'Location', dataIndex: 'location', width: 3},
        { header: 'OS', dataIndex: 'os', width: 4}
    ]
});