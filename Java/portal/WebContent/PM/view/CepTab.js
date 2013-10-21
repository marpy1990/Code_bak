Ext.define('PM.view.CepTab', {
	extend : 'Ext.container.Container',
	title : 'Complex Event Processing Specification',
	iconCls : 'cep',
	layout : 'column',
	border : 0,
	initComponent: function() {
		Ext.apply(this, {
			items : [ Ext.create('PM.view.Cep', {
				itemId:'cepTable',
				columnWidth : .6,
				listeners : {
					select : function(rowModel, record) {
						var paramTable = this.nextSibling().down();
						paramTable.setSource(record.raw.crc.params);
						paramTable.nextSibling().store.loadData(record.raw.subReq);
					}
				}
			}), {
				xtype : 'container',
				columnWidth : .4,
				items : [ Ext.create('Ext.grid.property.Grid', {
					title : 'Parameters',
					source : {
						"attribute" : "value"
					},
					nameColumnWidth : 200
				}), {
					xtype : 'grid',
					title : 'Event Source',
					columns : [ {
						header : 'Event Type',
						dataIndex : 'eventType',
					}, {
						header : 'Source Filter',
						dataIndex : 'attribute',
						renderer: function(value, metaData, record) {
							if (value=="local") return "Local";
							else return value;
						}
					} ],
					store : {
						fields : [ 'eventType', 'source'],
						data : []
					},
					height : '100%',
					forceFit : true
				} ]
			} ]
		});
		this.callParent(arguments);
	}
	
});