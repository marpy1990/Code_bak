Ext.define('PM.view.Tree', {
	extend:'Ext.tree.Panel',
//	useArrows: true,
	alias:'widget.networktree',
    preventHeader: true,
    listeners:{
    	itemclick: function(view, record) {
    		var detail = mainPage;
    		var type = record.data.type;
    		if (type == "function") {
    			// display a function page
    			var nodeId = record.data.id;
        		detail.getLayout().setActiveItem(nodeId);
    		} else {
    			
    			if (type == "node") {
    				global.activeNodeId = record.data.id;
    				detail.getLayout().setActiveItem("nodeView");
    				nodeView.store.load();
    				misc.add(Ext.create('PM.view.MachineInfo', {
    					machineId : record.data.id
    				}));
    			} else if (type == "probeOverview") {
    				detail.getLayout().setActiveItem("probeView");
    				global.activeNodeId = record.data.mid;
    				probeView.store.load();
    			} else if (type == "cepOverview") {
    				detail.getLayout().setActiveItem("cepView");
    				global.activeNodeId = record.data.mid;
    				cepView.down("#cepTable").store.load();
    			}
    		}
    	}
    }
});