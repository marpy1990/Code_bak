Ext.define('PM.store.Event', {
	extend: 'Ext.data.Store',
	model: 'PM.model.Event',
	pageSize: 50,
	remoteFilter: true,
	 proxy: {
         type: 'ajax',
         url: '/event',
         reader: {
        	 type: 'json',
             root: 'data'
         }
     },
     autoLoad: {start: 0, limit: 50}
});