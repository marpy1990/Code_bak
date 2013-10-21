Ext.define('PM.view.StartPage', {
	extend:'Ext.view.View',
	style: {
		"overflow-y": 'scroll'
	},
	tpl: new Ext.XTemplate(
		'<div class="page-header">Start Page</div>',
		'<tpl for=".">',
	        '<div class="function-container">',
	          '<p class="function-header">{name}</p>',
	          '<p class="function-description">{description}</p>',
	        '</div>',
	    '</tpl>'
	),
	itemSelector: 'div.function-container',
	store: {
		fields:['name', 'description'],
		data:[{
			name:'View the events',
			description: 'Go to <a href="#eventviewer">Event Viewer</a> to view all the events subscribed by this portal.'
		},{
			name:'Manage subscriptions',
			description: 'Go to <a href="#subman">Subscription Manager</a> to manage the portal\'s subscriptions. Only the subscribed events will show in the Event Viewer.'
		},{
			name:'Define/modify a CEP operator',
			description: 'Go to <a href="#cepman">CEP Operator Definition</a> to view all available operators for the Complex Event Processing module. Upload your custom Java .class file to define a new CEP operator.'
		},{
			name:'Define/modify a probe',
			description: 'Go to <a href="#probeman">Probe Definition</a> to view all available probes. Upload your custom Java .class file to define a new probe.'
		},{
			name:'Look inside the pub/sub system',
			description: 'Want to know how events are forwarded in the underlying pub/sub system? Go to <a href="pubsub">Inside Pub/Sub System</a>, and take a look at the animated graph that shows what is happening.'
		},{
			name:'Manage probes and CEP operators deployed on the monitored machines',
			description: 'You can dynamically start, stop or modify the runtime configurations of the probes and CEP operators that are deployed on the machines. Expand a machine node in the tree in the left sidebar to explore the details.'
		}]
	},
});