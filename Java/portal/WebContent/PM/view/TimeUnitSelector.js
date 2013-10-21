Ext.define('PM.view.TimeUnitSelector', {
	extend : 'Ext.form.field.ComboBox',
	store: [[ 1, 'millisecond(s)'],
	        [ 1000,'second(s)'],
	        [ 60000, 'minute(s)' ],
	        [ 3600000, 'hour(s)' ]
	        ],
    queryMode: 'local'
});