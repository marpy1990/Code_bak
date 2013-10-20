Ext.define('PM.view.EventViewer', {
	extend: 'Ext.panel.Panel',
	title: 'Event Viewer',
	layout:'column',
	iconCls:'event-viewer',
	initComponent: function() {
		Ext.apply(this, {
			items:[Ext.create('PM.view.EventTable',{
				itemId:'eventTable',
				columnWidth: .7
			}),Ext.create('PM.view.EventDetailTable', {
				itemId:'eventDetailTable',
				columnWidth: .3
			})],
			tbar: Ext.create('PM.view.EventViewerToolbar')
		});
		this.callParent(arguments); 
	}
});