Ext.define('PM.model.Event', {
	extend: 'Ext.data.Model',
	fields: ['id', 'type', 'source',
	         {name:'time', type:'date', dateFormat: 'time'}],
	idProperty: 'id'   
});