Ext.define('PM.view.CepConfigForm', {
	extend: 'Ext.form.Panel', 
	layout: 'anchor',
	bodyPadding: 10,
	items:[{
		fieldLabel: 'Operator Name',
		labelWidth: 200,
		allowBlank: false,
		name: 'ruleName',
		xtype: 'combo',
		anchor: '100%',
		store: Ext.data.StoreManager.lookup('cepRuleDefStore'),
	    queryMode: 'local',
	    displayField: 'name',
	    valueField: 'name',
	    emptyText: 'Select an operator...',
	    listeners: {
	    	select: function(combo, records) {
	    		var parameters = this.nextSibling();
	    		parameters.loadParams(records[0].raw.paramDefinitions);
	    	}
	    }
	},{
		xtype: 'fieldset',
        title: 'Parameters',
        collapsible: true,
        collapsed: true,
        loadParams: function(paramSpecs) {
        	this.removeAll();
        	if (this.collapsed) this.expand();
        	for (var i=0; i<paramSpecs.length; i++) {
        		var spec = paramSpecs[i];
        		var type = spec.type;
        		if (type=='eventType') {
        			this.add(Ext.create('PM.view.EventTypeSelector', {
        				labelWidth: 150,
        				labelAlign: 'right',
        	            anchor: '100%',
        	            margin:10,
        	            name: spec.paramName,
        				fieldLabel: spec.paramName,
        				emptyText: spec.description,
        				allowBlank: !(spec.isRequired)
        			}));
        		} else if (type=='string') {
        			this.add(Ext.create('Ext.form.field.Text', {
        				labelWidth: 150,
        				labelAlign: 'right',
        	            anchor: '100%',
        	            margin:10,
        				name: spec.paramName,
        				fieldLabel: spec.paramName,
        				emptyText: spec.description,
        				allowBlank: !(spec.isRequired)
        			}));
        		} else if (type=='enum') {
        			this.add(Ext.create('Ext.form.field.ComboBox', {
        				labelWidth: 150,
        				labelAlign: 'right',
        	            anchor: '100%',
        	            margin:10,
        				name: spec.paramName,
        				fieldLabel: spec.paramName,
        				emptyText: spec.description,
        				allowBlank: !(spec.isRequired),
        				store: spec.enumTexts
        			}));
        		} else if (type == 'number') {
        			this.add(Ext.create('Ext.form.field.Number', {
        				labelWidth: 150,
        	            anchor: '100%',
        	            labelAlign: 'right',
        	            margin:10,
        				name: spec.paramName,
        				fieldLabel: spec.paramName,
        				emptyText: spec.description,
        				allowBlank: !(spec.isRequired),
        				minValue: spec.lowerLimit,
        				maxValue: spec.upperLimit
        			}));
        		} else if (type == 'timeunit') {
        			this.add(Ext.create('Ext.form.FieldContainer', {
        				fieldLabel: spec.paramName,
        				labelWidth: 150,
        	            anchor: '100%',
        	            labelAlign: 'right',
        	            name: spec.paramName,
        				defaults: {
        					flex: 1,
        					hideLabel: true
                        },
                        layout: {
                        	type:'hbox',
                        	defaultMargins: {top: 0, right: 5, bottom: 0, left: 0},
                        	labelAlign:'top'
                        },
        				items: [Ext.create('Ext.form.field.Number', {
            				name: spec.paramName,
            				fieldLabel: spec.paramName,
            				hideLabel: true,
            				emptyText: spec.description,
            				allowBlank: false,
            				step: 1,
            				value: 1,
            				minValue: 1
            			}),Ext.create('PM.view.TimeUnitSelector', {
            				name: spec.paramName + "tu",
            				allowBlank: false
            			})]
        			}));
        		}
        	}
        },
        items:[]
	},{
		xtype: 'fieldset',
        title: 'Event Sources',
        collapsible: true,
        bodyPadding: 10,
        defaults: {
            anchor: '100%',
            padding: '10 0 0 0',
            layout: {
                type: 'hbox',
                defaultMargins: {top: 0, right: 10, bottom: 0, left: 0}
            },
            allowBlank: false
        },
		items:[{
			xtype: 'container',
			layout: {
            	type:'hbox'
            },items: [{
            	xtype: 'component',
            	flex: 8,
            	html: 'Event Type Name:'
            }, {
            	xtype: 'component',
            	flex: 8,
            	html: 'Source Filter:'
            }, {
            	xtype: 'component',
            	flex: 1
            }]
		},{
			xtype: 'fieldcontainer',
			items: [Ext.create('PM.view.EventTypeSelector', {
				name: 'eventType',
				flex: 8
			}),{
				xtype:'textfield',
				name: 'sourceFilter',
				flex: 8,
				allowBlank: false
			},{
				xtype: 'button',
				iconCls: 'delete',
				disabled: true,
				flex: 1,
				allowBlank: false
			}]
        }]
	},{
		xtype:'button',
		iconCls: 'add',
		text:'Add Event Source',
		handler: function() {
			this.previousSibling().add(Ext.create('Ext.form.FieldContainer', {
				padding: '10 0 0 0',
                layout: {
                	type:'hbox',
                	defaultMargins: {top: 0, right: 10, bottom: 0, left: 0}
                },
				items: [Ext.create('PM.view.EventTypeSelector', {
					flex: 8,
					name: 'eventType',
					allowBlank: false
				}),{
					xtype:'textfield',
					name: 'sourceFilter',
					allowBlank: false,
					flex: 8
				},{
					xtype: 'button',
					iconCls: 'delete',
					flex: 1,
					handler: function() {
						var container = this.up('fieldcontainer');
						var set = container.up('fieldset');
						set.remove(container);
					}
				}]
			}));
		}
	}],
	buttons: [{
		text:'Save',
		handler: function() {
            var form = this.up('form').getForm();
            if (form.isValid()) {
            	var params = form.getValues();
            	Ext.apply(params, {
					mid: global.activeNodeId
				});
            	log(params);
                Ext.Ajax.request({
                	url: 'cep?type=' + this.up('form').type,
                    params: params,
                    success: function(response){
//                    	var obj = Ext.decode(response.responseText);
                    	cepView.down("#cepTable").store.load();
                    	global.portalSubStore.load();
                    	global.treeStore.load();
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