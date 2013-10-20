var network = {
	"20000": ["20001", "20006","20011","20016"],
	"20001": ["20002", "20003", "20004", "20005"],
	"20006": ["20007", "20008", "20009", "20010"],
	"20011": ["20012", "20013", "20014", "20015"],
	"20016": ["20017", "20018", "20019", "20020"],
	"20002": ["20001"],
	"20003": ["20001"],
	"20004": ["20001"],
	"20005": ["20001"],
	"20007": ["20006"],
	"20008": ["20006"],
	"20009": ["20006"],
	"20010": ["20006"],
	"20012": ["20011"],
	"20013": ["20011"],
	"20014": ["20011"],
	"20015": ["20011"],
	"20017": ["20016"],
	"20018": ["20016"],
	"20019": ["20016"],
	"20020": ["20016"]
};

var definedEventImages = {
	"Cpu":"Cpu",
	"CpuSpike":"CpuSpike",
	"HighCpu":"HighCpu",
	"sensor":"sensor",
	"UnbalancedCluster":"UnbalancedCluster"
};

var graph, levelCount;
var nodeImg = new Image();
nodeImg.src = "img/server.png";

/**
 * Reads raw network architecture data from <code>network</code>
 * and populates the variable <code>graph</code> with <code>GraphNode</code>
 * objects.<br>
 * The layout algorithm picks one node as the tree root, and performs broad-first-search
 * to determine the row number (level) of every node. The nodes' coordinates
 * within the canvas are set afterwards; nodes with the same level value are
 * placed in a row and evenly separated.
 */
function generateGraph() {
	graph = new Object();
	var nextLevel = 1;
	var currentLevelArray = new Array();
	var nextLevelArray = new Array();
	// the first item plays root
	for (var id in network) {
		currentLevelArray.push(id);
		graph[id] = new GraphNode(0);
		break;
	}
	// BFS
	while (currentLevelArray.length != 0) {
		var levelItemCount = 0;
		// for all in current level
		for (var index = 0; index < currentLevelArray.length; index++) {
			var curId = currentLevelArray[index];
			// set x
			graph[curId].x = Math.round(canvas.width/(currentLevelArray.length+1)) * (index+1);
			var curNbSet = network[curId];
			for (var nbIndex = 0; nbIndex < curNbSet.length; nbIndex++) {
				var nbId = curNbSet[nbIndex];
				if (!graph[nbId]) {
					// undefined means not visited, add to next level
					graph[nbId] = new GraphNode(nextLevel);
					levelItemCount++;
					nextLevelArray.push(nbId);
				}
			}
		}
		nextLevel++;
		currentLevelArray = nextLevelArray;
		nextLevelArray = new Array();
	}
	levelCount = nextLevel - 1;
	for (var nodeIndex in graph) {
		var node = graph[nodeIndex];
		// set y, leaves 30px top and bottom margins
		node.y = 30 + Math.round((canvas.height-60) / (levelCount-1)) * node.level;
	}
}

function GraphNode(level) {
	this.level = level;
}

GraphNode.prototype = {
	setPosition: function(x, y) {
		this.x = x;
		this.y = y;
	}	
};

function Line(start, end) {
	this.start = start;
	this.end = end;
}

function Point(x, y) {
	this.x = x;
	this.y = y;
}

function FlyingItem(img, width, height, speed, label, path) {
	this.img = new Image();
	if (definedEventImages[img])
		this.img.src = "images/events/" + definedEventImages[img] + ".png";
	else this.img.src = "images/events/event.png";
	this.width = width;
	this.height = height;
	this.speed = speed;
	this.label = label;
	this.path = path;
	this.pathIndex = -1;
	this.nextPath();
} 

