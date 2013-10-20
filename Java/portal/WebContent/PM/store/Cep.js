Ext.define('PM.store.Cep', {
	extend: 'Ext.data.Store', 
	fields:[
	        {name:'className', mapping:'crc.className'},
	        {name:'running', mapping:'crc.running'},
	        'id',
	        {name:'ruleName', mapping:'crc.ruleName'},
	        {name:'complexEventType', mapping: 'crc.complexEventType'}
	]
});