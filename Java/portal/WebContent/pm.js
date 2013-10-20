Ext.Loader.setConfig({enabled:true});

Ext.create('Ext.data.Store', {
	storeId:'cepRuleDefStore',
	fields:['name', 'className', 'author', 'description'],
	proxy: {
		type:'ajax',
		url:'/cepdef'
	},
	autoLoad: true
});

Ext.create('Ext.data.Store', {
	storeId:'probeDefStore',
	fields:['id', 'name', 'classname', 'author', 'description']
});

var global = {
	activeNodeId: "",
	eventStore: Ext.create('PM.store.Event'),
	machineIdStore: Ext.create('PM.store.MachineId'),
	treeStore: Ext.create('PM.store.MachineTree'),
	timeunitStore: Ext.create('Ext.data.Store', {
		fields: ['name', 'value'],
	    data : [
	        {name:'seconds', value:1000},
	        {name:'minutes', value:60000},
	        {name:'hours', value:3600000},
	        {name:'days', value:86400000}
	    ]
	}),
	eventTypeStore: Ext.create('Ext.data.Store', {
		fields: ['id', 'description'],
		proxy: {
			type: 'ajax',
			url: '/eventtype'
		},
		autoLoad: true
	}),
	probeDefStore: Ext.create('Ext.data.Store', {
		fields: ['id', 'description', 'author', 'name', 'className'],
		proxy: {
			type: 'ajax',
			url: '/probedef'
		},
		autoLoad: true
	}),
	portalSubStore: Ext.create('Ext.data.Store', {
		fields: ['id', 'key', 'eventTypeName', 'filter'],
		proxy: {
			type: 'ajax',
			url: '/subman'
		},
		autoLoad: true
	}),
	networkStore: Ext.create('Ext.data.Store', {
		fields: ['id', 'ip', 'port', 'os', 'location', 'machineName'],
		proxy: {
			type: 'ajax',
			url: '/network?node=network',
		},
		autoLoad: true
	}),
	cepDefStore: Ext.create('Ext.data.Store', {
		fields: ['description', 'author', 'name', 'className'],
		proxy: {
			type: 'ajax',
			url: '/cepdef'
		},
		autoLoad: true
	}),
	cepParamStore: Ext.create('Ext.data.Store', {
		fields: ['paramName', 'description', 'isRequired', 'type'],
		autoLoad: false
	})
};

var cepView = Ext.create('PM.view.CepTab', {
	itemId: 'cepView'
});

var startPage = Ext.create('PM.view.StartPage', {
	itemId: 'startpage'
});

var eventViewer = Ext.create('PM.view.EventViewer', {
	itemId: 'eventviewer'
});

var misc = Ext.create('Ext.panel.Panel', {
	itemId: 'misc',
	preventHeader: true
});

var eventTypeViewer = Ext.create('PM.view.EventTypeViewer', {
	itemId: 'etman'
});

var cepOperatorViewer = Ext.create('Ext.panel.Panel', {
	preventHeader: true,
	itemId: 'cepdef',
	layout:'column',
	items: [
	    Ext.create('PM.view.CepOperatorViewer', {
	    	columnWidth: .7
	    }),{
	    	xtype: 'grid',
	    	columnWidth: .3,
	    	title: 'Parameters',
	    	store: global.cepParamStore,
	    	forceFit: true,
	    	columns: [
	    	          {header:'Parameter Name', dataIndex: 'paramName', flex: 3},
	    	          {header:'Type', dataIndex: 'type', flex: 2},
	    	          {header:'Required', dataIndex: 'isRequired', flex: 1}
	    	],
	    	height:'100%'
	    }
	]
});
		
var probeDefViewer = Ext.create('PM.view.ProbeDefViewer', {
	itemId: 'probedef'
});

var networkViewer = Ext.create('PM.view.NetworkViewer', {
	itemId: 'network'
});

var portalSubscriptionViewer = Ext.create('PM.view.PortalSubscriptionViewer', {
	itemId: 'subman'
});

var nodeView = Ext.create('PM.view.MachineInfo', {
	itemId: 'nodeView'
});

var probeView = Ext.create('PM.view.Probe', {
	itemId: 'probeView'
});

var pubsubView = Ext.create('Ext.panel.Panel', {
	title :'Monitored Network Structure',
	itemId: 'pubsub',
	html: '<iframe src="network.html" style="height:100%;width:100%"></iframe>'
});

var mainPage = Ext.create('Ext.panel.Panel', {
	height:'100%',
	id:'main',
	border:0,
	flex:4,
	layout:'card',
	items: [startPage, eventViewer, eventTypeViewer, probeDefViewer,
	        cepOperatorViewer, portalSubscriptionViewer, networkViewer, misc,
	        nodeView, cepView, probeView, pubsubView]
});

Ext.application({
    name: 'PM',
    appFolder: 'PM',
    launch: function() {
        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            items: [
                {
                	xtype:'component',
                	region: 'north',
            		html: '<div class="app-header"><span id="heading">Predictive Monitoring System - Web Portal</span><span class="description">Last Update: Jan 17, 2013</span></div>'
                },{
                	xtype: 'container',
                	border:0,
                	region:'center',
                	layout: {
                		type:'hbox',
                		align:'stretch'
                	},
                	height:'100%',
                	items: [
						Ext.create('PM.view.Tree', {
							store: global.treeStore,
							flex:1,
							height:'100%'
						}),{
							xtype:'splitter',
							width:1
						},mainPage
					]
                }
            ]
        });
    }
});

function onAfterRender() {
	
}

function log(d) {
	if (console) console.log(d);
}