FlyingItem.prototype = {
	constructor: FlyingItem,
	nextPath: function() {
		this.pathIndex++;
		if (this.pathIndex == this.path.length-1)
			return false;
		this.start = this.path[this.pathIndex];
		this.end = this.path[this.pathIndex+1];
		this.x = this.start.x;
		this.y = this.start.y;
		this.directionx = (this.end.x - this.start.x > 0) ? 1:-1;
		this.directiony = (this.end.y - this.start.y > 0) ? 1:-1;
		var angle = Math.atan((this.end.y-this.start.y)/(this.end.x-this.start.x));
		this.stepx = Math.abs(this.speed * Math.cos(angle));
		this.stepy = Math.abs(this.speed * Math.sin(angle));
		return true;
	},
	fly: function() {
		this.x += this.stepx * this.directionx;
		this.y += this.stepy * this.directiony;
		
		if (this.directionx * (this.x - this.end.x) <= 0 &&
				this.directiony * (this.y - this.end.y) <= 0) {
			return true;
		} else {
			return this.nextPath();
		}
	}
};

function pollServer() {
	$.ajax({
		type:"post", 
		url:"pubsub", 
		dataType: "json", 
		success:function(json){
			network = json.nb;
			generateGraph();
			for (var i=0; i<json.msgPaths.length; i++) {
				flyingItems.push(new FlyingItem(json.msgPaths[i].msgId, 24, 24, 5, json.msgPaths[i].msgId, getPath(json.msgPaths[i].path)));
			}
        }
	});
}

var flyingItems = new Array();

function refresh() {
	context.clearRect(0,0,canvas.width,canvas.height);
	canvasBufferContext.clearRect(0,0,canvas.width,canvas.height);
	drawNodes();
	var i;
	for (i=0; i<flyingItems.length; i++) {
		var item = flyingItems[i];
		canvasBufferContext.drawImage(item.img, item.x-12, item.y-12, item.width, item.height);
		canvasBufferContext.fillText(item.label, item.x-15, item.y-30);
		if (item.fly()==false) flyingItems.splice(i, 1);
	}
	context.drawImage(canvasBuffer,0,0);
}

/**
 * Transfers an array of node IDs to the actual position.
 * @param nodeIdArray
 * @returns {Array}
 */
function getPath(nodeIdArray) {
	var path = new Array();
	for (var i=0; i<nodeIdArray.length; i++) {
		var nodeId = nodeIdArray[i];
		path.push(new Point(graph[nodeId].x, graph[nodeId].y));
	}
	return path;
}

function drawNodes() {
	for (var nodeId in graph) {
		var node = graph[nodeId];
		var nbArray = network[nodeId];
		
		// draw lines
		for (var nbArrayIndex = 0; nbArrayIndex<nbArray.length; nbArrayIndex++) {
			var nbNode = graph[nbArray[nbArrayIndex]];
			context.beginPath();
			context.moveTo(node.x, node.y);
			context.lineTo(nbNode.x, nbNode.y);
			context.stroke();
		}
	}
	
	for (var nodeId in graph) {
		var node = graph[nodeId];
		// draw machine
		context.drawImage(nodeImg, node.x-12, node.y-12, 24, 24);
	}
}
/**
 * Shows a tip when the mouse is moved on a node, displaying the node's ID.
 * @param ev the mousemove event of the canvas
 */
function onCanvasMouseMove(ev) {
	var x = ev.layerX;
	var y = ev.layerY;
	for (var nodeId in graph) {
		var node = graph[nodeId];
		if (x > node.x - 12 && x < node.x + 12 && y > node.y - 12 && y < node.y + 12) {
			$("#tip").css({top:ev.y - 30, left:ev.x - 12, display:'block'}).html("Node ID: " + nodeId);
			return;
		}
	}
	$("#tip").css({top:ev.y, left:ev.x, display:'none'});
}

$(window).load(function() {
	canvas = document.getElementById("canvas");
	context = canvas.getContext('2d');
	context.font="12px Helvetica";
	context.lineWidth = 1;
	canvasBuffer = document.createElement("canvas");
	canvasBuffer.width = canvas.width;
	canvasBuffer.height = canvas.height;
	canvasBufferContext = canvasBuffer.getContext("2d");
	canvas.addEventListener('mousemove', onCanvasMouseMove, false);
	setInterval(refresh, 40);
	setInterval(pollServer, 1000);
});