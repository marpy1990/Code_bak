Ext.define('PM.view.SubscriptionForm', {
	extend: 'Ext.form.Panel', 
	layout: 'anchor',
	bodyPadding: 10,
	items:[{
		xtype:'textfield',
		hidden: true,
		name: 'id'
	},{
		xtype:'textfield',
		fieldLabel: 'Event Type Name',
		allowBlank: false,
		name: 'eventTypeName',
	    emptyText: 'The Event Type Name...'
	},{
		xtype:'textfield',
		fieldLabel: 'Filter',
		allowBlank: true,
		name: 'filter',
	    emptyText: '(All)'
	}],
	buttons: [{
		text:'Save',
		handler: function() {
            var form = this.up('form').getForm();
//                s = '';
            if (form.isValid()) {
//                Ext.iterate(form.getValues(), function(key, value) {
//                    s += Ext.util.Format.format("{0} = {1}<br />", key, value);
//                }, this);
                Ext.Ajax.request({
                    url: 'subman?type=' + this.up('form').type,
                    params: form.getValues(),
                    success: function(response){
                    	var obj = Ext.decode(response.responseText);
                    	global.portalSubStore.load();
                    }
                });
                this.up('window').close();
            }
        }
	},{
        text   : 'Reset',
        handler: function() {
            this.up('form').getForm().reset();
        }
    }]
});