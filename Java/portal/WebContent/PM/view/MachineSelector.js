Ext.define('PM.view.MachineSelector', {
	extend : 'Ext.form.field.ComboBox',
	store: global.machineIdStore,
    queryMode: 'local',
    displayField: 'id',
    valueField: 'id'
});