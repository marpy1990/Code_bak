Ext.define('PM.view.EventTypeSelector', {
	extend : 'Ext.form.field.ComboBox',
	store: global.eventTypeStore,
    queryMode: 'local',
    displayField: 'id',
    valueField: 'id'
});